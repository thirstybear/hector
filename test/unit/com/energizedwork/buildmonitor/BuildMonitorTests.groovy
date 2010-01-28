package com.energizedwork.buildmonitor

import com.energizedwork.buildmonitor.BuildMonitor
import com.energizedwork.buildmonitor.Project
import com.energizedwork.buildmonitor.hudson.HudsonServer
import org.gmock.WithGMock
import static com.energizedwork.buildmonitor.BuildState.failed
import static com.energizedwork.buildmonitor.BuildState.passed

@WithGMock
class BuildMonitorTests extends GroovyTestCase {

    BuildMonitor buildMonitor

    void setUp() {
        buildMonitor = new BuildMonitor()
    }

    void testUpdateShouldSetStateToPassWhenHudsonGreen() {
        setUpHudsonServer([passedProject])

        play {
            buildMonitor.update()
            assertEquals passed, buildMonitor.state
        }
    }

    void testUpdateShouldSetStateToFailWhenHudsonRed() {
        setUpHudsonServer([failedProject])

        play {
            buildMonitor.update()
            assertEquals failed, buildMonitor.state
        }
    }

    void testUpdateSetsStateToFailedIfAnyProjectHasFailed() {
        setUpHudsonServer([passedProject, failedProject, passedProject])

        play {
            buildMonitor.update()
            assertEquals failed, buildMonitor.state
        }
    }

    void testGetFailedProjectsReturnsEmptyListIfNoFailures() {
        buildMonitor.projects = []
        assertEquals([], buildMonitor.failedProjects)
    }

    void testGetFailedProjectsReturnsEmptyListIfNoProjects() {
        buildMonitor.projects = null
        assertEquals([], buildMonitor.failedProjects)
    }

    void testGetFailedProjectsReturnsListOfFailedProjectsWhenFailedInAlphabeticalOrder() {
        Project failingProjectA = getFailedProject('A')
        Project failingProjectZ = getFailedProject('Z')
        buildMonitor.projects = [passedProject, failingProjectZ, passedProject, failingProjectA]
        assertEquals([failingProjectA, failingProjectZ], buildMonitor.failedProjects)
    }

    void testUpdateShouldNotUpdateLastUpdateTimeIfNoProjectChange() {
        List<Project> allPass1 = [passedProject, passedProject, passedProject]
        List<Project> allPass2 = [passedProject, passedProject, passedProject]

        buildMonitor.hudsonServer = mock(HudsonServer) {
            projects.returns(allPass1)
            projects.returns(allPass2)
        }

        play {
            buildMonitor.update()
            Date update1 = buildMonitor.lastUpdate
            waitForAtLeast(1)
            buildMonitor.update()
            Date update2 = buildMonitor.lastUpdate

            int t1 = update1.getTime()
            int t2 = update2.getTime()
            assertEquals "update times different by ${t2 - t1} ms", update1, update2
        }
    }

    void testUpdateShouldSetHasChangedIfProjectChanges() {

        List<Project> allPass = [passedProject, passedProject, passedProject]
        List<Project> oneFail = [passedProject, failedProject, passedProject]

        buildMonitor.hudsonServer = mock(HudsonServer) {
            projects.returns(allPass)
            projects.returns(oneFail)
        }

        play {
            buildMonitor.update()
            Date update1 = buildMonitor.lastUpdate
            assertTrue buildMonitor.hasChanged(update1)

            buildMonitor.update()
            Date update2 = buildMonitor.lastUpdate
            assertTrue buildMonitor.hasChanged(update2)
        }
    }

    void testHasChangedReturnsFalseGivenSameDate() {        
        Date date = new Date(1000L)

        assertBuildMonitorHasNotChanged date, date
    }

    void testHasChangedReturnsFalseGivenLaterDate() {
        Date clientDate = new Date(1000L)
        Date serverDate = new Date(0L)

        assertBuildMonitorHasNotChanged clientDate, serverDate
    }

    void testHasChangedReturnsTrueGivenNoClientDate() {
        Date clientDate = null
        Date serverDate = new Date(0L)

        assertBuildMonitorHasChanged clientDate, serverDate
    }

    void testHasChangedReturnsTrueGivenNoServerDate() {
        Date clientDate = new Date(1000L)
        Date serverDate = null

        assertBuildMonitorHasChanged clientDate, serverDate
    }

    void testHasChangedReturnsTrueGivenNoClientOrServerDate() {
        Date clientDate = null
        Date serverDate = null

        assertBuildMonitorHasChanged clientDate, serverDate
    }

    void testHasChangedReturnsTrueGivenEarlierDate() {
        Date clientDate = new Date(0L)
        Date serverDate = new Date(1000L)

        assertBuildMonitorHasChanged clientDate, serverDate
    }

    void testHasChangedReturnsTrueGivenInternalUpdateFlag() {
        Date date = new Date(1000L)
        buildMonitor.forceClientReload = true

        assertBuildMonitorHasChanged date, date
    }

    void testGetLastUpdateDoesNotRoundUpOnSecondBoundary() {
        Date oneSecond = new Date(1000L)

        buildMonitor.lastUpdate = oneSecond
        assertEquals oneSecond, buildMonitor.lastUpdate
    }

    void testGetLastUpdateAlwaysRoundsUpToNearestSecond() {
        Date onePointOneSeconds = new Date(1100L)
        Date twoSeconds = new Date(2000L)
        
        buildMonitor.lastUpdate = onePointOneSeconds
        assertEquals twoSeconds, buildMonitor.lastUpdate
    }

    void assertBuildMonitorHasChanged(Date clientDate, Date serverDate) {
        buildMonitor.lastUpdate = serverDate
        assertTrue buildMonitor.hasChanged(clientDate)
    }

    void assertBuildMonitorHasNotChanged(Date clientDate, Date serverDate) {
        buildMonitor.lastUpdate = serverDate
        assertFalse buildMonitor.hasChanged(clientDate)
    }
    
    void setUpHudsonServer(List<Project> result) {
        buildMonitor.hudsonServer = mock(HudsonServer) {
            projects.returns(result).atLeastOnce()
        }
    }

    Project getFailedProject(String name = 'failedProject') {
        return new Project(state: failed, name: name)
    }

    Project getPassedProject(String name = 'passedProject') {
        return new Project(state: passed, name: name)
    }

    private void waitForAtLeast(int delayInMillis) {
        int target = System.currentTimeMillis() + delayInMillis

        while (target > System.currentTimeMillis()) {
            // loop
        }
    }
}