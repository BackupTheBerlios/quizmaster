package
{
	import flash.display.Sprite;
	import flash.text.TextField;
	
	public class BandList extends Sprite
	{
		public var bandsList:TextField;
		public var container:Sprite;
		
		public function BandList(x:Number, y:Number){
			container = new Sprite();
			bandsList = new TextField();
			container.x = x;
			container.y = y;
//			container.scrollRect = new Rectangle(10,0,190,500);
			this.addChild(container);			 
		}
	}
}