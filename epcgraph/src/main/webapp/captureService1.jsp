
<!DOCTYPE html>
<html>
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
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="Tutorial for EPCIS v1.2">
<meta name="author" content="Jaewook Jack Byun">

<title>Write Your Events</title>

<link rel="stylesheet" href="./css/bootstrap.min.css">

<script
	src="https://cdn.rawgit.com/google/code-prettify/master/loader/run_prettify.js?autoload=true&amp;skin=desert&amp;lang=html"
	defer="defer"></script>
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script>
	href = window.location.href;
	hrefArr = href.split("/");
	var baseURL = hrefArr[0] + "//" + hrefArr[2] + "/epcis";
</script>

<style>
.operative {
	font-weight: bold;
	border: 1px solid yellow
}
</style>
</head>

<body>

	<!-- Ranch		  -112.158897, 34.445473 -->
	<!--  Canned Factory    -117.688111, 34.002675  -->
	<!-- Retail  -118.048614, 33.808517 -->

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

	<div class="row">
		<div class="col-md-7">
			<div class="span4">
				<?prettify lang=html linenums=1?>
				<pre class="prettyprint" id="transaction"
					style="text-align: left; border: 4px solid #88c; width: 100%; height: 100%">
		</pre>
			</div>
		</div>
		<div class="col-md-4">
			<div class="list-group">
				<a href="#" class="list-group-item active"
					style="text-align: center"> Transaction Event </a> <a href="#"
					class="list-group-item">The event type TransactionEvent
					describes the association or disassociation of physical or digital
					objects to one or more business transactions.</a> <a href="#"
					class="list-group-item"> &#8226; When: 2017-01-01T00:00:00.000
					-8:00 <br> &#8226; What: urn:epc:id:sgtin:0000001.000001.1
					(Cow) <br> &#8226; Where: urn:epc:id:sgln:0000001.00001.1
					(Ranch), [-112.158897, 34.445473] <br> &#8226; Why: The cow is sold <br>
					&nbsp;&nbsp;&nbsp; &#8226; Ownership transfer from a ranch to a canned factory
				</a>
				<a href="#" class="list-group-item active"
					style="text-align: center"> Aggregation Event </a> <a href="#"
					class="list-group-item">The event type AggregationEvent
					describes the (un)packing/loading objects.</a> <a href="#"
					class="list-group-item"> &#8226; When: 2017-01-01T00:01:00.000
					-8:00 <br> &#8226; What: urn:epc:id:sgtin:0000001.000001.1
					(Cow), urn:epc:id:sscc:0000002.0000000001 (Truck) <br> &#8226; Where: urn:epc:id:sgln:0000001.00001.1
					(Ranch), [-112.158897, 34.445473] <br> &#8226; Why: The cow is loaded into the truck 
				</a>
				 <a href="#" class="list-group-item active"
					style="text-align: center"> Capture Events </a> <a
					href="#" class="list-group-item"> Send left EPCIS Document as
					HTTP POST Message to <br> <code>http://{baseURL}:{port}/epcis/Service/EventCapture</code><br>
					<br>
					<button type="button" class="btn btn-warning" onclick="capture()">Capture
						this event</button>
				</a>
			</div>
			<button type="button" class="btn btn-danger" onclick="back()">Back
				to main page</button>

			<button type="button" class="btn btn-info" onclick="skip()">Skip
				to next step</button>
			<br> <br>
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
	</div>

	<script>
		function html(s) {
			return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g,
					'&gt;');
		}

		$("#transaction").load("./exampleXML/Demo/example1.xml",
				function(responseTxt, statusTxt, xhr) {
					x = html(responseTxt);
					document.getElementById("transaction").innerHTML = x;
				});

		function capture() {
			$("#transaction").load("./exampleXML/Demo/example1.xml",
					function(responseTxt, statusTxt, xhr) {

						$.ajax({
							type : "POST",
							url : baseURL + "/Service/EventCapture",
							contentType : "application/xml; charset=utf-8",
							data : responseTxt
						}).done(function() {
							alert("Event is successfully stored");
							document.location.href = "./captureService2.jsp";
						});
					});
		}

		function back() {
			document.location.href = "./tutorialPage.jsp";
		}

		function skip() {
			document.location.href = "./captureService2.jsp";
		}

		function backToMainPage() {
			document.location.href = "./tutorialPage.jsp";
		}
	</script>
</body>
</html>
