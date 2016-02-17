package com.xinzhongxin.bletester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.xinzhongxin.adapter.BleSevicesListAdapter;
import com.xinzhongxin.service.BleService;
import com.xinzhongxin.utils.Utils;
import com.xinzhongxinbletester.R;

public class DeviceConnect extends Activity {

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	public static final String FIND_DEVICE_ALARM_ON = "find.device.alarm.on";
	public static final String DISCONNECT_DEVICE = "find.device.disconnect";
	public static final String CANCEL_DEVICE_ALARM = "find.device.cancel.alarm";
	public static final String DEVICE_BATTERY = "device.battery.level";

	ListView serviceList;
	SwipeRefreshLayout swagLayout;
	BleSevicesListAdapter servicesListAdapter;

	Intent intent;
	SharedPreferences sharedPreferences;
	Editor editor;
	public static String bleAddress;
	boolean isConnecting = false;
	boolean isAlarm = false;
	List<BluetoothGattService> gattServices = new ArrayList<BluetoothGattService>();
	BleService bleService;
	int rssi;
	private final ServiceConnection conn = new ServiceConnection() {
		@SuppressLint("NewApi")
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			bleService = ((BleService.LocalBinder) service).getService();
			if (!bleService.init()) {
				finish();
			}
			bleService.connect(bleAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			bleService = null;
		}
	};

	BroadcastReceiver mbtBroadcastReceiver = new BroadcastReceiver() {

		@SuppressLint({ "NewApi", "DefaultLocale" })
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (BleService.ACTION_GATT_CONNECTED.equals(action)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(DeviceConnect.this, "设备连接成功！",
								Toast.LENGTH_LONG).show();
					}
				});
			}
			if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Toast.makeText(DeviceConnect.this, "设备断开！", Toast.LENGTH_LONG)
						.show();
				if (sharedPreferences.getBoolean("AutoConnect", true)) {
					bleService.connect(bleAddress);
				}
			}
			if (BleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				String uuid = null;
				bleService.mBluetoothGatt.readRemoteRssi();
				gattServices = bleService.mBluetoothGatt.getServices();
				final ArrayList<HashMap<String, String>> serviceNames = new ArrayList<HashMap<String, String>>();
				for (BluetoothGattService ser : gattServices) {
					HashMap<String, String> currentServiceData = new HashMap<String, String>();
					uuid = ser.getUuid().toString();
					currentServiceData.put("Name", Utils.attributes
							.containsKey(uuid) ? Utils.attributes.get(uuid)
							: "Unknown Service");
					serviceNames.add(currentServiceData);
				}
				runOnUiThread(new Runnable() {
					@SuppressLint("NewApi")
					public void run() {
						// TODO Auto-generated method stub
						servicesListAdapter.addServiceNames(serviceNames);
						servicesListAdapter.addService(gattServices);
						servicesListAdapter.notifyDataSetChanged();

					}
				});

			}
			if (BleService.ACTION_GATT_RSSI.equals(action)) {
				rssi = intent.getExtras().getInt(BleService.EXTRA_DATA_RSSI);
				DeviceConnect.this.invalidateOptionsMenu();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setTitle("服务列表");
		sharedPreferences = getPreferences(0);
		editor = sharedPreferences.edit();
		init();
		bindBleSevice();
		registerReceiver(mbtBroadcastReceiver, makeGattUpdateIntentFilter());
	}

	private void bindBleSevice() {
		Intent serviceIntent = new Intent(this, BleService.class);
		bindService(serviceIntent, conn, BIND_AUTO_CREATE);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void init() {
		serviceList = (ListView) findViewById(R.id.lv_deviceList);
		serviceList.setEmptyView(findViewById(R.id.pb_empty));
		servicesListAdapter = new BleSevicesListAdapter(this);
		swagLayout = (SwipeRefreshLayout) findViewById(R.id.swagLayout);
		swagLayout.setOnRefreshListener(new OnRefreshListener() {

			@SuppressLint("NewApi")
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				servicesListAdapter.clear();
				bleService.mBluetoothGatt.discoverServices();
				servicesListAdapter.notifyDataSetChanged();
				swagLayout.setRefreshing(false);
			}
		});
		serviceList.setAdapter(servicesListAdapter);
		bleAddress = getIntent().getExtras().getString((EXTRAS_DEVICE_ADDRESS));
		serviceList.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				Intent servicesIntent = new Intent(DeviceConnect.this,
						CharacterisiticActivity.class);
				servicesIntent.putExtra("serviceUUID",
						bleService.mBluetoothGatt.getServices().get(position)
								.getUuid());
				startActivity(servicesIntent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		bleService.close(bleService.mBluetoothGatt);
		unbindService(conn);
		unregisterReceiver(mbtBroadcastReceiver);
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BleService.BATTERY_LEVEL_AVAILABLE);
		intentFilter.addAction(BleService.ACTION_GATT_RSSI);
		return intentFilter;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.services, menu);
		menu.getItem(1).setTitle(rssi + "");
		menu.getItem(0).setVisible(false);
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.menu_rssi)
			item.setTitle(rssi + "");
		return super.onOptionsItemSelected(item);
	}

}
