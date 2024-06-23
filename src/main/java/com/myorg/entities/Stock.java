package com.myorg.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "stocks")
public class Stock {

    @DynamoDBHashKey(attributeName = "product_id")
    private String productId;

    @DynamoDBAttribute(attributeName = "count")
    private Integer count;
}
