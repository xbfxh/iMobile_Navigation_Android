package com.supermap.navigation.demo;

import java.util.Vector;
import com.supermap.android.app.MyApplication;
import com.supermap.android.tools.GeoToolkit;
import com.supermap.data.GeoPoint;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.Recordset;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.MapView;
import com.supermap.navigation.demo.R;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class StartPointSearchView extends BaseView{

	static private StartPointSearchView mStartPointSearchView = null;
	
	private Context mContext = null;
	private View mMainView = null;
	private MapView mMapView = null;
	private MapControl mMapControl;
	
	private RelativeLayout mViewMainBottom = null;
	
	private View mViewMainView = null;
	private EditText edtSearch = null;
	private Button btnSearch = null;
	private ImageButton btnBack = null;
	private Button btnMylocation = null;
	private Button btnToMap = null;
	private ListView lstSearchResult = null;
	
	private Recordset mRecordsetUpdate = null;
	
	private Vector<Integer> vtID = new Vector<Integer>();
	private Vector<String> vtName = new Vector<String>();
	private Vector<Point2D> vtPoint = new Vector<Point2D>();
	
	private PoiSearchResultAdapter mPoiSearchResultAdapter = null;
	
	private boolean mIsEnd = true; //是否是设置终点
	
	static public StartPointSearchView getInstance(Context context, MapView mapView, View mainView) {
		if (mStartPointSearchView == null) {
			mStartPointSearchView = new StartPointSearchView(context, mapView, mainView);
		}
		return mStartPointSearchView;
	}
	
	private StartPointSearchView(Context context, MapView mapView, View mainView) {
		mContext = context;
		mMainView = mainView;
		mMapView = mapView;
		mMapControl = mapView.getMapControl();
		mPoiSearchResultAdapter = new PoiSearchResultAdapter();
		
		initView();
	}
	
	/**
	 *  初始化化界面显示，及界面控件设置
	 */
	private void initView() {	
		mViewMainBottom = (RelativeLayout)mMainView.findViewById(R.id.ly_main_bottom);
		mViewMainView= LayoutInflater.from(mContext).inflate(R.layout.startpt_search_view, null);
		mViewMainView.setClickable(true);
		
		edtSearch = (EditText) mViewMainView.findViewById(R.id.edt_startpt_search_view_back);

		btnSearch = (Button) mViewMainView.findViewById(R.id.btn_startpt_search_view_search);
		btnSearch.setOnClickListener(buttonOnClickListener);

		btnBack = (ImageButton) mViewMainView.findViewById(R.id.btn_startpt_search_view_back);
		btnBack.setOnClickListener(buttonOnClickListener);
		
		btnMylocation = (Button) mViewMainView.findViewById(R.id.btn_startpt_search_view_mylocation);
		btnMylocation.setOnClickListener(buttonOnClickListener);
		
		btnToMap = (Button) mViewMainView.findViewById(R.id.btn_startpt_search_view_map);
		btnToMap.setOnClickListener(buttonOnClickListener);
		
		lstSearchResult = (ListView) mViewMainView.findViewById(R.id.lst_startpt_search_view_result);
		lstSearchResult.setAdapter(mPoiSearchResultAdapter);
		
		edtSearch.addTextChangedListener(mTextWatcher);
	}
	
	TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			searchByname(s.toString(), 10);
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
	};
	
	OnClickListener buttonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {	
			case R.id.btn_startpt_search_view_search:	
				String name = edtSearch.getText().toString();
				searchByname(name, -1);
				hideSoftInput(edtSearch);
				break;

			case R.id.btn_startpt_search_view_back:
				ViewManager.getInstance().fallback();
				break;
				
			case R.id.btn_startpt_search_view_mylocation:
				setMyLocation();
				break;
				
			case R.id.btn_startpt_search_view_map:
				selectPoint();
				break;
			default:
				break;
			}
		}
	};
	
	private static final int UPDATE = 0xff01;
	
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE:
				mPoiSearchResultAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		};
	};
	
	/**
	 * 搜索
	 */
	private void searchByname(final String name, final int count) {

		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				doSearch(name, count);
			}
		});
		
		thread.start();
	}

	public synchronized void doSearch(String name, int count) {
		
		if (mRecordsetUpdate != null) {
			mRecordsetUpdate.dispose();
			mRecordsetUpdate = null;
		}
		
		mRecordsetUpdate = DataManager.getInstance(mContext).queryByName(name);
		
		vtID.clear();
		vtName.clear();
		vtPoint.clear();
		
		// 为节约时间，只取前10个
		if (mRecordsetUpdate != null) {
			int i = 0;
			while (!mRecordsetUpdate.isEOF() ) {//&& i<10) {
				if (count > 0 && i >= count) {
					break;
				}
				
				int id = mRecordsetUpdate.getID();
				vtID.add(id);
				
				String poiName = mRecordsetUpdate.getString("Name");
				vtName.add(poiName);
				
				Geometry geoPt = (GeoPoint)mRecordsetUpdate.getGeometry();
				Point2D pt = geoPt.getInnerPoint();
				vtPoint.add(pt);
				mRecordsetUpdate.moveNext();
				i++;
			}
		}
		
		Message msg = mHandler.obtainMessage();
		msg.what = UPDATE;
		mHandler.sendMessage(msg);
	}

	public void close() {
		mViewMainBottom.removeView(mViewMainView);
		
		hideSoftInput(edtSearch);	
		MyApplication.getInstance().setLongPressEnable(true);
	}

	public void show() {
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT);

		mViewMainBottom.removeView(mViewMainView);
		mViewMainBottom.addView(mViewMainView, param);
		
		edtSearch.setFocusable(true);
		edtSearch.setFocusableInTouchMode(true);
		edtSearch.requestFocus();
		InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		
		MyApplication.getInstance().setLongPressEnable(false);
	}

	class Holder {
		public TextView textView = null;
	}
	
	public class PoiSearchResultAdapter extends BaseAdapter {

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
				
				txtPoiItemName.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
					
						setPoint(itemPosition);
					}
				});
				
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
	
				holder.textView.setText(name);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertView;
		}
	}
	
	private void setPoint(int itemPosition) {
		Point2D pt = vtPoint.get(itemPosition);
		GeoToolkit.MapToLongitude_Latitude(mMapControl.getMap(), pt);
		
		String name = vtName.get(itemPosition);
		RouteSetView routeSetView = RouteSetView.getInstance(mContext, mMapView, mMainView);
		
		if (mIsEnd) {
			routeSetView.setEndPtName(name);
			NaviManager.getInstance(mContext).setDestinationPoint(pt.getX(), pt.getY());
		} else {
			routeSetView.setStartPtName(name);
			NaviManager.getInstance(mContext).setStartPoint(pt.getX(), pt.getY());
		}
		
		ViewManager.getInstance().fallback();
	}
	
	/**
	 * 设置我的位置
	 */
	private void setMyLocation() {
		RouteSetView routeSetView = RouteSetView.getInstance(mContext, mMapView, mMainView);
		Point2D pt = NaviManager.getInstance(mContext).getLocationPoint();
		
		if (mIsEnd) {
			routeSetView.setEndPtName("我的位置");
			NaviManager.getInstance(mContext).setDestinationPoint(pt.getX(), pt.getY());
		} else {
			routeSetView.setStartPtName("我的位置");
			NaviManager.getInstance(mContext).setStartPoint(pt.getX(), pt.getY());
		}
		
		ViewManager.getInstance().fallback();
	}
	
	/**
	 * 地图选点
	 */
	private void selectPoint() {
		SelectPointView view = SelectPointView.getInstance(mContext, mMapView, mMainView);
		view.setMode(mIsEnd);
		view.show();
		ViewManager.getInstance().addView(view);
		close();
	}
	
	private void hideSoftInput(View view) {
		InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public void setMode(boolean isEnd) {
		mIsEnd = isEnd;
	}
}
