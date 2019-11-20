package com.example.hafizhamza.memorableplace;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,GoogleMap.OnMapLongClickListener{
    LocationListener listener;
    LocationManager manager;
    private GoogleMap mMap;
    int a=0;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1)
        {
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,listener);
                    Location lastknownlocation=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                   center(lastknownlocation,"Your Location");
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void center(Location location,String title)
    {
        if (location!=null) {
            LatLng userlocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userlocation).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation,15));
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        Intent intent=getIntent();
        if (intent.getIntExtra("Location",0)==0) {
            Toast.makeText(getApplicationContext(), intent.getStringExtra("LocationName"), Toast.LENGTH_SHORT).show();
            manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                   while(a<=0) {
                       center(location, "Your Location");
                       a++;
                   }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            if (Build.VERSION.SDK_INT < 23) {

                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                    Location lastknownlocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    center(lastknownlocation, "Your Location");
                }
            }
        }else {
            Location savelocation=new Location(LocationManager.GPS_PROVIDER);
            savelocation.setLatitude(MainActivity.locations.get(intent.getIntExtra("Location",0)).latitude);
            savelocation.setLongitude(MainActivity.locations.get(intent.getIntExtra("Location",0)).longitude);
            center(savelocation,MainActivity.arrayList.get(intent.getIntExtra("Location",0)));
        }
    }


    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addressList =null;
                String address="";

        try {
            addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude, 1);
            if (addressList.get(0)!=null && addressList.size()>0)
            {
                address="Address: ";
                if (addressList.get(0).getThoroughfare()!=null) {
                    if (addressList.get(0).getSubThoroughfare()!=null) {
                    address+=addressList.get(0).getSubThoroughfare()+" ";
                    }
                    address+=addressList.get(0).getThoroughfare();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (address.equals(""))
        {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm yyyy-MM-dd");
            address+=simpleDateFormat.format(new Date());
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        MainActivity.arrayList.add(address);
        MainActivity.locations.add(latLng);
        MainActivity.adapter.notifyDataSetChanged();
        //DATA STORE
        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.hafizhamza.memorableplace;", Context.MODE_PRIVATE);

        try {
            ArrayList<String> Latitudes=new ArrayList<>();
            ArrayList<String> Longtitudes=new ArrayList<>();
            for (LatLng coord:MainActivity.locations)
            {
                Latitudes.add(Double.toString(coord.latitude));
                Longtitudes.add(Double.toString(coord.longitude));
            }
            sharedPreferences.edit().putString("LocateName",ObjectSerializer.serialize(MainActivity.arrayList)).apply();
            sharedPreferences.edit().putString("Latitude",ObjectSerializer.serialize(Latitudes)).apply();
            sharedPreferences.edit().putString("Longtitude",ObjectSerializer.serialize(Longtitudes)).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Toast.makeText(this,"Location Has Saved",Toast.LENGTH_SHORT).show();
    }
}
