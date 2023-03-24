package org.oliot.epcis.converter.xml.write;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.Document;
import org.oliot.epcis.model.VocabularyType;
import org.oliot.epcis.model.exception.ValidationException;
import org.oliot.epcis.util.CBVAttributeUtil;

import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;

import static org.oliot.epcis.util.BSONWriteUtil.*;

/**
 * Copyright (C) 2020-2022. (Jaewook Byun) all rights reserved.
 * <p>
 * Oliot EPCIS X is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * Among various modules, epcis-capture-xml acts as a server to receive
 * XML-formatted EPCIS documents to capture events in the documents into an
 * EPCIS repository.
 * <p>
 * 
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr
 *         <p>
 *         Associate Director, Auto-ID Labs, KAIST, bjw0829@kaist.ac.kr
 */
public class XMLMasterDataWriteConverter {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Stream<WriteModel<Document>> convert(VocabularyType vocabulary) throws RuntimeException {

		final String type = vocabulary.getType();

		List<WriteModel<Document>> list = vocabulary.getVocabularyElementList().getVocabularyElement().parallelStream()
				.map(ve -> {
					Document find = new Document();
					String id = ve.getId();
					find.put("id", id);
					find.put("type", type);
					Document update = new Document();
					Document attrObj = new Document();
					ve.getAttribute().parallelStream().forEach(a -> {

						try {
							CBVAttributeUtil.checkCBVCompliantAttribute(a.getId(), a.getOtherAttributes(),
									a.getContent(), attrObj);
						} catch (ValidationException e) {
							throw new RuntimeException(e.getCause());
						}

						String attrKey = "attributes." + encodeMongoObjectKey(a.getId());
						if (a.getContent().size() == 1 && a.getContent().get(0) instanceof String) {
							synchronized (attrObj) {
								if (!attrObj.containsKey(attrKey)) {
									// first occurrence
									attrObj.put(attrKey, a.getContent().get(0).toString().trim());
								} else if (attrObj.containsKey(attrKey) && !(attrObj.get(attrKey) instanceof List)) {
									// second occurrence
									Object firstObj = attrObj.remove(attrKey);
									ArrayList arr = new ArrayList();
									arr.add(firstObj);
									arr.add(a.getContent().get(0).toString().trim());
									attrObj.put(attrKey, arr);
								} else {
									// third or more occurrence
									List arr = attrObj.get(attrKey, List.class);
									arr.add(a.getContent().get(0).toString().trim());
									attrObj.put(attrKey, arr);
								}
							}
						} else {
							synchronized (attrObj) {

								Document any = null;
								try {
									any = putAny(new Document(), "extension", a.getContent(), true);
								} catch (ValidationException e) {
									throw new RuntimeException(e.getCause());
								}

								if (!attrObj.containsKey(attrKey)) {
									// first occurrence
									attrObj.put(attrKey, any);
								} else if (attrObj.containsKey(attrKey) && !(attrObj.get(attrKey) instanceof List)) {
									// second occurrence
									Object firstObj = attrObj.remove(attrKey);
									ArrayList arr = new ArrayList();
									arr.add(firstObj);
									arr.add(any);
									attrObj.put(attrKey, arr);
								} else {
									// third or more occurrence
									List arr = attrObj.get(attrKey, List.class);
									arr.add(any);
									attrObj.put(attrKey, arr);
								}
							}
						}
					});
					attrObj.put("attributes.lastUpdate", System.currentTimeMillis());
					update.put("$set", attrObj);

					// If children found, overwrite previous one(s)
					try {
						ArrayList childArray = new ArrayList();
						if (ve.getChildren() != null) {
							ve.getChildren().getId().parallelStream().forEach(c -> {
								synchronized (childArray) {
									childArray.add(c);
								}
							});
						}
						update.append("$addToSet",
								new Document().append("children", new Document().append("$each", childArray)));
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					return new UpdateOneModel<Document>(find, update, new UpdateOptions().upsert(true));
				}).collect(Collectors.toList());

		return list.parallelStream();

	}
}
