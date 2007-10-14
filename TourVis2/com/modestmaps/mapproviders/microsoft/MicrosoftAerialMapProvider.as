
package com.modestmaps.mapproviders.microsoft
{
	import com.modestmaps.core.Coordinate;
	import com.modestmaps.mapproviders.IMapProvider;
	import com.modestmaps.mapproviders.microsoft.AbstractMicrosoftMapProvider;
	
	/**
	 * @author darren
	 * $Id: MicrosoftAerialMapProvider.as,v 1.1 2007/10/14 22:32:21 thetable Exp $
	 */
	
	public class MicrosoftAerialMapProvider 
		extends AbstractMicrosoftMapProvider
		implements IMapProvider
	{
		override public function toString():String
		{
			return "MICROSOFT_AERIAL";
		}
		
		override public function getTileUrl(coord:Coordinate):String
		{		
	        return "http://a" + Math.floor(Math.random() * 4) + ".ortho.tiles.virtualearth.net/tiles/a" + getZoomString( coord ) + ".jpeg?g=45";
		}
	}
}