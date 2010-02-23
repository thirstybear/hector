package com.energizedwork.buildmonitor.hudson

import static com.energizedwork.buildmonitor.BuildState.*
import static com.energizedwork.buildmonitor.hudson.HudsonServer.*

import org.gmock.WithGMock
import com.energizedwork.buildmonitor.Project
import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.feed.synd.SyndEntry
import com.energizedwork.feed.FeedRetriever
import com.energizedwork.feed.XmlDocumentRetriever
import com.energizedwork.buildmonitor.Change

@WithGMock
class HudsonServerTests extends GroovyTestCase {

    HudsonServer hudsonServer
    String buildXmlLink = 'http://mockhudsonproject/'

    void setUp() {
        hudsonServer = new HudsonServer()
    }

    void testGetProjectsCanParseSingleSuccessfulProject() {
        String projectName = 'project1'
        String projectState = SUCCESS

        setUpHudsonServer projectName, projectState

        play {
            List<Project> actual = hudsonServer.projects
            assertEquals 1, actual.size()
            assertEquals projectName, actual[0].name
            assertEquals passed, actual[0].state
        }
    }

    void testGetProjectsCanParseSingleFailingProject() {
        String projectName = 'project2'
        String projectState = FAILURE

        setUpHudsonServer projectName, projectState

        play {
            List<Project> actual = hudsonServer.projects
            assertEquals 1, actual.size()
            assertEquals projectName, actual[0].name
            assertEquals failed, actual[0].state
        }
    }

    void testGetProjectsCanParseSingleAbortedProject() {
        String projectName = 'project2'
        String projectState = ABORTED

        setUpHudsonServer projectName, projectState

        play {
            List<Project> actual = hudsonServer.projects
            assertEquals 1, actual.size()
            assertEquals projectName, actual[0].name
            assertEquals aborted, actual[0].state
        }
    }

    void testGetProjectsCanParseSingleBuildingProject() {
        String projectName = 'project2'
        String projectState = BUILDING

        setUpHudsonServer projectName, projectState

        play {
            List<Project> actual = hudsonServer.projects
            assertEquals 1, actual.size()
            assertEquals projectName, actual[0].name
            assertEquals building, actual[0].state
        }
    }

    void testGetProjectsCanParseSyndFeedObjectWithMultipleEntries() {
        List<String> projectNames = ['myproject', 'yourproject', 'hisproject']

        String linkUrl = buildXmlLink

        List<SyndEntry> feedEntries = []
        projectNames.each {String projectName ->
            feedEntries << mock(SyndEntry) {
                title.returns "$projectName #123 (${SUCCESS})"
                link.returns(linkUrl).atLeastOnce()
            }
        }

        SyndFeed mockSyndFeed = mock(SyndFeed) {
            entries.returns feedEntries
        }

        hudsonServer.feedRetriever = mock(FeedRetriever) {
            update().returns mockSyndFeed
        }

        setUpHudsonBuildXml linkUrl

        play {
            List<Project> actual = hudsonServer.projects
            assertEquals projectNames.size(), actual.size()
            projectNames.eachWithIndex {String projectName, int index ->
                assertEquals projectName, actual[index].name
                assertEquals passed, actual[index].state
            }
        }
    }

    void testGetProjectsCanParseSyndFeedObjectWithMultipleEntriesButDoesNotDuplicate() {
        List<String> projectNames = ['myproject', 'yourproject', 'myproject']

        String linkUrl = buildXmlLink

        List<SyndEntry> feedEntries = []
        projectNames.each {String projectName ->
            feedEntries << mock(SyndEntry) {
                title.returns "$projectName #123 (${SUCCESS})"
                link.returns(linkUrl).atLeastOnce()
            }
        }

        SyndFeed mockSyndFeed = mock(SyndFeed) {
            entries.returns feedEntries
        }

        hudsonServer.feedRetriever = mock(FeedRetriever) {
            update().returns mockSyndFeed
        }

        setUpHudsonBuildXml linkUrl

        play {
            List<Project> actual = hudsonServer.projects
            assertEquals 2, actual.size()
            assertEquals projectNames[0], actual[0].name
            assertEquals projectNames[1], actual[1].name
            assertEquals passed, actual[0].state
            assertEquals passed, actual[1].state
        }
    }

    void testGetProjectsShouldReturnProjectsWithCheckinUser() {
        /*
            Get link property from RSS
            Get data from link.href
            Parse data for changeset & add to Project
         */

        String projectName = 'failproject'
        String projectState = FAILURE

        setUpHudsonServer projectName, projectState

        play {
            List<Project> actual = hudsonServer.projects
            assertEquals 1, actual.size()

            Project project = actual[0]
            assertEquals projectName, project.name
            assertEquals failed, project.state

            List<Change> changeset = project.changeset
            assertEquals 2, changeset.size()

            assertEquals('gus', changeset[0].owners[0])
            assertEquals('chris', changeset[1].owners[0])

        }


    }
    
//    public void testGetProjectsShouldCopeWithEmptyChangeSetsInProjectXml() {
//        fail 'Write this test!!'
//    }

    void setUpHudsonServer(String projectName, String projectState) {
        setUpHudsonFeed(projectName, projectState, buildXmlLink)
        setUpHudsonBuildXml(buildXmlLink)
    }

    void setUpHudsonFeed(String projectName, String projectState, String linkUrl) {
        SyndEntry mockSyndEntry = mock(SyndEntry) {
            title.returns "$projectName #123 ($projectState)"
            link.returns(linkUrl).atLeastOnce()
        }
        List<SyndEntry> feedEntries = [mockSyndEntry]

        SyndFeed mockSyndFeed = mock(SyndFeed) {
            entries.returns feedEntries
        }

        hudsonServer.feedRetriever = mock(FeedRetriever) {
            update().returns mockSyndFeed
        }
    }

    void setUpHudsonBuildXml(String linkUrl) {
        File failingBuildXml = new File('test/resources/failingBuild.xml')
        assertTrue 'Test resource file not found', failingBuildXml.exists()

        def xml = new XmlParser().parse(failingBuildXml)

        hudsonServer.xmlDocumentRetriever = mock(XmlDocumentRetriever) {
            getXml("${linkUrl}api/xml").returns(xml).atLeastOnce()
        }        
    }

}