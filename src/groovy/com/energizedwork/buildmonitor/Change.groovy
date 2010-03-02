package com.energizedwork.buildmonitor

class Change {

    String[] owners = []
    String checkinMsg

    String[] getOwners() {
       if (!this.owners) {
           checkinMsg.find(/\[([^]]*)\]/) { match, ownerString ->
               ownerString = ownerString.replaceAll("\\|", " ")
               owners = ownerString.split()
           }
       }

       return owners
    }

    String toString() {
        "Change: $checkinMsg"
    }
}