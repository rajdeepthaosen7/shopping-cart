package com.cams.shippingservice.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    // naive in-memory status store: orderItemId -> status
    private final Map<Long, String> status = new ConcurrentHashMap<>();

    @PostMapping("/{orderItemId}/create")
    public Map<String, Object> create(@PathVariable Long orderItemId) {
        status.put(orderItemId, "CREATED");
        return Map.of("orderItemId", orderItemId, "status", "CREATED");
    }

    @PostMapping("/{orderItemId}/dispatch")
    public Map<String, Object> dispatch(@PathVariable Long orderItemId) {
        status.put(orderItemId, "DISPATCHED");
        return Map.of("orderItemId", orderItemId, "status", "DISPATCHED");
    }

    @PostMapping("/{orderItemId}/deliver")
    public Map<String, Object> deliver(@PathVariable Long orderItemId) {
        status.put(orderItemId, "DELIVERED");
        return Map.of("orderItemId", orderItemId, "status", "DELIVERED");
    }

    @GetMapping("/{orderItemId}")
    public Map<String, Object> get(@PathVariable Long orderItemId) {
        return Map.of("orderItemId", orderItemId, "status", status.getOrDefault(orderItemId, "UNKNOWN"));
    }
}
