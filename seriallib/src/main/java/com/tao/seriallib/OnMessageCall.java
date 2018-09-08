package com.tao.seriallib;

import com.tao.protocol.TobaccoProtocol;

/**
 * Created by vencent on 2018/9/3.
 */

public interface OnMessageCall {
    
    void  onOpen();
    void  onInitFaile(String mag);
    void  onSendFaile() ;
    void  onReceiverFaile(TobaccoProtocol.TobaccoCmdInfo info) ;
    void  onReceiver(TobaccoProtocol.TobaccoCmdInfo info);
    void  onClose();
    void onPrepared();
    void onOver();
}
