/*
 * FuckUB Pty. Ltd. ("LKG") CONFIDENTIAL
 * Copyright (c) 2025 FuckUB project Pty. Ltd. All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of LKG. The intellectual and technical concepts contained
 * herein are proprietary to LKG and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 * from LKG.  Access to the source code contained herein is hereby forbidden to anyone except current LKG employees, managers or contractors who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 */
package com.practice.service.api;

import com.practice.service.api.specs.ProductSpecs;
import com.practice.service.dto.ProductResponse;
import com.practice.shared.domain.entities.Product;
import com.practice.shared.domain.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

import static com.practice.shared.domain.utils.MapperFunctions.mapIfNotNull;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private ModelMapper modelMapper;

    public ProductController(ModelMapper modelMapper, ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam Optional<String> keyword, @PageableDefault Pageable pageable) {
        Page<Product> products = productRepository.searchByName(keyword.orElse(""), pageable);
        Page<ProductResponse> productResponses = products.map((x) -> {
            ProductResponse productResponse = modelMapper.map(x, ProductResponse.class);
            mapIfNotNull(x::getName, productResponse::setName);
            return productResponse;
        });
        return ResponseEntity.ok().body(productResponses);
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAllWithSpecs(
            @RequestParam Optional<String> keyword,
            @RequestParam Optional<Product.ProductCategory> category,
            @RequestParam BigDecimal maxPrice,
            @RequestParam BigDecimal minPrice,
            @PageableDefault Pageable pageable) {
        Specification<Product> spec = Specification
                .where(ProductSpecs.hasName(keyword.orElse("")))
                .and(ProductSpecs.hasCategory(category.orElse(Product.ProductCategory.OTHER)))
                .and(ProductSpecs.priceBetween(minPrice, maxPrice));

        return ResponseEntity.ok().body(productRepository.findAll(spec, pageable));
    }
}
