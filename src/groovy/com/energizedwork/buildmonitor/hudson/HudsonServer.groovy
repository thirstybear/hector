package com.energizedwork.buildmonitor.hudson

import static com.energizedwork.buildmonitor.BuildState.*

import com.energizedwork.buildmonitor.Project
import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.feed.synd.SyndEntry
import com.energizedwork.buildmonitor.BuildState
import com.energizedwork.feed.FeedRetriever
import com.energizedwork.feed.XmlDocumentRetriever
import com.energizedwork.buildmonitor.Change


class HudsonServer {

    static final String SUCCESS = 'SUCCESS'
    static final String FAILURE = 'FAILURE'
    static final String BUILDING = 'null'
    static final String ABORTED = 'ABORTED'

    FeedRetriever feedRetriever
    XmlDocumentRetriever xmlDocumentRetriever

    List<Project> getProjects() {
        SyndFeed feed = feedRetriever.update()

        return feed?.entries.collect { SyndEntry entry ->
            buildProject(entry)
        }
    }

    private Project buildProject(SyndEntry entry) {
        String feedTitle = entry.title

        int hashIndex = feedTitle.indexOf('#')
        String projectName = feedTitle.substring(0, hashIndex).trim()

        def stateMatcher = (feedTitle =~ /.*\((\w*)\)$/)
        String stateString = stateMatcher[0][1]

        BuildState state = mapHudsonStateStringToBuildState(stateString)
        List<Change> changeset = getChangeSet(entry)

        return new Project(name:projectName, state:state, changeset:changeset)
    }

    private List<Change> getChangeSet(SyndEntry entry) {
        List<Change> result = []
        def xml = xmlDocumentRetriever.getXml("${entry.link}/api/xml")
        def msgs = xml.changeSet.item.msg
        msgs.each {
            println it.text()
            result << new Change()
        }
        return result
    }

    BuildState mapHudsonStateStringToBuildState(String buildStateString) {
        switch(buildStateString) {
            case SUCCESS: passed; break
            case FAILURE: failed; break
            case BUILDING: building; break
            case ABORTED: aborted; break
            default: failed
        }
    }
    
}