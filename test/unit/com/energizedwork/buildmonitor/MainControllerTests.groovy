package com.energizedwork.buildmonitor

import com.energizedwork.buildmonitor.BuildMonitor
import com.energizedwork.buildmonitor.Configuration
import com.energizedwork.buildmonitor.Project
import grails.test.ControllerUnitTestCase
import org.gmock.WithGMock
import static com.energizedwork.buildmonitor.BuildState.failed
import static com.energizedwork.buildmonitor.BuildState.passed
import static com.energizedwork.buildmonitor.ConfigurationState.configured
import static com.energizedwork.buildmonitor.ConfigurationState.unconfigured

@WithGMock
class MainControllerTests extends ControllerUnitTestCase {

    void testIndexRedirectsToConfigureIfSystemIsNotConfigured() {
        setUnconfigured()

        play {
            controller.index()
        }

        assertEquals 'configure', controller.redirectArgs.controller
    }

    void testIndexPutsBuildMonitorFailStateOnModelWithFailedProjects() {
        setConfigured()

        List<Project> projects = [new Project(name: 'failed', state: failed)]

        controller.buildMonitor = mock(BuildMonitor) {
            state.returns(failed)
            failedProjects.returns projects
        }

        play {
            controller.index()
        }

        assertEquals 'unexpected view', 'index', controller.modelAndView.view
        assertEquals 'unexpected model', [state: failed, failedProjects: projects], controller.modelAndView.model
    }

    void testIndexPutsBuildMonitorPassStateOnModel() {
        setConfigured()

        controller.buildMonitor = mock(BuildMonitor) {
            state.returns(passed)
        }

        play {
            controller.index()
        }

        assertEquals 'unexpected view', 'index', controller.modelAndView.view
        assertEquals 'unexpected model', [state: passed], controller.modelAndView.model
    }


    private void setConfigured() {
        setConfigState(configured)
    }

    private def setUnconfigured() {
        setConfigState(unconfigured)
    }

    private def setConfigState(ConfigurationState configState) {
        controller.configuration = mock(Configuration) {
            state.returns(configState)
        }
    }

}