package {
	import flash.display.Sprite;

	[SWF(width="1000", height="800", backgroundColor="#ffffff", frameRate="30")]
	public class TourVis2 extends Sprite
	{
		private var gui:Gui
		private var backend:TourBus;
		public function TourVis2()
		{
			gui = new Gui();
			addChild(gui);
			backend = new TourBus();
			
			
			
		}
		
		private function updateMap() {
			ArrayCollection tours = backend.getTours();
			foreach tour in 
		}
		
	}
}
