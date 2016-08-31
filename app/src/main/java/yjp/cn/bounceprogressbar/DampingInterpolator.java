package yjp.cn.bounceprogressbar;

import android.view.animation.Interpolator;

/**
 * Created by 1017 on 2016/8/27.
 */
public class DampingInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float input) {
        return (float) (1 - Math.exp(-3 * input) * Math.cos(10 * input));
    }
}
