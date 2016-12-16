package com.helloworld.nicita.nightfox_hw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;



/**
 * Created by Nicita on 12/12/2016.
 *
 */

public class MjpegFrameGetter extends AsyncTask {

    public boolean frameAvailable = false;
    private String reqUrl;
    private Bitmap bitmap;
    private ImageView linkedImgView;
    //private InputStream c_frame;
    //private ByteArrayOutputStream c_frame;
    private String frame;
    public boolean completed = false;

    protected int resultCode;

    public MjpegFrameGetter(String reqUrl, ImageView imgView)
    {
        this.reqUrl = reqUrl;
        this.linkedImgView = imgView;
    }

    @Override
    protected Object doInBackground(Object... arg0)
    {
        this.completed = false;
        this.getFrame();
        return null;
    }

    @Override
    protected void onPostExecute(Object param)
    {
        // DO something after doInBackground routine has been executed (.execute on the handler)
        this.completed = true;
    }

    public void getFrame()
    {
        this.frame = "";
        try{
            URL url = new URL(this.reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {   //Work with opened HTTP connection
                // Get bytes
                this.frameAvailable = false;    // Used to stop the while loop ahead
                while(!this.frameAvailable)
                {
                    //Read Output from InputStream
                    Scanner inStream = new Scanner(conn.getInputStream());
                    String temp = "";
                    boolean get_data = false;
                    int bound_cnt = 0;
                    this.resultCode = conn.getResponseCode();
                    while (inStream.hasNextLine())
                    {
                        temp = inStream.nextLine();
                        if( temp.contains("--jpgboundary") )
                        {
                            temp = inStream.nextLine();
                            get_data = !get_data;
                            bound_cnt++;
                        }
                        if(get_data)
                        {
                            this.frame += temp;
                        }
                        if(bound_cnt == 2)
                        {
                            break;
                        }
                    }
                    inStream.close();
                    this.frameAvailable = true;
                }
            } finally {
                conn.disconnect();
                show_frame();
            }

        } catch (MalformedURLException e)
        {
            this.frameAvailable = false;
        } catch (ProtocolException e)
        {
            this.frameAvailable = false;
        } catch (IOException e)
        {
            this.frameAvailable = false;
        }

    }

    public void show_frame()
    {
        //this.bitmap = BitmapFactory.decodeStream(this.c_frame);
        byte[] decodedBytes = Base64.decode(this.frame, 0);
        this.bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        this.linkedImgView.setImageBitmap(bitmap);
    }


    public int getResponseCode() { return this.resultCode; }
}
