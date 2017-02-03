package com.vtt.smartkar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "TestActivity";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @BindView(R.id.text_test)
    protected TextView mTextView;
    private BluetoothSocket mBluetoothSocket = null;
    private BluetoothDevice mBluetoothDevice;
    private String mAddress;
    private AsyncBluetooth mAsyncBluetooth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAddress = getIntent().getStringExtra("address");
        mAsyncBluetooth = new AsyncBluetooth();
        mAsyncBluetooth.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAsyncBluetooth.cancel(true);
        try {
            mBluetoothSocket.close();
            Log.i(DEBUG_TAG, "Socket destroyed");
        } catch (IOException e) {
            Log.i(DEBUG_TAG, "Fail to close socket");
        }
    }

    @OnClick(R.id.connection_check_button)
    protected void checkConnectionState(){
    }

    @OnClick(R.id.send_test)
    protected void sendText() {
        try {
            mBluetoothSocket.getOutputStream().write(mTextView.getText().toString().getBytes());
        } catch (IOException e) {
            showMessage("Could not send the message: " + mTextView.getText().toString());
        }
        mTextView.setText("");
    }

    private void showMessage(String msg){
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show();
        Log.i(DEBUG_TAG, msg);
    }

    private class AsyncBluetooth extends AsyncTask<Void, Void, Void>{
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
