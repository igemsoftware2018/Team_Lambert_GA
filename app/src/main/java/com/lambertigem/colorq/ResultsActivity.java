package com.lambertigem.colorq;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.text.Html;



public class ResultsActivity extends Activity {

    static final int REQUEST_LOCATION = 1;
    private double latitude;
    private double longitude;
    private String row1;
    private String row2;
    private String row3;
    private String row4;
    private String row5;
    private String row6;
    private String result;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();
        String image_path = intent.getStringExtra("imagePath");
        Uri fileUri = Uri.parse(image_path);
        ImageView myimage = (ImageView) findViewById(R.id.image_view);
        myimage.setImageURI(fileUri);


        TextView alltext = (TextView)findViewById(R.id.poscontrol);

        //Enabling scrolling on TextView.
        alltext.setMovementMethod(new ScrollingMovementMethod());

        Bundle b = getIntent().getExtras();
        String result = b.getString("result");

        alltext.setText(Html.fromHtml(result), TextView.BufferType.SPANNABLE);
  //      alltext.setText(result);

    }


    public void onbuttonClick(View v) {


        Intent intent = new Intent(ResultsActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    public void saveResult(View v) {
        getLocationInfo();
        Bundle bundle = getIntent().getExtras();
        double[] doubles = bundle.getDoubleArray("arr");
        Intent intent = new Intent(ResultsActivity.this, SaveActivity.class);
        result = bundle.getString("result");


        System.out.println(result);

        int index1 = result.lastIndexOf("Row1:");
        int index2 = result.indexOf("Row2:");
        row1 = result.substring(index1, index2);
        row1 = row1.replace("<br>", " ");
        System.out.println(row1);

        int index3 = result.lastIndexOf("Row2:");
        int index4 = result.indexOf("Row3:");
        row2 = result.substring(index3, index4);
        row2 = row2.replace("<br>", " ");
        System.out.println(row2);

        int index5 = result.lastIndexOf("Row3:");
        int index6 = result.indexOf("Row4:");
        row3 = result.substring(index5, index6);
        row3 = row3.replace("<br>", " ");
        System.out.println(row3);

        int index7 = result.lastIndexOf("Row4:");
        int index8 = result.indexOf("Row5:");
        row4 = result.substring(index7, index8);
        row4 = row4.replace("<br>", " ");
        System.out.println(row4);

        int index9 = result.lastIndexOf("Row5:");
        int index10 = result.indexOf("Row6:");
        row5 = result.substring(index9, index10);
        row5 = row5.replace("<br>", " ");
        System.out.println(row5);

        int index11 = result.lastIndexOf("Row6:");
        row6 = result.substring(index11);
        row6 = row6.replace("<br>", " ");
        System.out.println(row6);


        intent.putExtra("row1", row1);
        intent.putExtra("row2", row2);
        intent.putExtra("row3", row3);
        intent.putExtra("row4", row4);
        intent.putExtra("row5", row5);
        intent.putExtra("row6", row6);
        intent.putExtra("Latitude", Double.toString(latitude));
        intent.putExtra("Longitude", Double.toString(longitude));


        startActivity(intent);
    }

    public void getLocationInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                 latitude = location.getLatitude();
                 longitude = location.getLongitude();

            }

        }

    }
}