package com.supermap.navigation.demo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;
import com.supermap.android.app.MyApplication;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navi.NaviPath;
import com.supermap.navi.NaviStep;
import com.supermap.navi.Navigation;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class RouteiDetailsView extends BaseView {

	static private RouteiDetailsView mRouteiDetailsView = null;
	
	private Context mContext = null;
	private View mMainView = null;
	private MapView mMapView = null;
	private MapControl mMapControl = null;	
	private Navigation mNavigation = null;
	
	private RelativeLayout mViewMainTop = null;
	private RelativeLayout mViewMainControl = null;
	
	private RelativeLayout mViewMainBottom = null;	
	private View mViewRouteDetails = null;
	private ImageButton btnRouteDetailsBack = null;
	private TextView txtRouteDetailsDistance = null;
	private TextView txtRouteDetailsTime = null;
	private Button btnSimNavi = null;
	
	private ListView lstRouteResult = null;
	private Vector<String> vtName = new Vector<String>();
	private Vector<Integer> vtResID = new Vector<Integer>();
	
	static public RouteiDetailsView getInstance(Context context, MapView mapView, View mainView) {
		if (mRouteiDetailsView == null) {
			mRouteiDetailsView = new RouteiDetailsView(context, mapView, mainView);
		}
		return mRouteiDetailsView;
	}
	
	private RouteiDetailsView(Context context, MapView mapView, View mainView) {
		mContext = context;
		mMainView = mainView;
		mMapView = mapView;
		mMapControl = mMapView.getMapControl();
		mNavigation = mMapControl.getNavigation();
		initView();
	}
	
	/**
	 *  初始化化界面显示，及界面控件设置
	 */
	private void initView() {	
		mViewMainBottom = (RelativeLayout)mMainView.findViewById(R.id.ly_main_bottom);
		mViewMainControl = (RelativeLayout)mMainView.findViewById(R.id.ly_main_control);
		mViewMainTop= (RelativeLayout)mMainView.findViewById(R.id.ly_main_top);
		
		mViewRouteDetails= LayoutInflater.from(mContext).inflate(R.layout.route_details, null);
		mViewRouteDetails.setClickable(true);
		
		btnRouteDetailsBack = (ImageButton) mViewRouteDetails.findViewById(R.id.btn_route_details_back);
		btnRouteDetailsBack.setOnClickListener(buttonOnClickListener);
		
		lstRouteResult = (ListView) mViewRouteDetails.findViewById(R.id.lst_route_details);
		lstRouteResult.setAdapter(new RouteSearchResultAdapter());
		
		txtRouteDetailsDistance = (TextView)mViewRouteDetails.findViewById(R.id.txt_route_details_distance);
		txtRouteDetailsTime = (TextView)mViewRouteDetails.findViewById(R.id.txt_route_details_time);

		btnSimNavi = (Button) mViewRouteDetails.findViewById(R.id.btn_route_details_navi);
		btnSimNavi.setOnClickListener(buttonOnClickListener);
	}
	
	OnClickListener buttonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {	
			case R.id.btn_route_details_back:			
				ViewManager.getInstance().fallback();
				break;
			case R.id.btn_route_details_navi:			
				startGuide();
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 显示路径详情
	 */
	private void showRouteDetail() {
		NaviPath naviPath = mNavigation.getNaviPath();	
		if (naviPath == null) {
			return;
		}
		
		double lengthAll = naviPath.getLength();
		showDistance(lengthAll);
		double time = naviPath.getTime();
		showtTime(time);
		
		ArrayList<NaviStep> naviSteps= naviPath.getStep();
		
		int resID = R.drawable.nsdk_drawable_rg_ic_turn_front_s;
		
		vtName.clear();
		vtResID.clear();
		vtName.add("从当前位置出发");
		vtResID.add(resID);
		
		for (int i = 0; i < naviSteps.size(); i++) {	
			StringBuffer strBuffer = new StringBuffer();
			
			NaviStep naviStep = naviSteps.get(i);
			int length = (int) naviStep.getLength();
			int toSwerve = naviStep.getToSwerve();
			
			String strSwerve;
			switch (toSwerve) {
			case 0:
				strSwerve = "直行";
				resID = R.drawable.nsdk_drawable_rg_ic_turn_front_s;
				break;
			case 1:
				strSwerve = "左前方";
				resID = R.drawable.nsdk_drawable_rg_ic_turn_left_front_s;
				break;
			case 2:
				strSwerve = "右前方";
				resID = R.drawable.nsdk_drawable_rg_ic_turn_right_front_s;
				break;
			case 3:
				strSwerve = "左转";
				resID = R.drawable.nsdk_drawable_rg_ic_turn_left_s;
				break;
			case 4:
				strSwerve = "右转";
				resID = R.drawable.nsdk_drawable_rg_ic_turn_right_s;
				break;
			case 5:
				strSwerve = "左后转弯";
				resID = R.drawable.nsdk_drawable_rg_ic_turn_left_back_s;
				break;
			case 6:
				strSwerve = "右后转弯";
				resID = R.drawable.nsdk_drawable_rg_ic_turn_right_back_s;
				break;
			case 7:
				strSwerve = "调头";
				resID = R.drawable.nsdk_drawable_rg_ic_turn_back_s;
				break;
			case 8:
				strSwerve = "右转弯绕行至左";
				resID = R.drawable.nsdk_drawable_rg_ic_turn_left_s;				
				break;
			case 9:
				strSwerve = "右转弯";
				resID = R.drawable.nsdk_drawable_rg_ic_turn_right_s;
				break;
			case 10:
				strSwerve = "环岛";
				resID = R.drawable.nsdk_drawable_rg_ic_turn_ring_in_s;
				break;
			default:
				strSwerve = "直行";
				resID = R.drawable.nsdk_drawable_rg_ic_turn_front_s;
				break;
			}
			
			strBuffer.append("行驶" + length + "米后");			
			
			if (i == naviSteps.size()-1) {
				strBuffer.append("，到达目的地");
			} else {
				strBuffer.append(strSwerve);
				
				NaviStep naviStepNext = naviSteps.get(i+1);
				String name = naviStepNext.getName();
				if (name.isEmpty()) {
					name = "无名路";
				}
				strBuffer.append("，进入" + name);
			}

			vtResID.add(resID);
			vtName.add(strBuffer.toString());
		}
	}
	
	private void startGuide() {
		mNavigation.startGuide(1);
		
		BaseView view = new BaseView() {
			
			@Override
			public void show() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void close() {
				// TODO Auto-generated method stub
				
			}
		};
		
		mMapControl.zoomTo(1/9999.0, 100);
		mMapControl.getMap().refresh();
		
		ViewManager.getInstance().addView(view);
		close();
		mViewMainControl.setVisibility(View.INVISIBLE);
		mViewMainTop.setVisibility(View.INVISIBLE);
	}
	
	public void close() {
		mViewMainBottom.removeView(mViewRouteDetails);
		MyApplication.getInstance().setLongPressEnable(true);
	}
	
	public void show() {
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mViewMainBottom.removeView(mViewRouteDetails);
		mViewMainBottom.addView(mViewRouteDetails, param);
		
		showRouteDetail();
		
		MyApplication.getInstance().setLongPressEnable(false);
	}

	private void showDistance(double distance) {
		DecimalFormat df = new DecimalFormat("0.0");		
		String strDistance;
		
		if (distance < 0) {
			strDistance = 0 + "米";
		} else if (distance < 1000) {
			strDistance = (int)distance + "米";
		} else {
			strDistance = df.format(distance/1000.0) + "公里";
		}
				
		txtRouteDetailsDistance.setText("距离：" + strDistance);
	}
	
	private void showtTime(double time) {
		double routeRemainTime = time;	
		String strRouteRemainTime;
		
		if (routeRemainTime < 2) {
			strRouteRemainTime = "少于一分钟后到达";
		} else if (routeRemainTime < 60) {
			strRouteRemainTime = (int)(routeRemainTime) + "分钟";
		} else {
			int hour = (int) (routeRemainTime/60);
			int min = (int) (routeRemainTime%60);
			if (min == 0) {
				strRouteRemainTime = hour+"小时";
			} else {
				strRouteRemainTime = hour+"小时"+min+"分钟";
			}		
		}
				
		txtRouteDetailsTime.setText("时间：" + strRouteRemainTime);
	}
	
	class Holder {
		public TextView textView = null;
	}
	
	public class RouteSearchResultAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if(vtName != null){
				int nCount = vtName.size();
				return nCount;
			}
			
			return 0;
		}

		@Override
		public Object getItem(int itemPosition) {

			return null;
		}

		@Override
		public long getItemId(int itemPosition) {
			return itemPosition;
		}

		@Override
		public View getView(final int itemPosition, View convertView, ViewGroup arg2) {
			
			Holder holder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.poi_item, null);
				TextView txtPoiItemName = (TextView) convertView.findViewById(R.id.txt_poi_item_name);
				
				holder = new Holder();
				holder.textView = txtPoiItemName;
				convertView.setTag(holder);
			}else{
				holder = (Holder)convertView.getTag();
			}
			
			try {			
				if (vtName == null) {
					return null;
				}
				
				if (vtName.size() < 1) {
					return null;
				}
			
				String name = vtName.get(itemPosition);
				int resId = vtResID.get(itemPosition);
				holder.textView.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
				holder.textView.setText(name);
				holder.textView.setTextSize(2, 15);
				holder.textView.setPadding(0, 0, 0, 0);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertView;
		}
	}
}
