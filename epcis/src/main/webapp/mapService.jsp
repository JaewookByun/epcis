<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description"
	content="Tutorial for EPCIS v1.1. It peaks three different EPCIS events in the life of Cow">
<meta name="author" content="Jaewook Jack Byun">
<title>EPCIS v1.1 Tutorial - the cow's life</title>
<link rel="stylesheet" href="./css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script src="./js/bootstrap.js"></script>
<script type="text/javascript"
	src="http://maps.googleapis.com/maps/api/js?key=AIzaSyCAo5V1vzVEXzkliRcdS0jjTb_UNTt9MoM&sensor=TRUE&language=en&v=3">
	
</script>
<link href="carousel.css" rel="stylesheet">
<style type="text/css">
html {
	height: 100%
}

body {
	height: 100%;
	margin-left: 20px;
	margin-right: 20px;
	padding: 0
}

#map_canvas {
	height: 100%;
	margin-left: auto;
	margin-right: auto
}
</style>
</head>

<script type="text/javascript">
	var map;
	function initialize() {
		var mapOptions = {
			center : new google.maps.LatLng(36.375410, 127.366351),
			zoom : 16,
			mapTypeId : google.maps.MapTypeId.ROADMAP
		};
		map = new google.maps.Map(document.getElementById("map_canvas"),
				mapOptions);
		var polyOptions = {
			strokeColor : '#000000',
			strokeOpacity : 0.7,
			strokeWeight : 3
		}
		poly = new google.maps.Polyline(polyOptions);
		poly.setMap(map);

		// Add a listener for the click event
		// google.maps.event.addListener(map, 'click', addLatLng);

		// var latLng = new google.maps.LatLng(36, 127);
		// map.setCenter(latLng);

	}

	/**
	 * Handles click events on a map, and adds a new point to the Polyline.
	 * @param {MouseEvent} mouseEvent
	 */
	function addLatLng(event) {

		var path = poly.getPath();

		// Because path is an MVCArray, we can simply append a new coordinate
		// and it will automatically appear
		path.push(event.latLng);

		// Add a new marker at the new plotted point on the polyline.
		var marker = new google.maps.Marker({
			position : event.latLng,
			title : '#' + path.getLength(),
			map : map
		});

		timer = setInterval(function() {
			var newLatLng = new google.maps.LatLng(map.getCenter().lat() + 1,
					map.getCenter().lng() + 1);
			map.setCenter(newLatLng);
		}, 1000);

	}

	function backToMainPage() {
		window.location.href = 'tutorialPage.jsp';
	}

	function trace1() {
		$
				.get(
						"http://localhost:8080/epcis/Service/Poll/SimpleEventQuery?MATCH_epc=urn:epc:id:sgtin:4012345.077889.27",
						function(data) {
							xmlDoc = $.parseXML(data);
							$xml = $(xmlDoc);
							$eventTime = $xml.find("eventTime");
							$epc = $xml.find("epc");
							$geo = $xml.find("geo");
							lat = $geo.text().split(",")[0].trim();
							lon = $geo.text().split(",")[1].trim();
							$latlng1 = new google.maps.LatLng(lat, lon);
							mapOptions = {
								center : $latlng1,
								zoom : 10,
								mapTypeId : google.maps.MapTypeId.ROADMAP
							};
							map.setOptions(mapOptions);
							$marker1 = new google.maps.Marker({
								position : $latlng1,
								title : 'Matsuyama Food Mart',
								map : map
							});

							infoText = $epc.text().trim()
									+ "<br>was located in<br>Matsuyama Food Mart<br>at<br>"
									+ $eventTime.text().trim();

							$infowindow1 = new google.maps.InfoWindow({
								content : infoText
							});

							$infowindow1.open(map, $marker1);

							google.maps.event.addListener($marker1, 'click',
									trace2);

						});
	}

	function trace2() {
		$infowindow1.close();
		$
				.get(
						"http://localhost:8080/epcis/Service/Poll/SimpleEventQuery?MATCH_outputEPC=urn:epc:id:sgtin:4012345.077889.27",
						function(data) {
							xmlDoc = $.parseXML(data);
							$xml = $(xmlDoc);
							$eventTime = $xml.find("eventTime");
							$inputEPCList = $xml.find("inputEPCList");
							$origin = $inputEPCList.text().trim();
							$geo = $xml.find("geo");
							lat = $geo.text().split(",")[0].trim();
							lon = $geo.text().split(",")[1].trim();
							$latlng2 = new google.maps.LatLng(lat, lon);
							mapOptions = {
								center : $latlng2,
								zoom : 10,
								mapTypeId : google.maps.MapTypeId.ROADMAP
							};
							map.setOptions(mapOptions);
							$marker2 = new google.maps.Marker({
								position : $latlng2,
								title : 'Matsuyama Food Mart',
								map : map
							});

							infoText = $epc.text().trim()
									+ "<br>was produced from<br>" + $origin
									+ "<br>in Butcher Lauren B<br>at<br>"
									+ $eventTime.text().trim();

							$infowindow2 = new google.maps.InfoWindow({
								content : infoText,
								maxWidth : 800
							});

							$infowindow2.open(map, $marker2);

							path1 = [ $latlng1, $latlng2 ];
							var path1obj = new google.maps.Polyline({
								path : path1,
								strokeColor : "#0000FF",
								strokeOpacity : 0.8,
								strokeWeight : 2
							});
							
							path1obj.setMap(map);

							google.maps.event.addListener($marker2, 'click',
									trace3);
						});
	}

	function trace3() {
		$infowindow2.close();
		$
				.get(
						"http://localhost:8080/epcis/Service/Poll/SimpleEventQuery?MATCH_epc="
								+ $origin,
						function(data) {
							xmlDoc = $.parseXML(data);
							$xml = $(xmlDoc);
							$eventTime = $xml.find("eventTime");
							$inputEPCList = $xml.find("inputEPCList");
							$origin = $inputEPCList.text().trim();
							$geo = $xml.find("geo");
							lat = $geo.text().split(",")[0].trim();
							lon = $geo.text().split(",")[1].trim();
							$latlng3 = new google.maps.LatLng(lat, lon);
							mapOptions = {
								center : $latlng3,
								zoom : 10,
								mapTypeId : google.maps.MapTypeId.ROADMAP
							};
							map.setOptions(mapOptions);
							$marker3 = new google.maps.Marker({
								position : $latlng3,
								title : 'Matsuyama Food Mart',
								map : map
							});

							infoText = $origin
									+ "<br>was sold from Parker Ranch<br>to Butcher Lauren B<br>at<br>"
									+ $eventTime.text().trim();

							$infowindow3 = new google.maps.InfoWindow({
								content : infoText,
								maxWidth : 800
							});

							path2 = [ $latlng2, $latlng3 ];
							var path2obj = new google.maps.Polyline({
								path : path2,
								strokeColor : "#0000FF",
								strokeOpacity : 0.8,
								strokeWeight : 2
							});
							
							path2obj.setMap(map);
							
							
							$infowindow3.open(map, $marker3);
						});
	}
</script>
</head>
<body onload="initialize()">

	<nav class="navbar navbar-default">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#navbar" aria-expanded="false"
					aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">Oliot EPCIS Tutorial</a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
				<form class="navbar-form navbar-right">
					<button type="button" class="btn btn-success"
						onclick="backToMainPage()">Back to Tutorial Page</button>
				</form>
			</div>
			<!--/.navbar-collapse -->
		</div>
	</nav>

	<div class="container">
		<p>
			Insert the EPC you want to trace. If you want to trace it,
			<code>Click Markers</code>
		</p>
		<input type="text" class="input-medium search-query"
			value="urn:epc:id:sgtin:4012345.077889.27" size=35>
		<button type="button" class="btn btn-sm btn-primary"
			onclick="trace1()">Trace everyday-object</button>
	</div>
	<br>
	<div id="map_canvas" align="center" style="width: 100%; height: 65%"></div>
	<br>
	<div class="container" align="left">
		<footer>
			<p>&copy; Real time Embedded System Laboratory (RESL), Auto-ID
				Labs@Korea 2015</p>
			<p>
				Jaewook Jack Byun, Ph.D student<br>Korea Advanced Institute of
				Science and Technology (KAIST) <br>bjw0829@kaist.ac.kr,
				bjw0829@gmail.com
			</p>
		</footer>
	</div>

	<script src="js/bootstrap.min.js"></script>
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
</body>

</html>