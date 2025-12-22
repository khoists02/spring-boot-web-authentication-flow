package com.practice.service.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Optional: cấu hình strict matching để mapping chính xác hơn
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // Optional: ignore null values
        mapper.getConfiguration().setSkipNullEnabled(true);

        return mapper;
    }
}
