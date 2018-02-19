package edu.dartmouth.cs.actiontabs;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Calendar;

/*
 *  The activity of google map
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ServiceConnection {

    private GoogleMap mMap;
    private ServiceConnection mConnection = this;
    private TrackLocation.trackingBinder binder;
    private databaseItem item;                                                  //define the database entry
    private TextView status, avgSpeed, curSpeed, climb, calorie, distance;      //the textview showed in the activity
    private String type, inputType;
    private PolylineOptions rectOptions;                                        //the drawing line
    private DataBaseHelper helper;
    private Calendar mDateAndTime = Calendar.getInstance();
    private long startTime;
    private Marker startMarker, endMarker;                                      //the mark for start and end
    private String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        setUpMapIfNeeded();
        Intent intent = getIntent();
        type = intent.getStringExtra("ActivityType");
        inputType = intent.getStringExtra("InputType");
        System.out.println(inputType);
        status = (TextView) findViewById(R.id.type_stats);
        avgSpeed = (TextView) findViewById(R.id.avg_speed);
        curSpeed = (TextView) findViewById(R.id.cur_speed);
        climb = (TextView) findViewById(R.id.climb);
        calorie = (TextView) findViewById(R.id.map_calorie);
        distance = (TextView) findViewById(R.id.map_distance);
        helper = new DataBaseHelper(getApplicationContext());
    }


    /*
   * Broadcast Receiver of tracking service
   */
    private BroadcastReceiver onEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            if (binder != null) {
                res = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                        getString("Unit Preference", "Imperial (Miles)");
                System.out.println(res);
                item = binder.getItems();
                status.setText("Type: " + type);
                if (res.equals("Imperial (Miles)")) {
                    avgSpeed.setText("Avg speed: " + item.getAvgSpeed() + " m/h");
                    curSpeed.setText("Cur Speed: " + item.getCurSpeed() + " m/h");
                    climb.setText("Climb: " + item.getClimb() + " Miles");
                    int cal = (int) (item.getDistance() * 99.456);
                    calorie.setText("Calorie: " + cal);
                    distance.setText("Distance: " + item.getDistance() + " Miles");
                } else {
                    avgSpeed.setText("Avg speed: " + (item.getAvgSpeed() * 1.61) + " km/h");
                    curSpeed.setText("Cur Speed: " + (item.getCurSpeed() * 1.61) + " km/h");
                    climb.setText("Climb: " + (item.getClimb() * 1.61) + " Kilometers");
                    int cal = (int) (item.getDistance() * 99.456);
                    calorie.setText("Calorie: " + cal);
                    distance.setText("Distance: " + (item.getDistance() * 1.61) + " Kilometers");
                }
                LatLng loc = item.getLatlngs().get(item.getLatlngs().size() - 1);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,
                        17));
                rectOptions.add(loc);
                rectOptions.color(Color.BLACK);
                mMap.addPolyline(rectOptions);
                rectOptions = new PolylineOptions().add(loc);
                if (endMarker != null) {
                    endMarker.remove();
                }
                endMarker = mMap.addMarker(new MarkerOptions().position(loc).
                        icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_RED)));
            }
        }
    };

    /*
     * When map is ready, start tracking service
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        startService(new Intent(this, TrackLocation.class));
        bindService(new Intent(this, TrackLocation.class), mConnection, Context.BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter(TrackLocation.ACTION_UPDATE);
        registerReceiver(onEvent, filter);
        startTime = Calendar.getInstance().getTimeInMillis();
    }

    /*
     * Set up map
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = mapFragment.getMap();
            if (mMap != null) {
                setUpMap();
                // Configure the map display options
            }
        }
        mapFragment.getMapAsync(this);
    }

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }


    /*
     * Callback function for tracking service
     * Add information from service to the google map
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service){
        binder = (TrackLocation.trackingBinder)service;
        item = binder.getItems();
        status.setText("Type: " + type);
        res = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                getString("Unit Preference", "Imperial (Miles)");
        if (res.equals("Imperial (Miles)")) {
            avgSpeed.setText("Avg speed: " + item.getAvgSpeed() + " m/h");
            curSpeed.setText("Cur Speed: " + item.getCurSpeed() + " m/h");
            climb.setText("Climb: " + item.getClimb() + " Miles");
            int cal = (int) (item.getDistance() * 99.456);
            calorie.setText("Calorie: " + cal);
            distance.setText("Distance: " + item.getDistance() + " Miles");
        }
        else {
            avgSpeed.setText("Avg speed: " + (item.getAvgSpeed()*1.61) + " km/h");
            curSpeed.setText("Cur Speed: " + (item.getCurSpeed()*1.61) + " km/h");
            climb.setText("Climb: " + (item.getClimb()*1.61) + " Kilometers");
            int cal = (int) (item.getDistance() * 99.456);
            calorie.setText("Calorie: " + cal);
            distance.setText("Distance: " + (item.getDistance()*1.61) + " Kilometers");
        }
        LatLng loc = item.getLatlngs().get(item.getLatlngs().size() - 1);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,
                17));
        rectOptions = new PolylineOptions().add(loc);
        startMarker = mMap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN)));
    }

    /*
     * When service disconnected, delete all information on google map
     */
    @Override
    public void onServiceDisconnected (ComponentName name) {
        status.setText("Type: " + type);
        //when the res == Imperial(Miles)
        res = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                getString("Unit Preference", "Imperial (Miles)");
        if (res.equals("Imperial (Miles)")) {
            avgSpeed.setText("Avg speed: 0 m/h");
            curSpeed.setText("Cur Speed: 0 m/h");
            climb.setText("Climb: 0 Miles");
            calorie.setText("Calorie: 0");
            distance.setText("Distance: 0 Miles");
        }
        else {
            avgSpeed.setText("Avg speed: 0 km/h");
            curSpeed.setText("Cur Speed: 0 km/h");
            climb.setText("Climb: 0 Kilometers");
            calorie.setText("Calorie: 0");
            distance.setText("Distance: 0 Kilometers");
        }
    }

    /*
     * When the activity is destroyed, disconnect with tracking service
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(mConnection);
        stopService(new Intent(this, TrackLocation.class));
        unregisterReceiver(onEvent);
    }

    /*
     * Save map to database
     */
    public void saveMap(View view) {
        item.setActivityType(type);
        item.setInputType(inputType);
        item.setID(System.currentTimeMillis()+"-"+item.getInputType()+"-"+item.getActivityType());
        item.setCalories((int)(item.getDistance() * 99.456));
        item.setDate(mDateAndTime.get(Calendar.YEAR) +"-"+ (mDateAndTime.get(Calendar.MONTH)+1) +"-"+
                mDateAndTime.get(Calendar.DAY_OF_MONTH));
        item.setTime(mDateAndTime.get(Calendar.HOUR_OF_DAY) +":"+ mDateAndTime.get(Calendar.MINUTE) +":"+
                (mDateAndTime.get(Calendar.SECOND) == 0 ? "00" : mDateAndTime.get(Calendar.SECOND)));
        long nowTime = Calendar.getInstance().getTimeInMillis();
        item.setDuration((nowTime - startTime) / (1000 * 60 * 1.0));
        new asyncTask(item).execute();
        finish();
    }

    public void cancelMap(View view) {
        finish();
    }

    /*
     * Async task for add item to database
     */
    class asyncTask extends AsyncTask<Void, Void, Integer> {
        private databaseItem item;

        public asyncTask(databaseItem item){
            this.item = item;
        }
        @Override
        protected Integer doInBackground(Void... params) {
            helper.addItem(item);
            return helper.allItems().size();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Integer result) {
            Toast.makeText(getApplicationContext(), "Entry #"+ result + " saved.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
