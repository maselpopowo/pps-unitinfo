package org.ewicom.pps.unitinfo.model;

import org.ewicom.pps.unitinfo.DrawerItem;
import org.ewicom.pps.unitinfo.R;
import org.ewicom.pps.unitinfo.LeftDrawerAdapter.RowType;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class DrawerList implements DrawerItem {

	private final String name;
	private final long id;
	private final int color;

	public DrawerList(LayoutInflater layoutInflater, String name, long id, int color) {
		this.name = name;
		this.id = id;
		this.color = color;
	}

	@Override
	public int getViewType() {
		return RowType.LIST_ITEM.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		final Holder mHolder;
		View view = convertView;
		if (convertView == null) {
			mHolder = new Holder();
			view = inflater.inflate(R.layout.item_drawer_list, null);
			mHolder.name = (TextView) view.findViewById(R.id.text_drawer_list);
			mHolder.divider = view.findViewById(R.id.text_drawel_divider);
			view.setTag(mHolder);

		} else {
			mHolder = (Holder) view.getTag();
		}

		mHolder.name.setText(name);
		mHolder.divider.setBackgroundColor(color);

		return view;
	}

	private class Holder {
		TextView name;
		View divider;

	}

	public long getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return name;
	}

}
