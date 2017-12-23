package com.example.dfz.myapplication.MUtils;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.dfz.myapplication.R;
import com.vpaliy.last_fm_api.LastFm;
import com.vpaliy.last_fm_api.LastFmService;
import com.vpaliy.last_fm_api.model.Artist;
import com.vpaliy.last_fm_api.model.Image;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by DFZ on 2017/12/21.
 */

public class ArtistUtil {
    private static HashMap<Integer, ArrayList<String>> artistImages = new HashMap<>();
//    private static HashMap<Integer, ArrayList<String>> artistImages = new SparseArray<String>>;

    private static final String TAG = "ArtistUtil";
    public static void setArtistImage(Context context, String name, int id,ImageView imageView, int size) {
        LastFmService service = LastFm.create(LastFMUtil.API_KEY)
                .createService(context);

        if (artistImages.containsKey(id)){
            Glide.with(context).load(artistImages.get(id).get(size)).into(imageView);
        }else {
            Glide.with(context).load(R.color.cardview_dark_background).into(imageView);
            service.fetchArtist(name)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
                Artist a = response.result;
                if (a != null) {
                    Log.d(TAG, "setArtistImage: "+a.image.get(1).text);
                    Glide.with(context).load(a.image.get(2).text).into(imageView);
                    ArrayList<String> imageUrls = new ArrayList<>();
                    for (Image i : a.image){
                        imageUrls.add(i.text);
                    }
                    artistImages.put(id, imageUrls);
                }else {
                    Glide.with(context).clear(imageView);
                }

            });
        }


    }

}
