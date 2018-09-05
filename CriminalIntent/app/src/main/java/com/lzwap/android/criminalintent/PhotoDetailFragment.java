package com.lzwap.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import android.support.v4.app.DialogFragment;

public class PhotoDetailFragment extends DialogFragment {

    private static final String ARG_FILE = "file";

    private ImageView mPhotoView;

    public static PhotoDetailFragment newInstance(File file) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILE, file);

        PhotoDetailFragment fragment = new PhotoDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        File file = (File) getArguments().getSerializable(ARG_FILE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo_detail);

        //在真机上下面两种方式有区别，应该与拍摄照片像素有关
        Bitmap bitmap = PictureUtils.getScaledBitmap(file.getPath(), getActivity());
        //Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        mPhotoView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
