package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.BuildState.*
import com.energizedwork.buildmonitor.hudson.HudsonServer
import org.apache.commons.lang.time.DateUtils

class BuildMonitor {

    BuildState state = failed
    HudsonServer hudsonServer
    List<Project> projects

    boolean forceClientReload
    Date lastUpdate

    boolean hasChanged(Date lastClientUpdate) {
        boolean result = forceClientReload

        if(!result) {
            if(lastClientUpdate && lastUpdate) {
                result = lastUpdate.after(lastClientUpdate)
            } else {
                result = true
            }
        }

        forceClientReload = false

        return result
    }

    void update() {

        List<Project> newProjects = hudsonServer.projects

        if (projects != newProjects) {
            projects = newProjects

            if(projects.find { it.state == failed }) {
                state = failed
            } else {
                state = passed
            }

            setLastUpdate(new Date())
            forceClientReload = true
        }

    }

    List<Project> getFailedProjects() {
        projects.findAll {
            it.state == failed            
        }.sort { it.name }
    }

    void setLastUpdate(Date newLastUpdate) {
        if(newLastUpdate) {
            if(DateUtils.getFragmentInMilliseconds(newLastUpdate, Calendar.SECOND)) {
                newLastUpdate = DateUtils.truncate(newLastUpdate, Calendar.SECOND)
                newLastUpdate = DateUtils.addSeconds(newLastUpdate, 1)
            }
        }

        lastUpdate = newLastUpdate
    }

}
