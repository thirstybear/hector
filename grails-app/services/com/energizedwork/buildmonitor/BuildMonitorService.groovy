package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.BuildState.* 

class BuildMonitorService {
    Configuration configuration

    BuildState getState() {
//        URL hudson = new URL(configuration.url)
//
//        def rssData
//
//        slurpRssData()
        return failed    
    }

    private def slurpRssData() {
        def slurper = new XmlSlurper()
        url.withReader {reader ->
            rssData = slurper.parse(reader)
        }

        rssData
    }
}
