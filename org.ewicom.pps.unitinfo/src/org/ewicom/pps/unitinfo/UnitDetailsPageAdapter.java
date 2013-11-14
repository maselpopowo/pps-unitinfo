package org.ewicom.pps.unitinfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class UnitDetailsPageAdapter extends FragmentStatePagerAdapter {
	
	private long unitID = -1L;
	
	public UnitDetailsPageAdapter(FragmentManager fm) {
		super(fm);	
	}
	
	public UnitDetailsPageAdapter(FragmentManager fm, long unitID) {
		super(fm);
		this.unitID = unitID;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;

		switch (position) {
		case UnitDetails.ADDRESS_TAB_POSITION:
			fragment = new TabAddressFragment();
			break;
		case UnitDetails.LEADERS_TAB_POSITION:
			fragment = new TabLeadersFragment();
			break;
		case UnitDetails.PHONES_TAB_POSITION:
			fragment = new TabPhonesFragment();
			break;
		default:
			break;
		}

		Bundle args = new Bundle();
		args.putLong("unitID", unitID);
		fragment.setArguments(args);
		
		return fragment;
	}

	@Override
	public int getCount() {
		return 3;
	}

}
