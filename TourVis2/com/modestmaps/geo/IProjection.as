/*
 * $Id: IProjection.as,v 1.1 2007/10/14 22:32:21 thetable Exp $
 */

package com.modestmaps.geo
{ 
	import flash.geom.Point;
	import com.modestmaps.core.Coordinate;
	import com.modestmaps.geo.Location;

	public interface IProjection
	{
	   /*
	    * Return projected and transformed point.
	    */
	    function project(point:Point):Point;
	    
	   /*
	    * Return untransformed and unprojected point.
	    */
	    function unproject(point:Point):Point;
	    
	   /*
	    * Return projected and transformed coordinate for a location.
	    */
	    function locationCoordinate(location:Location):Coordinate;
	    
	   /*
	    * Return untransformed and unprojected location for a coordinate.
	    */
	    function coordinateLocation(coordinate:Coordinate):Location;
	    
	    function toString():String;
	}
}