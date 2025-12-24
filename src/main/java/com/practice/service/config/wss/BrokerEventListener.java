package com.practice.service.config.wss;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.stereotype.Component;

@Component
public class BrokerEventListener {
    @EventListener
    public void onBrokerAvailable(BrokerAvailabilityEvent event) {
        System.out.println("Broker available = " + event.isBrokerAvailable());
    }
}
