package org.ewicom.pps.unitinfo;

import android.view.LayoutInflater;
import android.view.View;

public interface DrawerItem {
	public String getName();
	public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
}
