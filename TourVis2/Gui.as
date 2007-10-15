package
{
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.text.TextField;
	
	import mx.collections.ArrayCollection;
	
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
		
		public function updateList(tours:ArrayCollection):void{
			var tour:Tour;
			var yPosition:int = 0;
			for each (tour in tours){
				var newBand:Sprite = new Sprite();
				var bandName:TextField = new TextField();
				bandName.text = (tour.band);
				bandName.textColor = tour.getColor();
				newBand.addChild(bandName);
				newBand.y = yPosition;
				newBand.addEventListener(MouseEvent.MOUSE_OVER, highlight);
				newBand.addEventListener(MouseEvent.MOUSE_OUT, unHighlight);
				list.bandsList.addChild(newBand);
				yPosition += 20;
			}	
//			list.container.addChild(list.bandsList);
		}
		
		private function highlight(e:MouseEvent):void{
			var temp:Sprite = Sprite(e.currentTarget);
			var text:TextField = temp.getChildAt(0) as TextField;
			trace (text.text);
			map.highlight(temp.getChildAt(0).toString());
		}
		
		private function unHighlight(e:MouseEvent):void{
			
		}
	}
	
	
}
