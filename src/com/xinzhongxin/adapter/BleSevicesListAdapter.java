package com.xinzhongxin.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xinzhongxinbletester.R;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BleSevicesListAdapter extends BaseAdapter {
	public static final String TAG = BleSevicesListAdapter.class
			.getSimpleName();
	private LayoutInflater mInflater;// 得到一个LayoutInfalter对象用来导入布局
	private ArrayList<BluetoothGattService> services;
	private ArrayList<HashMap<String, String>> serviceName;

	public BleSevicesListAdapter(Context context) {
		services = new ArrayList<BluetoothGattService>();
		this.mInflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return services.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return services.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void addService(List<BluetoothGattService> services) {
		// TODO Auto-generated method stub
		this.services = (ArrayList<BluetoothGattService>) services;
	}

	public void addServiceNames(List<HashMap<String, String>> serviceName) {
		this.serviceName = (ArrayList<HashMap<String, String>>) serviceName;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewholder;
		if (view == null) {
			view = mInflater.inflate(R.layout.item_devicelist, null);
			viewholder = new ViewHolder();
			viewholder.servicesname = (TextView) view
					.findViewById(R.id.tv_devicelist_name);
			viewholder.servicesUUID = (TextView) view
					.findViewById(R.id.tv_devicelist_address);
			viewholder.servicesInID = (TextView) view
					.findViewById(R.id.tv_devicelist_rssi);
			viewholder.servicestype = (TextView) view
					.findViewById(R.id.tv_devicelist_scanRecord);
			view.setTag(viewholder);

		} else {
			viewholder = (ViewHolder) view.getTag();
		}
		viewholder.servicesname.setText(serviceName.get(position).get("Name")
				);
		viewholder.servicesUUID.setText(services.get(position).getUuid()
				.toString());
		viewholder.servicesInID.setText("Instance Id: "
				+ services.get(position).getInstanceId());
		if (services.get(position).getType() == 0) {
			viewholder.servicestype.setText("Type : primary");
		} else {
			viewholder.servicestype.setText("Type : secondary");
		}
		return view;
	}

	static class ViewHolder {
		TextView servicesname;
		TextView servicesUUID;
		TextView servicesInID;
		TextView servicestype;
	}

	public void clear() {
		// TODO Auto-generated method stub
		services.clear();
	}
}