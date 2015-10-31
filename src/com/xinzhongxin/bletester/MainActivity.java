package com.xinzhongxin.bletester;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.xinzhongxin.adapter.BleDeviceListAdapter;
import com.xinzhongxin.customview.MyListView;
import com.xinzhongxinbletester.R;

public class MainActivity extends Activity {
	MyListView listView;
	SwipeRefreshLayout swagLayout;
	BluetoothAdapter mBluetoothAdapter;
	private LeScanCallback mLeScanCallback;
	BleDeviceListAdapter mBleDeviceListAdapter;
	private boolean scanning = true;

	Handler handler;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setTitle(R.string.app_title);
		init();
		getBleAdapter();
		getScanResualt();
		mBluetoothAdapter.startLeScan(mLeScanCallback);
	}

	private void init() {
		// TODO Auto-generated method stub
		listView = (MyListView) findViewById(R.id.lv_deviceList);
		listView.setEmptyView(findViewById(R.id.pb_empty));
		swagLayout = (SwipeRefreshLayout) findViewById(R.id.swagLayout);
		swagLayout.setVisibility(View.VISIBLE);
		swagLayout.setOnRefreshListener(new OnRefreshListener() {

			@SuppressLint("NewApi")
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				mBleDeviceListAdapter.clear();
				mBluetoothAdapter.startLeScan(mLeScanCallback);
				scanning = true;
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
								bytesToHex(scanRecord));
						mBleDeviceListAdapter.notifyDataSetChanged();
						invalidateOptionsMenu();
					}
				});
			}
		};

	}

	static final char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
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
				onDestroy();
			}
		});
	}

	@SuppressLint("NewApi")
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
		mBleDeviceListAdapter.clear();
		scanning = false;
		mBluetoothAdapter.cancelDiscovery();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mBleDeviceListAdapter.clear();
		mBluetoothAdapter.startLeScan(mLeScanCallback);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.getItem(0).setTitle("¹²" + mBleDeviceListAdapter.getCount() + "¸ö");
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_stop:
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			scanning = false;
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
