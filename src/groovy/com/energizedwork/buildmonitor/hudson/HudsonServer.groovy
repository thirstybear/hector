package com.energizedwork.buildmonitor.hudson

import static com.energizedwork.buildmonitor.BuildState.*

import com.energizedwork.buildmonitor.Project
import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.feed.synd.SyndEntry
import com.energizedwork.buildmonitor.BuildState


class HudsonServer {
    def feedRetriever

    List<Project> getProjects() {
        SyndFeed feed = feedRetriever.update()

        return feed.entries.collect { SyndEntry entry ->
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

        return new Project(name:projectName, state:state)
    }

    BuildState mapHudsonStateStringToBuildState(String buildStateString) {
        switch(buildStateString) {
            case "SUCCESS": passed; break
            case "FAILURE": failed; break
            case "null": building; break
            case "ABORTED": aborted; break
            default: failed
        }
    }
    
}