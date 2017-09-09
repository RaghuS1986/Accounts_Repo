package accounts.plugin.model.classes;

import java.util.ArrayList;
import java.util.List;

public class Member extends AbstractModel {

	public Member(String name) {
		setName(name);
	}
	
	private List<Date> dates;

	public List<Date> getDates() {
		if (dates == null) {
			dates = new ArrayList<>();
		}
		return dates;
	}
}
