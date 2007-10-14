/**
 * @author darren
 * $Id: IRequest.as,v 1.1 2007/10/14 22:32:21 thetable Exp $
 */
package com.modestmaps.io
{
	import flash.events.IEventDispatcher;
	
	public interface IRequest extends IEventDispatcher
	{
		function send():void;
		function execute():void;
		function isBlocking():Boolean;
	}
}