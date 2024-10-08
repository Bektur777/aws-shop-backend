package com.myorg.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.TransactionWriteRequest;
import com.google.gson.Gson;
import com.myorg.dto.ProductDto;
import com.myorg.entities.Product;
import com.myorg.entities.Stock;
import com.myorg.exceptions.ProductNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductService {

    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    private final DynamoDBMapper mapper = new DynamoDBMapper(client);

    public List<ProductDto> getAllProducts() {
        List<Product> products = mapper.scan(Product.class, new DynamoDBScanExpression());
        List<Stock> stocks = mapper.scan(Stock.class, new DynamoDBScanExpression());

        products.forEach(System.out::println);

        Map<String, Integer> stockMap = stocks.stream()
                .collect(Collectors.toMap(Stock::getProductId, Stock::getCount));

        stocks.forEach(System.out::println);

        return products.stream()
                .map(product -> {
                    ProductDto productDto = new ProductDto();
                    productDto.setId(product.getId());
                    productDto.setTitle(product.getTitle());
                    productDto.setDescription(product.getDescription());
                    productDto.setPrice(product.getPrice());

                    Integer count = stockMap.getOrDefault(product.getId(), 0);
                    productDto.setCount(count);

                    return productDto;
                })
                .toList();
    }

    public ProductDto getProductById(String id) {
        Product product = mapper.load(Product.class, id);
        Stock stocks = mapper.load(Stock.class, id);

        if (product != null) {
            ProductDto productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setTitle(product.getTitle());
            productDto.setDescription(product.getDescription());
            productDto.setPrice(product.getPrice());
            productDto.setCount(stocks.getCount());

            return productDto;
        } else {
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }
    }

    public void saveProduct(ProductDto productDto) {
        Product product = new Product();
        String productId = UUID.randomUUID().toString();
        product.setId(productId);
        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());

        Stock stock = new Stock();
        stock.setProductId(productId);
        stock.setCount(productDto.getCount());

        TransactionWriteRequest transactionRequest = new TransactionWriteRequest()
                .addPut(product)
                .addPut(stock);

        try {
            mapper.transactionWrite(transactionRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error transaction to saving product", e);
        }
    }
}
