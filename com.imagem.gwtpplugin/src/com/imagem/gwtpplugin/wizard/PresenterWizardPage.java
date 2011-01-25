package com.imagem.gwtpplugin.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
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
	private Text gateKeeper;
	private Button btnBrowseGateKeeper;
	private IJavaProject project;
	private Button onBind;
	private Button onHide;
	private Button onReset;
	private Button onReveal;
	private Button onUnbind;

	public PresenterWizardPage(IStructuredSelection selection) {
		super("PresenterWizardPage");
		setTitle("Create a Presenter");
		setDescription("Create a Presener, it's View and it's reference in GIN");
		this.selection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 5;

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

		label = new Label(container, SWT.NULL);

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
					gateKeeper.setEnabled(!isWidget.getSelection());
					btnBrowseGateKeeper.setEnabled(!isWidget.getSelection());
				}
				else {
					tokenName.setEnabled(isPlace.getSelection());
					gateKeeper.setEnabled(isPlace.getSelection());
					btnBrowseGateKeeper.setEnabled(isPlace.getSelection());
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
				gateKeeper.setEnabled(isPlace.getSelection());
				btnBrowseGateKeeper.setEnabled(isPlace.getSelection());
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
		label.setText("GateKeeper:");

		gateKeeper = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gateKeeper.setLayoutData(gd);
		gateKeeper.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		btnBrowseGateKeeper = new Button(container, SWT.PUSH);
		btnBrowseGateKeeper.setText("Browse...");
		btnBrowseGateKeeper.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gateKeeper.setText(getNameWithTypeParameters(chooseGateKeeper()));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				gateKeeper.setText(getNameWithTypeParameters(chooseGateKeeper()));
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
		
		initialize();
		setControl(container);
	}

	private IType chooseGateKeeper() {
		if (project == null) {
			return null;
		}

		IJavaElement[] elements= new IJavaElement[] { project };
		IJavaSearchScope scope= SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog= new FilteredTypesSelectionDialog(getShell(), false,
				getWizard().getContainer(), scope, IJavaSearchConstants.CLASS);
		dialog.setTitle("TITLE");
		dialog.setMessage("MESSAGE");
		dialog.setInitialPattern("*GateKeeper");

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
		// TODO package validity

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
				for(char c : tokenName.getText().toCharArray()) {
					// [a-z][0-9]!
					if(!((c >= 97 && c <= 122) || (c >= 48 && c <= 57) || c == 33)) {
						setErrorMessage("Token name must contain only lower-case letters, numbers and !");
						return;
					}
				}
				if(!gateKeeper.getText().isEmpty()) {
					try {
						IType type = project.findType(gateKeeper.getText());
						if(type == null || !type.exists()) {
							setErrorMessage(gateKeeper.getText() + " doesn't exists.");
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
							setErrorMessage(gateKeeper.getText() + " doesn't implements GateKeeper");
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
		return gateKeeper.getText();
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
