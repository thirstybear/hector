package com.energizedwork.feed

import org.gmock.WithGMock
import com.sun.syndication.feed.synd.SyndFeed
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.HttpClient
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.HttpEntity
import com.sun.syndication.io.SyndFeedInput
import com.energizedwork.buildmonitor.Configuration

@WithGMock
class FeedRetrieverTests extends GroovyTestCase {

    FeedRetriever feedRetriever

    void setUp() {
        feedRetriever = new FeedRetriever()
    }

    void testExecuteReturnsNullGivenNullURL() {
        setUpConfigurationUrl null

        play {
            assertNull feedRetriever.get()
        }
    }

    void testRetrievesBlogFeed() {
        URL url = new URL('http://path/to/some/atom/feed.xml')
        setUpConfigurationUrl url

        SyndFeed expected = mock(SyndFeed)

        ClientConnectionManager clientConnectionManager = mock(ClientConnectionManager) {
            shutdown()
        }

        StatusLine mockStatusLine = mock(StatusLine) {
            statusCode.returns 200
        }

        InputStream mockInputStream = mock(InputStream) {
            withReader(match { it.equals(feedRetriever.convertReaderToFeed) }).returns(expected)
        }

        HttpEntity mockEntity = mock(HttpEntity) {
            content.returns mockInputStream
        }

        HttpResponse response = mock(HttpResponse) {
            statusLine.returns mockStatusLine
            entity.returns mockEntity
        }

        HttpClient httpClient = mock(DefaultHttpClient, constructor()) {
            execute(match { it.URI.equals(url.toURI()) }).returns response
            connectionManager.returns clientConnectionManager
        }

        play {
            SyndFeed actual = feedRetriever.get()
            assertSame expected, actual
        }
    }

    void testConvertReaderToFeed() {
        Reader mockReader = mock(Reader)
        SyndFeed expected = mock(SyndFeed)
        SyndFeedInput mockSyndFeedInput = mock(SyndFeedInput, constructor()) {
            build(mockReader).returns expected
        }

        play {
            SyndFeed actual = feedRetriever.convertReaderToFeed(mockReader)
            assertSame expected, actual
        }
    }

    void setUpConfigurationUrl(URL value) {
        feedRetriever.configuration = mock(Configuration) {
            url.returns value
        }
    }

}