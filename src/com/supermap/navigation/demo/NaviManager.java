package com.supermap.navigation.demo;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import com.supermap.android.configuration.DefaultDataConfiguration;
import com.supermap.data.Environment;
import com.supermap.data.GeoStyle;
import com.supermap.data.Geometrist;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.mapping.MapControl;
import com.supermap.navi.Navigation;

public class NaviManager {

	static private NaviManager mNaviManager = null;
	
	private Context mContext = null;
	private Navigation mNavigation;
	private MapControl mMapControl;
	private int routeAnalystMode = 0;                    // 0:推荐模式; 1:时间最快模式; 2:距离最短模式; 3:最少收费模式
	
	private double mDistanceToMe;
	private Point2D mPtFrom;
	private Point2D mPtTo;
	private Point2D mPtLocation;
	private Point2D mPtPoi;
	
	static public NaviManager getInstance(Context context) {
		if (mNaviManager == null) {
			mNaviManager = new NaviManager(context);
		}
		return mNaviManager;
	}
	
	private NaviManager(Context context) {
		mContext = context;
		mMapControl = MapManager.getInstance(mContext).getMapControl();
		mNavigation = mMapControl.getNavigation();
		
		init();
	}
	
	private void init() {
		mPtFrom = new Point2D();
		mPtTo = new Point2D();
		mPtLocation = new Point2D();
		mPtPoi = new Point2D();

		mPtLocation.setX(116.5052061584224);
		mPtLocation.setY(39.985749510080936);
	
		GeoStyle style = new GeoStyle();
		if (Environment.isOpenGLMode()) {
			style.setLineSymbolID(964882);
		} else {
			style.setLineSymbolID(964883);
		}
		
		mNavigation.setRouteStyle(style);
	}
	
	/**
	 * 获取起点
	 */
	public Point2D getStartPoint(){	
		return mPtFrom;
	}
	
	/**
	 * 获取目的点
	 */
	public Point2D getDestinationPoint(){		
		return mPtTo;
	}
	
	/**
	 * 获取当前位置点
	 */
	public Point2D getLocationPoint(){		
		return mPtLocation;
	}
	
	/**
	 * 获取POI点
	 */
	public Point2D getPoiPoint(){		
		return mPtPoi;
	}
	
	/**
	 * 设置起点
	 * @param x 起点x坐标
	 * @param y 起点y坐标
	 */
	public void setStartPoint(double x,double y){
		if(mNavigation != null){
			mNavigation.setStartPoint(x, y);
		}
		
		mPtFrom.setX(x);
		mPtFrom.setY(y);
	}
	
	/**
	 * 设置目的点
	 * @param x 终点x坐标
	 * @param y 终点y坐标
	 */
	public void setDestinationPoint(double x,double y){
		if(mNavigation != null){
			mNavigation.setDestinationPoint(x, y);
		}
		
		mPtTo.setX(x);
		mPtTo.setY(y);
	}
	
	public void setLocationPoint(double x,double y){		
		mPtLocation.setX(x);
		mPtLocation.setY(y);
	}
	
	public void setPoiPoint(double x,double y){		
		mPtPoi.setX(x);
		mPtPoi.setY(y);
	}
	
	/**
	 * 设置导航模式
	 * @param routeAnalystMode
	 */
	public void setRouteAnalystMode(int routeAnalystMode) {
		this.routeAnalystMode = routeAnalystMode;
	}
	
	/**
	 * 路径分析
	 */
	public int routeAnalyst(){
		int result = 0;
		if(mNavigation != null){
			result = mNavigation.routeAnalyst(routeAnalystMode);
			mMapControl.getMap().refresh();
		}
		return result;
	}
	
	/**
     * 开始路径分析
     * @param startpoint2D
     * @param destpoint2D
     */
// 	public void routeAnalyst() {
// 		
// 		if((mPtFrom == null ) || (mPtTo == null) ){
// 			Toast.makeText(mContext, "无法获取位置信息", Toast.LENGTH_SHORT).show();
// 			return;
// 		}
// 		
// 		final ProgressDialog dialog = new ProgressDialog(mContext);
// 		dialog.setCancelable(false);
// 		dialog.setCanceledOnTouchOutside(false);
// 		dialog.setMessage("路径分析中....");
// 		dialog.show();
// 		new Thread(new Runnable() {
//
// 			@Override
// 			public void run() {
// 				// TODO Auto-generated method stub
// 			    // 导航路径分析				
// 		    	int analystResult = mNavigation.routeAnalyst(routeAnalystMode);
// 		    	mMapControl.getMap().refresh();
// 				dialog.dismiss();
//                if(analystResult == 0){
//                    System.out.println("路径分析失败");
//                    Runnable action = new Runnable() {
//
//     					@Override
//     					public void run() {
//     						// TODO Auto-generated method stub
//     						Toast.makeText(mContext, "路径分析失败", Toast.LENGTH_SHORT).show();
//     					}
//     				};
//     				((Activity)mContext).runOnUiThread(action);
//                }
// 			}
// 		}).start();
// 	}

//	public double getDistance(){
//		Point2Ds pts = new Point2Ds();
//		pts.add(mPtFrom);
//		pts.add(mPtTo);
//		
//		if (pts.getCount() < 2) {
//			return -1;
//		} 
//		mDistanceToMe = Geometrist.computeGeodesicDistance(pts, 6378137.0, 0.003352811);
//		return mDistanceToMe;
//	}
	
	// 显示停止导航对话框
	public void stopNaviDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("提示");
		builder.setMessage("确认退出导航？");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				mNavigation.stopGuide();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		builder.create().show();
	}
	
	/**
	 *  初始化导航数据
	 */
	public void initNaviData() {
		mNavigation = mMapControl.getNavigation();
		
		// 设置导航数据, 
		mNavigation.connectNaviData(DefaultDataConfiguration.MapDataPath);
	}
}
