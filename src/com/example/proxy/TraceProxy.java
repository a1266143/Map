package com.example.proxy;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.Trace;

public class TraceProxy {
	
	/**
	 * 鹰眼服务service_id
	 */
	public static final long SERVICE_ID = 110268;
	/**
	 * entity标识，一个entity代表一辆车
	 */
	private static final String ENTITY_NAME = "mycar";
	/**
	 * 轨迹服务类型（0：不上传位置数据，也不接受报警信息
	 * 1：不上传位置数据，但接收警报信息
	 * 2：上传位置数据，且接收警报信息）
	 */
	private static final int TRACETYPE = 2;
	/**
	 * 轨迹采集周期和打包周期
	 */
	private static final int GATHERINTERVAL = 10;
	private static final int PACKINTERVAL = 60;
	
	//轨迹服务开启监听器引用
	private MyOnStartTraceListener startListener;
	//轨迹服务停止监听器引用
	private MyOnStopTraceListener stopListener;
	//轨迹服务客户端引用
	public static LBSTraceClient client;
	//轨迹服务引用
	private Trace trace;
	//轨迹服务是否开启
	private boolean isStart = false;
	
	private Context context;
	/**
	 * 轨迹回调接口引用
	 * @param context
	 */
	private TraceUpload mTraceUpload;
	
	//private Context context;
	public TraceProxy(Context context,TraceUpload mTraceUpload){
		this.context = context;
		//实例化开启轨迹服务回调接口
		startListener = new MyOnStartTraceListener();
		//实例化停止轨迹服务回调接口
		stopListener = new MyOnStopTraceListener();
		//实例化轨迹服务客户端
		client = new LBSTraceClient(context);
		//实例化轨迹服务,依次传入context，serviceid，entityname，轨迹类型
		trace = new Trace(context, SERVICE_ID, ENTITY_NAME, TRACETYPE);
		//设置采集和打包周期
		client.setInterval(GATHERINTERVAL, PACKINTERVAL);
		//实例化回调接口对象
	    this.mTraceUpload = mTraceUpload;
	}
	/**
	 * 开启轨迹追踪
	 */
	public void startTrace(){
		//开启轨迹服务
		client.startTrace(trace, startListener);
		isStart = true;
	}
	
	/**
	 * 结束轨迹追踪
	 * @author Administrator
	 *
	 */
	public void stopTrace(){
		client.stopTrace(trace, stopListener);
		isStart = false;
	}
	
	/**
	 * 返回轨迹服务是否开启
	 */
	public boolean traceIsStart(){
		return isStart;
	}
	
	/**
	 * 开始轨迹追踪监听器
	 * @author 李晓军
	 *
	 */
	class MyOnStartTraceListener implements OnStartTraceListener{

		/**
		 * 开启轨迹服务回调接口（arg0：消息编码，arg1：消息内容）
		 */
		@Override
		public void onTraceCallback(int arg0, String arg1) {
			Log.e("轨迹服务开启回调信息", arg1);
			mTraceUpload.traceUpload(arg0, arg1);
		}

		/**
		 * 轨迹服务推送接口（用于接收服务端推送消息，arg0：消息类型，arg1：消息内容）
		 */
		@Override
		public void onTracePushCallback(byte arg0, String arg1) {
			
		}
		
	}
	
	/**
	 * 轨迹停止监听器
	 * @author 李晓军
	 *
	 */
	class MyOnStopTraceListener implements OnStopTraceListener{

		/**
		 * 轨迹服务停止失败（arg0：错误编码，arg1：消息内容）
		 */
		@Override
		public void onStopTraceFailed(int arg0, String arg1) {
			
		}

		/**
		 * 轨迹服务停止成功
		 */
		@Override
		public void onStopTraceSuccess() {
			Log.e("轨迹服务停止回调信息", "轨迹服务停止成功");
			mTraceUpload.traceUpload(0, "0");
		}
		
	}
	
	/**
	 * 回调接口通知activity
	 */
	public interface TraceUpload{
		public void traceUpload(int arg0,String arg1);
	}
}
