$(function() {
    updateSpeakerIcon();
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

function toggleSound() {
   toggleCookieValue();
   updateSpeakerIcon();
   window.location.reload();
}

function toggleCookieValue() {
    var playValue = $.cookies.get('playsounds');

    if (playValue == null || playValue == 'no') {
        $.cookies.set('playsounds', 'yes');
    } else {
        $.cookies.set('playsounds', 'no');
    }
}

function updateSpeakerIcon() {
    var playValue = $.cookies.get('playsounds');

    if (playValue == null || playValue == 'no') {
        document['speaker_icon'].src = '/images/sound_off.png';
    } else {
        document['speaker_icon'].src = '/images/sound_on.png';
    }
}