package com.IceCreamQAQ.YuQ.util;

import lombok.var;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private ThreadLocal<SimpleDateFormat> sdfDate;
    private ThreadLocal<SimpleDateFormat> sdfDateTime;
    private ThreadLocal<SimpleDateFormat> sdfDateTimeSSS;

    public DateUtil(){
        sdfDate=new ThreadLocal<>();
        sdfDateTime=new ThreadLocal<>();
        sdfDateTimeSSS=new ThreadLocal<>();
    }

    /***
     * 获取当前日期的格式化后内容（yyyy-MM-dd）
     * @return 时间
     */
    public String formatDate(){
        return formatDate(new Date());
    }
    /***
     * 获取指定日期的格式化后内容（yyyy-MM-dd）
     * @param date 指定的日期
     * @return 时间字符串
     */
    public String formatDate(Date date){
        var sdf=sdfDate.get();
        if (sdf==null){
            sdf=new SimpleDateFormat("yyyy-MM-dd");
            sdfDate.set(sdf);
        }
        return sdf.format(date);
    }

    /***
     * 获取当前日期和时间的格式化后内容（yyyy-MM-dd HH:mm:ss）
     * @return 时间
     */
    public String formatDateTime(){
        return formatDateTime(new Date());
    }

    /***
     * 获取指定日期和时间的格式化后内容（yyyy-MM-dd HH:mm:ss）
     * @param date 指定的日期和时间
     * @return 时间字符串
     */
    public String formatDateTime(Date date){
        var sdf=sdfDateTime.get();
        if (sdf==null){
            sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdfDateTime.set(sdf);
        }
        return sdf.format(date);
    }

    /***
     * 获取当前日期和时间（精确到毫秒）的格式化后内容（yyyy-MM-dd HH:mm:ss.SSS）
     * @return 时间
     */
    public String formatDateTimeSSS(){
        return formatDateTimeSSS(new Date());
    }

    /***
     * 获取指定日期和时间（精确到毫秒）的格式化后内容（yyyy-MM-dd HH:mm:ss.SSS）
     * @param date 指定的日期和时间
     * @return 时间字符串
     */
    public String formatDateTimeSSS(Date date){
        var sdf=sdfDateTimeSSS.get();
        if (sdf==null){
            sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            sdfDateTimeSSS.set(sdf);
        }
        return sdf.format(date);
    }
}
