package com.example.dfz.myapplication.MUtils;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.dfz.myapplication.R;
import com.vpaliy.last_fm_api.LastFm;
import com.vpaliy.last_fm_api.LastFmService;
import com.vpaliy.last_fm_api.model.Artist;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by DFZ on 2017/12/21.
 */

public class ArtistUtil {
    private static HashMap<String, String> artistImages = new HashMap<>();
    private static final String TAG = "ArtistUtil";
    public static void setArtistImage(Context context, Fragment fragment, String name, ImageView imageView, int position) {
        LastFmService service = LastFm.create(LastFMUtil.API_KEY)
                .createService(context);

        if (artistImages.containsKey(name)){
            Glide.with(fragment).load(artistImages.get(name)).into(imageView);
        }else {
            Glide.with(fragment).load(R.color.cardview_dark_background).into(imageView);
            service.fetchArtist(name)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(response -> {
                Artist a = response.result;
                if (a != null) {
//                        int tag = (int) imageView.getTag();
//                        if (tag==position){
//                            Log.d(TAG, "setArtistImage: "+a.image.get(1).text);
//                            Glide.with(fragment).load(a.image.get(2).text).into(imageView);
//                        }else {
//                            Glide.with(fragment).clear(imageView);
//                        }
                    Log.d(TAG, "setArtistImage: "+a.image.get(1).text);
                    Glide.with(fragment).load(a.image.get(2).text).into(imageView);
                    artistImages.put(name, a.image.get(2).text);
                }else {
                    Glide.with(fragment).clear(imageView);
                }

            });
        }


    }

}
