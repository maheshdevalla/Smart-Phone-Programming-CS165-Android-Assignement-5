package edu.dartmouth.cs.actiontabs;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainFragment extends Fragment {
    @Override

    /** called when the fragment is created */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.start, container, false);
        Button start = (Button) v.findViewById(R.id.start_start);
        Button sygn = (Button) v.findViewById(R.id.start_sync);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickStart(v);
            }
        });
        sygn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickSync(v);
            }
        });
        return v;
    }

    /** called when the Save button is clicked */
    public void onClickStart(View v) {
        Spinner spinnerInput = (Spinner)getActivity().findViewById(R.id.spinnerInputType);
        Spinner spinnerActivity = (Spinner)getActivity().findViewById(R.id.spinnerActivityType);
        String InputType = spinnerInput.getSelectedItem().toString();
        String ActivityType = spinnerActivity.getSelectedItem().toString();
        Intent mIntent;
        switch(InputType) {
            case "Manual Entry":
                mIntent = new Intent(getActivity(),
                        ManualEntry.class);
                mIntent.putExtra("InputType", "ManualEntry");
                mIntent.putExtra("ActivityType", ActivityType);
                startActivity(mIntent);
                break;
            case "GPS":
                mIntent = new Intent(getActivity(),
                        MapsActivity.class);
                mIntent.putExtra("InputType", "GPS");
                mIntent.putExtra("ActivityType", ActivityType);
                startActivity(mIntent);
                break;
            case "Automatic":
                mIntent = new Intent(getActivity(),
                        MapsActivity.class);
                mIntent.putExtra("InputType", "Automatic");
                mIntent.putExtra("ActivityType", "Unknown");
                startActivity(mIntent);
                break;
        }
    }

    /** called when the Sync button is clicked */
    public void onClickSync(View v) {
        Toast.makeText(getActivity(), "Sync", Toast.LENGTH_SHORT).show();
    }
}
