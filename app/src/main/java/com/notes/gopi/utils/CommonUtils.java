package com.notes.gopi.utils;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import rx.subscriptions.CompositeSubscription;

import static android.text.format.DateFormat.format;

/**
 * Created by gopikrishna on 11/11/16.
 */

public class CommonUtils {

    public static final long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;


    public static boolean isNullOrEmpty(@Nullable CharSequence str) {
        return (str == null || str.length() == 0 || str.toString().trim().length() == 0);
    }

    public static boolean isNullOrEmpty(@Nullable List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNullOrEmpty(String[] split) {
        return split == null || split.length == 0;
    }

    public static String getFormattedDate(Date date) {
        if (date == null) {
            return "";
        }
        return format("MMM dd", date).toString();
    }

    public static String getIntentValue(Intent intent, String key) {
        if (intent == null || intent.getExtras() == null) {
            return "";
        }
        Object object = intent.getExtras().get(key);
        if (object == null) {
            return "";
        }
        String result = String.valueOf(object);
        if (isNullOrEmpty(result)) {
            return "";
        }
        return result;
    }

    public static int getIntegerIntentValue(Intent intent, String key) {
        String strValue = getIntentValue(intent, key);
        try {
            return Integer.parseInt(strValue);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String getCurrentDateTimeForDb() {
        Calendar.getInstance().setTimeZone(TimeZone.getTimeZone("IST"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = Calendar.getInstance().getTime();
        return dateFormat.format(date);
    }

    public static Date getDateTimeFromString(String dateString) {
        Calendar.getInstance().setTimeZone(TimeZone.getTimeZone("IST"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getFormattedDateDifferenceFromNow(Date lastUpdatedDate) {
        String result = "";
        Date currentDate = Calendar.getInstance(TimeZone.getTimeZone("IST")).getTime();
        long diffInMillis = currentDate.getTime() - lastUpdatedDate.getTime();
        long days = diffInMillis / MILLISECS_PER_DAY;
        if (days == 0) {
            long hours = ((diffInMillis * 24) / MILLISECS_PER_DAY);
            if (hours == 0) {
                long min = ((diffInMillis * 24 * 60) / MILLISECS_PER_DAY);
                result = min + " min ago";
            } else {
                result = hours + " hours ago";
            }
        } else if (days > 0 && days < 30) {
            result = days + " days ago";
        } else {
            result = getFormattedDate(lastUpdatedDate);
        }
        return result;
    }

    public static void doUnsubscribe(CompositeSubscription lifeCycle) {
        if (lifeCycle == null || lifeCycle.isUnsubscribed()) {
            return;
        }
        lifeCycle.unsubscribe();
    }

    public static void dismissDialog(AlertDialog dialog) {
        if (dialog == null || !dialog.isShowing()) {
            return;
        }
        try {
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissDialog(DialogInterface dialog) {
        if (dialog == null) {
            return;
        }
        try {
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissDialog(ProgressDialog dialog) {
        if (dialog == null) {
            return;
        }
        try {
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
