package com.supermap.android.tools;

import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.Geometrist;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.mapping.Map;

public class GeoToolkit {

	/**
	 * 地图坐标转换到经纬度坐标
	 * @param map
	 * @param pt
	 */
	static public void MapToLongitude_Latitude(Map map, Point2D pt) {
		// 获取当前地图坐标系
		PrjCoordSys srcPrjCoordSys = map.getPrjCoordSys();
					
		if (srcPrjCoordSys.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
			
			Point2Ds point2Ds = new Point2Ds();
			point2Ds.add(pt);
			PrjCoordSys destPrjCoordSys = new PrjCoordSys();
			
			// 设置目标坐标系类型
			destPrjCoordSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
			
			// 转换投影坐标
			CoordSysTranslator.convert(point2Ds, srcPrjCoordSys,
					destPrjCoordSys, new CoordSysTransParameter(),
					CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
	
			pt = point2Ds.getItem(0);
		}
	}
	
	/**
	 * 经纬度坐标转换到地图坐标
	 * @param map
	 * @param pt
	 */
	static public void Longitude_LatitudeToMap(Map map, Point2D pt) {
		// 获取当前地图坐标系
		PrjCoordSys srcPrjCoordSys = map.getPrjCoordSys();
		
		// 当投影不是经纬坐标系时，则对点进行投影转换
		if (srcPrjCoordSys.getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE) {
			Point2Ds points = new Point2Ds();
			points.add(pt);
			PrjCoordSys desPrjCoorSys = new PrjCoordSys();
			desPrjCoorSys.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
			CoordSysTranslator.convert(points, desPrjCoorSys, srcPrjCoordSys,new CoordSysTransParameter(),
					CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
			pt = points.getItem(0);
		}
	}
	
	/**
	 * 计算两点距离（单位米）
	 * @param ptFrom
	 * @param ptTo
	 * @return
	 */
	static public double getDistance(Point2D ptFrom, Point2D ptTo){
		Point2Ds pts = new Point2Ds();
		pts.add(ptFrom);
		pts.add(ptTo);
		
		if (pts.getCount() < 2) {
			return -1;
		} 
		double distanceToMe = Geometrist.computeGeodesicDistance(pts, 6378137.0, 0.003352811);
		return distanceToMe;
	}
}
