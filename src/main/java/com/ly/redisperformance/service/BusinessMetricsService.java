package com.ly.redisperformance.service;

import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

//consume the data from Redis
@Service
public class BusinessMetricsService {

    @Autowired
    private RedissonReactiveClient client;
    public Mono<Map<Integer, Double>> top3Products(){
        String format = DateTimeFormatter.ofPattern("YYYYMMdd").format(LocalDate.now());
        RScoredSortedSetReactive<Integer> set = client.getScoredSortedSet("product:visit:" + format, IntegerCodec.INSTANCE);
        return set.entryRangeReversed(0, 2)  //list of scored entry, top3 products with descending order, so it should be 0, 1, 2 in index order
                .map(listSE -> listSE.stream().collect(
                        Collectors.toMap(
                                ScoredEntry::getValue,
                                ScoredEntry::getScore,
                                (a,b) -> a,
                                LinkedHashMap::new
                        )
                ));
    }
}
