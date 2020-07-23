# aws-lambda-scheduled

A simple lambda function that is triggered via scheduled event

## Manual deployment

Following works fine:

1. create a bucket
```
aws s3 mb s3://scheduled-event-lambda-integration-bucket
```

2. build the lambda
```
./gradlew clean build
```

3. package the artifacts
```
aws cloudformation package --template-file infrastructure/cf-template.yaml \
    --s3-bucket scheduled-event-lambda-integration-bucket \
    --output-template-file packaged-stack.yaml
```

4. deploy
```
aws cloudformation deploy --template-file packaged-stack.yaml \
    --stack-name scheduled-event-lambda \
    --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM
```

Now check the CloudWatch. There will be a log entry every 5 minutes.

You can change the cron expression in `cf-template.yaml` to suits your needs.


## Fully automated deployment

Create an OAuth Token in your GitHub account and store it in the Parameter Store under `/github/token`

Then from terminal run:

```
aws cloudformation create-stack --stack-name scheduled-event-lambda \
    --template-body file://cicd/pipeline.yaml \
    --parameters ParameterKey=TableName,ParameterValue=<DYNAMODB_TABLE> \
                 ParameterKey=SearchTerm,ParameterValue=<TWITTER_SEARCH_TERM> \
                 ParameterKey=TwitterApiKey,ParameterValue=<TWITTER_API_KEY> \
                 ParameterKey=TwitterApiSecret,ParameterValue=<TWITTER_API_SECRET> \
                 ParameterKey=TwitterAccessToken,ParameterValue=<TWITTER_ACCESS_TOKEN> \
                 ParameterKey=TwitterAccessTokenSecret,ParameterValue=<TWITTER_ACCESS_TOKEN_SECRET> \
    --capabilities CAPABILITY_NAMED_IAM
```

## To do
- use nested stacks 
- use the principle of the least privilege and restrict cloudformation policy even more
- add a webhook to trigger build on each code change
