package org.oliot.epcis.query;

import javax.xml.transform.dom.DOMSource;

import org.oliot.epcis.model.ArrayOfString;
import org.oliot.epcis.model.Poll;
import org.oliot.epcis.model.Subscribe;
import org.oliot.epcis.model.VoidHolder;
import org.w3c.dom.Node;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * The class unmarshalls XML-formatted query into POJO.
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class SOAPQueryUnmarshaller {

	private Unmarshaller pollUnmarshaller;
	private Unmarshaller subscribeUnmarshaller;
	private Unmarshaller arrayOfStringUnmarshaller;
	private Unmarshaller voidHolderUnmarshaller;

	public SOAPQueryUnmarshaller() {
		try {
			pollUnmarshaller = JAXBContext.newInstance(Poll.class).createUnmarshaller();
			subscribeUnmarshaller = JAXBContext.newInstance(Subscribe.class).createUnmarshaller();
			arrayOfStringUnmarshaller = JAXBContext.newInstance(ArrayOfString.class).createUnmarshaller();
			voidHolderUnmarshaller = JAXBContext.newInstance(VoidHolder.class).createUnmarshaller();
		} catch (JAXBException e) {
			// not happen
			e.printStackTrace();
		}
	}

	public Poll getPoll(Node pollNode) throws JAXBException {
		return pollUnmarshaller.unmarshal(new DOMSource(pollNode), Poll.class).getValue();
	}

	public Subscribe getSubscription(Node subscribeNode) throws JAXBException {
		return subscribeUnmarshaller.unmarshal(new DOMSource(subscribeNode), Subscribe.class).getValue();
	}

	public ArrayOfString getArrayOfString(Node arrayOfStringNode) throws JAXBException {
		return arrayOfStringUnmarshaller.unmarshal(new DOMSource(arrayOfStringNode), ArrayOfString.class).getValue();
	}

	public VoidHolder getVoidHolder(Node arrayOfStringNode) throws JAXBException {
		return voidHolderUnmarshaller.unmarshal(new DOMSource(arrayOfStringNode), VoidHolder.class).getValue();
	}
}
