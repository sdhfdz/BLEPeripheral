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
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class SecondActivity extends ActionBarActivity {
    public static final String TAG="bleperipheral";
    private  static boolean D = true;

    public static String serviceUUID = "039AFFF0-2C94-11E3-9E06-0002A5D5C51B";
    public static String characteristicUUID = "039AFFA1-2C94-11E3-9E06-0002A5D5C51B";

    private BluetoothManager mBluetoothManager;

    private BluetoothGattServer server;
    private BluetoothGattCharacteristic character;
    private BluetoothGattService service;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private  static Context mContext;
    private Button mStartButton, mStopButton, mSendButton;
    private TextView blueboothMacAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mContext = this;

        blueboothMacAddress=(TextView)findViewById(R.id.blueboothMacAddress);
        mStartButton = (Button) findViewById(R.id.start);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mBluetoothLeAdvertiser.startAdvertising(createAdvSettings(true, 0), createAdvertiseData(), mAdvertiseCallback);
                if (D) {
                    Toast.makeText(mContext, "Start Advertising", Toast.LENGTH_LONG).show();
                    Log.e(TAG,"Start Advertising");
                }
            }
        });

        mStopButton = (Button) findViewById(R.id.stop);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                stopAdvertise();
                if (D) {
                    Toast.makeText(mContext, "Stop Advertising", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Stop Advertising");
                }
            }
        });

        mSendButton = (Button) findViewById(R.id.send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                boolean b= character.setValue("send test hello".getBytes());
                if (b){
                    System.out.println("修改成功");
                }else{
                    System.out.println("修改！成功");
                }
                server.notifyCharacteristicChanged(mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER).get(0),character,false);




            }
        });

        init();

        setServer();
        if (mBluetoothAdapter.getState()==mBluetoothAdapter.STATE_ON)
        {

            blueboothMacAddress.setText(mBluetoothAdapter.getAddress());
           Toast.makeText(SecondActivity.this,mBluetoothAdapter.getAddress(),Toast.LENGTH_LONG).show();
        }

    }

    private void init(){
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
            finish();
        }

        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
//        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter ==  null){
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_LONG).show();
            finish();
        }

        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        if(mBluetoothLeAdvertiser == null){
            Toast.makeText(this, "the device not support peripheral", Toast.LENGTH_SHORT    ).show();
            Log.e(TAG, "the device not support peripheral");
            finish();
        }



    }

    private void setServer() {
        character = new BluetoothGattCharacteristic(
                UUID.fromString(characteristicUUID),
                BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE|BluetoothGattCharacteristic.PROPERTY_NOTIFY);
        character.setValue("send test".getBytes());

        service = new BluetoothGattService(UUID.fromString(serviceUUID),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        service.addCharacteristic(character);

        server = mBluetoothManager.openGattServer(this,
                new BluetoothGattServerCallback() {
                    @Override
                    public void onNotificationSent(BluetoothDevice device, int status) {
                        super.onNotificationSent(device, status);
                        if (status== BluetoothGatt.GATT_SUCCESS)
                        System.out.println("发送通知成功！");
                    }

                    @Override
                    public void onConnectionStateChange(BluetoothDevice device,
                                                        int status, int newState) {
                        super.onConnectionStateChange(device, status, newState);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SecondActivity.this,"连接状态变化",Toast.LENGTH_LONG).show();

                            }
                        });
                        Log.d("Chris", "onConnectionStateChange:"+device+"-=-=-=-="+status+"-=-=-=-"+newState);
                    }

                    @Override
                    public void onServiceAdded(int status,
                                               BluetoothGattService service) {
                        super.onServiceAdded(status, service);
                        Log.d("Chris", "service added");
                    }

                    @Override
                    public void onCharacteristicReadRequest(
                            BluetoothDevice device, int requestId, int offset,
                            BluetoothGattCharacteristic characteristic) {
                        super.onCharacteristicReadRequest(device, requestId,
                                offset, characteristic);
                        Log.d("Chris", "onCharacteristicReadRequest");
                    }

                    @Override
                    public void onCharacteristicWriteRequest(
                            BluetoothDevice device, int requestId,
                            BluetoothGattCharacteristic characteristic,
                            boolean preparedWrite, boolean responseNeeded,
                            int offset, byte[] value) {
                        super.onCharacteristicWriteRequest(device, requestId,
                                characteristic, preparedWrite, responseNeeded,
                                offset, value);
                        Log.d("Chris", "onCharacteristicWriteRequest");
                    }

                    @Override
                    public void onDescriptorReadRequest(BluetoothDevice device,
                                                        int requestId, int offset,
                                                        BluetoothGattDescriptor descriptor) {
                        super.onDescriptorReadRequest(device, requestId,
                                offset, descriptor);
                        Log.d("Chris", "onDescriptorReadRequest");
                    }

                    @Override
                    public void onDescriptorWriteRequest(
                            BluetoothDevice device, int requestId,
                            BluetoothGattDescriptor descriptor,
                            boolean preparedWrite, boolean responseNeeded,
                            int offset, byte[] value) {
                        super.onDescriptorWriteRequest(device, requestId,
                                descriptor, preparedWrite, responseNeeded,
                                offset, value);
                        Log.d("Chris", "onDescriptorWriteRequest");
                    }

                    @Override
                    public void onExecuteWrite(BluetoothDevice device,
                                               int requestId, boolean execute) {
                        super.onExecuteWrite(device, requestId, execute);
                        Log.d("Chris", "onExecuteWrite");
                    }

                });

        server.addService(service);
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            if (settingsInEffect != null) {
                Log.d(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode()
                        + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.e(TAG, "onStartSuccess, settingInEffect is null");
            }
            Log.e(TAG,"onStartSuccess settingsInEffect" + settingsInEffect);

        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            if(D)   Log.e(TAG,"onStartFailure errorCode" + errorCode);

            if(errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE){
                if(D){
                    Toast.makeText(mContext, R.string.advertise_failed_data_too_large, Toast.LENGTH_LONG).show();
                    Log.e(TAG,"Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
                }
            }else if(errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS){
                if(D){
                    Toast.makeText(mContext, R.string.advertise_failed_too_many_advertises, Toast.LENGTH_LONG).show();
                    Log.e(TAG,"Failed to start advertising because no advertising instance is available.");
                }
            }else if(errorCode == ADVERTISE_FAILED_ALREADY_STARTED){
                if(D){
                    Toast.makeText(mContext, R.string.advertise_failed_already_started, Toast.LENGTH_LONG).show();
                    Log.e(TAG,"Failed to start advertising as the advertising is already started");
                }
            }else if(errorCode == ADVERTISE_FAILED_INTERNAL_ERROR){
                if(D){
                    Toast.makeText(mContext, R.string.advertise_failed_internal_error, Toast.LENGTH_LONG).show();
                    Log.e(TAG,"Operation failed due to an internal error");
                }
            }else if(errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED){
                if(D){
                    Toast.makeText(mContext, R.string.advertise_failed_feature_unsupported, Toast.LENGTH_LONG).show();
                    Log.e(TAG,"This feature is not supported on this platform");
                }
            }
        }
    };

    /** create AdvertiseSettings */
    public static AdvertiseSettings createAdvSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder mSettingsbuilder = new AdvertiseSettings.Builder();
        mSettingsbuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        mSettingsbuilder.setConnectable(connectable);
        mSettingsbuilder.setTimeout(timeoutMillis);
        mSettingsbuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        AdvertiseSettings mAdvertiseSettings = mSettingsbuilder.build();
        if(mAdvertiseSettings == null){
            if(D){
                Toast.makeText(mContext, "mAdvertiseSettings == null", Toast.LENGTH_LONG).show();
                Log.e(TAG,"mAdvertiseSettings == null");
            }
        }
        return mAdvertiseSettings;
    }
    public static AdvertiseData createAdvertiseData(){
        AdvertiseData.Builder    mDataBuilder = new AdvertiseData.Builder();
        mDataBuilder.addServiceUuid(ParcelUuid.fromString(serviceUUID));
        mDataBuilder.setIncludeDeviceName(true);
        AdvertiseData mAdvertiseData = mDataBuilder.build();
        if(mAdvertiseData==null){
            if(D){
                Toast.makeText(mContext, "mAdvertiseSettings == null", Toast.LENGTH_LONG).show();
                Log.e(TAG,"mAdvertiseSettings == null");
            }
        }

        return mAdvertiseData;
    }

    private void stopAdvertise() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
//            mBluetoothLeAdvertiser = null;
        }
    }

}
