package com.example.bleperipheral;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;


public class AdvertisingService extends Service {
    public static final String TAG = "bleperipheral";
    public static String serviceUUID = "039AFFF0-2C94-11E3-9E06-0002A5D5C51B";
    public static String characteristicUUID = "039AFFA1-2C94-11E3-9E06-0002A5D5C51B";
    private BluetoothGattServer server;
    private BluetoothGattCharacteristic character;
    private BluetoothGattService service;
    private static boolean D = true;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
               switch (msg.what){
                   case 1:
                       Toast.makeText(getApplicationContext(),msg.arg1,Toast.LENGTH_SHORT).show();
                       break;
               }
            }
        };
        init();
        mBluetoothLeAdvertiser.startAdvertising(createAdvSettings(true, 0), createAdvertiseData(), mAdvertiseCallback);

    }

    public AdvertisingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void setServer() {
        character = new BluetoothGattCharacteristic(
                UUID.fromString(characteristicUUID),
                BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY);
        character.setValue("send test".getBytes());

        service = new BluetoothGattService(UUID.fromString(serviceUUID),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        service.addCharacteristic(character);

        server = mBluetoothManager.openGattServer(this,
                new BluetoothGattServerCallback() {
                    @Override
                    public void onNotificationSent(BluetoothDevice device, int status) {
                        super.onNotificationSent(device, status);
                        if (status == BluetoothGatt.GATT_SUCCESS)
                            System.out.println("发送通知成功！");
                    }

                    @Override
                    public void onConnectionStateChange(BluetoothDevice device,
                                                        int status, int newState) {
                        super.onConnectionStateChange(device, status, newState);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(SecondActivity.this,"连接状态变化",Toast.LENGTH_LONG).show();
//
//                            }
//                        });
                        Log.d("Chris", "onConnectionStateChange:" + device + "-=-=-=-=" + status + "-=-=-=-" + newState);
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
            Log.e(TAG, "onStartSuccess settingsInEffect" + settingsInEffect);

        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            if (D) Log.e(TAG, "onStartFailure errorCode" + errorCode);

            if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                if (D) {
//                    Toast.makeText(mContext, R.string.advertise_failed_data_too_large, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
                }
            } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
                if (D) {
//                    Toast.makeText(mContext, R.string.advertise_failed_too_many_advertises, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to start advertising because no advertising instance is available.");
                }
            } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
                if (D) {
//                    Toast.makeText(mContext, R.string.advertise_failed_already_started, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to start advertising as the advertising is already started");
                }
            } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
                if (D) {
//                    Toast.makeText(mContext, R.string.advertise_failed_internal_error, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Operation failed due to an internal error");
                }
            } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
                if (D) {
//                    Toast.makeText(mContext, R.string.advertise_failed_feature_unsupported, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "This feature is not supported on this platform");
                }
            }
        }
    };

    /**
     * create AdvertiseSettings
     */
    public static AdvertiseSettings createAdvSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder mSettingsbuilder = new AdvertiseSettings.Builder();
        mSettingsbuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        mSettingsbuilder.setConnectable(connectable);
        mSettingsbuilder.setTimeout(timeoutMillis);
        mSettingsbuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        AdvertiseSettings mAdvertiseSettings = mSettingsbuilder.build();
        if (mAdvertiseSettings == null) {
            if (D) {
//                Toast.makeText(mContext, "mAdvertiseSettings == null", Toast.LENGTH_LONG).show();
                Log.e(TAG, "mAdvertiseSettings == null");
            }
        }
        return mAdvertiseSettings;
    }

    public static AdvertiseData createAdvertiseData() {
        AdvertiseData.Builder mDataBuilder = new AdvertiseData.Builder();
        mDataBuilder.addServiceUuid(ParcelUuid.fromString(serviceUUID));
        mDataBuilder.setIncludeDeviceName(true);
        AdvertiseData mAdvertiseData = mDataBuilder.build();
        if (mAdvertiseData == null) {
            if (D) {
//                Toast.makeText(mContext, "mAdvertiseSettings == null", Toast.LENGTH_LONG).show();
                Log.e(TAG, "mAdvertiseSettings == null");
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

    private void init() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
        }

        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
//        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_LONG).show();

        }

        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
            if (mBluetoothLeAdvertiser == null) {
                Toast.makeText(this, "该设备不支持peripheral", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "the device not support peripheral");

            } else {
                setServer();
//                Intent intent=new Intent(SecondActivity.this,AdvertisingService.class);
//                startService(intent);
            }
        } else {
            Toast.makeText(this, "请开启蓝牙", Toast.LENGTH_SHORT).show();


        }
    }

//    private void ServiceToast(String message){
//        Message msg=new Message();
//        msg.what=1;
//        Bundle bundle=new Bundle();
//        bundle.putString();
//        msg.setData();
//    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "destroy执行了");
        stopAdvertise();
    }
}
