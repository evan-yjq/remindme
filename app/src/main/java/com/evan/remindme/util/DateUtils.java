package com.evan.remindme.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 上午1:09
 */
public class DateUtils {
    private SimpleDateFormat mFormat;

    public final static SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
    public final static SimpleDateFormat tasksFormat = new SimpleDateFormat("yy-MM-dd HH:mm");

    public DateUtils(SimpleDateFormat format){
        if (format == null){
            mFormat = defaultFormat;
        }else {
            mFormat = format;
        }
    }

    public DateUtils(){
        this(null);
    }

    public String Date2String(Date date){
        return mFormat.format(date);
    }

    public Date String2Date(String str) throws ParseException {
        return mFormat.parse(str);
    }
}
