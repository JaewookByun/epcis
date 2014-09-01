<html>
<body>
	<h2>Welcome to Oliot-EPCIS</h2>
	<br>
	<br>Spec:
	<br>-EPCIS v1.1
	<br>-Java, Dynamic Web Service
	<br>-Maven
	<br>-Spring Framework
	<br>
	<br><h2>Service:</h2>
	<br>-Capture
	<br>--Description: this service allows you to store your EPCIS
	Document into your backend storage (e.g. Mongo DB)
	<br>--method: post
	<br>--contents: raw-xml-text, complying with
	<a
		href="http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-schema-20140520/EPCglobal-epcis-1_1.xsd">EPCIS
		v1.1 XML Schema</a>
	<br>--url: http://{base-url}:{base-port}/epcis/Service/capture
	<br>
	<br>-ALE Capture
	<br>--Description: this service allows you to store your ECReport
	into your backend storage after converting it to EPCIS Document
	<br>--method: post
	<br>--contents: raw-xml-text, complying with
	<a
		href="http://www.gs1.org/gsmp/kc/epcglobal/ale/ale_1_1-schemas-20071202/EPCglobal-ale-1_1-ale.xsd">ALE
		v1.1 XML Schema</a><br>--url: http://{base-url}:{base-port}/epcis/Service/aleCapture<br><br>


</body>
</html>
