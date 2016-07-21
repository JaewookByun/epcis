<!DOCTYPE html>
<%@ page import="org.oliot.epcis.configuration.Configuration"%>
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
	})(window, document, 'script',
			'https://www.google-analytics.com/analytics.js', 'ga');

	ga('create', 'UA-64257932-1', 'auto');
	ga('send', 'pageview');
</script>
<link href="./css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="./css/bootstrap-switch.min.css" rel="stylesheet"
	media="screen">
<link href="./css/bootstrap-select.min.css" rel="stylesheet"
	media="screen">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
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
		var facebookAppID = "<%=facebookAppID%>";
		FB.api('/'+facebookAppID, function(response) {
			$('#consoleMsg').val('Welcome to '+response.name).hide().fadeIn('slow');
		});
	}
</script>


<script>
	$(document).ready(function() {
		// Loading a selected example to the text area
		$(".dropdown-menu").on("click", "li a", function(event) {
			$("#xmlTextArea").load($(this)[0].id);
		})
		$('#resetURL').val("http://"+location.host+"/epcis/Service/Admin/ResetDB");
	});
	
	function movePage(page) {
		document.location.href = page;
	}
	
	function resetDB(){
		var baseURL = $("#resetURL").val();
		var fid = $("#fid").val();
		var fAccessToken = $("#fAccessToken").val();
		var url = baseURL + "?userID=" + fid+"&accessToken="+fAccessToken;
		$.get(
				url,
				function(ret) {
					if (typeof ret == 'object') {
						$("#consoleMsg").val(
								(new XMLSerializer()).serializeToString(ret))
								.hide().fadeIn();
					} else {
						$("#consoleMsg").val(ret).hide().fadeIn();
					}
				}).fail(function(e) {
			$("#consoleMsg").val(e.responseText).hide().fadeIn();
		});
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
			<a class="navbar-brand" href="#">Oliot EPCIS Access Control Tool</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<form class="navbar-form navbar-right">
				<button type="button" class="btn btn-success"
					onclick="movePage('index.jsp')">Back</button>
			</form>
		</div>		

		<div class="panel-body">
			<div class="row">
				<div class="col-sm-12">
					<h4>Join In & Check your ID and access Token</h4>
					<fb:login-button
						data-scope="public_profile,user_friends,user_about_me,user_events,user_likes,user_location,user_posts,user_relationships,user_relationship_details,email"
						onlogin="checkLoginState();" auto_logout_link="true"
						class="panel-body">
					</fb:login-button>
					<input id="consoleMsg" type="text" class="form-control" disabled placeholder="Message from Facebook">
					<br>
					<p>Facebook ID</p>
					<input id="fid" type="text" class="form-control"
						placeholder="ID will be shown here"> <br>
					<p>Facebook Access Token</p>
					<input id="fAccessToken" type="text" class="form-control"
						placeholder="Access Token will be shown here">
					<br>
					<p>Reset Repository (Change IP or Domain if needed)</p>
					<input id="resetURL" type="text" class="form-control"
						value="http://localhost:8080/epcis/Service/Admin/ResetDB"
						placeholder="http://localhost:8080/epcis/Service/Admin/ResetDB">
					<br>
					<button type="button" class="btn btn-danger"
								onclick="resetDB()">Reset DB</button>	
				</div>
			</div>
		</div>
		
		<div class="panel-body">
			<div class="row">
				<div class="col-sm-12">
					<h4>Guideline for administrator</h4>
					<p>1. Create Facebook Application and Get Facebook Application
						ID.</p>
					<p>2. Set up the app ID, your facebook ID, and the delegation
						scope.</p>
					<p>2.1. If the scope is Private, the main administrator only
						uses the system function.</p>
					<p>2.2. If the scope is Friend, the friends of the
						administrator are the delegated administrators.</p>
					<p>2.2.1. The condition of 2.2 is to be a member of Facebook ID via
						the login button here.</p>
					<p>2.2.2. If the application is still 'test' one, the administrator should register friends as tester </p>
					<p>3. Try
						http://[URL]:[Port]/epcis/Service/Admin/ResetDB?userID={Your
						ID}&accessToken={Your Access Token}</p>
				</div>
			</div>
		</div>
	</div>
	<footer>
		<code style="font-size: 12pt">&copy; Auto-ID Labs. Korea 2016</code>
	</footer>
</body>
</html>