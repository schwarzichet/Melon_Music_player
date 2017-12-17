package com.example.dfz.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.dfz.myapplication.MUtils.LastFMUtil;
import com.vpaliy.last_fm_api.LastFm;
import com.vpaliy.last_fm_api.LastFmService;
import com.vpaliy.last_fm_api.model.Artist;
import com.vpaliy.last_fm_api.model.User;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LastFMActivity extends AppCompatActivity {

    private static final String TAG = "LastFMActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_fm);
        getSupportActionBar().setTitle("Last.fm");
        ImageView userImage1 = findViewById(R.id.userImage);
        ImageView userImage2 = findViewById(R.id.userImage2);
        ImageView userImage3 = findViewById(R.id.userImage3);
        ImageView userImage4 = findViewById(R.id.userImage4);


        LastFmService service= LastFm.create(LastFMUtil.API_KEY)
                .createService(this);


        service.fetchUserInfo("Schwarzwald_D")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    User user=response.result;
                    Log.d(TAG, "onCreate: "+user.image.size());
                    Glide.with(this).load(user.image.get(0).text).into(userImage1);
                    Glide.with(this).load(user.image.get(1).text).into(userImage2);
                    Glide.with(this).load(user.image.get(2).text).into(userImage3);
                    Glide.with(this).load(user.image.get(3).text).into(userImage4);
                    Log.d(TAG, "onCreate: 0"+user.image.get(0).size);
                    Log.d(TAG, "onCreate: 1"+user.image.get(1).size);
                    Log.d(TAG, "onCreate: 2"+user.image.get(2).size);
                    Log.d(TAG, "onCreate: 3"+user.image.get(3).size);

                });

    }


}
