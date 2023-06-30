package com.example.stopwatch;

import static android.text.TextUtils.isEmpty;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Button startBtn, stopBtn, resetBtn;
    Button startLap;
    TextView hour, min, sec, milliSec;
    TextView lapHour, lapMin, lapSec, lapMilliSec;
    TextView lap1, lap2, lap3;
    TextView smallLap1, smallLap2, smallLap3;
    int i, secondCounter = 0, minuteCounter = 0, hourCounter = 0, lapFull = 0;
    int secondLapCounter = 0, minuteLapCounter = 0, hourLapCounter = 0;
    volatile boolean stopThread = false;
    volatile boolean stopLapThread = false;

    volatile boolean lapStartFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.start);
        stopBtn = findViewById(R.id.stop);
        resetBtn = findViewById(R.id.reset);
        startLap = findViewById(R.id.lap);

        sec = findViewById(R.id.sec);
        min = findViewById(R.id.min);
        hour = findViewById(R.id.hour);
        milliSec = findViewById(R.id.milliSec);

        lapSec = findViewById(R.id.lapSec);
        lapMin = findViewById(R.id.lapMin);
        lapHour = findViewById(R.id.lapHour);
        lapMilliSec = findViewById(R.id.lapMilliSec);

        lap1 = findViewById(R.id.lap1);
        lap2 = findViewById(R.id.lap2);
        lap3 = findViewById(R.id.lap3);

        smallLap1 = findViewById(R.id.smallLap1);
        smallLap2 = findViewById(R.id.smallLap2);
        smallLap3 = findViewById(R.id.smallLap3);

        startLap.setEnabled(false);
    }

    public void startThread(View v){
        //Log.d(TAG, "startThread: ");
        stopThread = false;
        stopLapThread = false;
        threads milliSecThread = new threads(60);
        new Thread(milliSecThread).start();
        startBtn.setEnabled(false);
        startLap.setEnabled(true);
    }

    public void stopThread(View view) {
        startBtn.setEnabled(true);
        stopThread = true;
        stopLapThread = true;
        lapStartFlag = false;
    }

    public void startLapThread(View v){
        if(lapFull == 0){
            lap1.setText(hour.getText()+":"+min.getText()+":"+sec.getText()+":"+milliSec.getText());
            smallLap1.setText(lapHour.getText()+":"+lapMin.getText()+":"+lapSec.getText()+":"+lapMilliSec.getText());
            lapFull=1;
        }
        else if(lapFull == 1){
            lap2.setText(hour.getText()+":"+min.getText()+":"+sec.getText()+":"+milliSec.getText());
            smallLap2.setText(lapHour.getText()+":"+lapMin.getText()+":"+lapSec.getText()+":"+lapMilliSec.getText());
            lapFull=2;
        }
        else if (lapFull == 2){
            lap3.setText(hour.getText()+":"+min.getText()+":"+sec.getText()+":"+milliSec.getText());
            smallLap3.setText(lapHour.getText()+":"+lapMin.getText()+":"+lapSec.getText()+":"+lapMilliSec.getText());
            lapFull=0;
        }

        lapMilliSec.setText("00");
        lapSec.setText("00");
        lapMin.setText("00");
        lapHour.setText("00");
        secondLapCounter = 0;

        try {
            if(!stopThread){
                stopLapThread = false;
                if (!lapStartFlag){
                    lapThreads lapMilliSecThread = new lapThreads(60);
                    new Thread(lapMilliSecThread).start();
                    lapStartFlag = true;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void reset(View v){
        milliSec.setText("00");
        sec.setText("00");
        min.setText("00");
        hour.setText("00");

        stopLapThread = true;
        lapStartFlag = false;

        lapMilliSec.setText("00");
        lapSec.setText("00");
        lapMin.setText("00");
        lapHour.setText("00");
        secondLapCounter = 0;

        stopThread = true;
//        lapFull=0;
        secondCounter = 0;
        startBtn.setEnabled(true);
        startLap.setEnabled(false);
    }

    public void resetLap(View v){
        lap1.setText("");
        lap2.setText("");
        lap3.setText("");
        smallLap1.setText("");
        smallLap2.setText("");
        smallLap3.setText("");

        lap1.setHint("Lap 1");
        lap2.setHint("Lap 2");
        lap3.setHint("Lap 3");
        smallLap1.setHint("Lap 1");
        smallLap2.setHint("Lap 2");
        smallLap3.setHint("Lap 3");

        lapFull=0;
    }

    class threads implements Runnable{

        int seconds;
        int begin;

        threads(int seconds){
            this.seconds = seconds;
        }

        @Override
        public void run() {
            while(true){
                if(milliSec.getText()=="00"){
                    begin = 0;
                }
                else{
                    begin = Integer.parseInt(milliSec.getText().toString());
                }
                for (i = begin; i<=seconds; i++) {
                    if (stopThread)
                        return;
                    startBtn.post(new Runnable() {
                        @Override
                        public void run() {
                            if(i < 10){
                                milliSec.setText("0"+i);
                            }
                            else {
                                milliSec.setText(""+i);
                            }

                            if (i==60){
                                milliSec.setText("00");
                                secondCounter++;
                                if(secondCounter < 10){
                                    sec.setText("0"+secondCounter);
                                }
                                else {
                                    sec.setText(""+secondCounter);
                                }
                            }

                            if(secondCounter==60){
                                secondCounter=0;
                                sec.setText("0"+secondCounter);

                                minuteCounter++;
                                if(minuteCounter < 10){
                                    min.setText("0"+minuteCounter);
                                }
                                else {
                                    min.setText(""+minuteCounter);
                                }
                            }

                            if(minuteCounter==60){
                                minuteCounter=0;
                                min.setText("0"+minuteCounter);

                                hourCounter++;
                                if(hourCounter < 10){
                                    hour.setText("0"+hourCounter);
                                }
                                else {
                                    hour.setText(""+hourCounter);
                                }
                            }
                        }
                    });

                    try {
                        Thread.sleep(20);
                    }
                    catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }



    //Another class for handling lap counter

    class lapThreads implements Runnable{

        int seconds;
        int begin;

        lapThreads(int seconds){
            this.seconds = seconds;
        }

        @Override
        public void run() {
            while(true){
                if(lapMilliSec.getText()=="00"){
                    begin = 0;
                }
                else{
                    begin = Integer.parseInt(lapMilliSec.getText().toString());
                }
                for (i = begin; i<=seconds; i++) {
                    if (stopLapThread)
                        return;
                    startLap.post(new Runnable() {
                        @Override
                        public void run() {
                            if(i < 10){
                                lapMilliSec.setText("0"+i);
                            }
                            else {
                                lapMilliSec.setText(""+i);
                            }

                            if (i==60){
                                lapMilliSec.setText("00");
                                secondLapCounter++;
                                if(secondLapCounter < 10){
                                    lapSec.setText("0"+secondLapCounter);
                                }
                                else {
                                    lapSec.setText(""+secondLapCounter);
                                }
                            }

                            if(secondLapCounter==60){
                                secondLapCounter=0;
                                lapSec.setText("0"+secondLapCounter);

                                minuteLapCounter++;
                                if(minuteLapCounter < 10){
                                    lapMin.setText("0"+minuteLapCounter);
                                }
                                else {
                                    lapMin.setText(""+minuteLapCounter);
                                }
                            }

                            if(minuteLapCounter==60){
                                minuteLapCounter=0;
                                lapMin.setText("0"+minuteLapCounter);

                                hourLapCounter++;
                                if(hourLapCounter < 10){
                                    lapHour.setText("0"+hourLapCounter);
                                }
                                else {
                                    lapHour.setText(""+hourLapCounter);
                                }
                            }
                        }
                    });

                    try {
                        Thread.sleep(20);
                    }
                    catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}