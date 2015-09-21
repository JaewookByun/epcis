package org.oliot.epcis.service.registry;

import java.util.Map;
import java.util.Set;

import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import net.openhft.koloboke.collect.set.hash.HashObjSets;

public class DiscoveryServiceAgent {
	public static Map<String, String> gtinMap = HashObjObjMaps.<String,String>newUpdatableMap();
	public static Set<String> sgtinSet = HashObjSets.<String>newUpdatableSet();
}
