package com.hcx.hcxprovider.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String formatDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
        return dateFormat.format(date);
    }
}
