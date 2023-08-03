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

	public static ConcurrentHashSet<String> availableEPCs = new ConcurrentHashSet<String>();
	public static ConcurrentHashSet<String> availableBusinessSteps = new ConcurrentHashSet<String>();
	public static ConcurrentHashSet<String> availableBusinessLocations = new ConcurrentHashSet<String>();
	public static ConcurrentHashSet<String> availableReadPoints = new ConcurrentHashSet<String>();
	public static ConcurrentHashSet<String> availableDispositions = new ConcurrentHashSet<String>();
	public static ConcurrentHashSet<String> availableEventTypes = new ConcurrentHashSet<String>();

	private Vertx vertx;
	public static AtomicLong delay = new AtomicLong(5000l);
	public static JsonObject counts = new JsonObject();

	public DynamicResource(Vertx vertx) {
		this.vertx = vertx;

	}

	public static JsonObject getCounts() {
		synchronized (counts) {
			counts.put("numOfEvents", numOfEvents.get());
			counts.put("numOfVocabularies", numOfVocabularies.get());
			counts.put("epcs", availableEPCs.size());
			counts.put("bizSteps", availableBusinessSteps.size());
			counts.put("bizLocations", availableBusinessLocations.size());
			counts.put("readPoints", availableReadPoints.size());
			counts.put("dispositions", availableDispositions.size());
			counts.put("eventTypes", availableEventTypes.size());
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
					availableBusinessSteps.addAll(newEventTypes);

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
					availableReadPoints.clear();
					availableReadPoints.addAll(newReadPoint);

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

					availableBusinessLocations.clear();
					availableBusinessLocations.addAll(newBizLocation);

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
					availableEPCs.clear();
					availableEPCs.addAll(newEPCs);

					EPCISServer.logger.debug("# currently found resources: " + getCounts());

					long elapsed = System.currentTimeMillis() - pre;
					if (elapsed < 500) {
						DynamicResource.delay.set(5000l);
					} else {
						DynamicResource.delay.set(elapsed * 10);
					}
				}
			});
		}
	}
}
