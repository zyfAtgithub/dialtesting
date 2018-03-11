package com.yf.dialtesting.util.common;

import com.yf.dialtesting.util.Constants;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
    public FileUtil() {
    }

    private static boolean judgeFileExist(String fileAbsPath) {
        return StringUtils.isNullOrEmpty(fileAbsPath) ? false : (new File(fileAbsPath)).exists();
    }

    private static void generateFile(String fileAbsPath) {
        if (!judgeFileExist(fileAbsPath)) {
            String pdirPath = fileAbsPath.substring(0, fileAbsPath.lastIndexOf(File.separator) + 1);
            File f;
            if (!judgeFileExist(pdirPath)) {
                f = new File(pdirPath);
                boolean b = f.mkdirs();
                if (!b) {
                    return;
                }
            }

            f = new File(fileAbsPath);

            try {
                f.createNewFile();
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }
    }

    public static long getFileSize(String fileAbsPath) {
        RandomAccessFile randomFile = null;

        try {
            randomFile = new RandomAccessFile(fileAbsPath, "rw");
            long var2 = randomFile.length();
            return var2;
        } catch (IOException var13) {
            var13.printStackTrace();
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException var12) {
                    var12.printStackTrace();
                }
            }

        }

        return 0L;
    }

    private static boolean renameFile(String fileAbsPath) {
        if (!judgeFileExist(fileAbsPath)) {
            return false;
        } else {
            String pdirPath = fileAbsPath.substring(0, fileAbsPath.lastIndexOf(File.separator) + 1);
            String fileName = fileAbsPath.substring(fileAbsPath.lastIndexOf(File.separator) + 1);
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            String newName = pdirPath + fileName.substring(0, fileName.lastIndexOf(".")) + "-" + sf.format(new Date()) + fileName.substring(fileName.lastIndexOf("."));
            File f = new File(fileAbsPath);
            File newF = new File(newName);
            return f.renameTo(newF);
        }
    }

    public static boolean deleteFile(String fileAbsPath) {
        if (!judgeFileExist(fileAbsPath)) {
            return false;
        } else {
            File f = new File(fileAbsPath);
            return f.delete();
        }
    }

    public static synchronized boolean appentContent2File(String fileName, String content) {
        return appentContent2File(fileName, Constants.FILESIZE_THRESHOLD, content);
    }

    public static synchronized boolean appentContent2File(String fileName, long size, String content) {
        generateFile(fileName);
        RandomAccessFile randomFile = null;

        boolean var7;
        try {
            randomFile = new RandomAccessFile(fileName, "rw");
            long fileLength = randomFile.length();
            if (fileLength + (long)content.getBytes().length + 2L >= Constants.FILESIZE_THRESHOLD) {
                System.out.println("文件大小到达上限！！");
                if (randomFile != null) {
                    try {
                        randomFile.close();
                    } catch (IOException var21) {
                        var21.printStackTrace();
                        boolean var8 = false;
                        return var8;
                    }
                }

                renameFile(fileName);
                var7 = true;
                return var7;
            }

            randomFile.seek(fileLength);
            randomFile.write((content + "\r\n").getBytes());
            var7 = true;
        } catch (IOException var22) {
            var22.printStackTrace();
            boolean var6 = false;
            return var6;
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException var20) {
                    var20.printStackTrace();
                    return false;
                }
            }

        }

        return var7;
    }
}
