package edu.dartmouth.cs.actiontabs;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HistoryTab extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<databaseItem>>{

    private ListView listview;
    private List<databaseItem> list;
    private MyAdapter adapter;
    private String res;

    /*
     * called when the activity is created
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        getLoaderManager().initLoader(0, null, this);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.history_layout, container, false);
        listview = (ListView) view.findViewById(R.id.datalist);

        //set the adapter for the list view
        adapter = new MyAdapter(getActivity(), list);
        listview.setAdapter(adapter);

        //set the click listener for the list view
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (list.get(position).getInputType().equals("ManualEntry")) {
                    Intent intent = new Intent(getActivity(), InfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("ID", list.get(position).getID());
                    bundle.putString("ActivityType", list.get(position).getActivityType());
                    bundle.putString("DateTime", list.get(position).getTime() + " " + list.get(position).getDate());
                    bundle.putDouble("Duration", list.get(position).getDuration());
                    bundle.putDouble("Distance", list.get(position).getDistance());
                    bundle.putInt("Calories", list.get(position).getCalories());
                    bundle.putInt("HeartRate", list.get(position).getHeartRate());
                    bundle.putString("method", res);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if (list.get(position).getInputType().equals("GPS")) {
                    Intent intent = new Intent(getActivity(), DisplayMapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("ID", list.get(position).getID());
                    bundle.putString("ActivityType", list.get(position).getActivityType());
                    bundle.putDouble("AvgSpeed", list.get(position).getAvgSpeed());
                    bundle.putDouble("Climb", list.get(position).getClimb());
                    bundle.putDouble("Distance", list.get(position).getDistance());
                    bundle.putInt("Calories", list.get(position).getCalories());
                    bundle.putParcelableArrayList("List", (ArrayList<? extends Parcelable>) list.get(position).getLatlngs());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if (list.get(position).getInputType().equals("Automatic")) {
                    Intent intent = new Intent(getActivity(), DisplayMapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("ID", list.get(position).getID());
                    bundle.putString("ActivityType", list.get(position).getActivityType());
                    bundle.putDouble("AvgSpeed", list.get(position).getAvgSpeed());
                    bundle.putDouble("Climb", list.get(position).getClimb());
                    bundle.putDouble("Distance", list.get(position).getDistance());
                    bundle.putInt("Calories", list.get(position).getCalories());
                    bundle.putParcelableArrayList("List", (ArrayList<? extends Parcelable>) list.get(position).getLatlngs());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    /*
     * define the adapter for the list view
     */
    class MyAdapter extends BaseAdapter {
        Context context;
        List<databaseItem> list;
        private LayoutInflater layoutInflater;
        public MyAdapter(Context context, List<databaseItem> list) {
            this.context = context;
            this.list = list;
            layoutInflater = LayoutInflater.from(this.context);
        }
        /*
         * return the number of the items in the list
         */
        @Override
        public int getCount() {
            return list.size();
        }

        /*
         * return the item object;
         */
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        /*
         * return the id of the selected item
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /*
         * return the item in the list view
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_historylayout, null);
            }
            res = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Unit Preference", "Imperial (Miles)");

            TextView textview1 = (TextView) convertView.findViewById(R.id.method_time);
            TextView textview2 = (TextView) convertView.findViewById(R.id.mile_time);

            textview1.setText(list.get(position).getInputType() + ": " + list.get(position).getActivityType() + "," + list.get(position).getTime() + " " + list.get(position).getDate());
            int minute = (int)list.get(position).getDuration();
            int second = (int)((list.get(position).getDuration() - minute) * 60);
            int dis = (int)list.get(position).getDistance();
            int kdis = (int)(list.get(position).getDistance() * 1.61);

            //when the res == Imperial(Miles)
            if (res.equals("Imperial (Miles)")) {
                if (minute != 0) {
                    if (dis == list.get(position).getDistance())
                        textview2.setText(dis + " Miles, " + minute + "mins " + second + "secs");
                    else
                        textview2.setText(list.get(position).getDistance() + " Miles, " + minute + "mins " + second + "secs");
                }
                else {
                    if (dis == list.get(position).getDistance())
                        textview2.setText(dis + " Miles, " + second + "secs");
                    else
                        textview2.setText(list.get(position).getDistance() + " Miles, " + second + "secs");
                }
            }
            //when the res == Metric (Kilometers)
            else {
                if (minute != 0) {
                    if (kdis == (list.get(position).getDistance() * 1.61))
                        textview2.setText(kdis + " Kilometers, " + minute + "mins " + second + "secs");
                    else
                        textview2.setText((list.get(position).getDistance() * 1.61) + " Kilometers, " + minute + "mins " + second + "secs");
                }
                else {
                    if (kdis == (list.get(position).getDistance() * 1.61))
                        textview2.setText(kdis + " Kilometers, " + second + "secs");
                    else
                        textview2.setText((list.get(position).getDistance() * 1.61) + " Kilometers, " + second + "secs");
                }
            }
            return convertView;
        }
    }

    /*
     * called when the loader is created
     */
    @Override
    public Loader<ArrayList<databaseItem>> onCreateLoader(int i, Bundle bundle) {
        return new DataLoader(getActivity()); // DataLoader is your AsyncTaskLoader.
    }

    /*
     * called after the loadtask has finished
     */
    @Override
    public void onLoadFinished(Loader<ArrayList<databaseItem>> loader, ArrayList<databaseItem> items) {
        //Put your code here.
        list.clear();
        for(databaseItem item : items) {
            list.add(item);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<databaseItem>> loader) {
    }

    /*
     * reload the data
     */
    public void reLoadData() {
        if (isAdded()) {
            getLoaderManager().restartLoader(0, null, this).forceLoad();
        }
    }

    /*
     * define the asynctaskloader for reading the database
     */
    public static class DataLoader extends AsyncTaskLoader<ArrayList<databaseItem>>{
        private DataBaseHelper helper = new DataBaseHelper(getContext());

        public DataLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad(); //Force an asynchronous load.
        }
        @Override
        public ArrayList<databaseItem> loadInBackground() {
            return (ArrayList<databaseItem>)helper.allItems();
        }
    }
}
