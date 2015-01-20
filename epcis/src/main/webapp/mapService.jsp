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
	src="http://maps.googleapis.com/maps/api/js?key=AIzaSyCAo5V1vzVEXzkliRcdS0jjTb_UNTt9MoM&sensor=TRUE&language=en">
	
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
	function initialize() {
		var mapOptions = {
			center : new google.maps.LatLng(36.375410, 127.366351),
			zoom : 8,
			mapTypeId : google.maps.MapTypeId.ROADMAP
		};
		var map = new google.maps.Map(document.getElementById("map_canvas"),
				mapOptions);
		var polyOptions = {
			strokeColor : '#000000',
			strokeOpacity : 0.7,
			strokeWeight : 3
		}
		poly = new google.maps.Polyline(polyOptions);
		poly.setMap(map);

		// Add a listener for the click event
		google.maps.event.addListener(map, 'click', addLatLng);
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
	}

	function backToMainPage() {
		window.location.href = 'tutorialPage.jsp';
	}
</script>
</head>
<body onload="initialize()">

	<nav class="navbar navbar-default navbar-fixed-top">
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
	<br>
	<br>
	<br>
	<div class="container" align="left">
		<input type="text" placeholder="Insert the EPC you want to trace"
			size="50">
		<button type="button">Trace everyday-object</button>
	</div>
	<br>
	<div id="map_canvas" align="center" style="width: 70%; height: 80%"></div>

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