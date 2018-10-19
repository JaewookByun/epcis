<!DOCTYPE html>
<html lang="en">
<head>
<script>
	(function(i, s, o, g, r, a, m) {
		i['GoogleAnalyticsObject'] = r;
		i[r] = i[r] || function() {
			(i[r].q = i[r].q || []).push(arguments)
		}, i[r].l = 1 * new Date();
		a = s.createElement(o), m = s.getElementsByTagName(o)[0];
		a.async = 1;
		a.src = g;
		m.parentNode.insertBefore(a, m)
	})(window, document, 'script', '//www.google-analytics.com/analytics.js',
			'ga');

	ga('create', 'UA-64257932-1', 'auto');
	ga('send', 'pageview');
</script>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="Tutorial for EPCIS v1.2">
<meta name="author" content="Jaewook Jack Byun">

<title>Traceability Demonstration with Oliot EPCIS v1.2</title>

<link rel="stylesheet" href="./css/bootstrap.min.css">
<link href="./css/carousel.css" rel="stylesheet">

<script src="./js/jquery.min.js"></script>
<script src="./js/bootstrap.js"></script>
<script type="text/javascript"
	src="http://maps.googleapis.com/maps/api/js?key=AIzaSyCAo5V1vzVEXzkliRcdS0jjTb_UNTt9MoM&language=en&v=3">
	
</script>
<script>
	href = window.location.href;
	hrefArr = href.split("/");
	var baseURL = hrefArr[0] + "//" + hrefArr[2] + "/epcis";
</script>

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

.gm-style-iw {
	width: 200px;
}

.carousel-caption {
	color: #FFFFFF;
}
</style>
</head>

<script type="text/javascript">
	var map;
	var markers = [];
	function initialize() {
		var mapOptions = {
			center : new google.maps.LatLng(35.541637, 123.714265),
			zoom : 5,
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
	}

	function backToMainPage() {
		window.location.href = 'index.jsp';
	}

	function trace() {

		var epc = $('#epc').val();
		//console.log(epc);

		$
				.get(
						baseURL + "/Service/Poll/SimpleEventQuery?MATCH_epc="
								+ epc + "&orderBy=eventTime&orderDirection=ASC",
						function(xmlDoc) {
							//console.log(xmlDoc);
							$xml = $(xmlDoc);
							if ($xml.find("eventTime").length == 0) {
								alert("No Events, please capture your events first\nGo to Capture Tutorial");
								//document.location.href = "./captureService1.jsp";
							}
							$eventTime = $xml.find("eventTime");
							//console.log($eventTime);
							$geo = $xml.find("demo\\:location");
							//console.log($geo);
							$geoArr = $geo.text().replace(/\[/g, '').replace(
									/\]/g, ',').split(",");
							//console.log($geoArr);

							var $markerT = null;
							for (var i = 1; i < $geoArr.length; i = i + 2) {
								latfT1 = parseFloat($geoArr[i]);
								lonfT1 = parseFloat($geoArr[i - 1]);
								$latlngT1 = new google.maps.LatLng(latfT1,
										lonfT1);
								//console.log($latlngT1);
								latfT2 = parseFloat($geoArr[i + 2]);
								lonfT2 = parseFloat($geoArr[i + 1]);
								$latlngT2 = new google.maps.LatLng(latfT2,
										lonfT2);

								var mapOptions = {
									center : $latlngT1,
									zoom : 10,
									mapTypeId : google.maps.MapTypeId.ROADMAP
								};
								//map.setOptions(mapOptions);

								$markerT = new google.maps.Marker({
									position : $latlngT2,
									title : 'The Moving Route of ' + epc,
									map : map
								});

								var pathVar1 = new google.maps.Polyline({
									path : [ $latlngT1, $latlngT2 ],
									strokeColor : "#FF0000",
									strokeOpacity : 0.5,
									strokeWeight : 2,
									geodesic : true
								});

								pathVar1.setMap(map);
							}
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
				<a class="navbar-brand" href="#">Reusable Pallet Demonstration
					with Oliot EPCIS v1.2</a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
				<form class="navbar-form navbar-right">
					<button type="button" class="btn btn-success"
						onclick="backToMainPage()">Back to Index Page</button>
				</form>
			</div>
			<!--/.navbar-collapse -->
		</div>
	</nav>

	<div class="container">
		<p>
			Insert an EPC and click the button to see the history<br>
			<code>urn:epc:id:grai:95100027.1027.55414</code>
			<code>urn:epc:id:grai:95100027.1027.53374</code>
			<br>
			<code>urn:epc:id:grai:95100027.1027.53089</code>
			<code>urn:epc:id:grai:95100043.1025.40666</code>
			<br>
		</p>
		<input id="epc" type="text" class="input-medium search-query"
			value="urn:epc:id:grai:95100027.1027.55414" size=35>
		<button type="button" class="btn btn-sm btn-primary" onclick="trace()">Trace
		</button>
	</div>
	<br>
	<div id="map_canvas" align="center" style="width: 100%; height: 65%"></div>
	<br>
	<div class="container" align="left">
		<footer>
			<code style="font-size: 12pt">Auto-ID Labs, KAIST and LogisALL 2018</code>
		</footer>
	</div>
</body>

</html>