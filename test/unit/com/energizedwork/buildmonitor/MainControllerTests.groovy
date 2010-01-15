package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.ConfigurationState.*

import grails.test.ControllerUnitTestCase
import org.gmock.WithGMock

@WithGMock
class MainControllerTests extends ControllerUnitTestCase {

    void testIndexRenderIndexViewAndPutsConfigurationStateOnModel() {
        controller.configuration = mock(Configuration) {
            state.returns(configured).atLeastOnce()
        }

        play {
            controller.index()
        }

        assertEquals 'unexpected view', 'index', controller.modelAndView.view
        assertEquals 'unexpected model', [state:configured], controller.modelAndView.model
    }

    void testIndexRedirectsToConfigureIfSystemIsNotConfigured() {
        controller.configuration = mock(Configuration) {
            state.returns(unconfigured)
        }

        play {
            controller.index()
        }

        assertEquals 'configure', controller.redirectArgs.controller
    }

}