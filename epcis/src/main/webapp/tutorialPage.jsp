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
<link href="./css/carousel.css" rel="stylesheet">

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script src="./js/bootstrap.js"></script>

<script>
	href = window.location.href;
	hrefArr = href.split("/");
	var baseURL = hrefArr[0] + "//" + hrefArr[2] + "/epcis";
</script>

</head>
<body>
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
				<a class="navbar-brand" href="#">EPCIS v1.1 Tutorial and Demonstration ( Oliot Opensource Project )</a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
				<form class="navbar-form navbar-right">
					<button type="submit" class="btn btn-success" onclick="resetDB()">RESET
						DB</button>
				</form>
			</div>
			<!--/.navbar-collapse -->
		</div>
	</nav>
	<!-- Carousel
    ================================================== -->
	<div id="myCarousel" class="carousel slide" data-ride="carousel">
		<!-- Indicators -->
		<ol class="carousel-indicators">
			<li data-target="#myCarousel" data-slide-to="0" class="active"></li>
			<li data-target="#myCarousel" data-slide-to="1"></li>
			<li data-target="#myCarousel" data-slide-to="2"></li>
			<li data-target="#myCarousel" data-slide-to="3"></li>
			<li data-target="#myCarousel" data-slide-to="4"></li>
			<li data-target="#myCarousel" data-slide-to="5"></li>
		</ol>
		<div class="carousel-inner" role="listbox">
			<div class="item active">
				<img width="100%" src="./image/epcis.png" alt="1">
				<div class="container">
					<div class="carousel-caption">
						<h1 style="font-color: #FFFFFF">EPCIS Overview</h1>
						<h4>
							<abbr title="Electronic Product Code Information Service" class="initialism">EPCIS</abbr> enables to
							capture and share EPC-based event.<br> With EPCIS, event
							producer generates and sends events complying with EPCIS schema
							into EPCIS Repository.<br> Then, these events can be
							globally shared with given queries.
						</h4>
						<p>
							<a class="btn btn-lg btn-primary" href="index.jsp" role="button">Detail...
								&raquo;</a>
						</p>
					</div>
				</div>
			</div>
			<div class="item">
				<img width="100%" src="./image/inspection.png" alt="2">
				<div class="container">
					<div class="carousel-caption">
						<h1>When Customer Wants to Know about BEEF PACK</h1>
						<h4>EPCIS and RFID technology can give us solution</h4>
					</div>
				</div>
			</div>
			<div class="item">
				<img width="100%" src="./image/transaction.png" alt="3">
				<div class="container">
					<div class="carousel-caption">
						<h1>EPCIS Event Producer 1: Dairy Farm</h1>
						<h4>Capture when the cow was sold</h4>
					</div>
				</div>
			</div>
			<div class="item">
				<img width="100%" src="./image/transformation.png" alt="4">
				<div class="container">
					<div class="carousel-caption">
						<h1>EPCIS Event Producer 2: Butcher Shop</h1>
						<h4>Capture when the cow was transformed to beef packs</h4>
					</div>
				</div>
			</div>
			<div class="item">
				<img width="100%" src="./image/object.png" alt="5">
				<div class="container">
					<div class="carousel-caption">
						<h1>EPCIS Event Producer 3: Retail</h1>
						<h4>Capture when one of the beef packs was ready to be sold</h4>
					</div>
				</div>
			</div>
			<div class="item">
				<img width="100%" src="./image/trace.png" alt="6">
				<div class="container">
					<div class="carousel-caption">
						<h1>EPCIS visualizes everyday-objects</h1>
						<h4>Trace the history</h4>
					</div>
				</div>
			</div>
		</div>
		<a class="left carousel-control" href="#myCarousel" role="button"
			data-slide="prev"> <span class="glyphicon glyphicon-chevron-left"
			aria-hidden="true"></span> <span class="sr-only">Previous</span>
		</a> <a class="right carousel-control" href="#myCarousel" role="button"
			data-slide="next"> <span
			class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
			<span class="sr-only">Next</span>
		</a>
	</div>
	<!-- /.carousel -->

	<div class="container">
		<!-- Example row of columns -->
		<div class="row">
			<div class="col-md-4">
				<h2>Overview</h2>
				<p>Customers in retail want to trace the beefs in terms of
					Where, When, and Why. This tutorial gives the guideline about</p>
				<ul>
					<li>How Dairy Farm, Butcher Shop, and Retail can record the
						important events on the cow.</li>
					<li>How Customer easily traces the beef pack</li>
				</ul>
			</div>
			<div class="col-md-4">
				<h2>Write Your Events</h2>
				<p>This tutorial explains how you can record the important
					events based on EPCIS document.</p>
				<p>This tutorial helps you understand what the elements in the
					XML mean step by step with three business sites and three EPCIS
					event types.</p>
				<p>
					<a class="btn btn-default" href="./captureService1.jsp"
						role="button">Try it now &raquo;</a>
				</p>
			</div>
			<div class="col-md-4">
				<h2>Trace the history</h2>
				<p>This tutorial gives you a map service which traces the life
					log on the specific EPC.</p>
				<p></p>
				<p>
					<a class="btn btn-default" href="./mapService.jsp" role="button">See
						it now &raquo;</a>
				</p>
			</div>
		</div>

		<hr>

		<footer>
			<code style="font-size: 12pt">Auto-ID Labs. Korea 2015</code><br><br>
			<p class="lead" style="font-size: 12pt; color: blue; margin-top: 0pt; margin-bottom: 0pt" >Contact</p>
			<p>
				Jaewook Jack Byun, Ph.D student<br>Korea Advanced Institute of
				Science and Technology (KAIST) <br>bjw0829@kaist.ac.kr,
				bjw0829@gmail.com
			</p>
		</footer>
	</div>

	<script>
		function resetDB() {
			$.get(baseURL +"/Service/ResetDB");
			alert("Repository Initialized");
		}
	</script>

	<script src="js/bootstrap.min.js"></script>
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>

</body>
</html>
