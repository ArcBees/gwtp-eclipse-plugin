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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.dialogs.ITypeInfoFilterExtension;
import org.eclipse.jdt.ui.dialogs.ITypeInfoRequestor;
import org.eclipse.jdt.ui.dialogs.TypeSelectionExtension;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.imagem.gwtpplugin.Activator;
import com.imagem.gwtpplugin.controls.AddFieldDialog;
import com.imagem.gwtpplugin.projectfile.Field;

/**
 * 
 * @author Michael Renaud
 *
 */
@SuppressWarnings("restriction")
public class NewActionWizardPage extends NewTypeWizardPage {

	private final static String PAGE_NAME = "NewActionWizardPage";
	private Table actionTable;
	private Table resultTable;
	private List<Field> actionFields;
	private List<Field> resultFields;
	private Button addActionField;
	private Button addResultField;
	private IStatus fActionSuperclassStatus = new StatusInfo();
	private IStatus fActionFieldsStatus = new StatusInfo();
	private IStatus fResultFieldsStatus = new StatusInfo();
	private IStatus fActionHandlerStatus = new StatusInfo();
	private IStatus fActionValidatorStatus = new StatusInfo();
	private IStatus fHandlerModuleStatus = new StatusInfo();
	private Text handlerModule;
	private Text actionSuperclass;
	private Text actionHandlerPackage;
	private Text actionValidator;

	public NewActionWizardPage(IStructuredSelection selection) {
		super(true, PAGE_NAME);
		setTitle("Create an Action");
		setDescription("Create an Action and related classes");

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
				fActionSuperclassStatus, 
				fActionFieldsStatus,
				fResultFieldsStatus,
				fActionHandlerStatus, 
				fActionValidatorStatus, 
				fHandlerModuleStatus
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
		createActionSuperclassControls(composite, nColumns);
		createActionFieldsControls(composite, nColumns);
		createResultFieldsControls(composite, nColumns);
		createActionHandlerControls(composite, nColumns);
		createActionValidatorControls(composite, nColumns);
		createHanderModuleControls(composite, nColumns);

		setControl(composite);
		setFocus();
		setDefaultValues();

		Dialog.applyDialogFont(composite);
	}

	private void setDefaultValues() {
		try {
			if(actionSuperclass != null) {
				String actionSuperclassValue = getJavaProject().getProject().getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, "action"));
				actionSuperclass.setText(actionSuperclassValue == null ? "" : actionSuperclassValue);

				fActionSuperclassStatus = actionSuperclassChanged();
			}
			if(handlerModule != null) {
				String handlerModuleValue = getJavaProject().getProject().getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, "handlermodule"));
				handlerModule.setText(handlerModuleValue == null ? "" : handlerModuleValue);

				fHandlerModuleStatus = handlerModuleChanged();
			}
			doStatusUpdate();
		}
		catch (CoreException e1) {}
	}

	protected String getTypeNameLabel() {
		return "Action name:";
	}

	private void createActionSuperclassControls(Composite composite, int nColumns) {
		Label label = new Label(composite, SWT.NULL);
		label.setText("Action Superclass:");

		GridData gd = new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.horizontalSpan = nColumns - 2;

		actionSuperclass = new Text(composite, SWT.BORDER | SWT.SINGLE);
		actionSuperclass.setLayoutData(gd);
		actionSuperclass.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fActionSuperclassStatus = actionSuperclassChanged();
				doStatusUpdate();
			}
		});
		actionSuperclass.setText("com.gwtplatform.dispatch.shared.ActionImpl");

		Button browse = new Button(composite, SWT.PUSH);
		browse.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browse.setText("Browse...");
		browse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				actionSuperclass.setText(chooseActionSuperclass().getFullyQualifiedName('.'));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				actionSuperclass.setText(chooseActionSuperclass().getFullyQualifiedName('.'));
			}
		});
	}

	private IType chooseActionSuperclass() {
		IJavaProject project = getJavaProject();
		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[] { project };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, getWizard().getContainer(), scope, IJavaSearchConstants.CLASS, new ActionSuperclassSelectionExtension());
		dialog.setTitle("Action Superclass selection");
		dialog.setMessage("Choose a superclass for your action");
		dialog.setInitialPattern("*ActionImpl");

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	protected IStatus actionSuperclassChanged() {
		StatusInfo status = new StatusInfo();

		if(actionSuperclass.getText().isEmpty()) {
			status.setError("Enter the action's superclass");
			return status;
		}

		try {
			IType type = getJavaProject().findType(actionSuperclass.getText());
			if(type == null || !type.exists()) {
				status.setError(actionSuperclass.getText() + " doesn't exist");
				return status;
			}
			ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
			IType[] interfaces = hierarchy.getAllInterfaces();
			boolean isAction = false;
			for(IType inter : interfaces) {
				if(inter.getFullyQualifiedName('.').equals("com.gwtplatform.dispatch.shared.Action")) {
					isAction = true;
					break;
				}
			}
			if(!isAction) {
				status.setError(actionSuperclass.getText() + " doesn't implement Action");
				return status;
			}
		}
		catch (JavaModelException e) {
			status.setError("An unexpected error has happened. Close the wizard and retry.");
			return status;
		}
		return status;
	}

	protected void createActionFieldsControls(Composite composite, int nColumns) {
		actionFields = new ArrayList<Field>();

		Label label = new Label(composite, SWT.NULL);
		label.setText("Action fields:");
		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		actionTable = new Table(composite, SWT.BORDER);
		GridData gd = new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.horizontalSpan = nColumns - 2;
		gd.heightHint = 100;
		actionTable.setLayoutData(gd);

		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(50, false));
		layout.addColumnData(new ColumnWeightData(50, false));
		actionTable.setLayout(layout);
		actionTable.setHeaderVisible(true);

		TableColumn fieldType = new TableColumn(actionTable, SWT.LEFT);
		fieldType.setText("Type");
		TableColumn fieldName = new TableColumn(actionTable, SWT.LEFT);
		fieldName.setText("Name");

		Composite buttons = new Composite(composite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		buttons.setLayout(gridLayout);
		buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		addActionField = new Button(buttons, SWT.PUSH);
		addActionField.setText("Add...");
		addActionField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addActionField.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addActionField();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				addActionField();
			}
		});

		Button remove = new Button(buttons, SWT.PUSH);
		remove.setText("Remove");
		remove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		remove.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeActionField(actionTable.getSelectionIndex());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				removeActionField(actionTable.getSelectionIndex());
			}
		});
	}

	protected void addActionField() {
		AddFieldDialog dialog = new AddFieldDialog(getShell(), getJavaProject(), this);
		dialog.setWindowTitle("Fields edition");
		dialog.setTitle("Add a new field to the action");
		if(dialog.open() == Window.OK) {
			Field result = dialog.getValue();

			TableItem ligne = new TableItem(actionTable, SWT.NONE);
			if(result.isPrimitiveType())
				ligne.setText(0, result.getPrimitiveType());
			else
				ligne.setText(0, result.getType().getElementName());
			ligne.setText(1, result.getName());

			actionFields.add(result);
		}
		fActionFieldsStatus = fieldsChanged(actionFields);
		addActionField.setEnabled(fActionFieldsStatus.isOK());
		doStatusUpdate();
	}

	protected void removeActionField(int index) {
		if(index != -1) {
			actionTable.remove(index);
			actionFields.remove(index);
			fActionFieldsStatus = fieldsChanged(actionFields);
			addActionField.setEnabled(fActionFieldsStatus.isOK());
			doStatusUpdate();
		}
	}

	protected void createResultFieldsControls(Composite composite, int nColumns) {
		resultFields = new ArrayList<Field>();

		Label label = new Label(composite, SWT.NULL);
		label.setText("Result fields:");
		label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		resultTable = new Table(composite, SWT.BORDER);
		GridData gd = new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.horizontalSpan = nColumns - 2;
		gd.heightHint = 100;
		resultTable.setLayoutData(gd);

		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(50, false));
		layout.addColumnData(new ColumnWeightData(50, false));
		resultTable.setLayout(layout);
		resultTable.setHeaderVisible(true);

		TableColumn fieldType = new TableColumn(resultTable, SWT.LEFT);
		fieldType.setText("Type");
		TableColumn fieldName = new TableColumn(resultTable, SWT.LEFT);
		fieldName.setText("Name");

		Composite buttons = new Composite(composite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		buttons.setLayout(gridLayout);
		buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		addResultField = new Button(buttons, SWT.PUSH);
		addResultField.setText("Add...");
		addResultField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addResultField.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addResultField();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				addResultField();
			}
		});

		Button remove = new Button(buttons, SWT.PUSH);
		remove.setText("Remove");
		remove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		remove.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeResultField(resultTable.getSelectionIndex());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				removeResultField(resultTable.getSelectionIndex());
			}
		});
	}

	protected void addResultField() {
		AddFieldDialog dialog = new AddFieldDialog(getShell(), getJavaProject(), this);
		dialog.setWindowTitle("Fields edition");
		dialog.setTitle("Add a new field to the result");
		if(dialog.open() == Window.OK) {
			Field result = dialog.getValue();

			TableItem ligne = new TableItem(resultTable, SWT.NONE);
			if(result.isPrimitiveType())
				ligne.setText(0, result.getPrimitiveType());
			else
				ligne.setText(0, result.getType().getElementName());
			ligne.setText(1, result.getName());

			resultFields.add(result);
		}
		fResultFieldsStatus = fieldsChanged(resultFields);
		addResultField.setEnabled(fResultFieldsStatus.isOK());
		doStatusUpdate();
	}

	protected void removeResultField(int index) {
		if(index != -1) {
			resultTable.remove(index);
			resultFields.remove(index);
			fResultFieldsStatus = fieldsChanged(resultFields);
			addResultField.setEnabled(fResultFieldsStatus.isOK());
			doStatusUpdate();
		}
	}

	protected IStatus fieldsChanged(List<Field> fields) {
		StatusInfo status = new StatusInfo();

		if(fields.size() > 0) {
			for(int i = 0; i < fields.size() - 1; i++) {
				if(fields.get(i).getName().equals(fields.get(fields.size() - 1).getName())) {
					status.setError("A field named \"" + fields.get(i).getName() + "\" already exists.");
					return status;
				}
			}

			Field field = fields.get(fields.size() - 1);
			if(!field.isPrimitiveType() && (field.getType() == null || !field.getType().exists())) {
				status.setError(field.getType().getElementName() + " doesn't exist");
				return status;
			}
		}

		return status;
	}

	private void createActionHandlerControls(Composite composite, int nColumns) {
		Label label = new Label(composite, SWT.NULL);
		label.setText("ActionHandler package:");

		GridData gd = new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.horizontalSpan = nColumns - 2;

		actionHandlerPackage = new Text(composite, SWT.BORDER | SWT.SINGLE);
		actionHandlerPackage.setLayoutData(gd);
		actionHandlerPackage.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fActionHandlerStatus = actionHandlerChanged();
				doStatusUpdate();
			}
		});

		Button browse = new Button(composite, SWT.PUSH);
		browse.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browse.setText("Browse...");
		browse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				actionHandlerPackage.setText(chooseActionHandler().getElementName());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				actionHandlerPackage.setText(chooseActionHandler().getElementName());
			}
		});

		fActionHandlerStatus = actionHandlerChanged();
		doStatusUpdate();
	}

	protected IPackageFragment chooseActionHandler() {
		IPackageFragmentRoot froot = getPackageFragmentRoot();
		IJavaElement[] packages = null;
		try {
			if(froot != null && froot.exists()) {
				packages = froot.getChildren();
			}
		}
		catch(JavaModelException e) { 
			e.printStackTrace();
		}

		if(packages == null) {
			packages= new IJavaElement[0];
		}

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT));
		dialog.setIgnoreCase(false);
		dialog.setTitle("ActionHandler Package Selection");
		dialog.setMessage("Choose a folder:");
		dialog.setEmptyListMessage("Cannot find packages to select.");
		dialog.setElements(packages);
		dialog.setHelpAvailable(false);
		dialog.setFilter("*server");

		if (dialog.open() == Window.OK) {
			return (IPackageFragment) dialog.getFirstResult();
		}
		return null;
	}
	
	private IStatus actionHandlerChanged() {
		StatusInfo status = new StatusInfo();
		
		if(actionHandlerPackage.getText().isEmpty()) {
			status.setError("You must select the ActionHandler's package");
			return status;
		}
		
		IPackageFragmentRoot root = getPackageFragmentRoot();
		IJavaProject project = root.getJavaProject();
		IPackageFragment pack = root.getPackageFragment(actionHandlerPackage.getText());
		
		if(!pack.getElementName().isEmpty()) {
			IStatus val = JavaConventionsUtil.validatePackageName(pack.getElementName(), project);
			if(val.getSeverity() == IStatus.ERROR) {
				status.setError(Messages.format(NewWizardMessages.NewTypeWizardPage_error_InvalidPackageName, val.getMessage())); 
				return status;
			}
			else if(val.getSeverity() == IStatus.WARNING) {
				status.setWarning(Messages.format(NewWizardMessages.NewTypeWizardPage_warning_DiscouragedPackageName, val.getMessage())); 
				// continue
			}
		}
		else {
			status.setWarning(NewWizardMessages.NewTypeWizardPage_warning_DefaultPackageDiscouraged); 
		}
		
		if(!pack.getElementName().contains(".server")) {
			status.setError("ActionHandler's package must be in the server package");
			return status;
		}
		
		if(project != null) {
			if(project.exists() && !pack.getElementName().isEmpty()) {
				try {
					IPath rootPath = root.getPath();
					IPath outputPath = project.getOutputLocation();
					if(rootPath.isPrefixOf(outputPath) && !rootPath.equals(outputPath)) {
						// if the bin folder is inside of our root, don't allow to name a package
						// like the bin folder
						IPath packagePath = rootPath.append(pack.getElementName().replace('.', '/'));
						if(outputPath.isPrefixOf(packagePath)) {
							status.setError(NewWizardMessages.NewTypeWizardPage_error_ClashOutputLocation); 
							return status;
						}
					}
				}
				catch(JavaModelException e) {
					JavaPlugin.log(e);
					// let pass			
				}
			}
		}
		else {
			status.setError("");
		}
		return status;
	}


	private void createActionValidatorControls(Composite composite, int nColumns) {
		Label label = new Label(composite, SWT.NULL);
		label.setText("ActionValidator:");

		GridData gd = new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.horizontalSpan = nColumns - 2;

		actionValidator = new Text(composite, SWT.BORDER | SWT.SINGLE);
		actionValidator.setLayoutData(gd);
		actionValidator.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fActionValidatorStatus = actionValidatorChanged();
				doStatusUpdate();
			}
		});

		Button browse = new Button(composite, SWT.PUSH);
		browse.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browse.setText("Browse...");
		browse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				actionValidator.setText(chooseActionValidator().getFullyQualifiedName('.'));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				actionValidator.setText(chooseActionValidator().getFullyQualifiedName('.'));
			}
		});
		
		fActionValidatorStatus = actionValidatorChanged();
		doStatusUpdate();
	}

	private IType chooseActionValidator() {
		IJavaProject project = getJavaProject();
		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[] { project };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, getWizard().getContainer(), scope, IJavaSearchConstants.CLASS, new ActionValidatorSelectionExtension());
		dialog.setTitle("ActionValidator selection");
		dialog.setMessage("Select an ActionValidator class");
		dialog.setInitialPattern("*ActionValidator");

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	protected IStatus actionValidatorChanged() {
		StatusInfo status = new StatusInfo();

		try {
			IType type = getJavaProject().findType(actionValidator.getText());
			if(type == null || !type.exists()) {
				status.setError(actionValidator.getText() + " doesn't exist");
				return status;
			}
			ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
			IType[] interfaces = hierarchy.getAllInterfaces();
			boolean isActionValidator = false;
			for(IType inter : interfaces) {
				if(inter.getFullyQualifiedName('.').equals("com.gwtplatform.dispatch.server.actionvalidator.ActionValidator")) {
					isActionValidator = true;
					break;
				}
			}
			if(!isActionValidator) {
				status.setError(actionValidator.getText() + " doesn't implement ActionValidator");
				return status;
			}
		}
		catch (JavaModelException e) {
			status.setError("An unexpected error has happened. Close the wizard and retry.");
			return status;
		}
		return status;
	}


	private void createHanderModuleControls(Composite composite, int nColumns) {
		Label label = new Label(composite, SWT.NULL);
		label.setText("HandlerModule:");

		GridData gd = new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.horizontalSpan = nColumns - 2;

		handlerModule = new Text(composite, SWT.BORDER | SWT.SINGLE);
		handlerModule.setLayoutData(gd);
		handlerModule.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fHandlerModuleStatus = handlerModuleChanged();
				doStatusUpdate();
			}
		});

		Button browse = new Button(composite, SWT.PUSH);
		browse.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browse.setText("Browse...");
		browse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handlerModule.setText(chooseHandlerModule().getFullyQualifiedName('.'));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handlerModule.setText(chooseHandlerModule().getFullyQualifiedName('.'));
			}
		});

		fHandlerModuleStatus = handlerModuleChanged();
		doStatusUpdate();
	}

	private IType chooseHandlerModule() {
		IJavaProject project = getJavaProject();
		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[] { project };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, getWizard().getContainer(), scope, IJavaSearchConstants.CLASS, new HandlerModuleSelectionExtension());
		dialog.setTitle("HandlerModule selection");
		dialog.setMessage("Select an HandlerModule class");
		dialog.setInitialPattern("ServerModule");

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	protected IStatus handlerModuleChanged() {
		StatusInfo status = new StatusInfo();

		if(handlerModule.getText().isEmpty()) {
			status.setError("You must select an HandlerModule class.");
			return status;
		}

		try {
			IType type = getJavaProject().findType(handlerModule.getText());
			if(type == null || !type.exists()) {
				status.setError(handlerModule.getText() + " doesn't exist");
				return status;
			}
			ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
			IType[] superclasses = hierarchy.getAllClasses();
			boolean isHandlerModule = false;
			for(IType superclass : superclasses) {
				if(superclass.getFullyQualifiedName('.').equals("com.gwtplatform.dispatch.server.guice.HandlerModule")) {
					isHandlerModule = true;
					break;
				}
			}
			if(!isHandlerModule) {
				status.setError(handlerModule.getText() + " doesn't extend HandlerModule");
				return status;
			}
		}
		catch (JavaModelException e) {
			status.setError("An unexpected error has happened. Close the wizard and retry.");
			return status;
		}
		return status;
	}

	public String getResultPackageText() {
		return getPackageText();
	}

	public String getResultTypeName() {
		return getTypeName() + "Result";
	}

	public String getActionSuperclass() {
		return actionSuperclass.getText();
	}

	public Field[] getActionFields() {
		return actionFields.toArray(new Field[actionFields.size()]);
	}

	public Field[] getResultFields() {
		return resultFields.toArray(new Field[resultFields.size()]);
	}

	public String getActionHandlerPackageText() {
		return actionHandlerPackage.getText();
	}

	public String getActionHandlerTypeName() {
		return getTypeName() + "ActionHandler";
	}

	public String getActionValidator() {
		return actionValidator.getText();
	}

	public String getHandlerModule() {
		return handlerModule.getText();
	}

	public class ActionSuperclassSelectionExtension extends TypeSelectionExtension {

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
							if(inter.getFullyQualifiedName('.').equals("com.gwtplatform.dispatch.shared.Action")) {
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

	public class ActionValidatorSelectionExtension extends TypeSelectionExtension {

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
							if(inter.getFullyQualifiedName('.').equals("com.gwtplatform.dispatch.server.actionvalidator.ActionValidator")) {
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

	public class HandlerModuleSelectionExtension extends TypeSelectionExtension {

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
						IType[] superclasses = hierarchy.getAllClasses();
						for(IType superclass : superclasses) {
							if(superclass.getFullyQualifiedName('.').equals("com.gwtplatform.dispatch.server.guice.HandlerModule")) {
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
