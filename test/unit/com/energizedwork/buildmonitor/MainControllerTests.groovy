package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.BuildState.*
import static com.energizedwork.buildmonitor.ConfigurationState.*

import grails.test.ControllerUnitTestCase
import org.gmock.WithGMock

@WithGMock
class MainControllerTests extends ControllerUnitTestCase {

    void testIndexRedirectsToConfigureIfSystemIsNotConfigured() {
        controller.configuration = mock(Configuration) {
            state.returns(unconfigured)
        }

        play {
            controller.index()
        }

        assertEquals 'configure', controller.redirectArgs.controller
    }

    void testIndexPutsBuildMonitorStateOnModel() {
        controller.configuration = mock(Configuration) {
            state.returns(configured).atLeastOnce()
        }

        controller.buildMonitorService = mock(BuildMonitorService) {
            state.returns(failed)
        }

        play {
            controller.index()
        }

        assertEquals 'unexpected view', 'index', controller.modelAndView.view
        assertEquals 'unexpected model', [state:failed], controller.modelAndView.model                
    }

}