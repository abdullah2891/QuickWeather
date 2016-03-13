package com.example.abdullah_rahman.quickweather;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

final public class MainActivity extends AppCompatActivity implements LocationListener {
    String apiUrl ="http://api.openweathermap.org/data/2.5/forecast/daily?" +
            "lat=35&lon=139&units=metric&cnt=10&mode=json&appid=44db6a862fba0b067b1930da0d769e98";
    LocationManager locationManager;
    float latt;float lng;
    String provider ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] forcastList = {"light rain","superStorm","End of the world"};
        ArrayList<Integer> imageId = new ArrayList<>();




        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria,false);
        Location location = locationManager.getLastKnownLocation(provider);
        if(location!=null){
            onLocationChanged(location);
            Log.i("Status","Success");
        }else{
            Log.i("Status","Failed");
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latt = (float) location.getLatitude();
        lng = (float) location.getLongitude();
        apiUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?" +
                "lat=" +String.valueOf(latt)+
                "&lon=" +String.valueOf(lng)+
                "&units=metric" +
                "&cnt=10" +
                "&mode=json" +
                "&appid=b1b15e88fa797225412429c1c50c122a";

        DownloadTask task = new DownloadTask();
        task.execute(apiUrl);

        Log.i("latt", String.valueOf(latt));
        Log.i("longitude", String.valueOf(lng));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public class DownloadTask extends AsyncTask<String,Void,String> {
        ListView listView;
        ArrayList<String> displayList;
        String status;
        String temperature;
        String desc;
        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection;
            int data;
            String result ="";
            try {
                url = new URL(urls[0]);
                urlConnection =(HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                data = reader.read();

                while (data!=-1){
                    result += (char)data;
                    data = reader.read();
                }
                Log.i("RAW", result);
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            displayList = new ArrayList<>();
            Date date = new Date();
            ArrayList<Integer> imageId= new ArrayList<>();
            ImageView imageView= (ImageView)findViewById(R.id.imageView);
            ArrayList<Integer> ImageId = new ArrayList<>();

            try {
                JSONObject rawData = new JSONObject(result);
                JSONArray list = rawData.getJSONArray("list");
                for (int i =0; i<list.length();i++){
                    JSONObject day = list.getJSONObject(i);
                    date.setTime(Long.parseLong(day.optString("dt"))*1000);
                    String dateString=String.valueOf(date);
                    JSONArray weather = day.getJSONArray("weather");
                    JSONObject detail = weather.getJSONObject(0);
                    status =detail.optString("main");

                    if (status.equals("Clouds")){ImageId.add(R.drawable.cloudyicon);
                    }if(status.equals("Rain")){ImageId.add(R.drawable.rain_icon);}
                    else {ImageId.add(R.drawable.sunny_small);}


                    JSONObject temp = day.getJSONObject("temp");
                    String dayTemp = temp.optString("day");
                    Log.i("temperature",dayTemp);
                    Log.i("description", detail.optString("description"));
                    if(i==0){
                        status= detail.optString("main");
                        temperature = temp.optString("day");
                        desc = detail.optString("description");
                    }




                    displayList.add("Date:" + dateString.substring(0, dateString.length() - 24) + "\n" +
                            dayTemp + "\n " +
                            detail.optString("description"));
                }
                temperature = temperature+(char)0x00B0 ;
                desc = "Today "+desc;
                TextView Temperature = (TextView)findViewById(R.id.temperature);Temperature.setText(temperature);
                TextView Desc = (TextView)findViewById(R.id.currentWeather);Desc.setText(desc);

                if(status.equals("Clouds")){
                    Log.i("case","cloudy");
                    imageView.setImageResource(R.drawable.cloudy);

                }else{
                    Log.i("case","default");}


                String[] display = displayList.toArray(new String[displayList.size()]);
                ListView listview = (ListView)findViewById(R.id.forcasts);
                customList arrayAdapter= new customList(MainActivity.this,display,ImageId);
                listview.setAdapter(arrayAdapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(getApplicationContext(),DetailView.class);
                        startActivity(i);
                    }
                });



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }


}
