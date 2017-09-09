package accounts.plugin.ui.editors.patti;

import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import accounts.plugin.model.classes.AbstractModel;
import accounts.plugin.model.classes.Accounts;
import accounts.plugin.model.classes.Date;
import accounts.plugin.model.classes.ItemBought;
import accounts.plugin.model.classes.ItemSold;
import accounts.plugin.model.classes.Member;
import accounts.plugin.model.classes.ModelManager;
import accounts.plugin.model.patti.Patti;
import accounts.plugin.ui.Utility;
import accounts.plugin.ui.actions.AddDateAction;
import accounts.plugin.ui.actions.AddItemsAction;
import accounts.plugin.ui.actions.AddMemberAction;
import accounts.plugin.ui.actions.ExportPattiDataAction;
import accounts.plugin.ui.actions.RemoveDataAction;
import accounts.plugin.ui.actions.RemoveItemsBought;
import accounts.plugin.ui.editingsupport.MiscellaneousEditingSupport;
import accounts.plugin.ui.editingsupport.NameEditingSupport;
import accounts.plugin.ui.editingsupport.NoOfPocketsEditingSupport;
import accounts.plugin.ui.editingsupport.RatePerKGEditingSupport;
import accounts.plugin.ui.editingsupport.TotalInKgEditingSupport;
import accounts.plugin.ui.editingsupport.UnloadingChargesEditingSupport;
import accounts.plugin.ui.editors.EditorInterface;

public class PattiEditor extends EditorPart implements EditorInterface {
	private TreeViewer treeViewer;
	private Action addMemberAction;
	private Action addDateAction;
	private Action addItemsAction;
	private Action removeDateAction;
	private Action removeItemsBoughtAction;
	private Action exportData;

	public void doSave(IProgressMonitor arg0) {
	}

	public void doSaveAs() {
	}

	public void init(IEditorSite arg0, IEditorInput arg1) throws PartInitException {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editors = activePage.getEditorReferences();
		for (IEditorReference iEditorReference : editors) {
			IEditorPart editor = iEditorReference.getEditor(true);
			if ((editor instanceof PattiEditor)) {
				activePage.closeEditor(editor, true);
			}
		}
		setSite(arg0);
		setInput(arg1);
		IFile file = ((FileEditorInput) arg1).getFile();
		ModelManager.getInstance().loadPattiModelFromSetFile(file.getLocation().toOSString());
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

public void createPartControl(Composite arg0)
{
  Composite composite = new Composite(arg0, 0);
  composite.setLayout(new GridLayout());
  composite.setLayoutData(new GridData(4, 4, true, false));
  
  this.treeViewer = new TreeViewer(composite, 65536);
  this.treeViewer.getTree().setHeaderVisible(true);
  this.treeViewer.getTree().setLinesVisible(true);
  this.treeViewer.getTree().setLayoutData(new GridData(1808));
  this.treeViewer.setContentProvider(new PattiContentProvider());
  this.treeViewer.setLabelProvider(new PattiTreeLabelProvider());
  
  TreeViewerColumn nameClmn = new TreeViewerColumn(this.treeViewer, 0);
  nameClmn.getColumn().setWidth(200);
  nameClmn.getColumn().setResizable(true);
  nameClmn.getColumn().setText("Name");
  nameClmn.setLabelProvider(new ColumnLabelProvider()
  {
    public String getText(Object element)
    {
      String name = null;
      if ((element instanceof AbstractModel)) {
        name = ((AbstractModel)element).getName();
      }
      return name;
    }
    
    public Image getImage(Object element)
    {
      String iconPath = null;
      if (((element instanceof Accounts)) || ((element instanceof Patti))) {
        iconPath = "/icons/account.gif";
      } else if ((element instanceof Member)) {
        iconPath = "/icons/person.gif";
      } else if ((element instanceof Date)) {
        iconPath = "/icons/date.gif";
      } else if ((element instanceof ItemBought)) {
        iconPath = "/icons/in.gif";
      } else if ((element instanceof ItemSold)) {
        iconPath = "/icons/out.gif";
      }
      InputStream stream = getClass().getClassLoader().getResourceAsStream(iconPath);
      ImageData id = new ImageData(stream);
      Image image = new Image(null, id);
      return image;
    }
  });
  TreeViewerColumn vendorClmn = new TreeViewerColumn(this.treeViewer, 0);
  vendorClmn.getColumn().setWidth(110);
  vendorClmn.getColumn().setResizable(true);
  vendorClmn.getColumn().setText("Vendor Name");
  vendorClmn.setLabelProvider(new ColumnLabelProvider()
  {
    public String getText(Object element)
    {
      String vendorName = null;
      if ((element instanceof ItemBought)) {
        vendorName = ((ItemBought)element).getVendor();
      }
      return vendorName;
    }
  });
  vendorClmn.setEditingSupport(new NameEditingSupport(this.treeViewer));
  
  TreeViewerColumn quantityClmn = new TreeViewerColumn(this.treeViewer, 0);
  quantityClmn.getColumn().setWidth(110);
  quantityClmn.getColumn().setResizable(true);
  quantityClmn.getColumn().setText("No of Pockets");
  quantityClmn.setLabelProvider(new ColumnLabelProvider()
  {
    public String getText(Object element)
    {
      String quantity = null;
      if ((element instanceof ItemBought)) {
        quantity = ((ItemBought)element).getNoOfPockets();
      }
      return quantity;
    }
  });
  quantityClmn.setEditingSupport(new NoOfPocketsEditingSupport(this.treeViewer));
  
  TreeViewerColumn percentageCommClmn = new TreeViewerColumn(this.treeViewer, 0);
  percentageCommClmn.getColumn().setWidth(100);
  percentageCommClmn.getColumn().setResizable(true);
  percentageCommClmn.getColumn().setText("Total in KGs");
  percentageCommClmn.setLabelProvider(new ColumnLabelProvider()
  {
    public String getText(Object element)
    {
      String commission = null;
      if ((element instanceof ItemBought)) {
        commission = ((ItemBought)element).getTotalInKg();
      }
      return commission;
    }
  });
  percentageCommClmn.setEditingSupport(new TotalInKgEditingSupport(this.treeViewer));
  
  TreeViewerColumn priceClmn = new TreeViewerColumn(this.treeViewer, 0);
  priceClmn.getColumn().setWidth(100);
  priceClmn.getColumn().setResizable(true);
  priceClmn.getColumn().setText("Rate Per KG");
  priceClmn.setLabelProvider(new ColumnLabelProvider()
  {
    public String getText(Object element)
    {
      String unitPrice = null;
      if ((element instanceof ItemBought)) {
        unitPrice = ((ItemBought)element).getRatePerKg();
      }
      return unitPrice;
    }
  });
  priceClmn.setEditingSupport(new RatePerKGEditingSupport(this.treeViewer));
  

  TreeViewerColumn actualRateClmn = new TreeViewerColumn(this.treeViewer, 0);
  actualRateClmn.getColumn().setWidth(100);
  actualRateClmn.getColumn().setResizable(true);
  actualRateClmn.getColumn().setText("Actual Rate");
  actualRateClmn.setLabelProvider(new ColumnLabelProvider()
  {
    public String getText(Object element)
    {
      String cost = null;
      double totalCost = 0.0D;
      if ((element instanceof ItemBought))
      {
        double totalKg = Utility.converToDouble(((ItemBought)element).getTotalInKg()).doubleValue();
        double unitPrice = Utility.converToDouble(((ItemBought)element).getRatePerKg()).doubleValue();
        totalCost = totalKg * unitPrice;
        cost = totalCost+"";
      }
      return cost;
    }
  });
  TreeViewerColumn totalCommissionClmn = new TreeViewerColumn(this.treeViewer, 0);
  totalCommissionClmn.getColumn().setWidth(100);
  totalCommissionClmn.getColumn().setResizable(true);
  totalCommissionClmn.getColumn().setText("Commission");
  totalCommissionClmn.setLabelProvider(new ColumnLabelProvider()
  {
    public String getText(Object element)
    {
      String commission = null;
      double totalComm = 0.0D;
      if ((element instanceof ItemBought))
      {
        double totalKg = Utility.converToDouble(((ItemBought)element).getTotalInKg()).doubleValue();
        double unitPrice = Utility.converToDouble(((ItemBought)element).getRatePerKg()).doubleValue();
        totalComm = totalKg * unitPrice * 0.1D;
        commission = totalComm+"";
      }
      return commission;
    }
  });
  TreeViewerColumn miscClm = new TreeViewerColumn(this.treeViewer, 0);
  miscClm.getColumn().setWidth(120);
  miscClm.getColumn().setResizable(true);
  miscClm.getColumn().setText("Miscellaneous");
  miscClm.setLabelProvider(new ColumnLabelProvider()
  {
    public String getText(Object element)
    {
      String unitPrice = null;
      if ((element instanceof ItemBought)) {
        unitPrice = ((ItemBought)element).getMiscellaneous();
      }
      return unitPrice;
    }
  });
  miscClm.setEditingSupport(new MiscellaneousEditingSupport(this.treeViewer));
  
  TreeViewerColumn unloadingCharges = new TreeViewerColumn(this.treeViewer, 0);
  unloadingCharges.getColumn().setWidth(140);
  unloadingCharges.getColumn().setResizable(true);
  unloadingCharges.getColumn().setText("Unloading Charges");
  unloadingCharges.setLabelProvider(new ColumnLabelProvider()
  {
    public String getText(Object element)
    {
      String unloadingCharges = null;
      if ((element instanceof ItemBought)) {
        unloadingCharges = ((ItemBought)element).getUnloadingCharges();
      }
      return unloadingCharges;
    }
  });
  unloadingCharges.setEditingSupport(new UnloadingChargesEditingSupport(this.treeViewer));
  
  TreeViewerColumn finalTotal = new TreeViewerColumn(this.treeViewer, 0);
  finalTotal.getColumn().setWidth(100);
  finalTotal.getColumn().setResizable(true);
  finalTotal.getColumn().setText("Final Total");
  finalTotal.setLabelProvider(new ColumnLabelProvider()
  {
    public String getText(Object element)
    {
      String finalTl = null;
      double finalTotal = 0.0D;
      if ((element instanceof ItemBought))
      {
        double totalKg = Utility.converToDouble(((ItemBought)element).getTotalInKg()).doubleValue();
        double unitPrice = Utility.converToDouble(((ItemBought)element).getRatePerKg()).doubleValue();
        double totalRate = totalKg * unitPrice;
        double commission = totalKg * unitPrice * 0.1D;
        double miss = Utility.converToDouble(((ItemBought)element).getMiscellaneous()).doubleValue();
        double unloadingCharges = Utility.converToDouble(((ItemBought)element).getUnloadingCharges()).doubleValue();
        finalTotal = totalRate - (commission + miss + unloadingCharges);
        finalTl = finalTotal+"";
      }
      return finalTl;
    }
  });
  this.treeViewer.setInput(Arrays.asList(new Patti[] { ModelManager.getInstance().getPattiModel() }));
  
  MenuManager manager = new MenuManager();
  this.treeViewer.getControl().setMenu(manager.createContextMenu(this.treeViewer.getControl()));
  
  this.addMemberAction = new AddMemberAction(this.treeViewer, this);
  this.addMemberAction.setText("Add Member");
  manager.add(this.addMemberAction);
  
  this.addDateAction = new 	AddDateAction(this.treeViewer, this);
  this.addDateAction.setText("Add Date");
  manager.add(this.addDateAction);
  
  this.addItemsAction = new AddItemsAction(this.treeViewer, this);
  this.addItemsAction.setText("Add Bought Item");
  manager.add(this.addItemsAction);
  
  this.removeDateAction = new RemoveDataAction(this.treeViewer, this, true);
  this.removeDateAction.setText("Remove Date");
  manager.add(this.removeDateAction);
  
  this.removeItemsBoughtAction = new RemoveItemsBought(this.treeViewer, this, true);
  this.removeItemsBoughtAction.setText("Remove Item Bought");
  manager.add(this.removeItemsBoughtAction);
  
  this.exportData = new ExportPattiDataAction(this.treeViewer);
  this.exportData.setText("Export Data");
  manager.add(this.exportData);
  
  this.treeViewer.getTree().addSelectionListener(new SelectionListener()
  {
    public void widgetSelected(SelectionEvent arg0)
    {
      Object firstElement = PattiEditor.this.treeViewer.getStructuredSelection().getFirstElement();
      if ((firstElement instanceof Patti))
      {
        PattiEditor.this.addMemberAction.setEnabled(true);
        PattiEditor.this.addDateAction.setEnabled(false);
        PattiEditor.this.addItemsAction.setEnabled(false);
        
        PattiEditor.this.removeDateAction.setEnabled(false);
        PattiEditor.this.removeItemsBoughtAction.setEnabled(false);
        PattiEditor.this.exportData.setEnabled(false);
      }
      else if ((firstElement instanceof Member))
      {
        PattiEditor.this.addMemberAction.setEnabled(false);
        PattiEditor.this.addDateAction.setEnabled(true);
        PattiEditor.this.addItemsAction.setEnabled(false);
        
        PattiEditor.this.removeDateAction.setEnabled(false);
        PattiEditor.this.removeItemsBoughtAction.setEnabled(false);
        PattiEditor.this.exportData.setEnabled(false);
      }
      else if ((firstElement instanceof Date))
      {
        PattiEditor.this.addMemberAction.setEnabled(false);
        PattiEditor.this.addDateAction.setEnabled(false);
        PattiEditor.this.addItemsAction.setEnabled(true);
        
        PattiEditor.this.removeDateAction.setEnabled(true);
        PattiEditor.this.removeItemsBoughtAction.setEnabled(false);
        PattiEditor.this.exportData.setEnabled(false);
      }
      else if ((firstElement instanceof ItemBought))
      {
        PattiEditor.this.addMemberAction.setEnabled(false);
        PattiEditor.this.addDateAction.setEnabled(false);
        PattiEditor.this.addItemsAction.setEnabled(false);
        
        PattiEditor.this.removeDateAction.setEnabled(false);
        PattiEditor.this.removeItemsBoughtAction.setEnabled(true);
        PattiEditor.this.exportData.setEnabled(true);
      }
    }
    
    public void widgetDefaultSelected(SelectionEvent arg0) {}
  });
}

	public void setFocus() {
	}

	public void setInputToView() {
		this.treeViewer.setInput(Arrays.asList(new Patti[] { ModelManager.getInstance().getPattiModel() }));
		this.treeViewer.expandAll();
		ModelManager.getInstance()
				.savePattiModelToXml(((FileEditorInput) getEditorInput()).getFile().getLocation().toString());
	}

	public void dispose() {
		ModelManager.getInstance()
				.savePattiModelToXml(((FileEditorInput) getEditorInput()).getFile().getLocation().toString());
		super.dispose();
	}
}