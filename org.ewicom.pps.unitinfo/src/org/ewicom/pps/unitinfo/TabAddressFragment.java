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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.ewicom.pps.unitinfo.PPSAddressBook.PPSAddressBookPreferences;
import org.ewicom.pps.unitinfo.model.Unit;
import org.ewicom.pps.unitinfo.model.UnitDataSource;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TabAddressFragment extends Fragment {

	private UnitDataSource unitDataSource;
	private Long unitID;
	private Unit unit;

	private TextView showImage;
	private Helper helper;

	private LinearLayout phonesGroup;
	private LinearLayout emailsGroup;

	public TabAddressFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.tab_address, container,
				false);

		Bundle args = getArguments();
		if (args != null) {
			this.unitID = args.getLong("unitID");
		}

		TextView fullDescriptionLink = (TextView) rootView
				.findViewById(R.id.link_opendescription);
		fullDescriptionLink.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openUnitWebsite(rootView);
			}
		});

		TextView openMapLink = (TextView) rootView
				.findViewById(R.id.link_openmap);
		openMapLink.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openUnitOnMap(rootView);
			}
		});

		TextView openParent = (TextView) rootView
				.findViewById(R.id.text_unitparent);
		openParent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openParent(rootView);
			}
		});

		showImage = (TextView) rootView.findViewById(R.id.link_openimg);
		showImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				downloadImageAndShow();
			}
		});

		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		unitDataSource = new UnitDataSource(getActivity());
		unitDataSource.open();

		unit = unitDataSource.getUnitById(unitID);

		helper = new Helper();
		phonesGroup = (LinearLayout) getView().findViewById(R.id.group_phones);
		emailsGroup = (LinearLayout) getView().findViewById(R.id.group_emails);

		setUnitName();
		setUnitParent();
		setStreet();
		setCity();
		setPhone();
		setEmail();
		setDescription();

		new PreparePhoneButtons().execute();
		new PrepareEmailButtons().execute();

		if (unit.getSimg().isEmpty()) {
			showImage.setTextColor(Color.GRAY);
			showImage.setClickable(false);
			showImage.setEnabled(false);
		}

	}

	public void downloadImageAndShow() {
		if (PPSAddressBook.isOnline(getActivity())) {
			new DownloadUitImageTask().execute(unit.getSimg());
		} else {
			Toast.makeText(getActivity().getApplicationContext(),
					R.string.err_nonetwork, Toast.LENGTH_LONG).show();
		}
	}

	public void setUnitName() {
		TextView unitName = (TextView) getView().findViewById(
				R.id.text_unitname);
		unitName.setText(unit.getName());
	}

	public void setUnitParent() {
		TextView unitParent = (TextView) getView().findViewById(
				R.id.text_unitparent);
		unitParent.setText(unit.getParentLname());
	}

	public void setStreet() {
		TextView street = (TextView) getView().findViewById(
				R.id.text_unitstreet);
		street.setText(unit.getStreet());
	}

	public void setCity() {
		TextView city = (TextView) getView().findViewById(R.id.text_unitcity);
		city.setText(unit.getCity());
	}

	public void setPhone() {
		TextView phone = (TextView) getView().findViewById(R.id.text_unitphone);
		phone.setText(unit.getPhone());

		Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
	}

	public void setEmail() {
		TextView email = (TextView) getView().findViewById(R.id.text_unitemail);
		email.setText(unit.getEmail());

		Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
	}

	public void setDescription() {
		TextView desc = (TextView) getView().findViewById(
				R.id.text_unitdescription);
		String sDescription = unit.getDescription();

		if (!sDescription.isEmpty()) {
			desc.setText(sDescription);
		} else {
			LinearLayout descriptionLL = (LinearLayout) getView().findViewById(
					R.id.panel_unitdescription);
			TextView emptyDescription = (TextView) getActivity()
					.getLayoutInflater().inflate(R.layout.empty_description,
							null);

			descriptionLL.removeView(desc);
			descriptionLL.addView(emptyDescription, 0);

		}
	}

	public void openUnitWebsite(View view) {

		Toast toast = Toast.makeText(getActivity().getApplicationContext(),
				R.string.no_www_app, Toast.LENGTH_LONG);
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(unit.getLink()));

		if (PPSAddressBook.isIntentAvailable(getActivity(), intent)) {
			startActivity(intent);
		} else {
			toast.show();
		}
	}

	public void openUnitOnMap(View view) {
		String latitude = unit.getLatitude();
		String longitude = unit.getLongitude();

		Context context = getActivity().getApplicationContext();
		Toast noGeotoast = Toast.makeText(context, R.string.empty_geodata,
				Toast.LENGTH_SHORT);
		Toast noAppMap = Toast.makeText(context, R.string.no_map_app,
				Toast.LENGTH_LONG);

		if (latitude.equals("BRAK")) {
			noGeotoast.show();
		} else {
			String geoUri = "geo:" + latitude + "," + longitude + "?q="
					+ latitude + "," + longitude + "(" + unit.getShortName()
					+ ")";

			Intent showUnitOnMapIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(geoUri));
			if (PPSAddressBook.isIntentAvailable(getActivity(),
					showUnitOnMapIntent)) {
				startActivity(showUnitOnMapIntent);
			} else {
				noAppMap.show();
			}

		}
	}

	public void openParent(View view) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), UnitDetails.class);
		intent.putExtra(PPSAddressBookPreferences.INTENT_EXTRA_UNIT_ID,
				unit.getParentId());
		startActivity(intent);
		getActivity().finish();
	}

	@Override
	public void onPause() {
		unitDataSource.close();
		super.onPause();
	}

	@Override
	public void onResume() {
		unitDataSource.open();
		super.onResume();
	}

	private class DownloadUitImageTask extends AsyncTask<String, Void, Bitmap> {

		private ProgressBar progressBar;
		private ImageView imageView;
		private LinearLayout imageLL;

		public DownloadUitImageTask() {
			imageLL = (LinearLayout) getView().findViewById(
					R.id.panel_unitimage);
			progressBar = (ProgressBar) getView().findViewById(
					R.id.progerss_image);
			imageView = (ImageView) getView().findViewById(R.id.unitimage);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			imageLL.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Bitmap doInBackground(String... urls) {
			try {

				String url = urls[0];

				HttpUriRequest request = new HttpGet(url.toString());
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = httpClient.execute(request);

				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					byte[] bytes = EntityUtils.toByteArray(entity);

					Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
							bytes.length);
					return bitmap;
				} else {
					throw new IOException(
							"Download failed, HTTP response code " + statusCode
									+ " - " + statusLine.getReasonPhrase());
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Bitmap image) {
			super.onPostExecute(image);
			progressBar.setVisibility(View.GONE);
			imageView.setImageBitmap(image);
		}

	}

	private class PreparePhoneButtons extends
			AsyncTask<Void, Void, List<Button>> {

		@Override
		protected List<Button> doInBackground(Void... params) {
			String sPhones = unit.getPhone();
			String[] phones = helper.splitPhones(sPhones);
			List<Button> buttonsList = new ArrayList<Button>();

			if (phones.length > 0 && phones != null) {
				for (String p : phones) {
					p = helper.cleanPhoneString(p);
					buttonsList.add(generatePhoneButton(p));
				}
			}

			return buttonsList;
		}

		@Override
		protected void onPostExecute(List<Button> result) {
			super.onPostExecute(result);

			for (Button b : result) {
				phonesGroup.addView(b);
			}

		}

		private Button generatePhoneButton(String phone) {
			String label = phone;
			String number = phone.replaceAll("\\D+", "");
			Button button = null;

			int phoneType = helper.checkPhoneType(phone);

			switch (phoneType) {
			case Helper.CELL_PHONE_FORMAT:
				button = (Button) getActivity().getLayoutInflater().inflate(
						R.layout.button_cell_phone, null);
				label = helper.formatCellPhone(number);
				break;
			case Helper.LANDLINE_PHONE_FORMAT:
				button = (Button) getActivity().getLayoutInflater().inflate(
						R.layout.button_landline_phone, null);
				label = helper.formatLandlinePhone(number);
				break;
			case Helper.EXTENSION_PHONE_FORMAT:
				button = (Button) getActivity().getLayoutInflater().inflate(
						R.layout.button_extension_phone, null);
				label = helper.formatExtensionPhone(number);
				number = number.substring(0, 9) + "," + number.substring(9);
				break;
			default:
				break;
			}

			final String numberForIntent = number;
			button.setText(label);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent dialIntent = new Intent(Intent.ACTION_DIAL);
					dialIntent.setData(Uri.parse("tel:"
							+ Uri.encode(numberForIntent)));
					startActivity(dialIntent);
				}
			});

			return button;
		}
	}

	private class PrepareEmailButtons extends
			AsyncTask<Void, Void, List<Button>> {

		@Override
		protected List<Button> doInBackground(Void... params) {
			String sEmails = unit.getEmail();
			List<String> splitedEmails = helper.splitEmails(sEmails);
			List<Button> buttons = new ArrayList<Button>();

			for (String e : splitedEmails) {
				buttons.add(generateEmailButton(e));
			}

			return buttons;
		}

		@Override
		protected void onPostExecute(List<Button> result) {
			super.onPostExecute(result);

			for (Button b : result) {
				emailsGroup.addView(b);
			}

		}

		private Button generateEmailButton(String sEmail) {
			final String emailToSend = sEmail;
			Button button = (Button) getActivity().getLayoutInflater().inflate(
					R.layout.button_email, null);

			button.setText(emailToSend);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
					emailIntent.setData(Uri.parse("mailto:" + emailToSend));
					if (PPSAddressBook.isIntentAvailable(getActivity(),
							emailIntent)) {
						startActivity(emailIntent);
					} else {
						Toast.makeText(getActivity().getApplicationContext(),
								R.string.no_app_email, Toast.LENGTH_LONG)
								.show();
					}
				}
			});

			return button;
		}

	}

}
