package com.energizedwork.buildmonitor

import static com.energizedwork.web.support.HttpConstants.*

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

    final String GMT_EPOCH = 'Thu, 01 Jan 1970 00:00:00 GMT'

    TimeZone originalTimeZone

    void setUp() {
        originalTimeZone = TimeZone.default
        TimeZone.default = TimeZone.getTimeZone(TimeZone.GMT_ID)
        super.setUp()
    }

    void tearDown() {
        super.tearDown()
        TimeZone.default = originalTimeZone
    }

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
            hasChanged(match { it == null }).returns true
            failedProjects.returns projects
            lastUpdate.returns(new Date())
        }

        play {
            controller.index()
        }

        assertEquals 'unexpected view', 'index', controller.modelAndView.viewName
        assertEquals 'unexpected model', [state: failed, failedProjects: projects], controller.modelAndView.model
    }

    void testIndexPutsBuildMonitorPassStateOnModel() {
        setConfigured()

        controller.buildMonitor = mock(BuildMonitor) {
            state.returns(passed)
            hasChanged(match { it == null }).returns true
            lastUpdate.returns(new Date())
        }

        play {
            controller.index()
        }

        assertEquals 'unexpected view', 'index', controller.modelAndView.viewName
        assertEquals 'unexpected model', [state: passed], controller.modelAndView.model
    }

    void testIndexShouldPopulateLastModifiedHttpHeaderWithLastUpdateTime() {        
        setConfigured()

        controller.buildMonitor = mock(BuildMonitor) {
            state.returns passed
            hasChanged(match { it == null }).returns true
            lastUpdate.returns new Date(0L)
        }

        play {
            controller.index()
        }

        assertEquals GMT_EPOCH, controller.response.getHeader(LAST_MODIFIED)
        assertEquals 200, controller.response.status
    }

    void testIndexDoesNotPopulateLastModifiedHttpHeaderIfBuildMonitorDoesNotHaveLastUpdateTime() {        
        setConfigured()

        controller.buildMonitor = mock(BuildMonitor) {
            state.returns passed
            hasChanged(match { it == null }).returns true
            lastUpdate.returns null
        }

        play {
            controller.index()
        }

        assertFalse controller.response.containsHeader(LAST_MODIFIED)
        assertEquals 200, controller.response.status
    }

    void testIndexShouldReturn304IfNotModifiedSincePrevious() {
        setConfigured()

        Date clientIfModifiedSince = new Date(1000 * 60 * 10)
        Date serverLastModified = new Date(1000 * 60 * 5)
        controller.buildMonitor = mock(BuildMonitor) {
            hasChanged(clientIfModifiedSince).returns false
            lastUpdate.returns serverLastModified
        }

        controller.request.addHeader(IF_MODIFIED_SINCE, 'Thu, 01 Jan 1970 00:10:00 GMT')

        play {
            controller.index()
        }

        assertEquals 'Thu, 01 Jan 1970 00:05:00 GMT', controller.response.getHeader(LAST_MODIFIED)
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