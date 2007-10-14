package
{
import mx.collections.ArrayCollection;

	public class Tour
	{
		public var shows:ArrayCollection;
		public var band:String;
		
		public function addShow(show:Show):void{
			shows.addItem(show);		
		}
	
		public function Tour(bandName:String){
			band = bandName;
			shows = new ArrayCollection();
		}
	}
}