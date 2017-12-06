package org.oliot.epcis.converter.mongodb.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;

/**
 * Copyright (C) 2014-17 Jaewook Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class EPCISClient {
	private URL captureURL;
	private BsonDocument epcisDocument;

	public EPCISClient(URL captureURL) {
		this.captureURL = captureURL;
		this.epcisDocument = new BsonDocument();
		this.epcisDocument.put("EventData", new BsonArray());
		this.epcisDocument.put("MasterData", new BsonArray());
	}

	public void addAggregationEvent(AggregationEvent event) {
		epcisDocument.getArray("EventData")
				.add(event.asBsonDocument().append("recordTime", new BsonDateTime(System.currentTimeMillis())));
	}

	public void addObjectEvent(ObjectEvent event) {
		epcisDocument.getArray("EventData")
				.add(event.asBsonDocument().append("recordTime", new BsonDateTime(System.currentTimeMillis())));
	}

	public void addTransactionEvent(TransactionEvent event) {
		epcisDocument.getArray("EventData")
				.add(event.asBsonDocument().append("recordTime", new BsonDateTime(System.currentTimeMillis())));
	}

	public void addTransformationEvent(TransformationEvent event) {
		epcisDocument.getArray("EventData")
				.add(event.asBsonDocument().append("recordTime", new BsonDateTime(System.currentTimeMillis())));
	}

	public void addMasterData(MasterData masterData) {
		epcisDocument.getArray("MasterData").add(masterData.asBsonDocument());
	}

	public void sendDocument() {
		sendPost(this.captureURL, epcisDocument);
		this.epcisDocument.put("EventData", new BsonArray());
		this.epcisDocument.put("MasterData", new BsonArray());
	}

	/**
	 * 
	 * @param address
	 * @param port
	 * @param remainingURL
	 *            start with /
	 * @param message
	 * @throws IOException
	 */
	private void sendPost(URL captureURL, Object obj) {
		HttpProcessor httpproc = HttpProcessorBuilder.create().add(new RequestContent()).add(new RequestTargetHost())
				.add(new RequestConnControl()).add(new RequestUserAgent("Test/1.1"))
				.add(new RequestExpectContinue(true)).build();

		HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

		HttpCoreContext coreContext = HttpCoreContext.create();
		HttpHost host = new HttpHost(captureURL.getHost(), captureURL.getPort());
		coreContext.setTargetHost(host);

		DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024);
		ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			HttpEntity[] requestBodies = {
					new InputStreamEntity(new ByteArrayInputStream(baos.toByteArray()), ContentType.TEXT_PLAIN) };

			for (int i = 0; i < requestBodies.length; i++) {
				if (!conn.isOpen()) {
					Socket socket = new Socket(host.getHostName(), host.getPort());
					conn.bind(socket);
				}
				BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST",
						captureURL.getPath());
				request.setEntity(requestBodies[i]);
				System.out.println(">> Request URI: " + request.getRequestLine().getUri());

				httpexecutor.preProcess(request, httpproc, coreContext);
				HttpResponse response = httpexecutor.execute(request, conn, coreContext);
				httpexecutor.postProcess(response, httpproc, coreContext);

				// System.out.println("<< Response: " +
				// response.getStatusLine());
				// System.out.println(EntityUtils.toString(response.getEntity()));
				// System.out.println("==============");
				if (!connStrategy.keepAlive(response, coreContext)) {
					conn.close();
				} else {
					// System.out.println("Connection kept alive...");
				}
			}
		} catch (IOException e) {

		} catch (HttpException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
