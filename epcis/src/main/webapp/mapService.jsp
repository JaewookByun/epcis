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

<title>Traceability Demonstration of Canned Beef with Oliot
	EPCIS v1.2</title>

<link rel="stylesheet" href="./css/bootstrap.min.css">
<link href="./css/carousel.css" rel="stylesheet">

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script src="./js/bootstrap.js"></script>
<script type="text/javascript"
	src="http://maps.googleapis.com/maps/api/js?key=AIzaSyCAo5V1vzVEXzkliRcdS0jjTb_UNTt9MoM&sensor=TRUE&language=en&v=3">
	
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
	}

	function backToMainPage() {
		window.location.href = 'tutorialPage.jsp';
	}

	function trace1() {
		$
				.get(
						baseURL
								+ "/Service/Poll/SimpleEventQuery?MATCH_epc=urn:epc:id:sgtin:0000003.000001.1",
						function(xmlDoc) {
							//console.log(xmlDoc);
							//var text = new XMLSerializer().serializeToString(data);
							//xmlDoc = $.parseXML(text);
							$xml = $(xmlDoc);

							if ($xml.find("eventTime").length == 0) {
								alert("No Events, please capture your events first\nGo to Capture Tutorial");
								document.location.href = "./captureService1.jsp";
							}
							$eventTime = $xml.find("eventTime")[0];
							$epc = $xml.find("epc");
							//console.log($epc);
							$geo = $xml.find("location");
							//console.log($geo.text().replace(/\[/g,'').replace(/\]/g,'').split(","));
							$geoArr = $geo.text().replace(/\[/g, '').replace(
									/\]/g, ',').split(",");
							//console.log($geoArr);
							latf = parseFloat($geoArr[1]);
							lonf = parseFloat($geoArr[0]);
							$latlng1 = new google.maps.LatLng(latf, lonf);
							mapOptions = {
								center : $latlng1,
								zoom : 10,
								mapTypeId : google.maps.MapTypeId.ROADMAP
							};
							map.setOptions(mapOptions);
							$marker1 = new google.maps.Marker({
								position : $latlng1,
								title : 'Canning Factory',
								map : map
							});

							infoText = "The canned beef"
									+ "<br>was processed in"
									+ "<br>a canning factory here.<br><br> A transformation event found. <br> Please click this marker";

							$infowindow1 = new google.maps.InfoWindow({
								content : infoText
							});

							$infowindow1.open(map, $marker1);

							latf = parseFloat($geoArr[5]);
							lonf = parseFloat($geoArr[4]);
							$latlng2 = new google.maps.LatLng(latf, lonf);
							mapOptions = {
								center : $latlng2,
								zoom : 10,
								mapTypeId : google.maps.MapTypeId.ROADMAP
							};
							map.setOptions(mapOptions);
							$marker2 = new google.maps.Marker({
								position : $latlng2,
								title : 'Retail',
								map : map
							});

							infoText = "You bought the canned beef from a retail shop here";

							$infowindow2 = new google.maps.InfoWindow({
								content : infoText
							});

							$infowindow2.open(map, $marker2);

							google.maps.event.addListener($marker1, 'click',
									trace2);

						});
	}

	function trace2() {
		$
				.get(
						baseURL
								+ "/Service/Poll/SimpleEventQuery?MATCH_anyEPC=urn:epc:id:sgtin:0000001.000001.1",
						function(xmlDoc) {
							//console.log(xmlDoc);
							//var text = new XMLSerializer().serializeToString(data);
							//xmlDoc = $.parseXML(text);
							$xml = $(xmlDoc);
							$eventTime = $xml.find("eventTime")[0];

							$geo = $xml.find("location");
							//console.log($geo.text().replace(/\[/g,'').replace(/\]/g,'').split(","));
							$geoArr = $geo.text().replace(/\[/g, '').replace(
									/\]/g, ',').split(",");
							//console.log($geoArr);
							latf1 = parseFloat($geoArr[5]);
							lonf1 = parseFloat($geoArr[4]);
							$latlng3 = new google.maps.LatLng(latf1, lonf1);
							$infowindow1.close();
							$infowindow2.close();
							//path1 = [ $latlng1, $latlng2 ];

							infoText = "The canned beef was produced from"
									+ "<br>the cow in a canning factory <br><br> Additional transporting events found. <br> Please click this marker";

							$marker3 = new google.maps.Marker({
								position : $latlng3,
								title : 'Retail',
								map : map
							});

							$infowindow3 = new google.maps.InfoWindow({
								content : infoText,
								maxWidth : 2000
							});

							mapOptions = {
								center : $latlng3,
								zoom : 10,
								mapTypeId : google.maps.MapTypeId.ROADMAP
							};
							map.setOptions(mapOptions);

							$infowindow3.open(map, $marker3);

							latf2 = parseFloat($geoArr[1]);
							lonf2 = parseFloat($geoArr[0]);
							$latlng4 = new google.maps.LatLng(latf2, lonf2);

							infoText = "The cow bred in a ranch <br><br> Additional transporting events found. <br> Please click this marker";

							$marker4 = new google.maps.Marker({
								position : $latlng4,
								title : 'Ranch',
								map : map
							});

							$infowindow4 = new google.maps.InfoWindow({
								content : infoText,
								maxWidth : 2000
							});

							$infowindow4.open(map, $marker4);

							$mid1 = new google.maps.LatLng((latf2+latf1)/2.0, (lonf2+lonf1)/2.0);
							mapOptions1 = {
								center : $mid1,
								zoom : 8,
								mapTypeId : google.maps.MapTypeId.ROADMAP
							};
							map.setOptions(mapOptions1);

							google.maps.event.addListener($marker4, 'click', trace3);
						});
	}

	function trace3() {
		$
				.get(
						baseURL + "/Service/Poll/SimpleEventQuery?MATCH_anyEPC=urn:epc:id:sscc:0000002.0000000001",
						function(xmlDoc) {
							console.log(xmlDoc);
							//var text = new XMLSerializer().serializeToString(data);
							//xmlDoc = $.parseXML(text);
							$xml = $(xmlDoc);
							
							$geo = $xml.find("location");
							//console.log($geo.text().replace(/\[/g,'').replace(/\]/g,'').split(","));
							$geoArr = $geo.text().replace(/\[/g, '').replace(
									/\]/g, ',').split(",");
							console.log($geoArr);
							lat = $geo.text().split(",")[0].trim();
							lon = $geo.text().split(",")[1].trim();
							latf = parseFloat(lat);
							lonf = parseFloat(lon);
							$latlng3 = new google.maps.LatLng(latf, lonf);
							mapOptions = {
								center : $latlng3,
								zoom : 10,
								mapTypeId : google.maps.MapTypeId.ROADMAP
							};
							map.setOptions(mapOptions);
							$marker3 = new google.maps.Marker({
								position : $latlng3,
								title : 'Parker Ranch',
								map : map
							});

							infoText = "The cow"
									+ "<br>was sold from Parker Ranch"
									+ "<br>to Butcher Lauren B<br>at<br>"
									+ $eventTime.textContent.trim();

							path2 = [ $latlng2, $latlng3 ];

							// Animation
							$infowindow2.close();
							$cnt = 0;
							var timer2 = setInterval(timer2func, 15);

							function timer2func() {
								if ($cnt != 100) {

									latTmp = path2[0].lat()
											+ (path2[1].lat() - path2[0].lat())
											* $cnt / 100.0;
									lngTmp = path2[0].lng()
											+ (path2[1].lng() - path2[0].lng())
											* $cnt / 100.0;

									$latlngTmp = new google.maps.LatLng(latTmp,
											lngTmp);

									pathTmp = [ $latlng2, $latlngTmp ];

									var pathTmp = new google.maps.Polyline({
										path : pathTmp,
										strokeColor : "#E4421A",
										strokeOpacity : 0.08,
										strokeWeight : 5,
										geodesic : true
									});

									pathTmp.setMap(map);
									$cnt = $cnt + 1;

									mapOptions = {
										center : $latlngTmp,
										zoom : 10,
										mapTypeId : google.maps.MapTypeId.ROADMAP
									};
									map.setOptions(mapOptions);

								} else if ($cnt == 100) {
									clearInterval(timer2);

									$marker3 = new google.maps.Marker({
										position : $latlng3,
										title : 'Butcher Lauren B',
										map : map
									});

									$infowindow3 = new google.maps.InfoWindow({
										content : infoText,
										maxWidth : 800
									});

									$infowindow3.open(map, $marker3);
								}
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
				<a class="navbar-brand" href="#">Traceability Demonstration of
					Canned Beef with Oliot EPCIS v1.2</a>
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
			Insert the EPC you want to trace. (Not Working for other epcs) <br>
			Then,
			<code>Click</code>
			the latest marker
		</p>
		<input type="text" class="input-medium search-query"
			value="urn:epc:id:sgtin:0000003.000001.1" size=35>
		<button type="button" class="btn btn-sm btn-primary"
			onclick="trace1()">Trace everyday-object</button>
	</div>
	<br>
	<div id="map_canvas" align="center" style="width: 100%; height: 65%"></div>
	<br>
	<div class="container" align="left">
		<footer>
			<code style="font-size: 12pt">Auto-ID Labs, KAIST 2017</code>
			<br> <br>
			<p class="lead"
				style="font-size: 12pt; color: blue; margin-top: 0pt; margin-bottom: 0pt">Contact</p>
			<p>
				Jaewook Byun, Ph.D student<br>Korea Advanced Institute of
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