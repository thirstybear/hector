package com.energizedwork.buildmonitor

import org.joda.time.*
import grails.test.TagLibUnitTestCase
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException

class HectorTagLibTests extends TagLibUnitTestCase {

    void tearDown() {
        super.tearDown()

        DateTimeUtils.setCurrentMillisSystem()
    }

    void testTimeSinceRequiresValue() {
        shouldFail(GrailsTagException) {
            tagLib.timeSince(value: null)
        }
    }

    void testTimeSinceOutputsFriendlyStringInDaysBetweenGivenDateAndNow() {
        Date now = new Date()
        DateTimeUtils.currentMillisFixed = now.time
        def sinceDate = now - 1

        tagLib.timeSince(value: sinceDate)

        assertEquals "1 day ago", tagLib.out.toString()
    }

    void testTimeSinceOutputsMostSignificantBetweenGivenDateAndNow() {
        Date now = new Date()
        DateTimeUtils.currentMillisFixed = now.time
        def sinceDate = new DateTime().minusMonths(3).minusWeeks(1).minusHours(2).minusMinutes(24).minusSeconds(15).toDate()

        tagLib.timeSince(value: sinceDate)

        assertEquals "3 months and 7 days ago", tagLib.out.toString()
    }

    void testTimeSinceDoesNotOutputZeroElements() {
        Date now = new Date()
        DateTimeUtils.currentMillisFixed = now.time
        def sinceDate = new DateTime().minusMonths(3).minusHours(2).toDate()

        tagLib.timeSince(value: sinceDate)

        assertEquals "3 months and 2 hours ago", tagLib.out.toString()
    }

    void testTimeSinceHandlesShortDifferenceBetweenGivenDateAndNow() {
        Date now = new Date()
        DateTimeUtils.currentMillisFixed = now.time
        def sinceDate = new DateTime().minusSeconds(15).toDate()

        tagLib.timeSince(value: sinceDate)

        assertEquals "15 seconds ago", tagLib.out.toString()
    }

}