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

    void testIndexPutsBuildMonitorFailStateOnModelWithFailedProjects() {
        controller.configuration = mock(Configuration) {
            state.returns(configured).atLeastOnce()
        }

        List<Project> projects = [new Project(name:'failed', state:failed)]

        controller.buildMonitor = mock(BuildMonitor) {
            state.returns(failed)
            failedProjects.returns projects
        }

        play {
            controller.index()
        }

        assertEquals 'unexpected view', 'index', controller.modelAndView.view
        assertEquals 'unexpected model', [state:failed, failedProjects:projects], controller.modelAndView.model                
    }

    void testIndexPutsBuildMonitorPassStateOnModel() {
        controller.configuration = mock(Configuration) {
            state.returns(configured).atLeastOnce()
        }

        controller.buildMonitor = mock(BuildMonitor) {
            state.returns(passed)
        }

        play {
            controller.index()
        }

        assertEquals 'unexpected view', 'index', controller.modelAndView.view
        assertEquals 'unexpected model', [state:passed], controller.modelAndView.model
    }


}