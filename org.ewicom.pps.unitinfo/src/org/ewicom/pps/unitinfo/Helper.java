package org.ewicom.pps.unitinfo;

import org.apache.commons.lang3.StringUtils;

public class Helper {

	private static final String TAG = "Helper";

	public static final int LANDLINE_PHONE_FORMAT = 1;
	public static final int CELL_PHONE_FORMAT = 2;
	public static final int FAX_LANDLINE_PHONE_FORMAT = 3;
	public static final int EXTENSION_PHONE_FORMAT = 4;
	
	public static final String LANDLINE_PHONE_PATTERN = "(%1$s) %2$s-%3$s-%4$s";
	public static final String EXTENSION_PHONE_PATTERN = "(%1$s) %2$s-%3$s-%4$s wewn. %5$s";
	public static final String CELL_PHONE_PATTERN = "%1$s-%2$s-%3$s";
	public static final String FAX_LANDLINE_PHONE_PATTERN = "fax (%1$s) %2$s-%3$s-%4$s";

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

	public String formatLandlinePhone(String phone) {
		return String.format(LANDLINE_PHONE_PATTERN, phone.substring(0, 2),
				phone.substring(2, 5), phone.substring(5, 7),
				phone.substring(7, 9));
	}

	public String formatCellPhone(String phone) {
		return String.format(CELL_PHONE_PATTERN, phone.substring(0, 3),
				phone.substring(3, 6), phone.substring(6, 9));
	}
	
	public String formatExtensionPhone(String phone){
		return String.format(EXTENSION_PHONE_PATTERN, phone.substring(0, 2),
				phone.substring(2, 5), phone.substring(5, 7),
				phone.substring(7, 9), phone.substring(9));
	}
	
	public String formatFaxLandlinePhone(String phone){
		return String.format(FAX_LANDLINE_PHONE_PATTERN, phone.substring(0, 2),
				phone.substring(2, 5), phone.substring(5, 7),
				phone.substring(7, 9));
	}

	public int checkPhoneType(String phone) {
		if (isLandlinePhone(phone)) {
			return LANDLINE_PHONE_FORMAT;
		} else {
			return CELL_PHONE_FORMAT;
		}
	}

	public boolean isLandlinePhone(String number) {
		String patternBracket = "^.*\\(\\s*\\d{2}\\s*\\).*";
		String patternNoBracket = "^\\D*\\d{2}\\s+.*";
		String patternTwoDigits = "^\\D*\\d{2}-.*";
		String patternBackslash = "^\\D*\\d{2}/.*";

		return number.matches(patternBracket)
				|| number.matches(patternNoBracket)
				|| number.matches(patternTwoDigits)
				|| number.matches(patternBackslash);
	}

	public boolean isExtensionNumber(String number) {
		boolean flag = false;
		String[] words = new String[] { "wewn.", "wewn", "wew.", "wew" };

		for (String w : words) {
			flag = number.contains(w);
		}

		return flag;
	}
	
	public boolean isFaxLandlineNumber(String number) {
		boolean flag = false;
		String[] words = new String[] { "fax", "fax.", "faks"};

		for (String w : words) {
			flag = number.contains(w);
		}

		return flag;
	}
	
	
}
