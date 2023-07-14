package com.ly.redisperformance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@ToString
@Table  // we have to add this annotation because we are usingSpring data redis and spring data Postgres, so it will conflict. Add this to show that we're using Postgres instead of saving it in Redis.
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    private Integer id;
    private String description;
    private double price;


}
