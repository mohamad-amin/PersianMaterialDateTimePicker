package com.mohamadamin.persianmaterialdatetimepicker.date;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.lang.reflect.Field;

/**
 * Created by ImanX.
 * PersianMaterialDateTimePicker | Copyrights 2018 ZarinPal Crop.
 */

public class SimpleDatePickerDialog extends DatePickerDialog {

    private Activity activity;
    private Typeface typeface;


    public static SimpleDatePickerDialog newInstance(
            Activity activity,
            PersianCalendar persianCalendar) {

        return newInstance(
                activity,
                null,
                persianCalendar.getPersianYear(),
                persianCalendar.getPersianMonth(),
                persianCalendar.getPersianDay()
        );
    }

    public static SimpleDatePickerDialog newInstance(
            Activity activity,
            OnDateSetListener listener,
            PersianCalendar persianCalendar) {


        return newInstance(
                activity,
                listener,
                persianCalendar.getPersianYear(),
                persianCalendar.getPersianMonth(),
                persianCalendar.getPersianDay()
        );
    }

    public static SimpleDatePickerDialog newInstance(
            @NonNull Activity activity,
            OnDateSetListener listener,
            int year, int month, int day) {


        SimpleDatePickerDialog datePickerDialog = new SimpleDatePickerDialog();
        datePickerDialog.initialize(listener, year, month, day);
        datePickerDialog.activity = activity;
        return datePickerDialog;
    }

    @Override
    public void setTypeface(String fontName) {
        super.setTypeface(fontName);
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = super.onCreateView(inflater, container, savedInstanceState);


        if (typeface == null) {
            return view;
        }


        for (Field field : getClass().getSuperclass().getDeclaredFields()) {
            field.setAccessible(true);
            try {

                if (field.getType() == Button.class && field.get(this) != null) {
                    Button btn = (Button) field.get(this);
                    btn.setTypeface(typeface);
                    continue;
                }

                if (field.getType() == TextView.class && field.get(this) != null) {
                    TextView txt = (TextView) field.get(this);
                    txt.setTextSize(30);
                    txt.setTypeface(typeface);
                }

            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }

        }

        return view;
    }

    /**
     * fix min year to 1300;
     */
    @Override
    public int getMinYear() {
        try {
            Field field = getClass().getSuperclass().getDeclaredField("mMinYear");
            field.setAccessible(true);
            field.setInt(this, 1300);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return super.getMinYear();
    }


    public void show() {
        super.show(this.activity.getFragmentManager(), this.getClass().getSimpleName());
    }

}
