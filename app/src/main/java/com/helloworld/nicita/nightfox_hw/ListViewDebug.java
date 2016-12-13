package com.helloworld.nicita.nightfox_hw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//  import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewDebug extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_debug);

        final String[] citta = new String[]{"Torino", "Roma", "Milano", "Napoli", "Firenze"};   //Retrieving data
        final String[] commenti = new String[]{"Mole Antonelliana", "Colosseo", "Piazza Duomo", "Toledo", "Cupola"};
        //cityAdapter adapter = new cityAdapter(this, citta,commenti);
        //ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, R.layout.listview_row,R.id.cityText,citta);   //Setting up Row resource(layout->element)
        //ArrayAdapter<String> adapter2=new ArrayAdapter<String>(this,R.layout.listview_row,R.id.cityComment,commenti);

        //Create the ArrayList<HashMap<String, String>> to pass at the custom adapter
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < citta.length; i++) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put("name", citta[i]);
            temp.put("comment", commenti[i]);
            list.add(temp);
        }
        cityAdapter adapter = new cityAdapter(this, list);  //Our Custom adapter manages all the connections
        ListView listView = (ListView) findViewById(R.id.hwListView);   //Getting a handle on the listview
        listView.setAdapter(adapter);   //Connecting listview to data/custom adapter
        //Binding item click to a function;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,long id)
            {   //pos is the position of the Item clicked by the user
                Toast.makeText(getApplicationContext(),
                        "Selezionato "+ citta[pos], Toast.LENGTH_SHORT).show();  //Integer.toString(int something)
            }
        });
    }
}
