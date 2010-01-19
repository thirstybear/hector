package com.energizedwork.buildmonitor.hudson

import static com.energizedwork.buildmonitor.BuildState.*

import org.gmock.WithGMock
import com.energizedwork.buildmonitor.Project
import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.feed.synd.SyndEntry
import com.energizedwork.feed.FeedRetriever

@WithGMock
class HudsonServerTests extends GroovyTestCase {

    HudsonServer hudsonServer

    void setUp() {
        hudsonServer = new HudsonServer()
    }

    void testGetProjectsCanParseSingleSuccessfulProject() {
        String projectName = 'project1'
        String projectState = "SUCCESS"

        setUpHudsonFeed projectName, projectState

        play {
            List<Project> actual = hudsonServer.projects
            assertEquals 1, actual.size()
            assertEquals projectName, actual[0].name
            assertEquals passed, actual[0].state
        }
    }

    void testGetProjectsCanParseSingleFailingProject() {
        String projectName = 'project2'
        String projectState = "FAILURE"

        setUpHudsonFeed projectName, projectState

        play {
            List<Project> actual = hudsonServer.projects
            assertEquals 1, actual.size()
            assertEquals projectName, actual[0].name
            assertEquals failed, actual[0].state
        }
    }

    void testGetProjectsCanParseSingleAbortedProject() {
        String projectName = 'project2'
        String projectState = "ABORTED"

        setUpHudsonFeed projectName, projectState

        play {
            List<Project> actual = hudsonServer.projects
            assertEquals 1, actual.size()
            assertEquals projectName, actual[0].name
            assertEquals aborted, actual[0].state
        }
    }

    void testGetProjectsCanParseSingleBuildingProject() {
        String projectName = 'project2'
        String projectState = "null"

        setUpHudsonFeed projectName, projectState

        play {
            List<Project> actual = hudsonServer.projects
            assertEquals 1, actual.size()
            assertEquals projectName, actual[0].name
            assertEquals building, actual[0].state
        }
    }

    void testGetProjectsCanParseSyndFeedObjectWithMultipleEntries() {
        List<String> projectNames = ['myproject', 'yourproject', 'hisproject']

        List<SyndEntry> feedEntries = []
        projectNames.each {String projectName ->
            feedEntries << mock(SyndEntry) {
                title.returns "$projectName #123 (SUCCESS)"
            }
        }

        SyndFeed mockSyndFeed = mock(SyndFeed) {
            entries.returns feedEntries
        }

        hudsonServer.feedRetriever = mock(FeedRetriever) {
            update().returns mockSyndFeed
        }

        play {
            List<Project> actual = hudsonServer.projects
            assertEquals projectNames.size(), actual.size()
            projectNames.eachWithIndex {String projectName, int index ->
                assertEquals projectName, actual[index].name
                assertEquals passed, actual[index].state
            }
        }
    }

    void setUpHudsonFeed(String projectName, String projectState) {
        SyndEntry mockSyndEntry = mock(SyndEntry) {
            title.returns "$projectName #123 ($projectState)"
        }
        List<SyndEntry> feedEntries = [mockSyndEntry]

        SyndFeed mockSyndFeed = mock(SyndFeed) {
            entries.returns feedEntries
        }

        hudsonServer.feedRetriever = mock(FeedRetriever) {
            update().returns mockSyndFeed
        }
    }

}