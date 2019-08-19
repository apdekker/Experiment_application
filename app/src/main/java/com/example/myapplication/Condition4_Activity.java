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



public class Condition4_Activity extends AppCompatActivity implements MessageListener, MediaPlayer.OnCompletionListener  {


    Button start, pause, reset, nextbutton, previousbutton, volup, voldown, pauseplay;

    Handler handler;
    private MediaPlayer mPlayer, mPlayertwo, mediaPlayerthree, endclip,  pickingup, intro, maxvolume, minvolume, pausesong, playsong, wrong, correct, decline, answer, nextsong, repeatsong, voicemessage;
    AudioManager audio;

    int currentVolume, oldcurrentVolume;

    boolean playbool = false;



    Time today = new Time(Time.getCurrentTimezone());
    String newinputst, sessionId;
    String otp;
    List<String> list;
    PhoneStateListener callStateListener;
    TelephonyManager telephonyManager;

    int[] tracks = new int[4];
    int currentTrack = 0;
    int testen = 0;


    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition4_);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_appbar);
        getSupportActionBar().setSubtitle(" Condition: One hand");



        SmsReceiver.bindListener(this);
        otp ="";
        list = new ArrayList<String>();

        tracks[0] = R.raw.pack;
        tracks[1] = R.raw.kelly;
        tracks[2] = R.raw.sky;
        tracks[3]= R.raw.way;


        Intent intent = getIntent();
        sessionId = intent.getStringExtra("EXTRA_SESSION_ID");


        start = (Button) findViewById(R.id.btStart);
        pause = (Button) findViewById(R.id.btPause);
        nextbutton =(Button)findViewById(R.id.btInput);
        previousbutton = (Button) findViewById(R.id.btInputtwo);
        volup = (Button) findViewById(R.id.btInputthree);
        voldown = (Button) findViewById(R.id.btInputfour);
        pauseplay = (Button) findViewById(R.id.btInputfive);


        mPlayer= MediaPlayer.create(getApplicationContext(), tracks[currentTrack]);
        mPlayertwo = MediaPlayer.create(getApplicationContext(), tracks[currentTrack+1]);

        mediaPlayerthree = MediaPlayer.create(Condition4_Activity.this, R.raw.good);

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        handler = new Handler();
        oldcurrentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

        audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC,8,0);
        audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 15,0);


        telephonyManager =
                (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        callStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber)
            {


                if(state==TelephonyManager.CALL_STATE_RINGING){
                    Toast.makeText(getApplicationContext(),"Phone Is Riging",
                            Toast.LENGTH_LONG).show();

                    testen = 1;

                    String buttonID = "Incomming call from: ";
                    today.setToNow();
                    String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                    list.add(timestamp+ "," + buttonID + incomingNumber);
                    mPlayer.pause();

                }

                if (state==TelephonyManager.CALL_STATE_OFFHOOK){

                    Toast.makeText(getApplicationContext(),"Phone Is Calling",
                            Toast.LENGTH_LONG).show();

                    String buttonID = "Call answerd";
                    today.setToNow();
                    String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                    list.add(timestamp+ "," + buttonID);

                    testen = 2;



                }


                if (state == TelephonyManager.CALL_STATE_IDLE && testen == 1){
                    mPlayer.start();
                    testen = 0;

                    String buttonID = "CALL DECLINED";
                    today.setToNow();
                    String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                    list.add(timestamp+ "," + buttonID);

                }

                if (state == TelephonyManager.CALL_STATE_IDLE && testen == 2){
                    mPlayer.start();
                    testen = 0;

                    String buttonID = "CALL ENDED";
                    today.setToNow();
                    String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                    list.add(timestamp+ "," + buttonID);

                }






            }
        };


        telephonyManager.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE);



        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPlayer.start();
                today.setToNow();
                handler.postDelayed(runnable, 0);


                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+", START SESSION, " +"ID = " + sessionId +", Condition One hand");

                Toast.makeText(getBaseContext(), "Condition started",
                        Toast.LENGTH_SHORT).show();

            }
        });


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp + ", Session ended");
                mPlayer.stop();
                ReturnbeginActivity();


                String textfile = "ID." +sessionId + ".Condition4.txt";

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



        nextbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

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

        });


        previousbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mPlayer.seekTo(0);
                mPlayer.start();

                String buttonID = "REPEAT SONG";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);



            }

        });

        volup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + "VOLUME UP" + ","+ currentVolume);

            }

        });

        voldown.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + "VOLUME DOWN" + ","+ currentVolume);



            }

        });

        pauseplay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if (playbool == false){
                    mPlayer.pause();
                    playbool = true;
                    String buttonID = "pause";
                    today.setToNow();
                    String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                    list.add(timestamp+ "," + buttonID);

                }

                else if (playbool == true){

                    mPlayer.start();
                    playbool = false;
                    String buttonID = "play";
                    today.setToNow();
                    String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                    list.add(timestamp+ "," + buttonID);


                }


            }

        });

    }

    public void ReturnbeginActivity() {

        boolean finished = true;
        Intent intent = new Intent(this, Beginscreen.class);
        intent.putExtra("CON4", finished );
        startActivity(intent);

    }


    public Runnable runnable = new Runnable() {

        public void run() {


            currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);


            handler.postDelayed(this, 0);


            if (currentTrack == 2){
                currentTrack = 0;
            }



        }

    };


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

    public void onCompletion(MediaPlayer arg0) {
        arg0.release();
        if (currentTrack < tracks.length) {
            currentTrack++;
            arg0 = MediaPlayer.create(getApplicationContext(), tracks[currentTrack]);
            arg0.setOnCompletionListener(this);
            arg0.start();
        }
    }





}


