package accounts.plugin.model.patti;

import java.util.ArrayList;
import java.util.List;

import accounts.plugin.model.classes.AbstractModel;
import accounts.plugin.model.classes.Member;

public class Patti extends AbstractModel {
	private List<Member> members;

	public Patti(String name) {
		setName(name);
	}

	public List<Member> getMembers() {
		if (this.members == null) {
			this.members = new ArrayList<>();
		}
		return this.members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}
}
