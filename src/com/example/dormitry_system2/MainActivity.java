package com.example.dormitry_system2;

import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.util.Log;

public class MainActivity extends Activity {
	
	private EditText id;
	private SocketIO socket;
	
	private ArrayAdapter<String> adapter;
	private Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		//Viewの設定
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		ListView listView = (ListView)findViewById(R.id.listView1);
		listView.setAdapter(adapter);
		
		
		id = (EditText)findViewById(R.id.editText1);
		
		try{
			connect();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void connect() throws MalformedURLException{
		socket = new SocketIO("http://192.168.24.61:3000/");
		socket.connect(iocallback);
    }
	
	private IOCallback iocallback = new IOCallback() {

		@Override
		public void onConnect() {
		    Log.i("just log", "onConnect");
		}

		@Override
		public void onDisconnect() {
			Log.i("just log", "onDisconnect");
		}

		@Override
		public void onMessage(JSONObject json, IOAcknowledge ack) {
			Log.i("just log", "onMessage");
		}

		@Override
		public void onMessage(String data, IOAcknowledge ack) {
			Log.i("just log", "onMessage2");
		}
		
		@Override
		public void on(String event, IOAcknowledge ack, Object... args) {
			Log.i("just log", "on----");
			Log.i("event name", event);
			
			if (event.equals("push msg")) {
				Log.i("push msg event", args[0].toString());
				
				final String message = args[0].toString();
				new Thread(new Runnable() {
			        public void run() {
			            handler.post(new Runnable() {
			                public void run() {
			                	if(message != null) {
			                		adapter.insert(message, 0);
			                	}
			                }
			            });
			        }
				}).start();
			}
			
			if (event.equals("push data")) {
				Log.i("on123", event);
				Log.i("on123", args[0].toString());

				JSONArray mesArray = (JSONArray)args[0];

				for(int i = 0; i < mesArray.length(); i++){
				    try {
				        Log.i("on123", event);
				        JSONObject show = (JSONObject)mesArray.get(i);
				        Log.i("on123", show.get("student_number").toString());
				    }
				    catch (JSONException e) {
				        Log.i("on123", event);
				        // TODO Auto-generated catch block
				        e.printStackTrace();
				    }
				}
			}
		    
		}

		@Override
		public void onError(SocketIOException socketIOException) {
		    
		    socketIOException.printStackTrace();
		}
	};
	
	public void sendEvent(View view){
		// 文字が入力されていなければ何もしない
		if (id.getText().toString().length() == 0) {
		    return;
		}
		//remove id実行
		socket.emit("remove id", id.getText().toString());
    	// テキストリセット
    	id.setText("");
    }
}
