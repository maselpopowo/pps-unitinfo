package org.ewicom.pps.unitinfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ewicom.pps.unitinfo.model.Person;
import org.ewicom.pps.unitinfo.model.PersonDataSource;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class TabLeadersFragment extends Fragment {

	private PersonDataSource personDataSource;
	private long unitID;

	public TabLeadersFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.tab_leaders, container, false);

		Bundle args = getArguments();
		if (args != null) {
			this.unitID = args.getLong("unitID");
		}

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		personDataSource = new PersonDataSource(getActivity());
		personDataSource.open();

		LinearLayout leadersLL = (LinearLayout) getView().findViewById(
				R.id.ll_leaders);

		List<Person> persons = new ArrayList<Person>();
		persons = personDataSource.getPersonsByUnitId(unitID);

		if (persons != null && !persons.isEmpty()) {
			Iterator<Person> iterator = persons.iterator();

			while (iterator.hasNext()) {

				final Person person = iterator.next();

				LinearLayout personLL = (LinearLayout) getActivity()
						.getLayoutInflater().inflate(R.layout.person_ll, null);

				TextView positionTV = (TextView) personLL
						.findViewById(R.id.text_personposition);
				TextView nameTV = (TextView) personLL
						.findViewById(R.id.text_personname);
				TextView phoneTV = (TextView) personLL
						.findViewById(R.id.text_personphone);
				TextView emailTV = (TextView) personLL
						.findViewById(R.id.text_personemail);

				positionTV.setText(person.getPosition());
				nameTV.setText(person.getPerson());
				phoneTV.setText(person.getPhone());
				emailTV.setText(person.getEmail());

				Linkify.addLinks(phoneTV, Linkify.PHONE_NUMBERS);
				Linkify.addLinks(emailTV, Linkify.EMAIL_ADDRESSES);
				
				TextView saveContactLink = (TextView) getActivity().getLayoutInflater().inflate(R.layout.textview_link_savecontact, null);
				LayoutParams linkParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				personLL.addView(saveContactLink, linkParam);
				saveContactLink.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent saveContactIntent = new Intent(Intents.Insert.ACTION);
						saveContactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
						
						saveContactIntent.putExtra(Intents.Insert.NAME, person.getPerson());
						saveContactIntent.putExtra(Intents.Insert.JOB_TITLE, person.getPosition());
						saveContactIntent.putExtra(Intents.Insert.PHONE, person.getPhone());
						saveContactIntent.putExtra(Intents.Insert.EMAIL, person.getEmail());
						
						startActivity(saveContactIntent);
					}
				});

				leadersLL.addView(personLL);

			}

		}

	}

	@Override
	public void onPause() {
		personDataSource.close();
		super.onPause();
	}

	@Override
	public void onResume() {
		personDataSource.open();
		super.onResume();
	}

}
