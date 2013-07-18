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
import org.ewicom.pps.unitinfo.PPSAddressBook.PersonColumns;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PersonDataSource {

	private SQLiteDatabase db;
	private DatabaseHelper dbHelper;

	private static final String[] PERSON_PROJECTION = new String[] {
			PersonColumns._ID, PersonColumns.POSITION, PersonColumns.PERSON,
			PersonColumns.PHONE, PersonColumns.EMAIL, };

	private static final int COLUMN_INDEX_PERSON_ID = 0;
	private static final int COLUMN_INDEX_PERSON_POSITION = 1;
	private static final int COLUMN_INDEX_PERSON_PERSON = 2;
	private static final int COLUMN_INDEX_PERSON_PHONE = 3;
	private static final int COLUMN_INDEX_PERSON_EMAIL = 4;

	public PersonDataSource(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public boolean isPersonsAvailable(long unitId){
		boolean flag = true;
		
		Cursor cursor = db.query(PersonColumns.PERSON_TABLE_NAME,
				PERSON_PROJECTION,
				PersonColumns.UNIT_ID + "=" + String.valueOf(unitId), null,
				null, null, PersonColumns.DEFAULT_SORT_ORDER);
		
		if(cursor.getCount() == 0){
			flag = false;
		}
		
		cursor.close();
		return flag;
	}

	public List<Person> getAllPersons() {
		List<Person> persons = new ArrayList<Person>();

		Cursor cursor = db.query(PersonColumns.PERSON_TABLE_NAME,
				PERSON_PROJECTION, null, null, null, null,
				PersonColumns.DEFAULT_SORT_ORDER);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Person person = CursorToPerson(cursor);
			persons.add(person);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return persons;
	}

	public List<Person> getPersonsByUnitId(long unitId) {
		List<Person> persons = new ArrayList<Person>();

		Cursor cursor = db.query(PersonColumns.PERSON_TABLE_NAME,
				PERSON_PROJECTION,
				PersonColumns.UNIT_ID + "=" + String.valueOf(unitId), null,
				null, null, PersonColumns.DEFAULT_SORT_ORDER);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Person person = CursorToPerson(cursor);
			persons.add(person);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return persons;
	}

	private Person CursorToPerson(Cursor cursor) {
		Person person = new Person();

		person.setId(cursor.getLong(COLUMN_INDEX_PERSON_ID));
		person.setPosition(cursor.getString(COLUMN_INDEX_PERSON_POSITION));
		person.setPerson(cursor.getString(COLUMN_INDEX_PERSON_PERSON));
		person.setPhone(cursor.getString(COLUMN_INDEX_PERSON_PHONE));
		person.setEmail(cursor.getString(COLUMN_INDEX_PERSON_EMAIL));

		return person;
	}

}
