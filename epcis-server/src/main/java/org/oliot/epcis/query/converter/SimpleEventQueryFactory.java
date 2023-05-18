package org.oliot.epcis.query.converter;

import java.util.HashMap;

import org.oliot.epcis.query.converter.seq.*;

public class SimpleEventQueryFactory {
	private HashMap<String, QueryConverter> converterMap;

	public SimpleEventQueryFactory() {
		converterMap = new HashMap<String, QueryConverter>();
		converterMap.put("eventType", new EventTypeConverter());
		converterMap.put("GE_eventTime", new GEEventTimeConverter());
		converterMap.put("LT_eventTime", new LTEventTimeConverter());
		converterMap.put("GE_recordTime", new GERecordTimeConverter());
		converterMap.put("LT_recordTime", new LTRecordTimeConverter());
		converterMap.put("EQ_action", new EQActionConverter());
		converterMap.put("EQ_bizStep", new EQBizStepConverter());
		converterMap.put("EQ_disposition", new EQDispositionConverter());
		converterMap.put("EQ_persistentDisposition_set", new EQPersistentDispositionSetConverter());
		converterMap.put("EQ_persistentDisposition_unset", new EQPersistentDispositionUnsetConverter());
		converterMap.put("EQ_readPoint", new EQReadPointConverter());
		converterMap.put("EQ_bizLocation", new EQBizLocationConverter());
		converterMap.put("EQ_transformationID", new EQTransformationIDConverter());
		converterMap.put("EQ_eventID", new EQEventIDConverter());
		converterMap.put("EXISTS_errorDeclaration", new EXISTSErrorDeclarationConverter());
		converterMap.put("GE_errorDeclarationTime", new GEErrorDeclarationTimeConverter());
		converterMap.put("LT_errorDeclarationTime", new LTErrorDeclarationTimeConverter());
		converterMap.put("EQ_errorReason", new EQErrorReasonConverter());
		converterMap.put("EQ_correctiveEventID", new EQCorrectiveEventIDConverter());

		converterMap.put("MATCH_epc", new MATCHepcConverter());
		converterMap.put("MATCH_parentID", new MATCHparentIDConverter());
		converterMap.put("MATCH_inputEPC", new MATCHinputEPCConverter());
		converterMap.put("MATCH_outputEPC", new MATCHoutputEPCConverter());
		converterMap.put("MATCH_anyEPC", new MATCHanyEPCConverter());
		converterMap.put("MATCH_epcClass", new MATCHepcClassConverter());
		converterMap.put("MATCH_inputEPCClass", new MATCHinputEPCClassConverter());
		converterMap.put("MATCH_outputEPCClass", new MATCHoutputEPCClassConverter());
		converterMap.put("MATCH_anyEPCClass", new MATCHanyEPCClassConverter());

		converterMap.put("GE_startTime", new GESensorMetadataStartTimeConverter());
		converterMap.put("LT_startTime", new LTSensorMetadataStartTimeConverter());
		converterMap.put("GE_endTime", new GESensorMetadataEndTimeConverter());
		converterMap.put("LT_endTime", new LTSensorMetadataEndTimeConverter());
		converterMap.put("GE_SENSORMETADATA_time", new GESensorMetadataTimeConverter());
		converterMap.put("LT_SENSORMETADATA_time", new LTSensorMetadataTimeConverter());
		converterMap.put("GE_SENSORREPORT_time", new GESensorReportTimeConverter());
		converterMap.put("LT_SENSORREPORT_time", new LTSensorReportTimeConverter());
		converterMap.put("EQ_deviceID", new EQSensorMetadataDeviceIDConverter());
		converterMap.put("EQ_SENSORMETADATA_deviceID", new EQSensorMetadataDeviceIDConverter());
		converterMap.put("EQ_SENSORREPORT_deviceID", new EQSensorReportDeviceIDConverter());
		converterMap.put("EQ_SENSORMETADATA_deviceMetadata", new EQSensorMetadataDeviceMetadataConverter());
		converterMap.put("EQ_SENSORREPORT_deviceMetadata", new EQSensorReportDeviceMetadataConverter());
		converterMap.put("EQ_SENSORMETADATA_rawData", new EQSensorMetadataRawDataConverter());
		converterMap.put("EQ_SENSORREPORT_rawData", new EQSensorReportRawDataConverter());

		converterMap.put("EQ_dataProcessingMethod", new EQSensorMetadataDataProcessingMethodConverter());
		converterMap.put("EQ_SENSORMETADATA_dataProcessingMethod", new EQSensorMetadataDataProcessingMethodConverter());
		converterMap.put("EQ_SENSORREPORT_dataProcessingMethod", new EQSensorReportDataProcessingMethodConverter());

		// Sensors
		converterMap.put("EQ_type", new EQSensorReportSensorTypeConverter());
		converterMap.put("EQ_microorganism", new EQSensorReportMicroorganismConverter());
		converterMap.put("EQ_chemicalSubstance", new EQSensorReportChemicalSubstanceConverter());
		converterMap.put("EQ_bizRules", new EQSensorMetadataBizRulesConverter());
		converterMap.put("EQ_stringValue", new EQSensorReportStringValueConverter());
		converterMap.put("EQ_booleanValue", new EQSensorReportBooleanValueConverter());
		converterMap.put("EQ_hexBinaryValue", new EQSensorReportHexBinaryValueConverter());
		converterMap.put("EQ_uriValue", new EQSensorReportUriValueConverter());
		converterMap.put("GE_percRank", new GESensorReportPercRankConverter());
		converterMap.put("LT_percRank", new LTSensorReportPercRankConverter());

		// Complex
		converterMap.put("EQ_bizTransaction_", new EQBizTransactionConverter());
		converterMap.put("EQ_source_", new EQSourceConverter());
		converterMap.put("EQ_destination_", new EQDestinationConverter());

		converterMap.put("EQ_quantity_", new EQQuantityConverter());
		converterMap.put("GT_quantity_", new GTQuantityConverter());
		converterMap.put("GE_quantity_", new GEQuantityConverter());
		converterMap.put("LT_quantity_", new LTQuantityConverter());
		converterMap.put("LE_quantity_", new LEQuantityConverter());

		converterMap.put("EQ_INNER_ILMD_", new EQILMDInnerExtensionConverter());
		converterMap.put("GT_INNER_ILMD_", new GTILMDInnerExtensionConverter());
		converterMap.put("GE_INNER_ILMD_", new GEILMDInnerExtensionConverter());
		converterMap.put("LT_INNER_ILMD_", new LTILMDInnerExtensionConverter());
		converterMap.put("LE_INNER_ILMD_", new LEILMDInnerExtensionConverter());
		converterMap.put("EXISTS_INNER_ILMD_", new EXISTSILMDInnerExtensionConverter());

		converterMap.put("EQ_INNER_SENSORELEMENT_", new EQSensorElementInnerExtensionConverter());
		converterMap.put("GT_INNER_SENSORELEMENT_", new GTSensorElementInnerExtensionConverter());
		converterMap.put("GE_INNER_SENSORELEMENT_", new GESensorElementInnerExtensionConverter());
		converterMap.put("LT_INNER_SENSORELEMENT_", new LTSensorElementInnerExtensionConverter());
		converterMap.put("LE_INNER_SENSORELEMENT_", new LESensorElementInnerExtensionConverter());
		converterMap.put("EXISTS_INNER_SENSORELEMENT_", new EXISTSSensorElementInnerExtensionConverter());

		converterMap.put("EQ_INNER_readPoint_", new EQReadPointInnerExtensionConverter());
		converterMap.put("GT_INNER_readPoint_", new GTReadPointInnerExtensionConverter());
		converterMap.put("GE_INNER_readPoint_", new GEReadPointInnerExtensionConverter());
		converterMap.put("LT_INNER_readPoint_", new LTReadPointInnerExtensionConverter());
		converterMap.put("LE_INNER_readPoint_", new LEReadPointInnerExtensionConverter());
		converterMap.put("EXISTS_INNER_readPoint_", new EXISTSReadPointInnerExtensionConverter());

		converterMap.put("EQ_INNER_bizLocation_", new EQBizLocationInnerExtensionConverter());
		converterMap.put("GT_INNER_bizLocation_", new GTBizLocationInnerExtensionConverter());
		converterMap.put("GE_INNER_bizLocation_", new GEBizLocationInnerExtensionConverter());
		converterMap.put("LT_INNER_bizLocation_", new LTBizLocationInnerExtensionConverter());
		converterMap.put("LE_INNER_bizLocation_", new LEBizLocationInnerExtensionConverter());
		converterMap.put("EXISTS_INNER_bizLocation_", new EXISTSBizLocationInnerExtensionConverter());

		converterMap.put("EQ_INNER_ERROR_DECLARATION_", new EQErrorDeclarationInnerExtensionConverter());
		converterMap.put("GT_INNER_ERROR_DECLARATION_", new GTErrorDeclarationInnerExtensionConverter());
		converterMap.put("GE_INNER_ERROR_DECLARATION_", new GEErrorDeclarationInnerExtensionConverter());
		converterMap.put("LT_INNER_ERROR_DECLARATION_", new LTErrorDeclarationInnerExtensionConverter());
		converterMap.put("LE_INNER_ERROR_DECLARATION_", new LEErrorDeclarationInnerExtensionConverter());
		converterMap.put("EXISTS_INNER_ERROR_DECLARATION_", new EXISTSErrorDeclarationInnerExtensionConverter());

		converterMap.put("EQ_INNER_", new EQInnerExtensionConverter());
		converterMap.put("GT_INNER_", new GTInnerExtensionConverter());
		converterMap.put("GE_INNER_", new GEInnerExtensionConverter());
		converterMap.put("LT_INNER_", new LTInnerExtensionConverter());
		converterMap.put("LE_INNER_", new LEInnerExtensionConverter());
		converterMap.put("EXISTS_INNER_", new EXISTSInnerExtensionConverter());

		converterMap.put("EQ_ILMD_", new EQILMDExtensionConverter());
		converterMap.put("GT_ILMD_", new GTILMDExtensionConverter());
		converterMap.put("GE_ILMD_", new GEILMDExtensionConverter());
		converterMap.put("LT_ILMD_", new LTILMDExtensionConverter());
		converterMap.put("LE_ILMD_", new LEILMDExtensionConverter());
		converterMap.put("EXISTS_ILMD_", new EXISTSILMDExtensionConverter());

		converterMap.put("EQ_SENSORELEMENT_", new EQSensorElementExtensionConverter());
		converterMap.put("GT_SENSORELEMENT_", new GTSensorElementExtensionConverter());
		converterMap.put("GE_SENSORELEMENT_", new GESensorElementExtensionConverter());
		converterMap.put("LT_SENSORELEMENT_", new LTSensorElementExtensionConverter());
		converterMap.put("LE_SENSORELEMENT_", new LESensorElementExtensionConverter());
		converterMap.put("EXISTS_SENSORELEMENT_", new EXISTSSensorElementExtensionConverter());
		converterMap.put("EQ_SENSORMETADATA_", new EQSensorMetadataExtensionConverter());
		converterMap.put("EQ_SENSORREPORT_", new EQSensorReportExtensionConverter());
		converterMap.put("EXISTS_SENSORMETADATA_", new EXISTSSensorMetadataExtensionConverter());
		converterMap.put("EXISTS_SENSORREPORT_", new EXISTSSensorReportExtensionConverter());

		converterMap.put("EQ_readPoint_", new EQReadPointExtensionConverter());
		converterMap.put("GT_readPoint_", new GTReadPointExtensionConverter());
		converterMap.put("GE_readPoint_", new GEReadPointExtensionConverter());
		converterMap.put("LT_readPoint_", new LTReadPointExtensionConverter());
		converterMap.put("LE_readPoint_", new LEReadPointExtensionConverter());
		converterMap.put("EXISTS_readPoint_", new EXISTSReadPointExtensionConverter());

		converterMap.put("EQ_bizLocation_", new EQBizLocationExtensionConverter());
		converterMap.put("GT_bizLocation_", new GTBizLocationExtensionConverter());
		converterMap.put("GE_bizLocation_", new GEBizLocationExtensionConverter());
		converterMap.put("LT_bizLocation_", new LTBizLocationExtensionConverter());
		converterMap.put("LE_bizLocation_", new LEBizLocationExtensionConverter());
		converterMap.put("EXISTS_bizLocation_", new EXISTSBizLocationExtensionConverter());

		converterMap.put("EQ_ERROR_DECLARATION_", new EQErrorDeclarationExtensionConverter());
		converterMap.put("GT_ERROR_DECLARATION_", new GTErrorDeclarationExtensionConverter());
		converterMap.put("GE_ERROR_DECLARATION_", new GEErrorDeclarationExtensionConverter());
		converterMap.put("LT_ERROR_DECLARATION_", new LTErrorDeclarationExtensionConverter());
		converterMap.put("LE_ERROR_DECLARATION_", new LEErrorDeclarationExtensionConverter());
		converterMap.put("EXISTS_ERROR_DECLARATION_", new EXISTSErrorDeclarationExtensionConverter());

		converterMap.put("EQ_", new EQExtensionConverter());
		converterMap.put("GT_", new GTExtensionConverter());
		converterMap.put("GE_", new GEExtensionConverter());
		converterMap.put("LT_", new LTExtensionConverter());
		converterMap.put("LE_", new LEExtensionConverter());
		converterMap.put("EXISTS_", new EXISTSExtensionConverter());

		// sensor - complex
		converterMap.put("EQ_value_", new EQSensorValueConverter());
		converterMap.put("GT_value_", new GTSensorValueConverter());
		converterMap.put("GE_value_", new GESensorValueConverter());
		converterMap.put("LT_value_", new LTSensorValueConverter());
		converterMap.put("LE_value_", new LESensorValueConverter());

		converterMap.put("EQ_minValue_", new EQSensorMinValueConverter());
		converterMap.put("GT_minValue_", new GTSensorMinValueConverter());
		converterMap.put("GE_minValue_", new GESensorMinValueConverter());
		converterMap.put("LT_minValue_", new LTSensorMinValueConverter());
		converterMap.put("LE_minValue_", new LESensorMinValueConverter());

		converterMap.put("EQ_maxValue_", new EQSensorMaxValueConverter());
		converterMap.put("GT_maxValue_", new GTSensorMaxValueConverter());
		converterMap.put("GE_maxValue_", new GESensorMaxValueConverter());
		converterMap.put("LT_maxValue_", new LTSensorMaxValueConverter());
		converterMap.put("LE_maxValue_", new LESensorMaxValueConverter());

		converterMap.put("EQ_meanValue_", new EQSensorMeanValueConverter());
		converterMap.put("GT_meanValue_", new GTSensorMeanValueConverter());
		converterMap.put("GE_meanValue_", new GESensorMeanValueConverter());
		converterMap.put("LT_meanValue_", new LTSensorMeanValueConverter());
		converterMap.put("LE_meanValue_", new LESensorMeanValueConverter());

		converterMap.put("EQ_sDev_", new EQSensorSDevConverter());
		converterMap.put("GT_sDev_", new GTSensorSDevConverter());
		converterMap.put("GE_sDev_", new GESensorSDevConverter());
		converterMap.put("LT_sDev_", new LTSensorSDevConverter());
		converterMap.put("LE_sDev_", new LESensorSDevConverter());

		converterMap.put("EQ_percValue_", new EQSensorPercValueConverter());
		converterMap.put("GT_percValue_", new GTSensorPercValueConverter());
		converterMap.put("GE_percValue_", new GESensorPercValueConverter());
		converterMap.put("LT_percValue_", new LTSensorPercValueConverter());
		converterMap.put("LE_percValue_", new LESensorPercValueConverter());

		// master-data
		converterMap.put("WD_readPoint", new WDReadPointConverter());
		converterMap.put("WD_bizLocation", new WDBizLocationConverter());
		converterMap.put("HASATTR_", new HASATTRConverter());
		converterMap.put("EQ_ATTR_", new EQATTRConverter());
	}

	public HashMap<String, QueryConverter> getConverterMap() {
		return converterMap;
	}
}