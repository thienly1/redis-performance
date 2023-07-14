package com.ly.redisperformance.service;

import com.ly.redisperformance.entity.Product;
import com.ly.redisperformance.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceV1 {

    @Autowired
    private ProductRepository repository;

    //GET REQUEST
    public Mono<Product> getProduct(int id){
        return repository.findById(id);
    }

    //PUT REQUEST
    public Mono<Product> updateProduct(int id, Mono<Product> productMono){
        return repository.findById(id)
                .flatMap(p -> productMono.doOnNext(p1 ->p1.setId(id)))
                .flatMap(this.repository::save);
    }

}
