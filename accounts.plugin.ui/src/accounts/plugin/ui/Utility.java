package accounts.plugin.ui;

public class Utility {
	public static Double converToDouble(String val) {
		Double doubleVal = Double.valueOf(0.0D);
		try {
			if ((val != null) && (val.length() > 0)) {
				doubleVal = Double.valueOf(Double.parseDouble(val));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return doubleVal;
	}

	public static int parseInt(String val) {
		int i = 0;
		try {
			if ((val != null) && (val.length() > 0)) {
				i = Integer.parseInt(val);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return i;
	}
}
