package com.omazicsekib

import com.amazonaws.services.lambda.runtime.Context

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory

class EventLogger {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val gson : Gson = GsonBuilder().setPrettyPrinting().create();

    fun logEvent(event: Any, context: Context?) {
        // log execution details
        logger.info("DYNAMO_DB_TABLE: " + gson.toJson(System.getenv("DYNAMODB_TABLE_NAME")));
        logger.info("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
        logger.info("CONTEXT: " + gson.toJson(context));

        // log event details
        logger.info("EVENT: " + gson.toJson(event));
        logger.info("EVENT TYPE: " + event.javaClass.toString());
    }
}
