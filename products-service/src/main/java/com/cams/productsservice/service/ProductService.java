// File: products-service/src/main/java/com/cams/productsservice/service/ProductService.java
package com.cams.productsservice.service;

import com.cams.productsservice.dto.ProductDto;
import com.cams.productsservice.entity.Product;
import com.cams.productsservice.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    /* =========================
       Read operations
       ========================= */
    @Transactional(readOnly = true)
    public List<ProductDto> list() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ProductDto getDtoById(Long id) {
        Product p = getEntityById(id);
        return toDto(p);
    }

    /* =========================
       Create / Update
       ========================= */
    public ProductDto create(String name, BigDecimal price, Integer stock) {
        validateName(name);
        validatePrice(price);
        if (stock == null) stock = 0;
        if (stock < 0) throw badRequest("stock must be >= 0");

        Product p = new Product();
        p.setName(name.trim());
        p.setPrice(price);
        p.setStock(stock);

        Product saved = repo.save(p);
        return toDto(saved);
    }

    public ProductDto update(Long id, String name, BigDecimal price, Integer stock) {
        Product p = getEntityById(id);

        if (name != null) {
            validateName(name);
            p.setName(name.trim());
        }
        if (price != null) {
            validatePrice(price);
            p.setPrice(price);
        }
        if (stock != null) {
            if (stock < 0) throw badRequest("stock must be >= 0");
            p.setStock(stock);
        }

        Product saved = repo.save(p);
        return toDto(saved);
    }

    /* =========================
       Stock helpers (useful from Orders)
       ========================= */
    public ProductDto increaseStock(Long id, int delta) {
        if (delta <= 0) throw badRequest("delta must be > 0");
        Product p = getEntityById(id);
        p.setStock(p.getStock() + delta);
        return toDto(repo.save(p));
    }

    public ProductDto decreaseStock(Long id, int delta) {
        if (delta <= 0) throw badRequest("delta must be > 0");
        Product p = getEntityById(id);
        int newStock = p.getStock() - delta;
        if (newStock < 0) throw badRequest("insufficient stock for product id=%d".formatted(id));
        p.setStock(newStock);
        return toDto(repo.save(p));
    }

    /* =========================
       Internals
       ========================= */
    @Transactional(readOnly = true)
    public Product getEntityById(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Product %d not found".formatted(id))
        );
    }

    private ProductDto toDto(Product p) {
        // No SKU in your entity, so we don’t include it
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getPrice(),
                p.getStock()
        );
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw badRequest("name must not be blank");
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw badRequest("price must be >= 0");
        }
    }

    private ResponseStatusException badRequest(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}
