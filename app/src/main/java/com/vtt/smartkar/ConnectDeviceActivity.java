package com.vtt.smartkar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectDeviceActivity extends AppCompatActivity{
    private static final String DEBUG_TAG = "ChooseDeviceActivity";
    private static final int REQUEST_CODE_ACTIVATE_BLUETOOTH = 1;
    private static final int REQUEST_DEVICE_LIST = 2;
    private static final String KEY_ADDRESS = "keyAddress";

    @BindView(R.id.connect_button)
    protected Button mConnectButton;
    @BindView(R.id.with_connection_text_view)
    protected TextView mWithConnection;

    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);
        ButterKnife.bind(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) { //This means the mobile doesn't have bluetooth.
            mConnectButton.setEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No bluetooth");
            builder.setMessage("Your device doesn't have bluetooth");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryConnection();
            }
        });

        mWithConnection.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showPossibleConnections();
            }
        });

        if(savedInstanceState != null)
            mBluetoothAddress = savedInstanceState.getString(KEY_ADDRESS);
        else
            mBluetoothAddress = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(DEBUG_TAG, "onSaveInstanceState");
        if(mBluetoothAddress != null)
            outState.putString(KEY_ADDRESS, mBluetoothAddress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ACTIVATE_BLUETOOTH) { //Intent that activate bluetooth
            if(resultCode == RESULT_OK) { //Bluetooth is now activated
                showPossibleConnections();
            }
        } if(requestCode == REQUEST_DEVICE_LIST){
            if(resultCode == RESULT_OK){
                mBluetoothAddress = DeviceListActivity.getAddress(data);
                Log.i(DEBUG_TAG, "Address: " + mBluetoothAddress);
                mWithConnection.setText("With\n" + mBluetoothAdapter.getRemoteDevice(mBluetoothAddress).getName());
            }
        } else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showSnackBar(String msg) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
        Log.i(DEBUG_TAG, msg);
    }

    private BluetoothDevice getDevice(String address) {
        return mBluetoothAdapter.getRemoteDevice(address);
    }

    private void tryConnection(){
        if(!mBluetoothAdapter.isEnabled()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Activate bluetooth");
            builder.setMessage("Bluetooth is disabled. Do you want to enable it?");
            builder.setNegativeButton("No", null);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent BluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(BluetoothIntent, REQUEST_CODE_ACTIVATE_BLUETOOTH);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if(mWithConnection.getText().toString().equals("With")) {
            showPossibleConnections();
        } else {
            starConnectionActivity();
        }
    }

    private void showPossibleConnections(){
        Log.i(DEBUG_TAG, "Connection button pressed");
        Intent intent = new Intent(ConnectDeviceActivity.this, DeviceListActivity.class);
        startActivityForResult(intent, REQUEST_DEVICE_LIST);
    }

    private void starConnectionActivity(){
        Intent intent = ControlActivity.newIntent(ConnectDeviceActivity.this, mBluetoothAddress);
        startActivityForResult(intent, 2);
    }
}