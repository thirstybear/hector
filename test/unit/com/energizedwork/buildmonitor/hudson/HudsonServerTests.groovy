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
import java.text.SimpleDateFormat

@WithGMock
class HudsonServerTests extends GroovyTestCase {

    HudsonServer hudsonServer
    String projectBaseLink = 'http://mockhudsonproject'
    String buildXmlLink = "${projectBaseLink}/12/"

    void setUp() {
        hudsonServer = new HudsonServer()
    }

    void testGetProjectsCanParseSingleSuccessfulProject() {
        String projectName = 'project1'
        String projectState = SUCCESS
        String publishedDateString = '2010-01-07T22:00:52Z'
        Date expectedPublishedDate = new SimpleDateFormat(HudsonServer.DATE_FORMAT).parse(publishedDateString)

        setUpHudsonServer projectName, projectState, expectedPublishedDate

        play {
            List<Project> actual = hudsonServer.projects
            assertEquals 1, actual.size()
            assertEquals projectName, actual[0].name
            assertEquals passed, actual[0].state
            assertEquals expectedPublishedDate, actual[0].published
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
                title.returns ("$projectName #123 (${SUCCESS})").atLeastOnce()
                link.returns(linkUrl).atLeastOnce()
                publishedDate.returns(new Date())
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
                title.returns("$projectName #123 (${SUCCESS})").atLeastOnce()
                link.returns(linkUrl).stub()
                publishedDate.returns(new Date()).stub()
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

            assertEquals 3, changeset.size()

            assertEquals('gus', changeset[0].owners[0])
            assertEquals('chris', changeset[1].owners[0])
            assertEquals('simon', changeset[1].owners[1])
            assertEquals('fatfreddyscat', changeset[2].owners[0])

        }


    }

    public void testGetProjectsShouldCopeWithEmptyChangeSetsInProjectXml() {
        String projectName = 'failproject'
        String projectState = FAILURE

        setUpHudsonFeed projectName, projectState, buildXmlLink
        setUpHudsonBuildXmlWithEmptyChangeset buildXmlLink

        play {
            List<Project> actual = hudsonServer.projects
            assertEquals 1, actual.size()

            Project project = actual[0]
            assertEquals projectName, project.name
            assertEquals failed, project.state

            List<Change> changeset = project.changeset
            assertEquals 0, changeset.size()
        }
    }

    void setUpHudsonServer(String projectName, String projectState, Date publishedDate = new Date()) {
        setUpHudsonFeed(projectName, projectState, buildXmlLink, publishedDate)
        setUpHudsonBuildXml(buildXmlLink)
    }

    void setUpHudsonFeed(String projectName, String projectState, String linkUrl, Date pubDate = new Date()) {
        SyndEntry mockSyndEntry = mock(SyndEntry) {
            title.returns("$projectName #123 ($projectState)").atLeastOnce()
            link.returns(linkUrl).atLeastOnce()
            publishedDate.returns(pubDate).atLeastOnce()
        }
        List<SyndEntry> feedEntries = [mockSyndEntry]

        SyndFeed mockSyndFeed = mock(SyndFeed) {
            entries.returns feedEntries
        }

        hudsonServer.feedRetriever = mock(FeedRetriever) {
            update().returns mockSyndFeed
        }
    }

    void setUpHudsonBuildXmlWithEmptyChangeset(String linkUrl) {
        def xml = loadTestDataFile('noChangeSet')

        hudsonServer.xmlDocumentRetriever = mock(XmlDocumentRetriever) {
            getXml("${linkUrl}api/xml").returns(xml).atLeastOnce()
        }
    }

    void setUpHudsonBuildXml(String linkUrl) {
        def xml12 = loadTestDataFile('failingBuild12')
        def xml11 = loadTestDataFile('failingBuild11')
        def xml10 = loadTestDataFile('successfulBuild10')

        String internalProjectBaseLink = projectBaseLink    // WTF Why do we need this?

        hudsonServer.xmlDocumentRetriever = mock(XmlDocumentRetriever) {
            getXml("${internalProjectBaseLink}/12/api/xml").returns(xml12).stub()
            getXml("${internalProjectBaseLink}/11/api/xml").returns(xml11).stub()
            getXml("${internalProjectBaseLink}/10/api/xml").returns(xml10).stub()
        }
    }

    private def loadTestDataFile(String typeOfData) {
        File failingBuildXml = new File("test/resources/${typeOfData}.xml")
        assertTrue 'Test resource file not found', failingBuildXml.exists()

        def xml = new XmlParser().parse(failingBuildXml)
        xml
    }


    void testGetProjectBaseUrl() {
        String buildBase = hudsonServer.getProjectBaseUrl('http://build.energylab.ew/hudson/job/Backup-EnergyLab-DNS/277/')
        assertEquals 'http://build.energylab.ew/hudson/job/Backup-EnergyLab-DNS', buildBase
    }

    void testGetProjectBuildNumber() {
        int buildNo = hudsonServer.getProjectBuildNumber('http://build.energylab.ew/hudson/job/Backup-EnergyLab-DNS/277/')
        assertEquals 277, buildNo
    }
}