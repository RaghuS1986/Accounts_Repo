package accounts.plugin.model.classes;

import java.util.ArrayList;
import java.util.List;

public class Date extends AbstractModel{
	
	public Date(String name) {
		setName(name);
	}
	
	private List<ItemBought> items;

	public List<ItemBought> getItemsBought() {
		if (items == null) {
			items = new ArrayList<>();
		}
		return items;
	}
}
