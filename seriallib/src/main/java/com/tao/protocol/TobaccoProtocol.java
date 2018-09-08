package com.tao.protocol;

import android.util.Log;

import com.tao.seriallib.StringUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by vencent on 2018/9/3.
 */

public class TobaccoProtocol {

    public static byte[] getPathWayStatueCmd(int id) {
        byte[] cmd = new byte[12];
        cmd[0] = 0x48;
        cmd[1] = 0x02;


        cmd[2] = id2Hex(id)[0];
        byte[] bytes = Verify.Make_CRC(cmd, 0, 10);
        System.arraycopy(bytes, 0, cmd, 10, 2);
        return cmd;
    }

    private static byte[] id2Hex(int id) {
        String hexString = Integer.toHexString(id);

        if (hexString.length() < 2) {
            hexString = "0" + hexString;
        }
        System.err.println(" hex " + hexString);
        byte[] bytes1 = StringUtils.hexString2Bytes(hexString);
        System.err.println("" + bytes1.length);
        return bytes1;
    }

    public static byte[] getTransportCmd(int id) {

        byte[] cmd = new byte[12];
        cmd[0] = 0x48;
        cmd[1] = 0x01;

        cmd[2] = id2Hex(id)[0];
        byte[] bytes = Verify.Make_CRC(cmd, 0, 10);
        System.arraycopy(bytes, 0, cmd, 10, 2);
        return cmd;

    }

    public static byte[] getLightContorlCmd(int id, boolean isSwitch) {

        byte[] cmd = new byte[12];
        cmd[0] = 0x48;
        cmd[1] = 0x04;

        cmd[2] = id2Hex(id)[0];
        if (isSwitch)
            cmd[3] = 0x01;
        else
            cmd[3] = 0;

        byte[] bytes = Verify.Make_CRC(cmd, 0, 10);
        System.arraycopy(bytes, 0, cmd, 10, 2);
        return cmd;

    }

    public static byte[] getDoorContorlCmd(int id, boolean isSwitch) {
        byte[] cmd = new byte[12];
        cmd[0] = 0x48;
        cmd[1] = 0x05;
        cmd[2] = id2Hex(id)[0];
        if (isSwitch)
            cmd[3] = 0x01;
        else
            cmd[3] = 0;
        byte[] bytes = Verify.Make_CRC(cmd, 0, 10);
        System.arraycopy(bytes, 0, cmd, 10, 2);
        return cmd;
    }

    public static byte[] getCheckDoorStatueCmd(int id) {
        byte[] cmd = new byte[12];
        cmd[0] = 0x48;
        cmd[1] = 0x06;
        cmd[2] = id2Hex(id)[0];
        byte[] bytes = Verify.Make_CRC(cmd, 0, 10);
        System.arraycopy(bytes, 0, cmd, 10, 2);
        return cmd;

    }

    //     48021000000000000000EB55

    //     480211000000000000002A99

    public static TobaccoCmdInfo praceData(byte[] data, int flag) {
        if (data.length >= 12) {
            byte[] buff = new byte[12];
            for (int i = 0; i < data.length; i++) {
                if (data[i] == 0x48) {
                    byte[] b = new byte[2];
                    System.arraycopy(data, i, buff, 0, 12);
                    System.arraycopy(data, i + 10, b, 0, b.length);
                    TobaccoCmdInfo tobaccoCmdInfo = null;
                    if (checkData(buff, b)) {
                        //校验成功  
                        if (buff[1] == 0x01) {
                            //出货
                            tobaccoCmdInfo = outGoods(buff);
                        } else if (buff[1] == 0x02) {
                            //货道查询
                            tobaccoCmdInfo = queryChannel(buff);
                        } else if (buff[1] == 0x03) {
                            //整版查询
                        } else if (buff[1] == 0x04) {
                            //灯条控制
                            tobaccoCmdInfo = lightControl(buff);
                        } else if (buff[1] == 0x05) {
                            //门锁控制
                            tobaccoCmdInfo = doorControl(buff);
                        } else if (buff[1] == 0x06) {
                            //门锁检测
                            tobaccoCmdInfo = doorCheck(buff);
                        } else if (buff[1] == 0x07) {
                            //设置门号
                        } else if (buff[1] == 0x08) {
                            //设置出货成功时间
                        } else if (buff[1] == 0x09) {
                            //设置出货失败时间
                        } else if (buff[1] == 0x0a) {
                            // 读取出货失败和成功时间
                        } else if (buff[1] == 0x0b) {
                            //读取出货失败和成功时间
                        }
                        if (tobaccoCmdInfo != null)
                            tobaccoCmdInfo.setFlag(flag);
                        return tobaccoCmdInfo;
                    }
                }
            }

        }
        return null;
    }


    public static TobaccoCmdInfo praceData(TobaccoCmdInfo info) {
        byte[] data = info.getReceiveData();
        if (data.length >= 12) {
            byte[] buff = new byte[12];
            for (int i = 0; i < data.length; i++) {
                if (data[i] == 0x48) {
                    byte[] b = new byte[2];
                    System.arraycopy(data, i, buff, 0, 12);
                    System.arraycopy(data, i + 10, b, 0, b.length);
                    TobaccoCmdInfo tobaccoCmdInfo = null; 
                    if (checkData(buff, b)) {
                        //校验成功  
                        if (buff[1] == 0x01) {
                            //出货
                            tobaccoCmdInfo = outGoods(buff);
                        } else if (buff[1] == 0x02) {
                            //货道查询
                            tobaccoCmdInfo = queryChannel(buff);
                        } else if (buff[1] == 0x03) {
                            //整版查询
                        } else if (buff[1] == 0x04) {
                            //灯条控制
                            tobaccoCmdInfo = lightControl(buff);
                        } else if (buff[1] == 0x05) {
                            //门锁控制
                            tobaccoCmdInfo = doorControl(buff);
                        } else if (buff[1] == 0x06) {
                            //门锁检测
                            tobaccoCmdInfo = doorCheck(buff);
                        } else if (buff[1] == 0x07) {
                            //设置门号
                        } else if (buff[1] == 0x08) {
                            //设置出货成功时间
                        } else if (buff[1] == 0x09) {
                            //设置出货失败时间
                        } else if (buff[1] == 0x0a) {
                            // 读取出货失败和成功时间
                        } else if (buff[1] == 0x0b) {
                            //读取出货失败和成功时间
                        }
                        if (tobaccoCmdInfo != null) {
                            tobaccoCmdInfo.setNumber(StringUtils.byte2Int(info.getSendData()[2]));
                        }
                        return tobaccoCmdInfo;
                    }
                }
            }
        }
        info.setNumber(StringUtils.byte2Int(info.getSendData()[2]));
        return info;
    }

    private static TobaccoCmdInfo doorCheck(byte[] buff) {
        TobaccoCmdInfo cmdInfo = new TobaccoCmdInfo();
        cmdInfo.setCmdType(CmdType.doorCheck);
        cmdInfo.setReceiveData(buff);
        cmdInfo.setResult((int) buff[3]);
        cmdInfo.setNumber(StringUtils.byte2Int(buff[2]));
        return cmdInfo;
    }

    private static TobaccoCmdInfo doorControl(byte[] buff) {
        TobaccoCmdInfo cmdInfo = new TobaccoCmdInfo();
        cmdInfo.setCmdType(CmdType.query);
        cmdInfo.setReceiveData(buff);
        cmdInfo.setResult((int) buff[3]);
        cmdInfo.setNumber(StringUtils.byte2Int(buff[2]));

        return cmdInfo;
    }

    private static TobaccoCmdInfo lightControl(byte[] buff) {
        TobaccoCmdInfo cmdInfo = new TobaccoCmdInfo();
        cmdInfo.setCmdType(CmdType.lightContorl);
        cmdInfo.setReceiveData(buff);
        cmdInfo.setResult((int) buff[4]);
        cmdInfo.setStatue((int) buff[3]);
        cmdInfo.setNumber(StringUtils.byte2Int(buff[2]));
        return cmdInfo;
    }

    private static TobaccoCmdInfo queryChannel(byte[] buff) {
        TobaccoCmdInfo cmdInfo = new TobaccoCmdInfo();
        cmdInfo.setCmdType(CmdType.query);
        cmdInfo.setReceiveData(buff);
        cmdInfo.setResult((int) buff[3]);
        cmdInfo.setNumber(StringUtils.byte2Int(buff[2]));

        return cmdInfo;
    }

    private static TobaccoCmdInfo outGoods(byte[] buff) {

        TobaccoCmdInfo cmdInfo = new TobaccoCmdInfo();
        cmdInfo.setCmdType(CmdType.out);
        cmdInfo.setReceiveData(buff);
        cmdInfo.setResult((int) buff[3]);
        cmdInfo.setNumber(StringUtils.byte2Int(buff[2]));

        return cmdInfo;
    }

    private static boolean checkData(byte[] buff, byte[] b) {
        byte[] data = new byte[10];
        System.arraycopy(buff, 0, data, 0, data.length);
        byte[] bytes = Verify.Make_CRC(data);

        int aByte = StringUtils.byte2Int(bytes[0]);
        int b1 = StringUtils.byte2Int(b[0]);
        int aByte1 = StringUtils.byte2Int(bytes[1]);
        int b2 = StringUtils.byte2Int(b[1]);

        Log.e("checkData", "bytes[0] " + aByte + " b[0] " + b1 + "  bytes[1] " + aByte1 + " b[1] " + b2);

        if (aByte == b1 && aByte1 == b2) {
            System.out.println(" a = c ");
            return true;
        }
        return false;

    }

    public static class TobaccoCmdInfo implements Serializable {

        int flag;
        byte[] receiveData;
        byte[] sendData;
        CmdType cmdType;
        int result;
        int statue;
        int number;

        public CmdType getCmdType() {
            return cmdType;
        }

        public byte[] getSendData() {
            return sendData;
        }

        public void setSendData(byte[] sendData) {
            this.sendData = sendData;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getStatue() {
            return statue;
        }

        public void setStatue(int statue) {
            this.statue = statue;
        }

        public byte[] getReceiveData() {
            return receiveData;
        }

        public void setReceiveData(byte[] receiveData) {
            this.receiveData = receiveData;
        }

        public void setCmdType(CmdType cmdType) {
            this.cmdType = cmdType;
        }

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return "TobaccoCmdInfo{" +
                    "flag=" + flag +
                    ", receiveData=" + Arrays.toString(receiveData) +
                    ", sendData=" + Arrays.toString(sendData) +
                    ", cmdType=" + cmdType +
                    ", result=" + result +
                    ", statue=" + statue +
                    ", number=" + number +
                    '}';
        }
    }

    enum CmdType {
        none(0, "none"), out(1, "出货指令"), query(2, "查询指令"), allQuery(3, "整版查询"), lightContorl(4, "灯条控制"),
        doorControl(5, "门锁控制"), doorCheck(6, "门锁检测"), settingDoorNumber(7, "设置门号"), settingOutSuccessTime(8, "设置出货成功时间"),
        settingOutFaileTime(9, "设置出货失败时间"), readOutSuccessTime(10, " 读取出货失败"), readOutFaileTime(11, "读取出货失败"),;

        int type;
        String describe;

        CmdType(int type, String describe) {
            this.type = type;
            this.describe = describe;
        }

        public int getType() {
            return type;
        }


        public static CmdType type(int type) {
            switch (type) {
                case 1:
                    return out;
                case 2:
                    return query;
                case 3:
                    return allQuery;
                case 4:
                    return lightContorl;
                case 5:
                    return doorControl;
                case 6:
                    return doorCheck;
                case 7:
                    return settingDoorNumber;
                case 8:
                    return settingOutSuccessTime;
                case 9:
                    return settingOutFaileTime;
                case 10:
                    return readOutSuccessTime;
                case 11:
                    return readOutFaileTime;

            }
            return none;
        }
    }

}
