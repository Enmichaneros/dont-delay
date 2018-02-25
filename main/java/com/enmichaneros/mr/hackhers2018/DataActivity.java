package com.enmichaneros.mr.hackhers2018;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.db.chart.animation.Animation;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.LineChartView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Math;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class DataActivity extends AppCompatActivity {

    int graphRange;
    int flightCount;
    float thickness;
    float roundness;
    float[] minuteSums;
    TextView tv;
    LineChartView chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        graphRange = extras.getInt("range", 500);
        thickness = (graphRange < 500) ? 5.0f : 3.0f;
        roundness = (graphRange < 500) ? 7.0f : 5.0f;
        flightCount = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        tv = findViewById(R.id.sample_text);
        chart = findViewById(R.id.first_chart);
        tv.setText("Loading results...");

        Log.i("msg", "starting new display task");
        DisplayTask d = new DisplayTask();
        d.execute();
    }

    public void displayReasons(){
        int index = 0;
        float maxValue = minuteSums[0];

        for (int i = 0; i < 5; i++){
            if (minuteSums[i] > maxValue){
                index = i;
                maxValue = minuteSums[i];
            }
        }

        double maxNum = maxValue / (minuteSums[0] + minuteSums[1] + minuteSums[2] + minuteSums[3] + minuteSums[4]);
        String maxPercentage = String.format("%.2f", maxNum * 100);
        String averageMins = String.format("%.2f", maxValue / flightCount);
        Log.i("msg", "index: " + index);

        switch (index){
            case 0:
                tv.setText("Primary reason for recent delays: Carrier Service\nPercentage of minutes delayed: " + maxPercentage + "%\nAverage related delay: " + averageMins + " minutes");
                tv.setTextColor(Color.parseColor("#ff4444"));
                break;
            case 1:
                tv.setText("Primary reason for recent delays: Bad Weather\nPercentage of minutes delayed: " + maxPercentage + "%\nAverage related delay: " + averageMins + " minutes");
                tv.setTextColor(Color.parseColor("#ff8e44"));
                break;
            case 2:
                tv.setText("Primary reason for recent delays: NAS\nPercentage of minutes delayed: " + maxPercentage + "%\nAverage related delay: " + averageMins + " minutes");
                tv.setTextColor(Color.parseColor("#ffec44"));
                break;
            case 3:
                tv.setText("Primary reason for recent delays: Security\nPercentage of minutes delayed: " + maxPercentage + "%\nAverage related delay: " + averageMins + " minutes");
                tv.setTextColor(Color.parseColor("#baff44"));
                break;
            case 4:
                tv.setText("Primary reason for recent delays: Aircraft Arrived Late\nPercentage of minutes delayed: " + maxPercentage + "%\nAverage related delay: " + averageMins + " minutes");
                tv.setTextColor(Color.parseColor("#44ffdc"));
                break;
            default:
                tv.setText("Oops, something went wrong!");
        }
    }

    public void showData(){
        //chart.setAxisBorderValues(minValue, maxValue, (maxValue - minValue) / 5 );
        displayReasons();

        //chart.setYAxis(false);
        //chart.setYLabels(AxisRenderer.LabelPosition.NONE);
        chart.setStep(25);
        Animation anim = new Animation(500);
        anim.fromColor(0xffffff);
        chart.show(anim);


    }

    private class DisplayTask extends AsyncTask<Object, Object, String>{
        @SuppressLint("ResourceAsColor")
        @Override
        protected String doInBackground(Object... objects) {
            try {
                LineSet carrierLine = new LineSet();
                LineSet weatherLine = new LineSet();
                LineSet nasLine = new LineSet();
                LineSet securityLine = new LineSet();
                LineSet aircraftLine = new LineSet();

                minuteSums = new float[5];

                Log.i("msg", "declaring new URL");
                URL url=new URL("https://query.data.world/s/micm8hwmslwCG7SkjsxBHi_8SCHVVy");
                Log.i("msg", "opening connection");
                HttpURLConnection c=(HttpURLConnection)url.openConnection();
                c.setRequestMethod("GET");
                Log.i("msg", "attempting connection");
                c.connect();
                Log.i("msg", "checking for input stream");
                InputStream is = c.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                Log.i("msg", "reading from stream");
                String line = br.readLine();
                for (int i = 0; i < 500; i++) {
                    line = br.readLine();}
                for (int i = 0; i < graphRange; i++) {
                    line = br.readLine();
                    String[] row = line.split(",");
                    if (row.length > 25) {
                        flightCount++;
                        carrierLine.addPoint("", Float.valueOf(row[25]));
                        minuteSums[0] += Float.valueOf(row[25]);
                        weatherLine.addPoint("", Float.valueOf(row[26]));
                        minuteSums[1] += Float.valueOf(row[26]);
                        nasLine.addPoint("", Float.valueOf(row[27]));
                        minuteSums[2] += Float.valueOf(row[27]);
                        securityLine.addPoint("", Float.valueOf(row[28]));
                        minuteSums[3] += Float.valueOf(row[28]);
                        aircraftLine.addPoint("", Float.valueOf(row[29]));
                        minuteSums[4] += Float.valueOf(row[29]);
                        //Log.i("msg",  "current line: " + i + " | carrier delay: " + row[25]);
                    }

                }
                carrierLine.setThickness(thickness);
                carrierLine.setDotsRadius(roundness);
                carrierLine.setColor(0xff4444);
                carrierLine.setDotsColor(0xff4444);
                chart.addData(carrierLine);

                weatherLine.setThickness(thickness);
                weatherLine.setDotsRadius(roundness);
                weatherLine.setColor(0xff8e44);
                weatherLine.setDotsColor(0xff8e44);
                chart.addData(weatherLine);

                nasLine.setThickness(thickness);
                nasLine.setDotsRadius(roundness);
                nasLine.setColor(0xffec44);
                nasLine.setDotsColor(0xffec44);
                chart.addData(nasLine);

                securityLine.setThickness(thickness);
                securityLine.setDotsRadius(roundness);
                securityLine.setColor(0xbaff44);
                securityLine.setDotsColor(0xbaff44);
                chart.addData(securityLine);

                aircraftLine.setThickness(thickness);
                aircraftLine.setDotsRadius(roundness);
                aircraftLine.setColor(0x44ffdc);
                aircraftLine.setDotsColor(0x44ffdc);
                chart.addData(aircraftLine);

            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("success")) showData();
            else {
                tv.setText("Sorry, something went wrong!");
            }
        }
    }
}
