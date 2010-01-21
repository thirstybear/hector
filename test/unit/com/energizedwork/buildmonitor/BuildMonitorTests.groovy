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

    void testGetFailedProjectsReturnsListOfFailedProjectsWhenFailedInAlphabeticalOrder() {
        Project failingProjectA = getFailedProject('A')
        Project failingProjectZ = getFailedProject('Z')
        buildMonitor.projects = [passedProject, failingProjectZ, passedProject, failingProjectA]
        assertEquals ([failingProjectA, failingProjectZ], buildMonitor.failedProjects)
    }

  void testUpdateShouldNotUpdateLastUpdateTimeIfNoProjectChange() {
      setUpHudsonServer([passedProject, passedProject, passedProject])

      play {
        buildMonitor.update()
        Date update1 = buildMonitor.lastUpdate
        waitForAtLeast(1)
        buildMonitor.update()
        Date update2 = buildMonitor.lastUpdate

        int t1 = update1.getTime()
        int t2 = update2.getTime()
        assertEquals "update times different by ${t2-t1} ms", update1, update2
      }
  }

  void testUpdateShouldUpdateLastUpdateTimeIfProjectChanges() {

      Project project1 = passedProject(name:'project1', state:passed)
      Project project2 = passedProject(name:'project2', state:passed)
      Project project3 = passedProject(name:'project3', state:passed)

      setUpHudsonServer([project1, project2, project3])

      play {
        buildMonitor.update()
        Date update1 = buildMonitor.lastUpdate

        project2.state = failed
        waitForAtLeast(1)
        println 'Hudson:' + buildMonitor.hudsonServer.projects

        buildMonitor.update()
        Date update2 = buildMonitor.lastUpdate

        int t1 = update1.getTime()
        int t2 = update2.getTime()
        assertTrue "update times should be different", t2 > t1
      }
  }

    void setUpHudsonServer(List<Project> result) {
        buildMonitor.hudsonServer = mock(HudsonServer) {
            projects.returns(result).atLeastOnce()
        }
    }

    Project getFailedProject(String name = 'failedProject') {
        return new Project(state:failed, name:name)
    }

    Project getPassedProject() {
        return new Project(state:passed)
    }

    private void waitForAtLeast(int delayInMillis) {
      int target = System.currentTimeMillis() + delayInMillis

      while (target > System.currentTimeMillis()) {
        // loop
      }
    }
}