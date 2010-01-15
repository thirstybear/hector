package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.BuildState.*
import grails.test.*
import org.gmock.WithGMock

@WithGMock
class BuildMonitorServiceTests extends GrailsUnitTestCase {
    BuildMonitorService buildMonitorService

    void setUp() {
        buildMonitorService = new BuildMonitorService()
    }
    
    void testGetStateReturnsFailedWhenProjectFailed() {
        buildMonitorService.configuration = mock(Configuration) {
            url.returns("/fixture/hudsonRss?result=failed")   // TODO change - won't work - no fixture running unit tests
        }

// TODO Looks like we need to break out a 'URL Reader' service/method/closure to actually get to the data, otherwise it's a bitch to test

        assertEquals failed,  buildMonitorService.state
    }
}
