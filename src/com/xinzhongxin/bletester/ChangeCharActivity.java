package com.xinzhongxin.bletester;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import com.xinzhongxin.application.MyApplication;
import com.xinzhongxin.service.BleService;
import com.xinzhongxinbletester.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeCharActivity extends Activity implements OnClickListener {
	private final static String TAG = ChangeCharActivity.class.getSimpleName();
	TextView descriptor1;
	TextView descriptor2;
	TextView charHex;
	TextView charString;
	TextView charformat;
	TextView time;
	Button writeButton;
	Button readButton;
	Button notifyButton;

	UUID charUuid;
	UUID serUuid;
	BleService bleService;
	BluetoothGattCharacteristic gattChar;
	int prop;
	boolean startNotify;
	private ServiceConnection conn = new ServiceConnection() {
		@SuppressLint("NewApi")
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder service) {
			// TODO Auto-generated method stub
			Log.v(TAG, "onServiceConnected");
			bleService = ((BleService.LocalBinder) service).getService();
			gattChar = bleService.mBluetoothGatt.getService(serUuid)
					.getCharacteristic(charUuid);
			bleService.mBluetoothGatt.readCharacteristic(gattChar);
			if (gattChar.getDescriptors().size() != 0) {
				BluetoothGattDescriptor des = gattChar.getDescriptors().get(0);
				des.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
				bleService.mBluetoothGatt.writeDescriptor(des);
			}
			int prop = gattChar.getProperties();
			if ((prop & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
				bleService.mBluetoothGatt.setCharacteristicNotification(
						gattChar, false);
			}
			if ((prop & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
				bleService.mBluetoothGatt.setCharacteristicNotification(
						gattChar, false);
			}
			if ((prop & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
				bleService.mBluetoothGatt.setCharacteristicNotification(
						gattChar, false);

			}
			if ((prop & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
				bleService.mBluetoothGatt.setCharacteristicNotification(
						gattChar, true);

			}

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			bleService = null;
		}

	};

	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub

			final String des1String = intent.getExtras()
					.getString("desriptor1");
			final String des2String = intent.getExtras()
					.getString("desriptor2");
			final String stringValue = intent.getExtras().getString(
					"StringValue");
			final String hexValue = intent.getExtras().getString("HexValue");
			final String readTime = intent.getExtras().getString("time");

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					charString.setText("字符串: " + stringValue);
					charHex.setText("十六进制: " + hexValue);
					time.setText("读取时间: " + readTime);
					descriptor1.setText(des1String);
					descriptor2.setText(des2String);
				}
			});
		}
	};

	private IntentFilter makeIntentFilter() {
		// TODO Auto-generated method stub
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(BleService.ACTION_CHAR_READED);
		return mFilter;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changechar);
		init();
		bindService(new Intent(this, BleService.class), conn, BIND_AUTO_CREATE);
		registerReceiver(mBroadcastReceiver, makeIntentFilter());
	}

	private void init() {
		// TODO Auto-generated method stub
		descriptor1 = (TextView) findViewById(R.id.tv_descriptor1);
		descriptor2 = (TextView) findViewById(R.id.tv_descriptor2);
		charHex = (TextView) findViewById(R.id.tv_charHex);
		charString = (TextView) findViewById(R.id.tv_charString);
		charformat = (TextView) findViewById(R.id.tv_charformat);
		time = (TextView) findViewById(R.id.tv_time);
		writeButton = (Button) findViewById(R.id.btn_write);
		readButton = (Button) findViewById(R.id.btn_read);
		notifyButton = (Button) findViewById(R.id.btn_notify);

		writeButton.setOnClickListener(this);
		readButton.setOnClickListener(this);
		notifyButton.setOnClickListener(this);

		charUuid = UUID.fromString(getIntent().getExtras().get("charUUID")
				.toString());
		serUuid = UUID.fromString(getIntent().getExtras().get("serUUID")
				.toString());
		prop = getIntent().getExtras().getInt("properties");

		if ((prop & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
			writeButton.setVisibility(View.VISIBLE);
		}
		if ((prop & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
			writeButton.setVisibility(View.VISIBLE);
		}
		if ((prop & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
			readButton.setVisibility(View.VISIBLE);
		}
		if ((prop & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
			notifyButton.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(conn);
		unregisterReceiver(mBroadcastReceiver);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_read:
			bleService.mBluetoothGatt.readCharacteristic(gattChar);
			Log.v(TAG, "btn_read");
			break;

		case R.id.btn_write:
			AlertDialog.Builder dialog = new Builder(this);
			View dialogview = LayoutInflater.from(this).inflate(
					R.layout.writedialog, null);
			final EditText editText = (EditText) dialogview
					.findViewById(R.id.char_value);
			dialog.setView(dialogview);
			dialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							gattChar.setValue(new byte[] { Byte
									.parseByte(editText.getText().toString()) });
							bleService.mBluetoothGatt
									.writeCharacteristic(gattChar);
						}
					});
			dialog.show();
			break;
		case R.id.btn_notify:
			if (!startNotify) {
				// 开始状态就做开始的逻辑
				bleService.mBluetoothGatt.setCharacteristicNotification(
						gattChar, true);
				// 将状态设为开始
				startNotify = true;
				// 改变按钮显示
				notifyButton.setText("停止通知");
			} else {
				bleService.mBluetoothGatt.setCharacteristicNotification(
						gattChar, false);
				startNotify = false;
				// 改变按钮显示
				notifyButton.setText("开始通知");
			}
			break;
		}
	}
}
