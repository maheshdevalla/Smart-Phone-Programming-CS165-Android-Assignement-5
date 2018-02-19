package edu.dartmouth.cs.actiontabs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class DisplayMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView status, avgSpeed, curSpeed, climb, calorie, distance;
    private String id;
    private DataBaseHelper helper;
    private PolylineOptions rectOptions;
    private String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);
        setUpMapIfNeeded();

        status = (TextView)findViewById(R.id.type_stats);
        avgSpeed = (TextView) findViewById(R.id.avg_speed);
        curSpeed = (TextView) findViewById(R.id.cur_speed);
        climb = (TextView) findViewById(R.id.climb);
        calorie = (TextView) findViewById(R.id.map_calorie);
        distance = (TextView) findViewById(R.id.map_distance);
        helper = new DataBaseHelper(getApplicationContext());
    }


    /**
     * Changing the map if available and manipulating it later
     * according to the permissions available.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String activityType = bundle.getString("ActivityType");
        id = bundle.getString("ID");
        status.setText("Type: " + activityType);
        double avgspeed = bundle.getDouble("AvgSpeed");
        double tclimb = bundle.getDouble("Climb");
        double dis = bundle.getDouble("Distance");
        int cal = bundle.getInt("Calories");
        res = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("Unit Preference", "Imperial (Miles)");
        if (res.equals("Imperial (Miles)")) {
            avgSpeed.setText("Avg speed: " + avgspeed + " m/h");
            curSpeed.setText("Cur Speed: n/a");
            climb.setText("Climb: " + tclimb + " Miles");
            calorie.setText("Calorie: " + cal);
            distance.setText("Distance: " + dis + " Miles");
        }
        else {
            avgSpeed.setText("Avg speed: " + (avgspeed*1.61) + " km/h");
            curSpeed.setText("Cur Speed: n/a");
            climb.setText("Climb: " + (tclimb*1.61) + " Kilometers");
            calorie.setText("Calorie: " + cal);
            distance.setText("Distance: " + (dis*1.61) + " Kilometers");
        }

        List<LatLng> list = bundle.getParcelableArrayList("List");
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(list.get(list.size() - 1),
                17));
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                rectOptions = new PolylineOptions().add(list.get(i));
            }
            else {
                rectOptions.add(list.get(i));
                rectOptions.color(Color.BLACK);
                mMap.addPolyline(rectOptions);
                rectOptions = new PolylineOptions().add(list.get(i));
            }
        }
        mMap.addMarker(new MarkerOptions().position(list.get(0)).icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN)));
        mMap.addMarker(new MarkerOptions().position(list.get(list.size()-1)).icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED)));
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = mapFragment.getMap();
            if (mMap != null) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
        mapFragment.getMapAsync(this);
    }

    /*
    * define the menu for deleting
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /*
     * called when the delete item is clicked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                myThread thread = new myThread(id);
                thread.start();
                finish();
                return true;
        }
        return false;
    }

    class myThread extends Thread {

        private String ID;
        public myThread(String ID) {
            this.ID = ID;
        }

        @Override
        public void run() {
            super.run();
            helper.deleteItem(ID);
        }
    }

}
