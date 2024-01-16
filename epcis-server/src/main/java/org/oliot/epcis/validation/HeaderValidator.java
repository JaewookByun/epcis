package org.oliot.epcis.validation;

import java.util.Arrays;

import org.oliot.epcis.capture.json.JSONMessageFactory;
import org.oliot.epcis.common.Metadata;
import org.oliot.epcis.common.Version;
import org.oliot.epcis.model.EPCISException;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.HTTPUtil;
import org.oliot.epcis.util.SOAPMessage;

import io.vertx.ext.web.RoutingContext;

/**
 * Copyright (C) 2020-2024. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class HeaderValidator {

	public static boolean checkCBVMinMaxVersion(RoutingContext routingContext) {
		String min = routingContext.request().getHeader("GS1-CBV-Min");
		String max = routingContext.request().getHeader("GS1-CBV-Max");

		if (min != null && !Version.isValid(min)) {
			EPCISException e = new EPCISException(
					"[406NotAcceptable] The server receives an invalid GS1-CBV-Min. Provide one of "
							+ Arrays.stream(Version.values()).toList() + ".");
			HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 406);
			return false;
		}
		if (max != null && !Version.isValid(max)) {
			EPCISException e = new EPCISException(
					"[406NotAcceptable] The server receives an invalid GS1-CBV-Max. Provide one of "
							+ Arrays.stream(Version.values()).toList() + ".");
			HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 406);
			return false;
		}
		if (!Version.isCompatible(Metadata.GS1_CBV_Version, min, max)) {
			EPCISException e = new EPCISException(
					"[406NotAcceptable] The server receives an incompatible GS1-CBV-Min | GS1-CBV-Max");
			HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 406);
			return false;
		}
		return true;
	}

	public static boolean checkEPCISMinMaxVersion(RoutingContext routingContext) {
		String min = routingContext.request().getHeader("GS1-EPCIS-Min");
		String max = routingContext.request().getHeader("GS1-EPCIS-Max");

		if (min != null && !Version.isValid(min)) {
			EPCISException e = new EPCISException(
					"[406NotAcceptable] The server receives an invalid GS1-EPCIS-Min. Provide one of "
							+ Arrays.stream(Version.values()).toList() + ".");
			HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 406);
			return false;
		}
		if (max != null && !Version.isValid(max)) {
			EPCISException e = new EPCISException(
					"[406NotAcceptable] The server receives an invalid GS1-EPCIS-Max. Provide one of "
							+ Arrays.stream(Version.values()).toList() + ".");
			HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 406);
			return false;
		}

		if (min == null || max == null) {
			EPCISException e = new EPCISException(
					"[406NotAcceptable] The server did not received GS1-EPCIS-Min and GS1-EPCIS where its value is one of "
							+ Arrays.stream(Version.values()).toList() + ".");
			HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 406);
			return false;
		}
		if (!Version.isCompatible(Metadata.GS1_EPCIS_Version, min, max)) {
			EPCISException e = new EPCISException(
					"[406NotAcceptable] The server receives an incompatible GS1-EPCIS-Min | GS1-EPCIS/Max");
			HTTPUtil.sendQueryResults(routingContext.response(), new SOAPMessage(), e, e.getClass(), 406);
			return false;
		}
		return true;
	}

	public static boolean isEqualHeaderSOAP(RoutingContext routingContext, String givenHeaderKey, boolean isMandatory) {
		// check header
		String given = routingContext.request().getHeader(givenHeaderKey);
		if (given == null) {
			if (isMandatory) {
				EPCISException e = new EPCISException(
						"[406NotAcceptable] The server cannot return the response as requested. " + givenHeaderKey
								+ " does not provided.");
				EPCISServer.logger.error(e.getReason());
				HTTPUtil.sendQueryResults(routingContext.response(), e, e.getClass(), 406);
				return false;
			} else {
				return true;
			}
		}

		if ((givenHeaderKey.equals("GS1-EPCIS-Version") || givenHeaderKey.equals("GS1-EPCIS-Min")
				|| givenHeaderKey.equals("GS1-EPCIS-Max")) && !given.equals(Metadata.GS1_EPCIS_Version)) {
			if (!routingContext.response().closed()) {
				EPCISException e = new EPCISException(
						"[406NotAcceptable] The server cannot return the response as requested. \n Conflicting request and response headers."
								+ given + " != " + Metadata.GS1_EPCIS_Version);
				EPCISServer.logger.error(e.getReason());
				HTTPUtil.sendQueryResults(routingContext.response(), e, e.getClass(), 406);
			}
			return false;
		} else if ((givenHeaderKey.equals("GS1-CBV-Version") || givenHeaderKey.equals("GS1-CBV-Min")
				|| givenHeaderKey.equals("GS1-CBV-Max")) && !given.equals(Metadata.GS1_CBV_Version)) {
			if (!routingContext.response().closed()) {
				EPCISException e = new EPCISException(
						"[406NotAcceptable] The server cannot return the response as requested. \n Conflicting request and response headers."
								+ given + " != " + Metadata.GS1_CBV_Version);
				EPCISServer.logger.error(e.getReason());
				HTTPUtil.sendQueryResults(routingContext.response(), e, e.getClass(), 406);
			}
			return false;
		} else if (givenHeaderKey.equals("GS1-EPCIS-Capture-Error-Behaviour")
				&& !given.equals(Metadata.GS1_EPCIS_Capture_Error_Behaviour)) {
			if (!routingContext.response().closed()) {
				EPCISException e = new EPCISException(
						"[406NotAcceptable] The server cannot return the response as requested. \n Conflicting request and response headers."
								+ given + " != " + Metadata.GS1_EPCIS_Capture_Error_Behaviour);
				EPCISServer.logger.error(e.getReason());
				HTTPUtil.sendQueryResults(routingContext.response(), e, e.getClass(), 406);
			}
			return false;
		}
		return true;
	}

	public static boolean isEqualHeaderREST(RoutingContext routingContext, String givenHeaderKey, boolean isMandatory) {
		// check header
		String given = routingContext.request().getHeader(givenHeaderKey);
		if (given == null) {
			if (isMandatory) {
				HTTPUtil.sendQueryResults(routingContext.response(),
						JSONMessageFactory.get406NotAcceptableException(
								"[406NotAcceptable] The server cannot return the response as requested. "
										+ givenHeaderKey + " does not provided."),
						406);
				return false;
			} else {
				return true;
			}
		}

		if ((givenHeaderKey.equals("GS1-EPCIS-Version") || givenHeaderKey.equals("GS1-EPCIS-Min")
				|| givenHeaderKey.equals("GS1-EPCIS-Max")) && !given.equals(Metadata.GS1_EPCIS_Version)) {
			if (!routingContext.response().closed()) {
				HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory.get406NotAcceptableException(
						"[406NotAcceptable] The server cannot return the response as requested. \n Conflicting request and response headers."
								+ given + " != " + Metadata.GS1_EPCIS_Version),
						406);
			}
			return false;
		} else if ((givenHeaderKey.equals("GS1-CBV-Version") || givenHeaderKey.equals("GS1-CBV-Min")
				|| givenHeaderKey.equals("GS1-CBV-Max")) && !given.equals(Metadata.GS1_CBV_Version)) {
			if (!routingContext.response().closed()) {
				HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory.get406NotAcceptableException(
						"[406NotAcceptable] The server cannot return the response as requested. \n Conflicting request and response headers."
								+ given + " != " + Metadata.GS1_CBV_Version),
						406);
			}
			return false;
		} else if (givenHeaderKey.equals("GS1-EPCIS-Capture-Error-Behaviour")
				&& !given.equals(Metadata.GS1_EPCIS_Capture_Error_Behaviour)) {
			if (!routingContext.response().closed()) {
				HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory.get406NotAcceptableException(
						"[406NotAcceptable] The server cannot return the response as requested. \n Conflicting request and response headers."
								+ given + " != " + Metadata.GS1_EPCIS_Capture_Error_Behaviour),
						406);
			}
			return false;
		} else if (givenHeaderKey.equals("GS1-EPC-Format") && !given.equals(Metadata.GS1_EPC_Format_REST)) {
			if (!routingContext.response().closed()) {
				HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory.get406NotAcceptableException(
						"[406NotAcceptable] The server cannot return the response as requested. \n Conflicting request and response headers."
								+ given + " != " + Metadata.GS1_EPC_Format_REST),
						406);
			}
			return false;
		} else if (givenHeaderKey.equals("GS1-CBV-XML-Format") && !given.equals(Metadata.GS1_CBV_XML_Format_REST)) {
			if (!routingContext.response().closed()) {
				HTTPUtil.sendQueryResults(routingContext.response(), JSONMessageFactory.get406NotAcceptableException(
						"[406NotAcceptable] The server cannot return the response as requested. \n Conflicting request and response headers."
								+ given + " != " + Metadata.GS1_CBV_XML_Format_REST),
						406);
			}
			return false;
		}
		return true;
	}
}
