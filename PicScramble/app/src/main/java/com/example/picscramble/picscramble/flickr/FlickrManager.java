package com.example.picscramble.picscramble.flickr;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abande on 12/4/16.
 */

public class FlickrManager {

    // String to create Flickr API urls
    private static final String FLICKR_BASE_URL = "https://api.flickr.com/services/rest/?method=";
    private static final String FLICKR_PHOTOS_SEARCH_STRING = "flickr.photos.search";
    private static final int FLICKR_PHOTOS_SEARCH_ID = 1;
     private static final int NUMBER_OF_PHOTOS = 9;

    //You can set here your API_KEY
    private static final String APIKEY_SEARCH_STRING = "&api_key=3e1b75c88760ebee0d72edaee789fd64";

    private static final String TAGS_STRING = "&tags=";
    private static final String FORMAT_STRING = "&format=json";



    private static String createURL(int methodId, String parameter) {
        String method_type = "";
        String url = null;
        switch (methodId) {
            case FLICKR_PHOTOS_SEARCH_ID:
                method_type = FLICKR_PHOTOS_SEARCH_STRING;
                url = FLICKR_BASE_URL + method_type + APIKEY_SEARCH_STRING + TAGS_STRING + parameter + FORMAT_STRING + "&per_page="+NUMBER_OF_PHOTOS+"&media=photos";
                break;
        }
        return url;
    }

    // http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg


    public static List<String> searchImagesByTag(String tag) {
        List<String> imageURL = new ArrayList<>(9);;
        String url = createURL(FLICKR_PHOTOS_SEARCH_ID, tag);

        String jsonString = null;
        try {

                ByteArrayOutputStream baos = URLConnector.readBytes(url);
                jsonString = baos.toString();

            try {

                JSONObject root = new JSONObject(jsonString.replace("jsonFlickrApi(", "").replace(")", ""));
                JSONObject photos = root.getJSONObject("photos");
                JSONArray imageJSONArray = photos.getJSONArray("photo");
                for (int i = 0; i < imageJSONArray.length(); i++) {

                    JSONObject item = imageJSONArray.getJSONObject(i);
                    imageURL.add(createPhotoURL(item.getString("id"), item.getString("owner"), item.getString("secret"), item.getString("server"),
                            item.getString("farm")));
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (NullPointerException nue) {
            nue.printStackTrace();
        }

        return imageURL;
    }

    private static String createPhotoURL(String id, String owner, String secret, String server, String farm) {
        String tmp = null;
        tmp = "http://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret;// +".jpg"
        tmp += "_z.jpg";
        return tmp;
    }

}
