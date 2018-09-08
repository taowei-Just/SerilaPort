package com.tao.seriallib;

import com.tao.protocol.TobaccoProtocol;

/**
 * Created by vencent on 2018/9/3.
 */

public interface Function {
    
    void open();
    void send(TobaccoProtocol.TobaccoCmdInfo info);
    byte[] receiver(TobaccoProtocol.TobaccoCmdInfo info);
    byte[] receiver(int len, long time,TobaccoProtocol.TobaccoCmdInfo info);
    void  release();
    
}
