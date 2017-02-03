package com.vtt.smartkar;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseDeviceActivity extends AppCompatActivity{
    private static final String DEBUG_TAG = "ChooseDeviceActivity";
    private static final int REQUEST_CODE_ACTIVATE_BLUETOOTH = 1;
    private static final int REQUEST_DEVICE_LIST = 2;

    private BluetoothAdapter mBluetoothAdapter;
    @BindView(R.id.connect_button)
    protected Button mConnectButton;

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
        } else {
            showPossibleConnections();
        }
    }

    private void showPossibleConnections(){
        Log.i(DEBUG_TAG, "Connection button pressed");
        /*Set<BluetoothDevice> devicesList = mBluetoothAdapter.getBondedDevices();
        if(!devicesList.isEmpty()) {
            for(BluetoothDevice bluetoothDevice : devicesList){
                Log.i(DEBUG_TAG, "Bluetooth Device: " + bluetoothDevice.getAddress());
            }
        }*/
        Intent intent = new Intent(ChooseDeviceActivity.this, DeviceListActivity.class);
        startActivityForResult(intent, REQUEST_DEVICE_LIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ACTIVATE_BLUETOOTH) { //Intent that activate bluetooth
            if(resultCode == RESULT_OK) { //Bluetooth is now activated
                showPossibleConnections();
            }
        } if(requestCode == REQUEST_DEVICE_LIST){
            if(resultCode == RESULT_OK){
                Log.i(DEBUG_TAG, "Address: " + DeviceListActivity.getAddress(data));
            }
        } else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void conectWithDevice(String address){

    }
}