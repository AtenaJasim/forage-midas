package com.jpmc.midascore.kafka;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionListener.class);
    private final TransactionService service;

    public TransactionListener(TransactionService service) {
        this.service = service;
    }

    @KafkaListener(topics = "${general.kafka-topic}", containerFactory = "kafkaListenerContainerFactory")
    public void handleIncoming(Transaction tx) {
        log.info("Received transaction from Kafka: {}", tx);
        service.process(tx);
    }
}

