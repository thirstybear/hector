$(function() {
    window.setInterval ("checkForUpdate()", 10000)
});

function checkForUpdate() {
    $.ajax({
    url: '/',
    type: 'HEAD',
    ifModified: true,

    beforeSend: function(xhr) {
        xhr.setRequestHeader('If-Modified-Since', new Date(window.document.lastModified).toUTCString());
    },     

    complete: function(request, textStatus) {
            if (request.status != 304) {
                window.location.reload();
            }
        }
    });
}

