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

package org.ewicom.pps.unitinfo.model;

import java.util.ArrayList;
import java.util.List;

import org.ewicom.pps.unitinfo.DatabaseHelper;
import org.ewicom.pps.unitinfo.PPSAddressBook.UnitColumns;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class UnitDataSource {

	private SQLiteDatabase db;
	private DatabaseHelper dbHelper;

	private static final String[] UNIT_PROJECTION = new String[] {
			UnitColumns._ID, UnitColumns.NAME, UnitColumns.SHORT_NAME,
			UnitColumns.STREET, UnitColumns.CITY,
			UnitColumns.PHONE, UnitColumns.EMAIL, UnitColumns.LATITUDE, UnitColumns.LONGITUDE,
			UnitColumns.DESCRIPTION, UnitColumns.IMG, UnitColumns.SIMG, 
			UnitColumns.PARENT_LNAME, UnitColumns.PARENT_ID, 
			UnitColumns.LINK, UnitColumns.UNIT_TYPE_ID};

	private static final int COLUMN_INDEX_UNIT_ID = 0;
	private static final int COLUMN_INDEX_UNIT_NAME = 1;
	private static final int COLUMN_INDEX_UNIT_SHORT_NAME = 2;
	private static final int COLUMN_INDEX_UNIT_STREET = 3;
	private static final int COLUMN_INDEX_UNIT_CITY = 4;
	private static final int COLUMN_INDEX_UNIT_PHONE = 5;
	private static final int COLUMN_INDEX_UNIT_EMAIL = 6;
	private static final int COLUMN_INDEX_UNIT_LATITUDE = 7;
	private static final int COLUMN_INDEX_UNIT_LONGITUDE = 8;
	private static final int COLUMN_INDEX_UNIT_DESCRIPTION = 9;
	private static final int COLUMN_INDEX_UNIT_IMG = 10;
	private static final int COLUMN_INDEX_UNIT_SIMG = 11;
	private static final int COLUMN_INDEX_PARENT_LNAME = 12;
	private static final int COLUMN_INDEX_PARENT_ID = 13;
	private static final int COLUMN_INDEX_UNIT_LINK = 14;
	private static final int COLUMN_INDEX_UNIT_TYPE_ID = 15;

	public UnitDataSource(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getReadableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public int getCountOfUnits() {
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + UnitColumns.UNIT_TABLE_NAME, null);
		
		if(cursor.getCount() == 1){
			cursor.moveToFirst();
			int count = cursor.getInt(0);
			cursor.close();
			return count;
		}
		
		cursor.close();
		return -1;
	}

	public Unit getUnitById(long id) {
		Cursor cursor = db.query(UnitColumns.UNIT_TABLE_NAME, UNIT_PROJECTION,
				UnitColumns._ID + "=" + String.valueOf(id), null, null, null,
				UnitColumns.DEFAULT_SORT_ORDER);

		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			Unit unit = cursorToUnit(cursor);
			cursor.close();
			return unit;
		}

		cursor.close();
		return null;
	}

	public List<Unit> getAllUnits() {
		List<Unit> units = new ArrayList<Unit>();

		Cursor cursor = db.query(UnitColumns.UNIT_TABLE_NAME, UNIT_PROJECTION,
				null, null, null, null, UnitColumns.DEFAULT_SORT_ORDER);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Unit unit = cursorToUnit(cursor);
			units.add(unit);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return units;
	}
	
	public List<Unit> getUnitsByType(long unitTypeId){
		List<Unit> units = new ArrayList<Unit>();
		
		Cursor cursor = db.query(UnitColumns.UNIT_TABLE_NAME, UNIT_PROJECTION,
				UnitColumns.UNIT_TYPE_ID + "=" + String.valueOf(unitTypeId), null, null, null,
				UnitColumns.DEFAULT_SORT_ORDER);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Unit unit = cursorToUnit(cursor);
			units.add(unit);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return units;
	}

	private Unit cursorToUnit(Cursor cursor) {
		Unit unit = new Unit();

		unit.setId(cursor.getLong(COLUMN_INDEX_UNIT_ID));
		unit.setName(cursor.getString(COLUMN_INDEX_UNIT_NAME));
		unit.setShortName(cursor.getString(COLUMN_INDEX_UNIT_SHORT_NAME));
		unit.setStreet(cursor.getString(COLUMN_INDEX_UNIT_STREET));
		unit.setCity(cursor.getString(COLUMN_INDEX_UNIT_CITY));
		unit.setPhone(cursor.getString(COLUMN_INDEX_UNIT_PHONE));
		unit.setEmail(cursor.getString(COLUMN_INDEX_UNIT_EMAIL));
		unit.setLatitude(cursor.getString(COLUMN_INDEX_UNIT_LATITUDE));
		unit.setLongitude(cursor.getString(COLUMN_INDEX_UNIT_LONGITUDE));
		unit.setDescription(cursor.getString(COLUMN_INDEX_UNIT_DESCRIPTION));
		unit.setImg(cursor.getString(COLUMN_INDEX_UNIT_IMG));
		unit.setSimg(cursor.getString(COLUMN_INDEX_UNIT_SIMG));
		unit.setParentLname(cursor.getString(COLUMN_INDEX_PARENT_LNAME));
		unit.setParentId(cursor.getLong(COLUMN_INDEX_PARENT_ID));
		unit.setLink(cursor.getString(COLUMN_INDEX_UNIT_LINK));
		unit.setUnitTypeId(cursor.getLong(COLUMN_INDEX_UNIT_TYPE_ID));

		return unit;
	}
}
