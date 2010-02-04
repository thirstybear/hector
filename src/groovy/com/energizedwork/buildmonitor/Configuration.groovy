package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.ConfigurationState.*
import java.util.prefs.Preferences

class Configuration {
    ConfigurationState state = unconfigured
    String url
    Preferences preferences

    String getUrl() {
        if (!url) {
            url = getPreferences().get('url', null)
        }

        url
    }

    ConfigurationState getState() {
        if (!url) {
            getUrl()
        }

        state
    }

    void setUrl(String url) {
        this.url = url
        if (url) {
            getPreferences().put('url', url)
        } else {
            getPreferences().remove('url')
        }
        this.state = url ? configured : unconfigured
    }

    void reset() {
        setUrl(null)
        getPreferences().clear()
    }

    private Preferences getPreferences() {
        if (!preferences) {
            preferences = Preferences.userRoot().node('com/energizedwork/buildmonitor')
        }

        preferences
    }
}