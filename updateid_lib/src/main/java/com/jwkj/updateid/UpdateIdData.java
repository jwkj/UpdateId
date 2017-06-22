package com.jwkj.updateid;

import java.nio.ByteBuffer;

/**
 * 更新设备id实体类
 * Created by HDL on 2017/6/21.
 */

public class UpdateIdData {
    private int cmd;
    private boolean isSetNewIdSuccess;//是否修改新id成功
    private int oldContactId;//旧的设备id
    private int newContactId;//新的设备id

    public class CMD {
        public static final int UPDATE_ID_PORT = 8899;
        public static final int UPDATE_ID_CMD = 53;
        public static final int UPDATE_ID_RECEIVE_CMD = 54;
    }

    /**
     * 根据收到的结果（byte数组）转化为UpdateIdData对象
     *
     * @param result
     * @return
     */
    public static UpdateIdData getUpdateIdData(byte[] result) {
        if (result == null || result.length == 0) {
            return null;
        }
        UpdateIdData updateIdData = new UpdateIdData();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(result);
        updateIdData.setCmd(bytesToInt(result,0));
        if (updateIdData.getCmd() == CMD.UPDATE_ID_RECEIVE_CMD) {//54了才会去取
            updateIdData.setSetNewIdSuccess(bytesToInt(result,4) == 1 ? true : false);
            if (updateIdData.isSetNewIdSuccess()) {//成功了才拿值
                updateIdData.setOldContactId(bytesToInt(result,8));
                updateIdData.setNewContactId(bytesToInt(result,12));
            }
        }
        buffer.clear();
        return updateIdData;
    }

    /**
     * 获取发送修改设备id的指令
     *
     * @param contactId 设备的id
     * @return
     */
    public static byte[] getInstructions(int contactId) {
        byte[] bytes;
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(intToByte(CMD.UPDATE_ID_CMD));
        buffer.put(intToByte(contactId));
        bytes = buffer.array();
        buffer.clear();
        return bytes;
    }

    /**
     * 整形转换为直接数组
     *
     * @param i
     * @return
     */
    private static byte[] intToByte(int i) {
        byte[] targets = new byte[4];
        targets[0] = (byte) (i & 0xFF);
        targets[1] = (byte) (i >> 8 & 0xFF);
        targets[2] = (byte) (i >> 16 & 0xFF);
        targets[3] = (byte) (i >> 24 & 0xFF);
        return targets;
    }

    /**
     * byte数组转int
     * @param src
     * @param offset
     * @return
     */
    private static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24);
        return value;
    }

    public boolean isSetNewIdSuccess() {
        return isSetNewIdSuccess;
    }

    public void setSetNewIdSuccess(boolean setNewIdSuccess) {
        isSetNewIdSuccess = setNewIdSuccess;
    }

    public int getOldContactId() {
        return oldContactId;
    }

    public void setOldContactId(int oldContactId) {
        this.oldContactId = oldContactId;
    }

    public int getNewContactId() {
        return newContactId;
    }

    public void setNewContactId(int newContactId) {
        this.newContactId = newContactId;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return "UpdateIdData{" +
                "cmd=" + cmd +
                ", isSetNewIdSuccess=" + isSetNewIdSuccess +
                ", oldContactId=" + oldContactId +
                ", newContactId=" + newContactId +
                '}';
    }
}
