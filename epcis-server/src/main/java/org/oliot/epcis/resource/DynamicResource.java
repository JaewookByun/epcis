package org.oliot.epcis.resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.Document;
import org.oliot.epcis.server.EPCISServer;

import com.mongodb.client.MongoCursor;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonObject;

public class DynamicResource extends Thread {
	public static AtomicLong numOfEvents = new AtomicLong(0);
	public static AtomicLong numOfVocabularies = new AtomicLong(0);

	public static ConcurrentHashSet<String> availableEPCsInEvents = new ConcurrentHashSet<String>();
	public static ConcurrentHashSet<String> availableEPCsInVocabularies = new ConcurrentHashSet<String>();
	public static ConcurrentHashSet<String> availableBusinessSteps = new ConcurrentHashSet<String>();
	public static ConcurrentHashSet<String> availableBusinessLocationsInEvents = new ConcurrentHashSet<String>();
	public static ConcurrentHashSet<String> availableBusinessLocationsInVocabularies = new ConcurrentHashSet<String>();
	public static ConcurrentHashSet<String> availableReadPointsInEvents = new ConcurrentHashSet<String>();
	public static ConcurrentHashSet<String> availableReadPointsInVocabularies = new ConcurrentHashSet<String>();
	public static ConcurrentHashSet<String> availableDispositions = new ConcurrentHashSet<String>();
	public static ConcurrentHashSet<String> availableEventTypes = new ConcurrentHashSet<String>();
	
	public static AtomicLong numOfCaptureJobs = new AtomicLong(0);
	public static AtomicLong numOfSubscriptions = new AtomicLong(0);
			
	private Vertx vertx;
	public static JsonObject counts = new JsonObject();
	public static AtomicLong delay;

	public DynamicResource(Vertx vertx) {
		this.vertx = vertx;

	}

	public static JsonObject getCounts() {
		JsonObject newCounts = new JsonObject();

		newCounts.put("events", numOfEvents.get());
		newCounts.put("vocabularies", numOfVocabularies.get());
		newCounts.put("eventTypes", availableEventTypes.size());
		newCounts.put("epcs_in_events", availableEPCsInEvents.size());
		newCounts.put("epcs_in_vocabularies", availableEPCsInVocabularies.size());
		newCounts.put("dispositions", availableDispositions.size());
		newCounts.put("bizSteps", availableBusinessSteps.size());
		newCounts.put("bizLocations_in_events", availableBusinessLocationsInEvents.size());
		newCounts.put("bizLocations_in_vocabularies", availableBusinessLocationsInVocabularies.size());
		newCounts.put("readPoints_in_events", availableReadPointsInEvents.size());
		newCounts.put("readPoints_in_vocabularies", availableReadPointsInVocabularies.size());
		newCounts.put("captureJobs", numOfCaptureJobs.get());
		newCounts.put("subscriptions", numOfSubscriptions.get());

		synchronized (counts) {
			counts = newCounts;
		}

		return counts;
	}
	

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(delay.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			vertx.setTimer(1, new Handler<Long>() {
				@Override
				public void handle(Long event) {
					long pre = System.currentTimeMillis();

					numOfEvents.set(EPCISServer.monitoringEventCollection.countDocuments());
					numOfVocabularies.set(EPCISServer.monitoringVocCollection.countDocuments());

					Document eventTypeResult = EPCISServer.monitoringEventCollection
							.aggregate(List.of(new Document().append("$group", new Document().append("_id", null)
									.append("eventTypes", new Document().append("$addToSet", "$type")))))
							.first();

					HashSet<String> newEventTypes = new HashSet<String>();
					if (eventTypeResult != null) {
						for (String v : eventTypeResult.getList("eventTypes", String.class)) {
							newEventTypes.add(v);
						}
					}
					availableEventTypes.clear();
					availableEventTypes.addAll(newEventTypes);

					Document bizStepResult = EPCISServer.monitoringEventCollection
							.aggregate(List.of(new Document().append("$group", new Document().append("_id", null)
									.append("bizSteps", new Document().append("$addToSet", "$bizStep")))))
							.first();
					HashSet<String> newBizStep = new HashSet<String>();
					if (bizStepResult != null) {
						for (String v : bizStepResult.getList("bizSteps", String.class)) {
							newBizStep.add(v);
						}
					}
					availableBusinessSteps.clear();
					availableBusinessSteps.addAll(newBizStep);

					Document dispositionResult = EPCISServer.monitoringEventCollection
							.aggregate(
									List.of(new Document()
											.append("$group",
													new Document().append("_id", null).append("dispositions",
															new Document().append("$addToSet", "$disposition")))))
							.first();

					HashSet<String> newDisposition = new HashSet<String>();

					if (dispositionResult != null) {
						for (String v : dispositionResult.getList("dispositions", String.class)) {
							newDisposition.add(v);
						}
					}

					availableDispositions.clear();
					availableDispositions.addAll(newDisposition);

					MongoCursor<Document> readPointCursor = EPCISServer.monitoringEventCollection.aggregate(List.of(
							new Document().append("$group",
									new Document().append("_id", null).append("readPoints",
											new Document().append("$addToSet", "$readPoint"))),
							new Document().append("$unwind", new Document().append("path", "$readPoints")
									.append("preserveNullAndEmptyArrays", false))))
							.iterator();

					HashSet<String> newReadPoint = new HashSet<String>();

					while (readPointCursor.hasNext()) {
						newReadPoint.add(readPointCursor.next().getString("readPoints"));
					}
					availableReadPointsInEvents.clear();
					availableReadPointsInEvents.addAll(newReadPoint);

					MongoCursor<Document> vReadPointCursor = EPCISServer.monitoringVocCollection
							.find(new Document("type", "urn:epcglobal:epcis:vtype:ReadPoint"))
							.projection(new Document("id", true).append("_id", false)).iterator();
					HashSet<String> newVReadPoints = new HashSet<String>();
					while (vReadPointCursor.hasNext()) {
						newVReadPoints.add(vReadPointCursor.next().getString("id"));
					}
					availableReadPointsInVocabularies.clear();
					availableReadPointsInVocabularies.addAll(newVReadPoints);
					
					MongoCursor<Document> bizLocationCursor = EPCISServer.monitoringEventCollection.aggregate(List.of(
							new Document().append("$group",
									new Document().append("_id", null).append("bizLocations",
											new Document().append("$addToSet", "$bizLocation"))),
							new Document().append("$unwind", new Document().append("path", "$bizLocations")
									.append("preserveNullAndEmptyArrays", false))))
							.iterator();

					HashSet<String> newBizLocation = new HashSet<String>();

					while (bizLocationCursor.hasNext()) {
						newBizLocation.add(bizLocationCursor.next().getString("bizLocations"));
					}

					availableBusinessLocationsInEvents.clear();
					availableBusinessLocationsInEvents.addAll(newBizLocation);

					MongoCursor<Document> vBizLocationCursor = EPCISServer.monitoringVocCollection
							.find(new Document("type", "urn:epcglobal:epcis:vtype:BusinessLocation"))
							.projection(new Document("id", true).append("_id", false)).iterator();
					HashSet<String> newVBizLocations = new HashSet<String>();
					while (vBizLocationCursor.hasNext()) {
						newVBizLocations.add(vBizLocationCursor.next().getString("id"));
					}
					availableBusinessLocationsInVocabularies.clear();
					availableBusinessLocationsInVocabularies.addAll(newVBizLocations);

					Document epcGroup = new Document().append("$group", new Document().append("_id", null)
							.append("parentIDs", new Document().append("$addToSet", "$parentID"))
							.append("epcList", new Document().append("$addToSet", "$epcList"))
							.append("inputEPCList", new Document().append("$addToSet", "$inputEPCList"))
							.append("outputEPCList", new Document().append("$addToSet", "$outputEPCList"))
							.append("quantityList", new Document().append("$addToSet", "$quantityList.epcClass"))
							.append("inputQuantityList",
									new Document().append("$addToSet", "$inputQuantityList.epcClass"))
							.append("outputQuantityList",
									new Document().append("$addToSet", "$outputQuantityList.epcClass")));

					Document epcAddFields = new Document().append("$addFields",
							new Document()
									.append("epcList",
											new Document("$reduce", new Document().append("input", "$epcList")
													.append("initialValue", new ArrayList<String>()).append("in",
															new Document().append("$concatArrays",
																	List.of("$$value", "$$this")))))

									.append("inputEPCList",
											new Document("$reduce", new Document().append("input", "$inputEPCList")
													.append("initialValue", new ArrayList<String>()).append("in",
															new Document().append("$concatArrays",
																	List.of("$$value", "$$this")))))
									.append("outputEPCList",
											new Document("$reduce", new Document().append("input", "$outputEPCList")
													.append("initialValue", new ArrayList<String>()).append("in",
															new Document().append("$concatArrays",
																	List.of("$$value", "$$this")))))
									.append("quantityList",
											new Document("$reduce", new Document().append("input", "$quantityList")
													.append("initialValue", new ArrayList<String>()).append("in",
															new Document().append("$concatArrays",
																	List.of("$$value", "$$this")))))
									.append("inputQuantityList",
											new Document("$reduce", new Document().append("input", "$inputQuantityList")
													.append("initialValue", new ArrayList<String>()).append("in",
															new Document().append("$concatArrays",
																	List.of("$$value", "$$this")))))
									.append("outputQuantityList",
											new Document("$reduce",
													new Document().append("input", "$outputQuantityList")
															.append("initialValue", new ArrayList<String>())
															.append("in", new Document().append("$concatArrays",
																	List.of("$$value", "$$this"))))));

					Document epcProject = new Document().append("$project",
							new Document().append("epcs",
									new Document().append("$concatArrays",
											List.of("$parentIDs", "$epcList", "$inputEPCList", "$outputEPCList",
													"$quantityList", "$inputQuantityList", "$outputQuantityList"))));

					Document epcUnwind = new Document().append("$unwind",
							new Document().append("path", "$epcs").append("preserveNullAndEmptyArrays", false));

					MongoCursor<Document> epcCursor = EPCISServer.monitoringEventCollection
							.aggregate(List.of(epcGroup, epcAddFields, epcProject, epcUnwind)).iterator();
					HashSet<String> newEPCs = new HashSet<String>();
					while (epcCursor.hasNext()) {
						newEPCs.add(epcCursor.next().getString("epcs"));
					}
					availableEPCsInEvents.clear();
					availableEPCsInEvents.addAll(newEPCs);

					MongoCursor<Document> vEPCCursor = EPCISServer.monitoringVocCollection
							.find(new Document("type", "urn:epcglobal:epcis:vtype:EPCClass"))
							.projection(new Document("id", true).append("_id", false)).iterator();
					HashSet<String> newVocEPCs = new HashSet<String>();
					while (vEPCCursor.hasNext()) {
						newVocEPCs.add(vEPCCursor.next().getString("id"));
					}
					availableEPCsInVocabularies.clear();
					availableEPCsInVocabularies.addAll(newVocEPCs);

					numOfCaptureJobs.set(EPCISServer.monitoringTxCollection.countDocuments());
					numOfSubscriptions.set(EPCISServer.monitoringSubscriptionCollection.countDocuments());
					
					EPCISServer.logger.debug("# currently found resources: " + getCounts());

					long elapsed = System.currentTimeMillis() - pre;
					if (elapsed < delay.get() / 10) {
						delay.set(EPCISServer.resourceDiscoveryInterval.get());
					} else {
						delay.set(elapsed * 10);
					}
				}
			});
		}
	}
}
