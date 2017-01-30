package com.example.bleperipheral;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class SecondActivity extends ActionBarActivity {


    private static Context mContext;
    private Button mStartButton, mStopButton, mSendButton;
    private TextView blueboothMacAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mContext = this;
        Intent intent = new Intent(SecondActivity.this, AdvertisingService.class);
        startService(intent);

//        blueboothMacAddress=(TextView)findViewById(R.id.blueboothMacAddress);
//        mStartButton = (Button) findViewById(R.id.start);
//        mStartButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                mBluetoothLeAdvertiser.startAdvertising(createAdvSettings(true, 0), createAdvertiseData(), mAdvertiseCallback);
//                if (D) {
//                    Toast.makeText(mContext, "Start Advertising", Toast.LENGTH_LONG).show();
//                    Log.e(TAG,"Start Advertising");
//                }
//            }
//        });
//
//        mStopButton = (Button) findViewById(R.id.stop);
//        mStopButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
////                stopAdvertise();
//                if (D) {
//                    Toast.makeText(mContext, "Stop Advertising", Toast.LENGTH_LONG).show();
//                    Log.e(TAG, "Stop Advertising");
//                }
//            }
//        });
//
//        mSendButton = (Button) findViewById(R.id.send);
//        mSendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//
//                boolean b= character.setValue("send test hello".getBytes());
//                if (b){
//                    System.out.println("修改成功");
//                }else{
//                    System.out.println("修改！成功");
//                }
//                server.notifyCharacteristicChanged(mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER).get(0),character,false);
//
//
//
//
//            }
//        });

//        init();

//        if (mBluetoothAdapter.getState()==mBluetoothAdapter.STATE_ON)
//        {
//
//            blueboothMacAddress.setText(mBluetoothAdapter.getAddress());
//           Toast.makeText(SecondActivity.this,mBluetoothAdapter.getAddress(),Toast.LENGTH_LONG).show();
//        }

    }


}
