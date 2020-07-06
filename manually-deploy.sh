#!/usr/bin/env bash

# create bucket
$(aws s3 mb s3://scheduled-event-lambda-integration-bucket)

# build
./gradlew clean build

# package
$(aws cloudformation package --template-file infrastructure/cf-template.yaml --s3-bucket scheduled-event-lambda-integration-bucket --output-template-file packaged-stack.yaml)

# deploy
$(aws cloudformation deploy --template-file packaged-stack.yaml --stack-name scheduled-event-lambda --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM)
