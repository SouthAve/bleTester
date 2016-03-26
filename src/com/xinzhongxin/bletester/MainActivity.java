package com.xinzhongxin.bletester;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.xinzhongxin.adapter.BleDeviceListAdapter;
import com.xinzhongxin.utils.Utils;
import com.xinzhongxinbletester.R;

public class MainActivity extends Activity {
	ListView listView;
	SwipeRefreshLayout swagLayout;
	BluetoothAdapter mBluetoothAdapter;
	private LeScanCallback mLeScanCallback;
	BleDeviceListAdapter mBleDeviceListAdapter;
	boolean isExit;
	Handler handler;

	SharedPreferences sharedPreferences;
	Editor editor;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setTitle(R.string.app_title);
		sharedPreferences = getPreferences(0);
		editor = sharedPreferences.edit();
		init();
		getBleAdapter();
		getScanResualt();
		new Thread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mBluetoothAdapter.startLeScan(mLeScanCallback);
			}
		}).start();
	}

	private void init() {
		// TODO Auto-generated method stub
		listView = (ListView) findViewById(R.id.lv_deviceList);
		listView.setEmptyView(findViewById(R.id.pb_empty));
		swagLayout = (SwipeRefreshLayout) findViewById(R.id.swagLayout);
		swagLayout.setVisibility(View.VISIBLE);
		swagLayout.setOnRefreshListener(new OnRefreshListener() {

			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				mBleDeviceListAdapter.clear();
				mBluetoothAdapter.startLeScan(mLeScanCallback);
				swagLayout.setRefreshing(false);
			}
		});
		mBleDeviceListAdapter = new BleDeviceListAdapter(this);
		listView.setAdapter(mBleDeviceListAdapter);
		setListItemListener();
	}

	@SuppressLint("NewApi")
	private void getBleAdapter() {
		final BluetoothManager bluetoothManager = (BluetoothManager) this
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}

	@SuppressLint("NewApi")
	private void getScanResualt() {
		mLeScanCallback = new LeScanCallback() {
			@Override
			public void onLeScan(final BluetoothDevice device, final int rssi,
					final byte[] scanRecord) {
				MainActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						mBleDeviceListAdapter.addDevice(device, rssi,
								Utils.bytesToHex(scanRecord));
						mBleDeviceListAdapter.notifyDataSetChanged();
						invalidateOptionsMenu();
					}
				});
			}
		};
	}

	private void setListItemListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				BluetoothDevice device = mBleDeviceListAdapter
						.getDevice(position);
				final Intent intent = new Intent(MainActivity.this,
						DeviceConnect.class);
				intent.putExtra(DeviceConnect.EXTRAS_DEVICE_NAME,
						device.getName());
				intent.putExtra(DeviceConnect.EXTRAS_DEVICE_ADDRESS,
						device.getAddress());
				startActivity(intent);
			}
		});
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
		mBleDeviceListAdapter.clear();
		mBluetoothAdapter.cancelDiscovery();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.getItem(0).setTitle("共" + mBleDeviceListAdapter.getCount() + "个");
		return true;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_stop:
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			break;
		case R.id.menu_autoconnect:
			if (sharedPreferences.getBoolean("AutoConnect", true)) {
				editor.putBoolean("AutoConnect", false);
				editor.commit();
				Toast.makeText(this, "取消自动连接", Toast.LENGTH_SHORT).show();
			} else {
				editor.putBoolean("AutoConnect", true);
				editor.commit();
				Toast.makeText(this, "已设置为断开后自动连接", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.menu_about:
			MainActivity.this.startActivity(new Intent(this,
					AboutActivity.class));
		case R.id.menu_qrcode:
			MainActivity.this.startActivity(new Intent(this,
					QrcodeActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click();
		}
		return false;
	}

	private void exitBy2Click() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
			new Timer().schedule(new TimerTask() {
				public void run() {
					isExit = false;
				}
			}, 2000);
		} else {
			onDestroy();
			finish();
			System.exit(0);
		}
	}

}
