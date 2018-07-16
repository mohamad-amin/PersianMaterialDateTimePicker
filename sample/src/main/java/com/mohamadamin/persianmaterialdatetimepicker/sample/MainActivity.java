package com.mohamadamin.persianmaterialdatetimepicker.sample;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.date.SimpleDatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.multidate.MultiDatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener,
        MultiDatePickerDialog.OnDateSetListener,
        View.OnClickListener {

    private static final String TIMEPICKER = "TimePickerDialog",
            DATEPICKER                     = "DatePickerDialog", MULTIDATEPICKER = "MultiDatePickerDialog";

    private CheckBox mode24Hours, modeDarkTime, modeDarkDate;
    private TextView timeTextView, dateTextView, multiDateTextView;
    private Button timeButton, dateButton, multiDataButton, simplePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        handleClicks();
    }

    private void initializeViews() {
        timeTextView = findViewById(R.id.time_textview);
        dateTextView = findViewById(R.id.date_textview);
        multiDateTextView = findViewById(R.id.multi_date_textview);
        timeButton = findViewById(R.id.time_button);
        dateButton = findViewById(R.id.date_button);
        multiDataButton = findViewById(R.id.multi_date_button);
        mode24Hours = findViewById(R.id.mode_24_hours);
        modeDarkTime = findViewById(R.id.mode_dark_time);
        modeDarkDate = findViewById(R.id.mode_dark_date);
        simplePicker = findViewById(R.id.btn_simple_picker);
    }

    private void handleClicks() {
        timeButton.setOnClickListener(this);
        dateButton.setOnClickListener(this);
        multiDataButton.setOnClickListener(this);
        simplePicker.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String fontName = "DroidNaskh-Regular";
        switch (view.getId()) {
            case R.id.time_button: {
                PersianCalendar now = new PersianCalendar();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        MainActivity.this,
                        now.get(PersianCalendar.HOUR_OF_DAY),
                        now.get(PersianCalendar.MINUTE),
                        mode24Hours.isChecked()
                );
                tpd.setThemeDark(modeDarkTime.isChecked());
                tpd.setTypeface(fontName);
                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d(TIMEPICKER, "Dialog was cancelled");
                    }
                });
                tpd.show(getFragmentManager(), TIMEPICKER);
                break;
            }
            case R.id.date_button: {
                PersianCalendar now = new PersianCalendar();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MainActivity.this,
                        now.getPersianYear(),
                        now.getPersianMonth(),
                        now.getPersianDay()
                );
                dpd.setThemeDark(modeDarkDate.isChecked());
                dpd.setTypeface(fontName);
                dpd.show(getFragmentManager(), DATEPICKER);
                break;
            }
            case R.id.multi_date_button:
                MultiDatePickerDialog mdpd = MultiDatePickerDialog.newInstance(MainActivity.this, null);
                PersianCalendar[] pc = new PersianCalendar[30];
                for (int i = 0; i < pc.length; i++) {
                    pc[i] = new PersianCalendar();
                    pc[i].add(Calendar.DAY_OF_YEAR, i);
                }
                mdpd.setMinDate(pc[0]);
                mdpd.setMaxDate(pc[29]);
                //mdpd.setSelectableDays(pc);
                mdpd.setThemeDark(modeDarkDate.isChecked());
                mdpd.setTypeface(fontName);
                mdpd.show(getFragmentManager(), MULTIDATEPICKER);
                break;

            case R.id.btn_simple_picker:
                SimpleDatePickerDialog simpleDatePickerDialog = SimpleDatePickerDialog.newInstance(this, new PersianCalendar());
                simpleDatePickerDialog.setTypeface(Typeface.createFromAsset(getAssets(), "s.ttf"));
                simpleDatePickerDialog.setOnDateSetListener(this);
                simpleDatePickerDialog.show();
                break;

            default:
                break;
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String hourString   = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String time         = "You picked the following time: " + hourString + ":" + minuteString;
        timeTextView.setText(time);
    }

    @Override
    public void onDateSet(MultiDatePickerDialog view, ArrayList<PersianCalendar> selectedDays) {
        StringBuilder date = new StringBuilder("You picked the following dates:\n\t");
        for (PersianCalendar calendar : selectedDays) {
            date.append(calendar.getPersianDay()).append("/").append(calendar.getPersianMonth() + 1).append("/").append(calendar.getPersianYear()).append("\n\t");
        }
        multiDateTextView.setText(date.toString());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        // Note: monthOfYear is 0-indexed
        String date = "You picked the following date: " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        dateTextView.setText(date);
    }
}
