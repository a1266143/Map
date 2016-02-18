package com.example.proxy;

import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.SuggestAddrInfo;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.util.BikingRouteOverlay;
import com.example.util.DrivingRouteOverlay;
import com.example.util.WalkingRouteOverlay;

public class DrivingRouteProxy {

	private RoutePlanSearch mSearch;
	private Context context;
	private BaiduMap mBaiduMap;
	private TerminalMistake mTerminalMistake;

	public DrivingRouteProxy(Context context, BaiduMap mBaiduMap,TerminalMistake t) {
		this.mTerminalMistake = t;
		this.context = context;
		this.mBaiduMap = mBaiduMap;
		// 创建线路规划实例
		mSearch = RoutePlanSearch.newInstance();
		// 新建线路监听器实例
		MyOnGetRoutePlanResultListener listener = new MyOnGetRoutePlanResultListener();
		// 设置监听器
		mSearch.setOnGetRoutePlanResultListener(listener);
	}

	// 开始规划驾车路线
	/*
	 * public void startDrivingPlan(String startNode, String endNode) {
	 * mBaiduMap.clear(); PlanNode stNode =
	 * PlanNode.withCityNameAndPlaceName("成都", startNode); PlanNode enNode =
	 * PlanNode.withCityNameAndPlaceName("成都", endNode);
	 * //发起驾车线路规划请求，请求成功后在监听器中处理 mSearch.drivingSearch(new
	 * DrivingRoutePlanOption().from(stNode).to( enNode)); }
	 */

	// 驾车路线
	public void startDrivingPlan(PlanNode stNode, PlanNode edNode) {
		mBaiduMap.clear();
		mSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(
				edNode));
	}

	// 开始规划步行路线
	/*public void startWalkingPlan(String startNode, String endNode) {
		mBaiduMap.clear();
		PlanNode stNode = PlanNode.withCityNameAndPlaceName("成都", startNode);
		PlanNode enNode = PlanNode.withCityNameAndPlaceName("成都", endNode);
		mSearch.walkingSearch(new WalkingRoutePlanOption().from(stNode).to(
				enNode));
	}*/

	//走路路线
	public void startWalkingPlan(PlanNode stNode,PlanNode edNode){
		mBaiduMap.clear();
		mSearch.walkingSearch(new WalkingRoutePlanOption().from(stNode).to(edNode));
	}
	// 开始规划骑车路线
	/*public void startBikingPlan(String startNode, String endNode) {
		mBaiduMap.clear();
		PlanNode stNode = PlanNode.withCityNameAndPlaceName("成都", startNode);
		PlanNode enNode = PlanNode.withCityNameAndPlaceName("成都", endNode);
		mSearch.bikingSearch(new BikingRoutePlanOption().from(stNode)
				.to(enNode));
	}*/
	public void startBikingPlan(PlanNode stNode, PlanNode edNode){
		mBaiduMap.clear();
		mSearch.bikingSearch(new BikingRoutePlanOption().from(stNode).to(edNode));
	}

	class MyOnGetRoutePlanResultListener implements
			OnGetRoutePlanResultListener {

		@Override
		public void onGetBikingRouteResult(BikingRouteResult result) {
			// 自行车线路规划
			// 驾车线路规划
			if (result.error == null
					|| result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				Toast.makeText(context, "检索词有歧义", Toast.LENGTH_SHORT).show();
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				BikingRouteOverlay overlay = new BikingRouteOverlay(mBaiduMap);
	            overlay.setData(result.getRouteLines().get(0));
	            overlay.addToMap();
	            overlay.zoomToSpan();
			}
		}

		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult result) {
			// 驾车线路规划
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				//获得建议路线
				SuggestAddrInfo info = result.getSuggestAddrInfo();
				//获得终点地址选择列表
				List<PoiInfo> endList = info.getSuggestEndNode();
				List<PoiInfo> startList = info.getSuggestStartNode();
				//回调接口
				mTerminalMistake.terminalLocation(startList, endList);
				return;
			}
			if (result.error == null
					|| result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
				overlay.setData(result.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();
			}
		}

		@Override
		public void onGetTransitRouteResult(TransitRouteResult result) {
			// 公交线路

		}

		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult result) {
			// 走路
			if (result.error == null
					|| result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(context, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				Toast.makeText(context, "检索词有歧义", Toast.LENGTH_SHORT).show();
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
	            overlay.setData(result.getRouteLines().get(0));
	            overlay.addToMap();
	            overlay.zoomToSpan();
			}
		}

	}

	// 释放检索实例
	public void destroySearch() {
		mSearch.destroy();
	}
	
	public interface TerminalMistake{
		public void terminalLocation(List<PoiInfo> startList,List<PoiInfo> endList);
	}
}
