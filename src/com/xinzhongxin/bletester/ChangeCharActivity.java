package com.xinzhongxin.bletester;

import java.io.ByteArrayOutputStream;
import java.text.Format;
import java.util.UUID;

import android.R.integer;
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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xinzhongxin.service.BleService;
import com.xinzhongxinbletester.R;

public class ChangeCharActivity extends Activity implements OnClickListener {
	private final static String TAG = ChangeCharActivity.class.getSimpleName();
	TextView descriptor1;
	TextView descriptor2;
	TextView charHex;
	TextView charString;
	TextView charformat;
	TextView time;
	TextView resultcount;
	Button writeButton;
	Button readButton;
	Button clear_result;
	Button notifyButton;
	ScrollView scroll;
	EditText notify_resualt;
	RadioGroup radioGroup;
	RadioButton format_hex;
	RadioButton format_string;
	UUID charUuid;
	UUID serUuid;
	BleService bleService;
	BluetoothGattCharacteristic gattChar;
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	String text_hex = null;
	String text_string = null;
	String result = null;
	String resultLength = null;
	int resultLengthNum;
	int prop;
	boolean startNotify;
	boolean isNotifyHex;
	boolean isHex;
	private ServiceConnection conn = new ServiceConnection() {
		@SuppressLint("NewApi")
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder service) {
			// TODO Auto-generated method stub
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
			String action = intent.getAction();
			final String des1String = intent.getExtras()
					.getString("desriptor1");
			final String des2String = intent.getExtras()
					.getString("desriptor2");
			final String stringValue = intent.getExtras().getString(
					"StringValue");
			final String hexValue = intent.getExtras().getString("HexValue");
			final String readTime = intent.getExtras().getString("time");

			if (BleService.ACTION_DATA_AVAILABLE.equals(action)) {
				if (isNotifyHex) {
					result = intent.getExtras()
							.getString(BleService.EXTRA_DATA);
				} else {
					result = intent.getExtras().getString(
							BleService.EXTRA_STRING_DATA);
				}
				int countNumber = intent.getExtras().getInt(
						BleService.EXTRA_DATA_LENGTH);
				if (resultLengthNum != 0) {
					resultLengthNum += countNumber;
				} else {
					resultLengthNum = countNumber;
				}
				if (text_string != null) {
					text_string = text_string + result;
				} else {
					text_string = result;
				}
				resultLength = resultLengthNum + "";
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						notify_resualt.setText(text_string);
						resultcount.setText("字节数： " + resultLength);
					}
				});

			}

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
		mFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
		return mFilter;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changechar);
		sharedPreferences = this
				.getSharedPreferences("writedata", MODE_PRIVATE);
		editor = sharedPreferences.edit();
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
		clear_result = (Button) findViewById(R.id.clear_result);
		notify_resualt = (EditText) findViewById(R.id.et_notify_resualt);
		resultcount = (TextView) findViewById(R.id.result_count);
		scroll = (ScrollView) findViewById(R.id.scrollview);
		radioGroup = (RadioGroup) findViewById(R.id.rg_hex_string);

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if (arg1 == R.id.rb_string) {
					if (text_string != null) {
						if (isNotifyHex) {
							text_string = hexStr2Str(text_string);
						}
						notify_resualt.setText(text_string);
					}
					isNotifyHex = false;
				} else {
					if (text_string != null) {
						if (!isNotifyHex) {
							text_string = str2HexStr(text_string);
						}
						notify_resualt.setText(text_string);
					}
					isNotifyHex = true;
				}
			}
		});
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
			radioGroup.setVisibility(View.VISIBLE);
			scroll.setVisibility(View.VISIBLE);
			resultcount.setVisibility(View.VISIBLE);
			clear_result.setVisibility(View.VISIBLE);
		}
		clear_result.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				text_string = null;
				result = null;
				text_hex = null;
				notify_resualt.setText("");
				resultLengthNum = 0;
				resultcount.setText("字节数:0");
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(conn);
		unregisterReceiver(mBroadcastReceiver);
	}

	/**
	 * 
	 */
	public void writeDialog() {
		AlertDialog.Builder dialog = new Builder(this);
		View dialogview = LayoutInflater.from(this).inflate(
				R.layout.writedialog, null);
		final RadioGroup writeGroup = (RadioGroup) dialogview
				.findViewById(R.id.rg_write_format);
		final EditText editText = (EditText) dialogview
				.findViewById(R.id.char_value);
		final Button btn_0 = (Button) dialogview.findViewById(R.id.btn_00);
		final Button btn_1 = (Button) dialogview.findViewById(R.id.btn_01);
		final Button btn_2 = (Button) dialogview.findViewById(R.id.btn_02);
		final RadioButton radio1 = (RadioButton) dialogview
				.findViewById(R.id.rb_write_string);
		final RadioButton radio2 = (RadioButton) dialogview
				.findViewById(R.id.rb_write_hex);
		final EditText editbtn1 = (EditText) dialogview
				.findViewById(R.id.ed_edit_btn1);
		final EditText editbtn2 = (EditText) dialogview
				.findViewById(R.id.ed_edit_btn2);
		final EditText editbtn3 = (EditText) dialogview
				.findViewById(R.id.ed_edit_btn3);
		btn_0.setText(sharedPreferences.getString("btn00" + charUuid, "00"));
		btn_1.setText(sharedPreferences.getString("btn01" + charUuid, "01"));
		btn_2.setText(sharedPreferences.getString("btn02" + charUuid, "02"));
		if (sharedPreferences.getBoolean("isHex", false)) {
			isHex = true;
			radio2.setChecked(true);
			radio1.setChecked(false);
		} else {
			isHex = false;
			radio1.setChecked(true);
			radio2.setChecked(false);
		}

		writeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if (arg1 == R.id.rb_write_string) {
					isHex = false;
					editor.putBoolean("isHex", false);
				}
				if (arg1 == R.id.rb_write_hex) {
					isHex = true;
					editor.putBoolean("isHex", true);
				}
				editor.commit();
			}
		});
		editbtn1.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				String btnValue = editbtn1.getText().toString();
				if (!btnValue.isEmpty()) {
					btn_0.setText(editbtn1.getText());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				editor.putString("btn00" + charUuid, editbtn1.getText()
						.toString());
				editor.commit();
			}
		});
		editbtn2.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				String btnValue = editbtn2.getText().toString();
				if (!btnValue.isEmpty()) {
					btn_1.setText(editbtn2.getText());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				editor.putString("btn01" + charUuid, editbtn2.getText()
						.toString());
				editor.commit();
			}
		});
		editbtn3.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				String btnValue = editbtn3.getText().toString();
				if (!btnValue.isEmpty()) {
					btn_2.setText(editbtn3.getText());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				editor.putString("btn02" + charUuid, editbtn3.getText()
						.toString());
				editor.commit();
			}
		});
		btn_0.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					if (isHex) {
						String transStr = btn_0.getText().toString();
						gattChar.setValue(str2Byte(transStr));
					} else {
						gattChar.setValue(btn_0.getText().toString());
					}
					bleService.mBluetoothGatt.writeCharacteristic(gattChar);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
		btn_1.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					if (isHex) {
						String transStr = btn_1.getText().toString();
						gattChar.setValue(str2Byte(transStr));
					} else {
						gattChar.setValue(btn_1.getText().toString());
					}
					bleService.mBluetoothGatt.writeCharacteristic(gattChar);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
		btn_2.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					if (isHex) {
						String transStr = btn_2.getText().toString();
						gattChar.setValue(str2Byte(transStr));
					} else {
						gattChar.setValue(btn_2.getText().toString());
					}
					bleService.mBluetoothGatt.writeCharacteristic(gattChar);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});

		dialog.setView(dialogview);
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				String charvalue = editText.getText().toString();
				if (!charvalue.isEmpty()) {
					if (isHex) {
						byte[] str = str2Byte(charvalue);
						gattChar.setValue(str);
					} else {
						gattChar.setValue(charvalue);
					}
					bleService.mBluetoothGatt.writeCharacteristic(gattChar);
				}
			}
		});
		dialog.show();
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_read:
			bleService.mBluetoothGatt.readCharacteristic(gattChar);
			break;
		case R.id.btn_write:
			writeDialog();
			break;
		case R.id.btn_notify:
			if (!startNotify) {
				bleService.mBluetoothGatt.setCharacteristicNotification(
						gattChar, true);
				startNotify = true;
				notifyButton.setText("停止通知");
			} else {
				bleService.mBluetoothGatt.setCharacteristicNotification(
						gattChar, false);
				startNotify = false;
				notifyButton.setText("开始通知");
			}
			break;
		}
	}

	public static String str2HexStr(String str) {

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString().trim();
	}

	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
			System.out.println(bytes[i]);
		}
		return new String(bytes);
	}

	public static byte[] str2Byte(String hexStr) {
		String[] a = new String[hexStr.length() / 2];

		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			a[i] = hexStr.substring(2 * i, 2 * i + 2);
			System.out.println(a[i].toString());
		}
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(a[i], 16);
			System.out.println(bytes[i]);
		}
		return bytes;
	}
}
