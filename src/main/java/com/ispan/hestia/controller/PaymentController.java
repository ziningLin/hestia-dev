package com.ispan.hestia.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.service.EcpayFunction;

@CrossOrigin
@RestController
@RequestMapping("/payment")
public class PaymentController {
    private static final String HASH_KEY = "pwFHCqoQZGmho4w6"; // 綠界 串接金鑰HashKey
    private static final String HASH_IV = "EkRm7iFT261dpevs";

    @PostMapping("/generateCheckMacValue")
    public ResponseEntity<Map<String, String>> generateCheckMacValue(@RequestBody Map<String, String> requestParams) {
        try {
            // 使用 TreeMap 進行參數排序

            requestParams.forEach((key, value) -> {
                System.out.println("requestParams: Character: '" + key + "' -> Encoding: " + value);
            });
            Map<String, String> sortedParams = new TreeMap<>(requestParams);
            sortedParams.forEach((key, value) -> {
                System.out.println("sortedParams: Character: '" + key + "' -> Encoding: " + value);
            });

            // 生成 CheckMacValue
            String checkMacValue = EcpayFunction.genCheckMacValue(HASH_KEY, HASH_IV, sortedParams).toUpperCase();

            // 將檢查碼加入回傳參數
            sortedParams.put("CheckMacValue", checkMacValue);

            return ResponseEntity.ok(sortedParams);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "生成檢查碼失敗: " + e.getMessage()));
        }
    }

    @PostMapping("/getTradeNumber")
    public ResponseEntity<Map<String, Object>> getTradeNumber(@RequestBody String entity) {
        Map<String, Object> response = new HashMap<>();
        try {
            JSONObject obj = new JSONObject(entity);
            Integer orderId = obj.isNull("orderId") ? null : obj.getInt("orderId");
            String formattedId = String.format("A%06d", orderId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
            String tradeNumber = formattedId + "D" + sdf.format(new Date());
            System.out.println(tradeNumber);

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            String formattedDate = now.format(formatter);

            response.put("success", true);
            response.put("message", "成功獲得訂單編號");
            response.put("tradeNumber", tradeNumber);
            response.put("tradeDate", formattedDate);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取訂單編號失敗");
            return ResponseEntity.ok(response);
        }

    }

}
