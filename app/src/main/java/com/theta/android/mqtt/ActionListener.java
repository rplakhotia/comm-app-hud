package com.theta.android.mqtt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.theta.android.Constants;
import com.theta.android.MainActivity;
import com.theta.android.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public class ActionListener implements IMqttActionListener {

	public enum Action {
		/** Connect Action **/
		CONNECT,
		/** Disconnect Action **/
		DISCONNECT,
		/** Subscribe Action **/
		SUBSCRIBE,
		/** Publish Action **/
		PUBLISH,
		/** Subscribe AFter MAC change **/
		SUBSCRIBE_NEW_MAC,

		UNSUBCRIBE
	}

	private SharedPreferences settings ;
    private MainActivity mainActivity ;

	private Action action;
	/** The arguments passed to be used for formatting strings **/
	private Object[] additionalArgs;
	/** Handle of the {@link Connection} this action was being executed on **/
	private String clientHandle;
	/** {@link Context} for performing various operations **/
	private Context context;
	private MqttAndroidClient client;
	private MqttCallsHandler mMqttCallsHandler;
	private static final String CLASS_TAG = "ActionListener";


	public ActionListener(Context context, Action action, String clientHandle,
			Object... additionalArgs) {
		this.context = context;
		this.action = action;
		this.clientHandle = clientHandle;
		this.additionalArgs = additionalArgs;
		settings = context.getSharedPreferences(Constants.SharedPrefKeys.PREFS_NAME, 0);
	}

    public  ActionListener(Context context, MainActivity mainActivity, Action action, String clientHandle,
						   Object... additionalArgs){

        if (mainActivity != null){
            this.mainActivity = mainActivity ;
        }
        this.context = context;
        this.action = action;
        this.clientHandle = clientHandle;
        this.additionalArgs = additionalArgs;
        settings = context.getSharedPreferences(Constants.SharedPrefKeys.PREFS_NAME, 0);
    }

	@Override
	public void onSuccess(IMqttToken asyncActionToken) {

		switch (action) {
			case CONNECT:
				connect();
				break;
			case DISCONNECT:
				disconnect();
				break;
			case SUBSCRIBE:
				subscribe();
				break;
			case PUBLISH:
				publish(asyncActionToken);
				break;
			case UNSUBCRIBE:
				unsubscribe();
				break;
			case SUBSCRIBE_NEW_MAC:
				break;
		}
	}

	private void unsubscribe() {
		System.out.println("Topic Unsubscribed");
	}

	private void connect() {

		/*SwitchkitApplication.setclientConnected(true);
        SwitchkitApplication.setStatus(Constants.CLIENT_CONNECTION_STATUS.CLIENT_CONNECTED);*/

		Log.i("Client Connected", clientHandle);
		Intent intent = new Intent(Constants.MQTT.CLIENT_CONNECTED);
		context.sendBroadcast(intent);
	}

	private void publish(IMqttToken asyncActionToken) {
		String actionTaken = context.getString(R.string.toast_pub_success,
				(Object[]) null);
		Log.i("PUBLISH", "Message Published");
	}

	private void subscribe() {

		String actionTaken = context.getString(R.string.toast_sub_success,
				(Object[]) additionalArgs);
		Log.i(CLASS_TAG, "Topic Subscribed");
	}

	private void disconnect() {
		System.out.println("ActionListener.disconnect");

		Connection c = Connections.getInstance(context).getConnection(
				clientHandle);
		String actionTaken = context.getString(R.string.toast_disconnected);
		c.addAction(actionTaken);

		System.out.println("Setting client connection status to not connected...");
		//SwitchkitApplication.setStatus(Constants.CLIENT_CONNECTION_STATUS.CLIENT_NOT_CONNECTED);
	}



	@Override
	public void onFailure(IMqttToken token, Throwable exception) {
		switch (action) {
		case CONNECT:
			connect(exception);
			break;
		case DISCONNECT:
			disconnect(exception);
			break;
		case SUBSCRIBE:
			subscribe(exception);
			break;
		case PUBLISH:
			publish(exception);
			break;
		}

	}

	private void publish(Throwable exception) {

		Connection c = Connections.getInstance(context).getConnection(
				clientHandle);
		String action = context.getString(R.string.toast_pub_failed,
				(Object[]) null);
		c.addAction(action);

	}

	private void subscribe(Throwable exception) {
		Connection c = Connections.getInstance(context).getConnection(
				clientHandle);
		String action = context.getString(R.string.toast_sub_failed,
				(Object[]) additionalArgs);
		c.addAction(action);
	}

	private void disconnect(Throwable exception) {
		Connection c = Connections.getInstance(context).getConnection(
				clientHandle);
		c.addAction("Disconnect Failed - an error occured");

        //SwitchkitApplication.setStatus(Constants.CLIENT_CONNECTION_STATUS.CLIENT_NOT_CONNECTED);
	}

	private void connect(Throwable exception) {
		System.out.println("ActionListener.connect");

		Connection c = Connections.getInstance(context).getConnection(
				clientHandle);
		c.addAction("Client failed to connect");
		Log.i(CLASS_TAG, exception.getMessage());

		System.out.println("Setting client status to not connected from onFailure...");
		//SwitchkitApplication.setStatus(Constants.CLIENT_CONNECTION_STATUS.CLIENT_NOT_CONNECTED);
	}
}