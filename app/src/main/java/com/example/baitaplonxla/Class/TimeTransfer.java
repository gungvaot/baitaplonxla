package com.example.baitaplonxla.Class;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeTransfer {
    public static String getTime(Long time) {
        // Lấy thời gian hiện tại tính bằng mili giây kể từ epoch
        time = System.currentTimeMillis();

        // Chuyển đổi thành Instant
        Instant instant = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            instant = Instant.ofEpochMilli(time);
        }

        // Chuyển đổi thành LocalDateTime với múi giờ hệ thống mặc định
        LocalDateTime dateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        }

        // Định dạng ngày giờ theo định dạng mong muốn
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return dateTime.format(formatter);
        }
        return null;
    }
}
