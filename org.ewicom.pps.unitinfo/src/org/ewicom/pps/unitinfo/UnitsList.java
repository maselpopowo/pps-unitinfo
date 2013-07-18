/**************************************************************************
 * Copyright (C) 2012 Marcin Kunicki <masel.popowo@gmail.com>
 * $LastChangedRevision: 99 $
 * $LastChangedBy: masel.popowo@gmail.com $
 * $LastChangedDate: 2013-04-17 12:12:07 +0200 (Śr, 17 kwi 2013) $
 * $HeadURL: https://pps-addressbook.googlecode.com/svn/trunk/src/org/ewicom/ppsaddressbook/UnitsList.java $
 *
 * This file is part of PPS-AddressBook.
 *
 * PPS-AddressBook is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * PPS-AddressBook is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PPS-AddressBook; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Ten plik jest częścią PPS-AddressBook.
 * 
 * PPS-AddressBook jest wolnym oprogramowaniem; możesz go rozprowadzać dalej
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
import org.ewicom.pps.unitinfo.model.Unit;
import org.ewicom.pps.unitinfo.model.UnitDataSource;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

public class UnitsList extends ListActivity {

	private static final String TAG = "UnitsList";

	private UnitDataSource unitDataSource;

	private TextWatcher searchTextWatcher;
	private EditText searchEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.unitslist_searchview);

		unitDataSource = new UnitDataSource(this);
		unitDataSource.open();

		List<Unit> units = unitDataSource.getAllUnits();

		final UnitListAdapter adapter = new UnitListAdapter(this, units);
		setListAdapter(adapter);

		searchTextWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.d("WYSZUKIWANIE: ", s.toString());
				adapter.getFilter().filter(s);
			}
		};

		searchEditText = (EditText) findViewById(R.id.unitslist_searchbox);
		searchEditText.addTextChangedListener(searchTextWatcher);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			showAboutDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent();
		intent.setClass(this, UnitDetails.class);
		intent.putExtra(PPSAddressBookPreferences.INTENT_EXTRA_UNIT_ID, id);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		unitDataSource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		unitDataSource.close();
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

		builder.setTitle(R.string.menu_about)
				.setIcon(R.drawable.ic_action_about)
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

		public UnitListAdapter(Context context, List<Unit> units) {
			this.units = units;
			this.mContext = context;
			this.inflator = (LayoutInflater) mContext
					.getSystemService(LAYOUT_INFLATER_SERVICE);
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
				v.setTag(mHolder);
			} else {
				mHolder = (MainListHolder) v.getTag();
			}

			mHolder.shortUnitName.setText(units.get(position).getShortName());
			String longName = units.get(position).getStreet() + ", "
					+ units.get(position).getCity();
			mHolder.longUnitName.setText(longName);

			return v;
		}

		private class MainListHolder {
			private TextView shortUnitName;
			private TextView longUnitName;
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

}
