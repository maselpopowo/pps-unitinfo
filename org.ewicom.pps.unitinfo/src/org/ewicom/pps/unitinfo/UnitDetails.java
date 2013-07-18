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

import java.util.List;
import java.util.Vector;

import org.ewicom.pps.unitinfo.model.PersonDataSource;
import org.ewicom.pps.unitinfo.model.PhoneDataSource;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class UnitDetails extends FragmentActivity {

	private static final String TAG = "UnitDetails";

	private long unitID;

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);

		final Intent intent = getIntent();
		unitID = intent.getLongExtra("unit_id", -1);

		Object[] fragmentData = prepareFragmentList();
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), (List<Fragment>) fragmentData[0],
				(List<String>) fragmentData[1]);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	public Object[] prepareFragmentList() {
		Bundle bundle = new Bundle();
		bundle.putLong("unitID", unitID);

		List<Fragment> fragments = new Vector<Fragment>();
		List<String> titles = new Vector<String>();

		fragments.add(Fragment.instantiate(this,
				TabAddressFragment.class.getName(), bundle));
		titles.add(getResources().getString(R.string.addressTabTitle));

		if (checkPersons()) {
			fragments.add(Fragment.instantiate(this,
					TabLeadersFragment.class.getName(), bundle));
			titles.add(getResources().getString(R.string.leadersTabTitle));
		}

		if (checkPhones()) {
			fragments.add(Fragment.instantiate(this,
					TabPhonesFragment.class.getName(), bundle));
			titles.add(getResources().getString(R.string.phonesTabTitle));
		}

		return new Object[] { fragments, titles };
	}

	public boolean checkPersons() {
		PersonDataSource ds = new PersonDataSource(this);
		ds.open();
		boolean flag = ds.isPersonsAvailable(unitID);
		ds.close();

		return flag;
	}

	public boolean checkPhones() {
		PhoneDataSource ds = new PhoneDataSource(this);
		ds.open();
		boolean flag = ds.isPhonesAvailable(unitID);
		ds.close();

		return flag;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
