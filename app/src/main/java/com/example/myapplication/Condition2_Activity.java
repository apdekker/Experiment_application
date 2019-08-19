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

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class Condition2_Activity extends BlunoLibrary implements MessageListener, MediaPlayer.OnCompletionListener  {


    Button start, pause, buttonScan;
    private MediaPlayer mPlayer, mPlayertwo;
    AudioManager audio;
    int currentVolume, oldcurrentVolume, currentAlarmVolume, oldAlarmVolume, currentDTMFVolume;
    boolean answerbool = false;
    boolean pausebool = false, playbool = true, callingbool = false, nextbool = false, pausingbool = true, pressbool = false;
    Time today = new Time(Time.getCurrentTimezone());
    String sessionId, otp, CallerID;
    List<String> list;
    PhoneStateListener callStateListener;
    TelephonyManager telephonyManager;
    Handler handler;
    int[] tracks = new int[4];
    int currentTrack = 0;
    double newinput = 4;
    int poten = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition1_);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_appbar);
        getSupportActionBar().setSubtitle(" Condition: Rotation");

        onCreateProcess();														//onCreate Process by BlunoLibrary
        serialBegin(115200);

        SmsReceiver.bindListener(this);

        tracks[0] = R.raw.toto;
        tracks[1] = R.raw.paul2;
        tracks[2] = R.raw.way;
        tracks[3]= R.raw.queen;

        otp ="";
        list = new ArrayList<String>();

        mPlayer= MediaPlayer.create(getApplicationContext(), tracks[currentTrack]);
        mPlayertwo = MediaPlayer.create(getApplicationContext(), tracks[currentTrack+1]);

        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        start = (Button) findViewById(R.id.btStart);
        start.setEnabled(false);
        pause = (Button) findViewById(R.id.btPause);

        audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC,8,0);
        audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 15,0);

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

        mPlayer.setOnCompletionListener(this);

         telephonyManager =
                (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        callStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber)
            {

                if(state==TelephonyManager.CALL_STATE_RINGING){
                    Toast.makeText(getApplicationContext(),"Phone Is Riging",
                            Toast.LENGTH_LONG).show();

                    //playbool = false;
                    mPlayer.pause();

                    String buttonID = "Incomming call from:";
                    today.setToNow();
                    String timestamp = today.format("%Y-%m-%d %H:%M:%S");

                    list.add(timestamp+ "," + buttonID +" "+ incomingNumber);
                    poten = 3;


                }

                if (state==TelephonyManager.CALL_STATE_OFFHOOK){
                    mPlayer.pause();


                    Toast.makeText(getApplicationContext(),"Phone Is Calling",
                            Toast.LENGTH_LONG).show();

                    poten = 3;
                    pausebool = false;


                }

                if (state == TelephonyManager.CALL_STATE_IDLE){
                    playbool = true;


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
                list.add(timestamp+", START SESSION, " +"ID = " + sessionId +", Condition Rotation");

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


                String textfile = "ID." +sessionId + ".Condition2.txt";

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
            currentDTMFVolume = audio.getStreamVolume(AudioManager.STREAM_DTMF);


            handler.postDelayed(this, 0);


            if (currentDTMFVolume == 9){
                pressbool = true;
            }

            if (newinput > 4){
                poten = 1;
            }

            if (newinput < 4){
                poten =2;
            }






            // AWNSER PHONE CALL WITH FLIC BUTTON
            if (currentAlarmVolume == 1 && currentAlarmVolume != oldAlarmVolume) {
                //display file saved message

                String buttonID = "CALL ANSWERED";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);
                oldAlarmVolume = currentAlarmVolume;
                audio.setStreamVolume(AudioManager.STREAM_DTMF,7,0);






            }

            // END PHONE CALL WITH FLIC BUTTON

            if (currentAlarmVolume == 10 && currentAlarmVolume != oldAlarmVolume ){


                mPlayer.start();


                String buttonID = "CALL ENDED";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);
                oldAlarmVolume = currentAlarmVolume;
                poten = 0;
                audio.setStreamVolume(AudioManager.STREAM_DTMF,7,0);




            }

            // DECLINE CALL WITH FLIC BUTTON

            if (currentAlarmVolume == 2 && currentAlarmVolume != oldAlarmVolume ){

                audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
                audio.setStreamVolume(AudioManager.STREAM_DTMF,7,0);

                String buttonID = "CALL DECLINED";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);
                oldAlarmVolume = currentAlarmVolume;
                mPlayer.start();


                poten = 0;



            }

            // PLAY SONG



            if ( currentDTMFVolume == 10 && poten == 0 && pausebool == true && currentAlarmVolume == 7){
                mPlayer.start();
                pausebool = false;

                String buttonID = "PLAY";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);

                Toast.makeText(getBaseContext(), "PLAY",
                        Toast.LENGTH_SHORT).show();

                audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
                audio.setStreamVolume(AudioManager.STREAM_DTMF,7,0);
                audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 15,0);

                pressbool = false;
                poten = 0;


            }

            // PAUSE SONG
            else if (currentDTMFVolume == 10 && poten == 0 && pausebool == false && currentAlarmVolume == 7){

                mPlayer.pause();
                pausebool = true;

                String buttonID = "PAUSE";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);

                Toast.makeText(getBaseContext(), "PAUSE",
                        Toast.LENGTH_SHORT).show();

                audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
                audio.setStreamVolume(AudioManager.STREAM_DTMF,7,0);
                audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 15,0);

                pressbool = false;
                poten = 0;



            }




            // VOLUME UP
            if (newinput > 4 && currentAlarmVolume == 7 && pressbool == false){
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
                audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 15,0);



                String buttonID = "VOLUME UP";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID + "," + currentVolume);
                newinput = 4;
                poten = 0;



            }





            // VOLUME DOWN
            if (newinput < 4 && currentAlarmVolume == 7 && pressbool == false){

                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
                audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 15,0);


                String buttonID = "VOLUME DOWN";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID + "," + currentVolume);
                newinput = 4;
                poten = 0;



            }




            if (currentTrack == 2){
                currentTrack = 0;
            }


            // NEXT SONG

            if (currentDTMFVolume == 10 && pressbool == true && poten == 1) {

                audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
                audio.setStreamVolume(AudioManager.STREAM_DTMF,7,0);
                audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 15,0);

                mPlayer.pause();
                mPlayer = mPlayertwo;

                mPlayer.start();
                currentTrack++;

                mPlayertwo = MediaPlayer.create(getApplicationContext(), tracks[currentTrack+1]);


                String buttonID = "NEXT SONG";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);
                pressbool = false;
                poten = 0;


            }



            // REPEAT SONG
            if (currentDTMFVolume == 10 && pressbool == true && poten == 2){

                mPlayer.seekTo(0);
                mPlayer.start();

                audio.setStreamVolume(AudioManager.STREAM_ALARM,7,0);
                audio.setStreamVolume(AudioManager.STREAM_DTMF,7,0);
                audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 15,0);

                String buttonID = "REPEAT SONG";
                today.setToNow();
                String timestamp = today.format("%Y-%m-%d %H:%M:%S");
                list.add(timestamp+ "," + buttonID);
                pressbool = false;
                poten = 0;

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
        intent.putExtra("CON2", finished );
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
        poten = 0;

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

    @Override
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


