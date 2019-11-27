package me.iwf.photopicker.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by myc on 2016/12/14.
 * More Code on 1101255053@qq.com
 * Description:
 */
public class FileUtils {
    public static boolean fileIsExists(String path) {
        if (path == null || path.trim().length() <= 0) {
            return false;
        }
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static List<File> listImageInDir(final File dir, boolean deeply) {
        if (!isDir(dir)) {
            return new ArrayList<>();
        }
        List<File> list = new ArrayList<>();
        getImage(dir, list, deeply);
        return list;
    }

    private static void getImage(File dir, List<File> fileList, boolean deeply) {
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (deeply) {
                        getImage(file, fileList, true);
                    }
                } else {
                    String fileExtension = getFileExtension(file.getPath()).toLowerCase();
                    if ("jpg".equals(fileExtension) || "png".equals(fileExtension) || "jpeg".equals(fileExtension)) {
                        fileList.add(file);
                    }
                }
            }
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param filePath 文件路径
     * @return 文件扩展名 jpg png
     */
    private static String getFileExtension(final String filePath) {
        if (isSpace(filePath)) {
            return "";
        }
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) {
            return "";
        }
        return filePath.substring(lastPoi + 1);
    }

    /**
     * 判断是否为文件夹
     *
     * @param file 文件
     * @return true 文件夹
     */
    private static boolean isDir(final File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    private static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String formatYearSecond(Date date) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return format(date, pattern);
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat sourceSf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sourceSf.format(date);
    }
}
