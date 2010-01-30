$(function() {

    window.setInterval ("checkForUpdate()", 10000)

});

function checkForUpdate() {
    $.ajax({
    url: '/',
    type: 'HEAD',
    ifModified: true,
    success: function(data) {
            alert (request.status)
            if (data == null) alert ('data is null')
            alert('Load was performed.');
        }
    });
}