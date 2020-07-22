package com.omazicsekib.dynamodb

interface TweetRepository {
    fun save(tweet: Tweet)
    fun save (tweets: List<Tweet>)
    fun all(): List<Tweet>
    fun delete(searchTerm: String, id: Long)
    fun lastId(): Long
    fun lastTweet(searchTerm: String): Tweet?
}
