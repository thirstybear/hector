package com.energizedwork.buildmonitor.job

import org.gmock.WithGMock
import com.energizedwork.buildmonitor.BuildMonitor

@WithGMock
class BuildMonitorJobTests extends GroovyTestCase {

    void testBuildMonitorJobCallsUpdateOnBuildMonitorWhenRun() {
        BuildMonitorJob buildMonitorJob = new BuildMonitorJob()

        buildMonitorJob.buildMonitor = mock(BuildMonitor) {
            update()
        }
        
        play {
            buildMonitorJob.execute()
        }
    }

}