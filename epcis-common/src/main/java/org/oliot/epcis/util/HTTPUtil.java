package org.oliot.epcis.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class HTTPUtil {
	public static HttpResponse<String> post(URI uri, String body) throws IOException, InterruptedException {
		return HttpClient.newBuilder().build().send(HttpRequest.newBuilder().header("Content-Type", "application/xml")
				.uri(uri).POST(BodyPublishers.ofString(body)).build(), BodyHandlers.ofString());
	}

	@SuppressWarnings("rawtypes")
	public static void sendQueryResults(HttpServerResponse serverResponse, Object result, Document retDoc,
			Element envelope, Element body, Class resultType, int statusCode) {
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
			serverResponse.putHeader("content-type", "application/xml; charset=utf-8").setStatusCode(statusCode)
					.end(XMLUtil.toString(retDoc));
		} catch (JAXBException | ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
	}

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
		} catch (JAXBException | ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
	}

	public static void sendQueryResults(HttpServerResponse serverResponse, JsonArray result, int code) {
		serverResponse.putHeader("content-type", "application/json; charset=utf-8").setStatusCode(code)
				.end(result.encodePrettily().toString());
	}

	public static void sendQueryResults(HttpServerResponse serverResponse, JsonObject result) {
		serverResponse.putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
				.end(result.encodePrettily().toString());
	}
}
