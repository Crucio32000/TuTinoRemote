package com.helloworld.nicita.nightfox_hw;

import android.net.wifi.WifiConfiguration;
import android.os.AsyncTask;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by nicita on 15/02/16.
 */

//
// CLASSES / METHODS Definition for sending data through http using apache libs(deprecated in API 23-MarshMallow)
//

//Required for sending data over the Internet
//It requires as input an HashMap<String,String> where <Key,Value> sent through method POST
public class httpReqHandler extends AsyncTask {
    protected String reqUrl;
    protected HashMap<String, String> toPost;
    //Retrieved by external classes
    protected String resultContent = "Nobody touched me";
    protected int resultCode = 0;   //Used by HttpUrlConnection
    private boolean completed = false;

    //Constructor gets data to send in post. HashMap
    public httpReqHandler(HashMap<String, String> toPost, String reqUrl)
    {
        this.toPost = toPost;
        this.reqUrl = reqUrl;
    }
    @Override
    protected Object doInBackground(Object... arg0)
    {
        //this.postData();  //Data is posted but does not read page output. Moreover, it uses deprecated Apache libs
        this.postDataNew();
        return null;
    }

    @Override
    protected void onPostExecute(Object param) {
        this.completed = true;
    }


    //Actual function sending data to reqUrl using POST method
    //Use APACHE libs
    public void postData()
    {
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(this.reqUrl);
            //Create Post Data
            List<NameValuePair> postData = new ArrayList<NameValuePair>(1);
            postData = getPostData();
            //Set Post Data
            request.setEntity(new UrlEncodedFormEntity(postData));
            //Send Request
            HttpResponse response = client.execute(request);
        } catch (ClientProtocolException e) {
            //e.printStackTrace();
            this.resultContent = "Client Error";
        } catch (IOException e) {
            //e.printStackTrace();
            this.resultContent = "IO Error";
            //e.toString();
        } catch (Exception e) {
            this.resultContent = "Error occurred";
        }
    }

    private List<NameValuePair> getPostData() throws UnsupportedEncodingException {
        List<NameValuePair> postData = new ArrayList<NameValuePair>(1);
        for (int i=0; i<this.toPost.size(); i++)
        {
            //Use Iterator to navigate through the Map
            Set set = this.toPost.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext())
            {
                Map.Entry currentEntry = (Map.Entry) iterator.next();
                String aKey = (String) currentEntry.getKey(); //Returns an Object, therefore (String) cast is required
                String aValue = (String) (currentEntry.getValue()); //toString() removed. AndroidStudio says its redundant
                postData.add(new BasicNameValuePair(aKey, aValue));
            }
        }
        return postData;
    }


    //Http connection in a way compatible to API23
    public void postDataNew()
    {
        try {   //Tries to open an HTTP connection
            URL url = new URL(this.reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {   //Work with opened HTTP connection
                conn.setDoOutput(true); //Used for POST requests
                conn.setRequestMethod("POST");  //Not required actually.
                //conn.setDoInput(true);
                //conn.setChunkedStreamingMode(0);    //Used when we dont know how long is the request
                String params = getQuery();
                conn.setFixedLengthStreamingMode(params.getBytes().length); //When output length is known in advance, this method is the way to go
                conn.setRequestProperty("Content-type","application/x-www-form-urlencoded");
                // Write params on OutputStream
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(params);
                out.close();
                //Read Output from InputStream
                Scanner inStream = new Scanner(conn.getInputStream());
                this.resultContent = "";
                this.resultCode = conn.getResponseCode();
                while (inStream.hasNextLine())
                {
                    this.resultContent += inStream.nextLine();
                }
                inStream.close();
            } finally {
                conn.disconnect();
            }
        } catch (MalformedURLException e)
        {
            this.resultContent = "Url Error";
        } catch (ProtocolException e)
        {
            this.resultContent = "Protocol Error";
        } catch (IOException e)
        {
            this.resultContent = "Error while retrieving InputStream or Response Code";
        }
    }

    public String getQuery()
    {   //From HashMap toPost, creates a String query like param1=value1&param2=value2
        boolean isFirst = true;
        String query = "";
        Set set = this.toPost.entrySet();
        Iterator iter = set.iterator();
        while (iter.hasNext())
        {
            if(isFirst)
            {
                isFirst = false;
            } else {
                query += "&";
            }
            Map.Entry currentEntry = (Map.Entry) iter.next();
            try {
                query += currentEntry.getKey() + "=" + URLEncoder.encode((String) currentEntry.getValue(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                query += currentEntry.getKey() + "=Error";
            }
        }
        return query;
    }

    public String getStringResult()
    {
        return this.resultContent;
    }

    public int getResponseCode() { return this.resultCode; }

    public boolean getProgress() { return this.completed; }
}
