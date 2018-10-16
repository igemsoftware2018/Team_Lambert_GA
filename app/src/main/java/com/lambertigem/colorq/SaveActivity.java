package com.lambertigem.colorq;


import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class SaveActivity extends Activity {

    private String latitude;
    private String longitude;
    private String row1;
    private String row2;
    private String row3;
    private String row4;
    private String row5;
    private String row6;

    private EditText accesstohc;
    private String accesstohcvalue;
    private EditText bodyofwater;
    private String bodyofwatervalue;
    private EditText result;
    private String resultvalue;
    private EditText pathogen;
    private String pathogenvalue;
    private EditText notes;
    private String notesvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);


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
        }

    }



    public void sendServer(View v) {
        Bundle bundle = getIntent().getExtras();
        double[] doubles = bundle.getDoubleArray("arr");
        Intent intent = new Intent(SaveActivity.this, FinalActivity.class);
        accesstohc = findViewById(R.id.plain_text_input);
        accesstohcvalue = accesstohc.getText().toString();

        bodyofwater = (EditText)findViewById(R.id.plain_text_input2);
        bodyofwatervalue = bodyofwater.getText().toString();

        result = (EditText)findViewById(R.id.plain_text_input3);
        resultvalue = result.getText().toString();

        pathogen = (EditText)findViewById(R.id.plain_text_input4);
        pathogenvalue = pathogen.getText().toString();

        notes = (EditText)findViewById(R.id.plain_text_input5);
        notesvalue = notes.getText().toString();
        intent.putExtra("row1", row1);
        intent.putExtra("row2", row2);
        intent.putExtra("row3", row3);
        intent.putExtra("row4", row4);
        intent.putExtra("row5", row5);
        intent.putExtra("row6", row6);
        intent.putExtra("Latitude",latitude);
        intent.putExtra("Longitude", longitude);
        intent.putExtra("AccessToHC", accesstohcvalue);
        intent.putExtra("BodyOfWater", bodyofwatervalue);
        intent.putExtra("Result", resultvalue);
        intent.putExtra("Pathogen", pathogenvalue);
        intent.putExtra("Notes", notesvalue);


        startActivity(intent);
    }


}
