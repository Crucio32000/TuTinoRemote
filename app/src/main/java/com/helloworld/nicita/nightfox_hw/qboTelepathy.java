package com.helloworld.nicita.nightfox_hw;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;

public class qboTelepathy extends AppCompatActivity implements View.OnClickListener {
    //Setting values
    private static final int NOSE_RED = 1;
    private static final int NOSE_GREEN = 2;
    private static final int NOSE_YELLOW = 3;
    private static float LIN_MAX = 0.4f;
    // Maximum values of PAN and TILT is now 1, since it takes values going from -1 to 1
    private static final float ANG_MAX = (float) Math.PI/4;
    private static final float PAN_MAX = 1;  //(float) Math.PI/4;
    private static final float TILT_MAX = 1; //(float) Math.PI/4;
    protected HashMap<String, String> toPost = new HashMap<String, String>();   //Storing data to send through POST method
    // final private String reqUrl = "http://www.nightfox32000.altervista.org/QboTelepathy/cmdStore.php";  //URL
    private String loginUrl = "http://192.9.200.92:90/ROS_telepathy/checkStatus.php";
    private String reqUrl = "http://192.9.200.92:90/ROS_telepathy/cmdStore.php";  //URL


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qbo_telepathy);

        //Put Android screen in Landscape and toggle ActionBar(useless in this activity)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getSupportActionBar().hide();


        //Check if user and pwd are set properly
        // Also set the correct URLs
        Bundle bundle = this.getIntent().getExtras();
        String user = bundle.getString("username");
        String password = bundle.getString("password");
        String hostIP = bundle.getString("host_IP");
        String videoIP = "http://"+hostIP+":"+bundle.getString("video_port")+"/stream?topic=/stereo/image_raw?quality=30";
        this.loginUrl = "http://" + hostIP + ":" + bundle.getString("remote_port") + "/inbound";  //where cmdStore.php is saved
        this.reqUrl = this.loginUrl + "/cmdStore.php";
        this.loginUrl = this.loginUrl + "/checkStatus.php";

        //Initialize WebView
        final WebView camView = (WebView) findViewById(R.id.camView);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        final Context thisContext = (Context) this; //Its going to be used a lot. UI widgets usually requires Context object

        camView.setWebViewClient(new webViewHandler((Context) this, 5000)); //Context and Timeout for the progressBar
        /*camView.setInitialScale(1);
        camView.getSettings().setJavaScriptEnabled(false);
        camView.getSettings().setLoadWithOverviewMode(true);
        camView.getSettings().setUseWideViewPort(true);
        camView.getSettings().setMixedContentMode(camView.getSettings().MIXED_CONTENT_ALWAYS_ALLOW);
        camView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        camView.setScrollbarFadingEnabled(false); */
        camView.loadUrl(videoIP);

        //Handle Refresh event to reload WebView URL --> webViewHandler



        //Checking if data into the bundle are correct
        HashMap<String, String> loginData = new HashMap<String, String>();
        if(user != null && !user.isEmpty())
        {
            loginData.put("user", user);
        } else {
            loginData.put("user", user);
        }
        loginData.put("version", Build.VERSION.RELEASE);
        loginData.put("device_name", Build.MODEL);
        loginData.put("manufacturer", Build.MANUFACTURER);
        loginData.put("pwd", password);
        final httpReqHandler reqResult = new httpReqHandler(loginData, this.loginUrl);
        reqResult.execute();

        //CountDownTimer required for updating the view with Response.
        // It returns if it has been connected to the remote server successfully or not
        CountDownTimer CDTimer = new CountDownTimer(2000,2000) {
            TextView httpDebug = (TextView) findViewById(R.id.httpDebug);
            int attempts = 0;
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                httpDebug.setText("Establishing Connection... " + this.attempts);
                httpDebug.setTextColor(Color.RED);
                if ((reqResult.getResponseCode() != 200)) //Check if we got some response.(Hopefully 200!)
                {
                    this.start();
                    attempts += 1;
                } else {
                    httpDebug.setText(reqResult.getResponseCode() + " OK");
                    httpDebug.setTextColor(Color.GREEN);
                }
            }
        }.start();



        //Bind btns to onClick. Creating an Array of Button Object and then, navigating through the array to bind them to onClick method
        // Defines the handle of the nose switch too.
        final ArrayList<Button> noseBtns = new ArrayList<Button>();
        noseBtns.add((Button) findViewById(R.id.noseRED));
        noseBtns.add((Button) findViewById(R.id.noseGREEN));
        noseBtns.add((Button) findViewById(R.id.noseYELLOW));
        noseBtns.add((Button) findViewById(R.id.noseOFF));
        //Initiate keys on ArrayList
        for (int i=0; i< noseBtns.size(); i++)
        {
            Button tempBtn = (Button) noseBtns.get(i);
            tempBtn.setOnClickListener(this);
        }

        // Edit Text
        final EditText t2s_text = (EditText) findViewById(R.id.t2s_text);
        t2s_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String c_char = s.toString();
                //CharSequence elem = ".";
                CharSequence elem = "\n";
                if (c_char.contains(elem))
                {
                    toPost.put("text", c_char);
                    httpReqHandler hh = new httpReqHandler(toPost, reqUrl);
                    hh.execute();
                    t2s_text.setText("");
                    while (hh.getProgress())
                    {
                        toPost.put("text", "");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Handle DPAD (that is a Linear Layout) and bind it to OnTouch event
        LinearLayout DPadL = (LinearLayout) findViewById(R.id.dpadLayout);
        DPadL.setOnTouchListener(new DPadListener(this.reqUrl));   //See class definition below

        //Handle Analog Pad (Joystick)
        //Get a handle on the layout and then put the custom defined view. Joystick Controller includes also the OnTouchListener
        LinearLayout PadOuter = (LinearLayout) findViewById(R.id.joystickPad);
        PadOuter.setWillNotDraw(false); //Required when drawing using Canvas
        JoystickController JoystickView = new JoystickController(this, 300, Color.DKGRAY, Color.BLACK);
        PadOuter.addView(JoystickView);
        PadOuter.setOnTouchListener(new JoystickListener(JoystickView, this.reqUrl));

        //Seek Bar for dynamic tuning of the linear speed
        SeekBar linBar = (SeekBar) findViewById(R.id.LinearBar);
        final TextView linBarDebug = (TextView) findViewById(R.id.seekDebug);
        linBar.setProgress((int) LIN_MAX * 100);
        linBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double new_value = progress / 100.0;
                LIN_MAX = (float) new_value;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // Handle NumberPicker for Drive Mode
        final SeekBar drive_mode = (SeekBar) findViewById(R.id.drive_mode);
        //drive_mode
        //drive_mode.setMaxValue(2);
        //drive_mode.setMinValue(0);
        drive_mode.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue,boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                    //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
                }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                toPost.put("drive_mode", "" + seekBar.getProgress());
                new httpReqHandler(toPost, reqUrl).execute();
                // Update Text View
                TextView drive_text = (TextView) findViewById(R.id.drive_mode_text);
                drive_text.setText("" + seekBar.getProgress());
            }
        });


        // Toggle Button for selecting /drive_mode
        /*final Switch drive_switch = (Switch) findViewById(R.id.drive_switch);
        drive_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    toPost.put("drive_mode", "1");
                    drive_switch.setText("Remote");
                    drive_switch.setTextColor(Color.GREEN);
                    drive_mode.setValue(1);
                } else {
                    toPost.put("drive_mode", "0");
                    drive_switch.setText("Auto");
                    drive_switch.setTextColor(Color.RED);
                    drive_mode.setValue(0);
                }
                new httpReqHandler(toPost, reqUrl).execute();
            }
        });
        drive_switch.setText("Remote");
        drive_switch.setTextColor(Color.GREEN);
         */


        //Init values (POST)
        this.toPost.put("drive_mode", "0");
        this.toPost.put("linear", "0");
        this.toPost.put("pan", "0");
        this.toPost.put("tilt", "0");
        this.toPost.put("text", "Sono Posseduto. Aiuto!");
        this.toPost.put("nose", "2");
        new httpReqHandler(toPost, reqUrl).execute();


    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    //
    // CLICK AND TOUCH EVENTS
    //

    //Handles all the button click event
    @Override
    public void onClick(View v)
    { //Handles buttons click events
        TextView labelNose = (TextView) findViewById(R.id.labelNose);
        Button noseOFF = (Button) findViewById(R.id.noseOFF);
        LinearLayout noseBtns = (LinearLayout) findViewById(R.id.noseBtns);
        SeekBar linBar = (SeekBar) findViewById(R.id.LinearBar);
        LinearLayout dpad = (LinearLayout) findViewById(R.id.dpadLayout);
        LinearLayout apad = (LinearLayout) findViewById(R.id.joystickPad);
        EditText t2s = (EditText) findViewById(R.id.t2s_text);
        SeekBar drive_mode = (SeekBar) findViewById(R.id.drive_mode);
        int visibility = noseBtns.getVisibility();
        switch (v.getId()) { //Depending on the clicked button
            case R.id.noseRED:
                toPost.put("nose", ""+(NOSE_RED));
                labelNose.setText("RED");
                break;
            case R.id.noseGREEN:
                toPost.put("nose", ""+(NOSE_GREEN));
                labelNose.setText("GREEN");
                break;
            case R.id.noseYELLOW:
                toPost.put("nose", ""+(NOSE_YELLOW));
                labelNose.setText("YELLOW");
                break;
            case R.id.noseOFF:  // Toggle Visibility bool value
                if (visibility != View.INVISIBLE) {
                    visibility = View.INVISIBLE;
                    noseOFF.setText(R.string.nose_off);
                    toPost.put("nose", "0");
                    new httpReqHandler(this.toPost, this.reqUrl).execute();
                } else {
                    visibility = View.VISIBLE;
                    noseOFF.setText(R.string.nose_on);
                    toPost.put("nose", "" + this.NOSE_GREEN);
                    new httpReqHandler(this.toPost, this.reqUrl).execute();
                }
                // Set visibility
                noseBtns.setVisibility(visibility);
                linBar.setVisibility(visibility);
                t2s.setVisibility(visibility);
                drive_mode.setVisibility(visibility);
                break;
        }
        //Send data to URL
        new httpReqHandler(this.toPost, this.reqUrl).execute();
    }

    //
    //Handles DPAD Touch Listener
    //
    private final class DPadListener implements View.OnTouchListener {
        private String reqUrl;
        
        public DPadListener(String reqUrl)
        {
            this.reqUrl = reqUrl;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {    //If finger down and moving
            float x = event.getX(); //Get finger position X
            float y = event.getY();
            float vWidth = v.getWidth();    //Get dimensions of the layout calling back onTouch
            float vHeight = v.getHeight();
            boolean UpOrDown = x>= vWidth/3 && x<=vWidth*2/3;
            boolean LeftOrRight = y>= vHeight/3 && y<= vHeight*2/3;
            if (UpOrDown && y<= vHeight/3)
            {
                toPost.put("linear", ""+(LIN_MAX));
                toPost.put("angular", "0");
                v.setBackgroundResource(R.drawable.dpad_up);
            } else if (UpOrDown && y>= vHeight*2/3) {
                toPost.put("linear", ""+(-1 * LIN_MAX));
                toPost.put("angular", "0");
                v.setBackgroundResource(R.drawable.dpad_down);
            } else if (LeftOrRight && x<= vWidth/3) {
                toPost.put("angular", ""+(ANG_MAX));
                toPost.put("linear", "0");
                v.setBackgroundResource(R.drawable.dpad_left);
            } else if (LeftOrRight && x>= vWidth*2/3) {
                toPost.put("angular", ""+(-1 * ANG_MAX));
                toPost.put("linear", "0");
                v.setBackgroundResource(R.drawable.dpad_right);
            }
            new httpReqHandler(toPost, this.reqUrl).execute();    //Send data

        } else if (action == MotionEvent.ACTION_UP) {  //When finger is up
            v.setBackgroundResource(R.drawable.dpad_normal);
            //Set Linear and Angular speed to 0
            toPost.put("linear", "0");
            toPost.put("angular", "0");
        }
        return true;
        }
    }

    //Handles Joystick PAD
    private final class JoystickListener implements View.OnTouchListener {
        JoystickController JoystickView;
        String reqUrl;

        public JoystickListener(JoystickController JoystickView, String reqUrl)
        {
            this.JoystickView = JoystickView;   //Get the view
            this.reqUrl = reqUrl;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            int action = event.getAction();
            float x = event.getX();
            float y = event.getY();
            float vWidth = v.getWidth();
            float vHeight = v.getHeight();
            //View Elements
            //TextView labelDebug = (TextView) findViewById(R.id.labelAngle); //DEBUG


            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE)
            {   //Finger detected and moving
                this.JoystickView.setPosition(x,y);
                this.JoystickView.updateDraw();
                //Update data to send
                float power = this.JoystickView.getPower();
                float angle = (float) Math.toRadians(this.JoystickView.getAngle());
                //Process components value
                float panComponent = -PAN_MAX * Math.round( (power * Math.cos(angle) ) * 100)/100;   //Trick to get two digits out of a float/double number
                float tiltComponent = TILT_MAX * Math.round( (power * Math.sin(angle) ) * 100)/100; //Pan goes - because axis is inverted
                //Put data into HashMaps
                toPost.put("pan", "" + panComponent);
                toPost.put("tilt", "" + tiltComponent);
                //UI Feedback update
                // labelDebug.setText("Pan:" + panComponent + "\nTilt:"+tiltComponent); // Used for DEBUG
                new httpReqHandler(toPost, this.reqUrl).execute();
            } else if (action == MotionEvent.ACTION_UP)
            {   //Set view to its initial position when finger is up
                JoystickView.resetPosition();
                JoystickView.updateDraw();
                //Set action to 0
                toPost.put("pan", "0");
                toPost.put("tilt", "0");
                new httpReqHandler(toPost, this.reqUrl).execute();
            }


            return true;
        }
    }

}
