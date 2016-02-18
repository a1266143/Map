package com.example.proxy;


import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.util.PoiOverlay;

/**
 * Poi搜索
 * 
 * @author Administrator
 * 
 */
public class PoiSearchProxy {
	
	//poi搜索返回数
	public static final int PAGE_NUM = 10;

	private BaiduMap mBaiduMap;
	private PoiSearch mPoiSearch;
	private MyOnGetPoiSearchReslutListener listener;
	

	public PoiSearchProxy(BaiduMap mBaiduMap) {
		this.mBaiduMap = mBaiduMap;
		// 新建Poi检索实例
		mPoiSearch = PoiSearch.newInstance();
		// 创建监听器实例
		listener = new MyOnGetPoiSearchReslutListener();
		// 注册监听器
		mPoiSearch.setOnGetPoiSearchResultListener(listener);
	}

	public void search() {
		startSearchInCity("成都", "美食", PAGE_NUM);
	}

	/**
	 * 开始城市检索
	 * 
	 * @param city
	 *            城市
	 * @param keyword
	 *            关键词
	 * @param pageNum
	 *            单页容量
	 */
	public void startSearchInCity(String city, String keyword,
			int pageNum) {
		// 发送搜索请求
		// 新建搜索配置对象
		PoiCitySearchOption option = new PoiCitySearchOption();
		option.city(city);
		option.keyword(keyword);
		option.pageNum(pageNum);
		mPoiSearch.searchInCity(option);
	}

	/**
	 * 释放Poi检索实例
	 */
	public void destroy() {
		if (mPoiSearch != null) {
			mPoiSearch.destroy();
		}
	}

	/**
	 * 检索结果返回监听器
	 * 
	 * @author Administrator
	 * 
	 */
	class MyOnGetPoiSearchReslutListener implements
			OnGetPoiSearchResultListener {

		@Override
		public void onGetPoiDetailResult(PoiDetailResult result) {
			// 获取POI检索结果

		}

		@Override
		public void onGetPoiResult(PoiResult result) {
			// 获取Place详情页检索结果
			// 获取门址结果
			if(result.error == SearchResult.ERRORNO.NO_ERROR){
				mBaiduMap.clear();
				PoiOverlay overlay = new PoiOverlay(mBaiduMap);
				mBaiduMap.setOnMarkerClickListener(overlay);
				overlay.setData(result);
				overlay.addToMap();
				overlay.zoomToSpan();
				return;
			}
		}

	}
}
