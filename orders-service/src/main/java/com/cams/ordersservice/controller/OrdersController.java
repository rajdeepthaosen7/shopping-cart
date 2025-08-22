package com.cams.ordersservice.controller;

import com.cams.ordersservice.client.ProductsClient;
import com.cams.ordersservice.dto.OrderResponse;
import com.cams.ordersservice.dto.PlaceOrderRequest;
import com.cams.ordersservice.dto.ProductDto;
import com.cams.ordersservice.entity.OrderItem;
import com.cams.ordersservice.repository.OrderItemRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import feign.FeignException;

import java.math.BigDecimal;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    private final OrderItemRepository repo;
    private final ProductsClient products;

    public OrdersController(OrderItemRepository repo, ProductsClient products) {
        this.repo = repo;
        this.products = products;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@Valid @RequestBody PlaceOrderRequest req) {
        // 1) Fetch product details from products-service
        ProductDto p = products.getProduct(req.productId());

        // 2) Compute total = price * qty
        BigDecimal total = p.price().multiply(BigDecimal.valueOf(req.qty()));

        // 3) Persist order item (price snapshot)
        OrderItem item = new OrderItem();
        item.setProductId(req.productId());
        item.setQty(req.qty());
        item.setPriceAtOrder(p.price());
        item = repo.save(item);

        // 4) Return response
        return new OrderResponse(item.getId(), p.name(), req.qty(), total);
    }

    @GetMapping(produces = "application/json")
    public Object list() {
        return repo.findAll();
    }

    // ---- Optional: clean error handling ----
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({FeignException.NotFound.class})
    public String productNotFound(FeignException ex) {
        return "Product not found in products-service";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({DataAccessException.class})
    public String dbError(DataAccessException ex) {
        return "Database error while saving order";
    }
}
