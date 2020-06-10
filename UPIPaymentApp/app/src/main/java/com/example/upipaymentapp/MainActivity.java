package com.example.upipaymentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Timer timer;

    public void fade(View view)
    {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        //imageView.animate().alpha(0).setDuration(10000);
        Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    for(int i = 0;i<100;i++)
                    {
                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                        progressBar.incrementProgressBy(10);
                    }

                }
            }, 2000);

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                    startActivity(intent);
                }
            }, 4000);
            Toast.makeText(MainActivity.this, "Loading...!", Toast.LENGTH_LONG).show();
    }
}
