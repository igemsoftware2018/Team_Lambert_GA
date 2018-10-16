package com.lambertigem.colorq;

import android.os.AsyncTask;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import android.app.Activity;
import java.util.Calendar;

public class FinalActivity extends Activity {

    private String latitude;
    private String longitude;
    private String row1;
    private String row2;
    private String row3;
    private String row4;
    private String row5;
    private String row6;
    private String accesstohcvalue;
    private String bodyofwatervalue;
    private String resultvalue;
    private String pathogenvalue;
    private String notesvalue;
    private String timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            row1 = extras.getString("row1");
            row2 = extras.getString("row2");
            row3 = extras.getString("row3");
            row4 = extras.getString("row4");
            row5 = extras.getString("row5");
            row6 = extras.getString("row6");
            latitude = extras.getString("Latitude");
            longitude = extras.getString("Longitude");
            accesstohcvalue = extras.getString("AccessToHC");
            bodyofwatervalue = extras.getString("BodyOfWater");
            resultvalue = extras.getString("Result");
            pathogenvalue = extras.getString("Pathogen");
            notesvalue = extras.getString("Notes");
        }

        timestamp = Calendar.getInstance().getTime().toString();
        new FinalActivity.DBOperation().execute("");

    }

    public void exitApp(View v) {


        Intent intent = new Intent(FinalActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    public void runTest(View v) {


        Intent intent = new Intent(FinalActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private class DBOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                //Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://lambertigem.clsduc4zbmxw.us-east-2.rds.amazonaws.com:3306/chromqtestdata?autoReconnect=true&useSSL=false", "lambertigem", "lambertigem");
                //      Connection con = DriverManager.getConnection("jdbc:mysql://lambertigem.clsduc4zbmxw.us-east-2.rds.amazonaws.com:3306/chromqtestdata?user=\"lambertigem\"&password=\"lambertigem\"");
                Statement stmt = con.createStatement();
               String sql = "INSERT INTO chromqtestdata (latitude, longitude, row1, row2, row3, row4, row5, row6, accesstohc, bodyofwater, result, pathogen, notes, timestampvalue) VALUES (" + latitude + ", " + longitude + ", \'" + row1 + "\', \'" + row2 + "\', \'" + row3 + "\', \'" + row4 + "\', \'" + row5 + "\', \'" + row6 + "\', \'" + accesstohcvalue + "\', \'" + bodyofwatervalue + "\', \'" + resultvalue + "\', \'" + pathogenvalue + "\', \'" + notesvalue + "\', \'" + timestamp + "\')";
                System.out.println(sql);
               stmt.executeUpdate(sql);
                con.close();
                return "DB Operation Complete";
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
                return "Error";
            }
        }
    }

}
