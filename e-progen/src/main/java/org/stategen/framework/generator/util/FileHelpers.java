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
package org.stategen.framework.generator.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stategen.framework.util.StringUtil;

/**
 * The Class FileHelpers.
 */
public class FileHelpers {
    final static Logger logger = LoggerFactory.getLogger(FileHelpers.class);
    final static String unOverrideFolderFlag = "@" + StringUtil.SLASH;
    final static String unOverrideFolderFlag_Back = "@" + StringUtil.BACK_SLASH;

    public static boolean isExists(String fileName) {
        if (StringUtil.isNotBlank(fileName)) {
            File file = new File(fileName);
            return file.isFile() && file.exists();
        }
        return false;
    }

    public static File getFile(String file) {
        if (StringUtil.isBlank(file)) {
            throw new IllegalArgumentException("'file' must be not blank");
        }
        try {
            if (file.startsWith("classpath:")) {
                return getFileByClassLoader(file.substring("classpath:".length()));
            } else {
                return new File(toFilePathIfIsURL(new File(file)));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.toString(), e);
        } catch (IOException e) {
            throw new RuntimeException("getFile() error,file:" + file, e);
        }
    }

    public static String getSimpleFileName(String fileName) {
        File file = getFile(fileName);
        String name = file.getName();
        return name;
    }

    public static File getFileByClassLoader(String resourceName) throws IOException {
        String pathToUse = resourceName;
        if (pathToUse.charAt(0)=='/') {
            pathToUse = pathToUse.substring(1);
        }
        Enumeration<URL> urls = ClassHelpers.getDefaultClassLoader().getResources(pathToUse);
        while (urls.hasMoreElements()) {
            return new File(urls.nextElement().getFile());
        }
        urls = FileHelpers.class.getClassLoader().getResources(pathToUse);
        while (urls.hasMoreElements()) {
            return new File(urls.nextElement().getFile());
        }
        urls = ClassLoader.getSystemResources(pathToUse);
        while (urls.hasMoreElements()) {
            return new File(urls.nextElement().getFile());
        }
        throw new FileNotFoundException("classpath:" + resourceName);
    }

    /**
     *  搜索目录下的所有文件,并忽略如 .svn .cvs等文件 ,并且只能含有 如 .java ,.xml的文件
     *
     * @param dir the dir
     * @param extractFileName the extract file name
     * @return the list
     */
    public static List<File> searchAllNotIgnoreFile(File dir, Set<String> extractFileName) {
        ArrayList<File> arrayList = new ArrayList<File>();
        searchAllNotIgnoreFile(dir, arrayList);
        int size = arrayList.size();
        for (int i = size - 1; i >= 0; i--) {
            File file = arrayList.get(i);
            if (file.isDirectory()) {
                arrayList.remove(i);
                continue;
            }

            String fileName = file.getName();
            String prefix = fileName.substring(fileName.lastIndexOf('.') + 1);
            logger.info(prefix);

            if (!extractFileName.contains(prefix)) {
                arrayList.remove(i);
                continue;
            }
        }

        Collections.sort(arrayList, new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
            }
        });
        return arrayList;
    }

    public static void searchAllNotIgnoreFile(File dir, List<File> collector) {
        collector.add(dir);
        if ((!dir.isHidden() && dir.isDirectory()) && !isIgnoreFile(dir)) {
            File[] subFiles = dir.listFiles();
            for (int i = 0; i < subFiles.length; i++) {
                searchAllNotIgnoreFile(subFiles[i], collector);
            }
        }
    }

    public static Set<String> ignoreList = new HashSet<String>(Arrays.asList(".svn", "CVS", ".cvsignore",
        ".copyarea.db", "SCCS", "vssver.scc", ".DS_Store", ".git", ".settings", ".idea", ".vscode", ".myeclipse"));
    public static Set<String> binaryExtentionsList = new HashSet<String>(
        Arrays.asList(".gif", ".jpg", ".woff", ".png", ".eot", ".svg", ".ttf", ".woff", ".css", ".less", ".pdf"));

    private static boolean isIgnoreFile(File file) {
        return ignoreList.contains(file.getName());
        //        for (int i = 0; i < ignoreList.size(); i++) {
        //            if (file.getName().equals(ignoreList.get(i))) {
        //                return true;
        //            }
        //        }
        //        return false;
    }

    //
    //    public static Set<String> binaryExtentionsList = new HashSet<String>();
    //    
    //    static {
    //        loadBinaryExtentionsList("binary_filelist.txt", true);
    //        loadBinaryExtentionsList(
    //                "cn/org/rapid_framework/generator/util/binary_filelist.txt",
    //                false);
    //    }
    //
    //    public static void loadBinaryExtentionsList(String resourceName,
    //            boolean ignoreException) {
    //        try {
    //            Enumeration<URL> urls = FileHelpers.class.getClassLoader()
    //                    .getResources(resourceName);
    //            boolean notFound = true;
    //            while (urls.hasMoreElements()) {
    //                notFound = false;
    //                URL url = urls.nextElement();
    //                InputStream input = url.openStream();
    //                binaryExtentionsList.addAll(IOHelpers
    //                        .readLines(new InputStreamReader(input)));
    //                input.close();
    //            }
    //            if (notFound)
    //                throw new IllegalStateException("not found required file with:"
    //                        + resourceName);
    //        } catch (Exception e) {
    //            if (!ignoreException)
    //                throw new RuntimeException(
    //                        "loadBinaryExtentionsList occer error,resourceName:"
    //                                + resourceName, e);
    //        }
    //    }
    /**
     * 得到相对路径.
     *
     * @param baseDir the base dir
     * @param file the file
     * @return the relative path
     */
    public static String getRelativeFileName(File baseDir, File file) {
        String absolutePath = file.getAbsolutePath();
        if (baseDir == null) {
            return absolutePath;
        }

        if (baseDir.equals(file)) {
            return absolutePath;
        }

        if (baseDir.getParentFile() == null) {
            return absolutePath.substring(baseDir.getAbsolutePath().length());
        }

        return absolutePath.substring(baseDir.getAbsolutePath().length() + 1);
    }

    /**
     * https://blog.csdn.net/u013309993/article/details/84099290
     * 获得targetPath相对于sourcePath的相对路径
     * @param sourcePath    : 原文件路径
     * @param targetPath    : 目标文件路径
     * @return
     */
    public static String getRelativePath(String sourcePath, String targetPath) {
        StringBuilder pathSB = new StringBuilder();

        if (targetPath.indexOf(sourcePath) == 0) {
            pathSB.append(targetPath.replace(sourcePath, ""));
        } else {
            String[] sourcePathArray = sourcePath.split("/");
            String[] targetPathArray = targetPath.split("/");
            if (targetPathArray.length >= sourcePathArray.length) {
                for (int i = 0; i < targetPathArray.length; i++) {
                    if (sourcePathArray.length > i && targetPathArray[i].equals(sourcePathArray[i])) {
                        continue;
                    } else {
                        for (int j = i; j < sourcePathArray.length; j++) {
                            pathSB.append("../");
                        }
                        for (; i < targetPathArray.length; i++) {
                            pathSB.append(targetPathArray[i] + '/');
                        }
                        break;
                    }
                }
            } else {
                for (int i = 0; i < sourcePathArray.length; i++) {
                    if (targetPathArray.length > i && targetPathArray[i].equals(sourcePathArray[i])) {
                        continue;
                    } else {
                        for (int j = i; j < sourcePathArray.length; j++) {
                            pathSB.append("../");
                        }
                        break;
                    }
                }
            }

        }

        String result = pathSB.toString();
        if (!targetPath.endsWith("/") && result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    public static File parentMkdir(String fileName) {
        File result = new File(fileName);
        if (!result.exists()) {
            File parentFile = null;
            if (StringUtil.isFolderEnd(fileName)) {
                parentFile = result;
            } else {
                parentFile = result.getParentFile();
            }

            if (parentFile != null) {
                parentFile.mkdirs();
            }
        }
        return result;
    }

    public static boolean isUnOverridePath(String fileName) {
        return fileName.indexOf(unOverrideFolderFlag) > 0 || fileName.indexOf(unOverrideFolderFlag_Back) > 0;
    }

    public static String replaceUnOverridePath(String fileName) {
        return fileName.replaceAll(unOverrideFolderFlag, StringUtil.SLASH);
    }

    /** 搜索目录下的所有文件,并忽略如 .svn .cvs等文件 */
    public static List<File> searchAllNotIgnoreFile(File dir) {
        ArrayList<File> arrayList = new ArrayList<File>();
        searchAllNotIgnoreFile(dir, arrayList);
        Collections.sort(arrayList, new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
            }
        });
        return arrayList;
    }

    public static boolean isBinaryFile(String filename) {
        if (StringUtil.isBlank(getExtension(filename))) {
            return false;
        }
        return binaryExtentionsList.contains(getExtension(filename).toLowerCase());
    }

    public static String getExtension(String filename) {
        if (StringUtil.isEmpty(filename)) {
            return null;
        }

        int index = filename.lastIndexOf('.');
        //".git, .gitignore 不是"
        if (index <= 0) {
            return null;
        } else {
            return filename.substring(index);
        }
    }

    public static String toFilePathIfIsURL(File file) {
        try {
            return new URL(((File) file).getPath()).getPath();
        } catch (MalformedURLException e) {
            // ignore,fallback to file.getPath()
            return file.getPath();
        }
    }

    public static String getCanonicalPath(String outputDir, String targetFileName) throws IOException {
        File targetFile = new File(outputDir, targetFileName);
        return processCononicalPath(targetFile, targetFileName);
    }

    public static String getCanonicalPath(String fileName) throws IOException {
        File targetFile = getFile(fileName);
        return processCononicalPath(targetFile, fileName);

    }

    private static String processCononicalPath(File targetFile, String targetFileName) throws IOException {
        String canonicalPath = targetFile.getCanonicalPath();
        if (StringUtil.isFolderEnd(targetFileName)) {
            canonicalPath = StringUtil.endWithSlash(canonicalPath);
        }
        canonicalPath = StringUtil.replaceBackSlash(canonicalPath);
        return canonicalPath;
    }

    public static boolean isSameFileText(String fileName, String newText) throws IOException {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            String fileText = IOHelpers.readFile(file, StringUtil.UTF_8);
            if (newText.equals(fileText)) {
                return true;
            } else {
                //System.out.println(newText.length()+"   "+fileText.length());
            }
        }
        return false;


    }

    //
    //    public static String getTempDir() {
    //        return System.getProperty("java.io.tmpdir");
    //    }
}
