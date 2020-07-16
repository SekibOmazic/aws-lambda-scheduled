package com.omazicsekib

import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import io.mockk.verify
import org.joda.time.DateTime


class AppTest {

    private val twitterService = mockk<TwitterService>(relaxed = true)

    @Test
    fun testAppHasAGreeting() {

        // given
        val event: ScheduledEvent = ScheduledEvent()
                .withAccount("123456789012")
                .withRegion("eu-central-1")
                .withSource("aws.events")
                .withDetailType("Scheduled Event")
                .withId("c53a5cc4-00db-47f6-afc7-aa24f1667435")
                .withTime(DateTime("1971-02-03T00:00:00Z"))
                .withDetail(emptyMap<String, Any>())


        every { twitterService.searchTwitter(any()) } returns listOf()

        val classUnderTest = App(twitterService)

        // when
        val output = classUnderTest.handleRequest(event, null)

        //then
        verify(exactly = 1) { twitterService.searchTwitter(any()) }
    }

}
