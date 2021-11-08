package com.semurtha.booked;

/*
 * Code base comes from https://stackoverflow.com/questions/16889502/how-to-mask-an-edittext-to-show-the-dd-mm-yyyy-date-format
 * and has been modified by Shaun Murtha to produce the desired date mask for the U.S.
 */


import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.Calendar;

public class DateInputMask implements TextWatcher {

    private String current = "";
    private String mmddyyyy = "MMDDYYYY";
    private Calendar cal = Calendar.getInstance();
    private EditText input;

    public DateInputMask(EditText input) {
        this.input = input;
        this.input.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().equals(current)) {
            return;
        }

        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
        String cleanC = current.replaceAll("[^\\d.]|\\.", "");

        int cl = clean.length();
        int sel = cl;
        for (int i = 2; i <= cl && i < 6; i += 2) {
            sel++;
        }
        //Fix for pressing delete next to a forward slash
        if (clean.equals(cleanC)) sel--;

        if (clean.length() < 8){
            clean = clean + mmddyyyy.substring(clean.length());
        }else{
            //This part makes sure that when we finish entering numbers
            //the date is correct, fixing it otherwise
            int mon  = Integer.parseInt(clean.substring(0,2));
            int day  = Integer.parseInt(clean.substring(2,4));
            int year = Integer.parseInt(clean.substring(4,8));

            mon = mon < 1 ? 1 : Math.min(mon, 12);
            cal.set(Calendar.MONTH, mon-1);
            year = (year<1900)?1900: Math.min(year, 2100);
            cal.set(Calendar.YEAR, year);
            // ^ first set year for the line below to work correctly
            //with leap years - otherwise, date e.g. 29/02/2012
            //would be automatically corrected to 28/02/2012

            day = Math.min(day, cal.getActualMaximum(Calendar.DATE));
            clean = String.format("%02d%02d%02d",mon, day, year);
        }

        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                clean.substring(2, 4),
                clean.substring(4, 8));

        sel = Math.max(sel, 0);
        current = clean;
        input.setText(current);
        input.setSelection(Math.min(sel, current.length()));

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}