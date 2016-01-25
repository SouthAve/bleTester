package com.xinzhongxin.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xinzhongxinbletester.R;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CharacterisiticListAdapter extends BaseAdapter {
	ArrayList<BluetoothGattCharacteristic> chars;
	ArrayList<HashMap<String, String>> charNames;
	LayoutInflater mInflater;

	public CharacterisiticListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		chars = new ArrayList<BluetoothGattCharacteristic>();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return chars.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint({ "NewApi", "InflateParams" })
	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewholder;
		if (view == null) {
			view = mInflater.inflate(R.layout.item_devicelist, null);
			viewholder = new ViewHolder();
			viewholder.charName = (TextView) view
					.findViewById(R.id.tv_devicelist_name);
			viewholder.charUUID = (TextView) view
					.findViewById(R.id.tv_devicelist_address);
			viewholder.charInID = (TextView) view
					.findViewById(R.id.tv_devicelist_rssi);
			viewholder.charProperty = (TextView) view
					.findViewById(R.id.tv_devicelist_scanRecord);
			viewholder.charPermission = (TextView) view
					.findViewById(R.id.tv_devicelist_charPermission);
			view.setTag(viewholder);

		} else {
			viewholder = (ViewHolder) view.getTag();
		}
		viewholder.charName.setText(charNames.get(position).get("Name"));
		SpannableString span = new SpannableString(chars.get(position)
				.getUuid().toString());
		span.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 8,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0,
				8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		viewholder.charUUID.setText(span);
		viewholder.charInID.setText("Instance ID: "
				+ chars.get(position).getInstanceId());
		viewholder.charProperty.setText("Property: "
				+ chars.get(position).getProperties());
		return view;
	}

	public void addChars(List<BluetoothGattCharacteristic> characteristics) {
		// TODO Auto-generated method stub
		this.chars = (ArrayList<BluetoothGattCharacteristic>) characteristics;
	}

	public void addCharNames(List<HashMap<String, String>> charNames) {
		// TODO Auto-generated method stub
		this.charNames = (ArrayList<HashMap<String, String>>) charNames;
	}

	static class ViewHolder {
		TextView charName;
		TextView charUUID;
		TextView charInID;
		TextView charProperty;
		TextView charPermission;
	}

	public void addCharNames() {
		// TODO Auto-generated method stub

	}
}
