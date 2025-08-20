package com.cams.reviewsservice.controller;

import com.cams.reviewsservice.entity.Review;
import com.cams.reviewsservice.repository.ReviewRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository repo;

    public ReviewController(ReviewRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@Valid @RequestBody Review r) {
        return repo.save(r);
    }

    @GetMapping("/product/{productId}")
    public List<Review> byProduct(@PathVariable Long productId) {
        return repo.findByProductId(productId);
    }
}
