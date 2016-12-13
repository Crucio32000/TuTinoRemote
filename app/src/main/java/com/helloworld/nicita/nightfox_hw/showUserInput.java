package com.helloworld.nicita.nightfox_hw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class showUserInput extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_input);

        final TextView username = (TextView) findViewById(R.id.userOutput);
        final TextView password = (TextView) findViewById(R.id.passOutput);
        //Unpacking bundle
        Bundle bundle = this.getIntent().getExtras();
        username.setText(bundle.getString("username"));
        password.setText(bundle.getString("password"));

    }
}
