package com.cams.shippingservice.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    // IMPORTANT: initialize the map!
    private final Map<Long, String> status = new ConcurrentHashMap<>();

    @PostMapping("/{orderItemId}/create")
    public Map<String, Object> create(@PathVariable("orderItemId") Long orderItemId) {
        status.put(orderItemId, "DISPATCHED");
        return Map.of("orderItemId", orderItemId, "status", "DISPATCHED");
    }

    @PostMapping("/{orderItemId}/deliver")
    public Map<String, Object> deliver(@PathVariable("orderItemId") Long orderItemId) {
        status.put(orderItemId, "DELIVERED");
        return Map.of("orderItemId", orderItemId, "status", "DELIVERED");
    }

    @GetMapping("/{orderItemId}")
    public Map<String, Object> get(@PathVariable("orderItemId") Long orderItemId) {
        return Map.of("orderItemId", orderItemId, "status", status.getOrDefault(orderItemId, "UNKNOWN"));
    }
}
