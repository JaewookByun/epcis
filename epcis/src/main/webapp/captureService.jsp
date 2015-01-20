
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>Transaction Event</title>
<!-- The defer is not necessary for autoloading, but is necessary for the
     script at the bottom to work as a Quine. -->
<script src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js?autoload=true&amp;skin=doxy&amp;lang=html" defer="defer"></script>
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<style>.operative { font-weight: bold; border:1px solid yellow }</style>
</head>

<body>
<h1>Transaction Event</h1>

<!-- Language hints can be put in XML application directive style comments. -->
<?prettify lang=html linenums=1 skin=default?>
<pre class="prettyprint linenums" id="transaction" style="border:4px solid #88c">
</pre>

<script>

function html(s) {
    return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

$("#transaction").load("./exampleXML/gs1example1(Object).xml", function( responseTxt, statusTxt, xhr){
	x = html(responseTxt);
	document.getElementById("transaction").innerHTML = x;
});

var $pp = $("#transaction");
console.log($pp.attr());
</script>


</body>
</html>
