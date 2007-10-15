package{
	
	import com.modestmaps.Map;
	import com.modestmaps.core.MapExtent;
	import com.modestmaps.events.MapEvent;
	import com.modestmaps.geo.Location;
	import com.modestmaps.mapproviders.IMapProvider;
	import com.modestmaps.mapproviders.OpenStreetMapProvider;
	
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	import flash.text.TextField;
	
	import mx.collections.ArrayCollection;

	public class TourvisMap extends Sprite
	{
		private var map:Map;
	    private var provider:IMapProvider;
		private var navButtons:Sprite;
		private var status:TextField;
		// Local copy of tours, must be updated when updateMap() is called
		private var tours:ArrayCollection;
		
		private var lines:Sprite;
		private var circle:Sprite;
		private var lineContainer:Sprite;		
		
		private var highlighted:String = "Modest Mouse";
		
		public function TourvisMap()
		{
			status = new TextField();
	        status.selectable = false;
	        status.textColor = 0x000000;
	        status.text = '...';
//	        status.width = 600;
	        status.height = status.textHeight + 2;
	        addChild(status);
	        
//	        provider = new BlueMarbleMapProvider();
	        provider = new OpenStreetMapProvider();
			map = new Map(600, 600, true, provider);
			addChild(map);
			lineContainer = new Sprite();
			lineContainer.width = map.width;
			lineContainer.height =  map.height;
			addChild(lineContainer);
			map.addEventListener(MapEvent.ZOOMED_BY, onZoomed);
	        map.addEventListener(MapEvent.STOP_ZOOMING, onStopZoom);
	        map.addEventListener(MapEvent.PANNED, onPanned);
	        map.addEventListener(MapEvent.STOP_PANNING, onStopPan);
//			map.setExtent(new MapExtent(37.829853, 25.700121, -122.212601, -110.514725));
			map.setExtent(new MapExtent(37.829853, 5.700121, -122.212601, -100.514725));
//			map.putMarker("asdf", new Location(37.804274, -122.262940));
			var buttons:Array = new Array();
	        
	        navButtons = new Sprite();
	        addChild(navButtons);
	        
	        buttons.push(makeButton(navButtons, 'plus', 'zoom in', map.zoomIn));
	        buttons.push(makeButton(navButtons, 'minus', 'zoom out', map.zoomOut));
	        
	        var nextX:Number = 0;
			
			for(var i:Number = 0; i < buttons.length; i++) {
				buttons[i].x = nextX;
				nextX += Sprite(buttons[i]).getChildByName('label').width + 5;	
			}
			
			// Line
			lines = new Sprite();
			circle = new Sprite();
			map.addChild(lines);
			map.addChild(circle);
			
			redraw();
		}
		
		public function updateMap(tours:ArrayCollection):void {
			this.tours = tours;
			redraw();
		}
		
		private function redraw():void {
			circle.graphics.clear();
			circle.graphics.lineStyle(2, 0x555555);
			circle.graphics.beginFill(0x555555, 1);
			lines.graphics.clear();
//			lines.graphics.lineStyle(map.getZoom()  , .6);
			lines.graphics.beginFill(0xff0000, 0);
			var i:int = 0;
			for each (var tour:Tour in tours) {
//				trace("color: " + new uint( colors[i % colors.length]).valueOf());
//				var color:Number = i / tours.length *0xFFFFFF;
//				var color:Number = i *0xFFFFFF;
				lines.graphics.lineStyle(map.getZoom() /3, tour.getColor(), .6);
				if (tour.band == highlighted) {
					lines.graphics.lineStyle(3 * map.getZoom() /3, 0xff0000);
				}
				var last:Show;
				for each (var show:Show in tour.shows) {
					if (last != null) {
						var start:Point = map.locationPoint(new Location(last.venueLat, last.venueLong), this);
						var finish:Point = map.locationPoint(new Location(show.venueLat, show.venueLong), this);
						lines.graphics.moveTo(start.x, start.y);
						lines.graphics.curveTo(finish.x, finish.y, finish.x, finish.y);
						circle.graphics.drawCircle(start.x, start.y, map.getZoom());
					}
					last = show;
					i++;
				}
			}
		}
		
		public function highlight(band: String) {
			this.highlighted = band;
		}
		
//		private function redraw():void {
//			circle.graphics.clear();
//			circle.graphics.lineStyle(1, 0xff0000);
//			circle.graphics.beginFill(0xff0000, 1);
//			lines.graphics.clear();
//			lines.graphics.beginFill(0xff0000, 0);
//			lines.graphics.lineStyle(1, 0xff0033);
//			
//			var start:Point = map.locationPoint(new Location(37.804274, -122.262940), this);
//			var finish:Point = map.locationPoint(new Location(34.053290, -118.245009), this);
//			var midPoint:Point = new Point(
//				start.x + (Math.max(start.x, finish.x) - Math.min(start.x, finish.x)) / 2,
//				start.y + (Math.max(start.y, finish.y) - Math.min(start.y, finish.y)) / 2
//			);
//			circle.graphics.drawCircle(midPoint.x, midPoint.y, map.getZoom());
//			circle.graphics.drawCircle(start.x, start.y, map.getZoom());
//			circle.graphics.drawCircle(finish.x, finish.y, map.getZoom());
//			
//			for (var i:int = 0; i < 5; i++) {
//				lines.graphics.moveTo(start.x, start.y);
//				lines.graphics.curveTo(midPoint.x - i * 4 * map.getZoom(), midPoint.y - i * 4 * map.getZoom(), finish.x, finish.y);
//			}
//			
////			circle.x = p.x;
////			circle.y = p.y;
//		}
		
		private function onPanned(event:MapEvent):void
	    {
	    	redraw();
	        status.text = 'Panned by '+ event.panDelta.toString() +', top left: '+map.getExtent().northWest.toString()+', bottom right: '+map.getExtent().southEast.toString();
	    }
	    
	    private function onStopPan(event:MapEvent):void
	    {
	    	redraw();
	        status.text = 'Stopped panning, top left: '+map.getExtent().northWest.toString()+', center: '+map.getCenterZoom()[0].toString()+', bottom right: '+map.getExtent().southEast.toString()+', zoom: '+map.getCenterZoom()[1];
	    }
	    
	    private function onZoomed(event:MapEvent):void
	    {
	    	redraw();
	        status.text = 'Zoomed by '+event.zoomDelta.toString()+', top left: '+map.getExtent().northWest.toString()+', bottom right: '+map.getExtent().southEast.toString();
	    }
	    
	    private function onStopZoom(event:MapEvent):void
	    {
	    	redraw();
	        status.text = 'Stopped zooming, top left: '+map.getExtent().northWest.toString()+', center: '+map.getCenterZoom()[0].toString()+', bottom right: '+map.getExtent().southEast.toString()+', zoom: '+map.getCenterZoom()[1];
	    }
	    
	    public function makeButton(clip:Sprite, name:String, labelText:String, action:Function):Sprite
	    {
	        var button:Sprite = new Sprite();
	        button.name = name;
	        clip.addChild(button);
	        
	        var label:TextField = new TextField();
	        label.name = 'label';
	        label.selectable = false;
	        label.textColor = 0xffffff;
	        label.text = labelText;
	        label.width = label.textWidth + 4;
	        label.height = label.textHeight + 2;
	        button.addChild(label);
	        
	        button.graphics.moveTo(0, 0);
	        button.graphics.beginFill(0x000000, 1.0);
	        button.graphics.drawRect(0, 0, label.width, label.height);
	        button.graphics.endFill();

			button.addEventListener(MouseEvent.CLICK, action);
			button.useHandCursor = true;
			button.mouseChildren = false;
			button.buttonMode = true;
	        
	        return button;
	    }
	    
	}
}
