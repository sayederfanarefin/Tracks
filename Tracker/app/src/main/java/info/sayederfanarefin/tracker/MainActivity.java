package info.sayederfanarefin.tracker;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    protected LocationManager locationManager;
    TextView display, speed_display, waiting;
    private Button startButton ;
Boolean start = false;
float sec = 0;
    double lat1,lon1,lat2,lon2;
    float dist = 0;
    float[] result;
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 0; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; //mili secs

    TextView text;
    long starttime = 0;
    //this  posts a message to the main thread from our timertask
    //and updates the textfield
    final Handler h = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            long millis = System.currentTimeMillis() - starttime;
            sec = millis / 1000;
            int seconds = (int) (millis / 1000);

             int minutes = seconds / 60;
            seconds     = seconds % 60;

            int hour = minutes / 60;

            text.setText(String.format("%02d:%02d:%02d", hour, minutes, seconds));
            return false;
        }
    });
    //runs without timer be reposting self
    Handler h2 = new Handler();
    Runnable run = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - starttime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds     = seconds % 60;


            h2.postDelayed(this, 500);
        }
    };

    //tells handler to send a message
    class firstTask extends TimerTask {

        @Override
        public void run() {
            h.sendEmptyMessage(0);
        }
    };

    //tells activity to run on ui thread
    class secondTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    long millis = System.currentTimeMillis() - starttime;
                    int seconds = (int) (millis / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;


                }
            });
        }
    };


    Timer timer = new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display = (TextView) findViewById(R.id.textView_istance);

        text = (TextView)findViewById(R.id.textView_time);
        speed_display = (TextView) findViewById(R.id.textView_speed);
        waiting = (TextView) findViewById(R.id.textView_wait);
        waiting.setText("");
        startButton = (Button) findViewById(R.id.button_Start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!start) {
                    Toast.makeText(MainActivity.this,
                            "waiting...",
                            Toast.LENGTH_LONG).show();

                    speed_display.setText("Speed: 0.0 m/s");
                    Location location = booo();
                    boolean flag2 = false;

                    while (location == null) {
                        if(!flag2){
                            Toast.makeText(MainActivity.this,
                                    "waiting for locaiton...",
                                    Toast.LENGTH_LONG).show();
                        flag2 = true;
                    }
                        location = booo();

                    }
                    Toast.makeText(MainActivity.this,
                            "location not null",
                            Toast.LENGTH_LONG).show();

                    lat1 = location.getLatitude();
                    lon1 = location.getLongitude();
                    lat2 = location.getLatitude();
                    lon2 = location.getLongitude();
                    start = true;

                    starttime = System.currentTimeMillis();
                    timer = new Timer();
                    timer.schedule(new firstTask(), 0,500);
                    timer.schedule(new secondTask(),  0,500);
                    h2.postDelayed(run, 0);

                    startButton.setText("Stop");

                } else {
                    locationManager.removeUpdates(myLocationListener);
                    dist = 0;
                    lat1 = 0;
                    lat2 = 0;
                    lon2 = 0;
                    lon1 = 0;

                    timer.cancel();
                    timer.purge();
                    h2.removeCallbacks(run);
                    start = false;
                    startButton.setText("Start");
                    dist = 0;
                }
    }
});

    }



    private LocationListener myLocationListener = new LocationListener()
    {
        public void onLocationChanged(Location loc2)
        {
            if(loc2 != null){

                display.setText(String.valueOf(dist));

                lat2 = loc2.getLatitude();
                lon2 = loc2.getLongitude();

                float[] results = new float[1];
                Location.distanceBetween(
                        lat1, lon1,
                        lat2, lon2, results);

                dist = dist + results[0];
                double d = (double) dist;
                d = Math.round(d * 100.0) / 100.0;
                dist = (float)d;
                updateDistanceInTextView(dist);
                lat1 = lat2;
                lon1 = lon2;
                lat2 = 0;
                lon2 = 0;
            }
        }
        public void onStatusChanged(String provider, int status,
                                    Bundle extras)
        {
            Toast.makeText(MainActivity.this,
                    "GPS status changed",
                    Toast.LENGTH_LONG).show();
        }
        public void onProviderDisabled(String provider)
        {
            Toast.makeText(MainActivity.this,
                    "GPS is disabled",
                    Toast.LENGTH_LONG).show();
        }
        public void onProviderEnabled(String provider)
        {
            Toast.makeText(MainActivity.this,
                    "GPS is enabled",
                    Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void updateDistanceInTextView(final float yo){


        double speeed = dist/sec;
        speeed = Math.round(speeed * 100.0) / 100.0;

        final double s = speeed;


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                display.setText("Distance walked: " + String.valueOf(yo) + "m");
                speed_display.setText("Speed: "+ String.valueOf(s) + " m/s");
            }
        });
    }
    private Location booo(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // getting GPS status
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location = null;
        if(isGPSEnabled){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, myLocationListener);
            Log.d("GPS Enabled", "GPS Enabled");
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(criteria, true);
            location = locationManager.getLastKnownLocation(provider);

        }else if(isNetworkEnabled){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES, MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, myLocationListener);
            Log.d("network Enabled", "GPS Enabled");
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(criteria, true);
            location = locationManager.getLastKnownLocation(provider);
        }else{

        }
        return location;
    }
}
