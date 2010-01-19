package com.energizedwork.feed

import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.io.SyndFeedInput
import java.util.Formatter.DateTime
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

class FeedRetriever {
    private final URL url

    FeedRetriever(URL url) {
        this.url = url
    }

    SyndFeed update() {
        // TODO rename either this or the get method!
        get()
    }

    SyndFeed get() {
        SyndFeed result
        if (url) {
            HttpClient httpclient = new DefaultHttpClient()
            HttpGet get = new HttpGet(url.toURI())
            HttpResponse response = httpclient.execute(get)
            if (response.statusLine.statusCode == 200) {
                HttpEntity entity = response.entity
                if (entity) {
                    result = entity.content.withReader(convertReaderToFeed)
                }
            }
            httpclient.connectionManager.shutdown()
        }

        return result
    }

    Closure convertReaderToFeed = {Reader reader ->
        SyndFeedInput input = new SyndFeedInput()
        return input.build(reader)
    }

}
