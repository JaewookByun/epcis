package org.oliot.epcis.service.capture;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletResponse;

class VocabularyCaptureReadListener implements ReadListener {
	private ServletInputStream input = null;
	private HttpServletResponse res = null;
	private AsyncContext ac = null;
	// store the processed data to be sent back to client later
	private Queue<String> queue = new LinkedBlockingQueue<String>();

	VocabularyCaptureReadListener(ServletInputStream in, HttpServletResponse r, AsyncContext c) {
		input = in;
		res = r;
		ac = c;
	}

	public void onDataAvailable() throws IOException {
		StringBuilder sb = new StringBuilder();
		int len = -1;
		byte b[] = new byte[1024];
		// We need to check input#isReady before reading data.
		// The ReadListener will be invoked again when
		// the input#isReady is changed from false to true
		while (input.isReady() && (len = input.read(b)) != -1) {
			String data = new String(b, 0, len);
			sb.append(data);
		}
		queue.add(sb.toString());
	}

	public void onAllDataRead() throws IOException {
		res.setStatus(200);
		ac.complete();
		VocabularyCapture vc = new VocabularyCapture();
		String inputString = queue.toString();
		if (inputString.length() >= 2) {
			inputString = inputString.substring(1, inputString.length() - 1);
		}
		vc.asyncPost(inputString);
	}

	public void onError(final Throwable t) {
		res.setStatus(404);
		ac.complete();
		t.printStackTrace();
	}
}
