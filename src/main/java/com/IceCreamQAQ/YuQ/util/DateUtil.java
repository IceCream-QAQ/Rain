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

    public String formatDate(){
        return formatDate(new Date());
    }
    public String formatDate(Date date){
        var sdf=sdfDate.get();
        if (sdf==null){
            sdf=new SimpleDateFormat("yyyy-MM-dd");
            sdfDate.set(sdf);
        }
        return sdf.format(date);
    }

    public String formatDateTime(){
        return formatDateTime(new Date());
    }
    public String formatDateTime(Date date){
        var sdf=sdfDate.get();
        if (sdf==null){
            sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdfDate.set(sdf);
        }
        return sdf.format(date);
    }

    public String formatDateTimeSSS(){
        return formatDateTimeSSS(new Date());
    }
    public String formatDateTimeSSS(Date date){
        var sdf=sdfDate.get();
        if (sdf==null){
            sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            sdfDate.set(sdf);
        }
        return sdf.format(date);
    }
}
