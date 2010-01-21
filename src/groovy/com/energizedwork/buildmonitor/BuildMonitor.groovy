package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.BuildState.*
import com.energizedwork.buildmonitor.hudson.HudsonServer

class BuildMonitor {

    BuildState state = failed
    HudsonServer hudsonServer
    List<Project> projects

    Date lastUpdate = new Date(0L)

    void update() {

        List<Project> newProjects = hudsonServer.projects

        if (projects != newProjects) {
            projects = newProjects

            if(projects.find { it.state == failed }) {
                state = failed
            } else {
                state = passed
            }

            lastUpdate = new Date()
        }
    }

    List<Project> getFailedProjects() {
        projects.findAll {
            it.state == failed            
        }.sort { it.name }
    }

}
