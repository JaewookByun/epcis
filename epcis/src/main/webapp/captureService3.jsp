
<!DOCTYPE html>
<html>
<head>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-64257932-1', 'auto');
  ga('send', 'pageview');

</script>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description"
	content="Tutorial for EPCIS v1.1. It peaks three different EPCIS events in the life of Cow">
<meta name="author" content="Jaewook Jack Byun">

<title>Write Your Events</title>

<link rel="stylesheet" href="./css/bootstrap.min.css">

<script
	src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js?autoload=true&amp;skin=desert&amp;lang=html"
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
					style="text-align: center"> Object Event </a> <a href="#"
					class="list-group-item">An ObjectEvent captures information
					about an event pertaining to one or more physical or digital
					objects</a> <a href="#" class="list-group-item active"
					style="text-align: center"> Object Event Example - In the
					retail shop </a> <a href="#" class="list-group-item"> &#8226; When:
					2015-01-03T20:33:31 <br> &#8226; What:
					urn:epc:id:sgtin:4012345.077889.27 (Beef part) <br>
					&nbsp;&nbsp;&nbsp; &#8226; 0.6kg <br> &#8226; Where:
					urn:epc:id:sgln:0614141.07346.1235 (Retail Shop) <br> &#8226;
					Why: The beef pack is on display for sale <br>
					&nbsp;&nbsp;&nbsp; &#8226; Business Step: retail_selling <br>
					&nbsp;&nbsp;&nbsp; &#8226; Disposition: sellable_accessible
				</a> <a href="#" class="list-group-item active"
					style="text-align: center"> Capture Object Event </a> <a href="#"
					class="list-group-item"> Send left EPCIS Document as HTTP POST
					Message to <br> <code>http://{baseURL}:{port}/epcis/Service/EventCapture</code><br>
					<br>
					<button type="button" class="btn btn-warning" onclick="capture()">Capture
						this event</button>
				</a>
			</div>
			<button type="button" class="btn btn-danger" onclick="back()">Back
				to previous step</button>
			<button type="button" class="btn btn-info" onclick="skip()">Skip
				to next step</button>
			<br> <br>
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
	</div>

	<!-- Parker Ranch 		  20.019786, -155.681829 -->
	<!--  Butcher Lauren B    19.634746, -155.986547  -->
	<!-- Matsuyama Food Mart  19.708886, -155.893430 -->

	<!-- Language hints can be put in XML application directive style comments. -->



	<script>
		function html(s) {
			return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g,
					'&gt;');
		}

		$("#transaction").load("./exampleXML/cow-object.xml",
				function(responseTxt, statusTxt, xhr) {
					x = html(responseTxt);
					document.getElementById("transaction").innerHTML = x;
				});

		function capture() {
			$("#transaction").load("./exampleXML/cow-object.xml",
					function(responseTxt, statusTxt, xhr) {

						$.ajax({
							type : "POST",
							url : baseURL + "/Service/EventCapture",
							contentType : "application/xml; charset=utf-8",
							data : responseTxt
						}).done(function() {
							alert("Event is successfully stored");
							document.location.href = "./mapService.jsp";
						});
					});
		}

		function back() {
			document.location.href = "./captureService2.jsp";
		}

		function skip() {
			document.location.href = "./mapService.jsp";
		}
		function backToMainPage() {
			document.location.href = "./tutorialPage.jsp";
		}
	</script>
</body>
</html>
