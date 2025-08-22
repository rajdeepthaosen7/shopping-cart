package com.cams.notificationservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    record EmailRequest(String to, String subject, String body) {}
    record SmsRequest(String to, String message) {}
    record Notification(Long id, String type, String to, String content, Instant createdAt, String status) {}

    private final Queue<Notification> store = new ConcurrentLinkedQueue<>();
    private final AtomicLong seq = new AtomicLong(1);

    @PostMapping(path = "/email", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Notification sendEmail(@RequestBody EmailRequest req) {
        Notification n = new Notification(
                seq.getAndIncrement(),
                "EMAIL",
                req.to(),
                "[%s] %s".formatted(req.subject(), req.body()),
                Instant.now(),
                "QUEUED"
        );
        store.add(n);
        return n;
    }

    @PostMapping(path = "/sms", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Notification sendSms(@RequestBody SmsRequest req) {
        Notification n = new Notification(
                seq.getAndIncrement(),
                "SMS",
                req.to(),
                req.message(),
                Instant.now(),
                "QUEUED"
        );
        store.add(n);
        return n;
    }

    // Handy hook other services can call (orders/shipping)
    record OrderStatusRequest(Long orderItemId, String status, String customerEmail, String customerPhone) {}
    @PostMapping(path = "/order-status", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, Object> orderStatus(@RequestBody OrderStatusRequest req) {
        if (req.customerEmail() != null && !req.customerEmail().isBlank()) {
            sendEmail(new EmailRequest(
                    req.customerEmail(),
                    "Order Item %d: %s".formatted(req.orderItemId(), req.status()),
                    "Your order item #%d is now %s".formatted(req.orderItemId(), req.status())
            ));
        }
        if (req.customerPhone() != null && !req.customerPhone().isBlank()) {
            sendSms(new SmsRequest(
                    req.customerPhone(),
                    "Order item #%d: %s".formatted(req.orderItemId(), req.status())
            ));
        }
        return Map.of("ok", true);
    }

    @GetMapping(produces = "application/json")
    public List<Notification> list() {
        return new ArrayList<>(store);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear() {
        store.clear();
        seq.set(1);
    }
}
