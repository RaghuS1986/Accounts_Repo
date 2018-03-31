package accounts.plugin.ui.editors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
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
import accounts.plugin.model.classes.Month;
import accounts.plugin.model.classes.Year;
import accounts.plugin.ui.Utility;
import accounts.plugin.ui.actions.AddDateAction;
import accounts.plugin.ui.actions.AddItemsAction;
import accounts.plugin.ui.actions.AddMemberAction;
import accounts.plugin.ui.actions.AddMonthAction;
import accounts.plugin.ui.actions.AddYearAction;
import accounts.plugin.ui.actions.RemoveDataAction;
import accounts.plugin.ui.actions.RemoveItemsBought;
import accounts.plugin.ui.actions.RemoveMonthAction;
import accounts.plugin.ui.actions.RemoveYearAction;
import accounts.plugin.ui.editingsupport.AmtRecEditingSupport;
import accounts.plugin.ui.editingsupport.AmtRecModeEditingSupport;
import accounts.plugin.ui.editingsupport.BillNoEditingSupport;
import accounts.plugin.ui.editingsupport.KGEditingSupport;
import accounts.plugin.ui.editingsupport.MiscellaneousEditingSupport;
import accounts.plugin.ui.editingsupport.NameEditingSupport;
import accounts.plugin.ui.editingsupport.NoOfPocketsEditingSupport;
import accounts.plugin.ui.editingsupport.PacksEditingSupport;
import accounts.plugin.ui.editingsupport.PreviousBalEditingSupport;
import accounts.plugin.ui.editingsupport.RatePerKGEditingSupport;
import accounts.plugin.ui.editingsupport.TotalInKgEditingSupport;
import accounts.plugin.ui.editingsupport.TransportAndMiscEditingSupport;
import accounts.plugin.ui.editingsupport.UnitPriceEditingSupport;
import accounts.plugin.ui.editingsupport.UnloadingChargesEditingSupport;

public class AccountsEditor extends EditorPart implements EditorInterface {
	private Action addMemberAction;
	private Action addYearAction;
	private Action addMonthAction;
	private Action addDateAction;
	private Action addItemsAction;
	private Action addSoldItemsAction;
	private Action removeMemberAction;
	private Action removeYearAction;
	private Action removeMonthAction;
	private Action removeDateAction;
	private Action removeItemsBoughtAction;
	private Action removeItemsSoldAction;
	private Action exportData;
	private Action exportSummary;
	private Action exportDaySummary;
	private TreeViewer treeViewer;
	private Action exportPersonSaleData;

	public void doSave(IProgressMonitor arg0) {
	}

	public void doSaveAs() {
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void init(IEditorSite arg0, IEditorInput arg1) throws PartInitException {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editors = activePage.getEditorReferences();
		for (IEditorReference iEditorReference : editors) {
			IEditorPart editor = iEditorReference.getEditor(true);
			if ((editor instanceof AccountsEditor)) {
				activePage.closeEditor(editor, true);
			}
		}
		setSite(arg0);
		setInput(arg1);
		IFile file = ((FileEditorInput) arg1).getFile();
		ModelManager.getInstance().loadModelFromSetFile(file.getLocation().toOSString());
	}

	public boolean isDirty() {
		return false;
	}

	public void createPartControl(final Composite parent) {
		Composite composite = new Composite(parent, 0);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(4, 4, true, false));

		this.treeViewer = new TreeViewer(composite, 65536);
		this.treeViewer.getTree().setHeaderVisible(true);
		this.treeViewer.getTree().setLinesVisible(true);
		this.treeViewer.getTree().setLayoutData(new GridData(1808));
		this.treeViewer.setContentProvider(new AccountsContentProvider());
		this.treeViewer.setLabelProvider(new AccountsTreeLabelProvider());

		TreeViewerColumn nameClmn = new TreeViewerColumn(this.treeViewer, 0);
		nameClmn.getColumn().setWidth(200);
		nameClmn.getColumn().setResizable(true);
		nameClmn.getColumn().setText("Name");
		nameClmn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String name = null;
				if ((element instanceof AbstractModel)) {
					name = ((AbstractModel) element).getName();
				}
				return name;
			}

			public Image getImage(Object element) {
				String iconPath = null;
				if ((element instanceof Accounts)) {
					iconPath = "/icons/account.gif";
				} else if ((element instanceof Member)) {
					iconPath = "/icons/person.gif";
				} else if ((element instanceof Year)) {
					iconPath = "/icons/year.png";
				} else if ((element instanceof Month)) {
					iconPath = "/icons/month.gif";
				} else if ((element instanceof accounts.plugin.model.classes.Date)) {
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
		vendorClmn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String vendorName = null;
				if ((element instanceof ItemBought)) {
					vendorName = ((ItemBought) element).getVendor();
				}
				return vendorName;
			}
		});
		vendorClmn.setEditingSupport(new NameEditingSupport(this.treeViewer));

		TreeViewerColumn quantityClmn = new TreeViewerColumn(this.treeViewer, 0);
		quantityClmn.getColumn().setWidth(110);
		quantityClmn.getColumn().setResizable(true);
		quantityClmn.getColumn().setText("No of Pockets");
		quantityClmn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String quantity = null;
				if ((element instanceof ItemBought)) {
					quantity = ((ItemBought) element).getNoOfPockets();
				}
				return quantity;
			}
		});
		quantityClmn.setEditingSupport(new NoOfPocketsEditingSupport(this.treeViewer));

		TreeViewerColumn percentageCommClmn = new TreeViewerColumn(this.treeViewer, 0);
		percentageCommClmn.getColumn().setWidth(100);
		percentageCommClmn.getColumn().setResizable(true);
		percentageCommClmn.getColumn().setText("Total in KGs");
		percentageCommClmn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String commission = null;
				if ((element instanceof ItemBought)) {
					commission = ((ItemBought) element).getTotalInKg();
				}
				return commission;
			}
		});
		percentageCommClmn.setEditingSupport(new TotalInKgEditingSupport(this.treeViewer));

		TreeViewerColumn priceClmn = new TreeViewerColumn(this.treeViewer, 0);
		priceClmn.getColumn().setWidth(100);
		priceClmn.getColumn().setResizable(true);
		priceClmn.getColumn().setText("Rate Per KG");
		priceClmn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String unitPrice = null;
				if ((element instanceof ItemBought)) {
					unitPrice = ((ItemBought) element).getRatePerKg();
				}
				return unitPrice;
			}
		});
		priceClmn.setEditingSupport(new RatePerKGEditingSupport(this.treeViewer));

		TreeViewerColumn miscClm = new TreeViewerColumn(this.treeViewer, 0);
		miscClm.getColumn().setWidth(120);
		miscClm.getColumn().setResizable(true);
		miscClm.getColumn().setText("Miscellaneous");
		miscClm.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String unitPrice = null;
				if ((element instanceof ItemBought)) {
					unitPrice = ((ItemBought) element).getMiscellaneous();
				}
				return unitPrice;
			}
		});
		miscClm.setEditingSupport(new MiscellaneousEditingSupport(this.treeViewer));

		TreeViewerColumn unloadingCharges = new TreeViewerColumn(this.treeViewer, 0);
		unloadingCharges.getColumn().setWidth(140);
		unloadingCharges.getColumn().setResizable(true);
		unloadingCharges.getColumn().setText("Unloading Charges");
		unloadingCharges.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String unloadingCharges = null;
				if ((element instanceof ItemBought)) {
					unloadingCharges = ((ItemBought) element).getUnloadingCharges();
				}
				return unloadingCharges;
			}
		});
		unloadingCharges.setEditingSupport(new UnloadingChargesEditingSupport(this.treeViewer));

		TreeViewerColumn totalCommissionClmn = new TreeViewerColumn(this.treeViewer, 0);
		totalCommissionClmn.getColumn().setWidth(150);
		totalCommissionClmn.getColumn().setResizable(true);
		totalCommissionClmn.getColumn().setText("Total Purchase cost");
		totalCommissionClmn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String cost = null;
				double totalCost = 0.0D;
				if ((element instanceof ItemBought)) {
					double totalKg = Utility.converToDouble(((ItemBought) element).getTotalInKg()).doubleValue();
					double unitPrice = Utility.converToDouble(((ItemBought) element).getRatePerKg()).doubleValue();
					double mis = Utility.converToDouble(((ItemBought) element).getMiscellaneous()).doubleValue();
					double unloading = Utility.converToDouble(((ItemBought) element).getUnloadingCharges())
							.doubleValue();
					totalCost = totalKg * unitPrice - (mis + unloading);
					cost = totalCost + "";
				}
				return cost;
			}
		});

		TreeViewerColumn billNoClm = new TreeViewerColumn(this.treeViewer, 0);
		billNoClm.getColumn().setWidth(60);
		billNoClm.getColumn().setResizable(false);
		billNoClm.getColumn().setText("Bill No");
		billNoClm.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String counter = null;
				if (element instanceof ItemBought) {
					Member member = null;
					for (Member mem : ModelManager.getInstance().getModel().getMembers()) {
						if (mem.getName().equals(((ItemBought) element).getMemberName())) {
							member = mem;
							break;
						}
					}
					List<ItemBought> list = ModelManager.getInstance().getMapOfItemsBt().get(member);
					counter = list.indexOf(element) + 1 + "";
				}
				return counter;
			}
		});

		TreeViewerColumn emptyClm = new TreeViewerColumn(this.treeViewer, 0);
		emptyClm.getColumn().setWidth(50);
		emptyClm.getColumn().setResizable(false);
		emptyClm.getColumn().setText("");
		emptyClm.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return "";
			}
		});

		TreeViewerColumn itemSoldToPersonClm = new TreeViewerColumn(this.treeViewer, 0);
		itemSoldToPersonClm.getColumn().setWidth(100);
		itemSoldToPersonClm.getColumn().setResizable(true);
		itemSoldToPersonClm.getColumn().setText("Person Name");
		itemSoldToPersonClm.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String name = null;
				if ((element instanceof ItemSold)) {
					name = ((ItemSold) element).getPersonName();
				}
				return name;
			}
		});

		TreeViewerColumn billNoClm2 = new TreeViewerColumn(this.treeViewer, 0);
		billNoClm2.getColumn().setWidth(70);
		billNoClm2.getColumn().setResizable(true);
		billNoClm2.getColumn().setText("Bill No");
		billNoClm2.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String billNo = null;
				if ((element instanceof ItemSold)) {
					billNo = ((ItemSold) element).getBillNo();
				}
				return billNo;
			}
		});
		billNoClm2.setEditingSupport(new BillNoEditingSupport(this.treeViewer));

		TreeViewerColumn numOfPacks = new TreeViewerColumn(this.treeViewer, 0);
		numOfPacks.getColumn().setWidth(120);
		numOfPacks.getColumn().setResizable(true);
		numOfPacks.getColumn().setText("No of Pockets");
		numOfPacks.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String noOfPacks = null;
				if ((element instanceof ItemSold)) {
					noOfPacks = ((ItemSold) element).getNumberOfPacks();
				}
				return noOfPacks;
			}
		});
		numOfPacks.setEditingSupport(new PacksEditingSupport(this.treeViewer));

		TreeViewerColumn totalInKG = new TreeViewerColumn(this.treeViewer, 0);
		totalInKG.getColumn().setWidth(80);
		totalInKG.getColumn().setResizable(true);
		totalInKG.getColumn().setText("No of KGs");
		totalInKG.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String totalKg = null;
				if ((element instanceof ItemSold)) {
					totalKg = ((ItemSold) element).getTotalKg();
				}
				return totalKg;
			}
		});
		totalInKG.setEditingSupport(new KGEditingSupport(this.treeViewer));

		TreeViewerColumn unitPriceClmn = new TreeViewerColumn(this.treeViewer, 0);
		unitPriceClmn.getColumn().setWidth(60);
		unitPriceClmn.getColumn().setResizable(true);
		unitPriceClmn.getColumn().setText("Rate");
		unitPriceClmn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String unitPrice = null;
				if ((element instanceof ItemSold)) {
					unitPrice = ((ItemSold) element).getUnitPrice();
				}
				return unitPrice;
			}
		});
		unitPriceClmn.setEditingSupport(new UnitPriceEditingSupport(this.treeViewer));

		TreeViewerColumn transportMisClmn = new TreeViewerColumn(this.treeViewer, 0);
		transportMisClmn.getColumn().setWidth(200);
		transportMisClmn.getColumn().setResizable(true);
		transportMisClmn.getColumn().setText("Transport & Miscellaneous");
		transportMisClmn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String tranAndMisc = null;
				if ((element instanceof ItemSold)) {
					tranAndMisc = ((ItemSold) element).getTranportAndMisc();
				}
				return tranAndMisc;
			}
		});
		transportMisClmn.setEditingSupport(new TransportAndMiscEditingSupport(this.treeViewer));

		TreeViewerColumn totalPriceClmn = new TreeViewerColumn(this.treeViewer, 0);
		totalPriceClmn.getColumn().setWidth(100);
		totalPriceClmn.getColumn().setResizable(true);
		totalPriceClmn.getColumn().setText("Total Price");
		totalPriceClmn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String totalPrice = null;
				if ((element instanceof ItemSold)) {
					totalPrice = ((ItemSold) element).getTotalPrice();
				}
				return totalPrice;
			}
		});
		TreeViewerColumn prevBalanceClm = new TreeViewerColumn(this.treeViewer, 0);
		prevBalanceClm.getColumn().setWidth(150);
		prevBalanceClm.getColumn().setResizable(true);
		prevBalanceClm.getColumn().setText("Previous Balance");
		prevBalanceClm.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String preBal = null;
				if ((element instanceof ItemSold)) {
					preBal = ((ItemSold) element).getPreviousBal();
				}
				return preBal;
			}
		});
		prevBalanceClm.setEditingSupport(new PreviousBalEditingSupport(this.treeViewer));

		TreeViewerColumn amtRecClmn = new TreeViewerColumn(this.treeViewer, 0);
		amtRecClmn.getColumn().setWidth(120);
		amtRecClmn.getColumn().setResizable(true);
		amtRecClmn.getColumn().setText("Amt Received");
		amtRecClmn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String amtRec = null;
				if ((element instanceof ItemSold)) {
					amtRec = ((ItemSold) element).getAmtReceived();
				}
				return amtRec;
			}
		});
		amtRecClmn.setEditingSupport(new AmtRecEditingSupport(this.treeViewer));

		TreeViewerColumn amtBalClmn = new TreeViewerColumn(this.treeViewer, 0);
		amtBalClmn.getColumn().setWidth(100);
		amtBalClmn.getColumn().setResizable(true);
		amtBalClmn.getColumn().setText("Amt Balance");
		amtBalClmn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String amtBal = null;
				if ((element instanceof ItemSold)) {
					amtBal = ((ItemSold) element).getAmtBalance();
				}
				return amtBal;
			}
		});
		TreeViewerColumn amtRecMode = new TreeViewerColumn(this.treeViewer, 0);
		amtRecMode.getColumn().setWidth(160);
		amtRecMode.getColumn().setResizable(true);
		amtRecMode.getColumn().setText("Amt Received Mode");
		amtRecMode.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String amtRec = null;
				if ((element instanceof ItemSold)) {
					amtRec = ((ItemSold) element).getAmtRecMode();
				}
				return amtRec;
			}
		});
		amtRecMode.setEditingSupport(new AmtRecModeEditingSupport(this.treeViewer));

		TreeViewerColumn receiptNo = new TreeViewerColumn(this.treeViewer, 0);
		receiptNo.getColumn().setWidth(100);
		receiptNo.getColumn().setResizable(true);
		receiptNo.getColumn().setText("Receipt No");
		receiptNo.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				String recNum = null;
				if ((element instanceof ItemSold)) {
					Member member = null;
					for (Member mem : ModelManager.getInstance().getModel().getMembers()) {
						if (mem.getName().equals(((ItemSold) element).getSoldUnderMember())) {
							member = mem;
							break;
						}
					}
					int index = ModelManager.getInstance().getMapOfItemsSold().get(member).indexOf(element);
					recNum = index + 1 + "";
				}
				return recNum;
			}
		});

		this.treeViewer.setInput(Arrays.asList(new Accounts[] { ModelManager.getInstance().getModel() }));

		MenuManager manager = new MenuManager();
		this.treeViewer.getControl().setMenu(manager.createContextMenu(this.treeViewer.getControl()));

		this.addMemberAction = new AddMemberAction(this.treeViewer, this);
		this.addMemberAction.setText("Add Member");
		manager.add(this.addMemberAction);

		this.addYearAction = new AddYearAction(this.treeViewer, this);
		this.addYearAction.setText("Add Year");
		manager.add(this.addYearAction);

		this.addMonthAction = new AddMonthAction(this.treeViewer, this);
		this.addMonthAction.setText("Add Month");
		manager.add(this.addMonthAction);

		this.addDateAction = new AddDateAction(this.treeViewer, this);
		this.addDateAction.setText("Add Date");
		manager.add(this.addDateAction);

		this.addItemsAction = new AddItemsAction(this.treeViewer, this);
		this.addItemsAction.setText("Add Bought Item");
		manager.add(this.addItemsAction);

		this.addSoldItemsAction = new Action() {
			public void run() {
				TreeSelection selection = (TreeSelection) AccountsEditor.this.treeViewer.getSelection();
				TreePath[] paths = selection.getPaths();
				Member member = null;
				for (TreePath treePath : paths) {
					if ((treePath.getSegment(1) instanceof Member)) {
						member = (Member) treePath.getSegment(1);
						break;
					}
				}
				Object firstElement = selection.getFirstElement();
				if ((firstElement instanceof ItemBought)) {
					InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(), "Add Sold Item",
							"Enter the Person name", "", null);
					if ((dialog.open() == 0) && (dialog.getValue().trim().length() > 0)) {
						ItemSold itmSold = new ItemSold(dialog.getValue());
						itmSold.setSoldUnderMember(member.getName());

						ItemSold previousItemSold = null;
						for (Member mem : ModelManager.getInstance().getModel().getMembers()) {
							if (mem.equals(member)) {
								for (Year yr : mem.getYears()) {
									for (Month mon : yr.getMonths()) {
										for (Date date : mon.getDates()) {
											for (ItemBought itemsBought : date.getItemsBought()) {
												for (ItemSold itemSold : itemsBought.getItemsSold()) {
													if (itemSold.getPersonName().equalsIgnoreCase(dialog.getValue())) {
														previousItemSold = itemSold;
													}
												}
											}
										}
									}
								}
							}
						}
						((ItemBought) firstElement).getItemsSold().add(itmSold);
						itmSold.setAmtBalance(previousItemSold != null ? previousItemSold.getAmtBalance() : "0");
					}
					AccountsEditor.this.setInputToView();
				}
				treeViewer.expandToLevel(paths[0], 1);
			}
		};

		this.addSoldItemsAction.setText("Add Sold Item");
		manager.add(this.addSoldItemsAction);

		this.removeMemberAction = new Action() {
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) AccountsEditor.this.treeViewer.getSelection();
				TreeSelection sel = (TreeSelection) AccountsEditor.this.treeViewer.getSelection();
				TreePath[] paths = sel.getPaths();
				Object firstElement = selection.getFirstElement();
				if ((firstElement instanceof Member)) {
					boolean delete = MessageDialog.openConfirm(parent.getShell(), "Delete Member",
							"Do you want to remove the member?");
					if (delete) {
						Accounts model = ModelManager.getInstance().getModel();
						Iterator<Member> iterator = model.getMembers().iterator();
						while (iterator.hasNext()) {
							Member mem = (Member) iterator.next();
							if (firstElement.equals(mem)) {
								iterator.remove();
								break;
							}
						}
					}
				}
				AccountsEditor.this.setInputToView();
				treeViewer.expandToLevel(paths, 1);
			}
		};
		this.removeMemberAction.setText("Remove Member");
		manager.add(this.removeMemberAction);

		this.removeYearAction = new RemoveYearAction(this.treeViewer, this, false);
		this.removeYearAction.setText("Remove Year");
		manager.add(this.removeYearAction);
		
		this.removeMonthAction = new RemoveMonthAction(this.treeViewer, this, false);
		this.removeMonthAction.setText("Remove Month");
		manager.add(this.removeMonthAction);

		this.removeDateAction = new RemoveDataAction(this.treeViewer, this, false);
		this.removeDateAction.setText("Remove Date");
		manager.add(this.removeDateAction);

		this.removeItemsBoughtAction = new RemoveItemsBought(this.treeViewer, this, false);
		this.removeItemsBoughtAction.setText("Remove Item Bought");
		manager.add(this.removeItemsBoughtAction);

		this.removeItemsSoldAction = new Action() {
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) AccountsEditor.this.treeViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				TreeSelection sel = (TreeSelection) AccountsEditor.this.treeViewer.getSelection();
				TreePath[] paths = sel.getPaths();
				if ((firstElement instanceof ItemSold)) {
					boolean delete = MessageDialog.openConfirm(parent.getShell(), "Delete Item Sold",
							"Do you want to remove the Item Sold?");
					if (delete) {
						Accounts model = ModelManager.getInstance().getModel();
						for (Member mem : model.getMembers()) {
							for (Year yr : mem.getYears()) {
								for (Month mon : yr.getMonths()) {
									for (Date date : mon.getDates()) {
										for (ItemBought itmsBought : date.getItemsBought()) {
											Iterator<ItemSold> iterator = itmsBought.getItemsSold().iterator();
											while (iterator.hasNext()) {
												ItemSold itemSold = (ItemSold) iterator.next();
												if (firstElement.equals(itemSold)) {
													iterator.remove();
													break;
												}
											}
										}
									}
								}
							}
						}
					}
					AccountsEditor.this.setInputToView();
					treeViewer.expandToLevel(paths[0], 1);
				}
			}
		};
		this.removeItemsSoldAction.setText("Remove Item Sold");
		manager.add(this.removeItemsSoldAction);

		this.exportSummary = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) AccountsEditor.this.treeViewer.getSelection();
				final Object firstElement = selection.getFirstElement();
				if ((firstElement instanceof Date)) {

					final Date date = (Date) firstElement;
					Dialog dialog = new Dialog(parent.getShell()) {
						protected Control createDialogArea(Composite parent) {
							Composite composite = new Composite(parent, 0);
							composite.setLayout(new GridLayout());
							composite.setLayoutData(new GridData(4, 4, true, false));

							Label label = new Label(composite, 0);
							label.setText("Summary Data file will be created under C:/temp with a file as Date .");
							return parent;
						}

						protected void okPressed() {

							TreeSelection sel = (TreeSelection) AccountsEditor.this.treeViewer.getSelection();
							TreePath[] paths = sel.getPaths();
							// Date dt = null;
							// for (TreePath treePath : paths) {
							// if ((treePath.getSegment(3) instanceof Date)) {
							// dt = (Date) treePath.getSegment(3);
							// break;
							// }
							// }
							Member member = null;
							for (TreePath treePath : paths) {
								if ((treePath.getSegment(1) instanceof Member)) {
									member = (Member) treePath.getSegment(1);
									break;
								}
							}

							new File("C://temp").mkdir();
							try {
								File file = new File(
										"C://temp//" + ((Date) firstElement).getName().replace("/", "_") + ".doc");
								file.createNewFile();

								BufferedWriter bw = null;
								FileWriter fw = null;
								try {
									List<String> strings = new ArrayList<>();
									List<String> individualDetails = new ArrayList<>();
									strings.add("Summary of " + member.getName() + " as on " + date.getName());
									strings.add("---------------------------------------------------------------");
									// ------------------------ Summary of Purchase
									double totalPurchaseCost = 0.0;
									double totalUnloadingCharges = 0.0;
									double totalMiscCharges = 0.0;
									for (ItemBought itmsBt : date.getItemsBought()) {
										double totalKg = Utility.converToDouble(itmsBt.getTotalInKg()).doubleValue();
										double unitPrice = Utility.converToDouble(itmsBt.getRatePerKg()).doubleValue();
										double mis = Utility.converToDouble(itmsBt.getMiscellaneous()).doubleValue();
										double unloading = Utility.converToDouble(itmsBt.getUnloadingCharges())
												.doubleValue();
										double totalCost = totalKg * unitPrice - (mis + unloading);
										totalPurchaseCost += totalCost;
										totalUnloadingCharges += unloading;
										totalMiscCharges += mis;
										individualDetails
												.add(itmsBt.getName() + " > " + itmsBt.getVendor() + " > " + totalCost);
									}
									strings.add("Total Purcase Cost is : " + totalPurchaseCost + "\n");
									for (String details : individualDetails) {
										strings.add("	" + details);
									}
									// strings.add(" EXPENSES:");
									// strings.add(" Total Unloading charges : " + totalUnloadingCharges);
									// strings.add(" Total miscellaneous charges : " + totalMiscCharges + "\n");

									// -------------------- Summary of Sale
									double totalSaleCost = 0.0;
									double totalMisCost = 0.0;
									double totalAmtRecInCASH = 0.0;
									double totalAmtRecInACC = 0.0;

									// find the Amt balance till yesterdays date------------
									Map<String, String> amtBalanceMap = new HashMap<>();
									if (member != null) {
										boolean temp = false;
										for (Year yr : member.getYears()) {
											for (Month month : yr.getMonths()) {
												if (temp) {
													break;
												}
												for (Date date : month.getDates()) {
													if (!date.equals(firstElement)) {
														for (ItemBought itmBt : date.getItemsBought()) {
															for (ItemSold itmSold : itmBt.getItemsSold()) {
																double val = Utility
																		.converToDouble(itmSold.getPreviousBal() != null
																				? itmSold.getPreviousBal()
																				: "0.0");
																double temp2 = 0.0;
																if (val > 0.0) {
																	temp2 = val - Utility
																			.converToDouble(itmSold.getAmtReceived());
																}
																amtBalanceMap.put(itmSold.getPersonName(),
																		val > 0.0 ? temp2 + ""
																				: itmSold.getAmtBalance());
															}
														}
													} else {
														temp = true;
														break;
													}
												}
											}
										}
									}
									// -------------------

									Map<String, List<String>> personNameToTotalPriceMap = new HashMap<>();
									Map<String, List<String>> personNameToAmtReceivedForTheDay = new HashMap<>();
									for (ItemBought itmsBt : date.getItemsBought()) {
										for (ItemSold itmSld : itmsBt.getItemsSold()) {
											// double kg = Utility.converToDouble(itmSld.getTotalKg()).doubleValue();
											// double pricePerKg =
											// Utility.converToDouble(itmSld.getUnitPrice()).doubleValue();
											totalSaleCost += Utility.converToDouble(itmSld.getTotalPrice())
													.doubleValue();
											double tranMis = Utility.converToDouble(itmSld.getTranportAndMisc())
													.doubleValue();
											totalMisCost += tranMis;
											List<String> val = personNameToTotalPriceMap.get(itmSld.getPersonName());
											if (val == null) {
												List<String> value = new ArrayList<>();
												value.add(itmSld.getTotalPrice());
												personNameToTotalPriceMap.put(itmSld.getPersonName(), value);
											} else {
												val.add(itmSld.getTotalPrice());
											}

											List<String> amtRec = personNameToAmtReceivedForTheDay
													.get(itmSld.getPersonName());
											if (amtRec == null) {
												List<String> value = new ArrayList<>();
												value.add(itmSld.getAmtReceived());
												personNameToAmtReceivedForTheDay.put(itmSld.getPersonName(), value);
											} else {
												amtRec.add(itmSld.getAmtReceived());
											}

											if (itmSld.getAmtRecMode().startsWith("CASH")) {
												totalAmtRecInCASH += Utility.converToDouble(itmSld.getAmtReceived())
														.doubleValue();
											} else if (itmSld.getAmtRecMode().startsWith("ACC")) {
												totalAmtRecInACC += Utility.converToDouble(itmSld.getAmtReceived())
														.doubleValue();
											}
										}
									}
									strings.add("\nTotal Sale Cost : " + totalSaleCost + "\n");
									strings.add("(Person_Name  Previous_Bal  Total_Price  Amt_Received  Aval_Bal)");
									strings.add("---------------------------------------------------------------");
									for (String key : personNameToTotalPriceMap.keySet()) {
										List<String> values = personNameToTotalPriceMap.get(key);
										double totalPricetemp = 0.0;
										for (String string : values) {
											totalPricetemp += Utility.converToDouble(string).doubleValue();
										}

										String amtbalanceFromPreviousDay = amtBalanceMap.get(key);
										if (amtbalanceFromPreviousDay == null) {
											for (Year yr : member.getYears()) {
												for (Month mnt : yr.getMonths()) {
													if (amtbalanceFromPreviousDay != null) {
														break;
													}
													for (Date date : mnt.getDates()) {
														if (amtbalanceFromPreviousDay != null) {
															break;
														}
														for (ItemBought itmBt : date.getItemsBought()) {
															if (amtbalanceFromPreviousDay != null) {
																break;
															}
															for (ItemSold itmSld : itmBt.getItemsSold()) {
																if (key.equals(itmSld.getPersonName())) {
																	amtbalanceFromPreviousDay = itmSld.getPreviousBal();
																	break;
																}
															}
														}
													}
												}
											}
										}

										List<String> amtReceived = personNameToAmtReceivedForTheDay.get(key);
										double amtRecTemp = 0.0;
										for (String string : amtReceived) {
											amtRecTemp += Utility.converToDouble(string).doubleValue();
										}

										double availableBalance = Utility.converToDouble(amtbalanceFromPreviousDay)
												+ totalPricetemp - amtRecTemp;
										amtBalanceMap.put(key, Utility.converToDouble(amtbalanceFromPreviousDay) + "");
										strings.add(key + " > "
												+ (amtbalanceFromPreviousDay != null ? amtbalanceFromPreviousDay : 0.0)
												+ " > " + totalPricetemp + " > " + amtRecTemp + " > "
												+ availableBalance);
									}

									strings.add("\nTotal Amount recived in CASH mode : " + totalAmtRecInCASH);
									strings.add("Total Amount recived in ACC mode : " + totalAmtRecInACC);
									strings.add("Total Amount Recived (CASH + ACC): "
											+ (totalAmtRecInACC + totalAmtRecInCASH));

									double totalPreviousBalance = 0.0;
									for (String value : amtBalanceMap.values()) {
										totalPreviousBalance += Utility.converToDouble(value).doubleValue();
									}

									strings.add(
											"\nTotal Balance Amount for " + member.getName() + " is : "
													+ (totalPreviousBalance
															+ (totalSaleCost - (totalAmtRecInACC + totalAmtRecInCASH)))
													+ "\n");

									strings.add("EXPENSES:");
									strings.add("	Total Unloading charges : " + totalUnloadingCharges);
									strings.add("	Total miscellaneous charges : " + totalMiscCharges);
									strings.add("	Total Transport and Miscellaneous : " + totalMisCost);

									fw = new FileWriter(file);
									bw = new BufferedWriter(fw);
									for (String string : strings) {
										bw.write(string);
										bw.newLine();
									}
								} catch (IOException e) {
									e.printStackTrace();
									try {
										if (bw != null) {
											bw.close();
										}
										if (fw == null) {
										}
										fw.close();
									} catch (IOException localIOException1) {
									}
								} finally {
									try {
										if (bw != null) {
											bw.close();
										}
										if (fw != null) {
											fw.close();
										}
									} catch (IOException localIOException2) {
									}
								}
								try {
									if (bw != null) {
										bw.close();
									}
									if (fw != null) {
										fw.close();
									}
								} catch (IOException localIOException3) {
								}
								super.okPressed();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						protected void configureShell(Shell newShell) {
							super.configureShell(newShell);
							newShell.setText("Export Summary");
						}
					};
					dialog.open();
				}
			}
		};
		this.exportSummary.setText("Export Summary");
		manager.add(this.exportSummary);

		this.exportDaySummary = new Action() {
			@Override
			public void run() {
				IStructuredSelection selection = (IStructuredSelection) AccountsEditor.this.treeViewer.getSelection();
				final Object firstElement = selection.getFirstElement();
				if ((firstElement instanceof Date)) {

					final Date date = (Date) firstElement;
					Dialog dialog = new Dialog(parent.getShell()) {
						protected Control createDialogArea(Composite parent) {
							Composite composite = new Composite(parent, 0);
							composite.setLayout(new GridLayout());
							composite.setLayoutData(new GridData(4, 4, true, false));

							Label label = new Label(composite, 0);
							label.setText("Day Summary Data file will be created under C:/temp with a file as Date .");
							return parent;
						}

						protected void okPressed() {

							TreeSelection sel = (TreeSelection) AccountsEditor.this.treeViewer.getSelection();
							TreePath[] paths = sel.getPaths();
							Member member = null;
							for (TreePath treePath : paths) {
								if ((treePath.getSegment(1) instanceof Member)) {
									member = (Member) treePath.getSegment(1);
									break;
								}
							}

							new File("C://temp").mkdir();
							try {
								File file = new File("C://temp//" + ((Date) firstElement).getName().replace("/", "_")
										+ "_day_summary.doc");
								file.createNewFile();

								BufferedWriter bw = null;
								FileWriter fw = null;
								try {
									List<String> strings = new ArrayList<>();
									Map<ItemBought, String> individualDetails = new LinkedHashMap<ItemBought, String>();
									strings.add(member.getName() + ":");
									strings.add("					PURCHASE					");
									strings.add(
											"-------------------------------------------------------------------------");
									strings.add("Date			Bill Num		Amount		Party Name");
									strings.add(
											"-------------------------------------------------------------------------");
									for (ItemBought itmsBt : date.getItemsBought()) {
										double totalKg = Utility.converToDouble(itmsBt.getTotalInKg()).doubleValue();
										double unitPrice = Utility.converToDouble(itmsBt.getRatePerKg()).doubleValue();
										double mis = Utility.converToDouble(itmsBt.getMiscellaneous()).doubleValue();
										double unloading = Utility.converToDouble(itmsBt.getUnloadingCharges())
												.doubleValue();
										double totalCost = totalKg * unitPrice - (mis + unloading);
										individualDetails.put(itmsBt, totalCost + "");
									}

									for (ItemBought itm : individualDetails.keySet()) {
										int indexOf = ModelManager.getInstance().getMapOfItemsBt().get(member)
												.indexOf(itm);
										String billNo = (indexOf + 1) + "";
										int temp = individualDetails.get(itm).toString().length();
										strings.add(
												date.getName() + "\t\t" + billNo + "\t\t\t" + individualDetails.get(itm)
														+ (temp <= 5 ? "\t\t\t" : "\t\t") + itm.getVendor());
									}
									// ----------------------------- END OF Purchase details

									// -------------------- Summary of Sale
									Map<String, List<String>> personNameToTotalPriceMap = new LinkedHashMap<>();
									Map<String, String> personNameToBillNoMap = new LinkedHashMap<>();
									Map<ItemSold, List<String>> personNameToAmtReceivedForTheDay = new LinkedHashMap<>();
									for (ItemBought itmsBt : date.getItemsBought()) {
										for (ItemSold itmSld : itmsBt.getItemsSold()) {
											List<String> val = personNameToTotalPriceMap.get(itmSld.getPersonName());
											if (val == null) {
												List<String> value = new ArrayList<>();
												value.add(itmSld.getTotalPrice());
												personNameToTotalPriceMap.put(itmSld.getPersonName(), value);
											} else {
												val.add(itmSld.getTotalPrice());
											}

											List<String> amtRecList = personNameToAmtReceivedForTheDay.get(itmSld);
											if (amtRecList == null && !itmSld.getAmtReceived().trim().isEmpty()) {
												List<String> value = new ArrayList<>();
												value.add(itmSld.getAmtReceived() + "/" + itmSld.getAmtRecMode());
												personNameToAmtReceivedForTheDay.put(itmSld, value);
											} else {
												if (!itmSld.getAmtReceived().trim().isEmpty()) {
													amtRecList.add(
															itmSld.getAmtReceived() + "/" + itmSld.getAmtRecMode());
												}
											}

											String billNo = personNameToBillNoMap.get(itmSld.getPersonName());
											if (billNo == null && itmSld.getBillNo() != null
													&& !itmSld.getBillNo().trim().isEmpty()) {
												personNameToBillNoMap.put(itmSld.getPersonName(), itmSld.getBillNo());
											}
										}
									}

									strings.add("\n\n\n					SALES					");
									strings.add(
											"-------------------------------------------------------------------------");
									strings.add("Date			Bill Num		Amount		Party Name");
									strings.add(
											"-------------------------------------------------------------------------");
									for (String key : personNameToTotalPriceMap.keySet()) {
										List<String> values = personNameToTotalPriceMap.get(key);
										double totalPricetemp = 0.0;
										for (String string : values) {
											totalPricetemp += Utility.converToDouble(string).doubleValue();
										}
										if (totalPricetemp > 0.0) {
											strings.add(date.getName() + "		"
													+ (personNameToBillNoMap.get(key) != null
															? personNameToBillNoMap.get(key)
															: "")
													+ "			 " + totalPricetemp
													+ (((totalPricetemp + "").toString()).length() <= 4 ? "			"
															: "\t\t")
													+ key);
										}
									}

									List<String> tempList = new ArrayList<String>();
									for (ItemSold key : personNameToAmtReceivedForTheDay.keySet()) {
										List<String> amtReceived = personNameToAmtReceivedForTheDay.get(key);
										double amtRecTempInCash = 0.0;
										double amtRecTempInAcc = 0.0;
										for (String string : amtReceived) {
											String[] val = string.split("/");
											if (val.length > 1 && val[1].startsWith("CASH")) {
												amtRecTempInCash += Utility.converToDouble(val[0]).doubleValue();
											} else if (val.length > 1 && val[1].startsWith("ACC")) {
												amtRecTempInAcc += Utility.converToDouble(val[0]).doubleValue();
											}
										}
										int receiptNum = ModelManager.getInstance().getMapOfItemsSold().get(member)
												.indexOf(key);
										receiptNum = receiptNum + 1;
										if (amtRecTempInCash > 0.0) {
											tempList.add(date.getName() + "	" + receiptNum + "		" + amtRecTempInCash
													+ (((amtRecTempInCash + "").toString()).length() <= 5 ? "\t\t\t"
															: "\t\t")
													+ "CASH\t\t" + key.getPersonName());
										}
										if (amtRecTempInAcc > 0.0) {
											tempList.add(date.getName() + "	" + receiptNum + "		" + amtRecTempInAcc
													+ (((amtRecTempInAcc + "").toString()).length() <= 5 ? "\t\t\t"
															: "\t\t")
													+ "ACC\t\t" + key.getPersonName());
										}
									}

									if (!tempList.isEmpty()) {
										strings.add("\n\n\n					RECEIPTS					");
										strings.add(
												"-------------------------------------------------------------------------");
										strings.add("Date		Receipt	Amount		Amount	Party");
										strings.add("		number	received		mode		name");
										strings.add(
												"-------------------------------------------------------------------------");
										for (String temp : tempList) {
											strings.add(temp);
										}
									}

									fw = new FileWriter(file);
									bw = new BufferedWriter(fw);
									for (String string : strings) {
										bw.write(string);
										bw.newLine();
									}
								} catch (IOException e) {
									e.printStackTrace();
									try {
										if (bw != null) {
											bw.close();
										}
										if (fw == null) {
										}
										fw.close();
									} catch (IOException localIOException1) {
									}
								} finally {
									try {
										if (bw != null) {
											bw.close();
										}
										if (fw != null) {
											fw.close();
										}
									} catch (IOException localIOException2) {
									}
								}
								try {
									if (bw != null) {
										bw.close();
									}
									if (fw != null) {
										fw.close();
									}
								} catch (IOException localIOException3) {
								}
								super.okPressed();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						protected void configureShell(Shell newShell) {
							super.configureShell(newShell);
							newShell.setText("Export Day Summary");
						}
					};
					dialog.open();
				}
			}
		};
		exportDaySummary.setText("Export Day Summary");
		manager.add(this.exportDaySummary);

		this.exportData = new Action() {

			public void run() {
				IStructuredSelection selection = (IStructuredSelection) AccountsEditor.this.treeViewer.getSelection();
				final Object firstElement = selection.getFirstElement();
				if ((firstElement instanceof ItemSold)) {
					final ItemSold itmSoldObj = (ItemSold) firstElement;
					Dialog dialog = new Dialog(parent.getShell()) {
						protected Control createDialogArea(Composite parent) {
							Composite composite = new Composite(parent, 0);
							composite.setLayout(new GridLayout());
							composite.setLayoutData(new GridData(4, 4, true, false));

							Label label = new Label(composite, 0);
							label.setText(
									"Export Data file will be created under C:/temp with a file name starts with Person Name and time stamp appended to it.");
							return parent;
						}

						protected void okPressed() {

							TreeSelection sel = (TreeSelection) AccountsEditor.this.treeViewer.getSelection();
							TreePath[] paths = sel.getPaths();
							Date dt = null;
							for (TreePath treePath : paths) {
								if ((treePath.getSegment(4) instanceof Date)) {
									dt = (Date) treePath.getSegment(4);
									break;
								}
							}

							Map<ItemBought, List<ItemSold>> itemSolds = new LinkedHashMap<ItemBought, List<ItemSold>>();
							final Object firstElement = selection.getFirstElement();
							if ((firstElement instanceof ItemSold)) {
								final ItemSold itemSold = (ItemSold) firstElement;
								if (dt != null) {
									for (ItemBought itmBt : dt.getItemsBought()) {
										// List<ItemSold> list= new ArrayList<ItemSold>();
										for (ItemSold itmSld : itmBt.getItemsSold()) {
											if (itemSold.getPersonName().equals(itmSld.getPersonName())) {
												List<ItemSold> tempList = itemSolds.get(itmBt);
												if (tempList == null) {
													tempList = new ArrayList<ItemSold>();
													tempList.add(itmSld);
													itemSolds.put(itmBt, tempList);
												} else {
													tempList.add(itmSld);
													itemSolds.put(itmBt, tempList);
												}
											}
										}
									}
								}
							}

							new File("C://temp").mkdir();
							String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
							try {
								File file = new File("C://temp//" + ((ItemSold) firstElement).getPersonName() + "_"
										+ timeStamp + ".doc");
								file.createNewFile();

								BufferedWriter bw = null;
								FileWriter fw = null;
								try {
									List<String> strings = new ArrayList<>();
									java.util.Date date = new java.util.Date();
									strings.add(date.toString());
									String personName = itmSoldObj.getPersonName();
									if (personName != null && personName.trim().length() > 0) {
										strings.add("Person Name\t\t\t\t- " + personName);
									}

									boolean billNoFound = false;
									for (Entry<ItemBought, List<ItemSold>> entry : itemSolds.entrySet()) {
										List<ItemSold> itemsSold = entry.getValue();
										if (billNoFound) {
											break;
										}
										for (ItemSold itemSold : itemsSold) {
											if (itemSold.getBillNo() != null
													&& itemSold.getBillNo().trim().length() > 0) {
												strings.add("Bill number\t\t\t\t- " + itemSold.getBillNo());
												billNoFound = true;
												break;
											}
										}
									}

									strings.add("---------------------------------------------------------------");
									for (Entry<ItemBought, List<ItemSold>> entry : itemSolds.entrySet()) {
										String itemName = entry.getKey().getName();
										List<ItemSold> itemsSold = entry.getValue();

										for (ItemSold itemSold : itemsSold) {

											if (itemName != null && itemName.trim().length() > 0) {
												strings.add("Item Name\t\t\t\t- " + itemName);
											}

											String numberOfPacks = itemSold.getNumberOfPacks();
											if (numberOfPacks != null && numberOfPacks.trim().length() > 0
													&& Utility.parseInt(numberOfPacks) > 0) {
												strings.add("Number of Pockets\t\t\t- " + numberOfPacks);
											}

											String totalKg = itemSold.getTotalKg();
											if (totalKg != null && totalKg.trim().length() > 0
													&& Utility.parseInt(totalKg) > 0) {
												strings.add("Number of KGs\t\t\t- " + totalKg);
											}

											String unitPrice = itemSold.getUnitPrice();
											if (unitPrice != null && unitPrice.trim().length() > 0
													&& Utility.parseInt(unitPrice) > 0) {
												strings.add("Rate\t\t\t\t\t- " + unitPrice);
											}

											String tranportAndMisc = itemSold.getTranportAndMisc();
											if (tranportAndMisc != null && tranportAndMisc.trim().length() > 0
													&& Utility.parseInt(tranportAndMisc) > 0) {
												strings.add("Transport & Miscellaneous\t- " + tranportAndMisc);
											}

											String totalPrice = itemSold.getTotalPrice();
											if (totalPrice != null && totalPrice.trim().length() > 0
													&& Utility.converToDouble(totalPrice) > 0) {
												strings.add("Total Price\t\t\t\t- " + totalPrice);
											}

											String previousBal = itemSold.getPreviousBal();
											if (previousBal != null && previousBal.trim().length() > 0
													&& Utility.parseInt(previousBal) > 0) {
												strings.add("Previous Balance\t\t\t- " + previousBal);
											}
											String amtReceived = itemSold.getAmtReceived();
											if (amtReceived != null && amtReceived.trim().length() > 0
													&& Utility.parseInt(amtReceived) > 0) {
												strings.add("Amount Received\t\t\t- " + amtReceived);
											}

											String amtBalance = itemSold.getAmtBalance();
											if (amtBalance != null && amtBalance.trim().length() > 0
													&& Utility.converToDouble(amtBalance) > 0) {
												strings.add("Amount balance\t\t\t- " + amtBalance);
											}

											String amtRecMode = itemSold.getAmtRecMode();
											if (amtRecMode != null && amtRecMode.trim().length() > 0) {
												strings.add("Amount Received mode\t\t- " + amtRecMode);
											}
											strings.add("\n");
										}
									}

									fw = new FileWriter(file);
									bw = new BufferedWriter(fw);
									for (String string : strings) {
										bw.write(string);
										bw.newLine();
									}
								} catch (IOException e) {
									e.printStackTrace();
									try {
										if (bw != null) {
											bw.close();
										}
										if (fw == null) {
										}
										fw.close();
									} catch (IOException localIOException1) {
									}
								} finally {
									try {
										if (bw != null) {
											bw.close();
										}
										if (fw != null) {
											fw.close();
										}
									} catch (IOException localIOException2) {
									}
								}
								try {
									if (bw != null) {
										bw.close();
									}
									if (fw != null) {
										fw.close();
									}
								} catch (IOException localIOException3) {
								}
								super.okPressed();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						protected void configureShell(Shell newShell) {
							super.configureShell(newShell);
							newShell.setText("Export Data");
						}
					};
					dialog.open();
				}
			}
		};
		this.exportData.setText("Export Data");
		manager.add(this.exportData);

		this.exportPersonSaleData = new Action() {

			public void run() {
				IStructuredSelection selection = (IStructuredSelection) AccountsEditor.this.treeViewer.getSelection();
				final Object firstElement = selection.getFirstElement();
				if ((firstElement instanceof Member)) {
					final Member member = (Member) firstElement;
					Dialog dialog = new Dialog(parent.getShell()) {
						Text personNameTxt = null;
						Text startDateTxt = null;
						Text endDateTxt = null;

						protected Control createDialogArea(Composite parent) {
							Composite area = (Composite) super.createDialogArea(parent);
							Composite container = new Composite(area, SWT.NONE);
							GridLayout gl_container = new GridLayout(1, true);
							gl_container.horizontalSpacing = 8;
							gl_container.marginWidth = 8;
							container.setLayout(gl_container);
							container.setLayoutData(new GridData(GridData.FILL_BOTH));

							Label label = new Label(container, SWT.NONE);
							label.setText(
									"Export Data file will be created under C:/temp with a file name \nstarts with Person Name and time stamp appended to it.");
							new Label(container, SWT.NONE);

							label = new Label(container, SWT.NONE);
							label.setText("Enter Person name:");
							personNameTxt = new Text(container, SWT.BORDER);
							personNameTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
							new Label(container, SWT.NONE);

							label = new Label(container, SWT.NONE);
							label.setText("Enter Start date:");
							startDateTxt = new Text(container, SWT.BORDER);
							startDateTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
							new Label(container, SWT.NONE);

							label = new Label(container, SWT.NONE);
							label.setText("Enter End date:");
							endDateTxt = new Text(container, SWT.BORDER);
							endDateTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

							return parent;
						}

						protected void okPressed() {
							boolean started = false;
							Map<ItemSold, Date> itmSoldToDateMap = new LinkedHashMap<>();
							Map<ItemSold, ItemBought> itmSoldToBtMap = new LinkedHashMap<>();
							for (Year yr : member.getYears()) {
								for (Month mnt : yr.getMonths()) {
									for (Date date : mnt.getDates()) {
										if (date.getName().equals(startDateTxt.getText()) || started) {
											started = true;
											for (ItemBought itmBt : date.getItemsBought()) {
												for (ItemSold itmSld : itmBt.getItemsSold()) {
													if (itmSld.getPersonName()
															.equalsIgnoreCase(personNameTxt.getText().trim())) {
														itmSoldToDateMap.put(itmSld, date);
														itmSoldToBtMap.put(itmSld, itmBt);
													}
												}
											}
										}
										if (date.getName().equals(endDateTxt.getText())) {
											started = false;
										}
									}
								}
							}

							new File("C://temp").mkdir();
							String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
							try {
								File file = new File("C://temp//" + personNameTxt.getText() + "_" + timeStamp + ".doc");
								file.createNewFile();

								BufferedWriter bw = null;
								FileWriter fw = null;
								try {
									List<String> strings = new ArrayList<>();
									strings.add("Person Name\t\t\t\t- " + personNameTxt.getText());
									strings.add("---------------------------------------------------------------");

									for (ItemSold itemSold : itmSoldToDateMap.keySet()) {

										ItemBought itemBought = itmSoldToBtMap.get(itemSold);
										Date dt = itmSoldToDateMap.get(itemSold);

										if (dt != null && dt.getName().trim().length() > 0) {
											strings.add("Date\t\t\t\t\t- " + dt.getName());
										}

										if (itemSold.getBillNo() != null && itemSold.getBillNo().trim().length() > 0) {
											strings.add("Bill Number\t\t\t\t- " + itemSold.getBillNo());
										} else {
											boolean updatd = false;
											for (ItemBought itmBt : dt.getItemsBought()) {
												if (updatd) {
													break;
												}
												for (ItemSold itmSld : itmBt.getItemsSold()) {
													if (itmSld.getPersonName()
															.equalsIgnoreCase(itemSold.getPersonName())
															&& itmSld.getBillNo() != null
															&& itmSld.getBillNo().trim().length() > 0) {
														strings.add("Bill Number\t\t\t\t- " + itmSld.getBillNo());
														updatd = true;
														break;
													}
												}
											}
										}

										if (itemBought != null && itemBought.getName().trim().length() > 0) {
											strings.add("Item Name\t\t\t\t- " + itemBought.getName());
										}

										String numberOfPacks = itemSold.getNumberOfPacks();
										if (numberOfPacks != null && numberOfPacks.trim().length() > 0
												&& Utility.parseInt(numberOfPacks) > 0) {
											strings.add("Number of Pockets\t\t\t- " + numberOfPacks);
										}

										String totalKg = itemSold.getTotalKg();
										if (totalKg != null && totalKg.trim().length() > 0
												&& Utility.parseInt(totalKg) > 0) {
											strings.add("Number of KGs\t\t\t- " + totalKg);
										}

										String unitPrice = itemSold.getUnitPrice();
										if (unitPrice != null && unitPrice.trim().length() > 0
												&& Utility.parseInt(unitPrice) > 0) {
											strings.add("Rate\t\t\t\t\t- " + unitPrice);
										}

										String tranportAndMisc = itemSold.getTranportAndMisc();
										if (tranportAndMisc != null && tranportAndMisc.trim().length() > 0
												&& Utility.parseInt(tranportAndMisc) > 0) {
											strings.add("Transport & Miscellaneous\t- " + tranportAndMisc);
										}

										String totalPrice = itemSold.getTotalPrice();
										if (totalPrice != null && totalPrice.trim().length() > 0
												&& Utility.converToDouble(totalPrice) > 0) {
											strings.add("Total Price\t\t\t\t- " + totalPrice);
										}

										String previousBal = itemSold.getPreviousBal();
										if (previousBal != null && previousBal.trim().length() > 0
												&& Utility.parseInt(previousBal) > 0) {
											strings.add("Previous Balance\t\t\t- " + previousBal);
										}
										String amtReceived = itemSold.getAmtReceived();
										if (amtReceived != null && amtReceived.trim().length() > 0
												&& Utility.parseInt(amtReceived) > 0) {
											strings.add("Amount Received\t\t\t- " + amtReceived);
										}

										String amtBalance = itemSold.getAmtBalance();
										if (amtBalance != null && amtBalance.trim().length() > 0
												&& Utility.converToDouble(amtBalance) > 0) {
											strings.add("Amount balance\t\t\t- " + amtBalance);
										}

										String amtRecMode = itemSold.getAmtRecMode();
										if (amtRecMode != null && amtRecMode.trim().length() > 0) {
											strings.add("Amount Received mode\t\t- " + amtRecMode);
										}
										strings.add("\n");
									}

									fw = new FileWriter(file);
									bw = new BufferedWriter(fw);
									for (String string : strings) {
										bw.write(string);
										bw.newLine();
									}
								} catch (IOException e) {
									e.printStackTrace();
									try {
										if (bw != null) {
											bw.close();
										}
										if (fw == null) {
										}
										fw.close();
									} catch (IOException localIOException1) {
									}
								} finally {
									try {
										if (bw != null) {
											bw.close();
										}
										if (fw != null) {
											fw.close();
										}
									} catch (IOException localIOException2) {
									}
								}
								try {
									if (bw != null) {
										bw.close();
									}
									if (fw != null) {
										fw.close();
									}
								} catch (IOException localIOException3) {
								}
								super.okPressed();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						protected void configureShell(Shell newShell) {
							super.configureShell(newShell);
							newShell.setText("Export Person Sale Data");
						}
					};
					dialog.open();
				}
			}
		};
		this.exportPersonSaleData.setText("Export Person Sale Data");
		manager.add(this.exportPersonSaleData);

		this.treeViewer.getTree().addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent arg0) {
				Object firstElement = AccountsEditor.this.treeViewer.getStructuredSelection().getFirstElement();
				if ((firstElement instanceof Accounts)) {
					AccountsEditor.this.addMemberAction.setEnabled(true);
					AccountsEditor.this.addYearAction.setEnabled(false);
					AccountsEditor.this.addDateAction.setEnabled(false);
					AccountsEditor.this.addMonthAction.setEnabled(false);
					AccountsEditor.this.addItemsAction.setEnabled(false);
					AccountsEditor.this.addSoldItemsAction.setEnabled(false);
					AccountsEditor.this.removeMemberAction.setEnabled(false);
					AccountsEditor.this.removeYearAction.setEnabled(false);
					AccountsEditor.this.removeMonthAction.setEnabled(false);
					AccountsEditor.this.removeDateAction.setEnabled(false);
					AccountsEditor.this.removeItemsBoughtAction.setEnabled(false);
					AccountsEditor.this.removeItemsSoldAction.setEnabled(false);
					AccountsEditor.this.exportData.setEnabled(false);
					AccountsEditor.this.exportSummary.setEnabled(false);
					AccountsEditor.this.exportDaySummary.setEnabled(false);
					AccountsEditor.this.exportPersonSaleData.setEnabled(false);
				} else if ((firstElement instanceof Member)) {
					AccountsEditor.this.addMemberAction.setEnabled(false);
					AccountsEditor.this.addYearAction.setEnabled(true);
					AccountsEditor.this.addMonthAction.setEnabled(false);
					AccountsEditor.this.addDateAction.setEnabled(false);
					AccountsEditor.this.addItemsAction.setEnabled(false);
					AccountsEditor.this.addSoldItemsAction.setEnabled(false);
					AccountsEditor.this.removeMemberAction.setEnabled(true);
					AccountsEditor.this.removeYearAction.setEnabled(false);
					AccountsEditor.this.removeMonthAction.setEnabled(false);
					AccountsEditor.this.removeDateAction.setEnabled(false);
					AccountsEditor.this.removeItemsBoughtAction.setEnabled(false);
					AccountsEditor.this.removeItemsSoldAction.setEnabled(false);
					AccountsEditor.this.exportData.setEnabled(false);
					AccountsEditor.this.exportSummary.setEnabled(false);
					AccountsEditor.this.exportDaySummary.setEnabled(false);
					AccountsEditor.this.exportPersonSaleData.setEnabled(true);
				} else if ((firstElement instanceof Year)) {
					AccountsEditor.this.addMemberAction.setEnabled(false);
					AccountsEditor.this.addYearAction.setEnabled(false);
					AccountsEditor.this.addMonthAction.setEnabled(true);
					AccountsEditor.this.addDateAction.setEnabled(false);
					AccountsEditor.this.addItemsAction.setEnabled(false);
					AccountsEditor.this.addSoldItemsAction.setEnabled(false);
					AccountsEditor.this.removeMemberAction.setEnabled(false);
					AccountsEditor.this.removeYearAction.setEnabled(true);
					AccountsEditor.this.removeMonthAction.setEnabled(false);
					AccountsEditor.this.removeDateAction.setEnabled(false);
					AccountsEditor.this.removeItemsBoughtAction.setEnabled(false);
					AccountsEditor.this.removeItemsSoldAction.setEnabled(false);
					AccountsEditor.this.exportData.setEnabled(false);
					AccountsEditor.this.exportSummary.setEnabled(false);
					AccountsEditor.this.exportDaySummary.setEnabled(false);
					AccountsEditor.this.exportPersonSaleData.setEnabled(false);
				} else if ((firstElement instanceof Month)) {
					AccountsEditor.this.addMemberAction.setEnabled(false);
					AccountsEditor.this.addYearAction.setEnabled(false);
					AccountsEditor.this.addMonthAction.setEnabled(false);
					AccountsEditor.this.addDateAction.setEnabled(true);
					AccountsEditor.this.addItemsAction.setEnabled(false);
					AccountsEditor.this.addSoldItemsAction.setEnabled(false);
					AccountsEditor.this.removeMemberAction.setEnabled(false);
					AccountsEditor.this.removeYearAction.setEnabled(false);
					AccountsEditor.this.removeMonthAction.setEnabled(true);
					AccountsEditor.this.removeDateAction.setEnabled(false);
					AccountsEditor.this.removeItemsBoughtAction.setEnabled(false);
					AccountsEditor.this.removeItemsSoldAction.setEnabled(false);
					AccountsEditor.this.exportData.setEnabled(false);
					AccountsEditor.this.exportSummary.setEnabled(false);
					AccountsEditor.this.exportDaySummary.setEnabled(false);
					AccountsEditor.this.exportPersonSaleData.setEnabled(false);
				} else if ((firstElement instanceof accounts.plugin.model.classes.Date)) {
					AccountsEditor.this.addMemberAction.setEnabled(false);
					AccountsEditor.this.addYearAction.setEnabled(false);
					AccountsEditor.this.addMonthAction.setEnabled(false);
					AccountsEditor.this.addDateAction.setEnabled(false);
					AccountsEditor.this.addItemsAction.setEnabled(true);
					AccountsEditor.this.addSoldItemsAction.setEnabled(false);
					AccountsEditor.this.removeMemberAction.setEnabled(false);
					AccountsEditor.this.removeYearAction.setEnabled(false);
					AccountsEditor.this.removeMonthAction.setEnabled(false);
					AccountsEditor.this.removeDateAction.setEnabled(true);
					AccountsEditor.this.removeItemsBoughtAction.setEnabled(false);
					AccountsEditor.this.removeItemsSoldAction.setEnabled(false);
					AccountsEditor.this.exportData.setEnabled(false);
					AccountsEditor.this.exportSummary.setEnabled(true);
					AccountsEditor.this.exportDaySummary.setEnabled(true);
					AccountsEditor.this.exportPersonSaleData.setEnabled(false);
				} else if ((firstElement instanceof ItemBought)) {
					AccountsEditor.this.addMemberAction.setEnabled(false);
					AccountsEditor.this.addYearAction.setEnabled(false);
					AccountsEditor.this.addMonthAction.setEnabled(false);
					AccountsEditor.this.addDateAction.setEnabled(false);
					AccountsEditor.this.addItemsAction.setEnabled(false);
					AccountsEditor.this.addSoldItemsAction.setEnabled(true);
					AccountsEditor.this.removeMemberAction.setEnabled(false);
					AccountsEditor.this.removeYearAction.setEnabled(false);
					AccountsEditor.this.removeMonthAction.setEnabled(false);
					AccountsEditor.this.removeDateAction.setEnabled(false);
					AccountsEditor.this.removeItemsBoughtAction.setEnabled(true);
					AccountsEditor.this.removeItemsSoldAction.setEnabled(false);
					AccountsEditor.this.exportData.setEnabled(false);
					AccountsEditor.this.exportSummary.setEnabled(false);
					AccountsEditor.this.exportDaySummary.setEnabled(false);
					AccountsEditor.this.exportPersonSaleData.setEnabled(false);
				} else if ((firstElement instanceof ItemSold)) {
					AccountsEditor.this.addMemberAction.setEnabled(false);
					AccountsEditor.this.addYearAction.setEnabled(false);
					AccountsEditor.this.addMonthAction.setEnabled(false);
					AccountsEditor.this.addDateAction.setEnabled(false);
					AccountsEditor.this.addItemsAction.setEnabled(false);
					AccountsEditor.this.addSoldItemsAction.setEnabled(false);
					AccountsEditor.this.removeMemberAction.setEnabled(false);
					AccountsEditor.this.removeYearAction.setEnabled(false);
					AccountsEditor.this.removeMonthAction.setEnabled(false);
					AccountsEditor.this.removeDateAction.setEnabled(false);
					AccountsEditor.this.removeItemsBoughtAction.setEnabled(false);
					AccountsEditor.this.removeItemsSoldAction.setEnabled(true);
					AccountsEditor.this.exportData.setEnabled(true);
					AccountsEditor.this.exportSummary.setEnabled(false);
					AccountsEditor.this.exportDaySummary.setEnabled(false);
					AccountsEditor.this.exportPersonSaleData.setEnabled(false);
				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

		});
	}

	public void setInputToView() {
		this.treeViewer.setInput(Arrays.asList(new Accounts[] { ModelManager.getInstance().getModel() }));
		ModelManager.getInstance()
				.saveModelToXml(((FileEditorInput) getEditorInput()).getFile().getLocation().toString());
	}

	public void dispose() {
		ModelManager.getInstance()
				.saveModelToXml(((FileEditorInput) getEditorInput()).getFile().getLocation().toString());
		super.dispose();
	}

	public void setFocus() {
	}
}
