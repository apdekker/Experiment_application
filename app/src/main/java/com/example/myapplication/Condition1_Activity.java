package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;



public class Condition1_Activity extends AppCompatActivity implements MessageListener, MediaPlayer.OnCompletionListener  {


    Button start, pause, buttonScan;
    private MediaPlayer mPlayer, mPlayertwo;
    AudioManager audio;
    int currentVolume, oldcurrentVolume, currentAlarmVolume, oldAlarmVolume;
    boolean answerbool = false;
    boolean pausebool = false, playbool = true, callingbool = false, nextbool = false;
    Time today = new Time(Time.getCurrentTimezone());
    String sessionId, otp, CallerID;
    List<String> list;
    PhoneStateListener callStateListener;
    TelephonyManager telephonyManager;
    Handler handler;
    int[] tracks = new int[4];
    int currentTrack = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition1_);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_appbar);
        getSupportActionBar().setSubtitle(" Condition: Buttons");




        SmsReceiver.bindListener(this);

        tracks[0] = R.raw.sky;
        tracks[1] = R.raw.dis;
        tracks[2] = R.raw.kol;
        tracks[3]= R.raw.sky;

        otp ="";
        list = new ArrayList<String>();

        mPlayer= MediaPlayer.create(getApplicationContext(), tracks[currentTrack]);
        mPlayertwo = MediaPlayer.create(getApplicationContext(), tracks[currentTrack+1]);

        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        start = (Button) findViewById(R.id.btStart);
        //start.setEnabled(false);
        buttonScan.setEnabled(false);
        pause = (Button) findViewById(R.id.btPause);



        currentAlarmVolume = audio.getStreamVolume(AudioManager.STREAM_ALARM);
        oldAlarmVolume = audio.getStreamVolume(AudioManager.STREAM_ALARM);
        oldcurrentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

        audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC,8,0);
        audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 15,0);




        Intent intent = getIntent();
        sessionId = intent.getStringExtra("EXTRA_SESSION_ID");

        handler = new Handler();

        mPlayer.setOnCompletionListener(this);

         telephonyManager =
                (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        callStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber)
            {

                if(state==TelephonyManager.CALL_STATE_RINGING){
                    Toast.makeText(getApplicationContext(),"Phone Is Riging",
                            Toast.LENGTH_LONG).show();
                    answerbool = true;
                    playbool = false;
                    mPlayer.pause();

                    String buttonID = "Incomming call from:";
                    today.setToNow();
                    String timestamp = today.format("%Y-%m-%d %H:%M:%S");

                    list.add(timestamp+ "," + buttonID +" "+ incomingNumber);


                }

                if (state==TelephonyManager.CALL_STATE_OFFHOOK){
                    callingbool = true;
                    playbool = false;


                    Toast.makeText(getApplicationContext(),"Phone Is Calling",
                            Toast.LENGTH_LONG).show();


                }

                if (state == TelephonyManager.CALL_STATE_IDLE){
                    playbool = true;
                    callingbool = false;
                    answerbool = false;;

                    Toast.makeText(getApplicationContext(),"IDLE STATE",
                            Toast.LENGTH_LONG).show();
                }


            }
        };


        telephonyManager.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE);



        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPlayer.start();
                handler.postDelayed(runnable, 0);

                today.setToNow();


                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+", START SESSION, " +"ID = " + sessionId +", Condition Button");

                Toast.makeText(getBaseContext(), "Condition started",
                        Toast.LENGTH_SHORT).show();

            }
        });


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ReturnbeginActivity();


                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp + ", Session ended");
                mPlayer.stop();


                String textfile = "ID." +sessionId + ".Condition1.txt";

                try {
                    FileOutputStream fileout=openFileOutput(textfile, MODE_PRIVATE);
                    OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
                    for (String s : list) {
                        outputWriter.write(s+"\n\r");

                    }
                    outputWriter.close();


                    //display file saved message
                    Toast.makeText(getBaseContext(), "File saved successfully!",
                            Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


        });







    }


    // READ VALUES OF FLIC BUTTON
    public Runnable runnable = new Runnable() {

        public void run() {

            currentAlarmVolume = audio.getStreamVolume(AudioManager.STREAM_ALARM);
            currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
            audio.getStreamVolume(AudioManager.STREAM_DTMF);


            handler.postDelayed(this, 0);


            // AWNSER PHONE CALL WITH FLIC BUTTON
            if (currentAlarmVolume == 1 && currentAlarmVolume != oldAlarmVolume) {
                //display file saved message

                String buttonID = "CALL ANSWERED";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);


                oldAlarmVolume = currentAlarmVolume;

            }

            // END PHONE CALL WITH FLIC BUTTON

            if (currentAlarmVolume == 10 && currentAlarmVolume != oldAlarmVolume ){

                mPlayer.start();
                String buttonID = "CALL ENDED";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);
                oldAlarmVolume = currentAlarmVolume;

            }

            // DECLINE CALL WITH FLIC BUTTON

            if (currentAlarmVolume == 2 && currentAlarmVolume != oldAlarmVolume ){

                audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
                String buttonID = "CALL DECLINED";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);
                oldAlarmVolume = currentAlarmVolume;
                mPlayer.start();

            }

            // PLAY SONG

            if ( currentAlarmVolume == 9 && currentAlarmVolume != oldAlarmVolume && pausebool == true ){
                mPlayer.start();
                pausebool = false;

                String buttonID = "PLAY";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);

                Toast.makeText(getBaseContext(), "PLAY",
                        Toast.LENGTH_SHORT).show();

                audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);

            }

            // PAUSE SONG
            else if (currentAlarmVolume == 9 && currentAlarmVolume != oldAlarmVolume && pausebool == false ){

                mPlayer.pause();
                pausebool = true;

                String buttonID = "PAUSE";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);

                Toast.makeText(getBaseContext(), "PAUSE",
                        Toast.LENGTH_SHORT).show();

                audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);

            }

            // VOLUME UP
            if (currentAlarmVolume == 3 && currentAlarmVolume != oldAlarmVolume ){


                audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);

                String buttonID2 = "VOLUME UP";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + "VOLUME DOWN"+ "," + currentVolume);



            }





            // VOLUME DOWN
            if (currentAlarmVolume == 5 && currentAlarmVolume != oldAlarmVolume ){


                audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
                String buttonID1 = "VOLUME DOWN";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + "VOLUME UP" + "."+ currentVolume);


            }



            // NEXT SONG

            if (currentAlarmVolume == 4 && currentAlarmVolume != oldAlarmVolume && currentTrack < tracks.length){

                audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
                mPlayer.pause();
                mPlayer = mPlayertwo;

                mPlayer.start();
                currentTrack++;

                mPlayertwo = MediaPlayer.create(getApplicationContext(), tracks[currentTrack+1]);


                String buttonID = "NEXT SONG";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);



            }






            // REPEAT SONG
            if (currentAlarmVolume == 8 && currentAlarmVolume != oldAlarmVolume ){

                mPlayer.seekTo(0);
                mPlayer.start();

                audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
                String buttonID = "REPEAT SONG";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);



            }

            if (currentTrack == 2){
                currentTrack = 0;
            }





        }

    };

    public void onCompletion(MediaPlayer arg0) {
        arg0.release();
        if (currentTrack < tracks.length) {
            currentTrack++;
            arg0 = MediaPlayer.create(getApplicationContext(), tracks[currentTrack]);
            arg0.setOnCompletionListener(this);
            arg0.start();
        }
    }



    public void ReturnbeginActivity() {

        boolean finished = true;
        Intent intent = new Intent(this, Beginscreen.class);
        intent.putExtra("CON1", finished );
        startActivity(intent);

    }

    @Override
    public void messageReceived(String message) {
        //Toast.makeText(this, "New Message Received: " + message, Toast.LENGTH_SHORT).show();

        otp = message;
        today.setToNow();
        String timestamp = today.format("%Y-%m-%d %H:%M:%S");
        String activitystamp = timestamp + "," + otp;

        list.add(activitystamp);

        audio.setStreamVolume(AudioManager.STREAM_MUSIC,8,0);
        audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);


    }



    public Double valueOf2( String inputString ) {
        return (inputString == null) ? null : Double.parseDouble(inputString);
    }





}


