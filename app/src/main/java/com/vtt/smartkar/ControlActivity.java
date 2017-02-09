package com.vtt.smartkar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vtt on 04/02/17.
 */

public class ControlActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "ControlActivity";
    private static final String EXTRA_ADDRESS = "extraAddress";
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @BindView(R.id.interaction_automatic_checkbox)
    protected CheckBox mAutomatic;
    @BindView(R.id.interaction_static_controls_checkbox)
    protected CheckBox mStaticControls;
    @BindView(R.id.interaction_speed_seekbar)
    protected SeekBar mSpeed;
    @BindView(R.id.up_control_button)
    protected Button mUpButton;
    @BindView(R.id.down_control_button)
    protected Button mDownButton;
    @BindView(R.id.left_control_button)
    protected Button mLeftButton;
    @BindView(R.id.right_control_button)
    protected Button mRightButton;
    @BindView(R.id.stop_control_button)
    protected Button mStopButton;

    private BluetoothSocket mBluetoothSocket = null;
    private String mAddress;
    private AsyncBluetooth mAsyncBluetooth;

    public static Intent newIntent(Activity activity, String address){
        Intent intent = new Intent(activity, ControlActivity.class);
        intent.putExtra(EXTRA_ADDRESS, address);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction);

        ButterKnife.bind(this);

        /*
        mUpButton.setOnClickListener(new OnClickButtonListener("u"));
        mDownButton.setOnClickListener(new OnClickButtonListener("d"));
        mLeftButton.setOnClickListener(new OnClickButtonListener("l"));
        mRightButton.setOnClickListener(new OnClickButtonListener("r"));
        */
        mUpButton.setOnTouchListener(new OnTouchButtonListener("u", "s"));
        mDownButton.setOnTouchListener(new OnTouchButtonListener("d", "s"));
        mLeftButton.setOnTouchListener(new OnTouchButtonListener("l", "s"));
        mRightButton.setOnTouchListener(new OnTouchButtonListener("r", "s"));
        mStopButton.setOnClickListener(new OnClickButtonListener("s"));

        mStaticControls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mUpButton.setOnTouchListener(null);
                    mDownButton.setOnTouchListener(null);
                    mLeftButton.setOnTouchListener(null);
                    mRightButton.setOnTouchListener(null);

                    mUpButton.setOnClickListener(new OnClickButtonListener("u"));
                    mDownButton.setOnClickListener(new OnClickButtonListener("d"));
                    mLeftButton.setOnClickListener(new OnClickButtonListener("l"));
                    mRightButton.setOnClickListener(new OnClickButtonListener("r"));
                } else {
                    mUpButton.setOnClickListener(null);
                    mDownButton.setOnClickListener(null);
                    mLeftButton.setOnClickListener(null);
                    mRightButton.setOnClickListener(null);;

                    mUpButton.setOnTouchListener(new OnTouchButtonListener("u", "s"));
                    mDownButton.setOnTouchListener(new OnTouchButtonListener("d", "s"));
                    mLeftButton.setOnTouchListener(new OnTouchButtonListener("l", "s"));
                    mRightButton.setOnTouchListener(new OnTouchButtonListener("r", "s"));
                }
            }
        });

        mSpeed.setMax(150);
        mSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendCommand("v");
                sendCommand((seekBar.getProgress() + 100) + "\n");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAddress = getIntent().getStringExtra(EXTRA_ADDRESS);
        mAsyncBluetooth = new AsyncBluetooth();
        mAsyncBluetooth.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAsyncBluetooth.cancel(true);
        try {
            mBluetoothSocket.close();
            mBluetoothSocket = null;
            Log.i(DEBUG_TAG, "Socket destroyed");
            finish();
        } catch (IOException e) {
            Log.i(DEBUG_TAG, "Fail to close socket");
        }
    }

    protected void sendCommand(String text) {
        if (mBluetoothSocket != null)
            try {
                mBluetoothSocket.getOutputStream().write(text.getBytes());
                Log.i(DEBUG_TAG, "" + text);
            } catch (IOException e) {
                showMessage("Could not send the message: " + text);
            }
    }

    private void showMessage(String msg){
        Snackbar.make(findViewById(android.R.id.content), msg.trim(), Snackbar.LENGTH_SHORT).show();
        Log.i(DEBUG_TAG, msg);
    }

    private class OnTouchButtonListener implements View.OnTouchListener {
        private String mDownCommandMessage;
        private String mUpCommandMessage;

        public OnTouchButtonListener(String downCommandMessage, String upCommandMessage) {
            mDownCommandMessage = downCommandMessage;
            mUpCommandMessage = upCommandMessage;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                sendCommand(mDownCommandMessage);
            } else if(event.getAction() == MotionEvent.ACTION_UP) {
                sendCommand(mUpCommandMessage);
            }
            return true;
        }
    }

    private class OnClickButtonListener implements View.OnClickListener {
        private String mCommandMessage;

        public OnClickButtonListener(String commandMessage) {
            mCommandMessage = commandMessage;
        }

        @Override
        public void onClick(View v) {
            sendCommand(mCommandMessage);
        }
    }

    private class AsyncBluetooth extends AsyncTask<Void, Void, Void> {
        private boolean mConnectionDone;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (mBluetoothSocket == null) {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(mAddress);

                    mBluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    bluetoothAdapter.cancelDiscovery();
                    mBluetoothSocket.connect();
                    Log.i(DEBUG_TAG, bluetoothDevice.getType()+"");
                }
            } catch (IOException e){
                showMessage("FAIL TO CONNECT SERVER");
                mConnectionDone = false;
            }
            return null;
        }

        public AsyncBluetooth() {
            mConnectionDone = true;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!mConnectionDone) {
                showMessage("NO CONNECTED");
            } else {
                showMessage("Connection succeed");
            }
        }
    }
}
















