package org.ewicom.pps.unitinfo.model;

import org.ewicom.pps.unitinfo.DrawerItem;
import org.ewicom.pps.unitinfo.LeftDrawerAdapter.RowType;
import org.ewicom.pps.unitinfo.R;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class DrawerHeader implements DrawerItem {

	private final String name;

	public DrawerHeader(LayoutInflater layoutInflater, String name) {
		this.name = name;
	}

	@Override
	public int getViewType() {
		return RowType.HEADER_ITEM.ordinal();
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {
		final Holder mHolder;
		View view = convertView;
		if (convertView == null) {
			mHolder = new Holder();
			view = inflater.inflate(R.layout.item_drawer_header, null);
			mHolder.name = (TextView) view
					.findViewById(R.id.text_drawer_header);
			view.setTag(mHolder);

		} else {
			mHolder = (Holder) view.getTag();
		}

		mHolder.name.setText(name);

		return view;
	}
	
	private class Holder {
		TextView name;
	}

	@Override
	public String getName() {
		return name;
	}

}
