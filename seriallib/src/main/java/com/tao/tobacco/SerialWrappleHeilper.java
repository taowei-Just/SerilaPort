package com.tao.tobacco;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.tao.protocol.TobaccoProtocol;
import com.tao.seriallib.OnMessageCall;
import com.tao.seriallib.SerialHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vencent on 2018/9/3.
 */

public class SerialWrappleHeilper extends Thread {

    private SerialThread serialThread;
    private SerialHelper serialHelper;
    private OnMessageCall onMessageCall;
    private MyHandler myHandler;
    public static final int sendDataWhat = 1;
    private long wattingTime = 5 * 1000;
    private int dataLen = 12;
    boolean useHandler = false;
    boolean isPrepared = false;
    boolean isAutoNext = false;

    public SerialWrappleHeilper(Context context, String serialPath, int baudrate, OnMessageCall onMessageCall) {
        this.onMessageCall = onMessageCall;
        serialHelper = new SerialHelper(context, serialPath, baudrate, onMessageCall);
        serialHelper.open();
    }

    public SerialWrappleHeilper(Context context, String serialPath, int baudrate, OnMessageCall onMessageCall, boolean useHandler) {
        this.useHandler = useHandler;
        this.onMessageCall = onMessageCall;
        serialHelper = new SerialHelper(context, serialPath, baudrate, onMessageCall);
        if (useHandler) {
            serialThread = new SerialThread();
            serialThread.start();
        } else {
            serialHelper.open();
        }
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public void setOnMessageCall(OnMessageCall onMessageCall) {
        this.onMessageCall = onMessageCall;
        if(serialHelper!=null)
            serialHelper.setOnMessageCall(onMessageCall);
    }

    public void send(TobaccoProtocol.TobaccoCmdInfo info) {
        if (cmdList.contains(info))
        cmdList.remove(info);
        if (useHandler) {
            Message message = getMessage(sendDataWhat, info);
            myHandler.sendMessage(message);
        } else {
            serialHelper.send(info);
            serialHelper.receiver(info);

        }
    }

    public void send(int len, long time, TobaccoProtocol.TobaccoCmdInfo info) {
        cmdList.remove(info);

        if (useHandler) {
            Message message = getMessage(sendDataWhat, len, time, info);
            myHandler.sendMessage(message);

        } else {
            serialHelper.send(info);
            serialHelper.receiver(len, time, info);
        }
        if (isAutoNext)
            next();
    }

    private Message getMessage(int what, int len, long time, TobaccoProtocol.TobaccoCmdInfo info) {
        Message message = new Message();
        message.what = what;
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", info);
        bundle.putInt("len", len);
        bundle.putLong("wattingTime", time);
        message.setData(bundle);
        return message;
    }


    private Message getMessage(int what, TobaccoProtocol.TobaccoCmdInfo info) {
        Message message = new Message();
        message.what = what;
        Bundle bundle = new Bundle();
//        bundle.putByteArray("data", data);
//        bundle.putInt("flag", flag);
        bundle.putSerializable("data", info);
        message.setData(bundle);
        return message;
    }

    public void release() {
        serialHelper.release();
        if (serialThread != null)
            serialThread.close();
    }

    List<TobaccoProtocol.TobaccoCmdInfo> cmdList = new ArrayList<>();

    public void sendList(List<TobaccoProtocol.TobaccoCmdInfo> cmdInfoList) {
        if (cmdInfoList != null)
            cmdList.addAll(cmdInfoList);
        next();
    }

    public synchronized void stopSend() {
        cmdList.clear();
        if (useHandler) {
            myHandler.removeMessages(sendDataWhat);
        } else {
        }
        next();
    }


    public synchronized void next() {
        TobaccoProtocol.TobaccoCmdInfo info = nextInfo();
        if (info == null) {
            onMessageCall.onOver();
            return;
        }
        send(info);
    }


    private TobaccoProtocol.TobaccoCmdInfo nextInfo() {
        if (cmdList.size() > 0)
            return cmdList.get(0);
        else
            return null;
    }


    class SerialThread extends Thread {
        private Looper looper;

        @Override
        public void run() {
            Looper.prepare();
            looper = Looper.myLooper();
            serialHelper.open();
            myHandler = new MyHandler(looper);
            onMessageCall.onPrepared();
            isPrepared = true;
            Looper.loop();
        }

        public void close() {
            if (!isInterrupted())
                interrupt();
            myHandler.removeMessages(sendDataWhat);
            looper.quitSafely();
        }

       
    }

    class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case sendDataWhat:
                    try {
                        sendCmd(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }

        private void sendCmd(Message msg) {
            Log.e("sendCmd", "" + msg.toString());
            TobaccoProtocol.TobaccoCmdInfo info = (TobaccoProtocol.TobaccoCmdInfo) msg.getData().getSerializable("data");
            int len = msg.getData().getInt("len", dataLen);
            long wattingTime = msg.getData().getLong("wattingTime", SerialWrappleHeilper.this.wattingTime);
            serialHelper.send(info);
            serialHelper.receiver(len, wattingTime, info);
        }
    }
}
