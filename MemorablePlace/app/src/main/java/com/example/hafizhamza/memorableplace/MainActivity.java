package com.example.hafizhamza.memorableplace;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
ListView listView;
    static ArrayList<String> arrayList;
    static ArrayAdapter adapter;
    static  ArrayList<LatLng> locations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.listView);
        arrayList=new ArrayList<>();
        locations=new ArrayList<LatLng>();


        //DATA STORE
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.hafizhamza.memorableplace", Context.MODE_PRIVATE);
        ArrayList<String> Latitudes=new ArrayList<>();
        ArrayList<String> Longtitudes=new ArrayList<>();
        try {

            arrayList=(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("LocateName",ObjectSerializer.serialize(new ArrayList<>())));
            Latitudes=(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Latitude",ObjectSerializer.serialize(new ArrayList<>())));
            Longtitudes=(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Lontitude",ObjectSerializer.serialize(new ArrayList<>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (arrayList.size()>0 && Latitudes.size()>0 && Longtitudes.size()>0)
        {
            if (arrayList.size()==Latitudes.size()&& arrayList.size()==Longtitudes.size())
            {
                for (int i=0;i<Latitudes.size();i++) {
                    locations.add(new LatLng(Double.parseDouble(Latitudes.get(i)),Double.parseDouble(Longtitudes.get(i))));
                }
            }
        }else
        {
            arrayList.add("Add a New Place");
            locations.add(new LatLng(0,0));
        }



        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("Location",i);
                intent.putExtra("LocationName",arrayList.get(i));
                startActivity(intent);
            }
        });
    }
}
