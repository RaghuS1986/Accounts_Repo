package accounts.plugin.model.classes;

import java.util.ArrayList;
import java.util.List;

public class ItemBought extends AbstractModel{
	  private String vendor = "";
	  private String noOfPockets = "0";
	  private String totalInKg = "0";
	  private String ratePerKg = "0";
	  private String miscellaneous = "0";
	  private String unloadingCharges = "0";
	  private boolean isUnloadingChargesChanged = false;
	  private List<ItemSold> itemsSold;
	  
	  public ItemBought(String name, String vendor, String noOfPockets, String totalInKg, String ratePerKg, String miss, String unloadingCharges)
	  {
	    setName(name);
	    setVendor(vendor);
	    setNoOfPockets(noOfPockets);
	    setTotalInKg(totalInKg);
	    setRatePerKg(ratePerKg);
	    setMiscellaneous(miss);
	    setUnloadingCharges(unloadingCharges);
	  }
	  
	  public ItemBought(String string)
	  {
	    setName(string);
	  }
	  
	  public String getVendor()
	  {
	    return this.vendor;
	  }
	  
	  public void setVendor(String vendor)
	  {
	    this.vendor = vendor;
	  }
	  
	  public String getNoOfPockets()
	  {
	    return this.noOfPockets;
	  }
	  
	  public void setNoOfPockets(String noOfPockets)
	  {
	    this.noOfPockets = noOfPockets;
	  }
	  
	  public String getTotalInKg()
	  {
	    return this.totalInKg;
	  }
	  
	  public void setTotalInKg(String totalInKg)
	  {
	    this.totalInKg = totalInKg;
	  }
	  
	  public String getRatePerKg()
	  {
	    return this.ratePerKg;
	  }
	  
	  public void setRatePerKg(String ratePerKg)
	  {
	    this.ratePerKg = ratePerKg;
	  }
	  
	  public String getMiscellaneous()
	  {
	    return this.miscellaneous;
	  }
	  
	  public void setMiscellaneous(String miscellaneous)
	  {
	    this.miscellaneous = miscellaneous;
	  }
	  
	  public String getUnloadingCharges()
	  {
	    return this.unloadingCharges;
	  }
	  
	  public void setUnloadingCharges(String unloadingCharges)
	  {
	    this.unloadingCharges = unloadingCharges;
	  }
	  
	  public void setItemsSold(List<ItemSold> itemsSold)
	  {
	    this.itemsSold = itemsSold;
	  }
	  
	  public boolean isUnloadingChargesChanged()
	  {
	    return this.isUnloadingChargesChanged;
	  }
	  
	  public void setUnloadingChargesChanged(boolean isUnloadingChargesChanged)
	  {
	    this.isUnloadingChargesChanged = isUnloadingChargesChanged;
	  }
	  
	  public List<ItemSold> getItemsSold()
	  {
	    if (this.itemsSold == null) {
	      this.itemsSold = new ArrayList<>();
	    }
	    return this.itemsSold;
	  }
	}