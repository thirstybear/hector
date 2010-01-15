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

    void testResetClearsUrlAndSetsStateBackToUnconfigured() {
        Configuration configuration = new Configuration(url:'file:///some/file')

        configuration.reset()

        assertNull configuration.url
        assertEquals unconfigured, configuration.state
    }

}