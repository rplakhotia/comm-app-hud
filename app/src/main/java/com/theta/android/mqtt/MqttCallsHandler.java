package com.theta.android.mqtt;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;

import com.theta.android.Constants;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class MqttCallsHandler {

    private String clientID;
    private String clientHandle;
    private Context context;
    private final int QoS_ZERO = 0;
    private final int QoS_ONE = 1;
    private final int QoS_TWO = 2;

    public String getClientHandle() {
        return clientHandle;
    }

    private final String CLASS_TAG = "MqttCallsHandler";

    private static MqttCallsHandler mqttCallsHandler = null;

    private MqttCallsHandler(Context context) {
        this.context = context;

        clientID = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        clientHandle = getServerURI() + clientID;
    }

    public static MqttCallsHandler getInstance(Context context) {

        if (mqttCallsHandler == null) {
            mqttCallsHandler = new MqttCallsHandler(context);
        }
        return mqttCallsHandler;
    }

    public void subscribe(String topic) {

        Connection c = Connections.getInstance(context).getConnection(
                clientHandle);
        //MqttAndroidClient client = c.getClient();

        try {
            MqttAndroidClient client = c.getClient();
            client.subscribe(topic, QoS_ONE, context, new ActionListener(
                    context, ActionListener.Action.SUBSCRIBE, clientHandle, (Object) null));
        } catch (MqttException e) {
            if (e.getMessage() != null) {
                Log.i(CLASS_TAG, e.getMessage().toString());
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.i(CLASS_TAG, e.getMessage().toString());
            }
        }
    }

    public void publish(String topic, String message) {

        Connection c = Connections.getInstance(context).getConnection(
                clientHandle);
        MqttAndroidClient client = c.getClient();
        //GateLog.i("CLIENT", client.getClientId());
        MqttMessage msg = new MqttMessage(message.getBytes());

        try {
            //GateLog.i("CONTEXT", context.getPackageName());
            client.publish(topic, msg.getPayload(), QoS_ONE, false, context, new ActionListener(context,
                    ActionListener.Action.PUBLISH, clientHandle, (Object) null));
        } catch (MqttPersistenceException e) {
            Log.i(CLASS_TAG, e.getMessage());
        } catch (MqttException e) {
            Log.i(CLASS_TAG, e.getMessage());
        }
    }

    public void unSubscribe(String topic){
        try {
            Connection c = Connections.getInstance(context).getConnection(
                    clientHandle);
            MqttAndroidClient client = c.getClient();
            client.unsubscribe(topic, context, new ActionListener(context,
                    ActionListener.Action.UNSUBCRIBE, clientHandle, (Object) null));

        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.i(CLASS_TAG, e.getMessage().toString());
            }
        }
    }

    public void disconnect(){
        try {
            Connection c = Connections.getInstance(context).getConnection(
                    clientHandle);
            MqttAndroidClient client = c.getClient();
            client.disconnect();
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }
    public String getClientID() {
        return clientID;
    }

    public String getServerURI(){
        /*if (BuildConfig.DEBUG_MODE){
            return  "tcp://" + Constants.MQTT.MQTT_HOST_DEV + ":" + Constants.MQTT.port;
        }*/
        return "tcp://" + Constants.MQTT.MQTT_HOST_PROD + ":" + Constants.MQTT.port;
    }
}
