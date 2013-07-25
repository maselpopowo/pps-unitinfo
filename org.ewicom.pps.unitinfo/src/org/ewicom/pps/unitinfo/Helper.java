package org.ewicom.pps.unitinfo;

import org.apache.commons.lang3.StringUtils;

public class Helper {

	private static final String TAG = "Helper";

	public static final int LANDLINE_PHONE_FORMAT = 1;
	public static final int CELL_PHONE_FORMAT = 2;

	public Helper() {
	}

	public String[] splitPhones(String sPhones) {
		String[] separators = new String[] { "/", ",", ";" };
		String[] phones = new String[] { sPhones };
		boolean phoneLength = false;

		for (String sep : separators) {
			if (sPhones.contains(sep)) {
				String[] tempPhones = sPhones.split(sep);
				for (String t : tempPhones) {
					if (t.length() < 4) {
						phoneLength = false;
						break;
					}

					phoneLength = true;
				}

				if (phoneLength) {
					phones = tempPhones;
					break;
				}
			}
		}

		return phones;
	}

	public String cleanPhoneString(String text) {
		text = text.replaceFirst("\\+48", "");
		text = StringUtils.trimToEmpty(text);

		return text;
	}

	public int digitsCount(String text) {
		text = StringUtils.trimToEmpty(text);
		text = text.replaceAll("\\D+", "");
		return text.length();
	}

	public String formatLandlinePhone(String phone, String pattern) {
		return String.format(pattern,
				phone.substring(0, 2), phone.substring(2, 5),
				phone.substring(5, 7), phone.substring(7, 9));
	}
	
	public String formatCellPhone(String phone, String pattern) {
		return String.format(pattern,
				phone.substring(0, 3), phone.substring(3, 6),
				phone.substring(7, 9));
	}
	
	public int checkPhoneType(String phone){
		if(isLandlinePhone(phone)){
			return LANDLINE_PHONE_FORMAT;
		}else{
			return CELL_PHONE_FORMAT;
		}
	}

	public boolean isLandlinePhone(String number) {
		String patternBracket = "^.*\\(\\s*\\d{2}\\s*\\).*";
		String patternNoBracket = "^\\D*\\d{2}\\s+.*";

		return number.matches(patternBracket)
				| number.matches(patternNoBracket);
	}

	public boolean isExtensionNumber(String number) {
		boolean flag = false;
		String[] words = new String[] { "wewn.", "wewn", "wew.", "wew" };

		for (String w : words) {
			flag = number.contains(w);
		}

		return flag;
	}
}
