package com.services.inventoryservice.util;

import com.services.inventoryservice.model.Inventory;
import com.services.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final InventoryRepository inventoryRepository;
    @Override
    public void run(String... args) throws Exception {
        Inventory inventory = new Inventory();
        inventory.setSkuCode("iphone");
        inventory.setQuantity(100);

        Inventory inventory1 = new Inventory();
        inventory1.setSkuCode("realme");
        inventory1.setQuantity(0);

        Inventory inventory2 = new Inventory();
        inventory2.setSkuCode("samsung");
        inventory2.setQuantity(0);

        inventoryRepository.save(inventory);
        inventoryRepository.save(inventory1);
        inventoryRepository.save(inventory2);
    }
}
