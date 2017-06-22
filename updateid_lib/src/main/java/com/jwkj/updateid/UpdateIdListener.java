package com.jwkj.updateid;

/**
 * 更新设备id过程监听器
 * Created by Administrator on 2017/4/11.
 */

public abstract class UpdateIdListener {
    /**
     * 更新开始的时候回调
     */
    public void onStart() {
    }

    /**
     * 更新发生错误的时候开始回调
     *
     * @param throwable 异常信息
     */
    public void onError(Throwable throwable) {
    }

    /**
     * 每接收到一次回应就回调一次
     *
     * @param updateIdData 更改结果
     */
    public abstract void onNext(UpdateIdData updateIdData);

    /**
     * 更新结束的时候回调
     */
    public void onCompleted() {

    }
}
