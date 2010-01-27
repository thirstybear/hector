package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.BuildState.*
import com.energizedwork.buildmonitor.hudson.HudsonServer

class BuildMonitor {

    BuildState state = failed
    HudsonServer hudsonServer
    List<Project> projects

    boolean changed
    Date lastUpdate = new Date(0L)

    List<Project> getProjects() {
        changed = false
        return projects
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

            // todo move date rounding to here?
            lastUpdate = new Date()
            changed = true
        }

    }

    List<Project> getFailedProjects() {
        projects.findAll {
            it.state == failed            
        }.sort { it.name }
    }

}
