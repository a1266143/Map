package com.example.proxy;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;


/**
 * 地理编码类：
 * 正向地理编码：将具体地址转换为具体坐标
 * 反向地理编码：将具体坐标转换为具体地址
 * @author Administrator
 *
 */
public class GeoCodingProxy {
	
	
	private GeoCoder coder;
	private MyOnGetCoderResultListener listener;
	//回调接口
	private EncodeAddress mEncodeAddress;
	
	public GeoCodingProxy(EncodeAddress mEncodeAddress) {
		//新建地理编码查询实例
		coder = GeoCoder.newInstance();
		//新建监听器对象
		listener = new MyOnGetCoderResultListener();
		//设置监听器
		coder.setOnGetGeoCodeResultListener(listener);
		//回调接口实例化
		this.mEncodeAddress = mEncodeAddress;
	}

	/**
	 * 正向地理编码（地址信息->经纬度）
	 * @param city 城市
	 * @param address 具体地址
	 */
	public void geoCoding(String city,String address){
		//发起正向地理编码检索
		coder.geocode(new GeoCodeOption().city(city).address(address));
	}
	
	/**
	 * 反向地理编码（经纬度->地址信息）
	 * @param l 坐标
	 */
	public void reverseGeoCoding(LatLng l){
		coder.reverseGeoCode(new ReverseGeoCodeOption().location(l));
	}
	
	
	class MyOnGetCoderResultListener implements OnGetGeoCoderResultListener{

		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			//正向地理编码
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			//反向地理编码
			//回调接口，传回给Activity处理
			mEncodeAddress.getAddress(result.getAddress());
		}
		
	}
	
	public interface EncodeAddress{
		public void getAddress(String address);
	}
}
