package com.mohamadamin.persianmaterialdatetimepicker.sample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

public class MainActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener,
        View.OnClickListener {

    private static final String TIMEPICKER = "TimePickerDialog",
            DATEPICKER = "DatePickerDialog";

    private CheckBox mode24Hours, modeDarkTime, modeDarkDate;
    private TextView timeTextView, dateTextView;
    private Button timeButton, dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        handleClicks();
    }

    private void initializeViews() {
        timeTextView = (TextView)findViewById(R.id.time_textview);
        dateTextView = (TextView)findViewById(R.id.date_textview);
        timeButton = (Button)findViewById(R.id.time_button);
        dateButton = (Button)findViewById(R.id.date_button);
        mode24Hours = (CheckBox)findViewById(R.id.mode_24_hours);
        modeDarkTime = (CheckBox)findViewById(R.id.mode_dark_time);
        modeDarkDate = (CheckBox)findViewById(R.id.mode_dark_date);
    }

    private void handleClicks() {
        timeButton.setOnClickListener(this);
        dateButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_button : {
                PersianCalendar now = new PersianCalendar();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        MainActivity.this,
                        now.get(PersianCalendar.HOUR_OF_DAY),
                        now.get(PersianCalendar.MINUTE),
                        mode24Hours.isChecked()
                );
                tpd.setThemeDark(modeDarkTime.isChecked());
                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d(TIMEPICKER, "Dialog was cancelled");
                    }
                });
                tpd.show(getFragmentManager(), TIMEPICKER);
                break;
            }
            case R.id.date_button : {
                PersianCalendar now = new PersianCalendar();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MainActivity.this,
                        now.getPersianYear(),
                        now.getPersianMonth(),
                        now.getPersianDay()
                );
                dpd.setThemeDark(modeDarkDate.isChecked());
                dpd.show(getFragmentManager(), DATEPICKER);
                break;
            }
            default: break;
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String time = "You picked the following time: "+hourString+":"+minuteString;
        timeTextView.setText(time);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        // Note: monthOfYear is 0-indexed
        String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        dateTextView.setText(date);
    }

}
