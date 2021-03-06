/**************************************************************************
 * Copyright (C) 2012 - 2013 Marcin Kunicki <masel.popowo@gmail.com>
 *
 * This file is part of PPS-UnitInfo.
 *
 * PPS-UnitInfo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * PPS-UnitInfo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PPS-UnitInfo; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Ten plik jest częścią PPS-UnitInfo.
 * 
 * PPS-UnitInfo jest wolnym oprogramowaniem; możesz go rozprowadzać dalej
 * i/lub modyfikować na warunkach Powszechnej Licencji Publicznej GNU,
 * wydanej przez Fundację Wolnego Oprogramowania - według wersji 2 tej
 * Licencji lub (według twojego wyboru) którejś z późniejszych wersji.
 * 
 * Niniejszy program rozpowszechniany jest z nadzieją, iż będzie on
 * użyteczny - jednak BEZ JAKIEJKOLWIEK GWARANCJI, nawet domyślnej
 * gwarancji PRZYDATNOŚCI HANDLOWEJ albo PRZYDATNOŚCI DO OKREŚLONYCH
 * ZASTOSOWAŃ. W celu uzyskania bliższych informacji sięgnij do
 * Powszechnej Licencji Publicznej GNU.
 * 
 * Z pewnością wraz z niniejszym programem otrzymałeś też egzemplarz
 * Powszechnej Licencji Publicznej GNU (GNU General Public License);
 * jeśli nie - napisz do Free Software Foundation, Inc., 59 Temple
 * Place, Fifth Floor, Boston, MA  02110-1301  USA
 **************************************************************************/
package org.ewicom.pps.unitinfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ewicom.pps.unitinfo.PPSAddressBook.PPSAddressBookPreferences;
import org.ewicom.pps.unitinfo.model.DrawerHeader;
import org.ewicom.pps.unitinfo.model.DrawerList;
import org.ewicom.pps.unitinfo.model.Unit;
import org.ewicom.pps.unitinfo.model.UnitDataSource;
import org.ewicom.pps.unitinfo.model.UnitType;
import org.ewicom.pps.unitinfo.model.UnitTypeDataSource;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

public class UnitsList extends ActionBarActivity implements OnQueryTextListener {

	private UnitDataSource unitDataSource;
	private UnitTypeDataSource unitTypeDataSource;

	private DrawerLayout rootLayout;
	private ActionBarDrawerToggle mDrawerToogle;

	private ListView unitlist;
	private UnitListAdapter unitAdapter;

	private ListView drawerList;
	private LeftDrawerAdapter ldrawerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_unitlist);

		rootLayout = (DrawerLayout) findViewById(R.id.drawer_layout_unitlist);
		unitlist = (ListView) findViewById(R.id.list_unitlist);
		drawerList = (ListView) findViewById(R.id.drawer_left_unitlist);

		unitDataSource = new UnitDataSource(this);
		unitDataSource.open();
		unitTypeDataSource = new UnitTypeDataSource(this);
		unitTypeDataSource.open();

		List<Unit> allUnits = unitDataSource.getAllUnits();
		List<UnitType> allTypes = unitTypeDataSource.getAllUnitTypes();

		unitAdapter = new UnitListAdapter(this, allUnits);
		unitlist.setAdapter(unitAdapter);
		unitlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(UnitsList.this, UnitDetails.class);
				intent.putExtra(PPSAddressBookPreferences.INTENT_EXTRA_UNIT_ID,
						id);
				startActivity(intent);
			}
		});

		List<DrawerItem> drawerItems = new ArrayList<DrawerItem>();
		DrawerHeader header = new DrawerHeader(getLayoutInflater(),
				getString(R.string.drawer_header_unittype));
		drawerItems.add(header);

		DrawerList allUnitsItem = new DrawerList(getLayoutInflater(),
				getString(R.string.drawer_list_allunits), 0, 0);
		drawerItems.add(allUnitsItem);

		TypedArray colors = getResources().obtainTypedArray(R.array.divider_colors);
		
		for (UnitType t : allTypes) {
			drawerItems.add(new DrawerList(getLayoutInflater(), t.getName(), t
					.getId(), colors.getColor((int) t.getId() - 1, 0)));
		}
		
		colors.recycle();

		ldrawerAdapter = new LeftDrawerAdapter(UnitsList.this, drawerItems);
		drawerList.setAdapter(ldrawerAdapter);
		drawerList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						if (id > 0) {
							List<Unit> units = unitDataSource
									.getUnitsByType(id);
							unitAdapter.repopulate(units);
							rootLayout.closeDrawer(drawerList);
							getSupportActionBar().setTitle(
									"Filtr("
											+ ldrawerAdapter.getItem(position)
													.getName() + ")");
						}

						if (id == 0) {
							List<Unit> units = unitDataSource.getAllUnits();
							unitAdapter.repopulate(units);
							rootLayout.closeDrawer(drawerList);
							getSupportActionBar().setTitle(
									R.string.title_activity_unitlist);
						}
					}
				});

		mDrawerToogle = new ActionBarDrawerToggle(this, rootLayout,
				R.drawable.ic_drawer, R.string.open_drawer,
				R.string.close_drawer) {

			@Override
			public void onDrawerClosed(View drawerView) {
			}

			@Override
			public void onDrawerOpened(View drawerView) {
			}

		};

		rootLayout.setDrawerListener(mDrawerToogle);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_unitlist, menu);

		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat
				.getActionView(searchItem);

		searchView.setOnQueryTextListener(this);
		searchView.setOnCloseListener(new OnCloseListener() {

			@Override
			public boolean onClose() {
				unitAdapter.getFilter().filter("");
				return true;
			}
		});

		MenuItemCompat.setOnActionExpandListener(searchItem,
				new OnActionExpandListener() {

					@Override
					public boolean onMenuItemActionExpand(MenuItem arg0) {
						return true;
					}

					@Override
					public boolean onMenuItemActionCollapse(MenuItem arg0) {
						unitAdapter.getFilter().filter("");
						return true;
					}
				});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToogle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToogle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToogle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case R.id.action_about:
			showAboutDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		unitDataSource.open();
		unitTypeDataSource.close();
		super.onResume();
	}

	@Override
	protected void onPause() {
		unitDataSource.close();
		unitTypeDataSource.close();
		super.onPause();
	}

	public void showAboutDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LayoutInflater inflater = this.getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_about, null);
		TextView appVersionTV = (TextView) view
				.findViewById(R.id.dialog_about_appversion);
		TextView databaseVersionTV = (TextView) view
				.findViewById(R.id.dialog_about_appdatabase);
		TextView unitCountTV = (TextView) view
				.findViewById(R.id.dialog_about_appcount);

		appVersionTV.setText("VERSION: "
				+ PPSAddressBook.getAppVersionName(this));

		SharedPreferences settings = getSharedPreferences(
				PPSAddressBookPreferences.PREFERENCES_FILE, MODE_PRIVATE);
		databaseVersionTV
				.setText("DATABASE: "
						+ settings
								.getString(
										PPSAddressBookPreferences.PREFERENCE_KEY_DATABASE_VERSION,
										"NO_VERSION"));

		unitCountTV.setText("COUNT: " + unitDataSource.getCountOfUnits());

		builder.setTitle(R.string.action_about)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(view)
				.setNeutralButton(R.string.button_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
		final AlertDialog aboutDialog = builder.create();

		aboutDialog.show();
	}

	private class UnitListAdapter extends BaseAdapter implements Filterable {

		private List<Unit> units;
		private List<Unit> fullUnitsList;
		private Context mContext;
		private LayoutInflater inflator;
		
		private TypedArray colors;

		public UnitListAdapter(Context context, List<Unit> units) {
			this.units = units;
			this.mContext = context;
			this.inflator = (LayoutInflater) mContext
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			
			colors = getResources().obtainTypedArray(R.array.divider_colors);
		}

		public void repopulate(List<Unit> newUnits) {
			this.units.clear();
			this.units = newUnits;
			UnitListAdapter.this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return units.size();
		}

		@Override
		public Object getItem(int position) {
			return units.get(position);
		}

		@Override
		public long getItemId(int position) {
			return units.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final MainListHolder mHolder;
			View v = convertView;
			if (convertView == null) {
				mHolder = new MainListHolder();
				v = inflator.inflate(R.layout.unitslist_item, null);
				mHolder.shortUnitName = (TextView) v
						.findViewById(R.id.shortUnitName);
				mHolder.longUnitName = (TextView) v
						.findViewById(R.id.longUnitName);
				mHolder.typeDivider = v.findViewById(R.id.colorDivider);
				v.setTag(mHolder);
			} else {
				mHolder = (MainListHolder) v.getTag();
			}

			mHolder.shortUnitName.setText(units.get(position).getShortName());
			String longName = units.get(position).getStreet() + ", "
					+ units.get(position).getCity();
			mHolder.longUnitName.setText(longName);

			mHolder.typeDivider.setBackgroundColor(colors.getColor(
					(int) units.get(position).getUnitTypeId() - 1, 0));

			return v;
		}

		private class MainListHolder {
			private TextView shortUnitName;
			private TextView longUnitName;
			private View typeDivider;
		}

		@Override
		public Filter getFilter() {
			return new Filter() {

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					units = (List<Unit>) results.values;
					UnitListAdapter.this.notifyDataSetChanged();

				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					if (fullUnitsList == null) {
						fullUnitsList = new ArrayList<Unit>(units);
					}

					List<Unit> filteredUnits = new ArrayList<Unit>(
							fullUnitsList);

					for (Iterator<Unit> it = filteredUnits.iterator(); it
							.hasNext();) {
						if (!it.next()
								.getShortName()
								.toLowerCase()
								.startsWith(constraint.toString().toLowerCase())) {
							it.remove();
						}
					}

					FilterResults filterResults = new FilterResults();
					filterResults.values = filteredUnits;
					filterResults.count = filteredUnits.size();
					return filterResults;

				}
			};
		}

	}

	@Override
	public boolean onQueryTextChange(String newText) {
		unitAdapter.getFilter().filter(newText);
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}
}
