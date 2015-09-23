package com.xinzhongxin.bletester;

import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xinzhongxin.adapter.CharacterisiticListAdapter;
import com.xinzhongxin.service.BleService;
import com.xinzhongxinbletester.R;

public class CharacterisiticActivity extends Activity {
	private final static String TAG = "CharacterisiticActivity";
	ListView lv;
	BluetoothAdapter mBluetoothAdapter;
	CharacterisiticListAdapter charListAdapter;
	UUID uuid;
	// BLE Sevice
	BleService bleSevice;
	BluetoothGattService gattService;

	private final ServiceConnection conn = new ServiceConnection() {
		@SuppressLint("NewApi")
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder service) {
			// TODO Auto-generated method stub
			bleSevice = ((BleService.LocalBinder) service).getService();
			gattService = bleSevice.mBluetoothGatt.getService(uuid);
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					charListAdapter.addChars(gattService.getCharacteristics());
					charListAdapter.notifyDataSetChanged();
				}
			});
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			bleSevice = null;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setTitle("特性列表");
		uuid = (UUID) getIntent().getExtras().get("serviceUUID");
		init();
		bindService(new Intent(this, BleService.class), conn, BIND_AUTO_CREATE);
	}

	@SuppressLint("NewApi")
	private void init() {
		lv = (ListView) findViewById(R.id.lv_deviceList);
		charListAdapter = new CharacterisiticListAdapter(this);
		lv.setAdapter(charListAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(CharacterisiticActivity.this,
						ChangeCharActivity.class);

				UUID charUuid = bleSevice.mBluetoothGatt.getService(uuid)
						.getCharacteristics().get(position).getUuid();
				mIntent.putExtra("charUUID", charUuid);
				mIntent.putExtra("properties", bleSevice.mBluetoothGatt
						.getService(uuid).getCharacteristics().get(position)
						.getProperties());
				mIntent.putExtra("serUUID", uuid);
				startActivity(mIntent);
			}
		});
		

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(conn);
	}
}
