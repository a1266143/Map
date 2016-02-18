package com.example.proxy;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.map.R;

/**
 * 设置标记Maker的类
 * 
 * @author Administrator
 * 
 */
public class MarkerProxy {
	private Context context;
	private BaiduMap mBaiduMap;
	//private LatLng lastLatLng;
	//y轴偏移量
	public static final int offsetY = -47;

	public MarkerProxy(Context context, BaiduMap mBaiduMap) {
		this.context = context;
		this.mBaiduMap = mBaiduMap;
		
	}

	/**
	 * 在地图上插标记
	 * 
	 * @param latLng
	 *            经纬度对象
	 * @param iconRes
	 *            图标资源
	 */
	public void insertLabel(LatLng latLng,int iconRes,String moblieName){
		try {
			//将图标先清除
			//mBaiduMap.clear();
			// 构建maker图标
			BitmapDescriptor bitmap  = BitmapDescriptorFactory.fromResource(iconRes);
			//构建makeroption，用于在地图上添加maker
			OverlayOptions option = new MarkerOptions().position(latLng).icon(bitmap);
			//在地图上添加marker
			mBaiduMap.addOverlay(option);
			//显示infowindow
			//insertInfoWindow(moblieName, latLng);
		} catch (Exception e) {
			Toast.makeText(context, "资源图标设置不正确", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 在地图上显示infoWindow
	 * @param comments 上传信息
	 * @param l 坐标点
	 */
	public void insertInfoWindow(String mobileName,LatLng l){
		Button button = new Button(context);
		button.setText(mobileName);
		button.setTextColor(Color.BLACK);
		button.setBackgroundResource(R.drawable.popup);
		InfoWindow mInfoWindow = new InfoWindow(button, l, offsetY);
		//在地图上显示infoWindow
		mBaiduMap.showInfoWindow(mInfoWindow);
	}
}
