package edu.dartmouth.cs.actiontabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class InfoActivity extends Activity{

    private EditText eType, eDate, eDuration, eDistance, eCalories, eHeartRate;
    private String id;
    private DataBaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //get the values from the selected item in the history fragment
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("ID");
        String activityType = bundle.getString("ActivityType");
        String dateTime = bundle.getString("DateTime");
        Double duration = bundle.getDouble("Duration");
        Double distance = bundle.getDouble("Distance");
        int calories = bundle.getInt("Calories");
        int heartrate = bundle.getInt("HeartRate");

        eType = (EditText) findViewById(R.id.info_type);
        eType.setText(activityType);

        eDate = (EditText) findViewById(R.id.info_datetime);
        eDate.setText(dateTime);

        eDuration = (EditText) findViewById(R.id.info_duration);
        int minute = duration.intValue();
        int second = (int)((duration - minute) * 60);
        if (minute != 0)
            eDuration.setText(minute + "mins " + second + "secs");
        else
            eDuration.setText(second + "secs");

        String res = bundle.getString("method");
        eDistance = (EditText) findViewById(R.id.info_distance);
        int dis = distance.intValue();
        int kdis = (int)(distance * 1.61);
        if (res.equals("Imperial (Miles)")) {
            if (dis == distance)
                eDistance.setText(dis + " Miles");
            else
                eDistance.setText(distance + " Miles");
        }
        else {
            if (kdis == distance * 1.61)
                eDistance.setText(kdis + " Kilometers");
            else
                eDistance.setText((distance * 1.61) + " Kilometers");
        }

        eCalories = (EditText) findViewById(R.id.info_calories);
        eCalories.setText(calories + " cals");

        eHeartRate = (EditText) findViewById(R.id.info_heartrate);
        eHeartRate.setText(heartrate + " bpm");

        helper = new DataBaseHelper(getApplicationContext());
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

    //a thread used to delete the selected item
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
