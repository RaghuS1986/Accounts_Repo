package accounts.plugin.model.classes;

import java.util.ArrayList;
import java.util.List;

public class Accounts extends AbstractModel{
	
	public Accounts(String name) {
		setName(name);
	}
	
	private List<Member> members;

	public List<Member> getMembers() {
		if (members==null) {
			members= new ArrayList<>();
		}
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

}