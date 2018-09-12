package com.lzwap.android.photogallery;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoBean {

    @SerializedName("photos")
    private PhotosInfo mPhotoInfo;

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
}
