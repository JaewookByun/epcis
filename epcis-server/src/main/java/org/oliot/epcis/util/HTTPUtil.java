package org.oliot.epcis.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.server.EPCISServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public class HTTPUtil {
	public static HttpResponse<String> post(URI uri, String body) throws IOException, InterruptedException {
		return HttpClient.newBuilder().build().send(HttpRequest.newBuilder().header("Content-Type", "application/xml")
				.uri(uri).POST(BodyPublishers.ofString(body)).build(), BodyHandlers.ofString());
	}

	public static void sendQueryResults(HttpServerResponse serverResponse, Object message, int statusCode,
			String contentType) {
		serverResponse.putHeader("content-type", contentType).putHeader("Access-Control-Expose-Headers", "*")
				.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
				.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
				.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(statusCode).end(message.toString());
	}

	public static void sendQueryResults(HttpServerResponse serverResponse, SOAPMessage message, Object result,
			Class<?> resultType, int statusCode) {
		message.putResult(result, resultType);
		serverResponse.putHeader("content-type", "application/xml; charset=utf-8")
				.putHeader("Access-Control-Expose-Headers", "*")
				.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
				.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
				.putHeader("GS1-Extensions", Metadata.GS1_Extensions).setStatusCode(statusCode).end(message.toString());
	}

	public static void sendQueryResults(ServerWebSocket serverWebSocket, short code, JsonObject message) {
		serverWebSocket.close(code, message.toString());
	}

	public static void sendQueryResults(ServerWebSocket serverWebSocket, JsonObject message) {
		serverWebSocket.writeTextMessage(message.toString());
	}

	public static void sendQueryResults(HttpServerResponse serverResponse, JsonObject message, int statusCode) {
		serverResponse.putHeader("content-type", "application/json; charset=utf-8")
				.putHeader("Access-Control-Expose-Headers", "*").setStatusCode(statusCode)
				.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
				.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
				.putHeader("GS1-Extensions", Metadata.GS1_Extensions).end(message.toString());
	}

	public static void sendQueryEmptyResults(HttpServerResponse serverResponse, int statusCode) {
		serverResponse.putHeader("Access-Control-Expose-Headers", "*").setStatusCode(statusCode)
				.putHeader("GS1-EPCIS-Version", Metadata.GS1_EPCIS_Version)
				.putHeader("GS1-CBV-Version", Metadata.GS1_CBV_Version)
				.putHeader("GS1-Extensions", Metadata.GS1_Extensions).end();
	}

	public static void sendQueryResults(HttpClient webClient, URI uri, Logger logger, SOAPMessage message,
			Object result, Class<?> resultType) {
		message.putResult(result, resultType);
		webClient = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(uri)
				.POST(HttpRequest.BodyPublishers.ofString(message.toString())).build();
		try {
			HttpResponse<String> response = webClient.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				EPCISServer.logger.debug("subscription result successfully sent");
			} else {
				EPCISServer.logger.debug("subscription result delivery fails");
			}
		} catch (Exception e) {
			EPCISServer.logger.debug("subscription result delivery fails: " + e.getMessage());
		}

	}

//	@SuppressWarnings("rawtypes")
//	public static void sendQueryResults(HttpServerResponse serverResponse, Object result, Document retDoc,
//			Element envelope, Element body, Class resultType, int statusCode) {
//		try {
//			if (retDoc == null) {
//				retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
//				envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
//				body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
//			}
//
//			JAXBContext jc = JAXBContext.newInstance(resultType);
//			Marshaller marshaller = jc.createMarshaller();
//			marshaller.marshal(result, body);
//
//			envelope.appendChild(body);
//			envelope.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
//			retDoc.appendChild(envelope);
//			retDoc.normalize();
//			serverResponse.putHeader("content-type", "application/xml; charset=utf-8").setStatusCode(statusCode)
//					.end(XMLUtil.toString(retDoc));
//		} catch (JAXBException | ParserConfigurationException e) {
//			e.printStackTrace();
//		}
//	}

	public static void sendQueryResults(ServerWebSocket ws, Object result, Document retDoc, Element envelope,
			Element body, Class<?> resultType) {
		try {
			if (retDoc == null) {
				retDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				envelope = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Envelope");
				body = retDoc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soapenv:Body");
			}

			JAXBContext jc = JAXBContext.newInstance(resultType);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(result, body);

			envelope.appendChild(body);
			envelope.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			retDoc.appendChild(envelope);
			retDoc.normalize();

			ws.writeTextMessage(XMLUtil.toString(retDoc));
		} catch (JAXBException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void sendQueryResults(HttpServerResponse serverResponse, JsonArray result, int code) {
		serverResponse.putHeader("content-type", "application/json; charset=utf-8")
				.putHeader("Access-Control-Expose-Headers", "*").setStatusCode(code)
				.end(result.encodePrettily().toString());
	}

	public static void sendQueryResults(HttpServerResponse serverResponse, JsonObject result) {
		serverResponse.putHeader("content-type", "application/json; charset=utf-8")
				.putHeader("Access-Control-Expose-Headers", "*").setStatusCode(200)
				.end(result.encodePrettily().toString());
	}
}
