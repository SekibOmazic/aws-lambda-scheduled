package com.omazicsekib

import io.mockk.every
import io.mockk.mockk
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import com.omazicsekib.dynamodb.Tweet
import com.omazicsekib.dynamodb.TweetRepository
import io.mockk.verify
import org.joda.time.DateTime
import org.junit.jupiter.api.Test


class AppTest {

    private val twitterService = mockk<TwitterService>(relaxed = true)
    private val tweetRepository = mockk<TweetRepository>(relaxed = true)
    private val searchTerm = "#aws -filter:retweets -filter:replies"

    @Test
    fun `application can access twitter`() {

        // given
        val event: ScheduledEvent = ScheduledEvent()
                .withAccount("123456789012")
                .withRegion("eu-central-1")
                .withSource("aws.events")
                .withDetailType("Scheduled Event")
                .withId("c53a5cc4-00db-47f6-afc7-aa24f1667435")
                .withTime(DateTime("1971-02-03T00:00:00Z"))
                .withDetail(emptyMap<String, Any>())


        val ml = mutableListOf<Tweet>()
        every { twitterService.searchTwitter(any(), any()) } returns listOf()
        every { tweetRepository.lastTweet(any()) } returns null
        every { tweetRepository.save(capture(ml)) } returns Unit

        val classUnderTest = App(twitterService, tweetRepository, searchTerm)

        // when
        classUnderTest.handleRequest(event, null)

        //then
        verify(exactly = 1) { twitterService.searchTwitter(any()) }
    }

}
