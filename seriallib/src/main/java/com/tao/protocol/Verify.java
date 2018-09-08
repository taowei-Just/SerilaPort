package com.tao.protocol;

import com.tao.seriallib.StringUtils;

/**
 * Created by vencent on 2018/9/3.
 */

public class Verify {

 

    /**
     * 计算产生校验码
     *
     * @param data 需要校验的数据
     * @param  
     *@param   
     */
    public static byte[] Make_CRC(byte[] data, int offst, int  lent) {
        byte[] buf = new byte[lent];// 存储需要产生校验码的数据
        System.arraycopy(data,offst,buf,0,lent);
        int len = buf.length;
        int crc = 0xFFFF;//16位
        for (int pos = 0; pos < len; pos++) {
            if (buf[pos] < 0) {
                crc ^= (int) buf[pos] + 256; // XOR byte into least sig. byte of
                // crc
            } else {
                crc ^= (int) buf[pos]; // XOR byte into least sig. byte of crc
            }
            for (int i = 8; i != 0; i--) { // Loop over each bit
                if ((crc & 0x0001) != 0) { // If the LSB is set
                    crc >>= 1; // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                } else
                    // Else LSB is not set
                    crc >>= 1; // Just shift right
            }
        }
        
//        Error:Gradle: failed to create directory 'H:\MyWorking\GitFolder\CompanyGit\testDemo\app\build\generated\source\r\debug\com\tao\test'.
//        Information:Module "seriallib" was fully rebuilt due to project configuration/dependencies changes
//        Error:Gradle: java.util.concurrent.ExecutionException: java.util.concurrent.ExecutionException: com.android.tools.aapt2.Aapt2Exception: AAPT2 error: check logs for details
//        Error:Gradle: failed to create directory 'H:\MyWorking\GitFolder\CompanyGit\testDemo\app\build\generated\source\r\debug\com\tao\test'.
        String c = Integer.toHexString(crc);
        if (c.length() == 4) {
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 3) {
            c = "0" + c;
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 2) {
            c =   c.substring(0, 1)+ c.substring(1, 2)+"0"  + "0" ;
        }
        return StringUtils.hexString2Bytes(c );
    }
    
 public static byte[] Make_CRC(byte[] data) {
        byte[] buf = new byte[data.length];// 存储需要产生校验码的数据
        System.arraycopy(data,0,buf,0,data.length);
        int len = buf.length;
        int crc = 0xFFFF;//16位
        for (int pos = 0; pos < len; pos++) {
            if (buf[pos] < 0) {
                crc ^= (int) buf[pos] + 256; // XOR byte into least sig. byte of
                // crc
            } else {
                crc ^= (int) buf[pos]; // XOR byte into least sig. byte of crc
            }
            for (int i = 8; i != 0; i--) { // Loop over each bit
                if ((crc & 0x0001) != 0) { // If the LSB is set
                    crc >>= 1; // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                } else
                    // Else LSB is not set
                    crc >>= 1; // Just shift right
            }
        }
        
//        Error:Gradle: failed to create directory 'H:\MyWorking\GitFolder\CompanyGit\testDemo\app\build\generated\source\r\debug\com\tao\test'.
//        Information:Module "seriallib" was fully rebuilt due to project configuration/dependencies changes
//        Error:Gradle: java.util.concurrent.ExecutionException: java.util.concurrent.ExecutionException: com.android.tools.aapt2.Aapt2Exception: AAPT2 error: check logs for details
//        Error:Gradle: failed to create directory 'H:\MyWorking\GitFolder\CompanyGit\testDemo\app\build\generated\source\r\debug\com\tao\test'.
        String c = Integer.toHexString(crc);
        if (c.length() == 4) {
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 3) {
            c = "0" + c;
            c = c.substring(2, 4) + c.substring(0, 2);
        } else if (c.length() == 2) {
            c =   c.substring(0, 1)+ c.substring(1, 2)+"0"  + "0" ;
        }
        return StringUtils.hexString2Bytes(c );
    }

    public static  byte[] crc_16_CCITT_False(byte[] data , int offst , int lent) {

        byte[] buf = new byte[lent];// 存储需要产生校验码的数据
        System.arraycopy(data,offst,buf,0,lent);
        
        int crc = 0xffff; // initial value
        int polynomial = 0x1021; // poly value
        for (int index = 0; index < buf.length; index++) {
            byte b = buf[index];
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit)
                    crc ^= polynomial;
            }
        }
        crc &= 0xffff;
        //输出String字样的16进制
//        String strCrc = Integer.toHexString(crc).toUpperCase();
//        System.out.println(strCrc);
        return StringUtils.hexString2Bytes( Integer.toHexString(crc).toUpperCase() );
    }

    public static void main(String[] s) {
        //    48 02 01 00 00 00 00 00 00 00
        byte[] data = new byte[]{0x48, 0x02, 0x1c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

//        48021C0000000000000000BE
//        48021C000000000000000B0E
        
//        send 48021C0000000000000000EB
//        onReceiver 48021CFFFFFFFFFFFFFFE580
        //        System.err.println(""+CRC16());
        System.err.println("" + StringUtils.bytes2HexString( Make_CRC(data, 0, data.length)));
        int a =0 ;
        int b =0 ;
        if (a==b)
            System.err.println("a = b ");
//        
//        for (int i=0 ; i < 48  ; i++)
//        System.err.print("" + StringUtils.bytes2HexString(TobaccoProtocol.getPathWayStatueCmd(i)));
    }
}
