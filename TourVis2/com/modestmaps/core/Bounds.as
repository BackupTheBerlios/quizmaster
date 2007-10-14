/*
 * $Id: Bounds.as,v 1.1 2007/10/14 22:32:21 thetable Exp $
 */

package com.modestmaps.core
{
	import flash.geom.Point;

	public class Bounds extends Object
	{
	    public var min:Point;
	    public var max:Point;
	
	    public function Bounds(min:Point, max:Point)
	    {
	        this.min = min;
	        this.max = max;
	    }
	}
}
