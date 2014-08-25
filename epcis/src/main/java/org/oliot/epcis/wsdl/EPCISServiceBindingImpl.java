/**
 * EPCISServiceBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.wsdl;

import java.rmi.RemoteException;

import org.oliot.epcis.query.DuplicateSubscriptionException;
import org.oliot.epcis.query.EmptyParms;
import org.oliot.epcis.query.GetSubscriptionIDs;
import org.oliot.epcis.query.ImplementationException;
import org.oliot.epcis.query.InvalidURIException;
import org.oliot.epcis.query.NoSuchNameException;
import org.oliot.epcis.query.NoSuchSubscriptionException;
import org.oliot.epcis.query.Poll;
import org.oliot.epcis.query.QueryParameterException;
import org.oliot.epcis.query.QueryResults;
import org.oliot.epcis.query.QueryTooComplexException;
import org.oliot.epcis.query.QueryTooLargeException;
import org.oliot.epcis.query.Subscribe;
import org.oliot.epcis.query.SubscribeNotPermittedException;
import org.oliot.epcis.query.SubscriptionControlsException;
import org.oliot.epcis.query.Unsubscribe;
import org.oliot.epcis.query.ValidationException;
import org.oliot.epcis.query.VoidHolder;

public class EPCISServiceBindingImpl implements EPCISServicePortType{
	public String[] getQueryNames(EmptyParms parms) throws RemoteException, ImplementationException, org.oliot.epcis.query.SecurityException, ValidationException {

		return null;
	}

	public VoidHolder subscribe(Subscribe parms) throws RemoteException, QueryTooComplexException, SubscribeNotPermittedException, QueryParameterException, NoSuchNameException, DuplicateSubscriptionException, InvalidURIException, ImplementationException, SecurityException, SubscriptionControlsException, ValidationException {
		return null;
	}

	public VoidHolder unsubscribe(Unsubscribe parms) throws RemoteException, NoSuchSubscriptionException, ImplementationException, SecurityException, ValidationException {
		return null;
	}

	public String[] getSubscriptionIDs( GetSubscriptionIDs parms) throws RemoteException, NoSuchNameException, ImplementationException, SecurityException, ValidationException {
		return null;
	}

	public QueryResults poll(Poll parms) throws RemoteException, QueryTooComplexException, QueryTooLargeException, NoSuchNameException, QueryParameterException, ImplementationException, SecurityException, ValidationException {
		
		return null;
	}

	public String getStandardVersion(EmptyParms parms) throws RemoteException, ImplementationException, SecurityException, ValidationException {
		// This is the implementation of EPCIS v1.1
		return "1.1";
	}

	public String getVendorVersion(EmptyParms parms) throws RemoteException, ImplementationException, SecurityException, ValidationException {
		// This is standard Version, thus returning null according to the EPCIS v1.1
		return null;
	}

}
