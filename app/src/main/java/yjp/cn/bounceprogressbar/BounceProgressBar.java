package yjp.cn.bounceprogressbar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by 1017 on 2016/8/27.
 */
public class BounceProgressBar extends SurfaceView implements SurfaceHolder.Callback {


    public static final int STATE_DOWN = 1;
    public static final int STATE_UP = 2;
    private Paint mPaint;
    private Path mPath;
    private int mLineColor;
    private int mPointColor;
    private int mLineWidth;
    private int mLineHeigt;
    private float mDownDistance;
    private float mUpDistance;
    private float freeBallDistance;
    //向下运动.
    private ValueAnimator downController;
    //向上运动.
    private ValueAnimator upController;
    //自由落体.
    private ValueAnimator freeDownController;
    private AnimatorSet animatorSet;
    private int state;
    private boolean isBounced = false;
    private boolean isBallFreeUp = false;
    private boolean isUpControllerDied = false;
    private boolean isAnimationShowing = false;


    public BounceProgressBar(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public BounceProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public BounceProgressBar(Context context) {
        super(context);
        init(context,null);
    }

    private void init(Context context, AttributeSet attrs) {
        initAttributes(context, attrs);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mLineHeigt);//画笔的粗细
        mPaint.setStrokeCap(Paint.Cap.ROUND);//圆形的线帽
        mPath = new Path();
        getHolder().addCallback(this);

        initController();
    }

    private void initController() {
        downController = ValueAnimator.ofFloat(0,1);
        downController.setDuration(500);
        downController.setInterpolator(new DecelerateInterpolator());
        downController.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDownDistance = 50*(float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        downController.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                state = STATE_DOWN;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

        upController = ValueAnimator.ofFloat(0,1);
        upController.setDuration(900);
        upController.setInterpolator(new DampingInterpolator());
        upController.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mUpDistance = 50*(float) animation.getAnimatedValue();
                if(mUpDistance>=50){
                    System.out.println("mUpDistance:"+mUpDistance);
                    //进入自由落体状态
                    isBounced = true;
                    if(!freeDownController.isRunning()&&!freeDownController.isStarted()&&!isBallFreeUp){
                        freeDownController.start();
                    }
                }
                postInvalidate();
            }
        });
        upController.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                state = STATE_UP;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isUpControllerDied = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

        freeDownController = ValueAnimator.ofFloat(0,6.8f);
        freeDownController.setDuration(600);
        freeDownController.setInterpolator(new DecelerateInterpolator());
        freeDownController.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //一个公式解决上升减速和下降加速
                float t = (float) animation.getAnimatedValue();
                freeBallDistance = 34 * t - 5*t*t;
                if(isUpControllerDied){
                    postInvalidate();
                }
            }
        });
        freeDownController.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                isBallFreeUp = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationShowing = false;
                //循环第二次动画
                startTotalAnimations();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });

        animatorSet = new AnimatorSet();
        animatorSet.play(downController).before(upController);
        animatorSet.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                isAnimationShowing = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });

    }

    protected void startTotalAnimations() {
        if(isAnimationShowing){
            return;
        }
        if(animatorSet.isRunning()){
            animatorSet.end();
            animatorSet.cancel();
        }
        isBounced = false;
        isBallFreeUp = false;
        isUpControllerDied = false;
        animatorSet.start();
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BounceProgressBar);
        mLineColor = typedArray.getColor(R.styleable.BounceProgressBar_line_color, Color.WHITE);
        mLineWidth = typedArray.getDimensionPixelOffset(R.styleable.BounceProgressBar_line_width, 200);
        mLineHeigt = typedArray.getDimensionPixelOffset(R.styleable.BounceProgressBar_line_height, 2);
        mPointColor = typedArray.getColor(R.styleable.BounceProgressBar_point_color, Color.WHITE);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 1.一条绳子
        /**
         * 一跳绳子用左右两部分的二阶贝塞尔曲线绘制组合而成
         */
        mPaint.setColor(mLineColor);
        mPath.reset();
        //起始点
        mPath.moveTo(getWidth()/2-mLineWidth/2, getHeight()/2);
        if(state == STATE_DOWN){
            //下坠
            //左边的贝塞尔
            mPath.quadTo((float) (getWidth()/2-mLineWidth/2 + mLineWidth*0.375), getHeight()/2+mDownDistance,
                    getWidth()/2,
                    getHeight()/2+mDownDistance);
            //右边的贝塞尔
            mPath.quadTo((float) (getWidth()/2+mLineWidth/2 - mLineWidth*0.375), getHeight()/2+mDownDistance,
                    getWidth()/2+mLineWidth/2,
                    getHeight()/2);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mPath, mPaint);

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mPointColor);
            canvas.drawCircle(getWidth()/2, getHeight()/2 + mDownDistance -10, 10, mPaint);

        }else if(state == STATE_UP){
            //向上弹
            //绳子照样画
            //左边的贝塞尔
            mPath.quadTo((float) (getWidth()/2-mLineWidth/2 + mLineWidth*0.375), getHeight()/2+(50-mUpDistance),
                    getWidth()/2,
                    getHeight()/2+(50-mUpDistance));
            //右边的贝塞尔
            mPath.quadTo((float) (getWidth()/2+mLineWidth/2 - mLineWidth*0.375), getHeight()/2+(50-mUpDistance),
                    getWidth()/2+mLineWidth/2,
                    getHeight()/2);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mPath, mPaint);

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mPointColor);
            //2.弹性小球
            //第三种状态--自由落体小球
            if(!isBounced){
                //正常上升
                canvas.drawCircle(getWidth()/2, getHeight()/2 + (50 - mUpDistance) - 10, 10, mPaint);
            }else{
                //自由落体
                canvas.drawCircle(getWidth()/2, getHeight()/2 - freeBallDistance - 10, 10, mPaint);
            }

        }
        //3.两边的固定点的圆
        mPaint.setColor(mPointColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth()/2-mLineWidth/2, getHeight()/2, 10, mPaint);
        canvas.drawCircle(getWidth()/2+mLineWidth/2, getHeight()/2, 10, mPaint);
        super.onDraw(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //锁定画布
        Canvas canvas = holder.lockCanvas();
        draw(canvas);
        //接触锁定
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }
}
