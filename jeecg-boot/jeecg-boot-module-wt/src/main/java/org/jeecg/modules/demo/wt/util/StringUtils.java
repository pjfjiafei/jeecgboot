package org.jeecg.modules.demo.wt.util;

public class StringUtils {
    public static String captureName(String value) {
        char[] chars = value.toCharArray();
        if (chars[0]>90) {
            chars[0]-=32;
        }
        return String.valueOf(chars);
    }
}
