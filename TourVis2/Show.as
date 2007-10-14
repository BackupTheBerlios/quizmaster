package
{
	public class Show
	{
		public var date:String;
		public var venueName:String;
		public var venueLat:Number;
		public var venueLong:Number;
		public var city:String;
		public var state:String;
		
		public function Show(data:XML){
			date = data.attribute("date").toString();
			venueName = data.child("venue").attribute("name").toString();
			venueLat = parseFloat(data.child("venue").child("geolocation").attribute("lat").toString());
			venueLong = parseFloat(data.child("venue").child("geolocation").attribute("lon").toString());
			city = data.child("venue").child("city").toString();
			state = data.child("venue").child("state").toString();
		}
	}
}