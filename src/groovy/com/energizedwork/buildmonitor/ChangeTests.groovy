package com.energizedwork.buildmonitor


class ChangeTests extends GroovyTestCase {

    void testGetOwnerParsesCheckinString() {
        Change change = new Change('[gus] trying to handle spaces in paths *sigh*');

        assertEquals 'gus', change.owner
    }

    void testGetOwnerReturnsNullIfNoOwnerInCheckinString() {
        Change change = new Change('trying to handle spaces in paths *sigh*');

        assertEquals null, change.owner
    }

}