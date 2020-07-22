package com.omazicsekib

import com.omazicsekib.dynamodb.Tweet
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TwitterServiceTest {
    private val searchTerm = "#traton -filter:retweets -filter:replies"

    @Test
    fun testTwitterHashtag() {
        // given
        val twitterService = TwitterService(
                "wTB8PN09mnlb5DMS5XsoHvURl",
                "MroQR4Jtgo3To0XJbs4cATPgtcYPEfIjyfw6gcn3BtsC2ojAyh",
                "243289845-HQcF1BEqyzpGCLctpjSSkrud5G2k5yJtTG739Fne",
                "r8sIRIbCFOd2rrLUyrup0Nry1suIzFqcg4iiP7Aby42rS"
        )
        //when
        val tweets = twitterService.searchTwitter(searchTerm, 1284012000532746252L)

        // then
        assertNotNull(tweets)
        assertEquals(10, tweets.size)
    }

    @Test
    fun `collect only valid tweets`() {
        // given
        val fetched = getDummyTweets()

        // when
        val result = getTweets(searchTerm, 5, fetched)

        //then
        assertEquals(6, result.size)
    }

    private fun getTweets(searchTerm: String, sinceId: Long, fromTwitter: List<Tweet>) : List<Tweet> {
        var nextSinceId = sinceId

        val tweetsFound = fromTwitter.fold(mutableListOf(), { acc: MutableList<Tweet>, tweet: Tweet ->
            if (tweet.searchTerm == searchTerm) {

                if (tweet.tweetId > nextSinceId) {
                    nextSinceId = tweet.tweetId;
                    acc.add(tweet)
                }
            }
            acc
        })

        return tweetsFound
    }

    private fun getDummyTweets(): List<Tweet> {
        return listOf(
                Tweet(searchTerm, 1, "Tweet description", "en", "2020-02-04"),
                Tweet(searchTerm, 2, "another tweet description", "en", "2020-02-03"),
                Tweet(searchTerm, 3, "yet another tweet description", "en", "2020-02-04"),
                Tweet(searchTerm, 4, "Tweet description", "en", "2020-02-04"),
                Tweet("#aws", 5, "AWS tweet", "en", "2020-02-03"),
                Tweet(searchTerm, 6, "yet another tweet description", "en", "2020-02-04"),
                Tweet(searchTerm, 7, "Tweet description", "en", "2020-02-04"),
                Tweet("#aws", 8, "another AWS tweet", "en", "2020-02-03"),
                Tweet(searchTerm, 9, "yet another tweet description", "en", "2020-02-04"),
                Tweet(searchTerm, 10, "Tweet description", "en", "2020-02-04"),
                Tweet(searchTerm, 11, "another tweet description", "en", "2020-02-03"),
                Tweet(searchTerm, 12, "yet another tweet description", "en", "2020-02-04")
        )
    }
}
