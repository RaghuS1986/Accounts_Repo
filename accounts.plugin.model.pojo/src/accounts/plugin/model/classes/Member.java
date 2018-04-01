package accounts.plugin.model.classes;

import java.util.ArrayList;
import java.util.List;

public class Member extends AbstractModel {

	public Member(String name) {
		setName(name);
	}
	
//	private List<Month> months;
//
//	public List<Month> getMonths() {
//		if (months == null) {
//			months = new ArrayList<>();
//		}
//		return months;
//	}
	
	private List<Year> years;

	public List<Year> getYears() {
		if (years == null) {
			years = new ArrayList<>();
		}
		return years;
	}
	
}
