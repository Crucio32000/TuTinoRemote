package com.helloworld.nicita.nightfox_hw;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by nicita on 18/02/16.
 */
public class IPValidator implements TextWatcher {
    TextView viewDebug;
    EditText baby;
    int prevLength = 0;

    public IPValidator(TextView debugView,EditText babyText) {
        this.viewDebug = debugView;
        this.baby = babyText;

    }
    @Override
    final public void afterTextChanged(Editable s) {
        //Called every char that is being pressed by the User


    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //count signals if we are deleting or not. It is the opposite of after
        // start represent the char position (0 up to lenght-1)
        // after > 0, charlength is increasing


    }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) {
        //As Above
        String text = s.toString();
        boolean isIncreasing = text.length() > prevLength;
        this.prevLength = text.length();
        String[] bDots = text.split("\\.");
        String line = "";
        String buff = "";
        if (isIncreasing) {
            for (int i = 0; i < bDots.length; i++) {
                line = bDots[i];
                //Check current chunk
                if (line.length() >= 3 && i < 3) {
                    buff += line.subSequence(0, 3).toString();
                    buff += ".";
                    buff += line.subSequence(3,line.length()).toString();
                } else if (line.length() > 3 && i >= 3) {
                    buff += line.subSequence(0,3).toString();
                } else if (line.length() <= 3){
                    buff += line.subSequence(0,line.length()).toString();
                }
            }
        } else {
            buff = text;
        }

        //Check max length
        this.baby.removeTextChangedListener(this);
        this.baby.setText(buff);
        this.baby.addTextChangedListener(this); //Toggling the TextListener cause the cursor to focus on start
        this.baby.setSelection(this.baby.length()); //Put focus on the end

    }
}
