package com.modestmaps.mapproviders.yahoo
{
	import com.modestmaps.core.Coordinate;
	import com.modestmaps.mapproviders.IMapProvider;
	import com.modestmaps.mapproviders.yahoo.AbstractYahooMapProvider;
	
	/**
	 * @author darren
	 * $Id: YahooAerialMapProvider.as,v 1.1 2007/10/14 22:32:21 thetable Exp $
	 */
	public class YahooAerialMapProvider 
		extends AbstractYahooMapProvider 
		implements IMapProvider
	{
		override public function toString():String
		{
			return "YAHOO_AERIAL";
		}
	
		override public function getTileUrl(coord:Coordinate):String
		{		
	        return "http://us.maps3.yimg.com/aerial.maps.yimg.com/tile?v=1.7&t=a" + getZoomString(sourceCoordinate(coord));
		}
		
		protected function getZoomString( coord : Coordinate ) : String
		{		
	        var row : Number = ( Math.pow( 2, coord.zoom ) /2 ) - coord.row - 1;
			return "&x=" + coord.column + "&y=" + row + "&z=" + (18 - coord.zoom);
		}	
	}
}