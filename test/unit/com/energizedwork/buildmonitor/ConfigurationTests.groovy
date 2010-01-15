package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.ConfigurationState.*

class ConfigurationTests extends GroovyTestCase {

    void testConfigurationDefaultsToUnconfigured() {
        Configuration configuration = new Configuration()
        assertEquals unconfigured, configuration.state
    }

    void testConfigurationReturnsUnconfiguredIfNoRssUrlSpecified() {
        Configuration configuration = new Configuration()

        configuration.url = 'file:///some/file'
        assertNotSame 'State should be configured when URL defined', unconfigured, configuration.state

        configuration.url = null
        assertEquals unconfigured, configuration.state
    }

}