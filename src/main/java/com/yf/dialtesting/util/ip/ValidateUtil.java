package com.yf.dialtesting.util.ip;

import com.yf.dialtesting.util.common.StringUtils;
import org.apache.http.HttpHost;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {
    private static final Pattern IP_PATTERN = Pattern.compile("^(?:(?:1[0-9][0-9]\\.)|(?:2[0-4][0-9]\\.)|(?:25[0-5]\\.)|(?:[1-9][0-9]\\.)|(?:[0-9]\\.)){3}(?:(?:1[0-9][0-9])|(?:2[0-4][0-9])|(?:25[0-5])|(?:[1-9][0-9])|(?:[0-9]))$");
//    private static final Pattern URL_PATTERN = Pattern.compile("^(?:https?://)?[\\w]{1,}(?:\\.?[\\w]{1,})+[\\w-_/?&=#%:]*$");
    private static final Pattern URL_PATTERN = Pattern.compile("^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$");


    public ValidateUtil() {
    }

    public static boolean validateIp(String ipStr) {
        return IP_PATTERN.matcher(ipStr).matches();
    }

    public static boolean validateIpList(String ipStr, String separator) {
        if (StringUtils.isNullOrEmpty(ipStr)) {
            return false;
        }
        String[] ipArr = ipStr.split(separator);
        for (String ip : ipArr) {
            if (!IP_PATTERN.matcher(ip).matches()) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateUrl(String urlStr) {
        return URL_PATTERN.matcher(urlStr).matches();
    }

    public static void calcu(List<Integer> list) {
        list.set(0, list.get(0) + 1);
    }

    public static void main(String[] args) {
        System.out.println(validateIp("249.202.1.0"));
        System.out.println(validateIpList("58.212.181.96", ","));
        System.out.println(validateUrl("https://a.c.b"));
        System.out.println(validateUrl("http://www.osyunwei.com/archives/789.html"));

    }
}
