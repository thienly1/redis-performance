package com.ly.redisperformance.service.util;

import com.ly.redisperformance.entity.Product;
import com.ly.redisperformance.repository.ProductRepository;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductCacheTemplate extends CacheTemplate<Integer, Product> {

    @Autowired
    private ProductRepository repository;
    private RMapReactive<Integer, Product> map;

    public ProductCacheTemplate(RedissonReactiveClient client) {
        this.map = client.getMap("product", new TypedJsonJacksonCodec(Integer.class, Product.class));
    }
    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return this.repository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        return this.map.get(id);
    }

    @Override
    protected Mono<Product> updateSource(Integer id, Product product) {
        return this.repository.findById(id)
                .doOnNext(product1 -> product.setId(id))
                .flatMap(product1 -> this.repository.save(product));
    }

    @Override
    protected Mono<Product> updateCache(Integer id, Product product) {
        return this.map.fastPut(id, product).thenReturn(product);
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return this.repository.deleteById(id);
        //if you use database from external service, delete From Source is not able with repository, just do like this
        //return Mono.empty();
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return this.map.fastRemove(id).then();
    }
}
