package
{
	import flash.display.Sprite;
	import flash.text.TextField;
	
	public class Gui extends Sprite {
		private var map:TourvisMap;
		private var title:TextField;
		private var list:BandList;
		private var controller:TourVis2;
		
		public function Gui(controller:TourVis2) {
			this.controller = controller;
			map = new TourvisMap();
			addChild(map);
//			list = new BandList(map.width + 10, 0, controller.getBands());
			list = new BandList(map.width + 10, 0);
			addChild(list);
		}
		
		public function getMap():TourvisMap {
			return this.map;
		}
		
		public function updateList(bands:Array):void{
			var band:String;
			for each (band in bands){
				list.bandsList.appendText(band);
				list.bandsList.appendText("\n");
			}	
			list.container.addChild(list.bandsList);
		}
		
		
	}
}