package com.example.proxy;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

//专门用来定位的类
public class DingWeiProxy {
	public static final String COORTYPE_BAIDU = "bd09ll";
	//定位时间间隔，小于1000ms即只定位一次
	public static final int SCANSPAN = 3000;
	//缩放级别
	public static final int ZOOM = 18;
	
	private boolean changeStateFirst = true;
	
	private Context context;
	private BaiduMap mBaiduMap;
	private LocationClient mLocationClient;
	private LocationPort port;
	private MyLocationlistener listener;
	
	
	public DingWeiProxy(Context context, BaiduMap mBaiduMap,LocationPort port) {
		super();
		this.context = context;
		this.mBaiduMap = mBaiduMap;
		this.port = port;
		listener = new MyLocationlistener();
	}

	/**
	 * 开始定位
	 */
	public void startLocation(){
		mLocationClient = new LocationClient(context);
		//初始化定位参数
		initLocation();
		//注册定位监听器
		mLocationClient.registerLocationListener(listener);
		//开始定位
		mLocationClient.start();
	}
	
	/**
	 * 停止定位
	 */
	public void stopLocation(){
		//如果已经开始定位
		if(mLocationClient!=null&&mLocationClient.isStarted()){
			mLocationClient.stop();
		}
	}

	/**
	 * 初始化定位参数
	 */
	public void initLocation(){
		//首先开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		//定位参数设置类
		LocationClientOption option = new LocationClientOption();
		//设置需要设备方向
		option.setNeedDeviceDirect(true);
		/**
		 * 设置定位模式，高精度，低功耗，仅设备
		 * 设置成高精度，更费电
		 */
		//option.setLocationMode(LocationMode.Hight_Accuracy);
		//设置定位坐标系，百度的必须设置成bd09ll
		option.setCoorType(COORTYPE_BAIDU);
		//设置是否需要地址信息
		option.setIsNeedAddress(true);
		//设置定位间隔，>=1000ms就是循环定位
		option.setScanSpan(SCANSPAN);
		//设置是否需要位置语义化结果,比如在某某地方附近
		option.setIsNeedLocationDescribe(true);
		//为定位对象LocationClient设置配置参数
		mLocationClient.setLocOption(option);
	}
	
	
	/**
	 * 接收异步返回的定位结果
	 */
	class MyLocationlistener implements BDLocationListener{

		/**
		 * location为返回的定位对象，包含了相关位置信息
		 */
		@Override
		public void onReceiveLocation(BDLocation location) {
			//构造定位数据
			MyLocationData locData = new MyLocationData.Builder()
			.accuracy(location.getRadius())
			.direction(location.getDirection())
			.latitude(location.getLatitude())
			.longitude(location.getLongitude()).build();
			//设置定位数据
			mBaiduMap.setMyLocationData(locData);
			//不要忘记更新地图状态
			//如果是第一次定位（changeStateFirst == true），要更新地图状态
			if(changeStateFirst == true){
				updateMapStatute(new LatLng(location.getLatitude(), location.getLongitude()));
				changeStateFirst = false;
			}
			//回调接口将location对象传送给调用者
			port.updateLocation(location);
			//关闭定位
			//stopLocation();
		}
		
	}
	
	/**
	 * 更新地图状态,传入经纬度对象，以及缩放级别
	 */
	public void updateMapStatute(LatLng latLng){
		MapStatusUpdate status = MapStatusUpdateFactory.newLatLngZoom(latLng, ZOOM);
		mBaiduMap.setMapStatus(status);
	}
	
	/**
	 * 新建回调接口，目的是将location传递给调用者
	 */
	public interface LocationPort{
		public void updateLocation(BDLocation location);
	}
	
}
