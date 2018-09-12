package com.lzwap.android.photogallery;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoBean {

    public static final String STATUS_OK = "ok";
    public static final String STATUS_FAILED = "fail";

    @SerializedName("photos")
    private PhotosInfo mPhotoInfo;
    @SerializedName("stat")
    private String mStatus;
    @SerializedName("message")
    private String mMessage;

    public class PhotosInfo {
        @SerializedName("photo")
        List<GalleryItem> mPhoto;

        public List<GalleryItem> getPhoto() {
            return mPhoto;
        }

        public void setPhoto(List<GalleryItem> photo) {
            mPhoto = photo;
        }
    }

    public PhotosInfo getPhotoInfo() {
        return mPhotoInfo;
    }

    public void setPhotoInfo(PhotosInfo photoInfo) {
        mPhotoInfo = photoInfo;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }
}
