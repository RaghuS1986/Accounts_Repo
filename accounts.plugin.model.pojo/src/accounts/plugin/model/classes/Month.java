package accounts.plugin.model.classes;

import java.util.ArrayList;
import java.util.List;

public class Month extends AbstractModel{

	public Month(String name) {
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
