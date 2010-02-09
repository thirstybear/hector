package com.energizedwork.buildmonitor

class Change {
    String owner
    String checkinMsg

    String getOwner() {
       if (!this.owner) {
           checkinMsg.find(/\[(.*)\]/) { match, name ->
               owner = name
           }
       }

       return owner ?: null
    }

    String toString() {
        "Change: $checkinMsg"
    }
}