package
{
	import flash.display.Sprite;
	import flash.text.TextField;
	
	public class Gui extends Sprite {
		private var map:TourvisMap;
		private var title:TextField;
		public function Gui() {
			map = new TourvisMap();
			addChild(map);
			title = new TextField();
			title.x = map.width + 10;
			title.text = "TourVis";
			addChild(title);
		}
		
		public function getMap():TourvisMap {
			return this.map;
		}
	}
}