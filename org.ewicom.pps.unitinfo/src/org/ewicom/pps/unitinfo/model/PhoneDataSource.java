/**************************************************************************
 * Copyright (C) 2012 Marcin Kunicki <masel.popowo@gmail.com>
 * $LastChangedRevision: 39 $
 * $LastChangedBy: masel.popowo@gmail.com $
 * $LastChangedDate: 2012-10-19 11:13:36 +0200 (Pt, 19 paź 2012) $
 * $HeadURL: https://pps-addressbook.googlecode.com/svn/trunk/src/org/ewicom/ppsaddressbook/model/PhoneDataSource.java $
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
package org.ewicom.pps.unitinfo.model;

import java.util.ArrayList;
import java.util.List;

import org.ewicom.pps.unitinfo.DatabaseHelper;
import org.ewicom.pps.unitinfo.PPSAddressBook.PhoneColumns;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PhoneDataSource {

	private SQLiteDatabase db;
	private DatabaseHelper dbHelper;

	private static final String[] PHONE_PROJECTION = new String[] {
			PhoneColumns._ID, PhoneColumns.TYPE, PhoneColumns.PHONE, };

	private static final int COLUMN_INDEX_PHONE_ID = 0;
	private static final int COLUMN_INDEX_PHONE_TYPE = 1;
	private static final int COLUMN_INDEX_PHONE_PHONE = 2;

	public PhoneDataSource(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getReadableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public boolean isPhonesAvailable(long unitId){
		boolean flag = true;
		
		Cursor cursor = db.query(PhoneColumns.PHONE_TABLE_NAME,
				PHONE_PROJECTION,
				PhoneColumns.UNIT_ID + "=" + String.valueOf(unitId), null,
				null, null, PhoneColumns.DEFAULT_SORT_ORDER);
		
		if(cursor.getCount() == 0){
			flag = false;
		}
		
		cursor.close();
		return flag;
	}

	public List<Phone> getAllPhones() {
		List<Phone> phones = new ArrayList<Phone>();

		Cursor cursor = db.query(PhoneColumns.PHONE_TABLE_NAME,
				PHONE_PROJECTION, null, null, null, null,
				PhoneColumns.DEFAULT_SORT_ORDER);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Phone phone = CursorToPhone(cursor);
			phones.add(phone);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return phones;
	}

	public List<Phone> getPhonesByUnitId(long unitId) {
		List<Phone> phones = new ArrayList<Phone>();

		Cursor cursor = db.query(PhoneColumns.PHONE_TABLE_NAME,
				PHONE_PROJECTION,
				PhoneColumns.UNIT_ID + "=" + String.valueOf(unitId), null,
				null, null, PhoneColumns.DEFAULT_SORT_ORDER);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Phone phone = CursorToPhone(cursor);
			phones.add(phone);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return phones;
	}

	private Phone CursorToPhone(Cursor cursor) {
		Phone phone = new Phone();

		phone.setId(cursor.getLong(COLUMN_INDEX_PHONE_ID));
		phone.setType(cursor.getString(COLUMN_INDEX_PHONE_TYPE));
		phone.setPhone(cursor.getString(COLUMN_INDEX_PHONE_PHONE));

		return phone;
	}

}
