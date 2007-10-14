/*
 * $Id: MapPosition.as,v 1.1 2007/10/14 22:32:21 thetable Exp $
 */

package com.modestmaps.core
{
	import flash.geom.Point;
	
	public class MapPosition
	{
		public var coord:Coordinate;
		public var point:Point;
		
		public function MapPosition(c:Coordinate, p:Point)
		{
			coord = c;
			point = p;
		}
	}
}