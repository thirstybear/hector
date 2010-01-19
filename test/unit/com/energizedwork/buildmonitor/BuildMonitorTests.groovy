package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.BuildState.*
import org.gmock.WithGMock
import com.energizedwork.buildmonitor.hudson.HudsonServer

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
        assertEquals ([], buildMonitor.failedProjects)                
    }

    void testGetFailedProjectsReturnsEmptyListIfNoProjects() {
        buildMonitor.projects = null
        assertEquals ([], buildMonitor.failedProjects)
    }

    void testGetFailedProjectsReturnsListOfFailedProjectsWhenFailed() {
        Project failingProject = failedProject
        buildMonitor.projects = [passedProject, failingProject, passedProject]
        assertEquals ([failingProject], buildMonitor.failedProjects)
    }

    void setUpHudsonServer(List<Project> result) {
        buildMonitor.hudsonServer = mock(HudsonServer) {
            projects.returns(result)
        }
    }

    Project getFailedProject() {
        return new Project(state:failed)
    }

    Project getPassedProject() {
        return new Project(state:passed)
    }

}