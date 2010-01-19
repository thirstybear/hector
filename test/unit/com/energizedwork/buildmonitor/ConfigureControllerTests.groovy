package com.energizedwork.buildmonitor

import grails.test.*
import org.gmock.WithGMock

@WithGMock
class ConfigureControllerTests extends ControllerUnitTestCase {

    void testSaveStoresHudsonURLInConfigurationAndUpdatesMonitor() {
        String expectedUrl = "urandomstring"

        controller.configuration = mock(Configuration) {
            url.set(expectedUrl)
        }

        controller.buildMonitor = mock(BuildMonitor) {
            update()
        }

        play {
            controller.params.url = expectedUrl

            controller.save()

            assertEquals 'main', controller.redirectArgs.controller 
        }

    }
}
