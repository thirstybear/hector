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
        assertEquals "update times different by ${t2-t1} ms", update1, update2
      }
  }

    void testUpdateShouldUpdateLastUpdateTimeIfProjectChanges() {

      List<Project> allPass = [passedProject, passedProject, passedProject]
      List<Project> oneFail = [passedProject, failedProject, passedProject]

      buildMonitor.hudsonServer = mock(HudsonServer) {
          projects.returns(allPass)
          projects.returns(oneFail)
      }

      play {
        buildMonitor.update()
        Date update1 = buildMonitor.lastUpdate

        waitForAtLeast(1)

        buildMonitor.update()
        Date update2 = buildMonitor.lastUpdate

        int t1 = update1.getTime()
        int t2 = update2.getTime()
        assertTrue "update times should be different", t2 > t1
      }
  }

    /**
     void testIndexShouldReturn304IfNotModifiedWithin1Second() {
             setConfigured()

             Date laterThanIfModifiedSince = new Date(10L)
             controller.buildMonitor = mock(BuildMonitor) {
                 lastUpdate.returns(laterThanIfModifiedSince).atLeastOnce()
             }

             controller.request.addHeader(IF_MODIFIED_SINCE, GMT_EPOCH)

             play {
                 controller.index()
             }

             assertEquals GMT_EPOCH, controller.response.getHeader(LAST_MODIFIED)
             assertEquals 304, controller.response.status
         }

     */

    void setUpHudsonServer(List<Project> result) {
        buildMonitor.hudsonServer = mock(HudsonServer) {
            projects.returns(result).atLeastOnce()
        }
    }

    Project getFailedProject(String name = 'failedProject') {
        return new Project(state:failed, name:name)
    }

    Project getPassedProject(String name = 'passedProject') {
        return new Project(state:passed, name:name)
    }

    private void waitForAtLeast(int delayInMillis) {
      int target = System.currentTimeMillis() + delayInMillis

      while (target > System.currentTimeMillis()) {
        // loop
      }
    }
}