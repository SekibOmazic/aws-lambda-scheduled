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

## Fully automated deployment (not working yet)

From terminal run:

```
aws cloudformation create-stack --stack-name scheduled-event-lambda --template-body file://cicd/pipeline.yaml --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM
```

For some reason the pipeline gets a signal to delete itself. The created roles are deleted and the stack deletion fails. A zombie role is created and can not be deleted.

### Delete zombie role

create policy
```
echo "{ \"Version\": \"2012-10-17\", \"Statement\": [{ \"Action\": \"sts:AssumeRole\", \"Principal\": { \"Service\": \"cloudformation.amazonaws.com\" }, \"Effect\": \"Allow\", \"Sid\": \"\" }]}" > iam_role_cfn.json
```

export role name
```
export ZOMBIE_ROLE=scheduled-event-lambda-cloudformation-role
```

re-create role
```
aws iam create-role --role-name=$ZOMBIE_ROLE  --assume-role-policy-document file://iam_role_cfn.json --description "TEMP ROLE: Allow CFN to administer 'zombie' stack"
```

attach policy to the new role
```
aws iam attach-role-policy --role-name $ZOMBIE_ROLE --policy-arn arn:aws:iam::aws:policy/AdministratorAccess
```

delete stack
```
aws cloudformation delete-stack --stack-name scheduled-event-lambda
```

detach policy from the role
```
aws iam detach-role-policy --role-name $ZOMBIE_ROLE --policy-arn arn:aws:iam::aws:policy/AdministratorAccess
```

and finally delete the role
```
aws iam delete-role --role-name $ZOMBIE_ROLE
```
