package com.example.baitaplonxla.Class;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private final Context context;
    public int status;

    public PermissionManager(Context context) {
        this.context = context;
    }

    public void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            status = 1;
        } else {
            requestPermission();
        }
    }

    public void requestPermission() {
        // Yêu cầu quyền trực tiếp từ người dùng.
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    public void showPermissionDeniedMessage() {
        // Hiển thị thông báo cho người dùng biết rằng quyền đã bị từ chối và hành động không thể thực hiện.
        Toast.makeText(context, "Ứng dụng không thể truy cập vào ảnh mà bạn lưu trữ trên thiết bị.", Toast.LENGTH_SHORT).show();
    }
}

