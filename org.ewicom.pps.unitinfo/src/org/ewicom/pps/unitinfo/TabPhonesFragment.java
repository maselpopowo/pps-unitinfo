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

import org.ewicom.pps.unitinfo.model.Phone;
import org.ewicom.pps.unitinfo.model.PhoneDataSource;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabPhonesFragment extends Fragment {

	private long unitID;
	private PhoneDataSource phoneDataSource;

	public TabPhonesFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.tab_phones, container, false);

		Bundle args = getArguments();
		if (args != null) {
			this.unitID = args.getLong("unitID");
		}

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		phoneDataSource = new PhoneDataSource(getActivity());
		phoneDataSource.open();

		LinearLayout phonesLL = (LinearLayout) getView().findViewById(
				R.id.ll_phones);

		List<Phone> phones = new ArrayList<Phone>();
		phones = phoneDataSource.getPhonesByUnitId(unitID);

		if (phones != null && !phones.isEmpty()) {
			Iterator<Phone> iterator = phones.iterator();

			while (iterator.hasNext()) {

				Phone phone = iterator.next();

				LinearLayout phoneLL = (LinearLayout) getActivity()
						.getLayoutInflater().inflate(R.layout.phone_ll, null);

				TextView phoneTypeTV = (TextView) phoneLL
						.findViewById(R.id.text_phonetype);
				TextView phoneTV = (TextView) phoneLL
						.findViewById(R.id.text_phone);

				phoneTypeTV.setText(phone.getType());
				phoneTV.setText(phone.getPhone());

				Linkify.addLinks(phoneTV, Linkify.PHONE_NUMBERS);

				phonesLL.addView(phoneLL);
			}
		}
	}

	@Override
	public void onPause() {
		phoneDataSource.close();
		super.onPause();
	}

	@Override
	public void onResume() {
		phoneDataSource.open();
		super.onResume();
	}

}
