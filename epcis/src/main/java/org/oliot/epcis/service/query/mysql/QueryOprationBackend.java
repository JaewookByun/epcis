package org.oliot.epcis.service.query.mysql;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.oliot.model.epcis.SubscriptionType;
import org.oliot.model.oliot.Action;
import org.oliot.model.oliot.AggregationEvent;
import org.oliot.model.oliot.ObjectEvent;
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
		SubscriptionType Subscription=new SubscriptionType(subscription.getQueryName(), subscription.getSubscriptionID(),
				subscription.getDest(), subscription.getCronExpression(),subscription.isReportIfEmpty(), subscription.getEventType(),
				subscription.getEventType(),subscription.getGE_eventTime(), subscription.getLT_eventTime(), subscription.getGE_recordTime(),
				subscription.getLT_recordTime(), subscription.getEQ_action(), subscription.getEQ_bizStep(),
				subscription.getEQ_disposition(), subscription.getEQ_readPoint(), subscription.getWD_readPoint(),
				subscription.getEQ_bizLocation(), subscription.getWD_bizLocation(),
				subscription.getEQ_transformationID(), subscription.getMATCH_epc(),
				subscription.getMATCH_parentID(), subscription.getMATCH_inputEPC(),
				subscription.getMATCH_outputEPC(), subscription.getMATCH_anyEPC(), subscription.getMATCH_epcClass(),
				subscription.getMATCH_inputEPCClass(), subscription.getMATCH_outputEPCClass(),
				subscription.getMATCH_anyEPCClass(), subscription.getEQ_quantity(), subscription.getGT_quantity(),
				subscription.getGE_quantity(), subscription.getLT_quantity(), subscription.getLE_quantity(),
				subscription.getOrderBy(), subscription.getOrderDirection(), subscription.getEventCountLimit(),
				subscription.getMaxEventCount(), subscription.getParamMap());
		
		return Subscription;
	}
	
	public Subscription convertFromSubscriptionType(SubscriptionType subscriptionType){
		Subscription Subscription=new Subscription();
		Subscription.setQueryName(subscriptionType.getQueryName());
		Subscription.setSubscriptionID(subscriptionType.getSubscriptionID());
		Subscription.setDest(subscriptionType.getDest());
		Subscription.setCronExpression(subscriptionType.getCronExpression());
		Subscription.setReportIfEmpty(subscriptionType.isReportIfEmpty());
		Subscription.setEventType(subscriptionType.getEventType());
		Subscription.setGE_eventTime(subscriptionType.getGE_eventTime());
		Subscription.setLT_eventTime (subscriptionType.getLT_eventTime());;
		Subscription.setGE_recordTime (subscriptionType.getGE_recordTime());
		Subscription.setLT_recordTime (subscriptionType.getLT_recordTime());
		Subscription.setEQ_action (subscriptionType.getEQ_action());
		Subscription.setEQ_bizStep (subscriptionType.getEQ_bizStep());
		Subscription.setEQ_disposition(subscriptionType.getEQ_disposition());
		Subscription.setEQ_readPoint(subscriptionType.getEQ_readPoint());
		Subscription.setWD_readPoint(subscriptionType.getWD_readPoint());
		Subscription.setEQ_bizLocation(subscriptionType.getEQ_bizLocation());
		Subscription.setWD_bizLocation(subscriptionType.getWD_bizLocation());
		Subscription.setEQ_transformationID(subscriptionType.getEQ_transformationID());
		Subscription.setMATCH_epc(subscriptionType.getMATCH_epc());
		Subscription.setMATCH_parentID(subscriptionType.getMATCH_parentID());
		Subscription.setMATCH_inputEPC(subscriptionType.getMATCH_inputEPC());
		Subscription.setMATCH_outputEPC(subscriptionType.getMATCH_outputEPC());
		Subscription.setMATCH_anyEPC(subscriptionType.getMATCH_anyEPC());
		Subscription.setMATCH_epcClass(subscriptionType.getMATCH_epcClass());
		Subscription.setMATCH_inputEPCClass(subscriptionType.getMATCH_inputEPCClass());
		Subscription.setMATCH_outputEPCClass (subscriptionType.getMATCH_outputEPCClass());
		Subscription.setMATCH_anyEPCClass (subscriptionType.getMATCH_anyEPCClass());
		Subscription.setEQ_quantity(subscriptionType.getEQ_quantity());
		Subscription.setGT_quantity(subscriptionType.getGT_quantity());
		Subscription.setGE_quantity(subscriptionType.getGE_quantity());
		Subscription.setLT_quantity(subscriptionType.getLT_quantity());
		Subscription.setLE_quantity(subscriptionType.getLE_quantity());
		Subscription.setOrderBy (subscriptionType.getOrderBy());
		Subscription.setOrderDirection (subscriptionType.getOrderDirection());
		Subscription.setEventCountLimit(subscriptionType.getEventCountLimit());
		Subscription.setMaxEventCount(subscriptionType.getMaxEventCount());
		
		return Subscription;
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
			if (paramName.startsWith("EQ_")) {
				String type = paramName
						.substring(3, paramName.length());
				List<String> value=MysqlQueryUtil.getStringList(paramValues);
				if (eventType.equals("AggregationEvent")
						|| eventType.equals("ObjectEvent")
						|| eventType.equals("TransactionEvent")) {
					criteria.createAlias("extension.extension.mapExt", "ext");
					criteria.add(Restrictions.and(
							Restrictions.like("ext.type", type),
							Restrictions.in("ext.value", value)));
				}
				if (eventType.equals("QuantityEvent")
						|| eventType.equals("TransformationEvent")) {
					criteria.createAlias("extension.mapExt", "ext");
					criteria.add(Restrictions.and(
							Restrictions.like("ext.type", type),
							Restrictions.in("ext.value", value)));
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
				String type = paramName
						.substring(3, paramName.length());
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				Date value=sdf.parse("2013-06-08T23:58:56.591-09:00");
				float value2=0;
				try{
					value2=Float.parseFloat(paramValues);
				}catch(Exception e){}
				try{
					value = sdf.parse(paramValues);
				}catch(Exception e){}
				
				
				if (eventType.equals("AggregationEvent")
						|| eventType.equals("ObjectEvent")
						|| eventType.equals("TransactionEvent")) {
					criteria.createAlias("extension.extension.mapExt", "ext");
				
					if (paramName.startsWith("GT_")) {
						criteria.add(Restrictions.or(
								Restrictions.and(
								Restrictions.like("ext.type", type),
								Restrictions.gt("ext.floatValue", value2)),
								Restrictions.and(
										Restrictions.like("ext.type", type),
										Restrictions.gt("ext.TimeValue", value))));
					}
					if (paramName.startsWith("GE_")) {
						criteria.add(Restrictions.or(
								Restrictions.and(
								Restrictions.like("ext.type", type),
								Restrictions.ge("ext.floatValue", value2)),
								Restrictions.and(
										Restrictions.like("ext.type", type),
										Restrictions.ge("ext.TimeValue", value))));
					}
					if (paramName.startsWith("LT_")) {
						criteria.add(Restrictions.or(
								Restrictions.and(
								Restrictions.like("ext.type", type),
								Restrictions.lt("ext.floatValue", value2)),
								Restrictions.and(
										Restrictions.like("ext.type", type),
										Restrictions.lt("ext.TimeValue", value))));;
					}
					if (paramName.startsWith("LE_")) {
						criteria.add(Restrictions.or(
								Restrictions.and(
								Restrictions.like("ext.type", type),
								Restrictions.le("ext.floatValue", value2)),
								Restrictions.and(
										Restrictions.like("ext.type", type),
										Restrictions.le("ext.TimeValue", value))));
					}
				}
				if (eventType.equals("QuantityEvent")
						|| eventType.equals("TransformationEvent")) {
					criteria.createAlias("extension.mapExt", "ext");
					if (paramName.startsWith("GT_")) {
						criteria.add(Restrictions.or(
								Restrictions.and(
								Restrictions.like("ext.type", type),
								Restrictions.gt("ext.floatValue", value2)),
								Restrictions.and(
										Restrictions.like("ext.type", type),
										Restrictions.gt("ext.TimeValue", value))));
					}
					if (paramName.startsWith("GE_")) {
					criteria.add(Restrictions.or(
							Restrictions.and(
							Restrictions.like("ext.type", type),
							Restrictions.ge("ext.floatValue", value2)),
							Restrictions.and(
									Restrictions.like("ext.type", type),
									Restrictions.ge("ext.TimeValue", value))));
					}
					if (paramName.startsWith("LT_")) {
					criteria.add(Restrictions.or(
							Restrictions.and(
							Restrictions.like("ext.type", type),
							Restrictions.lt("ext.floatValue", value2)),
							Restrictions.and(
									Restrictions.like("ext.type", type),
									Restrictions.lt("ext.TimeValue", value))));
					}
					if (paramName.startsWith("LE_")) {
						criteria.add(Restrictions.or(
								Restrictions.and(
								Restrictions.like("ext.type", type),
								Restrictions.le("ext.floatValue", value2)),
								Restrictions.and(
										Restrictions.like("ext.type", type),
										Restrictions.le("ext.TimeValue", value))));
					}
				}
			}
		if (paramName.startsWith("EQ_ILMD_")){
			if (eventType.equals("TransformationEvent")
					|| eventType.equals("ObjectEvent")){
				String type = paramName
						.substring(8, paramName.length());
				List<String> value=MysqlQueryUtil.getStringList(paramValues);
				criteria.createAlias("ilmd.mapExt", "mpext");
				criteria.add(Restrictions.and(
						Restrictions.like("mpext.type", type),
						Restrictions.in("mpext.value", value)));
			}
			
		}
		if (paramName.startsWith("GT_ILMD_")
				|| paramName.startsWith("GE_ILMD_")
				|| paramName.startsWith("LT_ILMD_")
				|| paramName.startsWith("LE_ILMD_")) {
			String type = paramName
					.substring(3, paramName.length());
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			Date value=sdf.parse("2013-06-08T23:58:56.591-09:00");
			float value2=0;
			try{
				value2=Float.parseFloat(paramValues);
			}catch(Exception e){}
			try{
				value = sdf.parse(paramValues);
			}catch(Exception e){}
			if (eventType.equals("TransformationEvent")
					|| eventType.equals("ObjectEvent")){
				criteria.createAlias("ilmd.mapExt", "mpext");
				if (paramName.startsWith("GT_")) {
					criteria.add(Restrictions.or(
							Restrictions.and(
							Restrictions.like("mpext.type", type),
							Restrictions.gt("mpext.floatValue", value2)),
							Restrictions.and(
									Restrictions.like("mpext.type", type),
									Restrictions.gt("mpext.TimeValue", value))));
					}
				if (paramName.startsWith("LE_")) {
					criteria.add(Restrictions.or(
							Restrictions.and(
							Restrictions.like("mpext.type", type),
							Restrictions.le("mpext.floatValue", value2)),
							Restrictions.and(
									Restrictions.like("mpext.type", type),
									Restrictions.le("mpext.TimeValue", value))));
					}
				if (paramName.startsWith("GE_")) {
					criteria.add(Restrictions.or(
							Restrictions.and(
							Restrictions.like("mpext.type", type),
							Restrictions.ge("mpext.floatValue", value2)),
							Restrictions.and(
									Restrictions.like("mpext.type", type),
									Restrictions.ge("mpext.TimeValue", value))));
					}
				if (paramName.startsWith("LT_")) {
					criteria.add(Restrictions.or(
							Restrictions.and(
							Restrictions.like("mpext.type", type),
							Restrictions.lt("mpext.floatValue", value2)),
							Restrictions.and(
									Restrictions.like("mpext.type", type),
									Restrictions.lt("mpext.TimeValue", value))));
					}
			}
			
		}
		
		if (paramName.startsWith("HASATTR_")){
			
		}
			
			
			
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
	return true;
}
}
