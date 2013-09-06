package org.ewicom.pps.unitinfo;

import java.util.List;

import org.ewicom.pps.unitinfo.model.DrawerList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * LeftDrawerAdapter is class adapter for left drawer in UnitList Activity.
 * 
 * @author Marcin Kunicki
 * @since 1.1
 * 
 */
public class LeftDrawerAdapter extends BaseAdapter {

	private List<DrawerItem> mItems;
	private Context mContext;
	private LayoutInflater mLayoutInflater;

	public enum RowType {
		HEADER_ITEM, LIST_ITEM;
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            - Actual context
	 * @param types
	 *            - List of Unit Types
	 */
	public LeftDrawerAdapter(Context context, List<DrawerItem> types) {
		mContext = context;
		mItems = types;
		mLayoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getViewTypeCount() {
		return RowType.values().length;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getViewType();
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public DrawerItem getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getItem(position).getView(mLayoutInflater, convertView);
	}

	@Override
	public long getItemId(int position) {
		DrawerItem item = getItem(position);
		if(item instanceof DrawerList){
			return ((DrawerList) item).getId();
		}
		
		return -1;
	}
}
