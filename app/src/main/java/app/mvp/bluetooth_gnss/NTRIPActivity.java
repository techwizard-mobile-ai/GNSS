package app.mvp.bluetooth_gnss;

import static app.vsptracker.R.layout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.material.navigation.NavigationView;

import app.vsptracker.BaseActivity;
import app.vsptracker.R;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.clearevo.libbluetooth_gnss_service.bluetooth_gnss_service;
import com.clearevo.libbluetooth_gnss_service.ntrip_conn_mgr;
import com.clearevo.libbluetooth_gnss_service.rfcomm_conn_mgr;
import com.clearevo.libbluetooth_gnss_service.gnss_sentence_parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


import androidx.annotation.NonNull;

import android.content.SharedPreferences;

public class NTRIPActivity extends BaseActivity implements View.OnClickListener {

  Button connect;

  private static final int CHOOSE_FOLDER = 1;
  static final String TAG = "ntrip_activity";
  bluetooth_gnss_service m_service;
  boolean mBound = false;
  Handler m_handler;
  final int MESSAGE_PARAMS_MAP = 0;
  final int MESSAGE_SETTINGS_MAP = 1;
TextView tv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.base_content_frame);
    getLayoutInflater().inflate(layout.activity_ntripactivity, contentFrameLayout);
    NavigationView navigationView = (NavigationView) findViewById(R.id.base_nav_view);
    navigationView.getMenu().getItem(6).setChecked(true);

    connect = findViewById(R.id.connect);
    tv = findViewById(R.id.tv);
    connect.setOnClickListener(this);

  }

  final String log_uri_pref_key = "flutter.pref_log_uri";
  final String log_enabled_pref_key = "flutter.log_bt_rx";

  @Override
  public void onActivityResult(int requestCode, int resultCode,
                               Intent resultData) {
    super.onActivityResult(requestCode, resultCode, resultData);
    if (requestCode == CHOOSE_FOLDER) {
      Context context = getApplicationContext();
      final SharedPreferences prefs = context.getSharedPreferences("FlutterSharedPreferences", MODE_PRIVATE);
      if (resultCode == this.RESULT_OK) {
        Uri uri = null;
        if (resultData != null) {
          uri = resultData.getData();
          if (uri != null && context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
              getApplicationContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(log_uri_pref_key, uri.toString());
            editor.putBoolean(log_enabled_pref_key, true);
            editor.apply();
            Log.d(TAG, "choose_folder ok so set log uri: " + log_uri_pref_key + " val: " + prefs.getString(log_uri_pref_key, ""));
          }
        }
      } else {
        Log.d(TAG, "choose_folder not ok so disable log uri: " + log_uri_pref_key);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(log_uri_pref_key, "");
        editor.putBoolean(log_enabled_pref_key, false);
        editor.apply();
      }
    }
  }


  public void create() {
    Log.d(TAG, "create()");
    m_handler = new Handler(getMainLooper()) {
      @Override
      public void handleMessage(Message inputMessage) {
        if (inputMessage.what == MESSAGE_PARAMS_MAP) {
          Log.d(TAG, "mainactivity handler got params map");
          try {
//            if (mBound == false || m_events_sink == null) {
//              Log.d(TAG, "mBound == false || m_events_sink == null so not delivering params_map");
//            } else {
            Object params_map = inputMessage.obj;
            if (params_map instanceof HashMap) {
                                /*
                        PREVENT BELOW: try clone the hashmap...
                        D/btgnss_mainactvty(15208): handlemessage exception: java.util.ConcurrentModificationException
D/btgnss_mainactvty(15208): 	at java.util.HashMap$HashIterator.nextNode(HashMap.java:1441)
D/btgnss_mainactvty(15208): 	at java.util.HashMap$EntryIterator.next(HashMap.java:1475)
D/btgnss_mainactvty(15208): 	at java.util.HashMap$EntryIterator.next(HashMap.java:1473)
D/btgnss_mainactvty(15208): 	at io.flutter.plugin.common.StandardMessageCodec.writeValue(StandardMessageCodec.java:289)
D/btgnss_mainactvty(15208): 	at io.flutter.plugin.common.StandardMethodCodec.encodeSuccessEnvelope(StandardMethodCodec.java:57)
D/btgnss_mainactvty(15208): 	at io.flutter.plugin.common.EventChannel$IncomingStreamRequestHandler$EventSinkImplementation.success(EventChannel.java:226)
D/btgnss_mainactvty(15208): 	at com.clearevo.bluetooth_gnss.MainActivity$1.handleMessage(MainActivity.java:64)
                        * */
              //params_map = ((HashMap) params_map).clone();
              //Log.d(TAG, "cloned HashMap to prevent ConcurrentModificationException...");
            }
            Log.d(TAG, "sending params map to m_events_sink start");
//              m_events_sink.success(params_map);
            Log.d(TAG, "sending params map to m_events_sink done");
//            }
          } catch (Exception e) {
            Log.d(TAG, "handlemessage MESSAGE_PARAMS_MAP exception: " + Log.getStackTraceString(e));
          }
        } else if (inputMessage.what == MESSAGE_SETTINGS_MAP) {
          Log.d(TAG, "mainactivity handler got settings map");
          try {
//            if (mBound == false || m_settings_events_sink == null) {
            Log.d(TAG, "mBound == false || m_settings_events_sink == null so not delivering params_map");
//            } else {
            Object params_map = inputMessage.obj;
            //this is already a concurrenthashmap - no need to clone
            Log.d(TAG, "sending params map to m_settings_events_sink start");
//              m_settings_events_sink.success(params_map);
            Log.d(TAG, "sending params map to m_settings_events_sink done");
//            }
          } catch (Exception e) {
            Log.d(TAG, "handlemessage MESSAGE_SETTINGS_MAP exception: " + Log.getStackTraceString(e));
          }
        }
      }
    };
  }

//  @Override
//  public void onListen(Object args, final EventChannel.EventSink events) {
//    m_events_sink = events;
//    Log.d(TAG, "ENGINE_EVENTS_CHANNEL added listener: " + events);
//  }
//
//  @Override
//  public void onCancel(Object args) {
//    m_events_sink = null;
//    Log.d(TAG, "ENGINE_EVENTS_CHANNEL cancelled listener");
//  }

  public ArrayList<String> get_mountpoint_list(String host, int port, String user, String pass) {
    ArrayList<String> ret = null;
    ntrip_conn_mgr mgr = null;
    try {
      mgr = new ntrip_conn_mgr(host, port, "", user, pass, null);
      ret = mgr.get_mount_point_list();
    } catch (Exception e) {
      Log.d(TAG, "get_mountpoint_list call exception: " + Log.getStackTraceString(e));
    } finally {
      if (mgr != null) {
        try {
          mgr.close();
        } catch (Exception e) {
        }
      }
    }
    return ret;
  }


  @Override
  public void onClick(View view) {
    int id = view.getId();

    if (id == R.id.connect) {
      myHelper.log("connect");
//      myHelper.log(rfcomm_conn_mgr.get_bd_map().toString());
      Intent intent = new Intent(this, bluetooth_gnss_service.class);
      bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
  }

  public boolean open_phone_settings() {
    try {
      startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
      return true;
    } catch (Exception e) {
      Log.d(TAG, "launch phone settings activity exception: " + Log.getStackTraceString(e));
    }
    return false;
  }

  public boolean open_phone_bluetooth_settings() {
    try {
      startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
      return true;
    } catch (Exception e) {
      Log.d(TAG, "launch phone settings activity exception: " + Log.getStackTraceString(e));
    }
    return false;
  }

  public boolean open_phone_location_settings() {
    try {
      startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
      return true;
    } catch (Exception e) {
      Log.d(TAG, "launch phone settings activity exception: " + Log.getStackTraceString(e));
    }
    return false;
  }

  public boolean open_phone_developer_settings() {
    try {
      startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
      return true;
    } catch (Exception e) {
      Log.d(TAG, "launch phone settings activity exception: " + Log.getStackTraceString(e));
    }
    return false;
  }

  public void on_updated_nmea_params(HashMap<String, Object> params_map) {
    Log.d(TAG, "mainactivity on_updated_nmea_params()");
    try {
      Message msg = m_handler.obtainMessage(MESSAGE_PARAMS_MAP, params_map);
      msg.sendToTarget();
    } catch (Exception e) {
      Log.d(TAG, "on_updated_nmea_params sink update exception: " + Log.getStackTraceString(e));
    }
  }

  public void toast(String msg) {
    m_handler.post(
            new Runnable() {
              @Override
              public void run() {
                try {
                  Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                  Log.d(TAG, "toast exception: " + Log.getStackTraceString(e));
                }
              }
            }
    );
  }

  public void stop_service_if_not_connected() {
    if (mBound && m_service != null && m_service.is_bt_connected()) {
      toast("Bluetooth GNSS running in backgroud...");
    } else {
      Intent intent = new Intent(getApplicationContext(), bluetooth_gnss_service.class);
      stopService(intent);
    }
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "onDestroy()");
    super.onDestroy();
  }

  @Override
  public void onBackPressed() {
    Log.d(TAG, "onBackPressed()");
    super.onBackPressed();
  }

  @Override
  protected void onStart() {
    Log.d(TAG, "onStart()");
    super.onStart();
    // Bind to LocalService
    Intent intent = new Intent(this, bluetooth_gnss_service.class);
//    bindService(intent, connection, Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onStop() {
    Log.d(TAG, "onStop()");
    super.onStop();
//    unbindService(connection);
    mBound = false;

  }

  @Override
  protected void onPause() {
    Log.d(TAG, "onPause()");
    super.onPause();
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume()");
    super.onResume();
    tv.setText(
            "Is Bluetooth ON:" +rfcomm_conn_mgr.is_bluetooth_on()
            +"\nBluetooth: " + rfcomm_conn_mgr.get_bd_map()
//            +"\nNTRIP connected: " + m_service.is_ntrip_connected()
    );
  }

  /**
   * Defines callbacks for service binding, passed to bindService()
   */
  private ServiceConnection connection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName className,
                                   IBinder service) {

      myHelper.log( "onServiceConnected()");

      // We've bound to LocalService, cast the IBinder and get LocalService instance
      bluetooth_gnss_service.LocalBinder binder = (bluetooth_gnss_service.LocalBinder) service;
      m_service = binder.getService();
      mBound = true;
      m_service.set_callback((gnss_sentence_parser.gnss_parser_callbacks) NTRIPActivity.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      myHelper.log( "onServiceDisconnected()");
      mBound = false;
      m_service.set_callback((gnss_sentence_parser.gnss_parser_callbacks) null);
    }
  };
}