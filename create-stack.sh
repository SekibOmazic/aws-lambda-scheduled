#!/usr/bin/env bash

if [[ $# -le 3 ]] ; then
    echo 'Please call' $0 'with 4 parameters'
    echo 'Parameters are:'
    echo 'TWITTER_API_KEY TWITTER_API_SECRET TWITTER_ACCESS_TOKEN TWITTER_ACCESS_TOKEN_SECRET'
    echo 'Example:' $0 'eUB8PN3434534 MroQR4Jtgo3456fa45654 24334534-HT345345 g9sIRIbCFO03Fir3'
    exit 1
fi

aws cloudformation create-stack --stack-name scheduled-event-lambda-pipeline \
    --template-body file://cicd/pipeline.yaml \
    --parameters ParameterKey=TableName,ParameterValue=TwitterHashtags \
                 ParameterKey=TwitterApiKey,ParameterValue=$1 \
                 ParameterKey=TwitterApiSecret,ParameterValue=$2 \
                 ParameterKey=TwitterAccessToken,ParameterValue=$3 \
                 ParameterKey=TwitterAccessTokenSecret,ParameterValue=$4 \
    --capabilities CAPABILITY_NAMED_IAM
