package com.energizedwork.buildmonitor

class Change {
    String owner
    String checkinMsg

    Change(String checkinString) {
        checkinMsg = checkinString;
    }

    String getOwner() {
       if (!this.owner) {
           checkinMsg.find(/\[(.*)\]/) { match, name ->
               owner = name
           }
       }

       return owner ?: null
    }
}