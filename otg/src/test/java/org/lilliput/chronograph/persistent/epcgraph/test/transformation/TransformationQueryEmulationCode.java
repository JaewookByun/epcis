package org.lilliput.chronograph.persistent.epcgraph.test.transformation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bson.BsonArray;
import org.lilliput.chronograph.common.LoopPipeFunction;
import org.lilliput.chronograph.common.Step;
import org.lilliput.chronograph.common.TemporalType;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.common.Tokens.FC;
import org.lilliput.chronograph.common.Tokens.Position;
import org.lilliput.chronograph.persistent.ChronoEdge;
import org.lilliput.chronograph.persistent.ChronoGraph;
import org.lilliput.chronograph.persistent.ChronoVertex;
import org.lilliput.chronograph.persistent.EdgeEvent;
import org.lilliput.chronograph.persistent.VertexEvent;
import org.lilliput.chronograph.persistent.engine.ExternalTraversalEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mongodb.client.MongoCollection;
import com.tinkerpop.pipes.PipeFunction;

@SuppressWarnings({ "rawtypes", "unchecked", "unused", "unlikely-arg-type" })
public class TransformationQueryEmulationCode {

	public String source = null;
	public Long time = null;
	public String label = null;

	private Stream stream;
	private ArrayList<Step> stepList;
	private HashMap<String, Integer> stepIndex;
	private int loopCount;
	private boolean isPathEnabled;
	private boolean isParallel;
	private Class elementClass;
	private Class listElementClass;
	private Map<Object, Object> currentPath;
	private MongoCollection collection;

	class EPCTime {
		public String epc;
		public Long time;

		public EPCTime(String epc, Long time) {
			this.epc = epc;
			this.time = time;
		}

		public EPCTime getThis() {
			return this;
		}

		public String getEpc() {
			return epc;
		}

		public void setEpc(String epc) {
			this.epc = epc;
		}

		public Long getTime() {
			return time;
		}

		public void setTime(Long time) {
			this.time = time;
		}

		public String toString() {
			return epc + "-" + time;
		}

	}

	public Object compute() {
		ConcurrentHashMap<String, Long> gamma = new ConcurrentHashMap<String, Long>();
		EPCTime epcTime = new EPCTime(source, time);
		gamma.put(source, time);
		PipeFunction<VertexEvent, Boolean> exceedBound2 = new PipeFunction<VertexEvent, Boolean>() {
			@Override
			public Boolean compute(VertexEvent ve) {
				if (gamma.containsKey(ve.getVertex()) && (ve.getTimestamp() >= gamma.get(ve.getVertex()))) {
					return false;
				}
				return true;
			}
		};
		PipeFunction<List<EPCTime>, Object> storeGamma = new PipeFunction<List<EPCTime>, Object>() {
			@Override
			public Object compute(List<EPCTime> vertexEvents) {
				vertexEvents.parallelStream().forEach(ve -> {
					gamma.put(ve.epc, ve.time);
				});
				return null;
			}
		};

		LoopPipeFunction exitIfEmptyIterator = new LoopPipeFunction() {
			@Override
			public boolean compute(Object argument, Map<Object, Object> currentPath, int loopCount) {
				List list = (List) argument;
				// System.out.println(list.size());
				if (list == null || list.size() == 0)
					return false;
				return true;
			}
		};
		return new ExternalTraversalEngine(null, source, true, true, String.class).as("s").scatter()
				.oute(null, TemporalType.TIMESTAMP, AC.$gt, null, null, null, null, null, null, Position.first)
				.filter(exceedBound2).gather().elementDedup(FC.$min).sideEffect(storeGamma)
				.loop("s", exitIfEmptyIterator).path();
	}

	public void func1(final MongoCollection collection, final Object starts, final boolean setParallel,
			final boolean setPathEnabled, final Class elementClass) {

		currentPath = new HashMap<Object, Object>();

		if (starts instanceof ChronoGraph || starts instanceof ChronoVertex || starts instanceof ChronoEdge
				|| starts instanceof VertexEvent || starts instanceof EdgeEvent || starts instanceof EPCTime) {

			HashSet set = new HashSet();
			set.add(starts);
			if (setParallel == true)
				stream = set.parallelStream();
			else
				stream = set.stream();
			this.elementClass = starts.getClass();

			if (setPathEnabled) {
				HashSet initPathSet = new HashSet();
				List list = new ArrayList();
				list.add(starts);
				initPathSet.add(list);
				currentPath.put(starts, initPathSet);
			}
		}
		stepList = new ArrayList<Step>();
		stepIndex = new HashMap<String, Integer>();
		this.isPathEnabled = setPathEnabled;
		this.isParallel = setParallel;
		this.loopCount = 0;
		listElementClass = null;
		this.collection = collection;
	}

	private Set<EPCTime> getNextOutSet(EPCTime source)
			throws IOException, ParserConfigurationException, SAXException, ParseException {

		Set<EPCTime> ret = new HashSet<EPCTime>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String time = sdf.format(new Date(source.time));
		String url = "http://localhost:8080/epcgraph/Service/Poll/SimpleEventQuery?";
		url += "MATCH_inputEPC=" + source.epc + "&GE_eventTime=" + time;
		URL captureURL = new URL(url);
		HttpURLConnection con = (HttpURLConnection) captureURL.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		String res = response.toString();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(res.getBytes()));
		NodeList transformationEvents = doc.getElementsByTagName("TransformationEvent");
		for (int i = 0; i < transformationEvents.getLength(); i++) {
			Node transformationEvent = transformationEvents.item(i);
			NodeList transformationElements = transformationEvent.getChildNodes();
			long eventTimeMil = 0;
			HashSet<String> epcSet = new HashSet<String>();
			for (int j = 0; j < transformationElements.getLength(); j++) {
				Node element = transformationElements.item(j);
				String nodeName = element.getNodeName();
				if (nodeName.equals("eventTime")) {
					String eventTime = element.getTextContent();
					eventTimeMil = sdf.parse(eventTime).getTime();
				}
				if (nodeName.equals("outputEPCList")) {
					NodeList outputEPCList = element.getChildNodes();
					for (int k = 0; k < outputEPCList.getLength(); k++) {
						Node outputEPC = outputEPCList.item(k);
						NodeList elem2 = outputEPC.getChildNodes();
						if (elem2 instanceof Element) {
							for (int l = 0; l < elem2.getLength(); l++) {
								Node elem3 = elem2.item(l);
								String epc = elem3.getTextContent();
								epcSet.add(epc);

							}
						}
					}
				}
			}
			Iterator<String> epcIter = epcSet.iterator();
			while (epcIter.hasNext()) {
				String epc = epcIter.next();
				EPCTime epcTime = new EPCTime(epc, eventTimeMil);
				ret.add(epcTime);
			}
		}
		return ret;
	}

	public ExternalTraversalEngine oute(final BsonArray labels, final TemporalType typeOfVertexEvent, final AC tt,
			final AC s, final AC e, final AC ss, final AC se, final AC es, final AC ee, final Position pos) {
		Map intermediate = (Map) stream.map(ve -> {
			EPCTime et = (EPCTime) ve;
			Set<EPCTime> outSet = new HashSet<EPCTime>();
			try {
				outSet = getNextOutSet(et);
			} catch (IOException | ParserConfigurationException | SAXException | ParseException e1) {
				e1.printStackTrace();
			}
			return new AbstractMap.SimpleImmutableEntry(et, outSet);
		}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));
		updateTransformationPath(intermediate);
		stream = getStream(intermediate, isParallel);
		// Step Update
		final Class[] args = new Class[10];
		args[0] = BsonArray.class;
		args[1] = TemporalType.class;
		args[2] = AC.class;
		args[3] = AC.class;
		args[4] = AC.class;
		args[5] = AC.class;
		args[6] = AC.class;
		args[7] = AC.class;
		args[8] = AC.class;
		args[9] = Position.class;
		final Step step = new Step(this.getClass().getName(), "oute", args, labels, typeOfVertexEvent, tt, s, e, ss, se,
				es, ee, pos);
		stepList.add(step);
		elementClass = VertexEvent.class;
		return null;
	}

	private void updateTransformationPath(final Map intermediate) {
		HashMap<Object, Object> nextPath = new HashMap<Object, Object>();
		Iterator<Entry> intermediateEntrySet = intermediate.entrySet().iterator();
		while (intermediateEntrySet.hasNext()) {
			Entry entry = intermediateEntrySet.next();
			Object source = entry.getKey();
			Object objectValue = entry.getValue();
			if (objectValue instanceof Set) {
				Set destSet = (Set) objectValue;
				if (destSet.isEmpty()) {
					HashSet<List> currentPaths = (HashSet) currentPath.get(source);
					Iterator<List> currentPathIterator = currentPaths.iterator();
					while (currentPathIterator.hasNext()) {
						List current = currentPathIterator.next();
						List clone = new ArrayList(current);
						clone.add(null);
						if (nextPath.containsKey(null)) {
							HashSet<List> nextExisting = (HashSet) nextPath.get(null);
							nextExisting.add(clone);
							nextPath.put(null, nextExisting);
						} else {
							HashSet<List> nextEmpty = new HashSet<List>();
							nextEmpty.add(clone);
							nextPath.put(null, nextEmpty);
						}
					}
					continue;
				}
				Iterator valueIterator = destSet.iterator();
				while (valueIterator.hasNext()) {
					Object dest = valueIterator.next();
					HashSet<List> currentPaths = (HashSet) currentPath.get(source);
					Iterator<List> currentPathIterator = currentPaths.iterator();
					while (currentPathIterator.hasNext()) {
						List current = currentPathIterator.next();
						List clone = new ArrayList(current);
						clone.add(dest);
						if (nextPath.containsKey(dest)) {
							HashSet<List> nextExisting = (HashSet) nextPath.get(dest);
							nextExisting.add(clone);
							nextPath.put(dest, nextExisting);
						} else {
							HashSet<List> nextEmpty = new HashSet<List>();
							nextEmpty.add(clone);
							nextPath.put(dest, nextEmpty);
						}
					}
				}
			} else {
				Object dest = entry.getValue();
				if (dest == null) {
					HashSet<List> currentPaths = (HashSet) currentPath.get(source);
					Iterator<List> currentPathIterator = currentPaths.iterator();
					while (currentPathIterator.hasNext()) {
						List current = currentPathIterator.next();
						List clone = new ArrayList(current);
						clone.add(null);
						if (nextPath.containsKey(null)) {
							HashSet<List> nextExisting = (HashSet) nextPath.get(null);

							nextExisting.add(clone);
							nextPath.put(null, nextExisting);
						} else {
							HashSet<List> nextEmpty = new HashSet<List>();
							nextEmpty.add(clone);
							nextPath.put(null, nextEmpty);
						}
					}
					continue;
				}
				HashSet<List> currentPaths = (HashSet) currentPath.get(source);
				Iterator<List> currentPathIterator = currentPaths.iterator();
				while (currentPathIterator.hasNext()) {
					List current = currentPathIterator.next();
					List clone = new ArrayList(current);
					clone.add(dest);
					if (nextPath.containsKey(dest)) {
						HashSet<List> nextExisting = (HashSet) nextPath.get(dest);
						nextExisting.add(clone);
						nextPath.put(dest, nextExisting);
					} else {
						HashSet<List> nextEmpty = new HashSet<List>();
						nextEmpty.add(clone);
						nextPath.put(dest, nextEmpty);
					}
				}
			}
		}
		Iterator<Entry<Object, Object>> iter = currentPath.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Object, Object> entry = iter.next();
			if (entry.getKey() == null) {
				if (nextPath.containsKey(null)) {
					HashSet<List> next = (HashSet) nextPath.get(null);
					next.addAll((HashSet<List>) entry.getValue());
				}
			} else {
				if (!intermediate.containsKey(entry.getKey())) {
					nextPath.put(entry.getKey(), entry.getValue());
				}
			}
		}
		currentPath.clear();
		currentPath = new HashMap<Object, Object>(nextPath);
	}

	private Stream getStream(final Map intermediate, final boolean isParallel) {
		if (elementClass == List.class) {
			ArrayList next = new ArrayList();
			next.addAll(intermediate.values());
			if (isParallel)
				return next.parallelStream();
			else
				return next.stream();
		} else {
			Set next = (Set) intermediate.values().parallelStream().flatMap(e -> {
				if (e instanceof Collection) {
					return ((Collection) e).parallelStream();
				} else {
					return Stream.of(e);
				}
			}).collect(Collectors.toSet());
			if (isParallel)
				return next.parallelStream();
			else
				return next.stream();
		}
	}
}
