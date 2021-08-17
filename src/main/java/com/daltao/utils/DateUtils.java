package com.daltao.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    static ThreadLocal<DateFormat> yyyyMMddHHmmssFormats = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmss"));

    public static String yyyyMMddHHmmss(Date date) {
        return yyyyMMddHHmmssFormats.get().format(date);
    }
}
