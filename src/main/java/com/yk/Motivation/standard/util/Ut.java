package com.yk.Motivation.standard.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class Ut {

    public static class date {
        public static String getCurrentDateFormatted(String pattern) { // 현재 날짜와 시간을 pattern 대로 format 해서 return
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.format(new Date());
        }

    }

    public static class file {
        public static String getExt(String filename) {  // filename 에서 확장자 lowercase 로 추출
            return Optional.ofNullable(filename)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(filename.lastIndexOf(".") + 1).toLowerCase())
                    .orElse("");
        }

        public static String getFileExtTypeCodeFromFileExt(String ext) {

            switch (ext) {
                case "jpeg":
                case "jpg":
                case "gif":
                case "png":
                    return "img";
                case "mp4":
                case "avi":
                case "mov":
                    return "video";
                case "mp3":
                    return "audio";
            }

            return "etc";
        }

        public static String getFileExtType2CodeFromFileExt(String ext) {

            switch (ext) {
                case "jpeg":
                case "jpg":
                    return "jpg";
                case "gif", "png", "mp4", "mov", "avi", "mp3":
                    return ext;
            }

            return "etc";
        }
    }

    public static class url {

        public static String encode(String message) { // 문자열을 UTF-8 형식 으로 URL 인코딩

            try {
                return URLEncoder.encode(message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return null;
            }

        }

    }
}