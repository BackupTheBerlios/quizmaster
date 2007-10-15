package
{
	import flash.display.Sprite;
	import flash.events.*;
	import flash.net.*;
	
	import mx.collections.ArrayCollection;
	
	[SWF(width="800", height="800", backgroundColor="#00ffff", frameRate="30")]
	
	public class TourBus extends Sprite
	{
		private var data:XML;
		private var tours:ArrayCollection;
		private var callback:Function;
		
		public function TourBus(callback:Function){
			tours = new ArrayCollection();
			this.callback = callback;
		}		
		
		public function loadTours():void{
			var loader:URLLoader = new URLLoader();
            configureListeners(loader);
            var request:URLRequest = new URLRequest("bands.xml");
//            loader.addEventListener(Event.COMPLETE, callback);
            loader.load(request);
 		}
            
            
		public function getTours():ArrayCollection {
			return tours;
		}
		
		private function configureListeners(dispatcher:IEventDispatcher):void {
            dispatcher.addEventListener(Event.COMPLETE, completeHandler);
            dispatcher.addEventListener(Event.OPEN, openHandler);
            dispatcher.addEventListener(ProgressEvent.PROGRESS, progressHandler);
            dispatcher.addEventListener(SecurityErrorEvent.SECURITY_ERROR, securityErrorHandler);
            dispatcher.addEventListener(HTTPStatusEvent.HTTP_STATUS, httpStatusHandler);
            dispatcher.addEventListener(IOErrorEvent.IO_ERROR, ioErrorHandler);
        }

        private function completeHandler(event:Event):void {
            var loader:URLLoader = URLLoader(event.target);
 			this.data = new XML(loader.data);
 			var tourList:XMLList = this.data.children();
 			trace ("tourList: " + tourList.toString());
 			var tourItem:XML;
 			for each (tourItem in tourList){
 				trace ("bandName: " + tourItem.attribute("band").toString());
 				var tour:Tour = new Tour(tourItem.attribute("band").toString());
 				trace ("tour band: " + tour.band);
 				var showList:XMLList = tourItem.children();
 				var showItem:XML; 
 				for each (showItem in showList){
	 				var show:Show = new Show(showItem); 
	 				tour.addShow(show);
 				}
 				this.tours.addItem(tour);
 			}
 			trace("tour: " + tours.getItemAt(0).shows.getItemAt(0).venueLat);
 			trace("completeHandler: " + this.data.toString());
 			callback.call();
        }

        private function openHandler(event:Event):void {
            trace("openHandler: " + event);
        }

        private function progressHandler(event:ProgressEvent):void {
            trace("progressHandler loaded:" + event.bytesLoaded + " total: " + event.bytesTotal);
        }

        private function securityErrorHandler(event:SecurityErrorEvent):void {
            trace("securityErrorHandler: " + event);
        }

        private function httpStatusHandler(event:HTTPStatusEvent):void {
            trace("httpStatusHandler: " + event);
        }

        private function ioErrorHandler(event:IOErrorEvent):void {
            trace("ioErrorHandler: " + event);
        }
	}
}