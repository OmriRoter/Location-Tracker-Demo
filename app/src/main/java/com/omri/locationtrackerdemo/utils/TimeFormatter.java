package com.omri.locationtrackerdemo.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for formatting time values in a consistent format (HH:mm:ss).
 */
public class TimeFormatter {
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    /**
     * Formats a date into a time string with "Updated: " prefix
     * @param date The date to format
     * @return Formatted string in the format "Updated: HH:mm:ss"
     */
    public static String formatTime(Date date) {
        return "Updated: " + timeFormat.format(date);
    }
}