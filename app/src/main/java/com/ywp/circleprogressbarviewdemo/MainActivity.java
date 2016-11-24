package com.ywp.circleprogressbarviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public final int STROKE = 0;
    public final int FILL = 1;
    private CircleProgressBarView mProgress;
    private Switch mSwitch_state;
    private Switch mSwitch_style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mProgress = (CircleProgressBarView) findViewById(R.id.cpb_progress);
        mSwitch_state = (Switch) findViewById(R.id.switch_state);
        mSwitch_style = (Switch) findViewById(R.id.switch_style);
        //得到对应的值进行设置
        mSwitch_state.setChecked(mProgress.getAnimState());

        mSwitch_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // 根据改变来设置值
                mProgress.setAnimState(b);
            }
        });

        //得到对应的值进行设置
        if (mProgress.getPaintStyle() == STROKE) {
            mSwitch_style.setChecked(false);
        } else {
            mSwitch_style.setChecked(true);
        }

        mSwitch_style.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // 根据改变来设置值
                if (b) mProgress.setPainStyle(FILL);
                else mProgress.setPainStyle(STROKE);
            }
        });
    }

    public void onClick(View view) {
        int start = 0;
        int end = 100;
        if (start > 100 || start < 0 || end > 100 || end < 0) {
            Toast.makeText(getApplicationContext(), "强化值不能大于100和小于0", Toast.LENGTH_LONG).show();
            return;
        }
        mProgress.setProgress(start, end);
    }

    public void add(View view) {
        mProgress.addTen();
    }

    public void reduce(View view) {
        mProgress.reduceTen();
    }
}
