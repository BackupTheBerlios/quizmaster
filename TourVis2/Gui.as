package
{
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.text.TextField;
	import flash.text.TextFormat;
	
	import mx.collections.ArrayCollection;
	
	public class Gui extends Sprite {
		private var map:TourvisMap;
		private var title:TextField;
		private var list:BandList;
		private var controller:TourVis2;
		private var currentBand:TextField;
		
		public function Gui(controller:TourVis2) {
			this.controller = controller;
			map = new TourvisMap();
			addChild(map);

			currentBand = new TextField();
			currentBand.text = "Welcome to TourVis";
			currentBand.x = map.width;
			currentBand.width = currentBand.textWidth;
			currentBand.wordWrap = true;
			addChild(currentBand);

			list = new BandList(map.width + 1, 45);
			list.opaqueBackground = 0xffffff;
			addChild(list);

			
			
		}
		
		public function getMap():TourvisMap {
			return this.map;
		}
		
		public function updateList(tours:ArrayCollection):void{
			var tour:Tour;
			var yPosition:int = 0;
			var format:TextFormat = new TextFormat();
			format.font = "Verdana";
			format.size = 14;

			for each (tour in tours){
				var newBand:Sprite = new Sprite();
				var bandName:TextField = new TextField();

				bandName.text = (tour.band);
				bandName.textColor = tour.getColor();
				bandName.defaultTextFormat = format;
				bandName.selectable = false;

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
			this.currentBand.text = text.text;
			map.highlight(text.text);
		}
		
		private function unHighlight(e:MouseEvent):void{
			
		}
	}
	
	
}
