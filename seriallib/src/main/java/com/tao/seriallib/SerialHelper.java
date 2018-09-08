package com.tao.seriallib;

import android.content.Context;
import android.os.Handler;
import android.serialport.SerialPort;
import android.text.TextUtils;
import android.util.Log;

import com.tao.protocol.TobaccoProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by vencent on 2018/9/3.
 */

public class SerialHelper implements Function {

    String serialPath;
    int baudrate;
    private SerialPort serialPort;
    boolean autoReinit = false;
    private final Handler handler;
    private InputStream inputStream;
    private OutputStream outputStream;
    private long wattingTime = 100;
    OnMessageCall onMessageCall;

    ArrayList<byte[]> caches = new ArrayList<>();
    private String TAG = getClass().getSimpleName();

    public void setOnMessageCall(OnMessageCall onMessageCall) {
        this.onMessageCall = onMessageCall;
    }

    public SerialHelper(Context context, String serialPath, int baudrate, OnMessageCall onMessageCall) {
        this.serialPath = serialPath;
        this.baudrate = baudrate;
        this.onMessageCall = onMessageCall;
        handler = new Handler(context.getMainLooper());
    }

    @Override
    public void open() {
        try {
            serialPort = new SerialPort(serialPath, baudrate, 0);
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            onMessageCall.onOpen();

        } catch (Exception e) {
            e.printStackTrace();
            onMessageCall.onInitFaile(e.toString());
            reInit();
        }

    }

    private void reInit() {
        if (autoReinit && TextUtils.isEmpty(serialPath)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    open();
                }
            });
        }
    }

    @Override
    public void send(TobaccoProtocol.TobaccoCmdInfo info) {

        Log.e(TAG, " send " + StringUtils.bytes2HexString(info.getSendData()));
        if (outputStream != null) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                outputStream.write(info.getSendData());
            } catch (Exception e) {
                e.printStackTrace();
                outputStream = null;
                onMessageCall.onSendFaile();
                reInit();
            }
        }
    }

    @Override
    public byte[] receiver(TobaccoProtocol.TobaccoCmdInfo info) {
        int len = 0;
        byte[] buff;
        byte[] data = new byte[1];
        if (inputStream == null) {
            onMessageCall.onReceiverFaile(info);
            reInit();
            return data;
        }
        try {
            Thread.sleep(wattingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            while ((len = inputStream.available()) > 0) {
                buff = new byte[len];
                inputStream.read(buff, 0, len);
                byte[] cache = data;
                data = new byte[len + cache.length];
                System.arraycopy(cache, 0, data, 0, cache.length);
                System.arraycopy(buff, 0, data, cache.length - 1, buff.length);

            }
            Log.e("receiver ", " data:" + StringUtils.bytes2HexString(data));
            info.setReceiveData(data);
            onMessageCall.onReceiver(info);
        } catch (Exception e) {
            e.printStackTrace();
            inputStream = null;
            onMessageCall.onReceiverFaile(info);
            
            reInit();
            
        } finally {
            
        }
        
        return data;
    }

    @Override
    public byte[] receiver(int len, long time, TobaccoProtocol.TobaccoCmdInfo info) {
        byte[] data = new byte[0];
        if (inputStream == null) {
            onMessageCall.onReceiverFaile(info);
            reInit();
            return data;
        }
        try {
            Thread.sleep(wattingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            long startTimeMillis = System.currentTimeMillis();
            int lenth = 0;
            int totalLen = 0;
            byte[] buff;
            data = new byte[len];
            while ((System.currentTimeMillis() - startTimeMillis) < time && ((lenth = inputStream.available()) + totalLen) < len) {
                buff = new byte[lenth];
                inputStream.read(buff, 0, lenth);
                System.arraycopy(buff, 0, data, totalLen, buff.length);
                totalLen += buff.length;
            }
            if (totalLen < len && (lenth = inputStream.available()) > 0) {
                buff = new byte[(len - totalLen)];
                inputStream.read(buff, 0, buff.length);
                System.arraycopy(buff, 0, data, totalLen, buff.length);
            }
            info.setReceiveData(data);
            onMessageCall.onReceiver(info);
        } catch (IOException e) {
            e.printStackTrace();
            onMessageCall.onReceiverFaile(info);
            reInit();
        } finally {
        }
        return data;
    }

    @Override
    public void release() {

        try {
            inputStream = null;
            outputStream = null;
            serialPort.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            onMessageCall.onClose();
        }
    }
}
