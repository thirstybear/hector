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

@WithGMock
class FeedRetrieverTests extends GroovyTestCase {

    void testExecuteReturnsNullGivenNullURL() {
        assertNull new FeedRetriever(null).get()
    }

    void testRetrievesBlogFeed() {
        URL url = new URL('http://path/to/some/atom/feed.xml')
        FeedRetriever blogRetriever = new FeedRetriever(url)

        SyndFeed expected = mock(SyndFeed)

        ClientConnectionManager clientConnectionManager = mock(ClientConnectionManager) {
            shutdown()
        }

        StatusLine mockStatusLine = mock(StatusLine) {
            statusCode.returns 200
        }

        InputStream mockInputStream = mock(InputStream) {
            withReader(match { it.equals(blogRetriever.convertReaderToFeed) }).returns(expected)
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
            SyndFeed actual = blogRetriever.get()
            assertSame expected, actual
        }
    }

    void testConvertReaderToFeed() {
        URL url = new URL('http://path/to/some/atom/feed.xml')
        FeedRetriever blogRetriever = new FeedRetriever(url)

        Reader mockReader = mock(Reader)
        SyndFeed expected = mock(SyndFeed)
        SyndFeedInput mockSyndFeedInput = mock(SyndFeedInput, constructor()) {
            build(mockReader).returns expected
        }

        play {
            SyndFeed actual = blogRetriever.convertReaderToFeed(mockReader)
            assertSame expected, actual
        }
    }

}