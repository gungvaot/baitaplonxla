package com.example.baitaplonxla.activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.baitaplonxla.R;
import com.example.baitaplonxla.Class.ImageProcessor;
import com.example.baitaplonxla.Class.ScreenSlidePagerAdapter;
import com.example.baitaplonxla.Class.TimeTransfer;
import com.example.baitaplonxla.fragment.Bilateral;
import com.example.baitaplonxla.fragment.NonLocalMean;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.util.Objects;

public class StartApp extends AppCompatActivity implements NonLocalMean.OnImageProcessedListener, Bilateral.OnImageProcessedListener {

    private ImageView imageView;
    private NonLocalMean nonLocalMean;
    private Bilateral bilateral;
    private Uri src_image;
    private Bitmap save_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolBarMain));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        imageView = findViewById(R.id.imageView);
        nonLocalMean = new NonLocalMean();
        bilateral = new Bilateral();

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ScreenSlidePagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Non-local means");
                    break;
                case 1:
                    tab.setText("Bilateral");
                    break;
            }
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_app, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemHome) {
            finish();
        } else if (item.getItemId() == R.id.itemAdd) {
            getImageLauncher.launch("image/*");
        } else if (item.getItemId() == R.id.itemSave) {
            if (src_image != null && save_image != null) {
                String time = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                        TimeTransfer.getTime(System.currentTimeMillis()) : String.valueOf(System.currentTimeMillis());
                ImageProcessor.saveImage(this, save_image, "saved image at " + time);
                Toast.makeText(this, "Lưu ảnh thành công", Toast.LENGTH_SHORT).show();
            } else if (src_image != null) {
                String time = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ?
                        TimeTransfer.getTime(System.currentTimeMillis()) : String.valueOf(System.currentTimeMillis());
                ImageProcessor.saveImage(this, ImageProcessor.uriToBitmap(this, src_image), "saved image at " + time);
                Toast.makeText(this, "Lưu ảnh thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hãy chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private final ActivityResultLauncher<String> getImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null) {
            imageView.setImageURI(uri);
            src_image = uri;
            Bitmap bitmap_src_map = ImageProcessor.uriToBitmap(this, uri);
            nonLocalMean.setBitmap(bitmap_src_map);
            bilateral.setBitmap(bitmap_src_map);
        }
    });

    @Override
    public void onImageProcessed(Bitmap processedImage, String title, double time) {
        imageView.setImageBitmap(processedImage);
        Log.e("Time", time / 1000000000 + "s");
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
        save_image = processedImage;
        nonLocalMean.setBitmap(processedImage);
        bilateral.setBitmap(processedImage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Xóa tệp ảnh tạm thời khi Activity bị hủy
        if (src_image != null) {
            File file = new File(Objects.requireNonNull(src_image.getPath()));
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    Log.e("TAG", "không xóa được file");
                } else {
                    Log.e("TAG", "xóa được file");
                }
            }
        }
    }
}