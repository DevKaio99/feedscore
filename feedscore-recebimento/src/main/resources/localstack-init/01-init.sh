#!/bin/bash
awslocal dynamodb create-table \
  --table-name feedscore-feedbacks \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --region us-east-1

awslocal sqs create-queue \
  --queue-name feedscore-critical-feedbacks \
  --region us-east-1

echo "FeedScore init concluido"