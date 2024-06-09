package com.eric6166.inventory.service;

import com.eric6166.common.config.kafka.AppEvent;

public interface InventoryService {

    void handleOrderCreatedEvent(AppEvent appEvent);

}
