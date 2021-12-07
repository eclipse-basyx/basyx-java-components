/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.examples.kafkaproducer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyProducer {
	private static Logger logger = LoggerFactory.getLogger(DummyProducer.class);
	
	public static final String BOOTSTRAP_SERVER = "127.0.0.1:9092";
	public static final String TOPIC_NAME = "first-topic";
    
	public static void main(String[] args) throws InterruptedException {
        // create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        
        // send data
        for (int i = 0; i < 100; i++) {
            // create a producer record
        	String dataToSend = "{\"id'\":" + i + ", \"temperature\": " + 50*i + "}";
            ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(TOPIC_NAME, dataToSend);

            producer.send(producerRecord, (recordMetadata, e) -> {
                // executes every time record is sent or an exception is thrown
                if (e == null) {
                    // record was successfully sent
                    logger.info("Received new metadata: \n" +
                            "Topic: " + recordMetadata.topic() +
                            "Offset: " + recordMetadata.offset() +
                            "Partition: " + recordMetadata.partition() +
                            "Timestamp: " + recordMetadata.timestamp());
                } else {
                    logger.error(e.getMessage());
                }
            });
            Thread.sleep(1000);
        }

        producer.flush();
        producer.close();
    }
}
