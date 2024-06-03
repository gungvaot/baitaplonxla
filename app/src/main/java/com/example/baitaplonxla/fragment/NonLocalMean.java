package com.example.baitaplonxla.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.baitaplonxla.R;
import com.example.baitaplonxla.Class.ImageProcessor;

import org.opencv.core.Mat;

public class NonLocalMean extends Fragment {

    private Mat mat;
    private static Bitmap src_image;
    private ImageProcessor imageProcessor;
    private OnImageProcessedListener mListener;

    public NonLocalMean() {
    }

    public void setBitmap(Bitmap bitmap) {
        src_image = bitmap;
    }

    public interface OnImageProcessedListener {
        void onImageProcessed(Bitmap processedImage, String title, double time);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnImageProcessedListener) {
            mListener = (OnImageProcessedListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnImageProcessedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_non_local_mean, container, false);

        imageProcessor = new ImageProcessor();

        EditText edtH = view.findViewById(R.id.edtH);
        EditText edtHcolor = view.findViewById(R.id.edtHcolor);
        EditText edtTextTemplate = view.findViewById(R.id.edtTextTemplate);
        EditText edtSearch = view.findViewById(R.id.edtSearch);

        int h = edtH.getText().toString().isEmpty() ? 10 : Integer.parseInt(edtH.getText().toString());
        int hcolor = edtH.getText().toString().isEmpty() ? 10 : Integer.parseInt(edtHcolor.getText().toString());
        int template = edtH.getText().toString().isEmpty() ? 7 : Integer.parseInt(edtTextTemplate.getText().toString());
        int search = edtH.getText().toString().isEmpty() ? 21 : Integer.parseInt(edtSearch.getText().toString());

        Button btnNLM = view.findViewById(R.id.btnNLM);
        btnNLM.setOnClickListener(v -> {
            if (mListener != null && src_image != null) {
                mat = ImageProcessor.bitmatToMat(src_image);
                double startTime = System.nanoTime();
                Bitmap resultImage = imageProcessor.filterNLM(mat, h, hcolor, template, search);
                double endTime = System.nanoTime();
                double MethodeDuration = (endTime - startTime);
                mListener.onImageProcessed(resultImage, "Áp dụng Non-local Means thành công", MethodeDuration);
            } else {
                Toast.makeText(requireContext(), "Hãy chọn 1 ảnh để xử lý", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


}