#!/bin/bash

awslocal ses verify-email-identity \
  --email-address alertas@feedscore.local \
  --region us-east-1

echo "FeedScore init concluido"