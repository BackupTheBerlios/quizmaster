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
			gui = new Gui();
			addChild(gui);
			trace("setting up backend");
			backend = new TourBus(updateMap);
			trace("backend instantiated.");
			backend.loadTours();
			trace("called loadTours");
		}
		
		public function updateMap() {
			var tours:ArrayCollection = backend.getTours();
			var tour:Tour;
			trace ("updating map")
			for each (tour in tours){
			
			} 
		}
		
	}
}
