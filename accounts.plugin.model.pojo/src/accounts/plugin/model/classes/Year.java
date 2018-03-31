package accounts.plugin.model.classes;

import java.util.ArrayList;
import java.util.List;

public class Year extends AbstractModel{

	public Year(String name) {
		setName(name);
	}
	private List<Month> months;

	public List<Month> getMonths() {
		if (months == null) {
			months = new ArrayList<>();
		}
		return months;
	}
}
