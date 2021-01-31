/*
 * Copyright (C) 2018  niaoge<78493244@qq.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.stategen.framework.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class StringUtil.
 * 处理String的工具类
 */
public class StringUtil {
    final static Logger logger = LoggerFactory.getLogger(StringUtil.class);

    public static final String SLASH      = "/";
    public static final String BACK_SLASH = "\\";
    public static final String UNDERLINE  = "_";
    public static final String $          = "$";
    public static final String COLON      = ":";

    public static final String emptyString = "";
    public static final String UTF_8       = "UTF-8";


    public static void logInfo(Object o){
        if (logger.isInfoEnabled()) {
            logger.info(o != null ? o.toString() : "null");
        }
    }

    /**
     * Equals.
     *
     * @param source the source
     * @param dest the dest
     * @return true, if successful
     */
    public static boolean equals(String source, String dest) {
        return (source != null) && (source.equals(dest));
    }

    public static boolean equ(Object source, Object dest) {
        if (source == null && dest == null) {
            return true;
        }
        if (source == null) {
            return false;
        }
        return source.equals(dest);
    }

    /**
     * Not equals.
     *
     * @param source the source
     * @param dest the dest
     * @return true, if successful
     */
    public static boolean notEquals(String source, String dest) {
        return !equals(source, dest);
    }

    /**
     * Checks if is empty.
     *
     * @param str the str
     * @return true, if is empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * <p>Checks if a String is not empty ("") and not null.</p>
     *
     * <pre>
     * isNotEmpty(null)      = false
     * isNotEmpty("")        = false
     * isNotEmpty(" ")       = true
     * isNotEmpty("bob")     = true
     * isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is not empty and not null
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * isBlank(null)      = true
     * isBlank("")        = true
     * isBlank(" ")       = true
     * isBlank("bob")     = false
     * isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a String is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * isNotBlank(null)      = false
     * isNotBlank("")        = false
     * isNotBlank(" ")       = false
     * isNotBlank("bob")     = true
     * isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str the str
     * @return true, if is not blank
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Starts with ignore case.
     *
     * @param str the str
     * @param prefix the prefix
     * @return true, if successful
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return startsWith(str, prefix, true);
    }

    /**
     * <p>Check if a String starts with a specified prefix (optionally case insensitive).</p>
     *
     * @param str  the String to check, may be null
     * @param prefix the prefix to find, may be null
     * @param ignoreCase inidicates whether the compare should ignore case
     *  (case insensitive) or not.
     * @return <code>true</code> if the String starts with the prefix or
     *  both <code>null</code>
     * @see java.lang.String#startsWith(String)
     */
    private static boolean startsWith(String str, String prefix, boolean ignoreCase) {
        if (str == null || prefix == null) {
            return (str == null && prefix == null);
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
    }

    /**
     * Ends with ignore case.
     *
     * @param str the str
     * @param suffix the suffix
     * @return true, if successful
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return endsWith(str, suffix, true);
    }

    /**
     * <p>Check if a String ends with a specified suffix (optionally case insensitive).</p>
     *
     * @param str  the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @param ignoreCase inidicates whether the compare should ignore case
     *  (case insensitive) or not.
     * @return <code>true</code> if the String starts with the prefix or
     *  both <code>null</code>
     * @see java.lang.String#endsWith(String)
     */
    private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
        if (StringUtil.isEmpty(str) || StringUtil.isEmpty(suffix)) {
            return false;
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        int strOffset = str.length() - suffix.length();
        return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
    }

    /**
     * To upper case.
     * <pre>如果str为null 将为null</pre>
     *
     * @param str the str
     * @return the string
     */
    public static String toUpperCase(String str) {
        if (str != null) {
            return str.toUpperCase();
        }
        return null;
    }

    /**
     * To lower case.
     *<pre>如果str为null 将为null</pre>
     * @param str the str
     * @return the string
     */
    public static String toLowerCase(String str) {
        if (str != null) {
            return str.toLowerCase();
        }
        return null;
    }

    /**
     * Checks if is string.
     *
     * @param obj the obj
     * @return true, if is string
     */
    public static boolean isString(Object obj) {
        return (obj != null) && (obj instanceof String);
    }

    /**
     * Checks if is object.
     *
     * @param obj the obj
     * @return true, if is object
     */
    public static boolean isObject(Object obj) {
        return (obj != null) && !(obj instanceof String);
    }

    /**
     * To string blank.
     *
     * @param dest the dest
     * @return the string
     */
    public static String toStringBlank(String dest) {
        if (dest == null) {
            return "";
        }
        return dest;
    }

    /**
     * Parser to list.
     *
     * @param dest the dest
     * @return the list
     */
    public static List<String> parserToList(String dest) {
        List<String> result = new ArrayList<String>();
        if (isNotEmpty(dest)) {
            String[] dests = dest.split("\n");
            for (String sub1 : dests) {
                sub1 = sub1.trim();
                if (isNotBlank(sub1)) {
                    String[] subDests = sub1.split(" ");
                    for (String sub2 : subDests) {
                        sub2 = sub2.trim();
                        if (isNotEmpty(sub2)) {
                            result.add(sub2);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static String toParamsString(Object[] params) {
        if (CollectionUtil.isEmpty(params)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            sb.append(params[i]);
            if (i < params.length - 1) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    public static String[] toStringArray(Object[] params) {
        String[] result = null;
        if (CollectionUtil.isNotEmpty(params)) {
            result = new String[params.length];
            for (int i = 0; i < params.length; i++) {
                Object obj = params[i];
                if (obj == null) {
                    result[i] = "";
                } else {
                    result[i] = obj.toString();
                }
            }
        }
        return result;
    }

    public static int indexOf(String source, String dest) {
        if (isEmpty(source) || isEmpty(dest)) {
            return -1;
        }
        return source.indexOf(dest);
    }

    public static String succCn(Boolean dest) {
        if (dest == null || !dest) {
            return "失败";
        }
        return "成功";
    }

    /***
     * 截取字符串,从idx位置截取 ,截取的部分用replaceBy代替，获取最大的长度为
     * */
    public static String abbr(String source, char replaceChar, int fromIdx, int times, int resultLen) {
        if (isEmpty(source)) {
            return source;
        }

        if (replaceChar == 0) {
            return source;
        }

        if (fromIdx < 0) {
            return source;
        }

        if (times <= 0) {
            return source;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(source.subSequence(0, fromIdx));
        for (int i = 0; i < times; i++) {
            sb.append(replaceChar);
        }

        int last = resultLen - (fromIdx + times);
        if (last > 0) {
            sb.append(source.substring(source.length() - last));
        }
        return sb.toString();
    }

    //    /**
    //     * 处理：手机号脱敏
    //     */
    //    public static String desensitizeMobile(String mobile) {
    //        if (isEmpty(mobile)){
    //            return "";
    //        }
    //        int length = mobile.length();
    //        int fromIdx =length-5;
    //        if (fromIdx<=0){
    //            fromIdx =1;
    //        }
    //        return abbr(mobile, '*', fromIdx,4, length);
    //    }
    //  

    /**
     * 处理：手机号脱敏
     */
    public static String desensitizeMobile(String mobile) {
        if (null == mobile) {
            return "";
        }
        int beginIndex = 3;
        int endIndex   = 0;
        int stars      = 4;
        if (mobile.length() >= 11) {
            endIndex = mobile.length() - 4;
            stars    = mobile.length() - 7;
        } else if (mobile.length() > 7 && mobile.length() < 11) {
            endIndex = 7;
        } else if (mobile.length() > 3 && mobile.length() <= 7) {
            endIndex = mobile.length();
            stars    = mobile.length() - 3;
        } else {
            endIndex   = mobile.length();
            beginIndex = mobile.length();
            stars      = 0;
        }
        String before = mobile.substring(0, beginIndex);
        String end    = mobile.substring(endIndex, mobile.length());
        String star   = "";
        while (stars > 0) {
            star += "*";
            stars--;
        }
        mobile = before + star + end;
        return mobile;
    }

    /**
     * 处理：首尾脱敏
     */
    public static String desensitizeBeginAndEnd(String desensitizeStr) {
        if (null == desensitizeStr || desensitizeStr.length() == 0) {
            return "";
        }
        if (desensitizeStr.length() >= 2) {
            String star  = "";
            int    stars = desensitizeStr.length() - 2;
            while (stars > 0) {
                star += "*";
                stars--;
            }
            String begin = desensitizeStr.substring(0, 1);
            String end   = desensitizeStr.substring(desensitizeStr.length() - 1);
            desensitizeStr = begin + star + end;
        }
        return desensitizeStr;
    }

    /**
     * 字符串相加，null变为空.
     *
     * @param strs
     * @return
     */
    public static String concatNoNull(String... strs) {
        if (CollectionUtil.isNotEmpty(strs)) {
            StringBuilder sb = new StringBuilder();
            for (String str : strs) {
                if (isNotEmpty(str)) {
                    sb.append(str);
                }
            }
            return sb.toString();
        }
        return "";
    }

    public static String concat(String join, String... strs) {
        StringBuilder sb             = new StringBuilder();
        boolean      append         = false;
        boolean      joinIsNotEmpty = isNotEmpty(join);
        for (String str : strs) {
            if (isNotEmpty(str)) {
                if (append) {
                    if (joinIsNotEmpty) {
                        sb.append(join);
                    }
                }
                sb.append(str);
                append = true;
            }

        }
        return sb.toString();
    }

    public static String replaceBackSlash(String dest) {
        return dest.replace(StringUtil.BACK_SLASH, StringUtil.SLASH);
    }

    public static String concatWithSlash(boolean repalceBckSlash, String... subPaths) {
        if (CollectionUtil.isNotEmpty(subPaths)) {
            StringBuilder sb       = new StringBuilder();
            boolean       appended = false;
            for (int i = 0; i < subPaths.length; i++) {
                String subPath = subPaths[i];
                if (isNotEmpty(subPath)) {
                    if (repalceBckSlash) {
                        subPath = StringUtil.replaceBackSlash(subPath);
                    }

                    subPath = endWithSlash(subPath);
                    if (appended && subPath.startsWith(SLASH)) {
                        if (subPath.length() >= 2) {
                            sb.append(subPath, 1, subPath.length() - 1);
                            appended = true;
                        }
                    } else {
                        sb.append(subPath);
                        appended = true;
                    }

                }
            }
            return sb.toString();
        }
        return "";
    }
    
    /***最后个结尾有 ‘/’ */
    public static String concatWithSlash(String... subPaths) {
        return concatWithSlash(false, subPaths);
    }

    /***最后个结尾有 ‘/’,同时，替换 '\'为 '/' */
    public static String concatPath(String... subPaths) {
        return concatWithSlash(true, subPaths);
    }

    public static String toStringIfNullThenEmpty(Object dest) {
        if (dest == null) {
            return "";
        }

        if (dest instanceof String) {
            return (String) dest;
        }

        return dest.toString();
    }

    public static boolean isEqual(String str, String other) {
        if (str != null) {
            return str.equals(other);
        }

        if (other != null) {
            return other.equals(str);
        }

        return false;
    }

    private static String capOrUncapfirst(String dest, boolean isCap) {
        if (StringUtil.isEmpty(dest)) {
            return dest;
        }
        char ch = dest.charAt(0);
        if (isCap) {
            ch = Character.toUpperCase(ch);
        } else {
            ch = Character.toLowerCase(ch);
        }
        StringBuilder sb = new StringBuilder(dest.length()).append(ch);

        if (dest.length() > 1) {
            sb.append(dest.substring(1));
        }
        return sb.toString();
    }

    public static String uncapfirst(String dest) {
        return capOrUncapfirst(dest, false);
    }

    public static String capfirst(String dest) {
        return capOrUncapfirst(dest, true);
    }

    public static String startWith(String dest, String start) {
        if (dest != null) {
            if (!dest.startsWith(start)) {
                dest = new StringBuilder(start.length() + dest.length()).append(start).append(dest).toString();
            }
        }
        return dest;
    }

    public static String endWith(String dest, String end) {
        if (dest != null) {
            if (!dest.endsWith(end)) {
                dest = new StringBuilder(end.length() + dest.length()).append(dest).append(end).toString();
            }
        }
        return dest;
    }

    public static String startWithSlash(String dest) {
        return startWith(dest, SLASH);
    }

    public static String endWithSlash(String dest) {
        return endWith(dest, SLASH);
    }

    public static boolean isFolderEnd(String fileName) {
        return fileName.endsWith(SLASH) || fileName.endsWith(BACK_SLASH);
    }

    public static String endtWith(String dest) {
        return endWith(dest, SLASH);
    }

    public static String join(String joinStr, String... dests) {
        if (CollectionUtil.isEmpty(dests)) {
            return null;
        }
        
        if (dests.length>1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dests.length; i++) {
                String dest = dests[i];
                sb.append(dest);
                if (i < dests.length -1 && !dest.endsWith(joinStr)) {
                    sb.append(joinStr);
                }
            }
            return sb.toString();
        }
        
        return dests[0];
    }
    /***最后一个结尾没有 '/' */
    public static String joinSLash(String... dests) {
        return join(SLASH, dests);
    }

    public static String trimRightTo(String dest, String indexStr) {
        if (StringUtil.isEmpty(dest) || StringUtil.isEmpty(indexStr)) {
            return dest;
        }

        int idx = dest.indexOf(indexStr);
        if (idx > -1) {
            return dest.substring(0, idx);
        }
        return dest;
    }

    public static String trimSubfix(String dest, String indexStr) {
        return trimRight(dest, indexStr);
    }

    public static String trimRight(String dest, String indexStr) {
        if (StringUtil.isEmpty(dest) || StringUtil.isEmpty(indexStr)) {
            return dest;
        }

        if (dest.endsWith(indexStr)) {
            dest = dest.substring(0, dest.length() - indexStr.length());
        }
        return dest;
    }

    /**
     * trimLeftTo(abcdeabcde,c)=>de
     * 
     * @param dest
     * @param indexStr
     * @return
     */
    public static String trimLeftFormRightTo(String dest, String indexStr) {
        if (StringUtil.isEmpty(dest) || StringUtil.isEmpty(indexStr)) {
            return dest;
        }

        int idx = dest.lastIndexOf(indexStr);
        if (idx > -1) {
            return dest.substring(idx + indexStr.length());
        }
        return dest;

    }

    public static String trimPrefix(String dest, String indexStr) {
        return trimLeft(dest, indexStr);
    }

    /**
     * trimLeft(abcdec,abc)=>dec
     * 
     * @param dest
     * @param indexStr
     * @return
     */
    public static String trimLeft(String dest, String indexStr) {
        if (StringUtil.isEmpty(dest) || StringUtil.isEmpty(indexStr)) {
            return dest;
        }

        if (dest.startsWith(indexStr)) {
            dest = dest.substring(indexStr.length());
        }
        return dest;
    }

    public static String trimePrefixIgnoreCase(String dest, String indexStr) {
        if (StringUtil.isEmpty(dest) || StringUtil.isEmpty(indexStr)) {
            return dest;
        }

        if (startsWithIgnoreCase(dest, indexStr)) {
            dest = dest.substring(indexStr.length());
        }

        return dest;
    }

    public static boolean containsIgnoreCase(String dest, String subStr) {
        if (isEmpty(dest) || isEmpty(subStr)) {
            return false;
        }
        dest   = dest.toUpperCase();
        subStr = subStr.toUpperCase();
        return dest.contains(subStr);
    }

    public static String getSimpleTitle(String title) {
        if (StringUtil.isEmpty(title)) {
            return title;
        }

        title = StringUtil.trimRightTo(title, " ");
        title = StringUtil.trimRightTo(title, ",");
        title = StringUtil.trimRightTo(title, ";");
        title = StringUtil.trimRightTo(title, "，");
        return title;
    }

    public static List<String> toLines(String destLines) {
        if (StringUtil.isEmpty(destLines)) {
            return Arrays.asList(destLines);
        }
        String lines[] = destLines.split("\\r?\\n");
        return Arrays.asList(lines);
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static String trimToNull(String str) {
        String ts = trim(str);
        return isEmpty(ts) ? null : ts;
    }
    
    public static String findByRex(String line ,String regExStr) {
        if (StringUtil.isEmpty(line) || StringUtil.isEmpty(regExStr)) {
            return "";
        }
        
        Matcher mat = Pattern.compile(regExStr).matcher(line);//此处是中文输入的（）
        while(mat.find()){
            return mat.group();
        }
        return "";
    }

}
