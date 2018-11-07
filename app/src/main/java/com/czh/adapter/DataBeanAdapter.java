package com.czh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dream.tcp.DataBean;
import com.dream.tcp.R;

import java.util.List;

public class DataBeanAdapter extends BaseAdapter {

	private Context context;
	private List<DataBean> list;
	private LayoutInflater inflater;

	public DataBeanAdapter(Context context, List<DataBean> list) {
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		DataBean dataBean = list.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_layout, null);// 生成条目对象
			holder = new ViewHolder();
			holder.text_portID = (TextView) convertView.findViewById(R.id.text_portID);
			holder.text_name = (TextView) convertView.findViewById(R.id.text_name);
			holder.text_value = (TextView) convertView.findViewById(R.id.text_value);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
//		if (position % 2 == 0) {
//			convertView.setBackgroundColor(0xffe7e7e5);
//		}else {
//			convertView.setBackgroundColor(0xffffffff);
//		}
		String str_protID = dataBean.getPortID();
		String str_name = dataBean.getName();
		String str_value = dataBean.getValue();
		holder.text_portID.setText(str_protID);
		holder.text_name.setText(str_name);
		holder.text_value.setText(str_value);
		return convertView;
	}

	class ViewHolder{
		//		TextView text_day_num,text_league,text_date_time,text_vs;
		TextView text_portID,text_name,text_value;
	}

}
