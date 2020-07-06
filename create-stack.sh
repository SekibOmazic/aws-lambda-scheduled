#!/usr/bin/env bash

$(aws cloudformation create-stack --stack-name scheduled-event-lambda --template-body file://cicd/pipeline.yaml --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM)
