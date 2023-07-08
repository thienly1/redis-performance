package com.ly.redisperformance.service;

import com.ly.redisperformance.entity.Product;
import com.ly.redisperformance.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.management.monitor.MonitorNotification;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public Mono<Product> getProduct(int id){
        return repository.findById(id);
    }

    public Mono<Product> updateProduct(int id, Mono<Product> productMono){
        return repository.findById(id)
                .flatMap(p -> productMono.doOnNext(p1 ->p1.setId(id)))
                .flatMap(this.repository::save);
    }
}
