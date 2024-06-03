package com.example.baitaplonxla.Class;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.io.IOException;
import java.io.OutputStream;

public class ImageProcessor {
    @NonNull
    public static Mat bitmatToMat(Bitmap bitmap) {
        Mat mat = new Mat();

        // Chuyển đổi Bitmap thành Mat sử dụng Utils của OpenCV
        Utils.bitmapToMat(bitmap, mat);

        return mat;
    }

    public static Bitmap uriToBitmap(Context context, Uri uri) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return bitmap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveImage(@NonNull Context context, @NonNull Bitmap bitmap, @NonNull String title) {
        Uri imageUri = null;
        OutputStream fos;

        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures");
            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri collection;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            } else {
                collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }

            imageUri = context.getContentResolver().insert(collection, values);
            if (imageUri == null) {
                throw new IOException("Failed to create new MediaStore record.");
            }

            fos = context.getContentResolver().openOutputStream(imageUri);
            if (fos == null) {
                throw new IOException("Failed to get output stream.");
            }

            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                throw new IOException("Failed to save bitmap.");
            }

            fos.close();

            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, false);
            context.getContentResolver().update(imageUri, values, null, null);

        } catch (IOException e) {
            if (imageUri != null) {
                context.getContentResolver().delete(imageUri, null, null);
            }
            e.printStackTrace();
        }
    }

    public Bitmap filterNLM(Mat inputMat, int h, int hcolor, int templateWindowSie, int searchWindowSize) {
        Mat resultMat = new Mat();
        Photo.fastNlMeansDenoisingColored(inputMat, resultMat, h, hcolor, templateWindowSie, searchWindowSize);

        // Chuyển đổi kết quả thành ảnh bitmap
        Bitmap resultBitmap = Bitmap.createBitmap(resultMat.cols(), resultMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(resultMat, resultBitmap);

        return resultBitmap;
    }

    public Bitmap filterBilateral(Mat input, int d, int sigmaColor, int sigmaSpace) {
        //chuyển RGBA sang RGB
        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGRA2BGR);

        Mat resultMat = new Mat();
        Imgproc.bilateralFilter(input, resultMat, d, sigmaColor, sigmaSpace);

        //chuyển RGB sang RGBA
        Imgproc.cvtColor(resultMat, resultMat, Imgproc.COLOR_RGB2RGBA);

        Bitmap resultBitmap = Bitmap.createBitmap(resultMat.cols(), resultMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(resultMat, resultBitmap);

        return resultBitmap;
    }
}
