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
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.WindowManager;

public class PPSAddressBook {

	public static final String AUTHORITY = "org.ewicom.ppsaddressbook.provider.PPSAddressBook";
	private static final String TAG = "PPSAddressBook.Tools";

	private PPSAddressBook() {
	}
	
	public static String getAppVersionName(Activity activity){
		try {
			PackageInfo pinfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			return pinfo.versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Nie mozna pobrac nazwy pakietu i nazwy wersji oprogramowania");
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static int getAppVersionNumber(Activity activity){
		try {
			PackageInfo pinfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			return pinfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Nie mozna pobrac nazwy pakietu i numeru wersji oprogramowania");
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public static boolean isOnline(Activity activity){
		ConnectivityManager connManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connManager.getActiveNetworkInfo();
		return (netInfo != null && netInfo.isConnected());
	}
	
	public static void disableWindowOff(Activity activity){
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}
	
	public static boolean isIntentAvailable(Activity activity, Intent intent){
		final PackageManager packageManager = activity.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		
		return list.size() > 0;
	}
	
	
	public static final class PPSAddressBookPreferences{
		
		private PPSAddressBookPreferences() {	
		}
		
		public static final String DATABASE_PATH = "/data/data/org.ewicom.pps.unitinfo/databases/";
		public static final String DATABASE_NAME = "pps_addressbook.db";
		
		public static final String DATABASE_UPDATE_FILE_URL = "http://ppsaddressbook.ewicom.megiteam.pl/pps_addressbook_update.txt";
		public static final String APP_UPDATE_JSON_URL = "http://ewicom.hpbf.pl/ppsaddressbook/pps_update_webservice.php";
		public static final String RELEASE_JSON_URL = "http://ewicom.hpbf.pl/ppsaddressbook/pps_webservice.php";

		public static final String PREFERENCES_FILE = "pps_settings_file";
		public static final String PREFERENCE_KEY_DATABASE_FILE_URL = "databaseFileUrl";
		public static final String PREFERENCE_KEY_DATABASE_VERSION = "databaseVesion";
		
		public static final String APP_UPDATE_JSON_NAME_ID = "id";
		public static final String APP_UPDATE_JSON_NAME_VERSION = "version";
		public static final String APP_UPDATE_JSON_NAME_RELEASE_DATE = "release_date";
		public static final String APP_UPDATE_JSON_NAME_LINK = "link";
		public static final String APP_UPDATE_JSON_NAME_CHANGELOG = "changelog";
		
		public static final String RELEASE_JSON_NAME_ID = "id";
		public static final String RELEASE_JSON_NAME_RELEASE_DATE = "release_date";
		public static final String RELEASE_JSON_NAME_VERSION_NUMBER = "version_number";
		public static final String RELEASE_JSON_NAME_VERSION_NAME = "version_name";
		public static final String RELEASE_JSON_NAME_SYMBOL = "symbol";
		public static final String RELEASE_JSON_NAME_APK_URL = "apk_url";
		public static final String RELEASE_JSON_NAME_CHANGELOG_URL = "changelog_url";
		
		public static final String RELEASE_JSON_TAG_TESTING = "TESTING";
		public static final String RELEASE_JSON_TAG_STABLE = "STABLE";
		
		public static final String INTENT_EXTRA_UNIT_ID = "unit_id";
		public static final String INTENT_EXTRA_APP_UPDATE_AVAILABLE = "app_update_available";
		public static final String INTENT_EXTRA_APP_UPDATE_LINK = "app_update_link";
		public static final String INTENT_EXTRA_APP_UPDATE_VERSION = "app_update_version";
		public static final String INTENT_EXTRA_APP_UPDATE_RELEASE_DATE = "app_update_release_date";
		public static final String INTENT_EXTRA_APP_CHANGELOG = "app_update_changelog";
		public static final String INTENT_EXTRA_RELEASE_STABLE = "stable_release";
		public static final String INTENT_EXTRA_RELEASE_TESTING = "testing_release";
		
		public static final String PATTERN_EMAIL = "[a-zA-Z0-9]+(_[A-Za-z0-9]+)*(\\.[A-Za-z0-9]+)*@sw\\.gov\\.pl";
	}

	/**
	 * 
	 * @author Marcin Kunicki Table unit
	 */
	public static final class UnitColumns implements BaseColumns {

		private UnitColumns() {

		}

		/**
		 * Content style Uri for Unit Table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/units");

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of units.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ewicom.unit";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * unit.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.ewicom.unit";

		/**
		 * Default sort order for table unit (by name)
		 */
		public static final String DEFAULT_SORT_ORDER = "short_name ASC";

		public static final String UNIT_TABLE_NAME = "unit";

		/*****************************************************
		 * Definitions of Columns
		 */

		/**
		 * The name of unit
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";

		/**
		 * The short name of unit
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String SHORT_NAME = "short_name";

		/**
		 * The street of unit address
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String STREET = "street";

		/**
		 * The city of unit address
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String CITY = "city";

		/**
		 * Primary phone of unit
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String PHONE = "phone";

		/**
		 * Primary email of unit
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String EMAIL = "email";
		
		/**
		 * The latitude of unit
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String LATITUDE = "latitude";
		
		/**
		 * The longitude of unit
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String LONGITUDE = "longitude";
		
		public static final String IMG = "img";
		public static final String SIMG = "simg";
		public static final String DESCRIPTION = "description";
		
		public static final String PARENT_LNAME = "parent_lname";
		public static final String PARENT_ID = "parent_id";
		
		public static final String LINK = "link";
		
		public static final String UNIT_TYPE_ID = "unit_type_id";
	}

	/**
	 * 
	 * @author Marcin Kunicki Table person
	 * 
	 */
	public static final class PersonColumns implements BaseColumns {

		private PersonColumns() {

		}

		/**
		 * Content style Uri for Person Table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/persons");

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of
		 * persons.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ewicom.person";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * person.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.ewicom.person";

		/**
		 * Default sort order for table person (by _id)
		 */
		public static final String DEFAULT_SORT_ORDER = "_id ASC";

		public static final String PERSON_TABLE_NAME = "person";

		/*****************************************************
		 * Definitions of Columns
		 */

		/**
		 * Foreign key from unit table
		 * <P>
		 * type: INTEGER
		 * </P>
		 */
		public static final String UNIT_ID = "unit_id";

		/**
		 * Position name of person
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String POSITION = "position";

		/**
		 * Person rank, name and surname
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String PERSON = "person";

		/**
		 * Person phone
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String PHONE = "phone";

		/**
		 * Person email
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String EMAIL = "email";

	}

	/**
	 * 
	 * @author Marcin Kunicki Table phone
	 * 
	 */
	public static final class PhoneColumns implements BaseColumns {

		private PhoneColumns() {

		}

		/**
		 * Content style Uri for phone Table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/phones");

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of
		 * phones.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ewicom.phone";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * phone.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.ewicom.phone";

		/**
		 * Default sort order for table phone (by _id)
		 */
		public static final String DEFAULT_SORT_ORDER = "_id ASC";

		public static final String PHONE_TABLE_NAME = "phone";

		/*****************************************************
		 * Definitions of Columns
		 */

		/**
		 * Foreign key from unit table
		 * <P>
		 * type: INTEGER
		 * </P>
		 */
		public static final String UNIT_ID = "unit_id";

		/**
		 * Phone type
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String TYPE = "type";

		/**
		 * Phone number
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String PHONE = "phone";
	}
	
	public static final class UnitTypeColumns implements BaseColumns {
		/**
		 * Content style Uri for unit type Table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/unittypes");

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of
		 * unit types.
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ewicom.unittype";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * unit type.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.ewicom.unittype";

		/**
		 * Default sort order for table phone (by _id)
		 */
		public static final String DEFAULT_SORT_ORDER = "_id ASC";

		public static final String UNITTYPE_TABLE_NAME = "unit_type";

		/*****************************************************
		 * Definitions of Columns
		 */

		/**
		 * Long name
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";

		/**
		 * Short name
		 * <P>
		 * type: TEXT
		 * </P>
		 */
		public static final String SHORT_NAME = "short_name";
	}

}
