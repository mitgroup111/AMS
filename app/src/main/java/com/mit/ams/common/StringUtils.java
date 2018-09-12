package com.mit.ams.common;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 17.7.10.
 */

public class StringUtils {

    /**
     * 把String以一个char为分割点，拆分成String数组
     * @param s
     * @param chr
     * @return
     */
    public static String[] split(String s, int chr) {
        Vector res = new Vector();
        int curr;
        int prev = 0;
        while ((curr = s.indexOf(chr, prev)) >= 0) {
            res.addElement(s.substring(prev, curr));
            prev = curr + 1;
        }
        res.addElement(s.substring(prev));
        String[] splitted = new String[res.size()];
        res.copyInto(splitted);
        return splitted;
    }

    /**
     * 字符串替换
     * @param from
     *          字符串中要被替换的子串
     * @param to
     *         替换子串的字符串
     * @param source
     *          被操作的字符串
     * @return
     */
    public static String replaceAll(String from, String to, String source) {
        if (source == null || from == null || to == null) {
            return null;
        }
        StringBuffer bf = new StringBuffer();
        int index = -1;
        while ((index = source.indexOf(from)) != -1) {
            bf.append(source.substring(0, index) + to);
            source = source.substring(index + from.length());
            index = -1;
        }
        bf.append(source);
        return bf.toString();
    }

    /**
     * 功能：检查这个字符串是不是空字符串。<br/>
     * 如果这个字符串为null或者trim后为空字符串则返回true，否则返回false。
     *
     * @author jiangshuai
     * @date 2017年04月24日
     * @param chkStr
     *            被检查的字符串
     * @return boolean
     */
    public static boolean isEmpty(String chkStr) {
        if (chkStr == null) {
            return true;
        } else {
            return "".equals(chkStr.trim()) ? true : false;
        }
    }

    public static boolean isMobileNo(String mobileNo){
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobileNo);
        return m.matches();
    }
}
