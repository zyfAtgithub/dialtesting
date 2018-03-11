package com.yf.dialtesting.util;


import com.yf.dialtesting.util.system.OperSystem;
import com.yf.dialtesting.util.system.OperSystemType;

public final class Constants {
    public static long FILESIZE_THRESHOLD = 1073741824L;
    public static final String STOR_DIR;

    public Constants() {
    }

    public static void main(String[] args) {
        String fileName = "f:\\diatesting\\9890-78.txt";
        int pos1 = fileName.lastIndexOf("-");
        int pos2 = fileName.lastIndexOf(".txt");
        System.out.println(pos1);
        System.out.println(pos2);
        System.out.println(fileName.substring(pos1 + 1, pos2));
    }

    static {
        if (OperSystemType.WINDOWS == OperSystem.judgeSystemType()) {
            STOR_DIR = "c:\\diatesting\\";
        } else {
            STOR_DIR = "/root/diatesting/";
        }

    }
}
