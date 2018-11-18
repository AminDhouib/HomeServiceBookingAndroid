package com.handy.agile.agile_app;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditAvailabilityDialog extends DialogFragment implements View.OnClickListener {

    private Activity activity;
    private Button saveBtn;
    private Button cancelBtn;
    private TextView dayTextView;
    private Spinner stateSpinner;
    private TextView fromTextView;
    private TextView startTimeTextView;
    private TextView toTextView;
    private TextView endTimeTextView;


    private DatabaseReference database;

    private DayEntry dayEntry;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        View v = layoutInflater.inflate(R.layout.editavailability_dialog,viewGroup, false);

        database = FirebaseDatabase.getInstance().getReference();
        cancelBtn = v.findViewById(R.id.cancelBtn);
        saveBtn = v.findViewById(R.id.saveBtn);
        dayTextView = v.findViewById(R.id.dayTextView);
        stateSpinner = v.findViewById(R.id.stateSpinner);
        fromTextView = v.findViewById(R.id.fromTextView);
        startTimeTextView = v.findViewById(R.id.startTimeTextView);
        toTextView = v.findViewById(R.id.toTextView);
        endTimeTextView = v.findViewById(R.id.endTimeTextView);

        //Retrieve arguments passed from activity, that is the user
        Bundle args = getArguments();
        dayEntry = (DayEntry)args.getSerializable("AvailabilityInfo");
        user = (User)args.getSerializable("UserInfo");

        dayTextView.setText("Edit Availability On "+dayEntry.getDay());


        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //If the user selects Closed hide the textviews and edit texts of the time
                String selectedItem = stateSpinner.getSelectedItem().toString();
                if (selectedItem.equals("Closed")) {
                    //Set the time strings to null
                    startTimeTextView.setText("");
                    endTimeTextView.setText("");
                    fromTextView.setVisibility(View.INVISIBLE);
                    startTimeTextView.setVisibility(View.INVISIBLE);
                    toTextView.setVisibility(View.INVISIBLE);
                    startTimeTextView.setVisibility(View.INVISIBLE);
                } else {
                    startTimeTextView.setText("Click to enter start time");
                    endTimeTextView.setText("Click to enter end time");
                    fromTextView.setVisibility(View.VISIBLE);
                    startTimeTextView.setVisibility(View.VISIBLE);
                    toTextView.setVisibility(View.VISIBLE);
                    endTimeTextView.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Set edit texts to open the time pickers and display them
        startTimeTextView.setOnClickListener(this);
        endTimeTextView.setOnClickListener(this);
        //set save to update DB
        saveBtn.setOnClickListener(this);
        //set cancel to dismiss
        cancelBtn.setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            //cancel clicked, close dialog
            case R.id.cancelBtn:
                dismiss();
                break;

            case R.id.startTimeTextView:
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hourValue = Integer.toString(hourOfDay);
                        String minuteValue = Integer.toString(minute);
                        String amPM;
                        if (hourOfDay >= 12) {
                            amPM = "PM";
                        } else {
                            if(hourOfDay < 10) {
                                hourValue = "0"+hourOfDay;
                            }
                            amPM = "AM";
                        }
                        if (minute < 10) {
                            minuteValue = "0"+minute;
                        }
                        startTimeTextView.setText(hourValue+":"+minuteValue+amPM);

                    }
                },0,0,false);
                timePickerDialog.show();
                break;

            case R.id.endTimeTextView:
                endTimeTextView.setError(null);
                TimePickerDialog timePickerDialog1 = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hourValue = Integer.toString(hourOfDay);
                        String minuteValue = Integer.toString(minute);
                        String amPM;
                        if (hourOfDay >= 12) {
                            amPM = "PM";
                        } else {
                            if(hourOfDay < 10) {
                                hourValue = "0"+hourOfDay;
                            }
                            amPM = "AM";
                        }
                        if (minute < 10) {
                            minuteValue = "0"+minute;
                        }
                        endTimeTextView.setText(hourValue+":"+minuteValue+amPM);
                    }
                },0,0,false);
                timePickerDialog1.show();
                break;

            case R.id.saveBtn:
                //Call verify info for the dates
                verifyTime(startTimeTextView.getText().toString(),endTimeTextView.getText().toString());

                //Add to the DB
        }

    }

    public boolean verifyTime(String startTime, String endTime) {
        int startHour,startMinutes,endHour, endMinutes;
        startHour = Integer.parseInt(startTime.substring(0,2));
        startMinutes = Integer.parseInt(startTime.substring(3,5));

        endHour = Integer.parseInt(endTime.substring(0,2));
        endMinutes = Integer.parseInt(endTime.substring(3,5));

        if (endHour < startHour || (endMinutes < startMinutes && endHour == startHour) || (endMinutes == startMinutes && endHour == startHour)) {
            endTimeTextView.setError("Invalid time range");
            endTimeTextView.requestFocus();
            return false;
        }

        return true;
    }
}
