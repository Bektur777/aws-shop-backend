#!/bin/bash

AWS_REGION="eu-central-1"

PRODUCTS_TABLE_NAME="products"
STOCKS_TABLE_NAME="stocks"

function generate_uuid {
  uuidgen | tr '[:upper:]' '[:lower:]'
}

PRODUCT_1_UUID=$(generate_uuid)
PRODUCT_2_UUID=$(generate_uuid)

PRODUCTS_DATA=$(cat <<EOF
{
  "$PRODUCTS_TABLE_NAME": [
    {
      "PutRequest": {
        "Item": {
          "id": {"S": "$PRODUCT_1_UUID"},
          "title": {"S": "Product 1"},
          "description": {"S": "Description 1"},
          "price": {"N": "10.99"}
        }
      }
    },
    {
      "PutRequest": {
        "Item": {
          "id": {"S": "$PRODUCT_2_UUID"},
          "title": {"S": "Product 2"},
          "description": {"S": "Description 2"},
          "price": {"N": "20.99"}
        }
      }
    }
  ]
}
EOF
)

STOCKS_DATA=$(cat <<EOF
{
  "$STOCKS_TABLE_NAME": [
    {
      "PutRequest": {
        "Item": {
          "product_id": {"S": "$PRODUCT_1_UUID"},
          "count": {"N": "100"}
        }
      }
    },
    {
      "PutRequest": {
        "Item": {
          "product_id": {"S": "$PRODUCT_2_UUID"},
          "count": {"N": "50"}
        }
      }
    }
  ]
}
EOF
)

function insert_items {
  local data="$1"

  aws dynamodb batch-write-item \
    --request-items "$data" \
    --region $AWS_REGION
}

insert_items "$PRODUCTS_DATA"

insert_items "$STOCKS_DATA"

echo "Tables populated successfully!"
