$(function() {

    $.PeriodicalUpdater('/', {
        cache: true,
        data: '', // array of values to be passed to the page - e.g. {name: "John", greeting: "hello"}
        method: 'get', // method; get or post
        minTimeout: 1000, // starting value for the timeout in milliseconds
        maxTimeout: 8000, // maximum length of time between requests
        multiplier: 1, // if set to 2, timerInterval will double each time the response hasn't changed (up to maxTimeout)
        type: 'text', // response type - text, xml, json, etc. See $.ajax config options
        maxCalls: 0, // maximum number of calls. 0 = no limit.
        autoStop: 0 // automatically stop requests after this many returns of the same data. 0 = disabled.
    }, function(data) {
        location.reload();
    });

});
