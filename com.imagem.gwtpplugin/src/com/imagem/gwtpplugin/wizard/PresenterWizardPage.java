/**
 * Copyright 2011 Les Systèmes Médicaux Imagem Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imagem.gwtpplugin.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

@Deprecated
@SuppressWarnings("restriction")
public class PresenterWizardPage extends WizardPage {

	private IStructuredSelection selection;
	private Text presenterPackage;
	private Text presenterName;
	private Button isPlace;
	private Text tokenName;
	private Button isCodeSplit;
	private Button isWidget;
	private Button useUiBinder;
	private Text gatekeeper;
	private Button btnBrowseGatekeeper;
	private IJavaProject project;
	private Button onBind;
	private Button onHide;
	private Button onReset;
	private Button onReveal;
	private Button onUnbind;
	private Button btnPresenterPackage;

	public PresenterWizardPage(IStructuredSelection selection) {
		super("PresenterWizardPage");
		setTitle("Create a Presenter");
		setDescription("Create a Presener, it's View and it's reference in GIN");
		this.selection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		int nColumns = 3;
		
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = nColumns;
		layout.verticalSpacing = 5;
		
		// Test
		/*PackageField test = new PackageField(container, nColumns);
		test.setLabelText("Test:");
		test.setPackageFragmentRoot(project.getPackageFragmentRoot("src/"));*/

		// Presenter Package
		Label label = new Label(container, SWT.NULL);
		label.setText("Presenter package:");

		presenterPackage = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		presenterPackage.setLayoutData(gd);
		presenterPackage.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		btnPresenterPackage = new Button(container, SWT.PUSH);
		btnPresenterPackage.setText("Browse...");
		btnPresenterPackage.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IPackageFragmentRoot root = chooseContainer();
				if(root != null)
					presenterPackage.setText(root.getPath().makeRelative().toString());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				IPackageFragmentRoot root = chooseContainer();
				if(root != null)
					presenterPackage.setText(root.getPath().makeRelative().toString());
			}
		});
		
		//root.getPath().makeRelative().toString();

		// Presenter Name
		label = new Label(container, SWT.NULL);
		label.setText("Presenter name:");

		presenterName = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		presenterName.setLayoutData(gd);
		presenterName.setFocus();
		presenterName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);

		// Widget
		label = new Label(container, SWT.NULL);

		isWidget = new Button(container, SWT.CHECK);
		isWidget.setText("Is a widget?");
		isWidget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isPlace.setEnabled(!isWidget.getSelection());
				isCodeSplit.setEnabled(!isWidget.getSelection());
				if(isWidget.getSelection()) {
					tokenName.setEnabled(!isWidget.getSelection());
					gatekeeper.setEnabled(!isWidget.getSelection());
					btnBrowseGatekeeper.setEnabled(!isWidget.getSelection());
				}
				else {
					tokenName.setEnabled(isPlace.getSelection());
					gatekeeper.setEnabled(isPlace.getSelection());
					btnBrowseGatekeeper.setEnabled(isPlace.getSelection());
				}
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);

		// Place
		label = new Label(container, SWT.NULL);

		isPlace = new Button(container, SWT.CHECK);
		isPlace.setText("Is a place?");
		isPlace.setSelection(true);
		isPlace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tokenName.setEnabled(isPlace.getSelection());
				gatekeeper.setEnabled(isPlace.getSelection());
				btnBrowseGatekeeper.setEnabled(isPlace.getSelection());
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);

		// Token
		label = new Label(container, SWT.NULL);
		label.setText("Token name:");

		tokenName = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		tokenName.setLayoutData(gd);
		tokenName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);

		// GateKeeper
		label = new Label(container, SWT.NULL);
		label.setText("Gatekeeper:");

		gatekeeper = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gatekeeper.setLayoutData(gd);
		gatekeeper.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		btnBrowseGatekeeper = new Button(container, SWT.PUSH);
		btnBrowseGatekeeper.setText("Browse...");
		btnBrowseGatekeeper.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gatekeeper.setText(getNameWithTypeParameters(chooseGateKeeper()));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				gatekeeper.setText(getNameWithTypeParameters(chooseGateKeeper()));
			}
		});

		// CodeSplit
		label = new Label(container, SWT.NULL);

		isCodeSplit = new Button(container, SWT.CHECK);
		isCodeSplit.setText("Use CodeSplit?");
		isCodeSplit.setSelection(true);

		label = new Label(container, SWT.NULL);

		// UiBinder
		label = new Label(container, SWT.NULL);

		useUiBinder = new Button(container, SWT.CHECK);
		useUiBinder.setText("Use UiBinder?");
		useUiBinder.setSelection(true);

		label = new Label(container, SWT.NULL);
		
		// Methods
		label = new Label(container, SWT.BEGINNING);
		label.setText("Methods:");
		label.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		Composite methods = new Composite(container, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 5;
		methods.setLayout(layout);

		onBind = new Button(methods, SWT.CHECK);
		onBind.setText("onBind()");
		onBind.setSelection(false);

		onHide = new Button(methods, SWT.CHECK);
		onHide.setText("onHide()");
		onHide.setSelection(false);

		onReset = new Button(methods, SWT.CHECK);
		onReset.setText("onReset()");
		onReset.setSelection(false);

		onReveal = new Button(methods, SWT.CHECK);
		onReveal.setText("onReveal()");
		onReveal.setSelection(false);

		onUnbind = new Button(methods, SWT.CHECK);
		onUnbind.setText("onUnbind()");
		onUnbind.setSelection(false);
		
		label = new Label(methods, SWT.NULL);

		label = new Label(container, SWT.NULL);
		
		// TODO Choose RevealEvent
		
		initialize();
		setControl(container);
	}
	
	protected IPackageFragmentRoot chooseContainer() {
		IJavaElement initElement= project.getPrimaryElement();
		Class[] acceptedClasses= new Class[] { IPackageFragmentRoot.class, IJavaProject.class };
		TypedElementSelectionValidator validator= new TypedElementSelectionValidator(acceptedClasses, false) {
			public boolean isSelectedValid(Object element) {
				try {
					if (element instanceof IJavaProject) {
						IJavaProject jproject= (IJavaProject)element;
						IPath path= jproject.getProject().getFullPath();
						return (jproject.findPackageFragmentRoot(path) != null);
					} else if (element instanceof IPackageFragmentRoot) {
						return (((IPackageFragmentRoot)element).getKind() == IPackageFragmentRoot.K_SOURCE);
					}
					return true;
				} catch (JavaModelException e) {
					JavaPlugin.log(e.getStatus()); // just log, no UI in validation
				}
				return false;
			}
		};

		acceptedClasses= new Class[] { IJavaModel.class, IPackageFragmentRoot.class, IJavaProject.class };
		ViewerFilter filter= new TypedViewerFilter(acceptedClasses) {
			public boolean select(Viewer viewer, Object parent, Object element) {
				if (element instanceof IPackageFragmentRoot) {
					try {
						return (((IPackageFragmentRoot)element).getKind() == IPackageFragmentRoot.K_SOURCE);
					} catch (JavaModelException e) {
						JavaPlugin.log(e.getStatus()); // just log, no UI in validation
						return false;
					}
				}
				return super.select(viewer, parent, element);
			}
		};

		StandardJavaElementContentProvider provider= new StandardJavaElementContentProvider();
		ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
		ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(getShell(), labelProvider, provider);
		dialog.setValidator(validator);
		dialog.setComparator(new JavaElementComparator());
		dialog.setTitle(NewWizardMessages.NewContainerWizardPage_ChooseSourceContainerDialog_title);
		dialog.setMessage(NewWizardMessages.NewContainerWizardPage_ChooseSourceContainerDialog_description);
		dialog.addFilter(filter);
		dialog.setInput(project);
		dialog.setInitialSelection(initElement);
		dialog.setHelpAvailable(false);

		if (dialog.open() == Window.OK) {
			Object element= dialog.getFirstResult();
			if (element instanceof IJavaProject) {
				IJavaProject jproject= (IJavaProject)element;
				return jproject.getPackageFragmentRoot(jproject.getProject());
			} else if (element instanceof IPackageFragmentRoot) {
				return (IPackageFragmentRoot)element;
			}
			return null;
		}
		return null;
	}
	
	/*protected IPackageFragment choosePackage() {
		IPackageFragmentRoot froot= getPackageFragmentRoot();
		IJavaElement[] packages= null;
		try {
			if (froot != null && froot.exists()) {
				packages= froot.getChildren();
			}
		} catch (JavaModelException e) {
			JavaPlugin.log(e);
		}
		if (packages == null) {
			packages= new IJavaElement[0];
		}

		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT));
		dialog.setIgnoreCase(false);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_title);
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_description);
		dialog.setEmptyListMessage(NewWizardMessages.NewTypeWizardPage_ChoosePackageDialog_empty);
		dialog.setElements(packages);
		dialog.setHelpAvailable(false);

		IPackageFragment pack= getPackageFragment();
		if (pack != null) {
			dialog.setInitialSelections(new Object[] { pack });
		}

		if (dialog.open() == Window.OK) {
			return (IPackageFragment) dialog.getFirstResult();
		}
		return null;
	}*/

	private IType chooseGateKeeper() {
		if (project == null) {
			return null;
		}

		IJavaElement[] elements= new IJavaElement[] { project };
		IJavaSearchScope scope= SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog= new FilteredTypesSelectionDialog(getShell(), false,
				getWizard().getContainer(), scope, IJavaSearchConstants.CLASS);
		dialog.setTitle("Gatekeeper Selection");
		dialog.setMessage("Select a gatekeeper for your presenter");
		dialog.setInitialPattern("*Gatekseeper");

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	private String getNameWithTypeParameters(IType type) {
		String superName= type.getFullyQualifiedName('.');
		if (!JavaModelUtil.is50OrHigher(type.getJavaProject())) {
			return superName;
		}
		try {
			ITypeParameter[] typeParameters= type.getTypeParameters();
			if (typeParameters.length > 0) {
				StringBuffer buf= new StringBuffer(superName);
				buf.append('<');
				for (int k= 0; k < typeParameters.length; k++) {
					if (k != 0) {
						buf.append(',').append(' ');
					}
					buf.append(typeParameters[k].getElementName());
				}
				buf.append('>');
				return buf.toString();
			}
		} catch (JavaModelException e) {
			// ignore
		}
		return superName;
	}

	private void dialogChanged() {
		if(presenterPackage.getText().isEmpty()) {
			setMessage("Enter the presenter package path");
			return;
		}
		else {
			IResource container = project.getProject().findMember("src/" + presenterPackage.getText().replaceAll("\\.", "/"));
			if(!(container instanceof IContainer && container.exists())) {
				setErrorMessage("Presenter package must be valid");
				return;
			}
		}

		if(presenterName.getText().isEmpty()) {
			setMessage("Enter a name for the Presenter");
			return;
		}
		if(!presenterName.getText().endsWith("Presenter")) {
			setErrorMessage("Presenter File must ends by \"Presenter\"");
			return;
		}

		if(!isWidget.getSelection()) {
			if(isPlace.isEnabled() && isPlace.getSelection()) {
				if(tokenName.getText().isEmpty()) {
					setMessage("Enter a name for the Token");
					return;
				}
				// TODO More token validation
				for(char c : tokenName.getText().toCharArray()) {
					// [a-z][0-9]!
					if(!((c >= 97 && c <= 122) || (c >= 48 && c <= 57) || c == 33)) {
						setErrorMessage("Token name must contain only lower-case letters, numbers and !");
						return;
					}
				}
				if(!gatekeeper.getText().isEmpty()) {
					try {
						IType type = project.findType(gatekeeper.getText());
						if(type == null || !type.exists()) {
							setErrorMessage(gatekeeper.getText() + " doesn't exist");
							return;
						}
						ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
						IType[] interfaces = hierarchy.getAllInterfaces();
						boolean isGateKeeper = false;
						for(IType inter : interfaces) {
							String test = getNameWithTypeParameters(inter);
							if(test.equals("com.gwtplatform.mvp.client.proxy.Gatekeeper")) {
								isGateKeeper = true;
								break;
							}
						}
						if(!isGateKeeper) {
							setErrorMessage(gatekeeper.getText() + " doesn't implement GateKeeper");
							return;
						}
					} 
					catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		setPageComplete(true);
	}

	public void setMessage(String newMessage) {
		super.setMessage(newMessage);
		super.setErrorMessage(null);
		super.setPageComplete(false);
	}

	public void setErrorMessage(String newMessage) {
		super.setMessage(null);
		super.setErrorMessage(newMessage);
		super.setPageComplete(false);
	}

	public void setPageComplete(boolean complete) {
		if(complete) {
			super.setMessage(null);
			super.setErrorMessage(null);
		}
		super.setPageComplete(complete);
	}

	private void initialize() {
		if(selection != null && !selection.isEmpty()) {
			if (selection.size() > 1)
				return;
			Object firstElement = selection.getFirstElement();
			IResource resource = null;
			if (firstElement instanceof IResource) {
				// Is it a IResource ?
				resource = (IResource) firstElement;
			}
			else if (firstElement instanceof IAdaptable) {
				// Is it a IResource adaptable ?
				IAdaptable adaptable = (IAdaptable) firstElement;
				resource = (IResource) adaptable.getAdapter(IResource.class);
			}

			IContainer container = null;
			if(resource != null) {
				if(resource instanceof IContainer)
					container = (IContainer) resource;
				else
					container = resource.getParent();
			}
			project = JavaCore.create(container.getProject());

			String pack = "";
			for(int i = 1; i < container.getProjectRelativePath().segmentCount(); i++) {
				if(!pack.isEmpty())
					pack += ".";
				pack += container.getProjectRelativePath().segment(i);
			}
			presenterPackage.setText(pack);
		}
	}

	public String getPresenterPackage() {
		return presenterPackage.getText();
	}

	public String getPresenterName() {
		return presenterName.getText().replaceAll("Presenter", "");
	}

	public String getToken() {
		return tokenName.getText();
	}

	public String getGateKeeper() {
		return gatekeeper.getText();
	}

	public boolean isCodeSplit() {
		return isCodeSplit.getSelection();
	}

	public boolean isPlace() {
		return isPlace.getSelection();
	}

	public boolean isWidget() {
		return isWidget.getSelection();
	}

	public boolean useUiBinder() {
		return useUiBinder.getSelection();
	}
	
	public List<Boolean> onMethods() {
		List<Boolean> onMethods = new ArrayList<Boolean>();
		onMethods.add(onBind.getSelection());
		onMethods.add(onHide.getSelection());
		onMethods.add(onReset.getSelection());
		onMethods.add(onReveal.getSelection());
		onMethods.add(onUnbind.getSelection());
		return onMethods;
	}
}
