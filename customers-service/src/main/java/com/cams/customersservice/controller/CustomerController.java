package com.cams.customersservice.controller;

import com.cams.customersservice.entity.Customer;
import com.cams.customersservice.repository.CustomerRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository repo;

    public CustomerController(CustomerRepository repo) {
        this.repo = repo;
    }

    // List all
    @GetMapping
    public List<Customer> findAll() {
        return repo.findAll();
    }

    // Get by id
    @GetMapping("/{id}")
    public Customer findOne(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // Create
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@Valid @RequestBody Customer c) {
        try {
            return repo.save(c);
        } catch (DataIntegrityViolationException ex) {
            // unique email constraint
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }

    // Update
    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @Valid @RequestBody Customer c) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        c.setId(id);
        try {
            return repo.save(c);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        repo.deleteById(id);
    }
}
