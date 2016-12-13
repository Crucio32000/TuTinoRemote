package com.helloworld.nicita.nightfox_hw;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.NotificationCompat;   //Exists v4 lib too
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Constructor. First method called when instantiating the Activity. In this case, when the app is executed
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Bind Button to onClick function
        final Button loginBtn = (Button) findViewById(R.id.loginBtn);   //final keyword is used for ensuring that its value is not lost when going out of the current scope
        loginBtn.setOnClickListener(this);
        final Button cancelButton = (Button) findViewById(R.id.cancelBtn);
        cancelButton.setOnClickListener(this);
        //EditTexts
        EditText remoteEdit = (EditText) findViewById(R.id.remotePortEdit);
        remoteEdit.setText("5050");   //hostEdit.setText("192.9.200.103:5050/inbound");
        EditText videoEdit = (EditText) findViewById(R.id.videoPortEdit);
        videoEdit.setText("2020");  //IP_VIDEOEdit.setText("192.9.200.103:2020/cam.mjpg");  // 192.9.200.90
        EditText hostIP = (EditText) findViewById(R.id.IP_HOST);
        hostIP.setText("10.102.156.36");
        // ipEdit.addTextChangedListener(new IPValidator((TextView) findViewById(R.id.validateDebug), ipEdit)); // Works only when numbers between dots are 3 (192.9 is not allowed, 192.009 yes)

        // Notifications Builder
        /*final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        final NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //CountDown Timer. Implemented by Android;
        CountDownTimer CDTest = new CountDownTimer(10000, 1000) {
            TextView CD_Debug = (TextView) findViewById(R.id.CountDownDebug);
            int nCounter = 0;
            public void onTick(long msUntilFinish) {
                CD_Debug.setText("Secs remaining:" + msUntilFinish / 1000);
            }

            public void onFinish() {
                CD_Debug.setText("");
                nCounter += 1;
                if (nCounter > 5) {
                    mNotifyMgr.cancelAll();
                }else {

                    mBuilder.setSmallIcon(R.mipmap.app_launcher)
                            .setContentTitle("First Notification")
                            .setContentText("Hello World")
                            .setContentInfo("Notification Number:" + nCounter);
                    mNotifyMgr.notify(0, mBuilder.build());
                    this.start(); //Reboots the timer
                }
            }
        };
        CDTest.start(); */

    }
        //Handles all the buttons click event
        @Override
        public void onClick(View v)
        { //Handles buttons click events
            switch (v.getId()) { //Depending on the clicked button
                case R.id.loginBtn:
                    //Retrieving EditText elements and text
                    EditText username = (EditText) findViewById(R.id.userEdit);
                    EditText password = (EditText) findViewById(R.id.passEdit);
                    EditText remoteEdit = (EditText) findViewById(R.id.remotePortEdit);
                    EditText videoEdit = (EditText) findViewById(R.id.videoPortEdit);
                    EditText hostIPEdit = (EditText) findViewById(R.id.IP_HOST);
                    String account_username = username.getText().toString();
                    String account_password = password.getText().toString();
                    String remotePort = remoteEdit.getText().toString();
                    String videoPort = videoEdit.getText().toString();
                    String host_IP = hostIPEdit.getText().toString();
                    //String userIP = IP.getText().toString();
                    //String cmdIP = IP_VIDEO.getText().toString();

                    //Packing data to Intent(bundle)
                    Bundle bundle = new Bundle();
                    bundle.putString("username", account_username);
                    bundle.putString("password", account_password);
                    bundle.putString("remote_port", remotePort);
                    bundle.putString("video_port", videoPort);
                    bundle.putString("host_IP", host_IP);
                    Intent formIntent = new Intent(getApplicationContext(), qboTelepathy.class); //Target class/Activity
                    formIntent.putExtras(bundle);
                    startActivity(formIntent);
                    break;
                case R.id.cancelBtn:
                    EditText editUsername = (EditText) findViewById(R.id.userEdit);
                    EditText editPassword = (EditText) findViewById(R.id.passEdit);
                    editUsername.setText("");
                    editPassword.setText("");
                    break;
            }
        }


        //
        // MENU (3 dot menu )
        //
        @Override
        public boolean onCreateOptionsMenu (Menu menu)
        {
            getMenuInflater().inflate(R.menu.main_menu, menu);    //Set menu XML
            return true;
        }
        //Handling menu options click
        @Override
        public boolean onOptionsItemSelected (MenuItem item)
        {
            int id = item.getItemId();
            TextView debugMenu = (TextView) findViewById(R.id.menuDebug);
            switch (id) {
                case R.id.MainPage:
                    debugMenu.setText("MainPage selected!");
                    break;
                case R.id.MapsPage:
                    debugMenu.setText("AboutPage selected!");
                    Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                    PackageManager pkgManager = getPackageManager();
                    List<ResolveInfo> activities = pkgManager.queryIntentActivities(mapIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;
                    if (isIntentSafe) { startActivity(mapIntent); }
                    break;
                case R.id.ListViewPage:
                    Intent listViewIntent = new Intent(getApplicationContext(), ListViewDebug.class);
                    startActivity(listViewIntent);
                    break;
            }
            return false;
        }
    }