package com.yf.dialtesting.util.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);
    public static final String DATE_FORMAT1 = "yyyy-MM-dd";
    public static final String DATE_FORMAT2 = "yyyy.MM.dd";
    public static final String DATE_FORMAT3 = "yyyy/MM/dd";
    public static final String DATE_FORMAT4 = "yyyy_MM_dd";
    public static final String DATE_FORMAT5 = "yyyyMMdd";
    public static final String DATETIME_FORMAT1 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_FORMAT2 = "yyyy-MM-dd HH:mm:ss.S";
    public static final String DATETIME_FORMAT3 = "yyyy-MM-dd hh:mm:ss a";
    public static final String DATETIME_FORMAT4 = "yyyy-MM-dd hh:mm:ss.S a";
    public static final String DATETIME_FORMAT5 = "yyyy_MM_dd_hh_mm_ss";
    public static final String DATETIME_FORMAT6 = "yyyyMMddhhmmss";

    public DateUtils() {
    }

    public static String date2String(Date date) {
        return date2String(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String date2String(Date date, String dateFormat) {
        if (date != null && !StringUtils.isNullOrEmpty(dateFormat)) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                return format.format(date);
            } catch (Exception var3) {
                logger.error("date2String error.", var3);
                return "";
            }
        } else {
            logger.warn("parameter date or dateFormat is null.");
            return "";
        }
    }

    public static Date str2Date(String dateStr, String dateFormat) {
        if (!StringUtils.isNullOrEmpty(dateStr) && !StringUtils.isNullOrEmpty(dateFormat)) {
            Date date = null;

            try {
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                date = format.parse(dateStr);
            } catch (ParseException var4) {
                logger.error("str parse to date error.", var4);
            }

            return date;
        } else {
            logger.warn("parameter dateStr or dateFormat is null.");
            return null;
        }
    }

    public static java.sql.Date str2SQLDate(String dateStr, String dateFormat) {
        if (!StringUtils.isNullOrEmpty(dateStr) && !StringUtils.isNullOrEmpty(dateFormat)) {
            java.sql.Date sqlDate = null;

            try {
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                Date utilDate = format.parse(dateStr);
                sqlDate = new java.sql.Date(utilDate.getTime());
            } catch (ParseException var5) {
                logger.error("str parse to date error.", var5);
            }

            return sqlDate;
        } else {
            logger.warn("parameter dateStr or dateFormat is null.");
            return null;
        }
    }

    public static Date getNowTime() {
        long now = System.currentTimeMillis();
        return new Date(now);
    }

    public static String getNowTimeStr(String dateFormat) {
        if (StringUtils.isNullOrEmpty(dateFormat)) {
            logger.warn("parameter dateFormat is null.");
            return null;
        } else {
            Date now = getNowTime();
            return date2String(now, dateFormat);
        }
    }

    public static String getNowTimeMillSec() {
        long now = System.currentTimeMillis();
        return now + "";
    }

    public static void main(String[] args) {
        String now = getNowTimeMillSec();
        System.out.println("now:" + now);
        logger.info(now);
    }
}
