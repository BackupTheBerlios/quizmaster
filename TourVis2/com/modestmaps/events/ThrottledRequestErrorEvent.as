/*
 * $Id: ThrottledRequestErrorEvent.as,v 1.1 2007/10/14 22:32:21 thetable Exp $
 */

package com.modestmaps.events
{
	import flash.net.URLRequest;
	import flash.display.Sprite;
	import com.modestmaps.core.Coordinate;

	public class ThrottledRequestErrorEvent extends ThrottledRequestEvent
	{
		public function ThrottledRequestErrorEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
	}
}