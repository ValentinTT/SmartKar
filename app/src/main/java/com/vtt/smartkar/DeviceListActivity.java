package com.vtt.smartkar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by vtt on 01/02/17.
 */

public class DeviceListActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "DeviceListActivity";
    private static final String EXTRA_ADDRESS = "extraAddress";

    private RecyclerView mRecyclerView;
    private BluetoothAdapter mBluetoothAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_device_list);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mRecyclerView = (RecyclerView) findViewById(R.id.fragment_device_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new DeviceAdapter(mBluetoothAdapter.getBondedDevices()));

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void setAddressResult(String address) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ADDRESS, address);
        setResult(RESULT_OK, data);
        finish();
    }

    public void connectBluetooth(BluetoothDevice device) {
        Intent intent = new Intent(DeviceListActivity.this, TestActivity.class);
        intent.putExtra("address", device.getAddress());
        startActivity(intent);
    }

    public static String getAddress(Intent data) {
        return data.getStringExtra(EXTRA_ADDRESS);
    }


    private class DeviceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mDeviceName;
        private TextView mDeviceAddress;
        private BluetoothDevice mBluetoothDevice;

        public DeviceHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_device_element, parent, false));

            mDeviceName = (TextView) itemView.findViewById(R.id.list_item_device_name);
            mDeviceAddress = (TextView) itemView.findViewById(R.id.list_item_device_address);
            itemView.setOnClickListener(this);
        }

        public void bindDevice(BluetoothDevice bluetoothDevice) {
            mBluetoothDevice = bluetoothDevice;
            mDeviceName.setText(bluetoothDevice.getName());
            mDeviceAddress.setText(bluetoothDevice.getAddress());
        }

        @Override
        public void onClick(View v) {
            Log.i(DEBUG_TAG, mDeviceName.getText() + " clicked!");
            //setAddressResult((String) mDeviceAddress.getText());
            connectBluetooth(mBluetoothDevice);
        }
    }

    private class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder> {
        private List<BluetoothDevice> mBluetoothDevices;

        public DeviceAdapter(Set<BluetoothDevice> bluetoothDevices) {
            mBluetoothDevices = new ArrayList<>(bluetoothDevices);
        }

        @Override
        public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getBaseContext());
            return new DeviceHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(DeviceHolder holder, int position) {
            holder.bindDevice(mBluetoothDevices.get(position));
        }

        @Override
        public int getItemCount() {
            return mBluetoothDevices.size();
        }
    }
}










