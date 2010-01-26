package com.energizedwork.buildmonitor


class Project {

    String name
    BuildState state

    String toString() {
        return "$name ($state)"
    }

    boolean equals (Object o) {
        boolean equals = false
        if (o instanceof Project) {
            equals = (name == o.name) && state == o.state
        }
        
        return equals
    }

    int hashCode() {
        int hash = 357;

        int nameHash = name ? name.hashCode() : 0
        hash = hash * 31 + nameHash

        int stateHash = state ? state.hashCode() : 0
        hash = hash * 31 + stateHash
        return hash;
    }

}