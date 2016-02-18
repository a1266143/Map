package com.example.map;

import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.route.PlanNode;
import com.example.proxy.DingWeiProxy;
import com.example.proxy.DrivingRouteProxy;
import com.example.proxy.TraceProxy;
import com.example.proxy.TraceQueryProxy;
import com.example.proxy.DrivingRouteProxy.TerminalMistake;
import com.example.proxy.GeoCodingProxy;
import com.example.proxy.GeoCodingProxy.EncodeAddress;
import com.example.proxy.LeiDaProxy;
import com.example.proxy.TraceQueryProxy.TraceQueryCallback;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class LeiDaActivity extends Activity implements
		DingWeiProxy.LocationPort, LeiDaProxy.NumberOfPeople, EncodeAddress,TerminalMistake,TraceQueryCallback,TraceProxy.TraceUpload {

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private BDLocation currentLocation;
	// 定位
	private DingWeiProxy mDingWeiProxy;
	//雷达
	private LeiDaProxy mLeiDaProxy;
	// Poi搜索
	// private PoiSearchProxy mPoiSearch;
	// 地理编码
	private GeoCodingProxy mGeoCodingProxy;
	// 线路规划
	private DrivingRouteProxy mDrivingRouteProxy;
	//轨迹服务
	private TraceProxy mTraceProxy;
	//轨迹实时位置查询
	private TraceQueryProxy mTraceQueryProxy;

	private Button btnLeida;

	//起点和终点位置
	private PlanNode startPlanNode,endPlanNode;
	
	// 是否正在导航
	private boolean navigation;
	//路线图是否传入的我的位置
	@SuppressWarnings("unused")
	private boolean isMyLocation ;
	

	// 我的位置
	private String currentAddress = null;
	private Button myPosition;
	private TextView numberOfPeopleTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lei_da);
		// 初始化
		init();
		// 首先进行定位(代理模式)
		mDingWeiProxy.startLocation();
		// 搜索成都美食
		// mPoiSearch.search();
	}

	/**
	 * 初始化
	 */
	public void init() {
		btnLeida = (Button) findViewById(R.id.btnLeida);
		numberOfPeopleTv = (TextView) findViewById(R.id.numberofpeople);
		mMapView = (MapView) findViewById(R.id.leidamapView);
		mMapView.showZoomControls(false);
		mBaiduMap = mMapView.getMap();
		mDingWeiProxy = new DingWeiProxy(getApplicationContext(), mBaiduMap,	this);
		mLeiDaProxy = new LeiDaProxy(getApplicationContext(), mBaiduMap, this);
		mGeoCodingProxy = new GeoCodingProxy(this);
		mDrivingRouteProxy = new DrivingRouteProxy(this, mBaiduMap,this);
		mTraceProxy = new TraceProxy(getApplicationContext(),this);
		mTraceQueryProxy = new TraceQueryProxy(getApplicationContext(),this);
		// mPoiSearch = new PoiSearchProxy(mBaiduMap);
		myPosition = (Button) findViewById(R.id.myPosition);
		MyOnClickListener listener = new MyOnClickListener();
		//我的位置设置监听器
		myPosition.setOnClickListener(listener);
		//出发设置监听器
		findViewById(R.id.go).setOnClickListener(listener);
		//开启雷达监听器
		btnLeida.setOnClickListener(listener);
		//开启轨迹服务按钮设置监听器
		findViewById(R.id.traceStart).setOnClickListener(listener);
		//停止轨迹服务按钮监听器
		findViewById(R.id.traceStop).setOnClickListener(listener);
		//轨迹查询按钮监听器
		findViewById(R.id.btnQuery).setOnClickListener(listener);
		//轨迹历史查询按钮设置监听器
		findViewById(R.id.btnQueryHistory).setOnClickListener(listener);
	}
	
	//主界面按钮监听器
	class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.myPosition:
				if (currentLocation != null)
					// 更新地图状态
					mDingWeiProxy.updateMapStatute(new LatLng(currentLocation
							.getLatitude(), currentLocation.getLongitude()));
				break;
			case R.id.go:
				if (currentAddress != null) {
					Intent intent = new Intent(LeiDaActivity.this,
							GoActivity.class);
					intent.putExtra("currentAddress", currentAddress);
					startActivityForResult(intent, 0);
				}
				break;
			case R.id.btnLeida:
				navigation = false;
				btnLeida.setEnabled(false);
				numberOfPeopleTv.setVisibility(View.VISIBLE);
				break;
			case R.id.traceStart:
				if(!mTraceProxy.traceIsStart())
					//开启轨迹服务
					mTraceProxy.startTrace();
				break;
			case R.id.traceStop:
				if(mTraceProxy.traceIsStart())
					//停止轨迹服务
					mTraceProxy.stopTrace();
				break;
			case R.id.btnQuery:
				mTraceQueryProxy.queryRealPosition("mycar");
				break;
			case R.id.btnQueryHistory:
				mTraceQueryProxy.queryHistoryTrace("mycar");
				break;
			}
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == 0) {
			int checkedID = data.getIntExtra("checkedID", -1);
			String stPosition = data.getStringExtra("stPosition");
			String endPosition = data.getStringExtra("edPosition");
			isMyLocation = data.getBooleanExtra("mineLocation", true);
			endPlanNode = PlanNode.withCityNameAndPlaceName("成都", endPosition);
			switch (checkedID) {
			case R.id.jiache:
				// 如果起点是我的位置，就用传入经纬度的方法
				if (stPosition.equals(currentAddress)) {
					startPlanNode = PlanNode.withLocation(new LatLng(currentLocation
							.getLatitude(), currentLocation.getLongitude()));
				} else {
					startPlanNode = PlanNode.withCityNameAndPlaceName("成都", stPosition);
				}
				mDrivingRouteProxy.startDrivingPlan(startPlanNode, endPlanNode);
				break;
			case R.id.buxing:
				PlanNode st1;
				if (stPosition.equals(currentAddress)) {
					st1 = PlanNode.withLocation(new LatLng(currentLocation
							.getLatitude(), currentLocation.getLongitude()));
				} else {
					st1 = PlanNode.withCityNameAndPlaceName("成都", stPosition);
				}
				mDrivingRouteProxy.startWalkingPlan(st1, endPlanNode);
				break;
			case R.id.qiche:
				// 如果起点是我的位置，就用传入经纬度的方法
				PlanNode st2;
				if (stPosition.equals(currentAddress))
					st2 = PlanNode.withLocation(new LatLng(currentLocation
							.getLatitude(), currentLocation.getLongitude()));
				else
					st2 = PlanNode.withCityNameAndPlaceName("成都", stPosition);
				mDrivingRouteProxy.startBikingPlan(st2, endPlanNode);
				break;
			}
			// 将雷达关闭
			navigation = true;
			btnLeida.setEnabled(true);
			//将tv隐藏
			numberOfPeopleTv.setVisibility(View.GONE);
		}
	}

	/**
	 * 实时获得我的位置定位location
	 */
	@Override
	public void updateLocation(BDLocation location) {
		currentLocation = location;
		((TextView) findViewById(R.id.myPositionTv)).setText("我的位置:"
				+ location.getLocationDescribe());
		LatLng l = new LatLng(location.getLatitude(), location.getLongitude());
		// 反向地理编码
		mGeoCodingProxy.reverseGeoCoding(l);
		// 上传位置信息
		mLeiDaProxy.uploadInfo("", l);
		// 检索周边并在地图上标识出来
		// 如果没有导航才将雷达开启
		if (!navigation) {
			mLeiDaProxy.postionSearch(l);
		}

	}

	@Override
	protected void onDestroy() {
		// 停止定位
		mDingWeiProxy.stopLocation();
		// 清除用户信息
		mLeiDaProxy.clearUser();
		//清除线路检索实例
		mDrivingRouteProxy.destroySearch();
		// 清除Poi搜索
		// mPoiSearch.destroy();
		super.onDestroy();
	}

	// 周边雷达人数
	@Override
	public void numbaer(int num) {
		((TextView) findViewById(R.id.numberofpeople)).setText("你周围20000米内有："
				+ num + "个人在用此APP");
	}

	// 反向地理编码获得我的位置
	@Override
	public void getAddress(String address) {
		currentAddress = address;
	}
	

	@Override
	public void terminalLocation(List<PoiInfo> startList,List<PoiInfo> endList) {
		
		//list为null表示无歧义
		//如果终点有歧义
		if(startList==null&&endList!=null){
				mDrivingRouteProxy.startDrivingPlan(startPlanNode,
					PlanNode.withLocation(endList.get(0).location));
		}
		//否则如果起点有歧义
		else if(startList!=null&&endList==null){
			//如果起点是我的位置的话，肯定不会有歧义，所以直接传入建议点的位置
			mDrivingRouteProxy.startDrivingPlan(PlanNode.withLocation(startList.get(0).location),
					endPlanNode);
		}
		//如果都有歧义
		else{
			mDrivingRouteProxy.startDrivingPlan(PlanNode.withLocation(startList.get(0).location),
					PlanNode.withLocation(endList.get(0).location));
		}
	}

	/**
	 * TraceQueryCallback回调接口
	 */
	@Override
	public void query(int type,String info) {
		Log.e("回调接口", "调用了回调接口"+type);
		switch (type) {
		//历史轨迹查询
		case TraceQueryProxy.TYPE_QUERY_HISTORY:
			Looper.prepare();
			Toast.makeText(getApplicationContext(), "历史轨迹查询成功："+info, Toast.LENGTH_LONG).show();
			Looper.loop();
			break;
		//实时位置查询
		case TraceQueryProxy.TYPE_QUERY_REALTIME:
			Looper.prepare();
			Toast.makeText(this, "实时位置查询成功："+info, Toast.LENGTH_LONG).show();
			Looper.loop();
			break;
		}
	}

	/**
	 * 轨迹上传回调方法
	 */
	@Override
	public void traceUpload(int arg0, String arg1) {
		if(arg0==0&&arg1.equals("0")){
			Toast.makeText(getApplicationContext(), "轨迹服务停止成功", Toast.LENGTH_SHORT).show();
			findViewById(R.id.traceStop).setEnabled(false);
			findViewById(R.id.traceStart).setEnabled(true);
		}
		else{
			findViewById(R.id.traceStop).setEnabled(true);
			findViewById(R.id.traceStart).setEnabled(false);
			Toast.makeText(getApplicationContext(), "轨迹服务开启成功，你可以在其它终端(PC,APP)上查看此设备的实时位置或者历史运动轨迹", Toast.LENGTH_LONG).show();
		}
			
	}
	
}
