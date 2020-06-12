package com.IceCreamQAQ.Yu.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private ThreadLocal<SimpleDateFormat> sdfDate;
    private ThreadLocal<SimpleDateFormat> sdfDateTime;
    private ThreadLocal<SimpleDateFormat> sdfDateTimeSSS;

    public DateUtil() {
        sdfDate = new ThreadLocal<>();
        sdfDateTime = new ThreadLocal<>();
        sdfDateTimeSSS = new ThreadLocal<>();
    }

    public SimpleDateFormat getSDF(int type) {
        SimpleDateFormat sdf = null;
        switch (type) {
            case 0:
                sdf = sdfDate.get();
                if (sdf == null) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdfDate.set(sdf);
                }
                break;
            case 1:
                sdf = sdfDateTime.get();
                if (sdf == null) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sdfDateTime.set(sdf);
                }
                break;
            case 2:
                sdf = sdfDateTimeSSS.get();
                if (sdf == null) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    sdfDateTimeSSS.set(sdf);
                }
                break;
        }
        if (sdf != null) sdf.setTimeZone(TimeZone.getTimeZone("Etc/GMT-8"));
        return sdf;
    }

    /***
     * 获取当前日期的格式化后内容（yyyy-MM-dd）
     * @return 时间
     */
    public String formatDate() {
        return formatDate(new Date());
    }

    /***
     * 获取指定日期的格式化后内容（yyyy-MM-dd）
     * @param date 指定的日期
     * @return 时间字符串
     */
    public String formatDate(Date date) {
        return getSDF(0).format(date);
    }

    public Date parseDate(String dateStr) throws ParseException {
        return getSDF(0).parse(dateStr);
    }

    /***
     * 获取当前日期和时间的格式化后内容（yyyy-MM-dd HH:mm:ss）
     * @return 时间
     */
    public String formatDateTime() {
        return formatDateTime(new Date());
    }

    /***
     * 获取指定日期和时间的格式化后内容（yyyy-MM-dd HH:mm:ss）
     * @param date 指定的日期和时间
     * @return 时间字符串
     */
    public String formatDateTime(Date date) {
        return getSDF(1).format(date);
    }

    public Date parseDateTime(String dateTimeStr) throws ParseException {
        return getSDF(1).parse(dateTimeStr);
    }

    /***
     * 获取当前日期和时间（精确到毫秒）的格式化后内容（yyyy-MM-dd HH:mm:ss.SSS）
     * @return 时间
     */
    public String formatDateTimeSSS() {
        return formatDateTimeSSS(new Date());
    }

    /***
     * 获取指定日期和时间（精确到毫秒）的格式化后内容（yyyy-MM-dd HH:mm:ss.SSS）
     * @param date 指定的日期和时间
     * @return 时间字符串
     */
    public String formatDateTimeSSS(Date date) {
        return getSDF(2).format(date);
    }
}
