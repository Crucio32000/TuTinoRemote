package com.helloworld.nicita.nightfox_hw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nicita on 09/02/16.
 */
public class cityAdapter extends BaseAdapter {
    //String[] cityName, cityComment;
    public ArrayList<HashMap<String,String>> dataList;
    Context context;
    TextView cName, cComment;

    //public cityAdapter(Context context, String[] cName, String[] cComment) {
    public cityAdapter(Context context, ArrayList<HashMap<String,String>> list) {
        super();
        //this.cityName = cName;
        //this.cityComment = cComment;
        this.dataList = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        //return cityName.length;
        return this.dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.dataList.get(position);
        //return cityName[position] +":" + cityComment[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {    //Handle view
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listview_row, null);
        }
        //Connecting elements to viewIds
        this.cName = (TextView) v.findViewById(R.id.cityText);
        this.cComment = (TextView) v.findViewById(R.id.cityComment);
        HashMap<String, String> map = this.dataList.get(position);   //Get Element inside the array
        this.cName.setText(map.get("name"));
        this.cComment.setText(map.get("comment"));
        if(position%2==0) { //Handling resources depending on some value/state
            this.cName.setTextColor(Color.RED);
        }

        return v;
    }
}
