package com.omazicsekib.dynamodb

data class Tweet(val searchTerm: String, val tweetId: Long, val text: String, val lang: String, val createdAt: String) {
    companion object
}
