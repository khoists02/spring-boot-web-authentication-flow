package com.practice.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.strategies(List.of(
                new HeaderContentNegotiationStrategy(){
                    @Override
                    public List<MediaType> resolveMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
                        List<MediaType> types = super.resolveMediaTypes(request);
                        if(types.stream().anyMatch(MimeType::isWildcardType))
                        {
                            if(types.stream().filter(m -> !m.isWildcardType()).noneMatch(mediaType -> mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)))
                            {
                                types.add(MediaType.APPLICATION_JSON);
                                MimeTypeUtils.sortBySpecificity(types);
                            }
                        }
                        return types;
                    }
                }
        ));
    }
}
