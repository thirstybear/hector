package com.energizedwork.buildmonitor


class ChangeTests extends GroovyTestCase {

    void testGetOwnerParsesSingleUserCheckinString() {
        Change change = new Change(checkinMsg:'[gus] trying to handle spaces in paths *sigh*');

        assertEquals 1, change.owners.length
        assertEquals 'gus', change.owners[0]
    }

    void testGetOwnerParsesMultipleUserCheckinString() {
        Change change = new Change(checkinMsg:'[gus|simon] wibble');

        assertEquals 2, change.owners.length
        assertEquals 'gus', change.owners[0]
        assertEquals 'simon', change.owners[1]
    }

    void testGetOwnerReturnsNullIfNoOwnerInCheckinString() {
        Change change = new Change(checkinMsg:'trying to handle spaces in paths *sigh*');

        assertEquals 0, change.owners.length
    }

}