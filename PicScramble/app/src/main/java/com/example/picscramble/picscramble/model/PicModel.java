package com.example.picscramble.picscramble.model;

/**
 * Created by abande on 12/3/16.
 */
public class PicModel {
    public int getIdimage() {
        return id_image;
    }

    public void setIdimage(int id_image) {
        this.id_image = id_image;
    }

    private int id_image;

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    private boolean isFlipped;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    private String imageURL;
}
