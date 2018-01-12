/*
 * ExtraPreferences - net.zahi.extrapreferences
 *
 * Collection of extra preference controls extending different DialogPreferences.
 *
 * Sourced from stackoverflow and extended, tweaked for current android version (Oreo) as needed.
 *
 *
 * TimePreference is a wrapper for TimePicker dialog control.
 * Code modified to include seconds, from examples provided at:
 * From: https://stackoverflow.com/questions/5533078/timepicker-in-preferencescreen
 *
 * This version specifically uses a TimePicker Dialog which exposes Seconds as an option,
 * it uses the library com.ikovac.timepickerwithseconds.TimePicker
 *
 * Original code by CommonsWare, Matt Koala, et al.
 *
 *
 * Packaged / Modified by David Meggyesy, 2017.
 * Email:  david.meggyesy@unimelb.edu.au
 * Email: davidm@zahi.net
 *
 */
package net.zahi.extrapreferences;


import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import com.ikovac.timepickerwithseconds.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@SuppressWarnings("ALL")
public class TimePreference extends DialogPreference {
    private int lastHour = 0;
    private int lastMinute = 0;
    private int lastSecond = 0;

    private TimePicker picker = null;

    public static int getHour(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[1]));
    }

    public static int getSecond(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[2]));
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(true);

        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        // picker.set
        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
        picker.setCurrentSecond(lastSecond);

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastHour = picker.getCurrentHour();
            lastMinute = picker.getCurrentMinute();
            lastSecond = picker.getCurrentSeconds();

            setSummary(getSummary());

            String lastMinuteString = String.valueOf(lastMinute);
            String time = String.valueOf(lastHour) + ":" + (lastMinuteString.length() == 1 ? "0" + lastMinuteString : lastMinuteString + ":" + String.valueOf(lastSecond));

            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

        String time;
        String defaultValueStr = (defaultValue != null) ? defaultValue.toString() : "00:00:00";
        if (restoreValue)
            time = getPersistedString(defaultValueStr);
        else {
            time = defaultValueStr;
            if (shouldPersist())
                persistString(defaultValueStr);
        }

        lastHour = getHour(time);
        lastMinute = getMinute(time);
        lastSecond = getSecond(time);

        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, lastHour);
        cal.set(Calendar.MINUTE, lastMinute);
        cal.set(Calendar.SECOND, lastSecond);

        DateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        return sdf.format(cal.getTime());
    }

}