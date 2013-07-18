/**************************************************************************
 * Copyright (C) 2012 Marcin Kunicki <masel.popowo@gmail.com>
 * $LastChangedRevision: 78 $
 * $LastChangedBy: masel.popowo@gmail.com $
 * $LastChangedDate: 2012-12-04 10:22:44 +0100 (Wt, 04 gru 2012) $
 * $HeadURL: https://pps-addressbook.googlecode.com/svn/trunk/src/org/ewicom/ppsaddressbook/SplashActivity.java $
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import org.ewicom.pps.unitinfo.PPSAddressBook.PPSAddressBookPreferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

// TODO Uzycie androidowego httpUrlConection
// TODO Dokumentacja, argumenty itp
// TODO Trimowanie dla stringow
public class SplashActivity extends Activity {

	private static final String TAG = "SplashActivity";
	private static final int SPLASH_SCREEN_DURATION = 1000;

	private static String sDatabaseFileUrl;
	private static File sCasheFile;

	private SharedPreferences settings;
	private Editor editor;
	private TimerTask timerTask;
	private Timer timer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		settings = getSharedPreferences(
				PPSAddressBookPreferences.PREFERENCES_FILE, MODE_PRIVATE);
		sDatabaseFileUrl = settings.getString(
				PPSAddressBookPreferences.PREFERENCE_KEY_DATABASE_FILE_URL,
				"NO_URL");
		editor = settings.edit();

		// Blokada wygaszenia ekranu i blokady
		PPSAddressBook.disableWindowOff(this);

		timerTask = new TimerTask() {

			@Override
			public void run() {
				finish();
				Intent intent = new Intent(SplashActivity.this, UnitsList.class);
				startActivity(intent);
			}
		};

		timer = new Timer();

		if (PPSAddressBook.isOnline(this)) {
			new CheckUpdateTask(this).execute();
		} else {
			if (sDatabaseFileUrl.equals("NO_URL")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.err_apprequiresnetwork)
						.setCancelable(false)
						.setNeutralButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										SplashActivity.this.finish();
									}
								});

				AlertDialog noNetworkAlertDialog = builder.create();
				noNetworkAlertDialog.show();
			} else {
				timer.schedule(timerTask, SPLASH_SCREEN_DURATION);
			}
		}

	}

	private void updateDatabase() {
		new CopyDatabaseFile(this).execute();
	}

	private void downloadDatabaseFile() {
		new DownloadDatabaseFileTask(this).execute();
	}

	public String makeDatabaseVersion() {
		int slashIndex = sDatabaseFileUrl.lastIndexOf("_");
		Log.v(TAG, "slashIndex: " + slashIndex);
		int dotIndex = sDatabaseFileUrl.lastIndexOf(".");
		Log.v(TAG, "dotIndexIndex: " + dotIndex);
		String databaseVersionName = sDatabaseFileUrl.substring(slashIndex + 1,
				dotIndex);
		return databaseVersionName;
	}

	private class CheckUpdateTask extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog progressDialog;

		private URLConnection urlConnection;
		private URL url;

		private String databaseFileUrl;

		public CheckUpdateTask(Activity activity) {
			progressDialog = new ProgressDialog(activity);
			progressDialog.setMessage(getResources().getString(
					R.string.info_appcheckupdate));
			progressDialog.setIndeterminate(true);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				url = new URL(
						PPSAddressBookPreferences.DATABASE_UPDATE_FILE_URL);

				urlConnection = url.openConnection();
				urlConnection.connect();

				BufferedReader in = new BufferedReader(new InputStreamReader(
						url.openStream()));

				String str;
				while ((str = in.readLine()) != null) {
					Log.v(TAG, str);
					databaseFileUrl = str;
				}

				in.close();

				if (databaseFileUrl.equals(sDatabaseFileUrl)) {
					return false;
				}

			} catch (MalformedURLException e) {
				Log.e(TAG, "Bledny adres pliku aktualizacji");
			} catch (IOException e) {
				Log.e(TAG, "Blad IO dla pliku aktualizacji");
			}

			sDatabaseFileUrl = databaseFileUrl;
			return true;
		}

		@Override
		protected void onPostExecute(Boolean updateNesesery) {
			super.onPostExecute(updateNesesery);
			progressDialog.dismiss();
			if (updateNesesery) {
				downloadDatabaseFile();
			} else {
				timer.schedule(timerTask, SPLASH_SCREEN_DURATION);
			}
		}

	}

	private class DownloadDatabaseFileTask extends
			AsyncTask<Void, Integer, Void> {

		private Activity activity;

		private ProgressDialog progressDialog;

		private URL url;
		private URLConnection urlConnection;
		private int databaseFileLength;
		private File outputFile;

		public DownloadDatabaseFileTask(Activity activity) {
			this.activity = activity;

			progressDialog = new ProgressDialog(activity);
			progressDialog.setMessage(getResources().getString(
					R.string.info_appdownloaddatabasefile));
			progressDialog.setIndeterminate(false);
			progressDialog.setMax(100);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				url = new URL(sDatabaseFileUrl);

				urlConnection = url.openConnection();
				urlConnection.connect();

				databaseFileLength = urlConnection.getContentLength();

				outputFile = new File(activity.getCacheDir(), "pps_database.db");

				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(outputFile);

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress((int) (total * 100 / databaseFileLength));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
			} catch (MalformedURLException e) {
				Log.e(TAG, "Zly adres pliku z baza danych");
			} catch (IOException e) {
				Log.e(TAG, "Blad zapisu pliku bazy na urzadzeniu");
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			progressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			sCasheFile = outputFile;
			updateDatabase();
		}

	}

	private class CopyDatabaseFile extends AsyncTask<Void, Integer, Void> {

		private DatabaseHelper dbHelper;
		private ProgressDialog progressDialog;

		public CopyDatabaseFile(Activity activity) {
			this.dbHelper = new DatabaseHelper(activity);

			progressDialog = new ProgressDialog(activity);
			progressDialog.setMessage(getResources().getString(
					R.string.info_copydatabasefile));
			progressDialog.setIndeterminate(false);
			progressDialog.setMax(100);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (checkDatabase()) {

				try {
					File inFile = sCasheFile;

					long f_length = inFile.length();
					BufferedInputStream buffInput = new BufferedInputStream(
							new FileInputStream(inFile));

					File outFile = new File(
							PPSAddressBookPreferences.DATABASE_PATH,
							PPSAddressBookPreferences.DATABASE_NAME);

					OutputStream out = new FileOutputStream(outFile);

					byte data[] = new byte[1024];
					long total = 0;
					int count;
					while ((count = buffInput.read(data)) != -1) {
						total += count;
						publishProgress((int) (total * 100 / f_length));
						out.write(data, 0, count);
					}

					out.flush();
					out.close();
					buffInput.close();
				} catch (IOException e) {
					Log.e(TAG, "Blad kopiowania bazy danych");
				}
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			progressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (checkDatabase()) {
				editor.putString(
						PPSAddressBookPreferences.PREFERENCE_KEY_DATABASE_FILE_URL,
						sDatabaseFileUrl);
				editor.putString(
						PPSAddressBookPreferences.PREFERENCE_KEY_DATABASE_VERSION,
						makeDatabaseVersion());
				editor.commit();
			}
			timer.schedule(timerTask, SPLASH_SCREEN_DURATION);
		}

		private boolean checkDatabase() {
			SQLiteDatabase db = null;

			db = dbHelper.getReadableDatabase();

			if (db != null) {
				db.close();
			}

			return db != null ? true : false;
		}

	}

	

}
