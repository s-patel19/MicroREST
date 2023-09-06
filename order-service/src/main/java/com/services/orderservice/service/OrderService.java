package com.services.orderservice.service;

import com.services.orderservice.dto.InventoryResponse;
import com.services.orderservice.dto.OrderLineItemsDto;
import com.services.orderservice.dto.OrderRequest;
import com.services.orderservice.event.OrderPlacedEvent;
import com.services.orderservice.model.Order;
import com.services.orderservice.model.OrderLineItems;
import com.services.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    // private final ObservationRegistry observationRegistry;
    // private final ApplicationEventPublisher applicationEventPublisher;

    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        // Call Inventory Service, and place order if product is in
        // stock
        // Observation inventoryServiceObservation = Observation.createNotStarted("inventory-service-lookup",
        //         this.observationRegistry);
        //inventoryServiceObservation.lowCardinalityKeyValue("call", "inventory-service");
        // return inventoryServiceObservation.observe(() -> {
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                    .allMatch(InventoryResponse::isInStock);

            if (allProductsInStock) {
                orderRepository.save(order);
                // publish Order Placed Event
                // applicationEventPublisher.publishEvent(new OrderPlacedEvent(this, order.getOrderNumber()));
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
                return "Order Placed Successfullly";
            } else {
                return "Product is not in stock, please try again later";
                //throw new IllegalArgumentException("Product is not in stock, please try again later");
            }
        };


    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
