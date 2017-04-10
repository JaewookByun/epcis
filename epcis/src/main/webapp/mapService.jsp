<!DOCTYPE html>
<html lang="en">
<head>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-64257932-1', 'auto');
  ga('send', 'pageview');

</script>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description"
	content="Tutorial for EPCIS v1.1. It peaks three different EPCIS events in the life of Cow">
<meta name="author" content="Jaewook Jack Byun">

<title>EPCIS v1.1 Tutorial - the cow's life</title>

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
								+ "/Service/Poll/SimpleEventQuery?MATCH_epc=urn:epc:id:sgtin:4012345.077889.27",
						function(data) {
							var text = new XMLSerializer().serializeToString(data);
							xmlDoc = $.parseXML(text);
							$xml = $(xmlDoc);		
							if ($xml.find("eventTime").length == 0) {
								alert("No Events, please capture your events first\nGo to Capture Tutorial");
								document.location.href = "./captureService1.jsp";
							}
							$eventTime = $xml.find("eventTime")[0];
							$epc = $xml.find("epc");
							$geo = $xml.find("geo");
							lat = $geo.text().split(",")[0].trim();
							lon = $geo.text().split(",")[1].trim();
							latf = parseFloat(lat);
							lonf = parseFloat(lon);
							$latlng1 = new google.maps.LatLng(latf, lonf);
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

							infoText = "The beef pack" + "<br>was located in"
									+ "<br>Matsuyama Food Mart<br>at<br>"
									+ $eventTime.textContent.trim();

							$infowindow1 = new google.maps.InfoWindow({
								content : infoText
							});

							$infowindow1.open(map, $marker1);

							google.maps.event.addListener($marker1, 'click',
									trace2);

						});
	}

	function trace2() {
		$
				.get(
						baseURL
								+ "/Service/Poll/SimpleEventQuery?MATCH_outputEPC=urn:epc:id:sgtin:4012345.077889.27",
						function(data) {
							var text = new XMLSerializer().serializeToString(data);
							xmlDoc = $.parseXML(text);
							$xml = $(xmlDoc);
							$eventTime = $xml.find("eventTime")[0];
							$inputEPCList = $xml.find("inputEPCList");
							$origin = $inputEPCList[0].textContent.trim();
							$geo = $xml.find("geo");
							lat = $geo.text().split(",")[0].trim();
							lon = $geo.text().split(",")[1].trim();
							latf = parseFloat(lat);
							lonf = parseFloat(lon);
							$latlng2 = new google.maps.LatLng(latf, lonf);
							path1 = [ $latlng1, $latlng2 ];

							infoText = "The beef pack"
									+ "<br>was produced from" + "<br>the cow"
									+ "<br>in Butcher Lauren B<br>at<br>"
									+ $eventTime.textContent.trim();

							$infowindow2 = new google.maps.InfoWindow({
								content : infoText,
								maxWidth : 2000
							});

							// Animation
							$infowindow1.close();
							$cnt = 0;

							// smaller interval makes animation faster
							var timer1 = setInterval(timer1func, 10);

							$marker2 = new google.maps.Marker({
								position : $latlng2,
								title : 'Matsuyama Food Mart',
								map : map
							});

							function timer1func() {
								if ($cnt != 100) {

									latTmp = path1[0].lat()
											+ (path1[1].lat() - path1[0].lat())
											* $cnt / 100.0;
									lngTmp = path1[0].lng()
											+ (path1[1].lng() - path1[0].lng())
											* $cnt / 100.0;

									$latlngTmp = new google.maps.LatLng(latTmp,
											lngTmp);

									pathTmp = [ $latlng1, $latlngTmp ];

									var pathTmp = new google.maps.Polyline({
										path : pathTmp,
										strokeColor : "#1A3CE4",
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
									clearInterval(timer1);
									$infowindow2.open(map, $marker2);
								}
							}
							google.maps.event.addListener($marker2, 'click',
									trace3);
						});
	}

	function trace3() {
		console.log($origin);
		$
				.get(
						baseURL + "/Service/Poll/SimpleEventQuery?MATCH_epc="
								+ $origin,
						function(data) {
							var text = new XMLSerializer().serializeToString(data);
							xmlDoc = $.parseXML(text);
							$xml = $(xmlDoc);
							$eventTime = $xml.find("eventTime")[0];
							$inputEPCList = $xml.find("inputEPCList");
							$origin = $inputEPCList.text().trim();
							$geo = $xml.find("geo");
							console.log(data);
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
				<a class="navbar-brand" href="#">EPCIS v1.1 Tutorial and
					Demonstration ( Oliot Opensource Project )</a>
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
			Insert the EPC you want to trace. Then,
			<code>Click</code>
			the latest marker
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
			<code style="font-size: 12pt">Auto-ID Labs. Korea 2015</code>
			<br>
			<br>
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