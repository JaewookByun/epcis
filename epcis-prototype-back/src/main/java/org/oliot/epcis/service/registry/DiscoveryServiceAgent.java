package org.oliot.epcis.service.registry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import net.openhft.koloboke.collect.set.hash.HashObjSets;

public class DiscoveryServiceAgent {
	public static Map<String, HashSet<String>> gtinMap = HashObjObjMaps.<String, HashSet<String>> newUpdatableMap();
	public static Set<String> epcSet = HashObjSets.<String> newUpdatableSet();

	/**
	 * 원활한 작업을 위해 한글로 주석 후보 EPC들을 받아 DS에 전송함
	 * 
	 * @param candidateSet
	 *            : 후보 EPC들
	 * @return DS에 업데이트된 EPC의 개수를 리턴
	 */
	public int registerEPC(HashSet<String> candidateSet) {
		// 후보 EPC들을 순회함
		Iterator<String> iter = candidateSet.iterator();
		int updatedEPCCount = 0;
		while (iter.hasNext()) {
			// 후보 EPC를 받아
			String candidate = iter.next();
			// 해당하는 GTIN을 얻는다
			String gtin = getGTIN(candidate);

			if (gtin == null) {
				// null의 의미는 잘못된 코드
				continue;
			}
			// GTIN을 GTIN 맵에서 검색해서 URL을 얻음
			HashSet<String> urlForGTIN = gtinMap.get(gtin);
			if (urlForGTIN == null) {
				// null의 의미는 GTIN에 대한 맵이 없기 때문에
				// ONS를 접속해서 DS주소를 얻어내야 한다는 의미
				HashSet<String> dsURLSet = getDiscoveryServiceURL(gtin);
				if (dsURLSet.isEmpty() == true) {
					// ONS도 주소를 모른다는 의미 이 EPC는 패스한다
					continue;
				} else {
					// 새로운 주소를 gtinMap에 업데이트
					gtinMap.put(gtin, dsURLSet);
					// DS에 등록
					Iterator<String> dsURLIter = dsURLSet.iterator();
					while (dsURLIter.hasNext()) {
						// ONS가 반환한 dsURL을 가지고
						String dsURL = dsURLIter.next();
						// DS에 등록한다
						boolean isRegistered = registerEPCToDS(candidate, dsURL);
						// 등록이 성공하면
						if (isRegistered == true) {
							// 새로운 EPC를 epcSet에도 넣어줌
							epcSet.add(candidate);
							updatedEPCCount++;
						}
					}
				}
			} else {
				// GTIN에 대한 URL은 존재함을 의미
				// EPC SET에 epc가 있음을 체크
				if (epcSet.contains(candidate)) {
					// 있다면, DS에 등록할 필요 없음
					continue;
				} else {
					// 없다면, DS에 등록
					Iterator<String> dsURLIter = urlForGTIN.iterator();
					while (dsURLIter.hasNext()) {
						// 이미 gtinMap에서 보유했던 dsURL을 가지고
						String dsURL = dsURLIter.next();
						// DS에 등록한다
						boolean isRegistered = registerEPCToDS(candidate, dsURL);
						// 등록이 성공하면
						if (isRegistered == true) {
							// 새로운 EPC를 epcSet에도 넣어줌
							epcSet.add(candidate);
							updatedEPCCount++;
						}
					}
				}
			}
		}
		return updatedEPCCount;
	}

	private String getGTIN(String EPC) {
		// TODO: SGTIN, LGTIN를 받아 GTIN으로 변환해 돌려주는 기능
		// TODO: 변환이 안되는 코드 (예: SSCC, GSRN, SGLN등)은 null을 리턴
		// TODO: 실은 이것은 이슈임. ONS를 통하기 때문에 GTIN만 처리할 수 있음
		return null;
	}

	private HashSet<String> getDiscoveryServiceURL(String gtin) {
		// TODO: gtin을 ONS에 물어봐서 해당하는 DS주소 해시셋에 넣어서 돌려준다
		// TODO: Configuration.onsAddress 는 시작시 설정가능한 ons 주소를 갖고 있으므로 이용바람
		HashSet<String> dsURLSet = new HashSet<String>();
		// TODO: Some Logic
		return dsURLSet;
	}

	private boolean registerEPCToDS(String epc, String dsURL) {
		// TODO: dsURL에 epc를 등록하는 메소드, 결과로 성공 여부를 반환한다
		return false;
	}
}
