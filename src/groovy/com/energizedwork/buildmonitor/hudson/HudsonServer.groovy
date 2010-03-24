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
        // todo see if there is a more elegant way to do this

        SyndFeed feed = feedRetriever.update()
        List<Project> projects = []
        feed?.entries.each { SyndEntry entry ->
            String projectname = getProjectName(entry)
            Project existingProject = projects.find { Project project ->
                project.name == projectname
            }

            if (!existingProject) {
                projects << buildProject(entry)
            }
        }

        projects
    }

    private String getProjectName(SyndEntry entry) {
        String feedTitle = entry.title
        int hashIndex = feedTitle.indexOf('#')
        String projectName = feedTitle.substring(0, hashIndex).trim()
    }

    private BuildState getProjectState(SyndEntry entry) {
        String feedTitle = entry.title
        def stateMatcher = (feedTitle =~ /.*\((\w*)\)$/)
        String stateString = stateMatcher[0][1]

        BuildState state = mapHudsonStateStringToBuildState(stateString)
    }

    private Project buildProject(SyndEntry entry) {
        String projectName = getProjectName(entry)
        BuildState state = getProjectState(entry)
        List<Change> changeset = getChangeSet(entry)

        return new Project(name:projectName, state:state, changeset:changeset)
    }

    private List<Change> getChangeSet(SyndEntry entry) {
        List<Change> result = []

        def projectBaseLink = getProjectBaseUrl(entry.link)
        int buildNumber =  getProjectBuildNumber(entry.link)

        while (buildNumber != 0) {
            def xml = xmlDocumentRetriever.getXml("${projectBaseLink}/${buildNumber}/api/xml")

            if (buildFailed(xml)) {
                xml.changeSet.item.msg.each {
                    result << new Change(checkinMsg:it.text())
                }
                buildNumber--;
            } else {
                break
            }
        }
        result
    }


    boolean buildFailed(def xml) {
        xml.result.text() == FAILURE
    }

    String getProjectBaseUrl (def link) {
        def m = link =~ "(http://.*)/\\d+/"

        m[0][1]
    }

    int getProjectBuildNumber(def link) {
        def m = link =~ "/(\\d+)/"
        return m[0][1].toInteger()
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
