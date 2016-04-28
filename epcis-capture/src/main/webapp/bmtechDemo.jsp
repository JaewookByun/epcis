<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="css/bootstrap-switch.min.css" rel="stylesheet"
	media="screen">
<link href="css/bootstrap-select.min.css" rel="stylesheet"
	media="screen">
</head>
<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="https://apis.google.com/js/plusone.js"
	type="text/javascript"></script>
<script src="js/bootstrap-switch.min.js"></script>
<script src="js/bootstrap-select.js"></script>

<script>
	$(function() {
		recordTimeString = null;
	});

	function clickBtn() {

		var btn = $("#btn");
		if (btn.text().indexOf("Start") > -1) {
			timer = setInterval(monitor, 1000);
			btn.text("Stop Monitoring");
		} else {
			clearInterval(timer);
			btn.text("Start Monitoring");
			$("#image")[0].src = "./image/a0.png";
		}
	}

	function monitor() {

		$
				.get(
						"Service/Poll/SimpleEventQuery?orderBy=recordTime&orderDirection=DESC&eventCountLimit=1",
						function(data) {
							// Insecticide
							var insecticide = data
									.getElementsByTagName("BMTech_data_insecticide")[0];
							if (insecticide != null
									&& insecticide.childNodes != null
									&& insecticide.childNodes.length >= 1) {
								var text = insecticide.childNodes[1].textContent
										.toLowerCase();

								var b1 = $("#b1");
								var b2 = $("#b2");
								var b3 = $("#b3");
								var b4 = $("#b4");

								if (text === "none") {
									$("#image")[0].src = "./image/a1.png";

									if (b1.hasClass("disabled")) {
										b1.removeClass("disabled");
									}
									if (!b2.hasClass("disabled")) {
										b2.addClass("disabled");
									}
									if (!b3.hasClass("disabled")) {
										b3.addClass("disabled");
									}
									if (!b4.hasClass("disabled")) {
										b4.addClass("disabled");
									}
								} else if (text === "low") {
									$("#image")[0].src = "./image/a3.png";
									
									if (!b1.hasClass("disabled")) {
										b1.addClass("disabled");
									}
									if (b2.hasClass("disabled")) {
										b2.removeClass("disabled");
									}
									if (!b3.hasClass("disabled")) {
										b3.addClass("disabled");
									}
									if (!b4.hasClass("disabled")) {
										b4.addClass("disabled");
									}
								} else {
									$("#image")[0].src = "./image/a4.png";
									if (!b1.hasClass("disabled")) {
										b1.addClass("disabled");
									}
									if (!b2.hasClass("disabled")) {
										b2.removeClass("disabled");
									}
									if (!b3.hasClass("disabled")) {
										b3.addClass("disabled");
									}
									if (b4.hasClass("disabled")) {
										b4.removeClass("disabled");
									}
								}
							}

							// Data Barcode
							var dataBarcode = data
									.getElementsByTagName("BMTech_data_barcode")[0];
							if (dataBarcode != null
									&& dataBarcode.childNodes != null
									&& dataBarcode.childNodes.length >= 1) {
								var text = dataBarcode.childNodes[1].textContent
										.toLowerCase();
								$("#dataBarcode").text("Data Barcode: " + text);
							}

							// Data NodeID
							var dataNodeId = data
									.getElementsByTagName("BMTech_data_nodeid")[0];
							if (dataNodeId != null
									&& dataNodeId.childNodes != null
									&& dataNodeId.childNodes.length >= 1) {
								var text = dataNodeId.childNodes[1].textContent
										.toLowerCase();
								$("#nodeId").text("Data NodeID: " + text);
							}

							// Event Time
							var eventTime = data
									.getElementsByTagName("BMTech_time")[0];
							if (eventTime != null
									&& eventTime.childNodes != null
									&& eventTime.childNodes.length >= 1) {
								var text = eventTime.childNodes[1].textContent
										.toLowerCase();
								$("#sensingTime").text("Event Time: " + text);
							}

							// Device ID
							var deviceID = data
									.getElementsByTagName("BMTech_did")[0];
							if (deviceID != null && deviceID.childNodes != null
									&& deviceID.childNodes.length >= 1) {
								var text = deviceID.childNodes[1].textContent
										.toLowerCase();
								$("#did").text("Device ID: " + text);
							}

							// Record Time to animate Border
							recordTime = data
									.getElementsByTagName("recordTime")[0];
							// Animate
							if (recordTimeString == null
									|| !(recordTimeString === recordTime.textContent)) {
								$("#image").css({
									border : '0 solid #f37736'
								}).animate({
									borderWidth : 4
								}, 500);
								$("#image").css({
									border : '4 solid #f37736'
								}).animate({
									borderWidth : 0
								}, 500);
								recordTimeString = recordTime.textContent;
							}
						});
	}
</script>

<body>


	<div class="panel panel-info">
		<div class="panel-heading">
			<h3 class="panel-title">BMTech Demonstration</h3>
		</div>
		<div class="panel-body">
			<div class="row">
				<div class="col-md-3">
					<h4>Insert EPC to be monitored</h4>
					<input type="text" class="form-control" id="usr"
						value="urn:epc:id:sgtin:0693472.058672.137438953501"><br>
					<button id="btn" type="button" class="btn btn-success"
						onclick="clickBtn()">Start Monitoring</button>
				</div>
				<div class="col-md-6">
					<img id="image" src="./image/a0.png"
						style="width: 700px; height: 400px;"><br>
					<p id="dataBarcode">Data Barcode:</p>
					<p id="nodeId">Data NodeID:</p>
					<p id="sensingTime">Event Time:</p>
					<p id="did">Device ID:</p>
				</div>
				<div class="col-md-3">
					<h4>Insecticide Level</h4>
					<button id="b1" type="button"
						class="btn btn-primary btn-sm disabled" style="width: 140px">No
						Insecticide</button>
					<br>
					<button id="b2" type="button" class="btn btn-warning btn-sm disabled"
						style="width: 140px">Low Insecticide</button>
					<br>
					<button id="b4" type="button"
						class="btn btn-danger btn-sm disabled" style="width: 140px">High
						Insecticide</button>
				</div>
			</div>

		</div>
	</div>
</body>
</html>
