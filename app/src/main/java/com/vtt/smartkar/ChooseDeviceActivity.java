package com.vtt.smartkar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseDeviceActivity extends AppCompatActivity{
    private static final String DEBUG_TAG = "ChooseDeviceActivity";

    @OnClick(R.id.connect_button)
    public void tryConnection(){
        Log.i(DEBUG_TAG, "Connection button pressed");
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);
        ButterKnife.bind(this);
    }
}