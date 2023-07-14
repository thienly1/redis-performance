package com.ly.redisperformance.service;

import com.ly.redisperformance.entity.Product;
import com.ly.redisperformance.service.util.CacheTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceV2 {

    @Autowired
    private CacheTemplate<Integer, Product> cacheTemplate;

    //GET REQUEST
    public Mono<Product> getProduct(int id){
        return this.cacheTemplate.get(id);
    }

    //PUT REQUEST
    public Mono<Product> updateProduct(int id, Mono<Product> productMono){
        return productMono.flatMap(product -> this.cacheTemplate.update(id, product));
    }
    //DELETE
    public Mono<Void> deleteProduct(int id){
        return this.cacheTemplate.delete(id);
    }
    //INSERT or POST

}
