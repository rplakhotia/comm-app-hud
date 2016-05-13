package com.theta.android;

public class Constants {

    public class SharedPrefKeys {
        public static final String PREFS_NAME = "wifi.conf";
    }

    public class MQTT {
        public static final String MQTT_HOST_PROD = "iot.eclipse.org";
        public static final int port = 1883;
        public static final String CLIENT_CONNECTED = "client_connected";
        public static final String DECONFIGURE_AND_RESET = "{\"deconfigure\":1,\"reset\":1}";
        public static final int KEEP_ALIVE_INTERVAL = 60;
        public static final boolean CLEAN_SESSION = false ;
        public static final String LAST_WILL_MESSAGE = "offline";
        public static final int MQTT_RETRY_INTERVAL = 2000 ;
    }
}
