package {
	import flash.display.Sprite;
	
	import mx.collections.ArrayCollection;

	[SWF(width="1000", height="800", backgroundColor="#ffffff", frameRate="30")]
	public class TourVis2 extends Sprite
	{
		private var gui:Gui
		private var backend:TourBus;
		public function TourVis2()
		{
			trace("setting up backend");
			backend = new TourBus(updateGui);
			trace("backend instantiated.");
			backend.loadTours();
			trace("called loadTours");
		}
		
		public function updateGui():void{
			gui = new Gui(this);
			this.addChild(gui);
			updateMap();
			updateList(this.getBands());
		}
		
		public function updateList(bands:Array):void{
			gui.updateList(backend.getTours());
		}

		public function updateMap():void {
			var tours:ArrayCollection = backend.getTours();
			var tour:Tour;
			trace ("updating map")
			for each (tour in tours){
			
			} 
		}
		
		public function getBands():Array{
			var tours:ArrayCollection = backend.getTours();
			var tour:Tour;
			var result:Array = new Array();
			for each (tour in tours){
				result.push(tour.band);
			}
			return result;
		}
	}
}
