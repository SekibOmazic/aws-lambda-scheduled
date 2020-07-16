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
aws cloudformation package --template-file infrastructure/cf-template.yaml --s3-bucket scheduled-event-lambda-integration-bucket --output-template-file packaged-stack.yaml
```
4. deploy
```
aws cloudformation deploy --template-file packaged-stack.yaml --stack-name scheduled-event-lambda --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM
```

Now check the CloudWatch. There will be a log entry every 5 minutes

## Fully automated deployment

From terminal run:

```
aws cloudformation create-stack --stack-name scheduled-event-lambda-pipeline \
    --template-body file://cicd/pipeline.yaml \
    --parameters ParameterKey=TableName,ParameterValue=TwitterHashtags \
                 ParameterKey=TwitterApiKey,ParameterValue=<TWITTER_API_KEY> \
                 ParameterKey=TwitterApiSecret,ParameterValue=<TWITTER_API_SECRET> \
                 ParameterKey=TwitterAccessToken,ParameterValue=<TWITTER_ACCESS_TOKEN> \
                 ParameterKey=TwitterAccessTokenSecret,ParameterValue=<TWITTER_ACCESS_TOKEN_SECRET> \
    --capabilities CAPABILITY_NAMED_IAM
```

## To do
- use the principle of the least privilege and restrict cloudformation policy even more
- add a webhook to trigger build on each code change
