<!DOCTYPE html>
<%@ page import="org.oliot.epcis.configuration.Configuration"%>
<html>
<head>
<link rel="icon" href="image/autoid_logo.png">
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
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="./css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="./css/bootstrap-switch.min.css" rel="stylesheet"
	media="screen">
<link href="./css/bootstrap-select.min.css" rel="stylesheet"
	media="screen">
<title>EPCIS REST-Like Web Service</title>
</head>
<%
	String facebookAppID = Configuration.facebookAppID;
%>
<script src="./js/jquery.min.js"></script>
<script src="./js/bootstrap.min.js"></script>
<script src="./js/bootstrap-switch.min.js"></script>
<script src="./js/bootstrap-select.js"></script>

<!-- For Facebook Integration -->
<script>
		// This is called with the results from from FB.getLoginStatus().
		function statusChangeCallback(response) {
			console.log('statusChangeCallback');
			console.log(response);
			// The response object is returned with a status field that lets the
			// app know the current login status of the person.
			// Full docs on the response object can be found in the documentation
			// for FB.getLoginStatus().
			if (response.status === 'connected') {
				// Logged into your app and Facebook.
				getFacebookInformation();
				$('#fAccessToken').val(response.authResponse.accessToken).hide().fadeIn('slow');
				$("#scButton").prop("disabled", false);
				$('#consoleMsg').val("Login Success").hide().fadeIn('slow');
			} else if (response.status === 'not_authorized') {
				// The person is logged into Facebook, but not your app.
				console.log("Please Log into this App");
				$("#scButton").prop("disabled", true);
				$('#consoleMsg').val("Need Facebook Login").hide().fadeIn('slow');
			} else {
				// The person is not logged into Facebook, so we're not sure if
				// they are logged into this app or not.
				console.log("Please Log into this App");
				$("#scButton").prop("disabled", true);
				$('#consoleMsg').val("Need Facebook Login").hide().fadeIn('slow');
			}
		}

		// This function is called when someone finishes with the Login
		// Button.  See the onlogin handler attached to it in the sample
		// code below.
		function checkLoginState() {
			FB.getLoginStatus(function(response) {
				statusChangeCallback(response);
			});
		}

		window.fbAsyncInit = function() {
			FB.init({
				appId : "<%=facebookAppID%>",
			cookie : true, // enable cookies to allow the server to access 
			// the session
			xfbml : true, // parse social plugins on this page
			version : 'v2.5' // use version 2.2
		});

		// Now that we've initialized the JavaScript SDK, we call 
		// FB.getLoginStatus().  This function gets the state of the
		// person visiting this page and can return one of three states to
		// the callback you provide.  They can be:
		//
		// 1. Logged into your app ('connected')
		// 2. Logged into Facebook, but not your app ('not_authorized')
		// 3. Not logged into Facebook and can't tell if they are logged into
		//    your app or not.
		//
		// These three cases are handled in the callback function.

		FB.getLoginStatus(function(response) {
			statusChangeCallback(response);
		});

	};

	// Load the SDK asynchronously
	(function(d, s, id) {
		var js, fjs = d.getElementsByTagName(s)[0];
		if (d.getElementById(id))
			return;
		js = d.createElement(s);
		js.id = id;
		js.src = "//connect.facebook.net/en_US/sdk.js";
		fjs.parentNode.insertBefore(js, fjs);
	}(document, 'script', 'facebook-jssdk'));

	// Here we run a very simple test of the Graph API after login is
	// successful.  See statusChangeCallback() for when this call is made.
	function getFacebookInformation() {
		console.log('Welcome!  Fetching your information.... ');
		FB.api('/me?fields=name,id,email', function(response) {
			console.log('Successful login for: ' + response.name);
			$('#fid').val(response.id).hide().fadeIn('slow');
		});
		var facebookAppID = "<%=facebookAppID%>
	";
		FB.api('/' + facebookAppID, function(response) {
			$('#consoleMsg').val('Welcome to ' + response.name).hide().fadeIn(
					'slow');
		});
	}
</script>


<script>
	$(document)
			.ready(
					function() {
						$('#addBaseURL')
								.val(
										"http://"
												+ location.host
												+ "/epcis/Service/Admin/NamedEventQuery/{name}?");
						$('#getURL')
								.val(
										"http://"
												+ location.host
												+ "/epcis/Service/Admin/NamedEventQuery");
						$('#deleteURL')
								.val(
										"http://"
												+ location.host
												+ "/epcis/Service/Admin/NamedEventQuery/{name}?");

						systemInfoURL = 'http://' + location.host
								+ '/epcis/Service/Admin/SystemInformation';
						$
								.ajax({
									type : "GET",
									url : systemInfoURL
								})
								.done(
										function(result) {
											$("#sysInfoResp")
													.val(
															"[EPCIS] "
																	+ location.host
																	+ "/epcis"
																	+ " -> "
																	+ "[MongoDB] "
																	+ result.backend_database_name
																	+ " database in "
																	+ result.backend_ip
																	+ ":"
																	+ result.backend_port);
										});

					});

	function addNamedEventQuery() {
		var baseURL = $("#addBaseURL").val();
		var params = $("#pollParam").val();
		var desc = $("#description").val();
		var fid = $("#fid").val();
		var fAccessToken = $("#fAccessToken").val();

		var url = baseURL + params + "&description=" + desc + "&userID=" + fid
				+ "&accessToken=" + fAccessToken;
		$.get(
				url,
				function(ret) {
					if (typeof ret == 'object') {
						$("#xmlTextArea").val(
								(new XMLSerializer()).serializeToString(ret))
								.hide().fadeIn();
					} else {
						$("#xmlTextArea").val(ret).hide().fadeIn();
					}
				}).fail(function(e) {
			$("#xmlTextArea").val(e.responseText).hide().fadeIn();
		});
	}

	function getNamedEventQueries() {
		var url = $("#getURL").val();
		$.get(url, function(ret) {
			$("#xmlTextArea").val(JSON.stringify(ret));
		}).fail(function(e) {
			$("#xmlTextArea").val(e.responseText).hide().fadeIn();
		});
	}
	function deleteNamedEventQuery() {
		var base = $("#deleteURL").val();
		var fid = $("#fid").val();
		var fAccessToken = $("#fAccessToken").val();
		var u = base + "&userID=" + fid + "&accessToken=" + fAccessToken;

		$.ajax({
			type : "DELETE",
			url : u
		}).done(function(ret) {
			$("#xmlTextArea").val(ret);
		}).error(function(e) {
			$("#xmlTextArea").val(e.responseText).hide().fadeIn();
		});
	}

	function addParam(type) {
		var curParam = $("#pollParam").val();
		curParam += type + "=" + $("#" + type).val() + "&";
		$("#pollParam").val(curParam);
	}

	function addFamParam(type, famType) {

		var curParam = $("#pollParam").val();
		curParam += type + "_" + $("#" + famType).val() + "="
				+ $("#" + type).val() + "&";

		$("#pollParam").val(curParam);
	}

	function replaceURL(type) {
		if (type == "event") {
			var str = $("#addBaseURL").val();
			str = str.replace("SimpleMasterDataQuery", "SimpleEventQuery");
			$("#addBaseURL").val(str);
			$("#pollParam").val("");
		} else if (type == "vocabulary") {
			var str = $("#addBaseURL").val();
			str = str.replace("SimpleEventQuery", "SimpleMasterDataQuery");
			$("#addBaseURL").val(str);
			$("#pollParam").val("includeAttributes=true&includeChildren=true&");
		}
	}

	function movePage(page) {
		document.location.href = page;
	}

	function reset() {
		$('#pollParam').val('');
	}
</script>

<body>

	<div class="panel panel-info">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="#">Oliot EPCIS Named Event Query
				Management Tool</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<form class="navbar-form navbar-right">
				<button type="button" class="btn btn-success"
					onclick="movePage('index.jsp')">Back</button>
			</form>
		</div>

		<fb:login-button
			data-scope="public_profile,user_friends,user_about_me,user_events,user_likes,user_location,user_posts,user_relationships,user_relationship_details,email"
			onlogin="checkLoginState();" auto_logout_link="true"
			class="panel-body">
		</fb:login-button>
		<input id="consoleMsg" type="text" class="form-control" disabled
			placeholder="Message from Facebook"> <input id="fid"
			type="hidden" class="form-control"
			placeholder="ID will be shown here"> <br> <input
			id="fAccessToken" type="hidden" class="form-control"
			placeholder="Access Token will be shown here">

		<div class="panel-body">
			<div class="row">
				<div class="col-sm-4">
					<h4>Named Event Query Management URL (Change IP or Domain if
						needed)</h4>
					<input id="sysInfoResp" type="text" class="form-control" disabled
						placeholder="System Information..."> <input
						id="addBaseURL" type="text" class="form-control"
						value="http://localhost:8080/epcis/Service/Poll/SimpleEventQuery?"
						placeholder="http://localhost:8080/epcis/Service/Poll/SimpleEventQuery">
					<input id="description" type="text" class="form-control"
						placeholder="Description for NamedEventQuery to be added">
					<input id="pollParam" type="text" class="form-control"
						placeholder="POLL PARAMETERS">
					<button type="button" class="btn btn-info"
						onclick="addNamedEventQuery()">Add NamedEventQuery</button>
					<button type="reset" class="btn btn-danger" onclick="reset()">Reset
						Params</button>
					<br> <br> <input id="getURL" type="text"
						class="form-control"
						value="http://localhost:8080/epcis/Service/Admin/NamedEventQuery"
						placeholder="http://localhost:8080/epcis/Service/Admin/NamedEventQuery">
					<button type="button" class="btn btn-success"
						onclick="getNamedEventQueries()">Get NamedEventQueries</button>

					<input id="deleteURL" type="text" class="form-control"
						value="http://localhost:8080/epcis/Service/Admin/NamedEventQuery"
						placeholder="http://localhost:8080/epcis/Service/Admin/NamedEventQuery">
					<button type="button" class="btn btn-success"
						onclick="deleteNamedEventQuery()">Delete NamedEventQuery</button>
				</div>
				<div class="col-sm-8">
					<h4>Query Response</h4>
					<textarea id="xmlTextArea" class="form-control auto-text-area"
						rows="16"></textarea>
				</div>
			</div>
		</div>

		<div class="panel-body">
			<table class="table table-bordered">
				<thead>
					<tr>
						<td>Method Name</td>
						<td>Description</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>getNamedEventQueries</td>
						<td>
							<ul class="list-group">
								<li class="list-group-item">Return existing
									NamedEventQueries</li>
								<li class="list-group-item"><code>HTTP.GET</code><br>
									URL:
									http://{base-url}:{base-port}/epcis/Service/Admin/NamedEventQuery</li>
								<li class="list-group-item">Return Type: application/json</li>
								<li class="list-group-item">No Access Control Needed</li>
							</ul>
						</td>
					</tr>
					<tr>
						<td>deleteNamedEventQuery</td>
						<td>
							<ul class="list-group">
								<li class="list-group-item">Delete existing NamedEventQuery</li>
								<li class="list-group-item"><code>HTTP.DELETE</code><br>URL:
									http://{base-url}:{base-port}/epcis/Service/Admin/NamedEventQuery/{name}</li>
								<li class="list-group-item">Return Type: application/text</li>
								<li class="list-group-item">Administrator method</li>
							</ul>
						</td>
					</tr>
					<tr>
						<td>addNamedEventQueries</td>
						<td>
							<ul class="list-group">
								<li class="list-group-item">Register NamedEventQuery</li>
								<li class="list-group-item"><code>HTTP.GET</code><br>URL:
									http://{base-url}:{base-port}/epcis/Service/Admin/NamedEventQuery/{name}</li>
								<li class="list-group-item">Administrator method</li>
								<li class="list-group-item">Parameters: <br> <code>List
										of characters should be encoded: + -> %2B , # -> %23, ^ -> %5E</code><br>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3"><code>new</code>format</span>
										<input id="format" type="text" class="form-control"
											placeholder="XML or JSON" aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('format')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('format')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3"><code>new</code>PROJECTION_</span>
										<input id="projectionType" type="text" class="form-control"
											placeholder="Projection Key (e.g., eventTime, extension.quantityList, bizLocation)"
											aria-describedby="basic-addon3"> <input
											id="PROJECTION" type="text" class="form-control"
											placeholder="true or false, true/false cannot be mixed, if true, eventType is automatically set as true"
											aria-describedby="basic-addon3"> <span
											class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addFamParam('PROJECTION','projectionType')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3"><code>new</code>NEAR_</span>
										<input id="nearType" type="text" class="form-control"
											placeholder="Extension Key indexed by 2dsphere (e.g., http://ns.example.com/epcis%23point). Please create 2dsphere index first (e.g., db.EventData.createIndex({'any.http://ns．example．com/epcis#point': '2dsphere' }); we encode '.' peiod with \uff0e inside MongoDB)"
											aria-describedby="basic-addon3"> <input id="NEAR"
											type="text" class="form-control"
											placeholder="CSV of Longitude, Latitude, Minimum (meter, optional), Maximum (meter, optional)"
											aria-describedby="basic-addon3"> <span
											class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addFamParam('NEAR','nearType')">ADD</button>
										</span>
									</div>
									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">eventType</span>
										<input id="eventType" type="text" class="form-control"
											placeholder="Vertical bar Separated Values (VSV), Regex (e.g., ObjectEvent, ObjectEvent|AggregationEvent, (.)*Event^regex)"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('eventType')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('eventType')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">GE_eventTime</span>
										<input id="GE_eventTime" type="text" class="form-control"
											placeholder="yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('GE_eventTime')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('GE_eventTime')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">LT_eventTime</span>
										<input id="LT_eventTime" type="text" class="form-control"
											placeholder="yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('LT_eventTime')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('LT_eventTime')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">GE_recordTime</span>
										<input id="GE_recordTime" type="text" class="form-control"
											placeholder="yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('GE_recordTime')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('GE_recordTime')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">LT_recordTime</span>
										<input id="LT_recordTime" type="text" class="form-control"
											placeholder="yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('LT_recordTime')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('LT_recordTime')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">GE_errorDeclarationTime</span>
										<input id="GE_errorDeclarationTime" type="text"
											class="form-control"
											placeholder="yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('GE_errorDeclarationTime')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('GE_errorDeclarationTime')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">LT_errorDeclarationTime</span>
										<input id="LT_errorDeclarationTime" type="text"
											class="form-control"
											placeholder="yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('LT_errorDeclarationTime')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('LT_errorDeclarationTime')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">EQ_action</span>
										<input id="EQ_action" type="text" class="form-control"
											placeholder="VSV (e.g. ADD, OBSERVE, DELETE)"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('EQ_action')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('EQ_action')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">EQ_bizStep</span>
										<input id="EQ_bizStep" type="text" class="form-control"
											placeholder="VSV, Regex (e.g., urn:epcglobal:cbv:bizstep:shipping, urn:epcglobal:cbv:bizstep:........^regex)"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('EQ_bizStep')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('EQ_bizStep')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">EQ_disposition</span>
										<input id="EQ_disposition" type="text" class="form-control"
											placeholder="VSV, Regex (e.g., urn:epcglobal:cbv:disp:in_transit, urn:epcglobal:cbv:disp:..........^regex )"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('EQ_disposition')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('EQ_disposition')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">EQ_readPoint</span>
										<input id="EQ_readPoint" type="text" class="form-control"
											placeholder="VSV, Regex (e.g., urn:epc:id:sgln:0614141.07346.1234 )"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('EQ_readPoint')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('EQ_readPoint')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">WD_readPoint</span>
										<input id="WD_readPoint" type="text" class="form-control"
											placeholder="VSV, Regex (e.g., urn:epc:id:sgln:0614141[.]07346[.]....^regex )"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('WD_readPoint')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('WD_readPoint')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">EQ_bizLocation</span>
										<input id="EQ_bizLocation" type="text" class="form-control"
											placeholder="VSV, Regex (e.g., urn:epc:id:sgln:0614141.07346.1234 )"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('EQ_bizLocation')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('EQ_bizLocation')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">WD_bizLocation</span>
										<input id="WD_bizLocation" type="text" class="form-control"
											placeholder="VSV, Regex (e.g., urn:epc:id:sgln:0614141[.]07346[.]....^regex )"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('WD_bizLocation')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('WD_bizLocation')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">EQ_transformationID</span>
										<input id="EQ_transformationID" type="text"
											class="form-control" placeholder="VSV, Regex"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('EQ_transformationID')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('EQ_transformationID')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">MATCH_epc</span>
										<input id="MATCH_epc" type="text" class="form-control"
											placeholder="VSV, Regex (e.g., urn:epc:id:sgtin:0614141.107346.2018)"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('MATCH_epc')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('MATCH_epc')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">MATCH_parentID</span>
										<input id="MATCH_parentID" type="text" class="form-control"
											placeholder="VSV, Regex (e.g., urn:epc:id:sgtin:0614141[.]......[.]....^regex)"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('MATCH_parentID')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('MATCH_parentID')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">MATCH_inputEPC</span>
										<input id="MATCH_inputEPC" type="text" class="form-control"
											placeholder="VSV, Regex" aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('MATCH_inputEPC')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('MATCH_inputEPC')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">MATCH_outputEPC</span>
										<input id="MATCH_outputEPC" type="text" class="form-control"
											placeholder="VSV, Regex" aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('MATCH_outputEPC')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('MATCH_outputEPC')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">MATCH_anyEPC</span>
										<input id="MATCH_anyEPC" type="text" class="form-control"
											placeholder="VSV, Regex" aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('MATCH_anyEPC')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('MATCH_anyEPC')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">MATCH_epcClass</span>
										<input id="MATCH_epcClass" type="text" class="form-control"
											placeholder="VSV, Regex (e.g., urn:epc:class:lgtin:4012345.012345.998877)"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('MATCH_epcClass')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('MATCH_epcClass')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">MATCH_anyEPCClass</span>
										<input id="MATCH_anyEPCClass" type="text" class="form-control"
											placeholder="VSV, Regex (e.g., urn:epc:idpat:sgtin:4012345.098765.*)"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('MATCH_anyEPCClass')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('MATCH_anyEPCClass')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">MATCH_inputEPCClass</span>
										<input id="MATCH_inputEPCClass" type="text"
											class="form-control"
											placeholder="VSV, Regex (e.g., urn:epc:class:lgtin:4012345[.]012345[.]......^regex)"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('MATCH_inputEPCClass')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('MATCH_inputEPCClass')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">MATCH_outputEPCClass</span>
										<input id="MATCH_outputEPCClass" type="text"
											class="form-control" placeholder="VSV, Regex"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('MATCH_outputEPCClass')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('MATCH_outputEPCClass')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">EQ_bizTransaction_</span>
										<input id="bizTransactionType" type="text"
											class="form-control"
											placeholder="BizTransactionType (e.g., urn:epcglobal:cbv:btt:po)"
											aria-describedby="basic-addon3"> <input
											id="EQ_bizTransaction" type="text" class="form-control"
											placeholder="BizTransactionValue VSV (e.g., http://transaction.acme.com/po/12345678)"
											aria-describedby="basic-addon3"> <span
											class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addFamParam('EQ_bizTransaction','bizTransactionType')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">EQ_source_</span>
										<input id="sourceType" type="text" class="form-control"
											placeholder="SourceType (e.g., urn:epcglobal:cbv:sdt:possessing_party)"
											aria-describedby="basic-addon3"> <input
											id="EQ_source" type="text" class="form-control"
											placeholder="SourceValue VSV (e.g., urn:epc:id:sgln:4012345.00001.0)"
											aria-describedby="basic-addon3"> <span
											class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addFamParam('EQ_source','sourceType')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">EQ_destination_</span>
										<input id="destinationType" type="text" class="form-control"
											placeholder="DestinationType (e.g., urn:epcglobal:cbv:sdt:owning_party)"
											aria-describedby="basic-addon3"> <input
											id="EQ_destination" type="text" class="form-control"
											placeholder="DestinationValue VSV (e.g., urn:epc:id:sgln:0614141.00001.0)"
											aria-describedby="basic-addon3"> <span
											class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addFamParam('EQ_destination','destinationType')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">EQ_</span> <input
											id="eqextensionType" type="text" class="form-control"
											placeholder="extensionType (e.g., http://ns.example.com/epcis0%23a)"
											aria-describedby="basic-addon3"> <input id="EQ"
											type="text" class="form-control"
											placeholder="extensionValue Type-available VSV, Regex (e.g., 15%5Eint, EQ_http://ns.example.com/epcis0%23a=string, EQ_http://ns.example.com/epcis1%23c=20.5%5Edouble,, EQ_http://ns.example.com/epcis2%23f=2013-06-08T23:58:56.591%2B09:00%5EdateTime and	long/boolean are available)"
											aria-describedby="basic-addon3"> <span
											class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addFamParam('EQ','eqextensionType')">ADD</button>
										</span>
									</div>


									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">GT_</span> <input
											id="gtextensionType" type="text" class="form-control"
											placeholder="extensionType" aria-describedby="basic-addon3">
										<input id="GT" type="text" class="form-control"
											placeholder="Type-available VSV"
											aria-describedby="basic-addon3"> <span
											class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addFamParam('GT','gtextensionType')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">GE_</span>
										<input id="geextensionType" type="text" class="form-control"
											placeholder="extensionType" aria-describedby="basic-addon3">
										<input id="GE" type="text" class="form-control"
											placeholder="Type-available VSV"
											aria-describedby="basic-addon3"> <span
											class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addFamParam('GE','geextensionType')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">LT_</span>
										<input id="ltextensionType" type="text" class="form-control"
											placeholder="extensionType" aria-describedby="basic-addon3">
										<input id="LT" type="text" class="form-control"
											placeholder="Type-available VSV"
											aria-describedby="basic-addon3"> <span
											class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addFamParam('LT','ltextensionType')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">LE_</span>
										<input id="leextensionType" type="text" class="form-control"
											placeholder="extensionType" aria-describedby="basic-addon3">
										<input id="LE" type="text" class="form-control"
											placeholder="Type-available VSV"
											aria-describedby="basic-addon3"> <span
											class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addFamParam('LE','leextensionType')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">EXISTS_</span>
										<input id="existExtensionType" type="text"
											class="form-control"
											placeholder="field name (e.g., example:a)"
											aria-describedby="basic-addon3"> <input id="EXISTS"
											type="text" class="form-control" placeholder="true or false"
											aria-describedby="basic-addon3"> <span
											class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addFamParam('EXISTS','existExtensionType')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">EXISTS_ILMD_</span>
										<input id="existILMDExtensionType" type="text"
											class="form-control"
											placeholder="ILMD field name (e.g., example:a)"
											aria-describedby="basic-addon3"> <input
											id="EXISTS_ILMD" type="text" class="form-control"
											placeholder="true or false" aria-describedby="basic-addon3">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addFamParam('EXISTS_ILMD','existILMDExtensionType')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">EXISTS_errorDeclaration</span>
										<input id="EXISTS_errorDeclaration" type="text"
											class="form-control" placeholder="true or false"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('EXISTS_errorDeclaration')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('EXISTS_errorDeclaration')">ADD</button>
										</span>

									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">EQ_eventID</span>
										<input id="EQ_eventID" type="text" class="form-control"
											placeholder="User-defined eventID or auto-generated Mongo object ID (e.g., 57294415deab32126d209d13)"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('EQ_eventID')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('EQ_eventID')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">EQ_errorReason</span>
										<input id="EQ_errorReason" type="text" class="form-control"
											placeholder="VSV, Regex (e.g., urn:epcglobal:cbv:error:add)"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('EQ_errorReason')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('EQ_errorReason')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">EQ_correctiveEventID</span>
										<input id="EQ_correctiveEventID" type="text"
											class="form-control"
											placeholder="VSV, Regex (e.g., 5722d7e1deab322596705146^regex )"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('EQ_correctiveEventID')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('EQ_correctiveEventID')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">orderBy</span>
										<input id="orderBy" type="text" class="form-control"
											placeholder="(eventTime|recordTime|fieldName)"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('orderBy')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('orderBy')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">orderDirection</span>
										<input id="orderDirection" type="text" class="form-control"
											placeholder="ASC or DESC (default)"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('orderDirection')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('orderDirection')">ADD</button>
										</span>
									</div>

									<div class="input-group input-group-sm">
										<span class="input-group-addon" id="basic-addon3">eventCountLimit</span>
										<input id="eventCountLimit" type="number" class="form-control"
											placeholder="Limit the size of returning events"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('eventCountLimit')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('eventCountLimit')">ADD</button>
										</span> <span class="input-group-addon" id="basic-addon3">maxEventCount</span>
										<input id="maxEventCount" type="number" class="form-control"
											placeholder="Yield error if # of events exceeds specified number"
											aria-describedby="basic-addon3"
											onkeydown="if (event.keyCode == 13) addParam('maxEventCount')">
										<span class="input-group-btn" aria-describedby="basic-addon3">
											<button class="btn btn-default" type="button"
												onclick="addParam('orderDirection')">ADD</button>
										</span>
									</div> <code>NOTE: (EQ|GT|GE|LT|LE)_INNER_ ,
										(EQ|GT|GE|LT|LE)_ILMD_, (EQ|GT|GE|LT|LE)_INNER_ILMD,
										(EQ|GT|GE|LT|LE)_ERROR_DECLARATION_,
										(EQ|GT|GE|LT|LE)_INNER_ERROR_DECLARATION_ are supported like
										(EQ|GT|GE|LT|GE)_ </code> <br> <br>

								</li>
							</ul>
						</td>
					</tr>

				</tbody>
			</table>
		</div>
	</div>
	<footer>
		<code style="font-size: 12pt">&copy; Auto-ID Labs, KAIST 2018</code>
	</footer>
</body>
</html>