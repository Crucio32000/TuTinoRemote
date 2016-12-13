package com.helloworld.nicita.nightfox_hw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by nicita on 10/02/16.
 */
public class JoystickController extends View {
    protected int radius;
    protected int outerColor;
    protected int innerColor;
    protected int CenterX;
    protected int CenterY;
    protected float dx;
    protected float dy;
    //Values for UI
    protected double angle = 0;
    protected float distance = 0;
    //Boolean for directions
    public int STICK_UP = 0;
    public int STICK_DOWN = 1;
    public int STICK_LEFT = 2;
    public int STICK_RIGHT = 3;

    public JoystickController(Context context, int radius, int outerPadColor, int innerPadColor)
    {   //Constructor
        super(context);
        this.radius = radius;
        this.outerColor = outerPadColor;
        this.innerColor = innerPadColor;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {   //Called when the View changed its size. Required since the view will change size to fit into the Layout element after Constructor routine execution.
        this.CenterX = w/2;
        this.CenterY = h/2;
        this.dx = this.CenterX;
        this.dy = this.CenterY;
        this.updateDraw();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {   //Override onDraw
        super.onDraw(canvas);
        //Set Center coordinates
        //this.CenterX = this.getWidth() / 2;
        //this.CenterY = this.getHeight() / 2;
        //Paint required
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        //paint.setColor(Color.TRANSPARENT);
        //canvas.drawPaint(paint);    //draw background
        // Use Color.parseColor to define HTML colors
        //paint.setColor(Color.parseColor("#CD5C5C"));
        paint.setColor(this.outerColor);
        canvas.drawCircle(CenterX, CenterY, this.radius/2, paint); //Draw Outer Circle

        paint.setColor(this.innerColor);
        canvas.drawCircle(this.dx, this.dy, this.radius/3, paint);   //Draw Inner Circle
    }

    protected void updateDraw()
    {
        this.invalidate();
    }

    protected void setPosition(float x, float y)
    {   //X and Y are the coordinate of the finger, given by the onTouchListener binded in the activity , using this View
        //Translating point for debug easiness
        float posX = (x - this.CenterX);
        float posY = (y - this.CenterY);
        //Evaluate distance from center
        this.distance = (float) Math.sqrt(Math.pow(posX,2) + Math.pow(posY,2));
        //Check if the innerCircle must be limited or not
        if (this.distance > this.CenterX-this.radius/3)
        {   //Put center of the innerCircle to the perimeter of the outerCircle. Polar Coordinates are used
            this.angle = this.cal_angle(posX,posY);
            float finalX = (float) (Math.cos(Math.toRadians(this.angle))) * this.radius/2;
            float finalY = (float) (Math.sin(Math.toRadians(this.angle))) * this.radius/2;
            //Evaluating final innerCircle position
            finalX += this.CenterX;
            finalY += this.CenterY;
            this.dx = finalX;
            this.dy = finalY;
        } else if(this.distance <= this.CenterX) {
            this.dx = x;
            this.dy = y;
            this.angle = this.cal_angle(x,y);
        }
    }


    protected void resetPosition()
    {
        this.dx = this.CenterX;
        this.dy = this.CenterY;
    }


    private double cal_angle(float x, float y) {    //To Be Fixed
        if(x >= 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x));
        else if(x < 0 && y >= 0)
            return  Math.toDegrees(Math.atan(y / x)) + 180;   //previously 180
        else if(x < 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if(x >= 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 360 ;  //360 prev.
        return 0;
    }

    protected double getAngle()
    {
        return this.angle;
    }

    protected float getDistance()
    {
        return Math.round(this.distance);
    }

    protected int getMovement()
    {   //Takes touch coordinates and evaluate movement respect to the center of the View

        if (this.angle > 45 && this.angle < 90 + 45)
        {
            return this.STICK_DOWN;
        } else if (this.angle >= 90+45 && this.angle < 180 + 45){
            return this.STICK_LEFT;
        } else if (this.angle >= 180 + 45 && this.angle < 360 - 45) {
            return this.STICK_UP;
        } else {
            return this.STICK_RIGHT;
        }
    }

    protected float getPower()
    {   //Get a coefficient spanning from 0 to 1. Ratio between distance from the center and perimeter of the outerCircle
        float power = this.distance/this.radius*2;  //distance/radius of the outer circle
        return power;
    }


}
