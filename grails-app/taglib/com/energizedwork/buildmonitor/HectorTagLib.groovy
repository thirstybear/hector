package com.energizedwork.buildmonitor

import org.joda.time.DateTime
import org.joda.time.DurationFieldType
import static org.joda.time.DurationFieldType.*
import org.joda.time.Interval
import org.joda.time.PeriodType
import org.joda.time.format.PeriodFormat

class HectorTagLib {

    static namespace = "hector"

    def timeSince = { attrs ->
        if (!attrs.value) throwTagError("Attribute 'value' is required")

        def startTime = new DateTime(attrs.value)
        def endTime = new DateTime()
        Interval interval = new Interval(startTime, endTime)

        def fields = [years(), months(), days(), hours(), minutes(), seconds()]
        def periodType = PeriodType.forFields(fields as DurationFieldType[])
        def period = interval.toPeriod(periodType)
        int count = 0
        fields.each { DurationFieldType field ->
            if (period.get(field) > 0) {
                count++
                if (count > 2) period = period.withField(field, 0)
            }
        }

        out << period.toString(PeriodFormat.default) << " ago"
    }

}