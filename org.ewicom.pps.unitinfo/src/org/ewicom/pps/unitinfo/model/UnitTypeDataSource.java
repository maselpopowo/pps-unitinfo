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
import org.ewicom.pps.unitinfo.PPSAddressBook.UnitTypeColumns;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class UnitTypeDataSource {

	private SQLiteDatabase db;
	private DatabaseHelper dbHelper;

	private static final String[] UNIT_TYPE_PROJECTION = new String[] {
			UnitTypeColumns._ID, UnitTypeColumns.NAME,
			UnitTypeColumns.SHORT_NAME };

	private static final int COLUMN_INDEX_UNIT_TYPE_ID = 0;
	private static final int COLUMN_INDEX_UNIT_TYPE_NAME = 1;
	private static final int COLUMN_INDEX_UNIT_TYPE_SHORT_NAME = 2;

	public UnitTypeDataSource(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public List<UnitType> getAllUnitTypes() {
		List<UnitType> types = new ArrayList<UnitType>();

		Cursor cursor = db.query(UnitTypeColumns.UNITTYPE_TABLE_NAME,
				UNIT_TYPE_PROJECTION, null, null, null, null,
				UnitTypeColumns.DEFAULT_SORT_ORDER);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			UnitType unitType = cursorToUnitType(cursor);
			types.add(unitType);
			cursor.moveToNext();
		}
		
		cursor.close();
		return types;

	}

	private UnitType cursorToUnitType(Cursor cursor) {
		UnitType unitType = new UnitType();

		unitType.setId(cursor.getLong(COLUMN_INDEX_UNIT_TYPE_ID));
		unitType.setName(cursor.getString(COLUMN_INDEX_UNIT_TYPE_NAME));
		unitType.setShortName(cursor
				.getString(COLUMN_INDEX_UNIT_TYPE_SHORT_NAME));

		return unitType;
	}
}
