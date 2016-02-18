package com.example.proxy;

import android.content.Context;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnTrackListener;

public class TraceQueryProxy {

	/**
	 * 回调接口类型
	 */
	public static final int TYPE_QUERY_HISTORY = 0;
	public static final int TYPE_QUERY_REALTIME = 1;
	/**
	 * 设置协议类型，0为http，1为https
	 */
	public static final int PROTOCOTYPE = 0;
	/**
	 * 返回结果的类型（0：返回全部结果，1：只返回entityName的列表）
	 */
	public static final int RETURNTYPE = 0;
	/**
	 * 检索条件
	 */
	public static final String COLUMNKEY = "";
	/**
	 * 分页大小
	 */
	public static final int PAGESIZE = 1000;
	/**
	 * 分页索引
	 */
	public static final int PAGEINDEX = 1;

	private MyOnEntityListener realPositionListener = null;
	private MyOnTrackListener historyTrackListener;
	/**
	 * 回调接口
	 */
	private TraceQueryCallback mTraceQueryCallback;

	private LBSTraceClient client;

	public TraceQueryProxy(Context context,TraceQueryCallback mTraceQueryCallback) {
		// 实时位置监听器
		realPositionListener = new MyOnEntityListener();
		// 历史轨迹监听器
		historyTrackListener = new MyOnTrackListener();
		// 实例化轨迹服务客户端
		client = new LBSTraceClient(context);
		//实例化回调接口，以供下面回调
		this.mTraceQueryCallback = mTraceQueryCallback;
	}

	/**
	 * 查询实时位置
	 * 
	 * @param entityNames
	 *            entity标识列表（多个entityName以英文逗号隔开）
	 */
	public void queryRealPosition(String entityNames) {
		// 活跃时间，UNIX时间戳（指定该字段时，返回从该时间点之后仍有位置变动的entity的实时点集合）
		int activeTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);

		client.queryEntityList(TraceProxy.SERVICE_ID, entityNames, COLUMNKEY,
				RETURNTYPE, activeTime, PAGESIZE, PAGEINDEX,
				realPositionListener);
	}

	/**
	 * 执行实时位置查询回调监听器
	 * 
	 * @author 李晓军
	 * 
	 */
	class MyOnEntityListener extends OnEntityListener {

		@Override
		public void onRequestFailedCallback(String arg0) {
			Log.e("TraceQueryProxy", "收到了失败回调");

		}

		@Override
		public void onQueryEntityListCallback(String arg0) {
			Log.e("TraceQueryProxy", "收到了List回调" + arg0);
			//将json通过回调接口传送回activity，让activity来处理
			mTraceQueryCallback.query(TYPE_QUERY_REALTIME,arg0);
		}
	}

	/**
	 * 查询某辆汽车的历史轨迹
	 * 
	 * @param entity
	 */
	public void queryHistoryTrace(String entity) {
		// 是否返回精简的结果（0：将只返回经纬度，1：将返回经纬度及其他属性信息）
		int simpleReturn = 0;
		// 开始时间戳，从现在起之前的十二小时
		int startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
		// 结束时间，就是现在
		int endTime = (int) (System.currentTimeMillis()/1000);
		//执行历史轨迹查询
		client.queryHistoryTrack(TraceProxy.SERVICE_ID, entity, simpleReturn,
				startTime, endTime, PAGESIZE, PAGEINDEX, historyTrackListener);
	}

	/**
	 * 历史轨迹查询监听器
	 * 查询成功返回的是历史轨迹的json数据
	 * @author 李晓军
	 * 
	 */
	class MyOnTrackListener extends OnTrackListener {

		@Override
		public void onRequestFailedCallback(String arg0) {
			Log.e("TraceQueryProxy", "历史轨迹查询失败");
		}

		/**
		 * 查询历史轨迹回调接口
		 */
		@Override
		public void onQueryHistoryTrackCallback(String arg0) {
			Log.e("TraceQueryProxy", "历史轨迹查询成功" + arg0);
			//将json通过回调接口传送回activity，让activity来处理
			mTraceQueryCallback.query(TYPE_QUERY_HISTORY,arg0);
			
		}

	}

	/**
	 *轨迹历史查询成功回调接口
	 */
	public interface TraceQueryCallback{
		/**
		 * 查询信息回调方法
		 * @param info
		 */
		public void query(int type,String info);
	}
}
