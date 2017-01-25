package com.example.bleperipheral;

/**
 * Created by sdh on 2017-1-23.
 */

public class Utils {
    public static byte[] getMacBytes(String mac){
        byte []macBytes = new byte[6];
        String [] strArr = mac.split(":");

        for(int i = 0;i < strArr.length; i++){
            int value = Integer.parseInt(strArr[i],16);
            macBytes[i] = (byte) value;
        }
        return macBytes;
    }
}
