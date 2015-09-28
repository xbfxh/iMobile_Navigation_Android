package com.supermap.navigation.demo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import com.supermap.android.app.MyApplication;
import com.supermap.android.tools.GeoToolkit;
import com.supermap.data.Point2D;
import com.supermap.mapping.CallOut;
import com.supermap.mapping.CalloutAlignment;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.mapping.dyn.DynamicView;
import com.supermap.navigation.demo.R;

public class NearbyShowView extends BaseView {

	static private NearbyShowView mPoiShowView = null;
	
	private Context mContext = null;
	private View mMainView = null;
	private MapView mMapView = null;
	private MapControl mMapControl = null;
	private DynamicView mDynamicView = null;
	
	private RelativeLayout mViewMainBottom = null;
	private View mViewNearbyShowBottom = null;

	private Vector<Integer> vtID = null;
	private Vector<String> vtName = null;
	private Vector<Point2D> vtPoint = null;
	
	ViewPager mViewPager = null;
	ArrayList<View> mViewList = null;
	
	private int mStartIndex = 0;
	private int mCurrentPage = 0;
	
	private int mCurrentPageIndex = 0;
	
	private float mDensity = 1;
	private boolean mIsShowInfo = true;
			
	static public NearbyShowView getInstance(Context context, MapView mapView, View mainView) {
		if (mPoiShowView == null) {
			mPoiShowView = new NearbyShowView(context, mapView, mainView);
		}
		return mPoiShowView;
	}
	
	private NearbyShowView(Context context, MapView mapView, View mainView) {
		mContext = context;
		mMainView = mainView;
		mMapView = mapView;
		mMapControl = MapManager.getInstance(mContext).getMapControl();
		mDynamicView = MapManager.getInstance(mContext).getDynamicView();
		
		mViewList = new ArrayList<View>();
		
		initView();
	}
	
	/**
	 *  初始化化界面显示，及界面控件设置
	 */
	private void initView() {	
		mViewMainBottom = (RelativeLayout)mMainView.findViewById(R.id.ly_main_bottom);
		mViewNearbyShowBottom= LayoutInflater.from(mContext).inflate(R.layout.nearby_show_view, null);
		mViewNearbyShowBottom.setClickable(true);
		
		mViewPager = (ViewPager) mViewNearbyShowBottom.findViewById(R.id.vp_nearby_show_view_main);
		
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		mDensity = dm.density;
	}
	
	public void build(Vector<Integer> vtID, Vector<String> vtName, Vector<Point2D> vtPoint) {	
		this.vtID = vtID;
		this.vtName = vtName;
		this.vtPoint = vtPoint;
		
		mViewList.clear();
		for (int i = 0; i < vtID.size(); i++) {
			View view = createView(i);
			mViewList.add(view);		
		}
		
//		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setPageMargin((int) (-5 * mDensity));

		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(mOnPageChangeListener);
		
		mStartIndex = Integer.MAX_VALUE / 2;
		int size = mViewList.size();
		mStartIndex = mStartIndex - mStartIndex%size;
		
		if (size == 0) {
			mCurrentPage = 0;
		} else {
			mCurrentPage = mStartIndex % size;
		}
		
		mViewPager.setCurrentItem(mStartIndex);	
	}
	
	private View createView(int index) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.poi_show_bottom, null);
		
		Button btnPoiNearbyPlace = null;
		Button btnPoiOpenRoute = null;
		ImageButton btnPoiShowDetail = null;
		TextView txtPoiShowDistance = null;
		TextView txtPoiShowName = null;	
		
		btnPoiNearbyPlace = (Button) view.findViewById(R.id.btn_poi_show_nearby_place);
		btnPoiNearbyPlace.setOnClickListener(buttonOnClickListener);

		btnPoiOpenRoute = (Button) view.findViewById(R.id.btn_poi_show_open_route);
		btnPoiOpenRoute.setOnClickListener(buttonOnClickListener);

		btnPoiShowDetail = (ImageButton) view.findViewById(R.id.btn_poi_show_detail);
		btnPoiShowDetail.setOnClickListener(buttonOnClickListener);
		
		txtPoiShowDistance = (TextView) view.findViewById(R.id.txt_poi_show_distance);
		txtPoiShowName = (TextView) view.findViewById(R.id.txt_poi_show_name);
		
		int number = index + 1;
		txtPoiShowName.setText(number + ". " + vtName.get(index));	
		getDistance(txtPoiShowDistance, index);
		
		return view;
	}
	
	OnClickListener buttonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {	
			case R.id.btn_poi_show_nearby_place:
				searchNearby();
				break;

			case R.id.btn_poi_show_open_route:			
				openRoute();
				break;

			case R.id.btn_poi_show_detail:
				showPoiDetail();
				break;
			default:
				break;
			}
		}
	};
	
	PagerAdapter mPagerAdapter = new PagerAdapter() {
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (mViewList != null) {
				if (mViewList.size() < 2) {
					return mViewList.size();
				}	
			}
			return Integer.MAX_VALUE;
		}
		
		public void destroyItem(android.view.ViewGroup container, int position, Object object) {
//			container.removeView(mViewList.get(position));
		};
		
		public Object instantiateItem(android.view.ViewGroup container, int position) {

			int size = mViewList.size();
			position %= size;
			
			if (position < 0) {
				position += mViewList.size();
			}
			
			View view = mViewList.get(position);
			ViewParent vp = view.getParent();
			if (vp != null) {
				ViewGroup vg = (ViewGroup)vp;
				vg.removeView(view);
			}
			
			container.addView(view);
			return mViewList.get(position);
		};
	};
	
	OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			mCurrentPage = arg0;
			mCurrentPageIndex = arg0;
			
			int size = mViewList.size();
			if (size == 0) {
				mCurrentPage = 0;
			} else {
				mCurrentPage %= size;
			}
	
			if (mCurrentPage < 0) {
				mCurrentPage += mViewList.size();
			}
			
			if (mIsShowInfo) {
				showCurrentPoi();
			}		
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	//显示当前POI
	private void showCurrentPoi() {
		showPoi();
		if (mCurrentPage > vtPoint.size()-1) {
			return;
		}
		Point2D pt = vtPoint.get(mCurrentPage);
		mMapControl.panTo(pt, 100);
		mMapControl.getMap().refresh();
		
		int resId = R.drawable.icon_marka;
		String name = "icon_mark1";
		
		switch (mCurrentPage) {
		case 0:
			resId = R.drawable.icon_focus_marka;
			name = "icon_mark1";
			break;
		case 1:
			resId = R.drawable.icon_focus_markb;
			name = "icon_mark2";
			break;
		case 2:
			resId = R.drawable.icon_focus_markc;
			name = "icon_mark3";
			break;
		case 3:
			resId = R.drawable.icon_focus_markd;
			name = "icon_mark4";
			break;
		case 4:
			resId = R.drawable.icon_focus_marke;
			name = "icon_mark5";
			break;
		case 5:
			resId = R.drawable.icon_focus_markf;
			name = "icon_mark6";
			break;
		case 6:
			resId = R.drawable.icon_focus_markg;
			name = "icon_mark7";
			break;
		case 7:
			resId = R.drawable.icon_focus_markh;
			name = "icon_mark8";
			break;
		case 8:
			resId = R.drawable.icon_focus_marki;
			name = "icon_mark9";
			break;
		case 9:
			resId = R.drawable.icon_focus_markj;
			name = "icon_mark10";
			break;
		default:
			resId = R.drawable.icon_focus_marka;
			name = "icon_mark1";
			break;
		}
			
		addPoint(pt, name, resId, mCurrentPage);
	}
	
	/**
	 * 网络分析
	 */
	private void openRoute() {	
		RouteSetView view = RouteSetView.getInstance(mContext, mMapView, mMainView);
		
		Point2D pt = NaviManager.getInstance(mContext).getLocationPoint();
		NaviManager.getInstance(mContext).setStartPoint(pt.getX(), pt.getY());
		
		Point2D ptPoi = vtPoint.get(mCurrentPage);
		NaviManager.getInstance(mContext).setDestinationPoint(ptPoi.getX(), ptPoi.getY());
		
		view.setStartPtName("我的位置");
		
		String strPoiShowName = vtName.get(mCurrentPage);
		view.setEndPtName(strPoiShowName);
		view.show();
		ViewManager.getInstance().addView(view);
		close();
	}
	
	/**
	 * 周边搜索
	 */
	private void searchNearby() {
		close();
		NearbySearchView view = NearbySearchView.getInstance(mContext, mMapView, mMainView);
		if (mCurrentPage > vtPoint.size()-1) {
			return;
		}
		Point2D ptCurrent = vtPoint.get(mCurrentPage);
		view.setPtCurrent(ptCurrent);
		view.show();
		ViewManager.getInstance().addView(view);
	}
	
	/**
	 * 显示POI详情
	 */
	private void showPoiDetail() {
		PoiDetailsView view = PoiDetailsView.getInstance(mContext, mMapView, mMainView);
		
		String name = "POI_All_new";
		view.setDatasetName(name);
		
		view.setPoiID(vtID.get(mCurrentPage));
		view.show();
		ViewManager.getInstance().addView(view);
		close();
	}
	
	public void close() {
		mViewMainBottom.removeView(mViewNearbyShowBottom);
		mIsShowing = false;

		MyApplication.getInstance().setLongPressEnable(true);
		MyApplication.getInstance().setSingleTapEnable(true);
		clear();
	}
	
	public void show() {

		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (120*mDensity));

		mViewMainBottom.removeView(mViewNearbyShowBottom);
		mViewMainBottom.addView(mViewNearbyShowBottom, param);
		
		MyApplication.getInstance().setLongPressEnable(false);
		MyApplication.getInstance().setSingleTapEnable(false);
		
		mMapView.removeAllCallOut();
		mMapView.refresh();
		
//		mStartIndex = Integer.MAX_VALUE / 2;
//		int size = mViewList.size();
//		mStartIndex = mStartIndex - mStartIndex%size;
//		
//		mViewPager.setCurrentItem(mStartIndex);
		
		showCurrentPoi();
		mIsShowing = true;	
	}
	
	private void getDistance(TextView txtPoiShowDistance, int index) {
		Point2D ptLocation = NaviManager.getInstance(mContext).getLocationPoint();
		Point2D ptpoi = vtPoint.get(index);
		double distance = GeoToolkit.getDistance(ptLocation, ptpoi);
		 
		DecimalFormat df = new DecimalFormat("0.0");		
		String strDistance;
		
		if (distance < 0) {
			strDistance = 0 + "米";
		} else if (distance < 1000) {
			strDistance = (int)distance + "米";
		} else {
			strDistance = df.format(distance/1000.0) + "公里";
		}
				
		txtPoiShowDistance.setText("距离："+strDistance);
	}
	
	private void clear() {
		mMapView.removeAllCallOut();
		mMapView.refresh();
		Point2D startPoint = NaviManager.getInstance(mContext).getLocationPoint();
		MapManager.getInstance(mContext).showLocationPointByCallOut(startPoint,
				"location", R.drawable.navi_start, CalloutAlignment.CENTER);
		
		mDynamicView.clear();
		mDynamicView.refresh();
	}
	
	/**
	 * 显示查询结果
	 */
	private void showPoi() {
		mDynamicView.clear();
		mDynamicView.refresh();
		
//		Bitmap bitmap1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_marka);
//		Bitmap bitmap2 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_markb);
//		Bitmap bitmap3 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_markc);
//		Bitmap bitmap4 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_markd);
//		Bitmap bitmap5 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_marke);
//		Bitmap bitmap6 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_markf);
//		Bitmap bitmap7 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_markg);
//		Bitmap bitmap8 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_markh);
//		Bitmap bitmap9 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_marki);
//		Bitmap bitmap10 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_markj);
		
		Bitmap bitmap = null;
		
		int resId = R.drawable.icon_marka;
		String name = "icon_mark1";
		
		for (int i = 0; i < vtPoint.size(); i++) {

			switch (i) {
			case 0:
//				bitmap = bitmap1;
				resId = R.drawable.icon_marka;
				name = "icon_mark1";
				break;
			case 1:
//				bitmap = bitmap2;
				resId = R.drawable.icon_markb;
				name = "icon_mark2";
				break;
			case 2:
//				bitmap = bitmap3;
				resId = R.drawable.icon_markc;
				name = "icon_mark3";
				break;
			case 3:
//				bitmap = bitmap4;
				resId = R.drawable.icon_markd;
				name = "icon_mark4";
				break;
			case 4:
//				bitmap = bitmap5;
				resId = R.drawable.icon_marke;
				name = "icon_mark5";
				break;
			case 5:
//				bitmap = bitmap6;
				resId = R.drawable.icon_markf;
				name = "icon_mark6";
				break;
			case 6:
//				bitmap = bitmap7;
				resId = R.drawable.icon_markg;
				name = "icon_mark7";
				break;
			case 7:
//				bitmap = bitmap8;
				resId = R.drawable.icon_markh;
				name = "icon_mark8";
				break;
			case 8:
//				bitmap = bitmap9;
				resId = R.drawable.icon_marki;
				name = "icon_mark9";
				break;
			case 9:
//				bitmap = bitmap10;
				resId = R.drawable.icon_markj;
				name = "icon_mark10";
				break;
			default:
//				bitmap = bitmap1;
				resId = R.drawable.icon_marka;
				name = "icon_mark1";
				break;
			}
				
			Point2D pt = vtPoint.get(i);
			addPoint(pt, name, resId, i);
		}
		
//		mDynamicView.refresh();
	}
	
	private void addPoint(Point2D pt, String name, int resId,int tag) {
		
		showPointByCallOut(pt, name, resId, CalloutAlignment.BOTTOM, tag);
		
	}
	
//	private void addPoint(Point2D pt, Bitmap bitmap) {
//		DynamicPoint element = new DynamicPoint();
//		element.addPoint(pt);
//		
//		DynamicStyle style = new DynamicStyle();
//		style.setBackground(bitmap);
//		element.setStyle(style);
//		element.setAlignment(DynamicAlignment.BOTTOM);
//		element.setOnClickListenner(new DynamicElement.OnClickListener() {
//			
//			@Override
//			public void onClick(DynamicElement element) {
//				element.addAnimator(new ZoomAnimator(2.f, 600));
//				element.addAnimator(new ZoomAnimator(0.5f, 600));
//				element.startAnimation();
//				
//				mMapControl.getMap().setCenter(element.getBounds().getCenter());
//				mMapControl.getMap().refresh();
//			}
//		});
//		
//		mDynamicView.addElement(element);
//	}
	
	public void showPointByCallOut(Point2D point2D,
			String pointName, int idDrawable, CalloutAlignment alignment ,int tag) {
		// 设置标注
		CallOut callOut = new CallOut(mContext);
		callOut.setStyle(alignment); // 设置标注点的对齐方式：下方对齐
		callOut.setCustomize(true); // 设置自定义背景
		callOut.setLocation(point2D.getX(), point2D.getY()); // 设置标注点坐标
		View view = LayoutInflater.from(mContext).inflate(R.layout.callout_nearby, null);
		
		ImageButton imageView = (ImageButton)view.findViewById(R.id.btn_nearby_callout);
		imageView.setBackgroundResource(idDrawable);
		imageView.setTag(tag);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int tag = (Integer) v.getTag();
				int x =  tag - mCurrentPage;
				
				if (x > 0) {
					for (int i = 0; i < x; i++) {
						if (i == x-1) {
							mIsShowInfo = true;
						} else {
							mIsShowInfo = false;
						}
						mViewPager.setCurrentItem(mCurrentPageIndex + 1);
					}
				} else {
					x = -x;
					for (int i = 0; i < x; i++) {
						if (i == x-1) {
							mIsShowInfo = true;
						} else {
							mIsShowInfo = false;
						}
						mViewPager.setCurrentItem(mCurrentPageIndex - 1);
					}
				}
			}
		});
		
		// 显示起点
		callOut.setContentView(view);

		mMapView.removeCallOut(pointName);
		mMapView.addCallout(callOut, pointName); // 在地图上显示CallOut标注点，并设置名称
	}
	
	private void setCurrentPage(int page) {
		int x =  page - mCurrentPage;
		
		if (x > 0) {
			for (int i = 0; i < x; i++) {
				if (i == x-1) {
					mIsShowInfo = true;
				} else {
					mIsShowInfo = false;
				}
				mViewPager.setCurrentItem(mCurrentPageIndex + 1);
			}
		} else {
			x = -x;
			for (int i = 0; i < x; i++) {
				if (i == x-1) {
					mIsShowInfo = true;
				} else {
					mIsShowInfo = false;
				}
				mViewPager.setCurrentItem(mCurrentPageIndex - 1);
			}
		}
	}
}
