#!/usr/bin/env bash

usage() {
    echo "usage: create-stack.sh [--api-key twitter_api_key] [--api-secret twitter_api_secret] [--access-token twitter_access_token] [--access-token-secret twitter_access_token_secret] [--table dynamodb_table] [--search-term search_term] | [-h]"
}

count=0

while [[ "$1" != "" ]]; do
    case $1 in
        -k | --api-key )               shift
                                       api_key=$1
                                       ((count++))
                                       ;;
        -s | --api-secret )            shift
                                       api_secret=$1
                                       ((count++))
                                       ;;
        -t | --access-token )          shift
                                       access_token=$1
                                       ((count++))
                                       ;;
        -ts | --access-token-secret )  shift
                                       access_token_secret=$1
                                       ((count++))
                                       ;;
        -tn | --table )                shift
                                       table_name=$1
                                       ((count++))
                                       ;;
        -st | --search-term )          shift
                                       search_term=$1
                                       ((count++))
                                       ;;
        -h | --help )                  usage
                                       exit
                                       ;;
    esac
    shift
done

if [[ $count -ne 6 ]]; then
  usage
  exit 1
fi

aws cloudformation create-stack --stack-name scheduled-event-lambda \
    --template-body file://cicd/pipeline.yaml \
    --parameters ParameterKey=TableName,ParameterValue=$table_name \
                 ParameterKey=SearchTerm,ParameterValue=$search_term \
                 ParameterKey=TwitterApiKey,ParameterValue=$api_key \
                 ParameterKey=TwitterApiSecret,ParameterValue=$api_secret \
                 ParameterKey=TwitterAccessToken,ParameterValue=$access_token \
                 ParameterKey=TwitterAccessTokenSecret,ParameterValue=$access_token_secret \
    --capabilities CAPABILITY_NAMED_IAM
