package yjp.cn.bounceprogressbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    private BounceProgressBar qp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bounce_free);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        qp = (BounceProgressBar) findViewById(R.id.qp);

        qp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                qp.startTotalAnimations();
            }
        });
    }
}
