package com.kickstartlab.jm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OrderListAdapter extends ArrayAdapter<Order> {
	private final Activity context;
	private final List<Order> orders;
	public OrderListAdapter(Activity context, List<Order> objects) {
		super(context, R.layout.listitemlayout, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.orders = objects;
	}
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getItem(int)
	 */
	@Override
	public Order getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}
	
	private class ViewHolder {
		public TextView seq;
		public TextView merchantname;
		public TextView buyername;
		public TextView assigneddate;
    }
	
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//return super.getView(position, convertView, parent);
	    View rowView = convertView;
	    
	    if (rowView == null) {
	      LayoutInflater inflater = context.getLayoutInflater();
	      rowView = inflater.inflate(R.layout.listitemlayout, null);
	      ViewHolder holder = new ViewHolder();

	      holder.seq = (TextView) convertView.findViewById(R.id.sequence);
          holder.merchantname = (TextView) convertView.findViewById(R.id.merchant_name);
          holder.buyername = (TextView) convertView.findViewById(R.id.buyer_name);
          holder.assigneddate = (TextView) convertView.findViewById(R.id.delivery_date);

	      rowView.setTag(holder);
	    }

	    ViewHolder holder = (ViewHolder) rowView.getTag();
	    
	    holder.seq.setText( String.valueOf( orders.get(position).getSeq() ));
	    holder.merchantname.setText(orders.get(position).getMcName());
	    holder.buyername.setText(orders.get(position).getByName());
	    holder.assigneddate.setText(orders.get(position).getAs_date());

	    return rowView;
	}
	
}
