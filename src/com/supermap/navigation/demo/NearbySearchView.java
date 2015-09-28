package com.supermap.navigation.demo;

import java.util.Vector;
import com.supermap.android.app.MyApplication;
import com.supermap.data.GeoCircle;
import com.supermap.data.GeoPoint;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.QueryParameter;
import com.supermap.data.Recordset;
import com.supermap.data.SpatialQueryMode;
import com.supermap.mapping.MapView;
import com.supermap.navigation.demo.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class NearbySearchView extends BaseView{

	static private NearbySearchView mNearbyView = null;
	
	private Context mContext = null;
	private View mMainView = null;
	private MapView mMapView = null;;
	
	private RelativeLayout mViewMainBottom = null;
	
	private View mViewMainView = null;
//	private EditText edtNearbyViewSearch = null;
	private Button btnNearbyViewSearch = null;
	private ImageButton btnNearbyViewBack = null;
	
	private TextView txtChaoshi = null;
	private TextView txtGongce = null;
	private TextView txtXuexiao = null;
	private TextView txtJiudian = null;
	private TextView txtGongjiaozhan = null;
	private TextView txtJiayouzhan = null;
	private TextView txtYinhang = null;
	private TextView txtYiyuan = null;
	private TextView txtMeishi = null;
	private TextView txtShangchang = null;
	private TextView txtDianyingyuan = null;
	private TextView txtTingchechang = null;
	
	private Recordset mRecordsetUpdate = null;
	private Point2D ptCurrent = null;
	
	private Vector<Integer> vtID = new Vector<Integer>();
	private Vector<String> vtName = new Vector<String>();
	private Vector<Point2D> vtPoint = new Vector<Point2D>();
	
	static public NearbySearchView getInstance(Context context, MapView mapView, View mainView) {
		if (mNearbyView == null) {
			mNearbyView = new NearbySearchView(context, mapView, mainView);
		}
		return mNearbyView;
	}
	
	private NearbySearchView(Context context, MapView mapView, View mainView) {
		mContext = context;
		mMainView = mainView;
		mMapView = mapView;
		
		initView();
	}
	
	/**
	 *  初始化化界面显示，及界面控件设置
	 */
	private void initView() {	
		mViewMainBottom = (RelativeLayout)mMainView.findViewById(R.id.ly_main_bottom);
		mViewMainView= LayoutInflater.from(mContext).inflate(R.layout.nearby_search_view, null);
		mViewMainView.setClickable(true);
		
//		edtNearbyViewSearch = (EditText) mViewMainView.findViewById(R.id.edt_nearby_view_search);

		btnNearbyViewSearch = (Button) mViewMainView.findViewById(R.id.btn_nearby_view_search);
		btnNearbyViewSearch.setOnClickListener(buttonOnClickListener);

		btnNearbyViewBack = (ImageButton) mViewMainView.findViewById(R.id.btn_nearby_view_back);
		btnNearbyViewBack.setOnClickListener(buttonOnClickListener);
		
		txtChaoshi = (TextView) mViewMainView.findViewById(R.id.txt_chaoshi);
		txtChaoshi.setOnClickListener(buttonOnClickListener);
		txtGongce = (TextView) mViewMainView.findViewById(R.id.txt_gongce);
		txtGongce.setOnClickListener(buttonOnClickListener);
		txtXuexiao = (TextView) mViewMainView.findViewById(R.id.txt_xuexiao);
		txtXuexiao.setOnClickListener(buttonOnClickListener);
		txtJiudian = (TextView) mViewMainView.findViewById(R.id.txt_jiudian);
		txtJiudian.setOnClickListener(buttonOnClickListener);
		txtGongjiaozhan = (TextView) mViewMainView.findViewById(R.id.txt_gongjiaozhan);
		txtGongjiaozhan.setOnClickListener(buttonOnClickListener);
		txtJiayouzhan = (TextView) mViewMainView.findViewById(R.id.txt_jiayouzhan);
		txtJiayouzhan.setOnClickListener(buttonOnClickListener);
		txtYinhang = (TextView) mViewMainView.findViewById(R.id.txt_yinhang);
		txtYinhang.setOnClickListener(buttonOnClickListener);
		txtYiyuan = (TextView) mViewMainView.findViewById(R.id.txt_yiyuan);
		txtYiyuan.setOnClickListener(buttonOnClickListener);
		txtMeishi = (TextView) mViewMainView.findViewById(R.id.txt_meishi);
		txtMeishi.setOnClickListener(buttonOnClickListener);
		txtShangchang = (TextView) mViewMainView.findViewById(R.id.txt_shangchang);
		txtShangchang.setOnClickListener(buttonOnClickListener);
		txtDianyingyuan = (TextView) mViewMainView.findViewById(R.id.txt_dianyingyuan);
		txtDianyingyuan.setOnClickListener(buttonOnClickListener);
		txtTingchechang = (TextView) mViewMainView.findViewById(R.id.txt_tingchechang);
		txtTingchechang.setOnClickListener(buttonOnClickListener);
	}
	
	OnClickListener buttonOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {	
			case R.id.txt_chaoshi:
				searchNearby("chaoshi");
				break;
			case R.id.txt_gongce:			
				searchNearby("gongce");
				break;
			case R.id.txt_xuexiao:			
				searchNearby("xuexiao");
				break;
			case R.id.txt_jiudian:			
				searchNearby("jiudian");
				break;
			case R.id.txt_gongjiaozhan:			
				searchNearby("gongjiaozhan");
				break;
			case R.id.txt_jiayouzhan:			
				searchNearby("jiayouzhan");
				break;
			case R.id.txt_yinhang:			
				searchNearby("yinhang");
				break;
			case R.id.txt_yiyuan:			
				searchNearby("yiyuan");
				break;
			case R.id.txt_meishi:			
				searchNearby("meishi");
				break;
			case R.id.txt_shangchang:			
				searchNearby("shangchang");
				break;
			case R.id.txt_dianyingyuan:			
				searchNearby("dianyingyuan");
				break;
			case R.id.txt_tingchechang:			
				searchNearby("tingchechang");
				break;
			case R.id.btn_nearby_view_back:
				ViewManager.getInstance().fallback();
				break;
			default:
				break;
			}
		}
	};
	
	
	/**
	 * 周边搜索
	 */
	private void searchNearby(String kind) {
		String sql = "";
		if (kind.equals("chaoshi")) {
			sql = "Kind like '20%' or Kind like '21%'";
		}

		if (kind.equals("gongce")) {
			sql = "Kind = '7880'";
		}
		
		if (kind.equals("yiyuan")) {
			sql = "Kind like '72%' or Kind like '75%'";
		}
		
		if (kind.equals("xuexiao")) {
			sql = "Kind like 'A7%' or Kind = 'F00B' or Kind = 'F00D'";
		}
		
		if (kind.equals("jiudian")) {
			sql = "Kind like '50%' or Kind like '53%'";
		}
		
		if (kind.equals("gongjiaozhan")) {
			sql = "Kind like '80%'";
		}
		
		if (kind.equals("jiayouzhan")) {
			sql = "Kind like '40%'";
		}
		
		if (kind.equals("yinhang")) {
			sql = "Kind like 'A1%' and Kind != 'A100'";
		}
		
		if (kind.equals("meishi")) {
			sql = "Kind like '1%'";
		}
		
		if (kind.equals("shangchang")) {
			sql = "Kind like '22%' or Kind like '23%'";
		}
		
		if (kind.equals("dianyingyuan")) {
			sql = "Kind = '6500'";
		}
		
		if (kind.equals("tingchechang")) {
			sql = "Kind = '4100'";
		}
		
		if (mRecordsetUpdate != null) {
			mRecordsetUpdate.dispose();
			mRecordsetUpdate = null;
		}
		
		double length = 0.00000898 *1500;	
		GeoCircle circle = new GeoCircle(ptCurrent, length);	
		QueryParameter queryParameter = new QueryParameter();
		queryParameter.setSpatialQueryObject(circle);
		queryParameter.setAttributeFilter(sql);
		queryParameter.setSpatialQueryMode(SpatialQueryMode.CONTAIN);	
		mRecordsetUpdate = DataManager.getInstance(mContext).query(queryParameter);
		queryParameter.dispose();
		queryParameter = null;
		circle.dispose();
		circle = null;
		
		if (mRecordsetUpdate == null) {
			Toast.makeText(mContext, "附近没有搜索到", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (mRecordsetUpdate.getRecordCount() < 1) {
			Toast.makeText(mContext, "附近没有搜索到相关地物", Toast.LENGTH_SHORT).show();
			if (mRecordsetUpdate != null) {
				mRecordsetUpdate.dispose();
				mRecordsetUpdate = null;
			}
			return;
		}
		
		vtID.clear();
		vtName.clear();
		vtPoint.clear();
		
		mRecordsetUpdate.moveFirst();
		int i = 1;
		while (!mRecordsetUpdate.isEOF() ) {
			if (i > 10) {
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
			
			geoPt.dispose();
			geoPt = null;
			
			i++;
		}
		
		if (mRecordsetUpdate != null) {
			mRecordsetUpdate.dispose();
			mRecordsetUpdate = null;
		}
		
		close();
		
		NearbyShowView view = NearbyShowView.getInstance(mContext, mMapView, mMainView);
		view.build(vtID, vtName, vtPoint);
		view.show();
		ViewManager.getInstance().addView(view);
	}

	public void close() {
		mViewMainBottom.removeView(mViewMainView);
		
		MyApplication.getInstance().setLongPressEnable(true);
	}

	public void show() {
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT);

		mViewMainBottom.removeView(mViewMainView);
		mViewMainBottom.addView(mViewMainView, param);
		
		MyApplication.getInstance().setLongPressEnable(false);
	}

	public Point2D getPtCurrent() {
		return ptCurrent;
	}

	public void setPtCurrent(Point2D ptCurrent) {
		this.ptCurrent = ptCurrent;
	}

	

}
