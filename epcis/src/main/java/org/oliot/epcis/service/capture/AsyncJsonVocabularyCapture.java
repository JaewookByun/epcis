package org.oliot.epcis.service.capture;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AsyncVocabularyCapture
 */
@WebServlet("/JsonVocabularyCapture")
public class AsyncJsonVocabularyCapture extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AsyncJsonVocabularyCapture() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("GET /JsonVocabularyCapture Not Supported");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final ServletInputStream input = request.getInputStream();
		request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
		AsyncContext ac = request.startAsync();
		input.setReadListener(new JsonVocabularyCaptureReadListener(input, response, ac));
	}

}
