package com.supermap.navigation.demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.DisplayMetrics;
import com.supermap.android.app.MyApplication;
import com.supermap.android.configuration.DefaultDataConfiguration;
import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasets;
import com.supermap.data.Datasource;
import com.supermap.data.Point;
import com.supermap.data.Point2D;
import com.supermap.data.QueryParameter;
import com.supermap.data.Recordset;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Size2D;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;
import com.supermap.mapping.MapControl;

public class DataManager {
	
	static private DataManager mDataManager = null;
	
	private Context		mContext = null;
//	private MapControl	mMapControl;
	
	private Workspace	mWorkspace      = null;
	private Datasource	mDatasource = null;
	private Datasets	mDatasets = null;
	private Dataset		mPoiDataset = null;
	
	String mCurDatasetName = "POI_All_new";
	
	float mDensity = 1;
	
	static public DataManager getInstance(Context context) {
		if (mDataManager == null) {
			mDataManager = new DataManager(context);
		}
		return mDataManager;
	}
	
	private DataManager(Context context) {
		mContext = context;
		init();
	}
	
	private void init() {
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		mDensity = dm.density;
	}
	
	public void openWorkspace() {
		mWorkspace = new Workspace();

		WorkspaceConnectionInfo m_info = new WorkspaceConnectionInfo();
		m_info.setServer(DefaultDataConfiguration.DefaultWorkspace);   
		m_info.setType(WorkspaceType.SMWU);
//		m_info.setPassword("daohang_beijing");
		if (!mWorkspace.open(m_info)) {
			return;
		}
		
		mDatasource = mWorkspace.getDatasources().get(0);
		
		if (mDatasource != null) {
			mDatasets = mDatasource.getDatasets();
		}
	}
	
	public Workspace getWorkspace() {
		return mWorkspace;  	
	}
	
	public Recordset queryNearset(Point pt) {
		
		if (mDatasets == null) {
			return null;
		}

		String name = "POI_All_new";
		MapControl mapControl = MapManager.getInstance(mContext).getMapControl();
		
		double scale = mapControl.getMap().getScale();
			
		if (scale > 1/5000.0) {
			name = "POI_All_new";
		} else if (scale > 1/10000.0) {
			name = "Text_1w_All_new";
		} else if (scale > 1/25000.0) {
			name = "Text_1w_2_5w_All_new";
		} else if (scale > 1/50000.0) {
			name = "Text_2_5w_5w_All_new";
		} else {
			name = "Text_5w_10w_All";
		}
		
		mCurDatasetName = name;
		mPoiDataset = mDatasets.get(name);
		DatasetVector datasetVector = (DatasetVector)mPoiDataset;

		double length = getMapLength(mapControl, pt, 30);
		Size2D size2D = new Size2D(length, length);
		Point2D pt2d = mapControl.getMap().pixelToMap(pt);
		
		Rectangle2D bounds = new Rectangle2D(pt2d, size2D);
		Recordset recordset = datasetVector.query(bounds, CursorType.STATIC);
		
		return recordset;
	}
	
	private double getMapLength(MapControl mapControl, Point pt, int px) {
		Point pt1 = new Point();
		pt1.setX((int) (pt.getX() + mDensity *px));
		pt1.setY((int) (pt.getY() + mDensity *px));
		
		Point2D pt2d = mapControl.getMap().pixelToMap(pt);
		Point2D pt2d1 = mapControl.getMap().pixelToMap(pt1);
		
		double length = pt2d1.getX() - pt2d.getX();	
		return length;
	}
	
	/**
	 * POI查询
	 * @param name
	 * @return
	 */
	public Recordset queryByName(String name) {
		
		if (mDatasets == null) {
			return null;
		}

		String datasetName = "POI_All_new";
		mPoiDataset = mDatasets.get(datasetName);
		
		String sql;	
		String strName = name.trim();
		
		if (strName.equals("")) {
			return null;
		}
		
		Pattern p = Pattern.compile("[a-zA-Z]+$");
		Matcher m = p.matcher(name);
		
		if (m.matches()) {
			sql = "PYSZM like '" + strName +"%'";
		} else {
			sql = "Name like '%" + strName +"%'";
		}
		
		DatasetVector datasetVector = (DatasetVector)mPoiDataset;
		long a = System.currentTimeMillis();
		Recordset recordset = datasetVector.query(sql, CursorType.STATIC);
		long b = System.currentTimeMillis() - a;
		System.out.println("模糊Time="+b);
		
		return recordset;
	}
	
	/**
	 * 周边查询
	 * @param bounds
	 * @param sql
	 * @return
	 */
	public Recordset query(Rectangle2D bounds, String sql) {
		
		if (mDatasets == null) {
			return null;
		}

		String datasetName = "POI_All_new";
		mPoiDataset = mDatasets.get(datasetName);
		
		DatasetVector datasetVector = (DatasetVector)mPoiDataset;
		
		Recordset recordset = datasetVector.query(bounds, sql, CursorType.STATIC);

		return recordset;
	}
	
	public Recordset query(QueryParameter queryParameter) {
		
		if (mDatasets == null) {
			return null;
		}

		String datasetName = "POI_All_new";
		mPoiDataset = mDatasets.get(datasetName);
		
		DatasetVector datasetVector = (DatasetVector)mPoiDataset;
		
		long a = System.currentTimeMillis();
		Recordset recordset = datasetVector.query(queryParameter);
		long b = System.currentTimeMillis() - a;
		System.out.println("周边Time="+b);
		
		return recordset;
	}
	
//	public Recordset query(int id) {
//		if (mDatasets == null) {
//			return null;
//		}
//
//		String datasetName = "POI_All_new";
//		mPoiDataset = mDatasets.get(datasetName);
//		
//		int[] ids = {id};
//		DatasetVector datasetVector = (DatasetVector)mPoiDataset;
//		
//		long a = System.currentTimeMillis();
//		Recordset recordset = datasetVector.query(ids, CursorType.STATIC);
//		long b = System.currentTimeMillis() - a;
//		System.out.println("id Time="+b);
//		
//		return recordset;
//	}
	
	public Recordset query(String datasetName ,int id) {
		if (mDatasets == null) {
			return null;
		}

		mPoiDataset = mDatasets.get(datasetName);
		
		int[] ids = {id};
		DatasetVector datasetVector = (DatasetVector)mPoiDataset;
		
		long a = System.currentTimeMillis();
		Recordset recordset = datasetVector.query(ids, CursorType.STATIC);
		long b = System.currentTimeMillis() - a;
		System.out.println("id Time="+b);
		
		return recordset;
	}
}
