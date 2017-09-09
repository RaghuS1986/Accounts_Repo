package accounts.plugin.ui.editors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import accounts.plugin.ui.Utility;
import accounts.plugin.ui.actions.AddDateAction;
import accounts.plugin.ui.actions.AddItemsAction;
import accounts.plugin.ui.actions.AddMemberAction;
import accounts.plugin.ui.actions.RemoveDataAction;
import accounts.plugin.ui.actions.RemoveItemsBought;
import accounts.plugin.ui.editingsupport.AmtRecEditingSupport;
import accounts.plugin.ui.editingsupport.AmtRecModeEditingSupport;
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
	private Action addDateAction;
	private Action addItemsAction;
	private Action addSoldItemsAction;
	private Action removeMemberAction;
	private Action removeDateAction;
	private Action removeItemsBoughtAction;
	private Action removeItemsSoldAction;
	private Action exportData;
	private TreeViewer treeViewer;

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

		this.treeViewer.setInput(Arrays.asList(new Accounts[] { ModelManager.getInstance().getModel() }));

		MenuManager manager = new MenuManager();
		this.treeViewer.getControl().setMenu(manager.createContextMenu(this.treeViewer.getControl()));

		this.addMemberAction = new AddMemberAction(this.treeViewer, this);
		this.addMemberAction.setText("Add Member");
		manager.add(this.addMemberAction);

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
								for (Date date : mem.getDates()) {
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
						((ItemBought) firstElement).getItemsSold().add(itmSold);
						itmSold.setAmtBalance(
								previousItemSold != null ? previousItemSold.getAmtBalance() : "0");
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
							for (Date date : mem.getDates()) {
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
					AccountsEditor.this.setInputToView();
					treeViewer.expandToLevel(paths[0], 1);
				}
			}
		};
		this.removeItemsSoldAction.setText("Remove Item Sold");
		manager.add(this.removeItemsSoldAction);

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
								if ((treePath.getSegment(2) instanceof Date)) {
									dt = (Date) treePath.getSegment(2);
									break;
								}
							}
							
							
							Map<ItemBought, List<ItemSold>> itemSolds= new LinkedHashMap<ItemBought, List<ItemSold>>();
							final Object firstElement = selection.getFirstElement();
							if ((firstElement instanceof ItemSold)) {
								final ItemSold itemSold = (ItemSold) firstElement;
								if (dt!=null) {
									for (ItemBought itmBt : dt.getItemsBought()) {
//										List<ItemSold> list= new ArrayList<ItemSold>();
										for (ItemSold itmSld : itmBt.getItemsSold()) {
											if (itemSold.getPersonName().equals(itmSld.getPersonName())) {
												List<ItemSold> tempList = itemSolds.get(itmBt);
												if (tempList==null) {
													tempList= new ArrayList<ItemSold>();
													tempList.add(itmSld);
													itemSolds.put(itmBt,tempList);
												}else {
													tempList.add(itmSld);
													itemSolds.put(itmBt,tempList);
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
									strings.add("---------------------------------------------------------------");
									for (Entry<ItemBought, List<ItemSold>> entry : itemSolds.entrySet()) {
										String itemName =entry.getKey().getName();
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

		this.treeViewer.getTree().addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				Object firstElement = AccountsEditor.this.treeViewer.getStructuredSelection().getFirstElement();
				if ((firstElement instanceof Accounts)) {
					AccountsEditor.this.addMemberAction.setEnabled(true);
					AccountsEditor.this.addDateAction.setEnabled(false);
					AccountsEditor.this.addItemsAction.setEnabled(false);
					AccountsEditor.this.addSoldItemsAction.setEnabled(false);
					AccountsEditor.this.removeMemberAction.setEnabled(false);
					AccountsEditor.this.removeDateAction.setEnabled(false);
					AccountsEditor.this.removeItemsBoughtAction.setEnabled(false);
					AccountsEditor.this.removeItemsSoldAction.setEnabled(false);
					AccountsEditor.this.exportData.setEnabled(false);
				} else if ((firstElement instanceof Member)) {
					AccountsEditor.this.addMemberAction.setEnabled(false);
					AccountsEditor.this.addDateAction.setEnabled(true);
					AccountsEditor.this.addItemsAction.setEnabled(false);
					AccountsEditor.this.addSoldItemsAction.setEnabled(false);
					AccountsEditor.this.removeMemberAction.setEnabled(true);
					AccountsEditor.this.removeDateAction.setEnabled(false);
					AccountsEditor.this.removeItemsBoughtAction.setEnabled(false);
					AccountsEditor.this.removeItemsSoldAction.setEnabled(false);
					AccountsEditor.this.exportData.setEnabled(false);
				} else if ((firstElement instanceof accounts.plugin.model.classes.Date)) {
					AccountsEditor.this.addMemberAction.setEnabled(false);
					AccountsEditor.this.addDateAction.setEnabled(false);
					AccountsEditor.this.addItemsAction.setEnabled(true);
					AccountsEditor.this.addSoldItemsAction.setEnabled(false);
					AccountsEditor.this.removeMemberAction.setEnabled(false);
					AccountsEditor.this.removeDateAction.setEnabled(true);
					AccountsEditor.this.removeItemsBoughtAction.setEnabled(false);
					AccountsEditor.this.removeItemsSoldAction.setEnabled(false);
					AccountsEditor.this.exportData.setEnabled(false);
				} else if ((firstElement instanceof ItemBought)) {
					AccountsEditor.this.addMemberAction.setEnabled(false);
					AccountsEditor.this.addDateAction.setEnabled(false);
					AccountsEditor.this.addItemsAction.setEnabled(false);
					AccountsEditor.this.addSoldItemsAction.setEnabled(true);
					AccountsEditor.this.removeMemberAction.setEnabled(false);
					AccountsEditor.this.removeDateAction.setEnabled(false);
					AccountsEditor.this.removeItemsBoughtAction.setEnabled(true);
					AccountsEditor.this.removeItemsSoldAction.setEnabled(false);
					AccountsEditor.this.exportData.setEnabled(false);
				} else if ((firstElement instanceof ItemSold)) {
					AccountsEditor.this.addMemberAction.setEnabled(false);
					AccountsEditor.this.addDateAction.setEnabled(false);
					AccountsEditor.this.addItemsAction.setEnabled(false);
					AccountsEditor.this.addSoldItemsAction.setEnabled(false);
					AccountsEditor.this.removeMemberAction.setEnabled(false);
					AccountsEditor.this.removeDateAction.setEnabled(false);
					AccountsEditor.this.removeItemsBoughtAction.setEnabled(false);
					AccountsEditor.this.removeItemsSoldAction.setEnabled(true);
					AccountsEditor.this.exportData.setEnabled(true);
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
