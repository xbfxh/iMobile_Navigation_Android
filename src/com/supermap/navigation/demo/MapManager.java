package com.supermap.navigation.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.supermap.data.Point2D;
import com.supermap.data.Workspace;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.Map;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.dyn.DynamicView;
import com.supermap.navigation.demo.R;

public class MapManager {
	
	static private MapManager mMapManager = null;
	
	private Context		mContext = null;
	
	// 定义地图控件
	private MapView			mMapView        = null;
	private Workspace		mWorkspace      = null;
	private MapControl		mMapControl     = null;
	private Map				mMap            = null;
	private DynamicView		mDynamicView	= null;;
	
	
	static public MapManager getInstance(Context context) {
		if (mMapManager == null) {
			mMapManager = new MapManager(context);
		}
		return mMapManager;
	}
	
	private MapManager(Context context) {
		mContext = context;
	}
	
	/**
	 * 打开工作空间，显示地图
	 * @return
	 */
	public boolean init(MapView mapView) {
        // 获取地图控件
		mMapView    = mapView;
		mMapControl = mMapView.getMapControl();
		mWorkspace = DataManager.getInstance(mContext).getWorkspace();

		mMap = mMapControl.getMap();                                   // 获取地图控件
		mMap.setWorkspace(mWorkspace);   
		
		String mapName = mWorkspace.getMaps().get(0);
		mMapControl.getMap().open(mapName);
        
		mMapControl.getMap().setFullScreenDrawModel(true);
	
    	//设置地图初始的显示范围，放地图出图时就显示的是北京
//		m_mapControl.getMap().setScale(1/458984.375);
//		m_mapControl.getMap().setCenter(new Point2D(12953693.6950684, 4858067.04711915));
		mMapControl.getMap().refresh();
		
		mDynamicView = new DynamicView(mContext, mMapControl.getMap());
//		mMapView.addDynamicView(mDynamicView);
		return true;
	}
	
	public MapControl getMapControl() {
		return mMapControl;
	}
	
	public Map getMap() {
		return mMap;
	}
	
	public MapView getMapView() {
		return mMapView;
	}
	
	public DynamicView getDynamicView() {
		return mDynamicView;
	}
	

	/**
	 * 显示标注点
	 * @param point2D
	 * @param pointName
	 * @param idDrawable
	 */
	public void showLocationPointByCallOut(Point2D point2D,
			String pointName, int idDrawable, CalloutAlignment alignment) {
		// 设置标注
		CallOut callOut = new CallOut(mContext);
		callOut.setStyle(alignment); // 设置标注点的对齐方式：下方对齐
		callOut.setCustomize(true); // 设置自定义背景
		callOut.setLocation(point2D.getX(), point2D.getY()); // 设置标注点坐标
//		View view = LayoutInflater.from(mContext).inflate(R.layout.callout_location, null);
		
//		ImageButton imageView = (ImageButton)view.findViewById(R.id.btn_location_callout);
//		imageView.setBackgroundResource(idDrawable);
		
		ImageView imageView = new ImageView(mContext);
		imageView.setBackgroundResource(idDrawable);
		
		// 显示起点
		callOut.setContentView(imageView);

		mMapView.removeCallOut(pointName);
		mMapView.addCallout(callOut, pointName); // 在地图上显示CallOut标注点，并设置名称
	}
	
	/**
	 * 显示POI点
	 * @param point2D
	 * @param pointName
	 * @param idDrawable
	 */
	public void showPoiPointByCallOut(Point2D point2D,
			String pointName, int idDrawable, CalloutAlignment alignment) {
		// 设置标注
		CallOut callOut = new CallOut(mContext);
		callOut.setStyle(alignment); // 设置标注点的对齐方式：下方对齐
		callOut.setCustomize(true); // 设置自定义背景
		callOut.setLocation(point2D.getX(), point2D.getY()); // 设置标注点坐标
//		View view = LayoutInflater.from(mContext).inflate(R.layout.callout_poi, null);
//		
//		ImageButton imageView = (ImageButton)view.findViewById(R.id.btn_poi_callout);
		
		ImageView imageView = new ImageView(mContext);
		imageView.setBackgroundResource(idDrawable);
		
		// 显示起点
		callOut.setContentView(imageView);

		mMapView.removeCallOut(pointName);
		mMapView.addCallout(callOut, pointName); // 在地图上显示CallOut标注点，并设置名称
	}
}
