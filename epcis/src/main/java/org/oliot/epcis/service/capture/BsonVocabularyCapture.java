package org.oliot.epcis.service.capture;

import javax.servlet.ServletContext;

import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;
import org.oliot.epcis.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

/**
 * Copyright (C) 2015 Jaewook Jack Byun
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
 * 
 * @author Sungpil Woo, Master student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         woosungpil@kaist.ac.kr, woosungpil7@gmail.com
 */

@Controller
@RequestMapping("/BsonVocabularyCapture")
public class BsonVocabularyCapture implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@SuppressWarnings("unused")
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public String post(@RequestBody byte[] inputByteArray) {
		Configuration.logger.info(" EPCIS Bson Document Capture Started.... ");

		BasicBSONDecoder bsonDecoder = new BasicBSONDecoder();
		BSONObject bsonObject = bsonDecoder.readObject(inputByteArray);
		
		//TODO: 검증 필요없이, bsonObject를 저장하는 로직을 완성 
		
		return "EPCIS Document : Captured ";

	}
}
