package com.example.upipaymentapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class Main3Activity extends AppCompatActivity {

    ImageView done;

    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;

    Timer timer,timer1, timers, timertid;

    TextView textView, tid;

    Button button;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        final String transaction_reference_id = getIntent().getStringExtra("Transaction_Reference_ID");

        button = (Button) findViewById(R.id.button);

        done = findViewById(R.id.done);

//        //FULL SCREEN ACTIVITY
//        this.getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        );

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Drawable drawable = done.getDrawable();
                if(drawable instanceof AnimatedVectorDrawableCompat){
                    avd = (AnimatedVectorDrawableCompat) drawable;
                    avd.start();
                }else if(drawable instanceof AnimatedVectorDrawable) {
                    avd2 = (AnimatedVectorDrawable) drawable;
                    avd2.start();
                }
            }
        }, 3000);

        textView = (TextView) findViewById(R.id.successful);
        tid = (TextView) findViewById(R.id.tid);

        timers = new Timer();
        timers.schedule(new TimerTask() {
            @Override
            public void run() {
                textView.setText("PAYMENT SUCCESSFUL");
                timertid = new Timer();
                timertid.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        tid.setText("UPI Transaction ID : "+ transaction_reference_id);

                    }
                }, 1000);
            }
        }, 2000);

//        timertid = new Timer();
//        timertid.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Button button = (Button) findViewById(R.id.button);
//                button.setVisibility(View.VISIBLE);
//            }
//        }, 1000);

    }

    public void again(View view)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
