package org.oliot.epcis.service.query.mysql;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.persister.collection.CollectionPropertyNames;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.serde.mysql.PairType;
import org.oliot.epcis.serde.mysql.ReaderUtility;
import org.oliot.model.epcis.SubscriptionType;
import org.oliot.model.oliot.Action;
import org.oliot.model.oliot.AggregationEvent;
import org.oliot.model.oliot.ObjectEvent;
import org.oliot.model.oliot.PollParameters;
import org.oliot.model.oliot.QuantityEvent;
import org.oliot.model.oliot.ReadPoint;
import org.oliot.model.oliot.SensorEvent;
import org.oliot.model.oliot.Subscription;
import org.oliot.model.oliot.TransactionEvent;
import org.oliot.model.oliot.TransformationEvent;
import org.oliot.model.oliot.Vocabulary;
import org.oliot.model.oliot.VocabularyElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class QueryOprationBackend {
	@Autowired
	private SessionFactory sessionFactory; // check this

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void save(){
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		ReadPoint readPoint=new ReadPoint();
		readPoint.setsId("readpointCheck");
		session.save(readPoint);
		tx.commit();
		session.close();
		
	}
	
	public void save(SubscriptionType subscriptionType){
		Subscription subscription=new Subscription();
		subscription = convertFromSubscriptionType(subscriptionType);
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.save(subscription);
		tx.commit();
		session.close();
		
	}
	
	public void save(Subscription subscription){
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.save(subscription);
		tx.commit();
		session.close();
		
	}
	
	@SuppressWarnings("unchecked")
	public List<String> select(){
		System.out.println("2 here");
		Session session = getSessionFactory().openSession();
		Criteria criteria=session.createCriteria(TransformationEvent.class);
		criteria=addc( criteria);
		List<TransformationEvent> transformationEvents=criteria.list();
		List<String> ids=new ArrayList<String>();
		for(int i=0;i<transformationEvents.size();i++)
			ids.add((transformationEvents.get(i).getReadPoint().getsId())+"");
		return ids;
	}
	public Criteria addc(Criteria criteria){
		System.out.println("3 here");
		//Object[] array=new  Object[]{"bizStep"};
		//array[0]=1;
		//array[0]=2;
		List<String> array=new ArrayList<String>();
		array.add("bizStep2");
		array.add("ReadPoint_object");
		criteria.createAlias("readPoint", "rp");
		criteria.add(Restrictions.in("rp.sId", array));
		return criteria;
	}
	@SuppressWarnings("unchecked")
	public List<Vocabulary> checkTransformationEvent(){
		Session session = getSessionFactory().openSession();
		Criteria criteria=session.createCriteria(Vocabulary.class);
		
		//criteria.createAlias("extension.childQuantityList.quantityElement", "ch_qe");
		criteria.createAlias("vocabularyElementList.vocabularyElement","voc");
		//criteria.createAlias("voc.attribute","at");
		criteria.add(Restrictions.like("voc.sId", "urn:epc:id:sgln:0037000.00729.0"));

		
		List<Vocabulary> vocabulary=criteria.list();
		return vocabulary;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<String> findVocabilaryChildren(String vocaType,String id ){
		List<String> childrenList=new ArrayList<String>();
		Session session = getSessionFactory().openSession();
		Criteria criteria=session.createCriteria(Vocabulary.class);
		criteria.add(Restrictions.like("type", vocaType));
		criteria.createAlias("vocabularyElementList.vocabularyElement", "ve");
		criteria.add(Restrictions.like("ve.sId", id));
		List<Vocabulary> vocList=criteria.list();
		
		for(int i=0; i<vocList.size();i++){
			List<VocabularyElement> VocabElementList=vocList.get(i).getVocabularyElementList().getVocabularyElement();
			for(int j=0; j<VocabElementList.size();j++){
				List<String> childrenListij=VocabElementList.get(j).getChildren().getId();
				childrenList.addAll(childrenListij);
			}
			
		}
		
		return childrenList;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findVocabilaryChildren(String id ){
		List<String> childrenList=new ArrayList<String>();
		Session session = getSessionFactory().openSession();
		Criteria criteria=session.createCriteria(Vocabulary.class);
		criteria.createAlias("vocabularyElementList.vocabularyElement", "ve");
		criteria.add(Restrictions.like("ve.sId", id));
		List<Vocabulary> vocList=criteria.list();
		
		for(int i=0; i<vocList.size();i++){
			List<VocabularyElement> VocabElementList=vocList.get(i).getVocabularyElementList().getVocabularyElement();
			for(int j=0; j<VocabElementList.size();j++){
				List<String> childrenListij=VocabElementList.get(j).getChildren().getId();
				childrenList.addAll(childrenListij);
			}
			
		}
		
		return childrenList;
	}
	
	@SuppressWarnings("unused")
	public Criteria findepc(String epc ){
		List<String> childrenList=new ArrayList<String>();
		Session session = getSessionFactory().openSession();
		Criteria criteria=session.createCriteria(QuantityEvent.class);	
		 criteria=session.createCriteria(AggregationEvent.class);		
		criteria.createAlias("extension.childQuantityList.quantityElement", "in_qe");
		criteria.add(Restrictions.like("in_qe.epcClass", "epcClass1_O"));

			
		
		return criteria;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> find(String queryName){
		Session session = getSessionFactory().openSession();
		String hql="select subscription.subscriptionID from Subscription as subscription where subscription.queryName=:queryName";
		Query query=session.createQuery(hql);
		query.setString("queryName", queryName);
		List<String> subscriptionIdList=query.list();
		session.close();
		
		List<String> retList = new ArrayList<String>();
		for (int i = 0; i < subscriptionIdList.size(); i++) {
			retList.add(subscriptionIdList.get(i));
		}
		
		return retList;
	}
	
	@SuppressWarnings("unchecked")
	public int CountSubscriptionType(String subscriptionID){
		Session session = getSessionFactory().openSession();
		String hql=" from Subscription where subscriptionID= :subscriptionID";
		Query query=session.createQuery(hql);
		query.setString("subscriptionID", subscriptionID);
		List<Subscription> subscriptionList=query.list();
		session.close();
		
		
		
		return subscriptionList.size();
				
	}
	@SuppressWarnings("unchecked")
	public List<SubscriptionType> findAllSubscriptionType(){
		Session session = getSessionFactory().openSession();
		String hql=" from Subscription ";
		Query query=session.createQuery(hql);
		List<Subscription> subscriptionList=query.list();
		session.close();
		
		List<SubscriptionType> retList = new ArrayList<SubscriptionType>();
		for (int i = 0; i < subscriptionList.size(); i++) {
			retList.add(convertToSubscriptionType(subscriptionList.get(i)));
		}
		
		return retList;
				
	}
	
	@SuppressWarnings("unchecked")
	public List<SubscriptionType> findAllSubscriptionType(String subscriptionID){
		Session session = getSessionFactory().openSession();
		String hql=" from Subscription where subscriptionID= :subscriptionID";
		Query query=session.createQuery(hql);
		query.setString("subscriptionID", subscriptionID);
		List<Subscription> subscriptionList=query.list();
		session.close();
		
		List<SubscriptionType> retList = new ArrayList<SubscriptionType>();
		for (int i = 0; i < subscriptionList.size(); i++) {
			retList.add(convertToSubscriptionType(subscriptionList.get(i)));
		}
		
		return retList;
				
	}
	public void remove(SubscriptionType subscription){
		String subscriptionID=subscription.getSubscriptionID();
		System.out.println("from remove" + subscriptionID);
		
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String hql="  from Subscription  where subscriptionID= :subscriptionID";
		Query query=session.createQuery(hql);
		query.setString("subscriptionID", subscriptionID);
		Subscription subscripti=(Subscription)query.list().get(0);
		session.delete(subscripti);
		tx.commit();
		session.close();
	}
	
	public SubscriptionType convertToSubscriptionType(Subscription subscription){
		
		PollParameters p=subscription.getPollParametrs();
		org.oliot.model.epcis.PollParameters pollParametrs=new org.oliot.model.epcis.PollParameters(p.getQueryName(), p.getEventType(),
				p.getGE_eventTime(), p.getLT_eventTime(),p.getGE_recordTime(), p.getLT_recordTime(), p.getEQ_action(), p.getEQ_bizStep(),
				p.getEQ_disposition(),p.getEQ_readPoint(), p.getWD_readPoint(), p.getEQ_bizLocation(), p.getWD_bizLocation(),
				p.getEQ_transformationID(), p.getMATCH_epc(), p.getMATCH_parentID(), p.getMATCH_inputEPC(),p.getMATCH_outputEPC(), 
				p.getMATCH_anyEPC(), p.getMATCH_epcClass(), p.getMATCH_inputEPCClass(),	p.getMATCH_outputEPCClass(), p.getMATCH_anyEPCClass(), 
				p.getEQ_quantity(), p.getGT_quantity(), p.getGE_quantity(), p.getLT_quantity(), p.getLE_quantity(), p.getEQ_eventID(),
				p.getEXISTS_errorDeclaration(), p.getGE_errorDeclarationTime(), p.getLT_errorDeclarationTime(),
				p.getEQ_errorReason(), p.getEQ_correctiveEventID(), p.getOrderBy(), p.getOrderDirection(),
				p.getEventCountLimit(), p.getMaxEventCount(), p.getVocabularyName(), p.getIncludeAttributes(),
				p.getIncludeChildren(), p.getAttributeNames(), p.getEQ_name(), p.getWD_name(), p.getHASATTR(),
				p.getMaxElementCount(), p.getFormat(), p.getParams());

		SubscriptionType subscriptionType=new SubscriptionType(subscription.getSubscriptionID(), subscription.getDest(), 
				subscription.getSchedule(), subscription.getTriggerSub(),subscription.getInitialRecordTime(), 
				subscription.getReportIfEmpty(), pollParametrs);
		
		
		return subscriptionType;

	}
	
	public Subscription convertFromSubscriptionType(SubscriptionType subscriptionType){
	
		org.oliot.model.epcis.PollParameters p=subscriptionType.getPollParameters();
		PollParameters pollParameters=new PollParameters(p.getQueryName(), p.getEventType(),
				p.getGE_eventTime(), p.getLT_eventTime(),p.getGE_recordTime(), p.getLT_recordTime(), p.getEQ_action(), p.getEQ_bizStep(),
				p.getEQ_disposition(),p.getEQ_readPoint(), p.getWD_readPoint(), p.getEQ_bizLocation(), p.getWD_bizLocation(),
				p.getEQ_transformationID(), p.getMATCH_epc(), p.getMATCH_parentID(), p.getMATCH_inputEPC(),p.getMATCH_outputEPC(), 
				p.getMATCH_anyEPC(), p.getMATCH_epcClass(), p.getMATCH_inputEPCClass(),	p.getMATCH_outputEPCClass(), p.getMATCH_anyEPCClass(), 
				p.getEQ_quantity(), p.getGT_quantity(), p.getGE_quantity(), p.getLT_quantity(), p.getLE_quantity(), p.getEQ_eventID(),
				p.getEXISTS_errorDeclaration(), p.getGE_errorDeclarationTime(), p.getLT_errorDeclarationTime(),
				p.getEQ_errorReason(), p.getEQ_correctiveEventID(), p.getOrderBy(), p.getOrderDirection(),
				p.getEventCountLimit(), p.getMaxEventCount(), p.getVocabularyName(), p.getIncludeAttributes(),
				p.getIncludeChildren(), p.getAttributeNames(), p.getEQ_name(), p.getWD_name(), p.getHASATTR(),
				p.getMaxElementCount(), p.getFormat(), p.getParams());
		
		Subscription subscription=new Subscription(subscriptionType.getSubscriptionID(), subscriptionType.getDest(), 
				subscriptionType.getSchedule(), subscriptionType.getTrigger(),subscriptionType.getInitialRecordTime(), 
				subscriptionType.getReportIfEmpty(), pollParameters);
		return subscription;
	}
	
	public Criteria	makeVocQueryCriteria(String vocabularyName,
			boolean includeAttributes, boolean includeChildren,
			String attributeNames, String eQ_name, String wD_name,
			String hASATTR, String maxElementCount,Map<String, String> paramMap){
		Session session = getSessionFactory().openSession();
		Criteria criteria=session.createCriteria(Vocabulary.class);

		/**
		 * If specified, only vocabulary elements drawn from one of the
		 * specified vocabularies will be included in the results. Each element
		 * of the specified list is the formal URI name for a vocabulary; e.g.,
		 * one of the URIs specified in the table at the end of Section 7.2. If
		 * omitted, all vocabularies are considered.
		 */
		if(vocabularyName != null){
			List<String> subStringList=MysqlQueryUtil.getStringList(vocabularyName);
			criteria.add(Restrictions.in("type", subStringList));
		}
		
		/**If specified, the result will only
		 * include vocabulary elements whose
		 * names are equal to one of the
		 * specified values.
		 * If this parameter and WD_name are
		 * both omitted, vocabulary elements
		 * are included regardless of their names.	 
		 */
		criteria.createAlias("vocabularyElementList.vocabularyElement","vocAll");
		criteria.createAlias("vocAll.attribute","attAll");
		criteria.setResultTransformer( DistinctRootEntityResultTransformer.INSTANCE );
		if(eQ_name != null){
			List<String> subStringList=MysqlQueryUtil.getStringList(eQ_name);
			//criteria.createAlias("vocabularyElementList.vocabularyElement","voceq");
			criteria.add(Restrictions.in("vocAll.sId", subStringList));
		}
		/**If specified, only those attributes
		 * whose names match one of the
		 * specified names will be included in the results.
		 * If omitted, all attributes for each
		 * matching vocabulary element will
		 * be included. (To obtain a list of
		 * vocabulary element names with no
		 * attributes, specify false for
		 * includeAttributes.)The value of this parameter SHALL
		 * be ignored if includeAttributes is false.
		 * Note that this parameter does not affect which vocabulary elements
		 * are included in the result; it only limits which attributes will be
		 * included with each vocabulary element.		 
		 */
		if(attributeNames != null){
			List<String> subStringList=MysqlQueryUtil.getStringList(attributeNames);
			
			//criteria.createAlias("vocabularyElementList.vocabularyElement","vocatr");
			//criteria.createAlias("vocAll.attribute","at1");
			criteria.add(Restrictions.in("attAll.sId", subStringList));
			
		}
		
		/**	 If specified, the result will only
		 * include vocabulary elements that
		 * either match one of the specified
		 * names, or are direct or indirect
		 * descendants of a vocabulary
		 * element that matches one of the
		 * specified names. The meaning of
		 * “direct or indirect descendant” is
		 * described in Section 6.5. (WD is an
		 * abbreviation for “with descendants.”)
		 * If this parameter and EQ_name are
		 * both omitted, vocabulary elements
		 * are included regardless of their names.
		 */		
		if(wD_name != null){
			List<String> subStringList=MysqlQueryUtil.getStringList(wD_name);
			List<String> subStringListWithChildren=new ArrayList<String>();
			subStringListWithChildren.addAll(subStringList);
			//
			for(int i=0; i<subStringList.size();i++){
				subStringListWithChildren.addAll(
						findVocabilaryChildren(subStringList.get(i)));
			}
			if(subStringListWithChildren.size()>0){
				criteria.createAlias("vocabularyElementList.vocabularyElement","voc");
				criteria.add(Restrictions.in("voc.sId", subStringListWithChildren));
			}
		}
		/**	If specified, the result will only
		 * include vocabulary elements that
		 * have a non-null attribute whose
		 * name matches one of the values
		 * specified in this parameter.
		 */
		
		if(hASATTR != null){
			List<String> subStringList=MysqlQueryUtil.getStringList(hASATTR);
			criteria.createAlias("vocabularyElementList.vocabularyElement","hasvoc");
			criteria.createAlias("hasvoc.attribute","hasat");
			criteria.add(Restrictions.in("hasat.sId", subStringList));
		}
		
		
		Iterator<String> paramIter = paramMap.keySet().iterator();
		while (paramIter.hasNext()) {
			String paramName = paramIter.next();
			String paramValues = paramMap.get(paramName);
			if (paramName.contains("EQATTR_")) {
				String name = paramName.substring(7, paramName.length());
				List<String> subStringList=MysqlQueryUtil.getStringList(paramValues);
				//criteria.createAlias("vocabularyElementList.vocabularyElement","vocpar");
				//criteria.createAlias("vocAll.attribute","atpar");
				criteria.add(Restrictions.and(
						Restrictions.like("attAll.sId", name),
						Restrictions.in("attAll.value", subStringList)));
			}
			
			}

		
		return criteria;
	}
	
public Criteria makeQueryCriteria( String eventType,
	String GE_eventTime, String LT_eventTime, String GE_recordTime,
	String LT_recordTime, String EQ_action, String EQ_bizStep,
	String EQ_disposition, String EQ_readPoint, String WD_readPoint,
	String EQ_bizLocation, String WD_bizLocation,
	String EQ_transformationID, String MATCH_epc,
	String MATCH_parentID, String MATCH_inputEPC,
	String MATCH_outputEPC, String MATCH_anyEPC, String MATCH_epcClass,
	String MATCH_inputEPCClass, String MATCH_outputEPCClass,
	String MATCH_anyEPCClass, String EQ_quantity, String GT_quantity,
	String GE_quantity, String LT_quantity, String LE_quantity,	
	String EQ_eventID, Boolean EXISTS_errorDeclaration, String GE_errorDeclarationTime,
	String LT_errorDeclarationTime,String EQ_errorReason,String EQ_correctiveEventID,
	String orderBy, String orderDirection, String eventCountLimit,
	String maxEventCount, Map<String, String> paramMap) {
	Session session = getSessionFactory().openSession();
	Criteria criteria=session.createCriteria(AggregationEvent.class);
try {
	
	
	if(eventType.equals("AggregationEvent")){
		criteria=session.createCriteria(AggregationEvent.class);
	}
	else if(eventType.equals("TransactionEvent")){
		criteria=session.createCriteria(TransactionEvent.class);
	}else if(eventType.equals("ObjectEvent")){
		criteria=session.createCriteria(ObjectEvent.class);
	}else if(eventType.equals("TransformationEvent")){
		criteria=session.createCriteria(TransformationEvent.class);
	}else if(eventType.equals("QuantityEvent")){
		criteria=session.createCriteria(QuantityEvent.class);
	}else if(eventType.equals("SensorEvent")){
		criteria=session.createCriteria(SensorEvent.class);
	}
	/**
	 * GE_eventTime: If specified, only events with eventTime greater
	 * than or equal to the specified value will be included in the
	 * result. If omitted, events are included regardless of their
	 * eventTime (unless constrained by the LT_eventTime parameter).
	 * Example: 2014-08-11T19:57:59.717+09:00 SimpleDateFormat sdf = new
	 * SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	 * eventTime.setTime(sdf.parse(timeString)); e.g.
	 * 1988-07-04T12:08:56.235-07:00
	 * 
	 * Verified
	 */
	if (GE_eventTime != null) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		Date date=sdf.parse(GE_eventTime);
		criteria.add(Restrictions.ge("eventTime", date));
		
	}
	/**
	 * LT_eventTime: If specified, only events with eventTime less than
	 * the specified value will be included in the result. If omitted,
	 * events are included regardless of their eventTime (unless
	 * constrained by the GE_eventTime parameter).
	 * 
	 * Verified
	 */
	if (LT_eventTime != null) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		Date date=sdf.parse(LT_eventTime);
		criteria.add(Restrictions.lt("eventTime", date));
		
	}
	/**
	 * GE_recordTime: If provided, only events with recordTime greater
	 * than or equal to the specified value will be returned. The
	 * automatic limitation based on event record time (Section 8.2.5.2)
	 * may implicitly provide a constraint similar to this parameter. If
	 * omitted, events are included regardless of their recordTime,
	 * other than automatic limitation based on event record time
	 * (Section 8.2.5.2).
	 * 
	 * Verified
	 */
	if (GE_recordTime != null) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		Date date=sdf.parse(GE_recordTime);
		criteria.add(Restrictions.ge("recordTime", date));
		
	}
	/**
	 * LE_recordTime: If provided, only events with recordTime less than
	 * the specified value will be returned. If omitted, events are
	 * included regardless of their recordTime (unless constrained by
	 * the GE_recordTime parameter or the automatic limitation based on
	 * event record time).
	 * 
	 * Verified
	 */
	if (LT_recordTime != null) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		Date date=sdf.parse(LT_recordTime);
		criteria.add(Restrictions.lt("recordTime", date));

	}
//------------------------------------------	
	/**
	 * EQ_eventID : If this parameter is specified, the result will only
	 * include events that (a) have a non-null eventID field; and where (b)
	 * the eventID field is equal to one of the values specified in this
	 * parameter. If this parameter is omitted, events are returned
	 * regardless of the value of the eventID field or whether the eventID
	 * field exists at all.
	 * 
	 * List of String
	 * 
	 */
	if (EQ_eventID != null) {
		List<String> subStringList=MysqlQueryUtil.getStringList(EQ_eventID);
		if(subStringList.size()>0){
			criteria.createAlias("baseExtension", "beventID");
			criteria.add(Restrictions.in("beventID.eventID", subStringList));
		}

	}
	/**
	 * GE_errorDeclaration Time: If this parameter is specified, the result
	 * will only include events that (a) contain an ErrorDeclaration ; and
	 * where (b) the value of the errorDeclarationTime field is greater than
	 * or equal to the specified value. If this parameter is omitted, events
	 * are returned regardless of whether they contain an ErrorDeclaration
	 * or what the value of the errorDeclarationTime field is.
	 */
	if (GE_errorDeclarationTime != null) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		Date date=sdf.parse(GE_errorDeclarationTime);
		criteria.createAlias("baseExtension", "bGE_errorDeclarationTime");
		criteria.createAlias("bGE_errorDeclarationTime.errorDeclaration", "GE_errorDeclarationTimeDec");
		criteria.add(Restrictions.ge("GE_errorDeclarationTimeDec.declarationTime", date));
	}

	/**
	 * LT_errorDeclaration Time: contain an ErrorDeclaration ; and where (b)
	 * the value of the errorDeclarationTime field is less than to the
	 * specified value. If this parameter is omitted, events are returned
	 * regardless of whether they contain an ErrorDeclaration or what the
	 * value of the errorDeclarationTime field is.
	 */
	if (LT_errorDeclarationTime != null) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		Date date=sdf.parse(LT_errorDeclarationTime);
		criteria.createAlias("baseExtension", "bLT_errorDeclarationTime");
		criteria.createAlias("bLT_errorDeclarationTime.errorDeclaration", "LT_errorDeclarationTimeDec");
		criteria.add(Restrictions.lt("LT_errorDeclarationTimeDec.declarationTime", date));
	}


	/**
	 * EQ_errorReason: If this parameter is specified, the result will only
	 * include events that (a) contain an ErrorDeclaration ; and where (b)
	 * the error declaration contains a non-null reason field; and where (c)
	 * the reason field is equal to one of the values specified in this
	 * parameter. If this parameter is omitted, events are returned
	 * regardless of the they contain an ErrorDeclaration or what the value
	 * of the reason field is.
	 */

	if (EQ_errorReason != null) {
		List<String> subStringList=MysqlQueryUtil.getStringList(EQ_errorReason);
		if(subStringList.size()>0){
			criteria.createAlias("baseExtension", "bErrorReason");
			criteria.createAlias("bErrorReason.errorDeclaration", "EQ_errorReasonErrorDec");
			criteria.add(Restrictions.in("EQ_errorReasonErrorDec.reason", subStringList));
		}
	}

	/**
	 * EQ_correctiveEventID: If this parameter is specified, the result will
	 * only include events that (a) contain an ErrorDeclaration ; and where
	 * (b) one of the elements of the correctiveEventIDs list is equal to
	 * one of the values specified in this parameter. If this parameter is
	 * omitted, events are returned regardless of the they contain an
	 * ErrorDeclaration or the contents of the correctiveEventIDs list.
	 */

	if (EQ_correctiveEventID != null) {
		List<String> subStringList=MysqlQueryUtil.getStringList(EQ_correctiveEventID);
		if(subStringList.size()>0){
			
			criteria.createAlias("baseExtension", "bcorrectiveEventID");
			criteria.createAlias("bcorrectiveEventID.errorDeclaration", "correctiveEventIDDec");
			criteria.createAlias("correctiveEventIDDec.correctiveEventIDs", "ceids");
			criteria.createAlias("ceids.correctiveEventID", "ceid");
			criteria.add(Restrictions.in("ceid.correctiveEventID", subStringList));
		}
//		BsonArray paramArray = getParamBsonArray(p.getEQ_correctiveEventID());
//		BsonDocument queryObject = getQueryObject(new String[] { "errorDeclaration.correctiveEventIDs" },
//				paramArray);
//		if (queryObject != null) {
//			queryList.add(queryObject);
//		}
	}

	/**
	 * EXISTS_errorDeclaration: If this parameter is specified, the result
	 * will only include events that contain an ErrorDeclaration . If this
	 * parameter is omitted, events are returned regardless of whether they
	 * contain an ErrorDeclaration .
	 */

	if (EXISTS_errorDeclaration != null) {
		criteria.createAlias("baseExtension", "bEXISTS_errorD");
		criteria.add(Restrictions.isNotNull("bEXISTS_errorD.errorDeclaration"));
		
//		Boolean isExist = Boolean.parseBoolean(p.getEXISTS_errorDeclaration().toString());
//		BsonBoolean isExistBson = new BsonBoolean(isExist);
//		BsonDocument query = getExistsQueryObject("errorDeclaration", null, isExistBson);
//		if (query != null)
//			queryList.add(query);
	}
	
	
//---------------------------------------------------
	/**
	 * EQ_action: If specified, the result will only include events that
	 * (a) have an action field; and where (b) the value of the action
	 * field matches one of the specified values. The elements of the
	 * value of this parameter each must be one of the strings ADD,
	 * OBSERVE, or DELETE; if not, the implementation SHALL raise a
	 * QueryParameterException. If omitted, events are included
	 * regardless of their action field.
	 * 
	 * Verified
	 */

	if (EQ_action != null) {
		if(eventType.equals("AggregationEvent")||
				eventType.equals("ObjectEvent")||
				eventType.equals("TransactionEvent")){
			// Constrained already checked
			List<String> subStringList=MysqlQueryUtil.getStringList(EQ_action);
			List<Action> actions=new ArrayList<Action>();
			for(int i=0; i<subStringList.size();i++)
				actions.add(Action.fromValue(subStringList.get(i)));
			//if(subStringList.size()>0)
			criteria.add(Restrictions.in("action", actions));
		}else{
			criteria.add(Restrictions.eq("id",0));
		}	
		
	}
	/**
	 * EQ_bizStep: If specified, the result will only include events
	 * that (a) have a non-null bizStep field; and where (b) the value
	 * of the bizStep field matches one of the specified values. If this
	 * parameter is omitted, events are returned regardless of the value
	 * of the bizStep field or whether the bizStep field exists at all.
	 * 
	 * Verified
	 */
	if (EQ_bizStep != null) {
		List<String> subStringList=MysqlQueryUtil.getStringList(EQ_bizStep);
		if(subStringList.size()>0)
		criteria.add(Restrictions.in("bizStep", subStringList));
	}
	/**
	 * EQ_disposition: Like the EQ_bizStep parameter, but for the
	 * disposition field.
	 * 
	 * Verified
	 */
	if (EQ_disposition != null) {
		
		List<String> subStringList=MysqlQueryUtil.getStringList(EQ_disposition);
		if(subStringList.size()>0)
		criteria.add(Restrictions.in("disposition", subStringList));
	}
	/**
	 * EQ_readPoint: If specified, the result will only include events
	 * that (a) have a non-null readPoint field; and where (b) the value
	 * of the readPoint field matches one of the specified values. If
	 * this parameter and WD_readPoint are both omitted, events are
	 * returned regardless of the value of the readPoint field or
	 * whether the readPoint field exists at all.
	 */
	if (EQ_readPoint != null) {
		System.out.println("In criteria:  EQ_readPoint: "+EQ_readPoint);
		List<String> subStringList=MysqlQueryUtil.getStringList(EQ_readPoint);
		if(subStringList.size()>0){
			criteria.createAlias("readPoint", "rp");
			criteria.add(Restrictions.in("rp.sId", subStringList));
		}
	}

	/**
	 * WD_readPoint: If specified, the result will only include events
	 * that (a) have a non-null readPoint field; and where (b) the value
	 * of the readPoint field matches one of the specified values, or is
	 * a direct or indirect descendant of one of the specified values.
	 * The meaning of “direct or indirect descendant” is specified by
	 * master data, as described in Section 6.5. (WD is an abbreviation
	 * for “with descendants.”) If this parameter and EQ_readPoint are
	 * both omitted, events are returned regardless of the value of the
	 * readPoint field or whether the readPoint field exists at all.
	 */
	if (WD_readPoint != null) {
		List<String> subStringList=MysqlQueryUtil.getStringList(WD_readPoint);
		List<String> subStringListWithChildren=new ArrayList<String>();
		subStringListWithChildren.addAll(subStringList);
		//
		for(int i=0; i<subStringList.size();i++){
			subStringListWithChildren.addAll(
					findVocabilaryChildren("urn:epcglobal:epcis:vtype:ReadPoint", subStringList.get(i)));
		}
		if(subStringListWithChildren.size()>0){
			criteria.createAlias("readPoint", "wd_rp");
			criteria.add(Restrictions.in("wd_rp.sId", subStringListWithChildren));
		}

	}
	/**
	 * EQ_bizLocation: Like the EQ_readPoint parameter, but for the
	 * bizLocation field.
	 */
	if (EQ_bizLocation != null) {
		
		List<String> subStringList=MysqlQueryUtil.getStringList(EQ_bizLocation);
		if(subStringList.size()>0){
			criteria.createAlias("bizLocation", "bl");
			criteria.add(Restrictions.in("bl.sId", subStringList));
		}

	}
	/**
	 * WD_bizLocation: Like the WD_readPoint parameter, but for the
	 * bizLocation field.
	 */
	if (WD_bizLocation != null) {
		List<String> subStringList=MysqlQueryUtil.getStringList(WD_bizLocation);
		List<String> subStringListWithChildren=new ArrayList<String>();
		subStringListWithChildren.addAll(subStringList);
		//
		for(int i=0; i<subStringList.size();i++){
			subStringListWithChildren.addAll(
					findVocabilaryChildren("urn:epcglobal:epcis:vtype:BusinessLocation", subStringList.get(i)));
		}
		if(subStringListWithChildren.size()>0){
			criteria.createAlias("bizLocation", "wd_bl");
			criteria.add(Restrictions.in("wd_bl.sId", subStringListWithChildren));
		}
	}

	/**
	 * EQ_transformationID: If this parameter is specified, the result
	 * will only include events that (a) have a transformationID field
	 * (that is, TransformationEvents or extension event type that
	 * extend TransformationEvent); and where (b) the transformationID
	 * field is equal to one of the values specified in this parameter.
	 */
	if (EQ_transformationID != null) {
		if(eventType.equals("TransformationEvent")){
			List<String> subStringList=MysqlQueryUtil.getStringList(EQ_transformationID);
			if(subStringList.size()>0)
				criteria.add(Restrictions.in("transformationID", subStringList));
		}else{
			criteria.add(Restrictions.eq("id",0));
		}
	}

	/**
	 * MATCH_epc: If this parameter is specified, the result will only
	 * include events that (a) have an epcList or a childEPCs field
	 * (that is, ObjectEvent, AggregationEvent, TransactionEvent or
	 * extension event types that extend one of those three); and where
	 * (b) one of the EPCs listed in the epcList or childEPCs field
	 * (depending on event type) matches one of the EPC patterns or URIs
	 * specified in this parameter, where the meaning of “matches” is as
	 * specified in Section 8.2.7.1.1. If this parameter is omitted,
	 * events are included regardless of their epcList or childEPCs
	 * field or whether the epcList or childEPCs field exists.
	 * 
	 * Somewhat verified
	 */
	if (MATCH_epc != null) {
		List<String> epcList=MysqlQueryUtil.getepcList(MATCH_epc);
		if(eventType.equals("AggregationEvent")){
			criteria.createAlias("childEPCs.epc", "ch_ep");
			criteria.add(Restrictions.in("ch_ep.value", epcList));
		}else if(eventType.equals("TransactionEvent")||
				eventType.equals("ObjectEvent")) {
			criteria.createAlias("epcList.epc", "ep");
			criteria.add(Restrictions.in("ep.value", epcList));
		}else{
			criteria.add(Restrictions.eq("id",0));
		}

	}

	/**
	 * MATCH_parentID: Like MATCH_epc, but matches the parentID field of
	 * AggregationEvent, the parentID field of TransactionEvent, and
	 * extension event types that extend either AggregationEvent or
	 * TransactionEvent. The meaning of “matches” is as specified in
	 * Section 8.2.7.1.1.
	 */
	if (MATCH_parentID != null) {
		if(eventType.equals("AggregationEvent")||
				eventType.equals("TransactionEvent")){
			List<String> epcList=MysqlQueryUtil.getepcList(MATCH_parentID);
			criteria.add(Restrictions.in("parentID", epcList));
		}else{
			criteria.add(Restrictions.eq("id",0));
		}
		
		
	}

	/**
	 * MATCH_inputEPC: If this parameter is specified, the result will
	 * only include events that (a) have an inputEPCList (that is,
	 * TransformationEvent or an extension event type that extends
	 * TransformationEvent); and where (b) one of the EPCs listed in the
	 * inputEPCList field matches one of the EPC patterns or URIs
	 * specified in this parameter. The meaning of “matches” is as
	 * specified in Section 8.2.7.1.1. If this parameter is omitted,
	 * events are included regardless of their inputEPCList field or
	 * whether the inputEPCList field exists.
	 */
	if (MATCH_inputEPC != null) {
		if(eventType.equals("TransformationEvent")){
			List<String> epcList=MysqlQueryUtil.getepcList(MATCH_inputEPC);
			criteria.createAlias("inputEPCList.epc", "in_ep");
			criteria.add(Restrictions.in("in_ep.value", epcList));
		}else{
			criteria.add(Restrictions.eq("id",0));
		}


	}

	/**
	 * MATCH_outputEPC: If this parameter is specified, the result will
	 * only include events that (a) have an inputEPCList (that is,
	 * TransformationEvent or an extension event type that extends
	 * TransformationEvent); and where (b) one of the EPCs listed in the
	 * inputEPCList field matches one of the EPC patterns or URIs
	 * specified in this parameter. The meaning of “matches” is as
	 * specified in Section 8.2.7.1.1. If this parameter is omitted,
	 * events are included regardless of their inputEPCList field or
	 * whether the inputEPCList field exists.
	 */
	if (MATCH_outputEPC != null) {
		if(eventType.equals("TransformationEvent")){
			List<String> epcList=MysqlQueryUtil.getepcList(MATCH_outputEPC);
			criteria.createAlias("outputEPCList.epc", "out_ep");
			criteria.add(Restrictions.in("out_ep.value", epcList));
		}else{
			criteria.add(Restrictions.eq("id",0));
		}

	}

	/**
	 * MATCH_anyEPC: If this parameter is specified, the result will
	 * only include events that (a) have an epcList field, a childEPCs
	 * field, a parentID field, an inputEPCList field, or an
	 * outputEPCList field (that is, ObjectEvent, AggregationEvent,
	 * TransactionEvent, TransformationEvent, or extension event types
	 * that extend one of those four); and where (b) the parentID field
	 * or one of the EPCs listed in the epcList, childEPCs,
	 * inputEPCList, or outputEPCList field (depending on event type)
	 * matches one of the EPC patterns or URIs specified in this
	 * parameter. The meaning of “matches” is as specified in Section
	 * 8.2.7.1.1.
	 */

	if (MATCH_anyEPC != null) {
		List<String> epcList=MysqlQueryUtil.getepcList(MATCH_anyEPC);
		
		if(eventType.equals("AggregationEvent")){
			criteria.createAlias("childEPCs.epc", "any_ch_ep");
			criteria.add(Restrictions.or(
					Restrictions.in("any_ch_ep.value", epcList),
					Restrictions.in("parentID", epcList)));
		}
		else if(eventType.equals("ObjectEvent")){
			criteria.createAlias("epcList.epc", "any_ob_ep");
			criteria.add(Restrictions.in("any_ob_ep.value", epcList));
		}
		else if(eventType.equals("TransactionEvent")){
			criteria.createAlias("epcList.epc", "any_tr_ep");
			criteria.add(Restrictions.or(
					Restrictions.in("any_tr_ep.value", epcList),
					Restrictions.in("parentID", epcList)));
		}
		else if(eventType.equals("TransformationEvent")){
			criteria.createAlias("outputEPCList.epc", "any_out_ep");
			criteria.createAlias("inputEPCList.epc", "any_in_ep");
			criteria.add(Restrictions.or(
					Restrictions.in("any_out_ep.value", epcList),
					Restrictions.in("any_in_ep.value", epcList)));
		}

		
	}

	/**
	 * MATCH_epcClass: If this parameter is specified, the result will
	 * only include events that (a) have a quantityList or a
	 * childQuantityList field (that is, ObjectEvent, AggregationEvent,
	 * TransactionEvent or extension event types that extend one of
	 * those three); and where (b) one of the EPC classes listed in the
	 * quantityList or childQuantityList field (depending on event type)
	 * matches one of the EPC patterns or URIs specified in this
	 * parameter. The result will also include QuantityEvents whose
	 * epcClass field matches one of the EPC patterns or URIs specified
	 * in this parameter. The meaning of “matches” is as specified in
	 * Section 8.2.7.1.1.
	 */

	if (MATCH_epcClass != null) {
		List<String> epcList=MysqlQueryUtil.getepcList(MATCH_epcClass);
		if(eventType.equals("AggregationEvent")){
			criteria.createAlias("extension.childQuantityList.quantityElement", "ch_qe");
			criteria.add(Restrictions.in("ch_qe.epcClass", epcList));
		}
		else if(eventType.equals("ObjectEvent")||
				eventType.equals("TransactionEvent")){
			criteria.createAlias("extension.quantityList.quantityElement", "ql_qe");
			criteria.add(Restrictions.in("ql_qe.epcClass", epcList));
		}else if(eventType.equals("QuantityEvent")){
			criteria.add(Restrictions.in("epcClass", epcList));
		}else{
			criteria.add(Restrictions.eq("id",0));
		}

	}

	/**
	 * MATCH_inputEPCClass: If this parameter is specified, the result
	 * will only include events that (a) have an inputQuantityList field
	 * (that is, TransformationEvent or extension event types that
	 * extend it); and where (b) one of the EPC classes listed in the
	 * inputQuantityList field (depending on event type) matches one of
	 * the EPC patterns or URIs specified in this parameter. The meaning
	 * of “matches” is as specified in Section 8.2.7.1.1.
	 */
	if (MATCH_inputEPCClass != null) {
		if(eventType.equals("TransformationEvent")){
			List<String> epcList=MysqlQueryUtil.getepcList(MATCH_inputEPCClass);
			criteria.createAlias("inputQuantityList.quantityElement", "in_qe");
			criteria.add(Restrictions.in("in_qe.epcClass", epcList));
		}else{
			criteria.add(Restrictions.eq("id",0));
		}
	}

	/**
	 * MATCH_outputEPCClass: If this parameter is specified, the result
	 * will only include events that (a) have an outputQuantityList
	 * field (that is, TransformationEvent or extension event types that
	 * extend it); and where (b) one of the EPC classes listed in the
	 * outputQuantityList field (depending on event type) matches one of
	 * the EPC patterns or URIs specified in this parameter. The meaning
	 * of “matches” is as specified in Section 8.2.7.1.1.
	 */

	if (MATCH_outputEPCClass != null) {
		if(eventType.equals("TransformationEvent")){
			List<String> epcList=MysqlQueryUtil.getepcList(MATCH_outputEPCClass);
			criteria.createAlias("outputQuantityList.quantityElement", "out_qe");
			criteria.add(Restrictions.in("out_qe.epcClass", epcList));
		}else{
			criteria.add(Restrictions.eq("id",0));
		}
	}

	/**
	 * MATCH_anyEPCClass: If this parameter is specified, the result
	 * will only include events that (a) have a quantityList,
	 * childQuantityList, inputQuantityList, or outputQuantityList field
	 * (that is, ObjectEvent, AggregationEvent, TransactionEvent,
	 * TransformationEvent, or extension event types that extend one of
	 * those four); and where (b) one of the EPC classes listed in any
	 * of those fields matches one of the EPC patterns or URIs specified
	 * in this parameter. The result will also include QuantityEvents
	 * whose epcClass field matches one of the EPC patterns or URIs
	 * specified in this parameter. The meaning of “matches” is as
	 * specified in Section 8.2.7.1.1.
	 */
	if (MATCH_anyEPCClass != null) {
		List<String> epcList=MysqlQueryUtil.getepcList(MATCH_anyEPCClass);
		if(eventType.equals("AggregationEvent")){
			criteria.createAlias("extension.childQuantityList.quantityElement", "any_ch_qe");
			criteria.add(Restrictions.in("any_ch_qe.epcClass", epcList));
		}
		else if(eventType.equals("ObjectEvent")||
				eventType.equals("TransactionEvent")){
			criteria.createAlias("extension.quantityList.quantityElement", "any_ql_qe");
			criteria.add(Restrictions.in("any_ql_qe.epcClass", epcList));
		}else if(eventType.equals("TransformationEvent")){
			criteria.createAlias("outputQuantityList.quantityElement", "any_out_qe");
			criteria.createAlias("inputQuantityList.quantityElement", "any_in_qe");
			criteria.add(Restrictions.or(
					Restrictions.in("any_out_qe.epcClass", epcList),
					Restrictions.in("any_in_qe.epcClass", epcList)));
		}
		
		else if(eventType.equals("QuantityEvent")){
			criteria.add(Restrictions.in("epcClass", epcList));
		}else{
			criteria.add(Restrictions.eq("id",0));
		}

	}

	/**
	 * (DEPCRECATED in EPCIS 1.1) EQ_quantity; GT_quantity; GE_quantity;
	 * LT_quantity; LE_quantity
	 **/
	if(eventType.equals("QuantityEvent")){
		
		if(EQ_quantity!=null){
			int quantity=Integer.parseInt(EQ_quantity);
			criteria.add(Restrictions.eq("quantity", quantity));
		}
		if(GT_quantity!=null){
			int quantity=Integer.parseInt(GT_quantity);
			criteria.add(Restrictions.gt("quantity", quantity));
		}
		if(GE_quantity!=null){
			int quantity=Integer.parseInt(GE_quantity);
			criteria.add(Restrictions.ge("quantity", quantity));
		}
		if(LT_quantity!=null){
			int quantity=Integer.parseInt(LT_quantity);
			criteria.add(Restrictions.lt("quantity", quantity));
		}
		if(LE_quantity!=null){
			int quantity=Integer.parseInt(LE_quantity);
			criteria.add(Restrictions.le("quantity", quantity));
		}
		
	}

	/**
	 * EQ_fieldname: This is not a single parameter, but a family of
	 * parameters. If a parameter of this form is specified, the result
	 * will only include events that (a) have a field named fieldname
	 * whose type is either String or a vocabulary type; and where (b)
	 * the value of that field matches one of the values specified in
	 * this parameter. Fieldname is the fully qualified name of an
	 * extension field. The name of an extension field is an XML qname;
	 * that is, a pair consisting of an XML namespace URI and a name.
	 * The name of the corresponding query parameter is constructed by
	 * concatenating the following: the string EQ_, the namespace URI
	 * for the extension field, a pound sign (#), and the name of the
	 * extension field.
	 */

	Iterator<String> paramIter = paramMap.keySet().iterator();
	while (paramIter.hasNext()) {
		String paramName = paramIter.next();
		String paramValues = paramMap.get(paramName);

		/**
		 * EQ_bizTransaction_type: This is not a single parameter, but a
		 * family of parameters. If a parameter of this form is
		 * specified, the result will only include events that (a)
		 * include a bizTransactionList; (b) where the business
		 * transaction list includes an entry whose type subfield is
		 * equal to type extracted from the name of this parameter; and
		 * (c) where the bizTransaction subfield of that entry is equal
		 * to one of the values specified in this parameter.
		 */
		if (paramName.contains("EQ_bizTransaction_")) {
			String type = paramName.substring(18, paramName.length());
			List<String> subStringList=MysqlQueryUtil.getStringList(paramValues);
			criteria.createAlias("bizTransactionList.bizTransaction", "bt");
			criteria.add(Restrictions.and(
					Restrictions.like("bt.type", type),
					Restrictions.in("bt.value", subStringList)));
		}

		/**
		 * EQ_source_type: This is not a single parameter, but a family
		 * of parameters. If a parameter of this form is specified, the
		 * result will only include events that (a) include a
		 * sourceList; (b) where the source list includes an entry whose
		 * type subfield is equal to type extracted from the name of
		 * this parameter; and (c) where the source subfield of that
		 * entry is equal to one of the values specified in this
		 * parameter.
		 */

		if (paramName.contains("EQ_source_")) {
			String type = paramName.substring(10, paramName.length());
			List<String> subStringList=MysqlQueryUtil.getStringList(paramValues);
			if(eventType.equals("ObjectEvent")||
					eventType.equals("TransactionEvent")||
					eventType.equals("AggregationEvent")){
				criteria.createAlias("extension.sourceList.source", "ex_sl");
				criteria.add(Restrictions.and(
						Restrictions.like("ex_sl.type", type),
						Restrictions.in("ex_sl.value", subStringList)));
			}
			if(eventType.equals("TransformationEvent")){
				criteria.createAlias("sourceList.source", "sl");
				criteria.add(Restrictions.and(
						Restrictions.like("sl.type", type),
						Restrictions.in("sl.value", subStringList)));
			}
			

		}

		/**
		 * EQ_destination_type: This is not a single parameter, but a
		 * family of parameters. If a parameter of this form is
		 * specified, the result will only include events that (a)
		 * include a destinationList; (b) where the destination list
		 * includes an entry whose type subfield is equal to type
		 * extracted from the name of this parameter; and (c) where the
		 * destination subfield of that entry is equal to one of the
		 * values specified in this parameter.
		 */
		if (paramName.contains("EQ_destination_")) {
			String type = paramName.substring(15, paramName.length());
			List<String> subStringList=MysqlQueryUtil.getStringList(paramValues);
			if(eventType.equals("ObjectEvent")||
					eventType.equals("TransactionEvent")||
					eventType.equals("AggregationEvent")){
				criteria.createAlias("extension.destinationList.destination", "ex_dl");
				criteria.add(Restrictions.and(
						Restrictions.like("ex_dl.type", type),
						Restrictions.in("ex_dl.value", subStringList)));
			}
			if(eventType.equals("TransformationEvent")){
				criteria.createAlias("destinationList.destination", "dl");
				criteria.add(Restrictions.and(
						Restrictions.like("dl.type", type),
						Restrictions.in("dl.value", subStringList)));
			}

		}
		boolean isExtraParam = isExtraParameter(paramName);

		if (isExtraParam == true) {

			/**
			 * EQ_fieldname: This is not a single parameter, but a
			 * family of parameters. If a parameter of this form is
			 * specified, the result will only include events that (a)
			 * have a field named fieldname whose type is either String
			 * or a vocabulary type; and where (b) the value of that
			 * field matches one of the values specified in this
			 * parameter. Fieldname is the fully qualified name of an
			 * extension field. The name of an extension field is an XML
			 * qname; that is, a pair consisting of an XML namespace URI
			 * and a name. The name of the corresponding query parameter
			 * is constructed by concatenating the following: the string
			 * EQ_, the namespace URI for the extension field, a pound
			 * sign (#), and the name of the extension field.
			 */
			if (paramName.startsWith("EQ_")&&!paramName.startsWith("EQ_INNER_")&&!paramName.startsWith("EQ_ILMD_")&&!paramName.startsWith("EQ_ERROR_")) {
				String qName = paramName
						.substring(3, paramName.length());
				List<String> value=MysqlQueryUtil.getStringList(paramValues);
				
				qName=qName.replaceAll("!", "#");
				List<PairType> ValueTypeList=new ArrayList<PairType>();
				ReaderUtility.getValueTypeList(ValueTypeList,value);
				criteria.createAlias("extensionMaps.extensionMapList", "extMapList");
				ReaderUtility.extensionHierarchCriteria_EQ(criteria,ValueTypeList,qName,"extMapList",false);
				
				
				if (eventType.equals("AggregationEvent")
						|| eventType.equals("ObjectEvent")
						|| eventType.equals("TransactionEvent")) {
//					Configuration.logger.info("paramName: " +paramName);
//					criteria.createAlias("extension.extension.mapExt", "ext");
//					criteria.add(Restrictions.and(
//							Restrictions.like("ext.type", type),
//							Restrictions.in("ext.value", value)));
				}
				if (eventType.equals("QuantityEvent")
						|| eventType.equals("TransformationEvent")) {
//					criteria.createAlias("extension.mapExt", "ext");
//					criteria.add(Restrictions.and(
//							Restrictions.like("ext.type", type),
//							Restrictions.in("ext.value", value)));
				}
			}

			/**
			 * GT/GE/LT/LE_fieldname: Like EQ_fieldname as described
			 * above, but may be applied to a field of type Int, Float,
			 * or Time. The result will include events that (a) have a
			 * field named fieldname; and where (b) the type of the
			 * field matches the type of this parameter (Int, Float, or
			 * Time); and where (c) the value of the field is greater
			 * than the specified value. Fieldname is constructed as for
			 * EQ_fieldname.
			 */

			if (paramName.startsWith("GT_")
					|| paramName.startsWith("GE_")
					|| paramName.startsWith("LT_")
					|| paramName.startsWith("LE_")) {
				
				if(!paramName.startsWith("GT_INNER_")&&!paramName.startsWith("GE_INNER_")&&
						!paramName.startsWith("LT_INNER_")&&!paramName.startsWith("LE_INNER_")&&
						!paramName.startsWith("GT_ILMD_")&&!paramName.startsWith("GE_ILMD_")&&
						!paramName.startsWith("LT_ILMD_")&&!paramName.startsWith("LE_ILMD_")&&
						!paramName.startsWith("GT_ERROR_")&&!paramName.startsWith("GE_ERROR_")&&
						!paramName.startsWith("LT_ERROR_")&&!paramName.startsWith("LE_ERROR_")){
					String qName = paramName
							.substring(3, paramName.length());
					List<String> value=MysqlQueryUtil.getStringList(paramValues);
					
					qName=qName.replaceAll("!", "#");
					List<PairType> ValueTypeList=new ArrayList<PairType>();
					ReaderUtility.getValueTypeList(ValueTypeList,value);
					criteria.createAlias("extensionMaps.extensionMapList", "extMapListNeq");
					//ReaderUtility.extensionHierarchEQ_Criteria(criteria,ValueTypeList,qName,"extMapList",false);
					
					if (paramName.startsWith("GT_")) {
						ReaderUtility.extensionHierarchCriteria_GT(criteria,ValueTypeList,qName,"extMapListNeq",false);
					}else if (paramName.startsWith("GE_")){
						ReaderUtility.extensionHierarchCriteria_GE(criteria,ValueTypeList,qName,"extMapListNeq",false);
					}else if (paramName.startsWith("LT_")){
						ReaderUtility.extensionHierarchCriteria_LT(criteria,ValueTypeList,qName,"extMapListNeq",false);
					}else if (paramName.startsWith("LE_")){
						ReaderUtility.extensionHierarchCriteria_LE(criteria,ValueTypeList,qName,"extMapListNeq",false);
					}
				}
				
				
			
			}
		if (paramName.startsWith("EQ_ILMD_")){

			String qName = paramName
					.substring(8, paramName.length());
			List<String> value=MysqlQueryUtil.getStringList(paramValues);
			
			qName=qName.replaceAll("!", "#");
			List<PairType> ValueTypeList=new ArrayList<PairType>();
			ReaderUtility.getValueTypeList(ValueTypeList,value);
			if (eventType.equals("TransformationEvent")
					|| eventType.equals("ObjectEvent")) {
				if(eventType.equals("TransformationEvent")){
					//ilmd
					criteria.createAlias("ilmd.extensionMaps", "ilextmapeq");
					criteria.createAlias("ilextmapeq.extensionMapList", "ilextmapeqList");
					ReaderUtility.extensionHierarchCriteria_EQ(criteria,ValueTypeList,qName,"ilextmapeqList",false);
				}else if(eventType.equals("ObjectEvent")){
					
					criteria.createAlias("extension.ilmd", "exIleqilmd");
					criteria.createAlias("exIleqilmd.extensionMaps", "extMapeqilmd");
					criteria.createAlias("extMapeqilmd.extensionMapList", "extMapListeqilmd");
					
					//ilmdCriteria(Criteria criteria, List<PairType> ValueTypeList, String qName, String alias,boolean isInner){
					ReaderUtility.extensionHierarchCriteria_EQ(criteria,ValueTypeList,qName,"extMapListeqilmd",false);
				}
			}
						
		}
		
		
		/**
		 * Analogous to EQ_fieldname , GT_fieldname , GE_fieldname ,
		 * GE_fieldname , LT_fieldname , and LE_fieldname , respectively,
		 * but matches events whose ILMD area (Section 7.3.6) contains a field
		 * having the specified fieldname whose integer, float, or time value
		 * matches the specified value according to the specified relational operator.
		 */
		if (paramName.startsWith("GT_ILMD_")
				|| paramName.startsWith("GE_ILMD_")
				|| paramName.startsWith("LT_ILMD_")
				|| paramName.startsWith("LE_ILMD_")) {
			String qName = paramName
					.substring(8, paramName.length());
			List<String> value=MysqlQueryUtil.getStringList(paramValues);
			
			qName=qName.replaceAll("!", "#");
			List<PairType> ValueTypeList=new ArrayList<PairType>();
			ReaderUtility.getValueTypeList(ValueTypeList,value);
			if (eventType.equals("TransformationEvent")
					|| eventType.equals("ObjectEvent")) {
				if(eventType.equals("TransformationEvent")){
					//ilmd
					criteria.createAlias("ilmd.extensionMaps", "ilextmapeq");
					criteria.createAlias("ilextmapeq.extensionMapList", "ilextmapNeqList");
					ReaderUtility.extensionHierarchCriteria_EQ(criteria,ValueTypeList,qName,"ilextmapNeqList",false);
				}else if(eventType.equals("ObjectEvent")){
					
					criteria.createAlias("extension.ilmd", "exIleqilmd");
					criteria.createAlias("exIleqilmd.extensionMaps", "extMapeqilmd");
					criteria.createAlias("extMapeqilmd.extensionMapList", "ilextmapNeqList");
					
				
				}
				
				if (paramName.startsWith("GT_")){
					ReaderUtility.extensionHierarchCriteria_GT(criteria,ValueTypeList,qName,"ilextmapNeqList",false);
				}else if (paramName.startsWith("GE_")){
					ReaderUtility.extensionHierarchCriteria_GE(criteria,ValueTypeList,qName,"ilextmapNeqList",false);
				}else if (paramName.startsWith("LT_")){
					ReaderUtility.extensionHierarchCriteria_LT(criteria,ValueTypeList,qName,"ilextmapNeqList",false);
				}else if (paramName.startsWith("LE_")){
					ReaderUtility.extensionHierarchCriteria_LE(criteria,ValueTypeList,qName,"ilextmapNeqList",false);
				}
			}
			
			
		}
		/**
		 * Analogous to EQ_fieldname , but matches inner extension elements;
		 * that is, any XML element nested within a top-level extension
		 * element. Note that a matching inner element may exist within in
		 * more than one top-level element or may occur more than once
		 * within a single top-level element; this parameter matches if at
		 * least one matching occurrence is found anywhere in the event
		 * (except at top-level).
		 */

		if (paramName.startsWith("EQ_INNER_")&&
				!paramName.startsWith("EQ_INNER_ILMD_")&&
				!paramName.startsWith("EQ_INNER_ERROR_")) {
			String qName = paramName
					.substring(9, paramName.length());
			List<String> value=MysqlQueryUtil.getStringList(paramValues);
			
			qName=qName.replaceAll("!", "#");
			List<PairType> ValueTypeList=new ArrayList<PairType>();
			ReaderUtility.getValueTypeList(ValueTypeList,value);
			criteria.createAlias("extensionMaps.extensionMapList", "extMapEqInList");
			ReaderUtility.extensionHierarchCriteria_EQ(criteria,ValueTypeList,qName,"extMapEqInList",true);
			
			
		}
		
		/**
		 * Like EQ_INNER _ fieldname as described above, but may be applied to
		 * a field of type Int, Float, or Time.
		 */
		if (paramName.startsWith("GT_INNER_")
				|| paramName.startsWith("GE_INNER_")
				|| paramName.startsWith("LT_INNER_")
				|| paramName.startsWith("LE_INNER_")) {
			
			if(!paramName.startsWith("GT_INNER_ILMD_")&& !paramName.startsWith("GE_INNER_ILMD_")
				&& !paramName.startsWith("LT_INNER_ILMD_")&& !paramName.startsWith("LE_INNER_ILMD_")&&
				!paramName.startsWith("GT_INNER_ERROR_")&& !paramName.startsWith("GE_INNER_ERROR_")
				&& !paramName.startsWith("LT_INNER_ERROR_")&& !paramName.startsWith("LE_INNER_ERROR_")){
				
				String qName = paramName
						.substring(9, paramName.length());
				List<String> value=MysqlQueryUtil.getStringList(paramValues);
				
				qName=qName.replaceAll("!", "#");
				List<PairType> ValueTypeList=new ArrayList<PairType>();
				ReaderUtility.getValueTypeList(ValueTypeList,value);
				criteria.createAlias("extensionMaps.extensionMapList", "extMapNeqInList");
				
				if (paramName.startsWith("GT_INNER_")) {
					ReaderUtility.extensionHierarchCriteria_GT(criteria,ValueTypeList,qName,"extMapNeqInList",true);
				}else if (paramName.startsWith("GE_INNER_")){
					ReaderUtility.extensionHierarchCriteria_GE(criteria,ValueTypeList,qName,"extMapNeqInList",true);
				}else if (paramName.startsWith("LT_INNER_")){
					ReaderUtility.extensionHierarchCriteria_LT(criteria,ValueTypeList,qName,"extMapNeqInList",true);
				}else if (paramName.startsWith("LE_INNER_")){
					ReaderUtility.extensionHierarchCriteria_LE(criteria,ValueTypeList,qName,"extMapNeqInList",true);
				}
			}
			
		}
		
		/**
		 * Analogous to EQ_ILMD_fieldname , but matches inner ILMD elements; that is, any XML element nested at 
		 * any level within a top-level ILMD element. Note that a matching inner element may exist within more 
		 * than one top-level element or may occur more than once within a single top-level element; this parameter 
		 * matches if at least one matching occurrence is found anywhere in the ILMD section (except at top-level).
		 */
		if (paramName.startsWith("EQ_INNER_ILMD_")) {
			String qName = paramName
					.substring(14, paramName.length());
			List<String> value=MysqlQueryUtil.getStringList(paramValues);
			
			qName=qName.replaceAll("!", "#");
			List<PairType> ValueTypeList=new ArrayList<PairType>();
			ReaderUtility.getValueTypeList(ValueTypeList,value);
			if (eventType.equals("TransformationEvent")
					|| eventType.equals("ObjectEvent")) {
				if(eventType.equals("TransformationEvent")){
					//ilmd
					criteria.createAlias("ilmd.extensionMaps", "ilextmapeqInn");
					criteria.createAlias("ilextmapeqInn.extensionMapList", "ilextmapeqInnList");
					ReaderUtility.extensionHierarchCriteria_EQ(criteria,ValueTypeList,qName,"ilextmapeqInnList",true);
				}else if(eventType.equals("ObjectEvent")){
					
					criteria.createAlias("extension.ilmd", "exIleqInnilmd");
					criteria.createAlias("exIleqInnilmd.extensionMaps", "extMapeqInnilmd");
					criteria.createAlias("extMapeqInnilmd.extensionMapList", "extMapListeqInnilmd");
					
					//ilmdCriteria(Criteria criteria, List<PairType> ValueTypeList, String qName, String alias,boolean isInner){
					ReaderUtility.extensionHierarchCriteria_EQ(criteria,ValueTypeList,qName,"extMapListeqInnilmd",true);
				}
			}
			
			
		}
		/**
		 * Like EQ_INNER _ ILMD_ fieldname as described above, but may be
		 * applied to a field of type Int, Float, or Time.
		 */
		if(paramName.startsWith("GT_INNER_ILMD_")
				|| paramName.startsWith("GE_INNER_ILMD_")
				|| paramName.startsWith("LT_INNER_ILMD_")
				|| paramName.startsWith("LE_INNER_ILMD_")){
			
			String qName = paramName
					.substring(14, paramName.length());
			List<String> value=MysqlQueryUtil.getStringList(paramValues);
			
			qName=qName.replaceAll("!", "#");
			List<PairType> ValueTypeList=new ArrayList<PairType>();
			ReaderUtility.getValueTypeList(ValueTypeList,value);
			criteria.createAlias("extensionMaps.extensionMapList", "extMapNeqInnInList");
			String alias="";
			if (eventType.equals("TransformationEvent")
					|| eventType.equals("ObjectEvent")) {
				if(eventType.equals("TransformationEvent")){
					//ilmd
					criteria.createAlias("ilmd.extensionMaps", "ilextmapNeqInnIl");
					criteria.createAlias("ilextmapNeqInnIl.extensionMapList", "ilextmapNeqInnIlList");
					alias="ilextmapNeqInnIlList";
				}else if(eventType.equals("ObjectEvent")){
					
					criteria.createAlias("extension.ilmd", "exIlNeqInnilmd");
					criteria.createAlias("exIlNeqInnilmd.extensionMaps", "extMapNeqInnilmd");
					criteria.createAlias("extMapNeqInnilmd.extensionMapList", "extMapListNeqInnilmd");
					alias="extMapListNeqInnilmd";
				}
				
				if (paramName.startsWith("GT_INNER_ILMD_")) {
					ReaderUtility.extensionHierarchCriteria_GT(criteria,ValueTypeList,qName,alias,true);
				}else if (paramName.startsWith("GE_INNER_ILMD_")){
					ReaderUtility.extensionHierarchCriteria_GE(criteria,ValueTypeList,qName,alias,true);
				}else if (paramName.startsWith("LT_INNER_ILMD_")){
					ReaderUtility.extensionHierarchCriteria_LT(criteria,ValueTypeList,qName,alias,true);
				}else if (paramName.startsWith("LE_INNER_ILMD_")){
					ReaderUtility.extensionHierarchCriteria_LE(criteria,ValueTypeList,qName,alias,true);
				}
			}
			
			
		
		}
		
		
		
		/**
		 * Like EQ_fieldname as described above, but may be applied to a field
		 * of any type (including complex types). The result will include events
		 * that have a non-empty field named fieldname .Fieldname is constructed 
		 * as for EQ_fieldname .Note that the value for this query parameter is ignored.
		 */
		
		if(paramName.startsWith("EXISTS_")){
			if(!paramName.startsWith("EXISTS_INNER")&&
					!paramName.startsWith("EXISTS_ILMD_")&&
					!paramName.startsWith("EXISTS_errorDeclaration")&&
					!paramName.startsWith("EXISTS_ERROR")){
				String qName = paramName
						.substring(7, paramName.length());
				
				qName=qName.replaceAll("!", "#");
				criteria.createAlias("extensionMaps.extensionMapList", "extMapListExist");
				ReaderUtility.extensionHierarchCriteria_EQ_qName(criteria,qName,"extMapListExist",false);
			}
		}
		/**
		 * Like EXISTS_fieldname as described above, but includes events that 
		 * have a non-empty inner extension field named fieldname .
		 * Note that the value for this query parameter is ignored.
		 */
		if(paramName.startsWith("EXISTS_INNER_")){
			if(!paramName.startsWith("EXISTS_INNER_ILMD_")&&
					!paramName.startsWith("EXISTS_INNER_ERROR_")){
				String qName = paramName
						.substring(13, paramName.length());
				
				qName=qName.replaceAll("!", "#");
				criteria.createAlias("extensionMaps.extensionMapList", "extMapListExistInner");
				ReaderUtility.extensionHierarchCriteria_EQ_qName(criteria,qName,"extMapListExistInner",true);
			}
		}
		
		/**
		 *Like EXISTS_fieldname as described above, but events that have a
		 *non-empty field named fieldname in the ILMD area (Section 7.3.6).
		 *Fieldname is constructed as for EQ_ILMD_fieldname .
		 *Note that the value for this query parameter is ignored. 
		 */
		if(paramName.startsWith("EXISTS_ILMD_")){
			String qName = paramName
					.substring(12, paramName.length());
			
			qName=qName.replaceAll("!", "#");
			
			
			String alias="";
			if (eventType.equals("TransformationEvent")
					|| eventType.equals("ObjectEvent")) {
				if(eventType.equals("TransformationEvent")){
					criteria.createAlias("ilmd.extensionMaps", "ilextmapExistIlmd");
					criteria.createAlias("ilextmapExistIlmd.extensionMapList", "ilextmapExistIlmdList");
					alias="ilextmapExistIlmdList";
				}else if(eventType.equals("ObjectEvent")){
					
					criteria.createAlias("extension.ilmd", "exIlExistIlmd");
					criteria.createAlias("exIlExistIlmd.extensionMaps", "extMapExistIlmd");
					criteria.createAlias("extMapExistIlmd.extensionMapList", "extMapListExistIlmd");
					alias="extMapListExistIlmd";
				}
				ReaderUtility.extensionHierarchCriteria_EQ_qName(criteria,qName,alias,false);
			}
			
		}
		/**
		 * Like EXISTS_ILMD_fieldname as described above, but includes
		 * events that have a non-empty inner extension field named fieldname
		 * within the ILMD area.
		 * Note that the value for this query parameter is ignored.
		 */
		if(paramName.startsWith("EXISTS_INNER_ILMD_")){
			String qName = paramName
					.substring(18, paramName.length());
			
			qName=qName.replaceAll("!", "#");
			
			
			String alias="";
			if (eventType.equals("TransformationEvent")
					|| eventType.equals("ObjectEvent")) {
				if(eventType.equals("TransformationEvent")){
					criteria.createAlias("ilmd.extensionMaps", "ilextmapExistInnIlmd");
					criteria.createAlias("ilextmapExistInnIlmd.extensionMapList", "ilextmapExistInnIlmdList");
					alias="ilextmapExistInnIlmdList";
				}else if(eventType.equals("ObjectEvent")){
					
					criteria.createAlias("extension.ilmd", "exIlExistInnIlmd");
					criteria.createAlias("exIlExistInnIlmd.extensionMaps", "extMapExistInnIlmd");
					criteria.createAlias("extMapExistInnIlmd.extensionMapList", "extMapListExistInnIlmd");
					alias="extMapListExistInnIlmd";
				}
				ReaderUtility.extensionHierarchCriteria_EQ_qName(criteria,qName,alias,true);
			}
			
		}
		/**
		 * This is not a single parameter, but a family of parameters.If a parameter of this form is specified, 
		 * the result will only include events that (a) have a field named fieldname whose type is a vocabulary 
		 * type; and (b) where the value of that field is a vocabulary element for which master data is available; 
		 * and (c) the master data has a non-null attribute whose name matches one of the values specified in this 
		 * parameter. Fieldname is the fully qualified name of a field. For a standard field, this is simply the 
		 * field name; e.g., bizLocation . For an extension field, the name of an extension field is an XML qname; 
		 * that is, a pair consisting of an XML namespace URI and a name. The name of the corresponding query 
		 * parameter is constructed by concatenating the following: the string HASATTR_ , the namespace URI for 
		 * the extension field, a pound sign (#), and the name of the extension field.
		 */
		
		
		if (paramName.startsWith("HASATTR_")){
			
		}
		
		/**
		 * Analogous to EQ_fieldname , but matches events containing an 
		 * ErrorDeclaration and where the ErrorDeclaration contains a
		 * field having the specified fieldname whose value matches one of the
		 * specified values.
		 */
		if(paramName.startsWith("EQ_ERROR_DECLARATION_")){
			String qName = paramName
					.substring(21, paramName.length());
			
			List<String> value=MysqlQueryUtil.getStringList(paramValues);
			
			qName=qName.replaceAll("!", "#");
			List<PairType> ValueTypeList=new ArrayList<PairType>();
			ReaderUtility.getValueTypeList(ValueTypeList,value);
			
			criteria.createAlias("baseExtension", "baExtEventIDeqErDec");
			criteria.createAlias("baExtEventIDeqErDec.errorDeclaration", "baExtEventIDDeceqErDec");
			criteria.createAlias("baExtEventIDDeceqErDec.extensionMaps", "extMapsEqErDec");			
			criteria.createAlias("extMapsEqErDec.extensionMapList", "extMapsListEqErDec");
			ReaderUtility.extensionHierarchCriteria_EQ(criteria,ValueTypeList,qName,"extMapsListEqErDec",false);
		}
		/**
		 * Analogous to EQ_fieldname , GT_fieldname , GE_fieldname ,
		 * GE_fieldname , LT_fieldname , and LE_fieldname , respectively,
		 * but matches events containing an ErrorDeclaration and where the
		 * ErrorDeclaration contains a field having the specified fieldname
		 * whose integer, float, or time value matches the specified value
		 * according to the specified relational operator.
		 */
		if(paramName.startsWith("GT_ERROR_DECLARATION_")||
				paramName.startsWith("GE_ERROR_DECLARATION_")||
				paramName.startsWith("LT_ERROR_DECLARATION_")||
				paramName.startsWith("LE_ERROR_DECLARATION_")){
			
			String qName = paramName
					.substring(21, paramName.length());
			List<String> value=MysqlQueryUtil.getStringList(paramValues);
			
			qName=qName.replaceAll("!", "#");
			List<PairType> ValueTypeList=new ArrayList<PairType>();
			ReaderUtility.getValueTypeList(ValueTypeList,value);
			
			criteria.createAlias("baseExtension", "baExtEventIDNeqErDec");
			criteria.createAlias("baExtEventIDNeqErDec.errorDeclaration", "baExtEventIDDecNeqErDec");
			criteria.createAlias("baExtEventIDDecNeqErDec.extensionMaps", "extMapsNeqErDec");			
			criteria.createAlias("extMapsNeqErDec.extensionMapList", "extMapsListNeqErDec");
			
			if (paramName.startsWith("GT_")) {
				ReaderUtility.extensionHierarchCriteria_GT(criteria,ValueTypeList,qName,"extMapsListNeqErDec",false);
			}else if (paramName.startsWith("GE_")){
				ReaderUtility.extensionHierarchCriteria_GE(criteria,ValueTypeList,qName,"extMapsListNeqErDec",false);
			}else if (paramName.startsWith("LT_")){
				ReaderUtility.extensionHierarchCriteria_LT(criteria,ValueTypeList,qName,"extMapsListNeqErDec",false);
			}else if (paramName.startsWith("LE_")){
				ReaderUtility.extensionHierarchCriteria_LE(criteria,ValueTypeList,qName,"extMapsListNeqErDec",false);
			}
		}
		/**
		 * Analogous to EQ_ERROR_DECLARATION_fieldname , but matches
		 * inner extension elements; that is, any XML element nested within a
		 * top-level extension element. Note that a matching inner element may
		 * exist within more than one top-level element or may occur more than
		 * once within a single top-level element; this parameter matches if at
		 * least one matching occurrence is found anywhere in the event (except at top-level)..
		 */
		if(paramName.startsWith("EQ_INNER_ERROR_DECLARATION_")){
			String qName = paramName
					.substring(27, paramName.length());
			
			List<String> value=MysqlQueryUtil.getStringList(paramValues);
			
			qName=qName.replaceAll("!", "#");
			List<PairType> ValueTypeList=new ArrayList<PairType>();
			ReaderUtility.getValueTypeList(ValueTypeList,value);
			
			criteria.createAlias("baseExtension", "baExtEventIDeqInnErDec");
			criteria.createAlias("baExtEventIDeqInnErDec.errorDeclaration", "baExtEventIDDeceqInnErDec");
			criteria.createAlias("baExtEventIDDeceqInnErDec.extensionMaps", "extMapsEqInnErDec");			
			criteria.createAlias("extMapsEqInnErDec.extensionMapList", "extMapsListEqInnErDec");
			ReaderUtility.extensionHierarchCriteria_EQ(criteria,ValueTypeList,qName,"extMapsListEqInnErDec",true);
		}
		
		/**
		 * Like EQ_INNER_ERROR_DECLARATION _ fieldname as described
		 * above, but may be applied to a field of type Int, Float, or Time.
		 */
		if(paramName.startsWith("GT_INNER_ERROR_DECLARATION_")||
				paramName.startsWith("GE_INNER_ERROR_DECLARATION_")||
				paramName.startsWith("LT_INNER_ERROR_DECLARATION_")||
				paramName.startsWith("LE_INNER_ERROR_DECLARATION_")){
			String qName = paramName
					.substring(27, paramName.length());
			
			List<String> value=MysqlQueryUtil.getStringList(paramValues);
			
			qName=qName.replaceAll("!", "#");
			List<PairType> ValueTypeList=new ArrayList<PairType>();
			ReaderUtility.getValueTypeList(ValueTypeList,value);
			
			criteria.createAlias("baseExtension", "baExtEventIDNeqInnErDec");
			criteria.createAlias("baExtEventIDNeqInnErDec.errorDeclaration", "baExtEventIDDecNeqInnErDec");
			criteria.createAlias("baExtEventIDDecNeqInnErDec.extensionMaps", "extMapsNeqInnErDec");			
			criteria.createAlias("extMapsNeqInnErDec.extensionMapList", "extMapsListNeqInnErDec");
			
			if (paramName.startsWith("GT_")) {
				ReaderUtility.extensionHierarchCriteria_GT(criteria,ValueTypeList,qName,"extMapsListNeqInnErDec",true);
			}else if (paramName.startsWith("GE_")){
				ReaderUtility.extensionHierarchCriteria_GE(criteria,ValueTypeList,qName,"extMapsListNeqInnErDec",true);
			}else if (paramName.startsWith("LT_")){
				ReaderUtility.extensionHierarchCriteria_LT(criteria,ValueTypeList,qName,"extMapsListNeqInnErDec",true);
			}else if (paramName.startsWith("LE_")){
				ReaderUtility.extensionHierarchCriteria_LE(criteria,ValueTypeList,qName,"extMapsListNeqInnErDec",true);
			}
		}
		/**
		 * Like EXISTS_fieldname as described above, but events that have an
		 * error declaration containing a non-empty extension field named
		 * fieldname .
		 * Fieldname is constructed as for EQ_ERROR_DECLARATION_fieldname .
		 * Note that the value for this query parameter is ignored
		 */
		if(paramName.startsWith("EXISTS_ERROR_DECLARATION_")){
			String qName = paramName
					.substring(25, paramName.length());
			
			List<String> value=MysqlQueryUtil.getStringList(paramValues);
			
			qName=qName.replaceAll("!", "#");
			List<PairType> ValueTypeList=new ArrayList<PairType>();
			ReaderUtility.getValueTypeList(ValueTypeList,value);
			
			criteria.createAlias("baseExtension", "baExtEventIDexiErDec");
			criteria.createAlias("baExtEventIDexiErDec.errorDeclaration", "baExtEventIDDecExiErDec");
			criteria.createAlias("baExtEventIDDecExiErDec.extensionMaps", "extMapsExiErDec");			
			criteria.createAlias("extMapsExiErDec.extensionMapList", "extMapsListExiErDec");
			ReaderUtility.extensionHierarchCriteria_EQ_qName(criteria,qName,"extMapsListExiErDec",false);
		}
		/**
		 * Like EXISTS_ERROR_DECLARATION_fieldname as described above,
		 * but includes events that have an error declaration containing a non-
		 * empty inner extension field named fieldname .
		 * Note that the value for this query parameter is ignored.
		 */
		if(paramName.startsWith("EXISTS_INNER_ERROR_DECLARATION_")){
			String qName = paramName
					.substring(31, paramName.length());
			
			List<String> value=MysqlQueryUtil.getStringList(paramValues);
			
			qName=qName.replaceAll("!", "#");
			List<PairType> ValueTypeList=new ArrayList<PairType>();
			ReaderUtility.getValueTypeList(ValueTypeList,value);
			
			criteria.createAlias("baseExtension", "baExtEventIDexiINNErDec");
			criteria.createAlias("baExtEventIDexiINNErDec.errorDeclaration", "baExtEventIDDecExiINNErDec");
			criteria.createAlias("baExtEventIDDecExiINNErDec.extensionMaps", "extMapsExiINNErDec");			
			criteria.createAlias("extMapsExiINNErDec.extensionMapList", "extMapsListExiINNErDec");
			ReaderUtility.extensionHierarchCriteria_EQ_qName(criteria,qName,"extMapsListExiINNErDec",true);
		}
		
		
			
		//---------------------------	
		}//end of if isExtraParam
	}	//end of while loop
		/**
		 * orderBy : If specified, names a single field that will be used to
		 * order the results. The orderDirection field specifies whether the
		 * ordering is in ascending sequence or descending sequence. Events
		 * included in the result that lack the specified field altogether may
		 * occur in any position within the result event list. The value of this
		 * parameter SHALL be one of: eventTime, recordTime, or the fully
		 * qualified name of an extension field whose type is Int, Float, Time,
		 * or String. A fully qualified fieldname is constructed as for the
		 * EQ_fieldname parameter. In the case of a field of type String, the
		 * ordering SHOULD be in lexicographic order based on the Unicode
		 * encoding of the strings, or in some other collating sequence
		 * appropriate to the locale. If omitted, no order is specified. The
		 * implementation MAY order the results in any order it chooses, and
		 * that order MAY differ even when the same query is executed twice on
		 * the same data. (In EPCIS 1.0, the value quantity was also permitted,
		 * but its use is deprecated in EPCIS 1.1.)
		 * 
		 * orderDirection : If specified and orderBy is also specified,
		 * specifies whether the results are ordered in ascending or descending
		 * sequence according to the key specified by orderBy. The value of this
		 * parameter must be one of ASC (for ascending order) or DESC (for
		 * descending order); if not, the implementation SHALL raise a
		 * QueryParameterException. If omitted, defaults to DESC.
		 */
			//String orderBy, String orderDirection, String eventCountLimit,
			//String maxEventCount,
	if(orderBy!=null){
		if(orderDirection!=null){
			if(orderDirection.equals("ASC")){
				criteria.addOrder(Order.asc(orderBy));
			}else if(orderDirection.equals("DESC")){
				criteria.addOrder(Order.desc(orderBy));
			}
			
		}
		else if(orderDirection==null){
			criteria.addOrder(Order.desc(orderBy));
		}
	 }
	if(eventCountLimit!=null){
		//int countLimit=Integer.parseInt(eventCountLimit);
		//criteria.setMaxResults(countLimit);
	}
		
	
} catch (ParseException e) {
//	Configuration.logger.log(Level.ERROR, e.toString());
}
return criteria;
}


boolean isExtraParameter(String paramName) {

	if (paramName.contains("eventTime"))
		return false;
	if (paramName.contains("recordTime"))
		return false;
	if (paramName.contains("action"))
		return false;
	if (paramName.contains("bizStep"))
		return false;
	if (paramName.contains("disposition"))
		return false;
	if (paramName.contains("readPoint"))
		return false;
	if (paramName.contains("bizLocation"))
		return false;
	if (paramName.contains("bizTransaction"))
		return false;
	if (paramName.contains("source"))
		return false;
	if (paramName.contains("destination"))
		return false;
	if (paramName.contains("transformationID"))
		return false;
	if (paramName.contains("eventID"))
		return false;
	if (paramName.contains("errorReason"))
		return false;
	if (paramName.contains("errorDeclarationTime"))
		return false;
	if (paramName.contains("correctiveEventID"))
		return false;
	
	return true;
}
}
