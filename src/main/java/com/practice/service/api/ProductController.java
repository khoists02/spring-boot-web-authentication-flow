package com.practice.service.api;

import com.practice.service.dto.ProductResponse;
import com.practice.shared.domain.entities.Product;
import com.practice.shared.domain.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
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
    public ResponseEntity<?> findAll(@RequestParam Optional<String> keyword) {
        List<Product> products = productRepository.findAllByName(keyword.orElse(""));
        List<ProductResponse> productResponses = products.stream().map((x) -> {
            ProductResponse productResponse = modelMapper.map(x, ProductResponse.class);
            mapIfNotNull(x::getName, productResponse::setName);
            return productResponse;
        }).toList();
        return ResponseEntity.ok().body(productResponses);
    }

}
