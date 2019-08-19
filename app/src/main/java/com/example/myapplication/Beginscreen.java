package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Beginscreen extends AppCompatActivity {

    String sessionId;
    Boolean statusC1, statusC2, statusC3, statusC4;
    static int statusint1, statusint2, statusint3, statusint4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_appbar);

        Button butcon1 = (Button) findViewById(R.id.button);
        Button butcon2 = (Button) findViewById(R.id.button2);
        Button butcon3 = (Button) findViewById(R.id.button3);
        Button butcon4 = (Button) findViewById(R.id.button4);




        Intent intent = getIntent();
        sessionId = intent.getStringExtra("EXTRA_SESSION_ID");

        statusC1 = getIntent().getBooleanExtra("CON1",false);
        statusC2 = getIntent().getBooleanExtra("CON2",false);
        statusC3 = getIntent().getBooleanExtra("CON3",false);
        statusC4 = intent.getBooleanExtra("CON4", false);




        // Condition 1 is complete
        if (statusC1){
            Toast.makeText(getBaseContext(), "Condition 1 is complete",
                    Toast.LENGTH_SHORT).show();

            //butcon1.setBackgroundColor(Color.green(1));
            statusint1 = 1;

        }

        if (statusint1 == 1){
            butcon1.setBackgroundColor(Color.green(1));
        }



        // Condition 2 is complete
        if (statusC2){
            Toast.makeText(getBaseContext(), "Condition 2 is complete",
                    Toast.LENGTH_SHORT).show();
            statusint2 = 1;
            //butcon2.setBackgroundColor(Color.green(1));

        }

        if (statusint2 == 1){
            butcon2.setBackgroundColor(Color.green(1));
        }



        // Condition 3 is complete
        if (statusC3){
            Toast.makeText(getBaseContext(), "Condition 3 is complete",
                    Toast.LENGTH_SHORT).show();
            statusint3 = 1;
            //butcon3.setBackgroundColor(Color.green(1));

        }

        if(statusint3==1){
            butcon3.setBackgroundColor(Color.green(1));

        }

        // Condition 4 is complete
        if (statusC4){
            Toast.makeText(getBaseContext(), "Condition 4 is complete",
                    Toast.LENGTH_SHORT).show();
            statusint4 =1;
            //butcon4.setBackgroundColor(Color.green(1));

        }

        if(statusint4==1){
            butcon4.setBackgroundColor(Color.green(1));

        }


        // If all conditions are completed
        if (statusint1 == 1 && statusint2 ==1 && statusint3 == 1 && statusint4 ==1){
            Toast.makeText(getBaseContext(), "Finnished",
                    Toast.LENGTH_SHORT).show();


        }

    }


    public void openCondition1Activity(View view) {


        Intent intent = new Intent(this, Condition1_Activity.class);
        intent.putExtra("EXTRA_SESSION_ID", sessionId);
        startActivity(intent);


    }

    public void openCondition2Activity(View view) {

        Intent intent2 = new Intent(this, Condition2_Activity.class);
        intent2.putExtra("EXTRA_SESSION_ID", sessionId);
        startActivity(intent2);
    }

    public void openCondition3Activity(View view) {

        Intent intent3 = new Intent(this, Condition4_Activity.class);
        intent3.putExtra("EXTRA_SESSION_ID", sessionId);
        startActivity(intent3);
    }

    public void openCondition4Activity(View view){

        Intent intent4 = new Intent(this, Condition3_Activity.class);
        intent4.putExtra("EXTRA_SESSION_ID", sessionId);
        startActivity(intent4);
    }

    public  void openTrainingmode_Activity(View view){
        Intent intent5 = new Intent(this, Trainingmode_Activity.class);
        startActivity(intent5);

    }





}
