package com.example.a01demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Products implements Comparable<Products>{

//    идентификатор продукта (целое число),
    private Integer productId;

//    имя (строка),
    private String name;

//    условие (строка),
    private String condition;

//    состояние (строка),
    private String state;

//    цена (float).
    private Float price;

    public String csvString(){
        return String.format("%d,%s,%s,%s,%.2f"
                ,productId,name,condition,state,price
        );
    }

    @Override
    public String toString(){
        return String.format("(%d,%s,%.2f)",productId,name,price);
    }

    @Override
    public int compareTo(Products product) {
        int compare = productId.compareTo(product.productId);
        return compare==0? price.compareTo(product.price) : compare;
    }
}