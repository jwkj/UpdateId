package com.jwkj.updateid;

import android.text.TextUtils;

import com.hdl.udpsenderlib.UDPResult;
import com.hdl.udpsenderlib.UDPResultCallback;
import com.hdl.udpsenderlib.UDPSender;


/**
 * 设备id重号处理、id异常处理管理类
 * Created by HDL on 2017/6/21.
 */

public class UpdateIdManger {
    private static UpdateIdManger mUpdateIdManger;
    /**
     * 设置发送的次数
     */
    private int sendCount = 1;
    /**
     * 上一次结束到下一次开始的间隔
     */
    private long delay = 0;
    /**
     * 单次接收超时时间
     */
    private long stopTimeout = 60 * 1000;

    private UpdateIdManger() {
    }

    public static UpdateIdManger getInstance() {
        synchronized (UpdateIdManger.class) {
            if (mUpdateIdManger == null) {
                mUpdateIdManger = new UpdateIdManger();
            }
        }
        return mUpdateIdManger;
    }

    /**
     * <p>更新设备id.</p>
     * 局域网搜索设备时，当发现设备id发生异常，应该调用此方法来更新设备的id
     *
     * @param oldContactId 需要更新id的设备id号，即旧id号
     * @param targetIp     目标ip地址，即设备的ip地址
     * @param listener     更新进度监听器 {@link UpdateIdListener}
     * @return
     */
    public UpdateIdManger updateId(int oldContactId, final String targetIp, final UpdateIdListener listener) {
        listener.onStart();
        if (oldContactId == 0 || TextUtils.isEmpty(targetIp)) {
            listener.onError(new Throwable("oldContactId = null  or targetIp = null"));
            listener.onCompleted();
            return this;
        }
        UDPSender.getInstance()
                .setTargetIp(targetIp)
                .schedule(sendCount, delay)
                .setReceiveTimeOut(stopTimeout)
                .setInstructions(UpdateIdData.getInstructions(oldContactId))
                .setLocalReceivePort(UpdateIdData.CMD.UPDATE_ID_PORT)
                .setTargetPort(UpdateIdData.CMD.UPDATE_ID_PORT)
                .start(new UDPResultCallback() {
                    @Override
                    public void onNext(UDPResult udpResult) {
                        if (targetIp.equals(udpResult.getIp())) {
                            UpdateIdData updateIdData = UpdateIdData.getUpdateIdData(udpResult.getResultData());
                            if (updateIdData.getCmd() == UpdateIdData.CMD.UPDATE_ID_RECEIVE_CMD) {
                                listener.onNext(updateIdData);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        listener.onError(throwable);
                    }

                    @Override
                    public void onCompleted() {
                        listener.onCompleted();
                    }
                });
        return this;
    }

    /**
     * 设置重复执行的策略
     *
     * @param sendCount 发送的次数
     * @param delay     上一次结束到下一次开始前的时间间隔
     * @return
     */
    public UpdateIdManger schedule(int sendCount, long delay) {
        this.sendCount = sendCount;
        this.delay = delay;
        return this;
    }

    /**
     * 设置单次任务停止超时时间
     *
     * @param stopTimeout 停止超时时间
     * @return
     */
    public UpdateIdManger setStopTimeout(long stopTimeout) {
        this.stopTimeout = stopTimeout;
        return this;
    }

    /**
     * 停止更新id的任务
     *
     * @return
     */
    public UpdateIdManger stop() {
        UDPSender.getInstance().stop();
        return this;
    }
}
