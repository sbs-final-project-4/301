package com.yk.Motivation.standard.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Ut {
    public static class url {

        public static String encode(String message) { // 문자열을 UTF-8으로 URL 인코딩

            try {
                return URLEncoder.encode(message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return null;
            }

        }

    }
}