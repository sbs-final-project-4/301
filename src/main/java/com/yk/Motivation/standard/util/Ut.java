package com.yk.Motivation.standard.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Ut {
    public static class date {
        public static String getCurrentDateFormatted(String pattern) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.format(new Date());
        }
    }

    public static class file {
        public static String getExt(String filename) {
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

        public static String encode(String message) {
            try {
                return URLEncoder.encode(message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }

        public static String modifyQueryParam(String url, String paramName, String paramValue) {
            url = deleteQueryParam(url, paramName);
            url = addQueryParam(url, paramName, paramValue);

            return url;
        }

        public static String addQueryParam(String url, String paramName, String paramValue) {
            if (!url.contains("?")) {
                url += "?";
            }

            if (!url.endsWith("?") && !url.endsWith("&")) {
                url += "&";
            }

            url += paramName + "=" + paramValue;

            return url;
        }

        private static String deleteQueryParam(String url, String paramName) {
            int startPoint = url.indexOf(paramName + "=");
            if (startPoint == -1) return url;

            int endPoint = url.substring(startPoint).indexOf("&");

            if (endPoint == -1) {
                return url.substring(0, startPoint - 1);
            }

            String urlAfter = url.substring(startPoint + endPoint + 1);

            return url.substring(0, startPoint) + urlAfter;
        }

        public static String encodeWithTtl(String s) {
            return withTtl(encode(s));
        }

        public static String withTtl(String msg) {
            return msg + ";ttl=" + new Date().getTime();
        }
    }

    public static class str {
        public static boolean hasLength(String string) {
            return string != null && string.length() > 0;
        }

        public static String tempPassword(int i) {
            String passwordSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
            StringBuilder password = new StringBuilder();

            for (int x = 0; x < i; x++) {
                int random = (int) (Math.random() * passwordSet.length());
                password.append(passwordSet.charAt(random));
            }

            return password.toString();
        }
    }

    public static class thy {
        private static String getFirstStrOrEmpty(List<String> requestParameterValues) {
            return Optional.ofNullable(requestParameterValues)
                    .filter(values -> !values.isEmpty())
                    .map(values -> values.get(0).replaceAll("%20", "").trim())
                    .orElse("");
        }

        public static boolean inputAttributeAutofocus(List<String> requestParameterValues) {
            return !str.hasLength(getFirstStrOrEmpty(requestParameterValues));
        }

        public static String value(List<String> requestParameterValues) {
            return getFirstStrOrEmpty(requestParameterValues);
        }
    }
}