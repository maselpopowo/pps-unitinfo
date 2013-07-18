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
