package com.practice.service.api;

import com.practice.service.dto.ProductResponse;
import com.practice.shared.domain.entities.Product;
import com.practice.shared.domain.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
        Page<Product> products = productRepository.findAllByName(keyword.orElse(""), pageable);
        Page<ProductResponse> productResponses = products.map((x) -> {
            ProductResponse productResponse = modelMapper.map(x, ProductResponse.class);
            mapIfNotNull(x::getName, productResponse::setName);
            return productResponse;
        });
        return ResponseEntity.ok().body(productResponses);
    }

}
