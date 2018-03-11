package com.yf.dialtesting.util.common;

import java.util.ArrayList;
import java.util.List;

public final class StringUtils {
    public StringUtils() {
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isEmpty(Object obj) {
        return obj == null;
    }

    public static boolean isNullOrEmpty(String str) {
        return isNull(str) || "".equals(str);
    }

    public static boolean isNullOrEmpty(Object obj) {
        return isNull(obj) ? true : isNullOrEmpty(obj.toString());
    }

    public static String convert2String(Object obj) {
        return isNull(obj) ? "" : obj.toString();
    }

    public static String replace(String str, String pattern, String target) {
        return "";
    }

    public static void main(String[] args) {
        List<String> al = new ArrayList();
        al.add("a");
        al.add("a");
        al.add("a");
        System.out.println(convert2String(al));
    }
}
