package com.example.dfz.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.dfz.myapplication.MUtils.LastFMUtil;
import com.vpaliy.last_fm_api.LastFm;
import com.vpaliy.last_fm_api.LastFmService;
import com.vpaliy.last_fm_api.model.Artist;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LastFMActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_fm);
        getSupportActionBar().setTitle("Last.fm");
        ImageView userImage = findViewById(R.id.userImage);

        LastFmService service= LastFm.create(LastFMUtil.API_KEY)
                .createService(this);

//        service.fetchUserInfo("")
//        service.fetchArtist("name of an artist")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(response -> {
//                    Artist artist=response.result;
//                });
////        Glide.with(this).load(uri).into(userImage);

    }


}
