package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.BuildState.*

public class ProjectTests extends GroovyTestCase {
    void testOwnersReturnsSetOfOwners() {
        List<Change> changeset = []
        changeset << new Change(checkinMsg:"[chris] wibble")
        changeset << new Change(checkinMsg:"[chris] wobble")
        changeset << new Change(checkinMsg:"[gus] tweaking stuffz")
        changeset << new Change(checkinMsg:"[gus|chris] trashing ur teztz")
        changeset << new Change(checkinMsg:"[gus|simon] trashing ur teztz more")

        Project project = new Project(changeset:changeset)
        String[] owners = project.owners
        
        assertEquals 3, owners.length
        assertTrue 'gus' in owners
        assertTrue 'simon' in owners
        assertTrue 'chris' in owners
    }

    void testEquals() {
        Project p1 = new Project();
        assertEquals false, p1.equals(null)
        assertEquals true, p1.equals(p1)

        Project p2 = new Project()
        assertEquals true, p1.equals(p2)

        p1.name = 'new name'
        assertEquals false, p1.equals(p2)
        p2.name = 'badname'
        assertEquals false, p1.equals(p2)
        p2.name = 'new name'
        assertEquals true, p1.equals(p2)

        p1.state = failed
        assertEquals false, p1.equals(p2)
        p2.state = passed
        assertEquals false, p1.equals(p2)
        p2.state = failed
        assertEquals true, p1.equals(p2)
    }


    void testHashCode() {
        Project p1 = new Project();
        Project p2 = new Project();

        assertEquals(p1.hashCode(), p2.hashCode())

        p1.name = 'name'
        p1.state = passed
        p2.name = 'name'
        p2.state = passed

        assertEquals(p1.hashCode(), p2.hashCode())
    }

}
