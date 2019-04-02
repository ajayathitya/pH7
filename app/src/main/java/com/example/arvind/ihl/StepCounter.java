package com.example.arvind.ihl;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.util.Log;

public class StepCounter extends AppCompatActivity implements SensorEventListener, StepListener {

    private TextView TvSteps;
    private StepDetector simpleStepDetector;
    private Button BtnStart,BtnStop;
    private SensorManager sensorManager;
    private Sensor accel;
    private int numSteps=0;
    private Chronometer chronometer;
    private boolean running;
    long pauseoffset;
    int elapsedMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);


        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        TvSteps = (TextView) findViewById(R.id.tv_steps);
        BtnStart = (Button) findViewById(R.id.btn_start);
        BtnStop = (Button) findViewById(R.id.btn_stop);

        chronometer = findViewById(R.id.ch);


        BtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!running){
                    chronometer.setBase(SystemClock.elapsedRealtime()-pauseoffset);
                    chronometer.start();
                    running = true;
                }

                numSteps = 0;
                sensorManager.registerListener(StepCounter.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

            }
        });


        BtnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(running){
                    elapsedMillis = (int) (SystemClock.elapsedRealtime() - chronometer.getBase());
                    chronometer.stop();
                    running = false;
                    pauseoffset = 0;
                    elapsedMillis/=1000;
                    TvSteps.setText("0");
                    Intent i = new Intent(StepCounter.this,ActivityDashboard.class);
                    i.putExtra("sec",elapsedMillis+"");
                    i.putExtra("steps",numSteps+"");
                    startActivity(i);
                }

                sensorManager.unregisterListener(StepCounter.this);

            }
        });
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        TvSteps.setText(numSteps+"");
    }


}
