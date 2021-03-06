package com.energizedwork.web.support

final class HttpConstants {

    private HttpConstants() {}

    final static String LAST_MODIFIED = 'Last-Modified'    
    final static String IF_MODIFIED_SINCE = 'If-Modified-Since'
    final static String ISO_DATE_FORMAT = 'E, dd MMM yyyy HH:mm:ss z'
    final static int NOT_MODIFIED = 304

}