package com.rtbhouse.reporter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public final class HostNameHelper {

    private static final Pattern IP_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private static final String HOST_ENV = "HOST_NAME";

    private HostNameHelper() {
    }

    public static String getHostName() {
        if (System.getenv(HOST_ENV) != null) {
            return System.getenv(HOST_ENV);
        }
        try {
            String host = InetAddress.getLocalHost().getCanonicalHostName();
            if (IP_PATTERN.matcher(host).matches()) {
                return InetAddress.getLocalHost().getHostName();
            } else {
                return host;
            }
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }

    public static String getShortHostName() {
        return getHostName().split("\\.", 2)[0];
    }

}
