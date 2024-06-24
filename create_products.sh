#!/bin/bash

API_URL="https://wa015rf5vk.execute-api.eu-central-1.amazonaws.com/prod/products"

create_product() {
  local id=$1
  local title=$2
  local description=$3
  local price=$4
  local count=$5

  local product_json=$(cat <<EOF
{
  "id": "$id",
  "title": "$title",
  "description": "$description",
  "price": $price,
  "count": $count
}
EOF
)

  response=$(curl -s -w "%{http_code}" -o /dev/null -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -d "$product_json")

  if [ "$response" -eq 200 ]; then
    echo "Product $title created successfully."
  else
    echo "Failed to create product $title. Status code: $response"
  fi
}

create_product "20" "Product 1" "Description for product 1" 10.0 100
create_product "21" "Product 2" "Description for product 2" 20.0 200
create_product "23" "Product 3" "Description for product 3" 30.0 300
