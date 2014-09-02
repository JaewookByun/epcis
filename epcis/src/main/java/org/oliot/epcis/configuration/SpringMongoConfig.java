package org.oliot.epcis.configuration;

import java.util.ArrayList;
import java.util.List;

import org.oliot.epcis.serde.ObjectEventWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

/**
 * Copyright (C) 2014 KAIST RESL
 *
 * This file is part of Oliot (oliot.org).
 *
 * @author Jack Jaewook Byun, Ph.D student Korea Advanced Institute of Science
 *         and Technology Real-time Embedded System Laboratory(RESL)
 *         bjw0829@kaist.ac.kr
 */
@Configuration
public class SpringMongoConfig extends AbstractMongoConfiguration {

	public @Bean MongoDbFactory mongoDbFactory() throws Exception {
		return new SimpleMongoDbFactory(new MongoClient(), "epcis");
	}

	public @Bean MongoTemplate mongoTemplate() throws Exception {

		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
		return mongoTemplate;

	}

	@Bean
	@Override
	public CustomConversions customConversions() {
		List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
		converterList.add(new ObjectEventWriteConverter());
		return new CustomConversions(converterList);
	}

	@Override
	protected String getDatabaseName() {
		return "epcis";
	}

	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient("localhost");
	}
}
