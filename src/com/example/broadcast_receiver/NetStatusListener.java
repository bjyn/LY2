package com.example.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.common.HttpUtils;

/**
 * 监听网络状态改变的的广播接收器
 * 
 * @author steven
 * 
 */
public class NetStatusListener extends BroadcastReceiver {

	public NetStatusListener() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (new HttpUtils(context).getNetStatus() > 0) {
			// TODO 网络状态变好时，通知服务将未传上去的操作上传服务器
		}
		
	}

}
