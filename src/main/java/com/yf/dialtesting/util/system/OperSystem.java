//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yf.dialtesting.util.system;

import java.io.IOException;

public class OperSystem {
    public OperSystem() {
    }

    public static OperSystemType judgeSystemType() {
        try {
            System.out.println("===========操作系统是:" + System.getProperties().getProperty("os.name"));
            String osName = System.getProperty("os.name");
            if (osName == null) {
                throw new IOException("os.name not found");
            } else {
                osName = osName.toLowerCase();
                if (osName.indexOf("windows") != -1) {
                    return OperSystemType.WINDOWS;
                } else if (osName.indexOf("linux") == -1 && osName.indexOf("sun os") == -1 && osName.indexOf("sunos") == -1 && osName.indexOf("solaris") == -1 && osName.indexOf("mpe/ix") == -1 && osName.indexOf("freebsd") == -1 && osName.indexOf("irix") == -1 && osName.indexOf("digital unix") == -1 && osName.indexOf("unix") == -1 && osName.indexOf("mac os x") == -1) {
                    return osName.indexOf("hp-ux") == -1 && osName.indexOf("aix") == -1 ? OperSystemType.OTHER : OperSystemType.POSIX_UNIX;
                } else {
                    return OperSystemType.UNIX;
                }
            }
        } catch (Exception var1) {
            return OperSystemType.INIT_PROBLEM;
        }
    }
}
