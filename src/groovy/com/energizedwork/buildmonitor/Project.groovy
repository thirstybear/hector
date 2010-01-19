package com.energizedwork.buildmonitor


class Project {

    String name
    BuildState state

    String toString() {
        return "$name ($state)"
    }

}