package com.cams.productsservice.controller;

import com.cams.productsservice.dto.ProductDto;
import com.cams.productsservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/products", produces = "application/json")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService service;

    @GetMapping
    public List<ProductDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ProductDto get(@PathVariable("id") Long id) {
        return service.getDtoById(id);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto req) {
        ProductDto created = service.create(req.name(), req.price(), req.stock());
        return ResponseEntity
                .created(URI.create("/products/" + created.id()))
                .body(created);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public ProductDto update(@PathVariable("id") Long id, @Valid @RequestBody ProductDto req) {
        return service.update(id, req.name(), req.price(), req.stock());
    }

    // Focused stock endpoints (optional)
    @PostMapping("/{id}/stock/increase")
    public ProductDto inc(@PathVariable Long id, @RequestParam int by) {
        return service.increaseStock(id, by);
    }

    @PostMapping("/{id}/stock/decrease")
    public ProductDto dec(@PathVariable Long id, @RequestParam int by) {
        return service.decreaseStock(id, by);
    }
}
