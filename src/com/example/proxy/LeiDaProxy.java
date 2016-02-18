package com.example.proxy;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.baidu.mapapi.radar.RadarUploadInfoCallback;
import com.example.map.R;

/**
 * 周边雷达类
 * 
 * @author Administrator
 * 
 */
public class LeiDaProxy {

	// 周边雷达用户身份标识符
	public static final String IDENTIFIER = null;
	// 周边雷达检索的半径,单位：米
	public static final int RADIUS = 200000;
	//查询周边每页容量
	public static final int PAGECAPACITY = 5;

	private RadarSearchManager mManager;
	private MyRadarSearchListener listener;
	private Context context;
	private BaiduMap mBaiduMap;
	//回调接口
	private NumberOfPeople mNumberOfPeople;

	private RadarUploadInfoCallback mRadarUploadInfoCallback;
	
	private RadarUploadInfo info;

	private MarkerProxy mMarkerProxy;

	public LeiDaProxy(Context context, BaiduMap mBaiduMap,NumberOfPeople mNumberOfPeople) {
		// 对周边雷达初始化
		mManager = RadarSearchManager.getInstance();
		// 新建位置信息上传监听器实例
		listener = new MyRadarSearchListener();
		//新建位置连续上传监听器实例
		mRadarUploadInfoCallback = new MyRadarUploadInfoCallback();
		this.context = context;
		this.mBaiduMap = mBaiduMap;
		this.mNumberOfPeople = mNumberOfPeople;
		mMarkerProxy = new MarkerProxy(context, mBaiduMap);
		// 新建上传信息载体实例
		info = new RadarUploadInfo();

		// 周边雷达设置监听
		mManager.addNearbyInfoListener(listener);
	}

	/**
	 * 单次用户信息上传
	 * 
	 * @param userComment
	 *            用户备注信息
	 */
	public void uploadInfo(String userComment, LatLng latLng) {
		// 设置周边雷达的用户身份标识，id为空默认是设备标识
		mManager.setUserID(IDENTIFIER);
		// 设置坐标点
		info.pt = latLng;
		// 设置用户备注信息
		 info.comments = userComment;
		// 上传
		mManager.uploadInfoRequest(info);
	}

	/**
	 * 连续位置上传
	 */
	public void uploadInfoCon() {
		//位置自动上传,每3秒上传一次位置信息
		mManager.startUploadAuto(mRadarUploadInfoCallback, 3000);
	}

	/**
	 * 周边位置检索
	 * 
	 * @param latLng
	 *            自己的位置坐标
	 */
	public void postionSearch(LatLng latLng) {
		// 构造请求参数
		RadarNearbySearchOption option = new RadarNearbySearchOption();
		// 设置自己的位置坐标,不设置默认就是上次用户上传的位置
		 //option.centerPt(latLng);
		// 设置查询每页容量
		option.pageCapacity(PAGECAPACITY);
		// 设置半径
		option.radius(RADIUS);
		// 发起查询请求
		mManager.nearbyInfoRequest(option);
	}

	// 位置信息上传/周边检索监听器类
	class MyRadarSearchListener implements RadarSearchListener {

		@Override
		public void onGetClearInfoState(RadarSearchError error) {
			if(error == RadarSearchError.RADAR_NO_ERROR)
				Log.e("用户信息", "用户信息清除成功");
			else
				Log.e("用户信息", "用户信息清除失败");
		}

		// 周边雷达检索返回结果
		@Override
		public void onGetNearbyInfoList(RadarNearbyResult result,
				RadarSearchError error) {
			if (error == RadarSearchError.RADAR_NO_ERROR) {
				// 清除所有marker以及infowindow
				mBaiduMap.clear();
				// 获得所有附近的人员的坐标并在地图上标识出来
				for (int i = 0; i < result.infoList.size(); i++) {
					// 获得每个信息
					RadarNearbyInfo info = result.infoList.get(i);
					// 获得坐标
					LatLng l = info.pt;
					// 获得mobileName信息
					String mobileName = info.mobileName;
					// 在地图上标注出来
					mMarkerProxy.insertLabel(l, R.drawable.ic, mobileName);
					//回调接口传递周边人数给activity
					mNumberOfPeople.numbaer(result.infoList.size());
				}
			}else{
				//清除地图上的所有标记
				mBaiduMap.clear();
				//回调接口传递周边人数给activity
				mNumberOfPeople.numbaer(0);
			}
			
		}

		// 上传监听结果
		@Override
		public void onGetUploadState(RadarSearchError error) {
			if (error == RadarSearchError.RADAR_NO_ERROR) {
				 //Toast.makeText(context, "上传信息成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "上传位置信息失败", Toast.LENGTH_SHORT).show();
			}
		}

	}
	
	public interface NumberOfPeople{
		public void numbaer(int num);
	}

	/**
	 * 移除用户信息
	 */
	public void clearUser() {
		// 移除监听
		mManager.removeNearbyInfoListener(listener);
		// 清除用户信息,不会被其他用户检索到
		mManager.clearUserInfo();
		// 释放资源
		mManager.destroy();
		mManager = null;
	}
	
	/**
	 * 位置连续上传监听器
	 */
	class MyRadarUploadInfoCallback implements RadarUploadInfoCallback{

		@Override
		public RadarUploadInfo onUploadInfoCallback() {
			
			return null;
		}
		
	}

}
