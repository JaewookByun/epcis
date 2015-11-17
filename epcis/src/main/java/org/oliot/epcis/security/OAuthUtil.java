package org.oliot.epcis.security;

import java.util.List;

import com.mongodb.DBObject;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.User;

public class OAuthUtil {
	public static boolean isValidated(String accessToken, String fid) {
		try {
			FacebookClient fc = new DefaultFacebookClient(accessToken, Version.VERSION_2_2);
			String id = fc.fetchObject("me", User.class).getId();
			if (!id.equals(fid)) {
				return false;
			}
			return true;
		} catch (FacebookOAuthException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static FacebookClient isValidatedFacebookClient(String accessToken, String fid) {
		try {
			FacebookClient fc = new DefaultFacebookClient(accessToken, Version.VERSION_2_4);
			String id = fc.fetchObject("me", User.class).getId();
			if (!id.equals(fid)) {
				return null;
			}
			return fc;
		} catch (FacebookOAuthException e) {
			e.printStackTrace();
			return null;
		}
	}

	// For MongoDB Document
	public static boolean isAccessible(String fid, List<String> friendList, DBObject doc) {

		String am = (String) doc.get("accessModifier");
		String providerID = (String) doc.get("fid");

		// Public Document
		if (providerID == null || am == null) {
			return true;
		}

		// Non-public document && No authorization
		if (fid == null) {
			return false;
		}

		// If Owner, accessible
		if (providerID.equals(fid)) {
			return true;
		}

		// If Not Owner
		if (am.equals("Friend")) {
			if (friendList.contains(providerID)) {
				return true;
			} else {
				return false;
			}
		}

		// If Not Owner
		if (am.equals("Follow")) {
			return false;
			// Facebook's subscribers are now deprecated, just return false;
			/*
			 * Connection<User> followerList =
			 * fc.fetchConnection("me/subscribers", User.class); for (List<User>
			 * followers : followerList) { for (User follower : followers) {
			 * String followerID = follower.getId(); if
			 * (ownerSet.contains(followerID)) { return true; } } }
			 */
		}

		return false;
	}
}
