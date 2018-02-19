package edu.dartmouth.cs.actiontabs;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class ManualEntry extends ListActivity{
    static final String[] content = new String[] { "Date", "Time", "Duration",
            "Distance", "Calories","Heart Rate", "Comment" };
    Calendar mDateAndTime = Calendar.getInstance();
    public databaseItem item;
    private DataBaseHelper helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = new databaseItem();


        setContentView(R.layout.manualentry_layout);

        // Define a new adapter
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, content);

        // Assign the adapter to ListView
        setListAdapter(mAdapter);

        // Define the listener interface
        AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String selected = ((TextView) view).getText().toString();
                switch(selected){
                    case "Date":
                        onDateClicked();
                        break;
                    case "Time":
                        onTimeClicked();
                        break;
                    default:
                        showDialog(selected);
                }
            }
        };
        //Database Helper
        helper = new DataBaseHelper(getApplicationContext());

        //Intent
        Intent intent = getIntent();
        item.setActivityType(intent.getStringExtra("ActivityType"));
        item.setInputType(intent.getStringExtra("InputType"));

        // Get the ListView and binds to the listener
        ListView listView = getListView();
        listView.setOnItemClickListener(mListener);
    }

    /*
     * called when the date is clicked
     */
    private void onDateClicked() {
        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                mDateAndTime.set(Calendar.YEAR, year);
                mDateAndTime.set(Calendar.MONTH, monthOfYear);
                mDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        };

        new DatePickerDialog(ManualEntry.this, mDateListener,
                mDateAndTime.get(Calendar.YEAR),
                mDateAndTime.get(Calendar.MONTH),
                mDateAndTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    /*
     * called when the time is clicked
     */
    private void onTimeClicked() {

        TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mDateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mDateAndTime.set(Calendar.MINUTE, minute);
                mDateAndTime.set(Calendar.SECOND,0);
            }
        };

        new TimePickerDialog(ManualEntry.this, mTimeListener,
                mDateAndTime.get(Calendar.HOUR_OF_DAY),
                mDateAndTime.get(Calendar.MINUTE), true).show();
    }

    /*
     * showing the dialog
     */
    private void showDialog(String title){
        HistoryDialogFragment mydialog = new HistoryDialogFragment();
        DialogFragment newFragment = mydialog
                .newInstance(title,item);
        newFragment.show(getFragmentManager(), "dialog");
    }

    /*
     * called when the save button is clicked
     */
    public void onEntrySaveClicked(View v) {
        item.setDate(mDateAndTime.get(Calendar.YEAR) +"-"+ (mDateAndTime.get(Calendar.MONTH)+1) +"-"+
                mDateAndTime.get(Calendar.DAY_OF_MONTH));
        item.setTime(mDateAndTime.get(Calendar.HOUR_OF_DAY) +":"+ mDateAndTime.get(Calendar.MINUTE) +":"+
                (mDateAndTime.get(Calendar.SECOND) == 0 ? "00" : mDateAndTime.get(Calendar.SECOND)));
        item.setID(System.currentTimeMillis()+"-"+item.getInputType()+"-"+item.getActivityType());
        new asyncTask(item).execute();
        Toast.makeText(getApplicationContext(), "Entry saved.",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    /*
     * called when the cancel button is clicked
     */
    public void onEntryCancelClicked(View v) {
        Toast.makeText(getApplicationContext(), "Entry discarded.",
                Toast.LENGTH_SHORT).show();

        finish();
    }

    /*
     * create an asynctask to handle writing to the database.
     */
    class asyncTask extends AsyncTask<Void, Void, Void> {
        private databaseItem item;

        public asyncTask(databaseItem item){
            this.item = item;
        }
        @Override
        protected Void doInBackground(Void... params) {
            helper.addItem(item);
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }
}
