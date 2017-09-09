package accounts.plugin.model.patti;

import java.util.ArrayList;
import java.util.List;

import accounts.plugin.model.classes.AbstractModel;
import accounts.plugin.model.classes.Date;

public class PattiMember extends AbstractModel {
	private List<Date> dates;

	public PattiMember(String name) {
		setName(name);
	}

	public List<Date> getDates() {
		if (this.dates == null) {
			this.dates = new ArrayList<>();
		}
		return this.dates;
	}
}
