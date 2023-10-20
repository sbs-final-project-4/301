package com.yk.Motivation.standard.util;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Ut {
    public static class date {
        public static String getCurrentDateFormatted(String pattern) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.format(new Date());
        }
    }

    public static class file {
        private static final String ORIGIN_FILE_NAME_SEPARATOR = "--originFileName_";

        public static String getOriginFileName(String sourceFile) {
            if (sourceFile.contains(ORIGIN_FILE_NAME_SEPARATOR)) {
                String[] fileInfos = sourceFile.split(ORIGIN_FILE_NAME_SEPARATOR);
                return fileInfos[fileInfos.length - 1];
            }

            return Paths.get(sourceFile).getFileName().toString();
        }

        public static String toFile(MultipartFile multipartFile, String tempDirPath) {
            if (multipartFile == null) return "";
            if (multipartFile.isEmpty()) return "";

            String filePath = tempDirPath + "/" + UUID.randomUUID() + ORIGIN_FILE_NAME_SEPARATOR + multipartFile.getOriginalFilename();

            try {
                multipartFile.transferTo(new File(filePath));
            } catch (IOException e) {
                return "";
            }

            return filePath;
        }

        public static void moveFile(String filePath, File file) {
            moveFile(filePath, file.getAbsolutePath());
        }

        public static class DownloadFileFailException extends RuntimeException {

        }

        private static String getFileExt(File file) {
            Tika tika = new Tika();
            String mimeType = "";

            try {
                mimeType = tika.detect(file);
            } catch (IOException e) {
                return null;
            }

            String ext = mimeType.replaceAll("image/", "");
            ext = ext.replaceAll("jpeg", "jpg");

            return ext.toLowerCase();
        }

        public static String getFileExt(String fileName) {
            int pos = fileName.lastIndexOf(".");

            if (pos == -1) {
                return "";
            }

            return fileName.substring(pos + 1).trim();
        }

        public static String getFileNameFromUrl(String fileUrl) {
            try {
                return Paths.get(new URI(fileUrl).getPath()).getFileName().toString();
            } catch (URISyntaxException e) {
                return "";
            }
        }

        // fileUrl 에서 파일을 다운로드 하여 filePath(outputDir/tempFileName) 에 저장
        public static String downloadFileByHttp(String fileUrl, String outputDir) {
            String originFileName = getFileNameFromUrl(fileUrl); // filename.ext
            String fileExt = getFileExt(originFileName); // ext

            if (fileExt.isEmpty()) {
                fileExt = "tmp";
            }

            new File(outputDir).mkdirs(); // 경로 없으면 생성 (tem Directory)

            String tempFileName = UUID.randomUUID() + ORIGIN_FILE_NAME_SEPARATOR + originFileName + "." + fileExt;
            String filePath = outputDir + "/" + tempFileName;

            // 'try-with-resources' 문을 사용하여 파일 출력 스트림을 생성
            // 이렇게 하면 스트림이 사용 후 자동으로 닫음
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {

                // 주어진 파일 URL에서 입력 스트림을 열어 읽기 가능한 바이트 채널을 생성
                ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(fileUrl).openStream());

                // FileOutputStream 객체에서 파일 채널을 가져옴
                FileChannel fileChannel = fileOutputStream.getChannel();

                // readableByteChannel에서 데이터를 읽어와 fileChannel을 통해 파일에 기록
                // 시작 위치는 0이며, Long.MAX_VALUE는 최대 복사 가능한 바이트 수를 나타냄
                // 실제로는 EOF(End-Of-File) 또는 파일의 크기에 도달할 때까지 데이터를 복사
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            } catch (Exception e) {
                throw new DownloadFileFailException();
            }

            File file = new File(filePath);

            if (file.length() == 0) {
                throw new DownloadFileFailException();
            }

            if (fileExt.equals("tmp")) {
                String ext = getFileExt(file);

                if (ext == null || ext.isEmpty()) {
                    throw new DownloadFileFailException();
                }

                String newFilePath = filePath.replaceAll("\\.tmp", "\\." + ext);
                moveFile(filePath, newFilePath);
                filePath = newFilePath;
            }

            return filePath;
        }

        public static void moveFile(String filePath, String destFilePath) {
            Path file = Paths.get(filePath);
            Path destFile = Paths.get(destFilePath);

            try {
                Files.move(file, destFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) {

            }
        }


        public static String getExt(String filename) {
            return Optional.ofNullable(filename)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(filename.lastIndexOf(".") + 1).toLowerCase())
                    .orElse("");
        }

        public static String getFileExtTypeCodeFromFileExt(String ext) {
            return switch (ext) {
                case "jpeg", "jpg", "gif", "png" -> "img";
                case "mp4", "avi", "mov" -> "video";
                case "mp3" -> "audio";
                default -> "etc";
            };

        }

        public static String getFileExtType2CodeFromFileExt(String ext) {

            return switch (ext) {
                case "jpeg", "jpg" -> "jpg";
                case "gif", "png", "mp4", "mov", "avi", "mp3" -> ext;
                default -> "etc";
            };

        }

        public static void remove(String filePath) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static class url {

        public static String encode(String message) {
            return URLEncoder.encode(message, StandardCharsets.UTF_8);
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
            if (Ut.str.isBlank(s)) return "";

            return withTtl(encode(s));
        }

        public static String withTtl(String msg) {
            return msg + ";ttl=" + new Date().getTime();
        }

        public static String getPath(String refererUrl, String defaultValue) {
            try {
                return new URI(refererUrl).getPath();
            } catch (URISyntaxException e) {
                return defaultValue;
            }
        }
    }

    public static class str {
        public static boolean hasLength(String string) {
            return string != null && !string.isEmpty();
        }

        public static boolean isBlank(String string) {
            return !hasLength(string);
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

        public static boolean isBlank(List<String> requestParameterValues) {
            return !hasText(requestParameterValues);
        }

        public static boolean hasText(List<String> requestParameterValues) {
            return str.hasLength(getFirstStrOrEmpty(requestParameterValues));
        }

        public static String value(List<String> requestParameterValues) {
            return getFirstStrOrEmpty(requestParameterValues);
        }
    }
}