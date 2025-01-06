package com.ispan.hestia.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

import com.ispan.hestia.errorMsg.ErrorMessage;
import com.ispan.hestia.exception.EcpayException;

public class EcpayFunction {
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * 依照綠界檢查碼生成規則計算檢查碼。
     * 
     * @param key    HashKey
     * @param iv     HashIV
     * @param params POST參數
     * @return 檢查碼
     */
    public final static String genCheckMacValue(String key, String iv, Map<String, String> params) {
        try {
            Map<String, String> sortedParams = new TreeMap<>(params);

            // 2. 將參數組裝成字串
            StringBuilder sb = new StringBuilder();
            sb.append("HashKey=").append(key);
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
            sb.append("&HashIV=").append(iv);
            System.out.println("sb" + sb.toString());
            String urlEncode = urlEncode(sb.toString()).toLowerCase();
            System.out.println("urlEncode" + urlEncode);
            urlEncode = netUrlEncode(urlEncode);

            return hash(urlEncode.getBytes(), "SHA-256");
        } catch (Exception e) {
            throw new EcpayException(ErrorMessage.GEN_CHECK_MAC_VALUE_FAIL);
        }
    }

    private final static String hash(byte data[], String mode) {
        MessageDigest md = null;
        try {
            if (mode == "MD5") {
                md = MessageDigest.getInstance("MD5");
            } else if (mode == "SHA-256") {
                md = MessageDigest.getInstance("SHA-256");
            }
        } catch (NoSuchAlgorithmException e) {
        }
        return bytesToHex(md.digest(data));
    }

    private static String netUrlEncode(String url) {
        String netUrlEncode = url.replaceAll("%21", "\\!").replaceAll("%28", "\\(").replaceAll("%29", "\\)");
        return netUrlEncode;
    }

    // public final static String genCheckMacValue(String key, String iv,
    // Map<String, String> params) throws Exception {
    // // 1. 依參數進行排序
    // Map<String, String> sortedParams = new TreeMap<>(params);

    // // 2. 將參數組裝成字串
    // StringBuilder sb = new StringBuilder();
    // sb.append("HashKey=").append(key);
    // for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
    // sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
    // }
    // sb.append("&HashIV=").append(iv);

    // System.out.println("組裝前字串: " + sb);

    // // 3. 使用自定義URL編碼
    // String encodedString = CustomUrlEncoder.encode(sb.toString()).toLowerCase();
    // System.out.println("URL Encode 後字串: " + encodedString);

    // // 4. 計算SHA256
    // return sha256(encodedString.getBytes(StandardCharsets.UTF_8)).toUpperCase();
    // }

    /**
     * URL Encode 方法，符合 RFC 1866 規範。
     */
    public static String urlEncode(String data) {
        String result = "";
        try {
            result = URLEncoder.encode(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }
        return result;
    }

    /**
     * 使用 SHA256 計算。
     */
    private static String sha256(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(bytes);
        return bytesToHex(digest);
    }

    /**
     * 將 byte[] 轉為十六進位字串。
     */
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // public final static String sendToEcpay(String url, Map<String, String>
    // params) {
    // StringBuilder postData = new StringBuilder();
    // for (Map.Entry<String, String> param : params.entrySet()) {
    // if (postData.length() > 0)
    // postData.append("&");
    // postData.append(param.getKey()).append("=").append(urlEncode(param.getValue()));
    // }

    // try {
    // java.net.URL endpoint = new java.net.URL(url);
    // java.net.HttpURLConnection connection = (java.net.HttpURLConnection)
    // endpoint.openConnection();
    // connection.setRequestMethod("POST");
    // connection.setRequestProperty("Content-Type",
    // "application/x-www-form-urlencoded;charset=UTF-8");
    // connection.setDoOutput(true);

    // java.io.OutputStream os = connection.getOutputStream();
    // os.write(postData.toString().getBytes(StandardCharsets.UTF_8));
    // os.flush();
    // os.close();

    // int responseCode = connection.getResponseCode();
    // java.io.BufferedReader br = new java.io.BufferedReader(new
    // java.io.InputStreamReader(
    // connection.getInputStream(), StandardCharsets.UTF_8));
    // String inputLine;
    // StringBuilder response = new StringBuilder();
    // while ((inputLine = br.readLine()) != null) {
    // response.append(inputLine);
    // }
    // br.close();
    // return response.toString();
    // } catch (Exception e) {
    // e.printStackTrace();
    // throw new RuntimeException("Error sending POST request", e);
    // }
    // }
}
// public static void main(String[] args) throws NoSuchAlgorithmException,
// UnsupportedEncodingException {
// // 測試資料
// String hashKey = "yourHashKey";
// String hashIV = "yourHashIV";

// Map<String, String> params = new HashMap<>();
// params.put("MerchantID", "3002607");
// params.put("MerchantTradeNo", "trade20240312");
// params.put("TotalAmount", "30000");
// params.put("TradeDesc", "促銷方案");
// params.put("ReturnURL", "https://www.ecpay.com.tw/receive.php");
// params.put("PaymentType", "aio");
// params.put("ChoosePayment", "ALL");

// String checkMacValue = genCheckMacValue(hashKey, hashIV, params);
// params.put("CheckMacValue", checkMacValue);

// String response =
// sendToEcpay("https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5",
// params);

// System.out.println("Response from Ecpay: " + response);
// }
// }

// package com.ispan.hestia.service;

// import java.io.UnsupportedEncodingException;
// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.util.Map;
// import java.util.TreeMap;

// public class EcpayFunction {

// public static String genCheckMacValue(String hashKey, String hashIV,
// Map<String, String> params)
// throws UnsupportedEncodingException, NoSuchAlgorithmException {
// // 1. 參數排序後組裝成字串
// StringBuilder paramStr = new StringBuilder("HashKey=" + hashKey);

// for (Map.Entry<String, String> entry : new TreeMap<>(params).entrySet()) {
// paramStr.append("&").append(entry.getKey()).append("=").append(entry.getValue());
// }

// paramStr.append("&HashIV=").append(hashIV);
// System.out.println("CheckMacValue: " + paramStr);

// // 2. URL Encode 並轉小寫
// String encodedStr = urlEncode(paramStr.toString()).toLowerCase();

// // 3. 使用 SHA256 進行雜湊
// MessageDigest digest = MessageDigest.getInstance("SHA-256");
// byte[] hash = digest.digest(encodedStr.getBytes(StandardCharsets.UTF_8));

// // 4. 將雜湊結果轉為大寫的十六進制字串
// StringBuilder hexString = new StringBuilder();
// for (byte b : hash) {
// String hex = Integer.toHexString(0xff & b);
// if (hex.length() == 1) {
// hexString.append('0');
// }
// hexString.append(hex);
// }

// return hexString.toString().toUpperCase();
// }

// public static String genCheckMacValueTest(String hashKey, String hashIV,
// Map<String, String> params)
// throws UnsupportedEncodingException, NoSuchAlgorithmException {
// // 1. 參數排序後組裝成字串
// StringBuilder paramStr = new StringBuilder("HashKey=" + hashKey);

// for (Map.Entry<String, String> entry : new TreeMap<>(params).entrySet()) {
// paramStr.append("&").append(entry.getKey()).append("=").append(entry.getValue());
// }

// paramStr.append("&HashIV=").append(hashIV);

// // 2. URL Encode 並轉小寫
// String encodedStr = urlEncode(paramStr.toString()).toLowerCase();

// // 3. 使用 SHA256 進行雜湊
// MessageDigest digest = MessageDigest.getInstance("SHA-256");
// byte[] hash = digest.digest(encodedStr.getBytes(StandardCharsets.UTF_8));

// // 4. 將雜湊結果轉為大寫的十六進制字串
// StringBuilder hexString = new StringBuilder();
// for (byte b : hash) {
// String hex = Integer.toHexString(0xff & b);
// if (hex.length() == 1) {
// hexString.append('0');
// }
// hexString.append(hex);
// }

// return hexString.toString().toUpperCase();
// }

// // URL Encode 方法
// private static String urlEncode(String str) throws
// UnsupportedEncodingException {
// return URLEncoder.encode(str, StandardCharsets.UTF_8.toString())
// .replace("+", "%20")
// .replace("*", "%2A")
// .replace("%7E", "~");
// }
// }
