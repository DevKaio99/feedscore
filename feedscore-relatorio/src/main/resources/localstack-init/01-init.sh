#!/bin/bash
set -e

awslocal dynamodb create-table \
  --table-name feedscore-feedbacks \
  --attribute-definitions \
      AttributeName=id,AttributeType=S \
      AttributeName=semana,AttributeType=S \
      AttributeName=createdAt,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --global-secondary-indexes '[{
      "IndexName": "semana-createdAt-index",
      "KeySchema": [
        {"AttributeName": "semana", "KeyType": "HASH"},
        {"AttributeName": "createdAt", "KeyType": "RANGE"}
      ],
      "Projection": {"ProjectionType": "ALL"}
  }]' \
  --billing-mode PAY_PER_REQUEST \
  --region us-east-1

awslocal ses verify-email-identity \
  --email-address alertas@feedscore.local \
  --region us-east-1

echo "FeedScore init concluido"