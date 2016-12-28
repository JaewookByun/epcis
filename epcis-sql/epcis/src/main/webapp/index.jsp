<!DOCTYPE html>
<html lang="en">
<head>
<title>Oliot EPCIS v1.2 Capture Server</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
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
	})(window, document, 'script',
			'https://www.google-analytics.com/analytics.js', 'ga');

	ga('create', 'UA-64257932-1', 'auto');
	ga('send', 'pageview');
</script>
</head>
<body>
	<script src="js/bootstrap.min.js"></script>

	<div class="panel panel-info">
		<div class="panel-heading">
			<h3 class="panel-title">Welcome to Oliot EPCIS v1.2.3</h3>
		</div>
		<div class="panel-body">
			<div class="list-group">
				<a href="#" class="list-group-item active"> Introduction </a><a
					href="#" class="list-group-item"><img alt=""
					src="image/intro.png" style="height: 96px; width: 365px;"></a> <a
					href="#" class="list-group-item">Electronic Product Code
					Information Service (EPCIS) enables to capture and share standardized event/master data ratified by GS1.
				</a>
			</div>

			<div class="list-group">
				<a href="#" class="list-group-item active"> Specification </a> <a
					href="#" class="list-group-item">EPCIS v1.2 compliance<br>Web Application Server, Tomcat v8<br>Java v8, Servlet v3.1<br>Maven<br>Spring
					Framework<br>MySQL v5.7.15
				</a>
			</div>
			<div class="list-group">
				<a href="#" class="list-group-item active"> Contact </a> <a href="#"
					class="list-group-item">Jaewook Byun, Ph.D student<br>
					Korea Advanced Institute of Science and Technology (KAIST)<br>
					Real-time and Embedded Systems Laboratory(RESL)<br>
					bjw0829@kaist.ac.kr<br> GitHub:
					https://github.com/JaewookByun/epcis<br><br>
					
					Yalew Kidane, Ph.D student<br>
					Korea Advanced Institute of Science and Technology (KAIST)<br>
					Real-time and Embedded Systems Laboratory(RESL)<br>
					yalewkidane@gmail.com/@kaist.ac.kr<br> GitHub:
					https://github.com/yalewkidane
				</a>
			</div>
		</div>
	</div>

	<div class="panel panel-info">
		<div class="panel-heading">
			<h3 class="panel-title">Service</h3>
		</div>
		<div class="panel-body">
			<div class="list-group">
				<a href="#" class="list-group-item active"> SOAP Web Service </a> <a
					href="./webservice" class="list-group-item">EPCIS v1.2
					SOAP/HTTP Web Service</a>
			</div>

			<div class="list-group">
				<a href="#" class="list-group-item active"> REST-Like Web
					Service </a> <a href="./rest-like_capture_service.html"
					class="list-group-item">EPCIS Capture Client</a> <a
					href="./rest-like_query_service.html" class="list-group-item">EPCIS
					Query Client</a> <a href="./rest-like_query_service2.html"
					class="list-group-item">EPCIS Query Client (Subscription)</a>
					<a href="./accessControlManager.jsp"
					class="list-group-item">Sign Up or Sign In via Facebook</a>
			</div>
		</div>
	</div>
	<footer>
		<code style="font-size: 12pt">&copy; Auto-ID Labs, KAIST 2016</code>
	</footer>
</body>
</html>
