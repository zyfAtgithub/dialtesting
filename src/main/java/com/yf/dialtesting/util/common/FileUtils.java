package com.yf.dialtesting.util.common;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    public FileUtils() {
    }

    public static boolean mkdirs(File file) {
        return StringUtils.isNull(file) ? false : file.mkdirs();
    }

    public static boolean fileExists(File file) {
        return StringUtils.isNull(file) ? false : file.exists();
    }

    public static void main(String[] args) throws IOException {
        File f = new File("d:\\dir\\test\\a");
        System.out.println(mkdirs(f));
    }
}
