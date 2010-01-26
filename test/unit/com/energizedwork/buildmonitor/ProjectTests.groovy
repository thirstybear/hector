package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.BuildState.*

public class ProjectTests extends GroovyTestCase {
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