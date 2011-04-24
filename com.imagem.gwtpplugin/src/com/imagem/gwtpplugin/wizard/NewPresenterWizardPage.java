/**
 * Copyright 2011 IMAGEM Solutions TI santé
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.ui.dialogs.ITypeInfoFilterExtension;
import org.eclipse.jdt.ui.dialogs.ITypeInfoRequestor;
import org.eclipse.jdt.ui.dialogs.TypeSelectionExtension;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.imagem.gwtpplugin.Activator;

/**
 * 
 * @author Michael Renaud
 *
 */
@SuppressWarnings("restriction")
public class NewPresenterWizardPage extends NewTypeWizardPage {

	private final static String PAGE_NAME = "NewPresenterWizardPage";
	private IStatus fRevealInParentStatus = new StatusInfo();
	private IStatus fPlaceStatus = new StatusInfo();
	private IStatus fGinStatus = new StatusInfo();
	private Button isPlace;
	private Button isProxyStandard;
	private Button isProxyCodeSplit;
	private Text tokenName;
	private Text gatekeeper;
	private Button onBind;
	private Button onHide;
	private Button onReset;
	private Button onReveal;
	private Button onUnbind;
	private Button isPresenterWidget;
	private Button useUiBinder;
	private Button isRevealContentEvent;
	private Button isRevealRootContentEvent;
	private Button isRevealRootLayoutContentEvent;
	private Button isRevealRootPopupContentEvent;
	private Text contentSlot;
	private Button browseGatekeeper;
	private Button browseContentSlot;
	private Button browseTokenName;
	private String selectedSlot;
	private String selectedTokenName;
	private Text presenterModule;
	private Text ginjector;
	private Button browseGinjector;
	private Button isSingleton;

	public NewPresenterWizardPage(IStructuredSelection selection) {
		super(true, PAGE_NAME);
		setTitle("Create a Presenter");
		setDescription("Create a Presenter, its View and its GIN's reference");

		init(selection);
	}

	// -------- Initialization ---------

	/**
	 * The wizard owning this page is responsible for calling this method with the
	 * current selection. The selection is used to initialize the fields of the wizard
	 * page.
	 *
	 * @param selection used to initialize the fields
	 */
	protected void init(IStructuredSelection selection) {
		IJavaElement jelem= getInitialJavaElement(selection);
		initContainerPage(jelem);
		initTypePage(jelem);
		doStatusUpdate();
	}

	// ------ validation --------
	private void doStatusUpdate() {
		// status of all used components
		IStatus[] status= new IStatus[] {
				fContainerStatus,
				fPackageStatus,
				fTypeNameStatus,
				fRevealInParentStatus, 
				fPlaceStatus, 
				fGinStatus
		};

		// the mode severe status will be displayed and the OK button enabled/disabled.
		updateStatus(status);
	}


	/*
	 * @see NewContainerWizardPage#handleFieldChanged
	 */
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);

		doStatusUpdate();
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns= 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		// pick & choose the wanted UI components
		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		//createEnclosingTypeControls(composite, nColumns);

		createSeparator(composite, nColumns);

		createTypeNameControls(composite, nColumns);
		createUITypeControls(composite, nColumns);
		createWidgetControls(composite, nColumns);
		createRevealInParentControls(composite, nColumns);
		createPlaceControls(composite, nColumns);
		createMethodStubsControls(composite, nColumns);
		createGinControls(composite, nColumns);

		setControl(composite);
		setFocus();
		setDefaultValues();
		
		Dialog.applyDialogFont(composite);
	}

	private void setDefaultValues() {
		try {
			if(tokenName != null) {
				String nameTokensValue = getJavaProject().getProject().getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, "nametokens"));
				tokenName.setText(nameTokensValue == null ? "" : nameTokensValue + "#");
				
				fPlaceStatus = placeChanged();
				doStatusUpdate();
			}
			if(ginjector != null && presenterModule != null) {
				String ginjectorValue = getJavaProject().getProject().getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, "ginjector"));
				String presenterModuleValue = getJavaProject().getProject().getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, "presentermodule"));
				
				ginjector.setText(ginjectorValue == null ? "" : ginjectorValue);
				presenterModule.setText(presenterModuleValue == null ? "" : presenterModuleValue);
				
				fGinStatus = ginChanged();
				doStatusUpdate();
			}
		}
		catch (CoreException e1) {}
	}

	protected String getTypeNameLabel() {
		return "Presenter name:";
	}

	protected IStatus typeNameChanged() {
		StatusInfo status = (StatusInfo) super.typeNameChanged();

		if(status.isOK()) {
			String typeNameWithParameters= getTypeName();
			if(!typeNameWithParameters.endsWith("Presenter")) {
				status.setError("Presenter class must ends by \"Presenter\"");
				return status;
			}
		}

		return status;
	}

	private void createUITypeControls(Composite composite, int nColumns) {
		Label label = new Label(composite, SWT.NULL);
		label.setText("Ui:");

		GridData gd = new GridData(GridData.FILL);
		gd.horizontalSpan = nColumns - 2;

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 5;

		Composite checks = new Composite(composite, SWT.NULL);
		checks.setLayoutData(gd);
		checks.setLayout(layout);

		isPresenterWidget = new Button(checks, SWT.CHECK);
		isPresenterWidget.setText("Extend PresenterWidget");
		isPresenterWidget.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setRevealInParentEnabled(!isPresenterWidget.getSelection());
				setPlaceEnabled(!isPresenterWidget.getSelection());
				setGinjectorEnabled(!isPresenterWidget.getSelection());
				isSingleton.setEnabled(isPresenterWidget.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				setRevealInParentEnabled(!isPresenterWidget.getSelection());
				setPlaceEnabled(!isPresenterWidget.getSelection());
				setGinjectorEnabled(!isPresenterWidget.getSelection());
				isSingleton.setEnabled(isPresenterWidget.getSelection());
			}
		});

		useUiBinder = new Button(checks, SWT.CHECK);
		useUiBinder.setText("Use UiBinder");
		useUiBinder.setSelection(true);

		label = new Label(composite, SWT.NULL);
	}

	private void createWidgetControls(Composite composite, int nColumns) {
		Label label = new Label(composite, SWT.NULL);
		label.setText("PresenterWidget:");
		
		GridData gd = new GridData(GridData.FILL);
		gd.horizontalSpan = nColumns - 1;

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 5;

		Composite checks = new Composite(composite, SWT.NULL);
		checks.setLayoutData(gd);
		checks.setLayout(layout);
		
		isSingleton = new Button(checks, SWT.CHECK);
		isSingleton.setText("Singleton");
		isSingleton.setSelection(false);
		isSingleton.setEnabled(false);
	}

	private void createRevealInParentControls(Composite composite, int nColumns) {
		Label label = new Label(composite, SWT.NULL);
		label.setText("Reveal Event:");

		GridData gd = new GridData(GridData.FILL);
		gd.horizontalSpan = nColumns - 1;

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 5;

		Composite radios = new Composite(composite, SWT.NULL);
		radios.setLayoutData(gd);
		radios.setLayout(layout);

		isRevealContentEvent = new Button(radios, SWT.RADIO);
		isRevealContentEvent.setText("RevealContentEvent");
		isRevealContentEvent.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				contentSlot.setEnabled(isRevealContentEvent.isEnabled() ? isRevealContentEvent.getSelection() : false);
				browseContentSlot.setEnabled(isRevealContentEvent.isEnabled() ? isRevealContentEvent.getSelection() : false);

				fRevealInParentStatus = revealInParentChanged();
				doStatusUpdate();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				contentSlot.setEnabled(isRevealContentEvent.isEnabled() ? isRevealContentEvent.getSelection() : false);
				browseContentSlot.setEnabled(isRevealContentEvent.isEnabled() ? isRevealContentEvent.getSelection() : false);

				fRevealInParentStatus = revealInParentChanged();
				doStatusUpdate();
			}
		});
		isRevealContentEvent.setSelection(true);

		isRevealRootContentEvent = new Button(radios, SWT.RADIO);
		isRevealRootContentEvent.setText("RevealRootContentEvent");

		isRevealRootLayoutContentEvent = new Button(radios, SWT.RADIO);
		isRevealRootLayoutContentEvent.setText("RevealRootLayoutContentEvent");

		isRevealRootPopupContentEvent = new Button(radios, SWT.RADIO);
		isRevealRootPopupContentEvent.setText("RevealRootPopupContentEvent");
		isRevealRootPopupContentEvent.setEnabled(false); // TODO

		label = new Label(composite, SWT.NULL);
		label.setText("Content Slot:");

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns - 2;

		contentSlot = new Text(composite, SWT.BORDER | SWT.SINGLE);
		contentSlot.setLayoutData(gd);
		contentSlot.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fRevealInParentStatus = revealInParentChanged();
				doStatusUpdate();
			}
		});

		browseContentSlot = new Button(composite, SWT.PUSH);
		browseContentSlot.setText("Browse...");
		browseContentSlot.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browseContentSlot.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				contentSlot.setText(chooseContentSlot().getFullyQualifiedName('.') + "#" + selectedSlot);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				contentSlot.setText(chooseContentSlot().getFullyQualifiedName('.') + "#" + selectedSlot);
			}
		});

		fRevealInParentStatus = revealInParentChanged();
		doStatusUpdate();
	}

	protected void setRevealInParentEnabled(boolean enabled) {
		isRevealContentEvent.setEnabled(enabled);
		isRevealRootContentEvent.setEnabled(enabled);
		isRevealRootLayoutContentEvent.setEnabled(enabled);
		//isRevealRootPopupContentEvent.setEnabled(enabled);
		contentSlot.setEnabled(isRevealContentEvent.isEnabled() ? isRevealContentEvent.getSelection() : false);
		browseContentSlot.setEnabled(isRevealContentEvent.isEnabled() ? isRevealContentEvent.getSelection() : false);

		fRevealInParentStatus = revealInParentChanged();
		doStatusUpdate();
	}

	protected IStatus revealInParentChanged() {
		StatusInfo status = new StatusInfo();

		if(isRevealContentEvent.isEnabled() && isRevealContentEvent.getSelection()) {
			if(contentSlot.getText().isEmpty()) {
				status.setError("You must enter the parent's content slot when selecting RevealContentEvent");
				return status;
			}

			String slotParent = "";
			String slotName = "";
			if(!contentSlot.getText().contains("#")) {
				slotParent = contentSlot.getText();
			}
			else {
				String[] split = contentSlot.getText().split("#");
				slotParent = split[0];
				if(split.length > 1)
					slotName = split[1];
			}

			try {
				IType type = getJavaProject().findType(slotParent);
				if(type == null || !type.exists()) {
					status.setError(slotParent + " doesn't exist");
					return status;
				}
				ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
				IType[] interfaces = hierarchy.getAllInterfaces();
				boolean hasSlots = false;
				for(IType inter : interfaces) {
					if(inter.getFullyQualifiedName('.').equals("com.gwtplatform.mvp.client.HasSlots")) {
						hasSlots = true;
						break;
					}
				}
				if(!hasSlots) {
					status.setError(slotParent + " doesn't implement HasSlots");
					return status;
				}

				if(slotName.isEmpty()) {
					status.setError("You must enter the slot's name (fully.qualified.ParentPresenter#SlotName)");
					return status;
				}
				IField field = type.getField(slotName);
				if(!field.exists()) {
					status.setError(slotName + " doesn't exist");
					return status;
				}
				if(!field.getAnnotation("ContentSlot").exists()) {
					status.setError(slotName + " isn't a ContentSlot");
					return status;
				}
			}
			catch (JavaModelException e) {
				status.setError("An unexpected error has happened. Close the wizard and retry.");
				return status;
			}
		}

		return status;
	}

	protected IType chooseContentSlot() {
		IJavaProject project = getJavaProject();

		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[] { project };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, getWizard().getContainer(), scope, IJavaSearchConstants.CLASS, new ContentSlotSelectionExtension());
		dialog.setTitle("ContentSlot Selection");
		dialog.setMessage("Select the Presenter's parent");
		dialog.setInitialPattern("*Presenter");

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	private void createPlaceControls(Composite composite, int nColumns) {
		Label label = new Label(composite, SWT.NULL);
		label.setText("Place:");

		GridData gd = new GridData(GridData.FILL);
		gd.horizontalSpan = nColumns - 1;

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 5;

		Composite checks = new Composite(composite, SWT.NULL);
		checks.setLayoutData(gd);
		checks.setLayout(layout);

		isPlace = new Button(checks, SWT.CHECK);
		isPlace.setText("Is Place");
		isPlace.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPlaceEnabled(isPlace.getSelection());
				isPlace.setEnabled(true);

				fPlaceStatus = placeChanged();
				doStatusUpdate();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				setPlaceEnabled(isPlace.getSelection());
				isPlace.setEnabled(true);

				fPlaceStatus = placeChanged();
				doStatusUpdate();
			}
		});
		isPlace.setSelection(true);

		// Proxy
		label = new Label(composite, SWT.NULL);
		label.setText("Proxy:");

		layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 5;

		Composite radios = new Composite(composite, SWT.NULL);
		radios.setLayoutData(gd);
		radios.setLayout(layout);

		isProxyStandard = new Button(radios, SWT.RADIO);
		isProxyStandard.setText("Standard");

		isProxyCodeSplit = new Button(radios, SWT.RADIO);
		isProxyCodeSplit.setText("CodeSplit");
		isProxyCodeSplit.setSelection(true);

		// Token
		label = new Label(composite, SWT.NULL);
		label.setText("Token name:");

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns - 2;

		tokenName = new Text(composite, SWT.BORDER | SWT.SINGLE);
		tokenName.setLayoutData(gd);
		tokenName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fPlaceStatus = placeChanged();
				doStatusUpdate();
			}
		});

		browseTokenName = new Button(composite, SWT.PUSH);
		browseTokenName.setText("Browse...");
		browseTokenName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browseTokenName.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tokenName.setText(chooseTokenName().getFullyQualifiedName('.') + "#" + selectedTokenName);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				tokenName.setText(chooseTokenName().getFullyQualifiedName('.') + "#" + selectedTokenName);
			}
		});

		// GateKeeper
		label = new Label(composite, SWT.NULL);
		label.setText("Gatekeeper:");

		gatekeeper = new Text(composite, SWT.BORDER | SWT.SINGLE);
		gatekeeper.setLayoutData(gd);
		gatekeeper.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fPlaceStatus = placeChanged();
				doStatusUpdate();
			}
		});

		browseGatekeeper = new Button(composite, SWT.PUSH);
		browseGatekeeper.setText("Browse...");
		browseGatekeeper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browseGatekeeper.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gatekeeper.setText(chooseGatekeeper().getFullyQualifiedName('.'));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				gatekeeper.setText(chooseGatekeeper().getFullyQualifiedName('.'));
			}
		});

		fPlaceStatus = placeChanged();
		doStatusUpdate();
	}

	protected IStatus placeChanged() {
		StatusInfo status = new StatusInfo();

		if(isPlace.isEnabled() && isPlace.getSelection()) {
			// Token
			if(tokenName.getText().isEmpty()) {
				status.setError("Enter the token's name (fully.qualified.NameTokens#name)");
				return status;
			}
			String parent = "";
			String token = "";
			if(!tokenName.getText().contains("#")) {
				parent = tokenName.getText();
			}
			else {
				String[] split = tokenName.getText().split("#");
				parent = split[0];
				if(split.length > 1)
					token = split[1];
			}

			try {
				IType type = getJavaProject().findType(parent);
				if(type == null || !type.exists()) {
					status.setError(parent + " doesn't exist");
					return status;
				}
				if(type.isBinary()) {
					status.setError(parent + " is a Binary class");
					return status;
				}
				if(token.isEmpty()) {
					status.setError("You must enter the token name (fully.qualified.NameTokens#name)");
					return status;
				}
				char start = token.toCharArray()[0];
				if(start >= 48 && start <= 57) {
					status.setError("Token name must not start by a number");
					return status;
				}
				for(char c : token.toCharArray()) {
					// [a-z][0-9]!
					if(!((c >= 97 && c <= 122) || (c >= 48 && c <= 57) || c == 33)) {
						status.setError("Token name must contain only lower-case letters, numbers and !");
						return status;
					}
				}
				IField field = type.getField(token);
				if(field.exists()) {
					status.setError("The token " + token + " already exists");
					return status;
				}
			}
			catch (JavaModelException e) {
				status.setError("An unexpected error has happened. Close the wizard and retry.");
				return status;
			}
			// Gatekeeper
			if(!gatekeeper.getText().isEmpty()) {
				try {
					IType type = getJavaProject().findType(gatekeeper.getText());
					if(type == null || !type.exists()) {
						status.setError(gatekeeper.getText() + " doesn't exist");
						return status;
					}
					ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
					IType[] interfaces = hierarchy.getAllInterfaces();
					boolean isGateKeeper = false;
					for(IType inter : interfaces) {
						if(inter.getFullyQualifiedName('.').equals("com.gwtplatform.mvp.client.proxy.Gatekeeper")) {
							isGateKeeper = true;
							break;
						}
					}
					if(!isGateKeeper) {
						status.setError(gatekeeper.getText() + " doesn't implement GateKeeper");
						return status;
					}
				}
				catch (JavaModelException e) {
					status.setError("An unexpected error has happened. Close the wizard and retry.");
					return status;
				}
			}
		}

		return status;
	}

	protected IType chooseTokenName() {
		IJavaProject project = getJavaProject();

		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[] { project };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, getWizard().getContainer(), scope, IJavaSearchConstants.CLASS, new TokenNameSelectionExtension());
		dialog.setTitle("Token name Selection");
		dialog.setMessage("Select the Tokens class");
		dialog.setInitialPattern("*Tokens");

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	private IType chooseGatekeeper() {
		IJavaProject project = getJavaProject();

		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[] { project };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, getWizard().getContainer(), scope, IJavaSearchConstants.CLASS, new GatekeeperSelectionExtension());
		dialog.setTitle("Gatekeeper Selection");
		dialog.setMessage("Select the Presenter's Gatekeeper");
		dialog.setInitialPattern("*Gatekeeper");

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	protected void setPlaceEnabled(boolean enabled) {
		isPlace.setEnabled(enabled);
		isProxyStandard.setEnabled(isPlace.getSelection() ? enabled : false);
		isProxyCodeSplit.setEnabled(isPlace.getSelection() ? enabled : false);
		tokenName.setEnabled(isPlace.getSelection() ? enabled : false);
		browseTokenName.setEnabled(isPlace.getSelection() ? enabled : false);
		gatekeeper.setEnabled(isPlace.getSelection() ? enabled : false);
		browseGatekeeper.setEnabled(isPlace.getSelection() ? enabled : false);

		fPlaceStatus = placeChanged();
		doStatusUpdate();
	}

	protected void createMethodStubsControls(Composite composite, int nColumns) {
		// Methods
		Label label = new Label(composite, SWT.BEGINNING);
		label.setText("Method stubs:");
		label.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns - 1;

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 5;

		Composite methods = new Composite(composite, SWT.NULL);
		methods.setLayoutData(gd);
		methods.setLayout(layout);

		onBind = new Button(methods, SWT.CHECK);
		onBind.setText("onBind()");
		onBind.setSelection(true);

		onHide = new Button(methods, SWT.CHECK);
		onHide.setText("onHide()");

		onReset = new Button(methods, SWT.CHECK);
		onReset.setText("onReset()");

		onReveal = new Button(methods, SWT.CHECK);
		onReveal.setText("onReveal()");

		onUnbind = new Button(methods, SWT.CHECK);
		onUnbind.setText("onUnbind()");

		label = new Label(methods, SWT.NULL);
	}

	private void createGinControls(Composite composite, int nColumns) {
		// Ginjector
		Label label = new Label(composite, SWT.NULL);
		label.setText("Ginjector:");

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumns - 2;

		ginjector = new Text(composite, SWT.BORDER | SWT.SINGLE);
		ginjector.setLayoutData(gd);
		ginjector.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fGinStatus = ginChanged();
				doStatusUpdate();
			}
		});

		browseGinjector = new Button(composite, SWT.PUSH);
		browseGinjector.setText("Browse...");
		browseGinjector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browseGinjector.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ginjector.setText(chooseGinjector().getFullyQualifiedName('.'));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				ginjector.setText(chooseGinjector().getFullyQualifiedName('.'));
			}
		});

		// Presenter Module
		label = new Label(composite, SWT.NULL);
		label.setText("Presenter Module:");

		presenterModule = new Text(composite, SWT.BORDER | SWT.SINGLE);
		presenterModule.setLayoutData(gd);
		presenterModule.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fGinStatus = ginChanged();
				doStatusUpdate();
			}
		});

		Button browsePresenterModule = new Button(composite, SWT.PUSH);
		browsePresenterModule.setText("Browse...");
		browsePresenterModule.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browsePresenterModule.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenterModule.setText(choosePresenterModule().getFullyQualifiedName('.'));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				presenterModule.setText(choosePresenterModule().getFullyQualifiedName('.'));
			}
		});

		fGinStatus = ginChanged();
		doStatusUpdate();
	}

	protected IStatus ginChanged() {
		StatusInfo status = new StatusInfo();

		if(ginjector.isEnabled()) {
			if(ginjector.getText().isEmpty()){
				status.setError("Enter a Ginjector");
				return status;
			}
			try {
				IType type = getJavaProject().findType(ginjector.getText());
				if(type == null || !type.exists()) {
					status.setError(ginjector.getText() + " doesn't exist");
					return status;
				}
				if(type.isBinary()) {
					status.setError(ginjector.getText() + " is a Binary class");
					return status;
				}
				ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
				IType[] interfaces = hierarchy.getAllInterfaces();
				boolean isGinjector = false;
				for(IType inter : interfaces) {
					if(inter.getFullyQualifiedName('.').equals("com.google.gwt.inject.client.Ginjector")) {
						isGinjector = true;
						break;
					}
				}
				if(!isGinjector) {
					status.setError(ginjector.getText() + " doesn't extend Ginjector");
					return status;
				}
			}
			catch (JavaModelException e) {
				status.setError("An unexpected error has happened. Close the wizard and retry.");
				return status;
			}
		}

		if(presenterModule.getText().isEmpty()) {
			status.setError("Enter a PresenterModule");
			return status;
		}
		try {
			IType type = getJavaProject().findType(presenterModule.getText());
			if(type == null || !type.exists()) {
				status.setError(presenterModule.getText() + " doesn't exist");
				return status;
			}
			if(type.isBinary()) {
				status.setError(ginjector.getText() + " is a Binary class");
				return status;
			}
			ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
			IType[] superclasses = hierarchy.getAllClasses();
			boolean isPresenterModule = false;
			for(IType superclass : superclasses) {
				if(superclass.getFullyQualifiedName('.').equals("com.gwtplatform.mvp.client.gin.AbstractPresenterModule")) {
					isPresenterModule = true;
					break;
				}
			}
			if(!isPresenterModule) {
				status.setError(ginjector.getText() + " doesn't implement AbstractPresenterModule");
				return status;
			}
		}
		catch (JavaModelException e) {
			status.setError("An unexpected error has happened. Close the wizard and retry.");
			return status;
		}	

		return status;
	}

	protected IType chooseGinjector() {
		IJavaProject project = getJavaProject();

		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[] { project };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, getWizard().getContainer(), scope, IJavaSearchConstants.INTERFACE, new GinjectorSelectionExtension());
		dialog.setTitle("Ginjector Selection");
		dialog.setMessage("Select a Ginjector");
		dialog.setInitialPattern("*Ginjector");

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	protected IType choosePresenterModule() {
		IJavaProject project = getJavaProject();

		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[] { project };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, getWizard().getContainer(), scope, IJavaSearchConstants.CLASS, new PresenterModuleSelectionExtension());
		dialog.setTitle("PresenterModule Selection");
		dialog.setMessage("Select a PresenterModule");
		dialog.setInitialPattern("*Module");

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	protected void setGinjectorEnabled(boolean enabled) {
		ginjector.setEnabled(enabled);
		browseGinjector.setEnabled(enabled);

		fGinStatus = ginChanged();
		doStatusUpdate();
	}

	public boolean isWidget() {
		return isPresenterWidget.getSelection();
	}

	public boolean useUiBinder() {
		return useUiBinder.getSelection();
	}
	
	public boolean isSingleton() {
		return isSingleton.getSelection();
	}

	public String getRevealEvent() {
		if(isRevealContentEvent.getSelection()) {
			return "com.gwtplatform.mvp.client.proxy.RevealContentEvent";
		}
		else if(isRevealRootContentEvent.getSelection()) {
			return "com.gwtplatform.mvp.client.proxy.RevealRootContentEvent";
		}
		else if(isRevealRootLayoutContentEvent.getSelection()) {
			return "com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent";
		}
		else {
			return "com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent";
		}
	}

	public String getViewPackageText() {
		return getPackageText();
	}

	public String getViewTypeName() {
		return getTypeName().replaceAll("Presenter", "View");
	}

	public String getParent() {
		return contentSlot.getText().split("#")[0];
	}

	public String getContentSlot() {
		return contentSlot.getText().split("#")[1];
	}

	public boolean isPlace() {
		return isPlace.getSelection() && isPlace.isEnabled();
	}

	public boolean isProxyStandard() {
		return isProxyStandard.getSelection();
	}

	public String getTokenClass() {
		return tokenName.getText().split("#")[0];
	}

	public String getTokenName() {
		return tokenName.getText().split("#")[1];
	}

	public String getGatekeeper() {
		return gatekeeper.getText();
	}

	public String getGinjector() {
		return ginjector.getText();
	}

	public String getPresenterModule() {
		return presenterModule.getText();
	}

	public String[] getMethodStubs() {
		List<String> methods = new ArrayList<String>();
		if(onBind.getSelection())
			methods.add("onBind");
		if(onHide.getSelection())
			methods.add("onHide");
		if(onReset.getSelection())
			methods.add("onReset");
		if(onReveal.getSelection())
			methods.add("onReveal");
		if(onUnbind.getSelection())
			methods.add("onUnbind");
		
		return methods.toArray(new String[methods.size()]);
	}

	public class GatekeeperSelectionExtension extends TypeSelectionExtension {

		@Override
		public ITypeInfoFilterExtension getFilterExtension() {
			ITypeInfoFilterExtension extension = new ITypeInfoFilterExtension() {
				@Override
				public boolean select(ITypeInfoRequestor requestor) {
					try {
						IType type = getJavaProject().findType(requestor.getPackageName() + "." + requestor.getTypeName());
						if(type == null || !type.exists()) {
							return false;
						}
						ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
						IType[] interfaces = hierarchy.getAllInterfaces();
						for(IType inter : interfaces) {
							if(inter.getFullyQualifiedName('.').equals("com.gwtplatform.mvp.client.proxy.Gatekeeper")) {
								return true;
							}
						}
						return false;
					}
					catch (JavaModelException e) {
						return false;
					}
				}
			};

			return extension;
		}
	}

	public class ContentSlotSelectionExtension extends TypeSelectionExtension {
		private Combo slot;

		@Override
		public ITypeInfoFilterExtension getFilterExtension() {
			ITypeInfoFilterExtension extension = new ITypeInfoFilterExtension() {
				@Override
				public boolean select(ITypeInfoRequestor requestor) {
					try {
						IType type = getJavaProject().findType(requestor.getPackageName() + "." + requestor.getTypeName());
						if(type == null || !type.exists()) {
							return false;
						}
						ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
						IType[] interfaces = hierarchy.getAllInterfaces();
						for(IType inter : interfaces) {
							if(inter.getFullyQualifiedName('.').equals("com.gwtplatform.mvp.client.HasSlots")) {
								for(IField field : type.getFields()) {
									if(field.getAnnotation("ContentSlot").exists()) {
										return true;
									}
								}
							}
						}
						return false;
					}
					catch (JavaModelException e) {
						return false;
					}
				}
			};
			return extension;
		}

		@Override
		public ISelectionStatusValidator getSelectionValidator() {
			ISelectionStatusValidator validator = new ISelectionStatusValidator() {
				@Override
				public IStatus validate(Object[] selection) {
					slot.removeAll();

					IType type = (IType) selection[0];

					try {
						for(IField field : type.getFields()) {
							if(field.getAnnotation("ContentSlot").exists()) {
								slot.add(field.getElementName());
							}
						}
						slot.select(0);
					}
					catch (JavaModelException e) {
						return new StatusInfo();
					}

					return new StatusInfo();
				}
			};
			return validator;
		}

		@Override
		public Control createContentArea(Composite parent) {
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;

			Composite composite = new Composite(parent, SWT.NONE);
			composite.setFont(parent.getFont());
			composite.setLayout(layout);

			Label label = new Label(composite, SWT.NONE);
			label.setText("Slot:");

			slot = new Combo(composite, SWT.READ_ONLY);
			slot.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			slot.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					if(slot.getSelectionIndex() >= 0)
						selectedSlot = slot.getItem(slot.getSelectionIndex());
				}
			});

			return composite;
		}
	}

	public class TokenNameSelectionExtension extends TypeSelectionExtension {
		private Text name;

		@Override
		public ITypeInfoFilterExtension getFilterExtension() {
			ITypeInfoFilterExtension extension = new ITypeInfoFilterExtension() {
				@Override
				public boolean select(ITypeInfoRequestor requestor) {
					try {
						IType type = getJavaProject().findType(requestor.getPackageName() + "." + requestor.getTypeName());
						if(type == null || !type.exists() || type.isBinary()) {
							return false;
						}
						return true;
					}
					catch (JavaModelException e) {
						return false;
					}
				}
			};
			return extension;
		}

		@Override
		public Control createContentArea(Composite parent) {
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;

			Composite composite = new Composite(parent, SWT.NONE);
			composite.setFont(parent.getFont());
			composite.setLayout(layout);

			Label label = new Label(composite, SWT.NONE);
			label.setText("Token name:");

			name = new Text(composite, SWT.BORDER | SWT.SINGLE);
			name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			name.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					selectedTokenName = name.getText();
				}
			});

			return composite;
		}
	}

	public class GinjectorSelectionExtension extends TypeSelectionExtension {

		@Override
		public ITypeInfoFilterExtension getFilterExtension() {
			ITypeInfoFilterExtension extension = new ITypeInfoFilterExtension() {
				@Override
				public boolean select(ITypeInfoRequestor requestor) {
					try {
						IType type = getJavaProject().findType(requestor.getPackageName() + "." + requestor.getTypeName());
						if(type == null || !type.exists() || type.isBinary()) {
							return false;
						}
						ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
						IType[] interfaces = hierarchy.getAllInterfaces();
						for(IType inter : interfaces) {
							if(inter.getFullyQualifiedName('.').equals("com.google.gwt.inject.client.Ginjector")) {
								return true;
							}
						}
						return false;
					}
					catch (JavaModelException e) {
						return false;
					}
				}
			};

			return extension;
		}
	}

	public class PresenterModuleSelectionExtension extends TypeSelectionExtension {

		@Override
		public ITypeInfoFilterExtension getFilterExtension() {
			ITypeInfoFilterExtension extension = new ITypeInfoFilterExtension() {
				@Override
				public boolean select(ITypeInfoRequestor requestor) {
					try {
						IType type = getJavaProject().findType(requestor.getPackageName() + "." + requestor.getTypeName());
						if(type == null || !type.exists() || type.isBinary()) {
							return false;
						}
						ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
						IType[] superclasses = hierarchy.getAllClasses();
						for(IType superclass : superclasses) {
							if(superclass.getFullyQualifiedName('.').equals("com.gwtplatform.mvp.client.gin.AbstractPresenterModule")) {
								return true;
							}
						}
						return false;
					}
					catch (JavaModelException e) {
						return false;
					}
				}
			};

			return extension;
		}
	}
}
