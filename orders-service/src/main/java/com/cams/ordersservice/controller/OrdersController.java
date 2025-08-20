package com.cams.ordersservice.controller;

import com.cams.ordersservice.client.ProductsClient;
import com.cams.ordersservice.dto.OrderResponse;
import com.cams.ordersservice.dto.PlaceOrderRequest;
import com.cams.ordersservice.dto.ProductDto;
import com.cams.ordersservice.entity.OrderItem;
import com.cams.ordersservice.repository.OrderItemRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    private final ProductsClient productsClient;
    private final OrderItemRepository repo;

    public OrdersController(ProductsClient productsClient, OrderItemRepository repo) {
        this.productsClient = productsClient;
        this.repo = repo;
    }

    @PostMapping
    public OrderResponse place(@Valid @RequestBody PlaceOrderRequest req) {
        ProductDto p = productsClient.getProduct(req.productId());
        BigDecimal total = p.price().multiply(BigDecimal.valueOf(req.qty()));

        OrderItem item = new OrderItem();
        item.setProductId(req.productId());
        item.setQty(req.qty());
        item.setPriceAtOrder(p.price());
        repo.save(item);

        return new OrderResponse(item.getId(), p.name(), req.qty(), total);
    }

    @GetMapping
    public Object list() {
        return repo.findAll();
    }
}
