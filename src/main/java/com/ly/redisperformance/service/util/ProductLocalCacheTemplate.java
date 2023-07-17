package com.ly.redisperformance.service.util;

import com.ly.redisperformance.entity.Product;
import com.ly.redisperformance.repository.ProductRepository;
import org.redisson.api.*;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductLocalCacheTemplate extends CacheTemplate<Integer, Product> {
    @Autowired
    private ProductRepository repository;
    private RLocalCachedMap<Integer, Product> map;

    public ProductLocalCacheTemplate(RedissonClient client) {
        LocalCachedMapOptions<Integer, Product> mapOptions = LocalCachedMapOptions.<Integer, Product>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR);

        this.map = client.getLocalCachedMap("product", new TypedJsonJacksonCodec(Integer.class, Product.class), mapOptions);
    }
    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return this.repository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        return Mono.justOrEmpty(this.map.get(id));
    }

    @Override
    protected Mono<Product> updateSource(Integer id, Product product) {
        return this.repository.findById(id)
                .doOnNext(product1 -> product.setId(id))
                .flatMap(product1 -> this.repository.save(product));
    }

    @Override
    protected Mono<Product> updateCache(Integer id, Product product) {
        return Mono.create(productMonoSink ->
            this.map.fastPutAsync(id, product)
                    .thenAccept(aBoolean -> productMonoSink.success(product))
                    .exceptionally(ex -> {
                        productMonoSink.error(ex);
                        return null;
                    })
        );
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return this.repository.deleteById(id);
        //if you use database from external service, delete From Source is not able with repository, just do like this
        //return Mono.empty();
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return Mono.create(productMonoSink ->
                this.map.fastRemoveAsync(id)
                        .thenAccept(aBoolean -> productMonoSink.success())
                        .exceptionally(ex -> {
                            productMonoSink.error(ex);
                            return null;
                        })
        );
    }
}