#!/usr/bin/env bash

echo "{ \"Version\": \"2012-10-17\", \"Statement\": [{ \"Action\": \"sts:AssumeRole\", \"Principal\": { \"Service\": \"cloudformation.amazonaws.com\" }, \"Effect\": \"Allow\", \"Sid\": \"\" }]}" > iam_role_cfn.json

export ZOMBIE_ROLE=scheduled-lambda-cloudformation-role

aws iam create-role --role-name=$ZOMBIE_ROLE  --assume-role-policy-document file://iam_role_cfn.json --description "TEMP ROLE: Allow CFN to administer 'zombie' stack"

$(aws iam attach-role-policy --role-name $ZOMBIE_ROLE --policy-arn arn:aws:iam::aws:policy/AdministratorAccess)

$(aws cloudformation delete-stack --stack-name scheduled-event-lambda)

$(aws iam detach-role-policy --role-name $ZOMBIE_ROLE --policy-arn arn:aws:iam::aws:policy/AdministratorAccess)

$(aws iam delete-role --role-name $ZOMBIE_ROLE)
