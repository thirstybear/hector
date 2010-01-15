package com.energizedwork.buildmonitor

import grails.test.*
import org.gmock.WithGMock

@WithGMock
class ConfigureControllerTests extends ControllerUnitTestCase {

    void testSaveStoresHudsonURLInConfiguration() {
        String expectedUrl = "urandomstring"

        controller.configuration = mock(Configuration) {
            url.set(expectedUrl)
        }

        play {
            controller.params.url = expectedUrl

            controller.save()

            assertEquals 'main', controller.redirectArgs.controller 
        }

    }
}
