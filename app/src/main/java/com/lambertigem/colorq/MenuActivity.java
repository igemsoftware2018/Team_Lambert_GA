package com.lambertigem.colorq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void startApp(View view)
    {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void instructApp(View view)
    {
        Intent intent = new Intent(MenuActivity.this, InstructionsActivity.class);
        startActivity(intent);
    }

    public void creditApp(View view)
    {
        Intent intent = new Intent(MenuActivity.this, CreditActivity.class);
        startActivity(intent);
    }


}
