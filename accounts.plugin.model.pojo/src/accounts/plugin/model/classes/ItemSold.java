package accounts.plugin.model.classes;

public class ItemSold {
	private String personName = "";
	private String numberOfPacks = "0";
	private String totalKg = "0";
	private String rate = "0";
	private String tranportAndMisc = "0";
	private String totalPrice = "0";
	private String previousBal = "0";
	private String amtReceived = "0";
	private String amtBalance = "0";
	private String amtRecMode = "";
	private String soldUnderMember = "";

	public ItemSold(String personName, String numberOfPacks, String totalKg, String rate, String transportAndMis,
			String totalPrice, String previousBalance, String amtReceived, String amtBalance, String amtRecMode,
			String soldUnderMember) {
		this.personName = personName;
		this.numberOfPacks = numberOfPacks;
		this.totalKg = totalKg;
		this.rate = rate;
		this.tranportAndMisc = transportAndMis;
		this.totalPrice = totalPrice;
		this.previousBal = previousBalance;
		this.amtReceived = amtReceived;
		this.amtBalance = amtBalance;
		this.amtRecMode = amtRecMode;
		this.soldUnderMember = soldUnderMember;
	}

	public String getTranportAndMisc() {
		return this.tranportAndMisc;
	}

	public void setTranportAndMisc(String tranportAndMisc) {
		this.tranportAndMisc = tranportAndMisc;
	}

	public String getSoldUnderMember() {
		return this.soldUnderMember;
	}

	public void setSoldUnderMember(String soldUnderMember) {
		this.soldUnderMember = soldUnderMember;
	}

	public String getPreviousBal() {
		return this.previousBal;
	}

	public void setPreviousBal(String previousBal) {
		this.previousBal = previousBal;
	}

	public String getAmtRecMode() {
		return this.amtRecMode;
	}

	public void setAmtRecMode(String amtRecMode) {
		this.amtRecMode = amtRecMode;
	}

	public String getTotalKg() {
		return this.totalKg;
	}

	public void setTotalKg(String totalKg) {
		this.totalKg = totalKg;
	}

	public ItemSold(String personName) {
		setPersonName(personName);
	}

	public String getPersonName() {
		return this.personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getNumberOfPacks() {
		return this.numberOfPacks;
	}

	public void setNumberOfPacks(String numberOfPacks) {
		this.numberOfPacks = numberOfPacks;
	}

	public String getUnitPrice() {
		return this.rate;
	}

	public void setUnitPrice(String unitPrice) {
		this.rate = unitPrice;
	}

	public String getTotalPrice() {
		return this.totalPrice;
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getAmtReceived() {
		return this.amtReceived;
	}

	public void setAmtReceived(String amtReceived) {
		this.amtReceived = amtReceived;
	}

	public String getAmtBalance() {
		return this.amtBalance;
	}

	public void setAmtBalance(String amtBalance) {
		this.amtBalance = amtBalance;
		ModelManager.getInstance().getBalanceModel().put(this.personName, amtBalance);
	}
}
