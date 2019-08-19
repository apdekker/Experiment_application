package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class Trainingmode_Activity extends BlunoLibrary implements MessageListener, MediaPlayer.OnCompletionListener  {


    Button start, pause, buttonScan, nextbutton, previousbutton,volup, voldown, pauseplay;
    private MediaPlayer mPlayer, mPlayertwo, mediaPlayerthree;
    AudioManager audio;
    int currentVolume, oldcurrentVolume, currentAlarmVolume, oldAlarmVolume, currentDTMFVolume;

    boolean answerbool = false;
    boolean pausebool = false, playbool = true, callingbool = false, nocallingbool, nextbool = false, pausingbool = true, pressbool = false;

    Time today = new Time(Time.getCurrentTimezone());
    String sessionId, otp;
    List<String> list;
    PhoneStateListener callStateListener;
    TelephonyManager telephonyManager;
    Handler handler;
    int[] tracks = new int[8];
    int currentTrack = 0;
    double newinput = 4;
    int poten = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainingmode_);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_appbar);

        onCreateProcess();														//onCreate Process by BlunoLibrary
        serialBegin(115200);

        SmsReceiver.bindListener(this);


        otp ="";
        list = new ArrayList<String>();

        tracks[0] = R.raw.toto;
        tracks[1] = R.raw.paul2;
        tracks[2] = R.raw.sky;
        tracks[3]= R.raw.kelly;
        tracks[4]= R.raw.dis;
        tracks[5]=R.raw.pack;
        tracks[6]=R.raw.kol;
        tracks[7]=R.raw.way;



        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device

        mPlayer= MediaPlayer.create(getApplicationContext(), tracks[currentTrack]);
        mPlayertwo = MediaPlayer.create(getApplicationContext(), tracks[currentTrack+1]);

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        start = (Button) findViewById(R.id.btStart);
        //start.setEnabled(false);
        pause = (Button) findViewById(R.id.btPause);
        nextbutton =(Button)findViewById(R.id.btInput);
        previousbutton = (Button) findViewById(R.id.btInputtwo);
        volup = (Button) findViewById(R.id.btInputthree);
        voldown = (Button) findViewById(R.id.btInputfour);
        pauseplay = (Button) findViewById(R.id.btInputfive);


        audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC,8,0);
        currentAlarmVolume = audio.getStreamVolume(AudioManager.STREAM_ALARM);
        oldAlarmVolume = audio.getStreamVolume(AudioManager.STREAM_ALARM);
        oldcurrentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);







        buttonScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                buttonScanOnClickProcess();                                        //Alert Dialog for selecting the BLE device
            }
        });



        Intent intent = getIntent();
        sessionId = intent.getStringExtra("EXTRA_SESSION_ID");

        handler = new Handler();

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

                    String buttonID = "Incomming call";
                    today.setToNow();
                    String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                    list.add(timestamp+ "," + buttonID);


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


            }
        });


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ReturnbeginActivity();

                mPlayer.stop();


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


            }

        });


        previousbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mPlayer.seekTo(0);
                mPlayer.start();

            }

        });

        volup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);


            }

        });

        voldown.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

            }

        });

        pauseplay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if (playbool == false){
                    mPlayer.pause();
                    playbool = true;

                }

                else if (playbool == true){

                    mPlayer.start();
                    playbool = false;


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

            if (currentTrack == 7){
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

        Intent intent = new Intent(this, Beginscreen.class);
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

    public void volumeReceived(String message){
        Toast.makeText(this, "New Message Received: " + message, Toast.LENGTH_SHORT).show();
    }



    protected void onResume(){
        super.onResume();
        System.out.println("BlUNOActivity onResume");
        onResumeProcess();
        //onResume Process by BlunoLibrary
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, "New Message Received: ", Toast.LENGTH_SHORT).show();
    }





    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called



        switch (theConnectionState) {											//Four connection state
            case isConnected:
                buttonScan.setText("Connected");
                start.setEnabled(true);

                break;
            case isConnecting:
                buttonScan.setText("Connecting");
                break;
            case isToScan:
                buttonScan.setText("Scan");
                break;
            case isScanning:
                buttonScan.setText("Scanning");
                break;
            case isDisconnecting:
                buttonScan.setText("isDisconnecting");
                break;
            default:
                break;
        }
    }

    public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
        // TODO Auto-generated method stub
        CharSequence sensorvalue = new StringBuffer(theString);
        String c2 = sensorvalue.toString();
        newinput = valueOf2(c2);
        currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        currentAlarmVolume= audio.getStreamVolume(AudioManager.STREAM_ALARM);

    }

    public Double valueOf2( String inputString ) {
        return (inputString == null) ? null : Double.parseDouble(inputString);
    }





}


