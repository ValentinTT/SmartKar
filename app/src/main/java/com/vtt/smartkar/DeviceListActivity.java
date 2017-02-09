package com.vtt.smartkar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by vtt on 01/02/17.
 */

public class DeviceListActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "DeviceListActivity";
    private static final String EXTRA_ADDRESS = "extraAddress";

    private RecyclerView mRecyclerView;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_device_list);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mRecyclerView = (RecyclerView) findViewById(R.id.fragment_device_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new DeviceAdapter(mBluetoothAdapter.getBondedDevices()));

    }

    public void setAddressResult(String address) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ADDRESS, address);
        setResult(RESULT_OK, data);
        finish();
    }

    public static String getAddress(Intent data) {
        return data.getStringExtra(EXTRA_ADDRESS);
    }


    private class DeviceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mDeviceName;
        private TextView mDeviceAddress;

        public DeviceHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_device_element, parent, false));

            mDeviceName = (TextView) itemView.findViewById(R.id.list_item_device_name);
            mDeviceAddress = (TextView) itemView.findViewById(R.id.list_item_device_address);
            itemView.setOnClickListener(this);
        }

        public void bindDevice(BluetoothDevice bluetoothDevice) {
            mDeviceName.setText(bluetoothDevice.getName());
            mDeviceAddress.setText(bluetoothDevice.getAddress());
        }

        @Override
        public void onClick(View v) {
            Log.i(DEBUG_TAG, mDeviceName.getText() + " clicked!");
            setAddressResult((String) mDeviceAddress.getText());
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










