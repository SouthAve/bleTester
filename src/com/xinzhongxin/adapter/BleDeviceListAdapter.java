package com.xinzhongxin.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.xinzhongxinbletester.R;

import android.R.integer;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BleDeviceListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;// 得到一个LayoutInfalter对象用来导入布局
	private ArrayList<BluetoothDevice> mLeDevices;
	private ArrayList<Integer> RSSIs;
	private ArrayList<String> scanRecords;

	public BleDeviceListAdapter(Context context) {
		mLeDevices = new ArrayList<BluetoothDevice>();
		RSSIs = new ArrayList<Integer>();
		scanRecords = new ArrayList<String>();
		this.mInflater = LayoutInflater.from(context); // 获得inflater实例
	}

	public void addDevice(BluetoothDevice device, int RSSI, String scanRecord) {

		if (!mLeDevices.contains(device)) {
			this.mLeDevices.add(device);
			this.RSSIs.add(new Integer(RSSI));
			this.scanRecords.add(scanRecord);
		} else {
			for (int i = 0; i < mLeDevices.size(); i++) {
				BluetoothDevice d = mLeDevices.get(i);
				if (device.getAddress().equals(d.getAddress())) {
					RSSIs.set(i, RSSI);
					scanRecords.set(i, scanRecord);
				}
			}
		}
	}

	public BluetoothDevice getDevice(int position) {
		// TODO Auto-generated method stub
		return mLeDevices.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mLeDevices.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mLeDevices.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub

		ViewHolder viewholder;
		if (view == null) {
			view = mInflater.inflate(R.layout.item_devicelist, null);
			viewholder = new ViewHolder();
			viewholder.devicename = (TextView) view
					.findViewById(R.id.tv_devicelist_name);
			viewholder.deviceAddress = (TextView) view
					.findViewById(R.id.tv_devicelist_address);
			viewholder.deviceRSSI = (TextView) view
					.findViewById(R.id.tv_devicelist_rssi);
			viewholder.devicerecord = (TextView) view
					.findViewById(R.id.tv_devicelist_scanRecord);
			view.setTag(viewholder);

		} else {
			viewholder = (ViewHolder) view.getTag();
		}
		viewholder.devicename.setText(mLeDevices.get(position).getName());
		viewholder.deviceAddress.setText("地址： "
				+ mLeDevices.get(position).getAddress());
		viewholder.deviceRSSI.setText("信号： " + RSSIs.get(position).toString());
		viewholder.devicerecord.setText("广播包： " + "\n"
				+ scanRecords.get(position));
		return view;

	}

	static class ViewHolder {
		TextView devicename;
		TextView deviceAddress;
		TextView deviceRSSI;
		TextView devicerecord;
	}

	public void clear() {
		// TODO Auto-generated method stub
		mLeDevices.clear();
		RSSIs.clear();
		scanRecords.clear();
	}

}
