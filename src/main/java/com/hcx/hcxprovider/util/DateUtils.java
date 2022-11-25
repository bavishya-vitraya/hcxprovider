package com.hcx.hcxprovider.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String parseDateInFormat(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-YYYY");
            Date formattedDate= sdf.parse(sdf.format(date));
            return String.valueOf(formattedDate);
        } catch (ParseException e) {
            return null;
        }
    }
}
