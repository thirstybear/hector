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
import java.text.SimpleDateFormat

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
            lastUpdate.returns(new Date())
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
            lastUpdate.returns(new Date())
        }

        play {
            controller.index()
        }

        assertEquals 'unexpected view', 'index', controller.modelAndView.view
        assertEquals 'unexpected model', [state: passed], controller.modelAndView.model
    }

    void testIndexShouldPopulateLastModifiedHttpHeaderWithLastUpdateTime() {
        setConfigured()

        Date startOfEpoch = new Date(0L)
        controller.buildMonitor = mock(BuildMonitor) {
            state.returns(passed)
            lastUpdate.returns startOfEpoch
        }

        play {
            controller.index()
        }

        assertEquals 'Thu, 01 Jan 1970 01:00:00 GMT', controller.response.getHeader('Last-Modified')
        assertEquals 200, controller.response.status
    }

    void testIndexShouldReturn304IfNotModifiedSincePrevious() {
        setConfigured()

        Date startOfEpoch = new Date(0L)
        controller.buildMonitor = mock(BuildMonitor) {
            lastUpdate.returns(startOfEpoch).atLeastOnce()
        }

        controller.request.addHeader('If-Modified-Since', 'Thu, 01 Jan 1970 01:10:00 GMT')

        play {
            controller.index()
        }

        assertEquals 'Thu, 01 Jan 1970 01:00:00 GMT', controller.response.getHeader('Last-Modified')
        assertEquals 304, controller.response.status
    }

    void testIndexShouldReturn304IfNotModifiedWithin1Second() {
        setConfigured()

        Date laterThanIfModifiedSince = new Date(10L)
        controller.buildMonitor = mock(BuildMonitor) {
            lastUpdate.returns(laterThanIfModifiedSince).atLeastOnce()
        }

        controller.request.addHeader('If-Modified-Since', 'Thu, 01 Jan 1970 01:00:00 GMT')

        play {
            controller.index()
        }

        assertEquals 'Thu, 01 Jan 1970 01:00:00 GMT', controller.response.getHeader('Last-Modified')
        assertEquals 304, controller.response.status
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