package
{
	import flash.display.Sprite;
	import flash.geom.Rectangle;
	import flash.text.TextField;
	
	
	public class BandList extends Sprite
	{
		public var bandsList:Sprite;
//		public var listContainer:Sprite;
				
		public function BandList(x:Number, y:Number){
//			listContainer = new Sprite();
			bandsList = new Sprite;
//			listContainer.addChild(bandsList);
//			listContainer.x = x;
//			listContainer.y = y;
//			bandsList.width = 170;
//			bandsList.height = 800;
			bandsList.x = x;
			bandsList.y = y;
			addChild(bandsList);
//			this.addChild(listContainer);			 
		}
	}
}