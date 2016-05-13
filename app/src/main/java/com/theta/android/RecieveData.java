package com.theta.android;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.theta.android.mqtt.ActionListener;
import com.theta.android.mqtt.Connection;
import com.theta.android.mqtt.Connections;
import com.theta.android.mqtt.MqttCallsHandler;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class RecieveData extends Service implements MqttCallback{

    private final String topic = "handgesture";
    private final String intent_from_service = "INTENT_FROM_SERVICE";

    private MqttCallsHandler mqttCallsHandler;
    private static final String ServiceHandler = "ServiceHandler";
    private Context mApplicationContext ;
    private String clientId = null;
    private String clientHandle = null;
    private MqttAndroidClient client = null;
    private boolean sslConnection = false;

    public RecieveData() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerReceiver(messageBroadcastReceiver,new IntentFilter(Constants.MQTT.CLIENT_CONNECTED));

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void connectionLost(Throwable throwable) {

        if (!Connections.getInstance(getApplicationContext()).getConnection(clientHandle).isConnected())
            connectClient();
        else System.out.println("connected or connecting");

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

        System.out.println("s = " + s);
        System.out.println("mqttMessage.toString() = " + mqttMessage.toString());

        if(TextUtils.equals(s,topic)){
            Intent intent = new Intent(intent_from_service);
            intent.putExtra("TOPIC",topic);
            intent.putExtra("MESSAGE",mqttMessage.toString());
            this.sendBroadcast(intent);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(ServiceHandler, "starting the service");

        initMqttVariables();
        mApplicationContext = getApplicationContext();
        connectClient();

        if (Connections.getInstance(getApplicationContext()).getConnections().size() != 0)
            Connections.getInstance(getApplicationContext()).getConnection(clientHandle).
                    getClient().registerResources(getApplicationContext());

        return START_STICKY;
    }

    private void connectClient() {

        System.out.println("MqttServiceHandler.connectClient");
        MqttConnectOptions conOpt = new MqttConnectOptions();

        client = Connections.getInstance(getApplicationContext()).createClient(mApplicationContext,
                mqttCallsHandler.getServerURI(), clientId);
        Connection conn = new Connection(clientHandle, clientId, Constants.MQTT.MQTT_HOST_PROD, Constants.MQTT.port,
                mApplicationContext, client, sslConnection);
        //client.setTraceCallback(new com.switchkit.android.mqtt.MqttTrace());
        //client.setTraceEnabled(true);
        conOpt.setCleanSession(Constants.MQTT.CLEAN_SESSION);
        conOpt.setKeepAliveInterval(60);
        conOpt.setWill("lastwill", Constants.MQTT.LAST_WILL_MESSAGE.getBytes(), 1, true);

        conn.addConnectionOptions(conOpt);
        Connections.getInstance(mApplicationContext).addConnection(conn);

        try {
            client.connect(conOpt, null, new ActionListener(mApplicationContext, ActionListener.Action.CONNECT,
                    clientHandle, (Object[]) null));
            System.out.println("client.toString() = " + client.toString());
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(ServiceHandler, e.getMessage());
        } catch (Exception e) {
            Log.i(ServiceHandler, e.getMessage());
        }
    }

    private void initMqttVariables() {
        //clientId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        clientId = "1234567890987654321";
        mqttCallsHandler = MqttCallsHandler.getInstance(getApplicationContext());
        clientHandle = mqttCallsHandler.getServerURI() + clientId;
    }

    public BroadcastReceiver messageBroadcastReceiver  = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(TextUtils.equals(action, Constants.MQTT.CLIENT_CONNECTED)){
                //mqttCallsHandler.subscribe(mac_malti + "/out");
                try {
                    client.subscribe(topic,2);
                } catch (MqttException e) {
                    System.out.println(e.getMessage());
                }
                /*mqttCallsHandler.subscribe(mac_manoj_hyd + "/lastwill");
                mqttCallsHandler.subscribe(mac_manoj_hyd + "/online");*/
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageBroadcastReceiver);
    }
}
