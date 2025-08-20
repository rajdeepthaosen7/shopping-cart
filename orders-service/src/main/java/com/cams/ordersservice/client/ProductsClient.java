package com.cams.ordersservice.client;

import com.cams.ordersservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "products-service")
public interface ProductsClient {

    @GetMapping("/products/{id}")
    ProductDto getProduct(@PathVariable("id") Long id);  // <-- name it explicitly
}
