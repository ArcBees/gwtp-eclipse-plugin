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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

@Deprecated
@SuppressWarnings("restriction")
public class ActionWizardPage extends WizardPage {

	private IStructuredSelection selection;
	private Text actionPackage;
	private Text actionText;
	private Text addActionParameter;
	private Text addResultParameter;
	private List actionParameterList;
	private List resultParameterList;
	private Button addActionButton;
	private Button addResultButton;
	private Text actionType;
	private Button btnBrowseActionType;
	private IJavaProject project;

	public ActionWizardPage(IStructuredSelection selection) {
		super("ActionWizardPage");
		setTitle("Create an Action");
		setDescription("Create an Action and related classes");
		this.selection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 5;

		// Action Package
		Label label = new Label(container, SWT.NULL);
		label.setText("Action package:");

		actionPackage = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		actionPackage.setLayoutData(gd);
		actionPackage.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);

		// Action Name
		label = new Label(container, SWT.NULL);
		label.setText("Action name:");

		actionText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		actionText.setLayoutData(gd);
		actionText.setFocus();
		actionText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);

		// Action Type
		label = new Label(container, SWT.NULL);
		label.setText("Action Superclass:");

		actionType = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		actionType.setLayoutData(gd);
		actionType.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		btnBrowseActionType = new Button(container, SWT.PUSH);
		btnBrowseActionType.setText("Browse...");
		btnBrowseActionType.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				actionType.setText(getNameWithTypeParameters(chooseActionType()));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				actionType.setText(getNameWithTypeParameters(chooseActionType()));
			}
		});

		// Add Parameter
		label = new Label(container, SWT.NULL);
		label.setText("Action Parameters:");

		addActionParameter = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		addActionParameter.setLayoutData(gd);
		addActionParameter.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		addActionButton = new Button(container, SWT.PUSH);
		addActionButton.setText("+");
		addActionButton.setEnabled(false);
		addActionButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean isInList = false;
				for(String item : actionParameterList.getItems()) {
					if(item.equals(addActionParameter.getText())) {
						isInList = true;
						break;
					}
				}
				if(!isInList) {
					actionParameterList.add(addActionParameter.getText());
				}
				addActionParameter.setText("");
				addActionParameter.setFocus();
			}
		});

		// Parameters List
		label = new Label(container, SWT.NULL);

		actionParameterList = new List(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		actionParameterList.setLayoutData(gd);

		Button suppButton = new Button(container, SWT.PUSH);
		suppButton.setText("-");
		suppButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(actionParameterList.getItemCount() > 0) {
					if(actionParameterList.getSelectionIndex() != -1) {
						actionParameterList.remove(actionParameterList.getSelectionIndex());
					}
				}
			}
		});

		// Add Parameter
		label = new Label(container, SWT.NULL);
		label.setText("Result Parameters:");

		addResultParameter = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		addResultParameter.setLayoutData(gd);
		addResultParameter.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		addResultButton = new Button(container, SWT.PUSH);
		addResultButton.setText("+");
		addResultButton.setEnabled(false);
		addResultButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean isInList = false;
				for(String item : resultParameterList.getItems()) {
					if(item.equals(addResultParameter.getText())) {
						isInList = true;
						break;
					}
				}
				if(!isInList) {
					resultParameterList.add(addResultParameter.getText());
				}
				addResultParameter.setText("");
				addResultParameter.setFocus();
			}
		});

		// Parameters List
		label = new Label(container, SWT.NULL);

		resultParameterList = new List(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		resultParameterList.setLayoutData(gd);

		suppButton = new Button(container, SWT.PUSH);
		suppButton.setText("-");
		suppButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(resultParameterList.getItemCount() > 0) {
					if(resultParameterList.getSelectionIndex() != -1) {
						resultParameterList.remove(resultParameterList.getSelectionIndex());
					}
				}
			}
		});

		initialize();
		dialogChanged();
		setControl(container);
	}

	private IType chooseActionType() {
		if (project == null) {
			return null;
		}

		IJavaElement[] elements= new IJavaElement[] { project };
		IJavaSearchScope scope= SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog= new FilteredTypesSelectionDialog(getShell(), false,
				getWizard().getContainer(), scope, IJavaSearchConstants.CLASS);
		dialog.setTitle("Action Superclass Selection");
		dialog.setMessage("Choose a superclass for your action");
		dialog.setInitialPattern("*Action");

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
			actionPackage.setText(pack);
			actionType.setText("com.gwtplatform.dispatch.shared.ActionImpl");
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */
	private void dialogChanged() {
		if(actionPackage.getText().isEmpty()) {
			setMessage("Enter the action package");
			return;
		}
		if(actionText.getText().isEmpty()) {
			setMessage("Enter the action's name");
			return;
		}

		if(!addActionParameter.getText().isEmpty()) {
			String[] parameter = addActionParameter.getText().split(" ");

			if(parameter.length != 2) {
				addActionButton.setEnabled(false);
				setErrorMessage("Action parameter must be valid");
				return;
			}
			addActionButton.setEnabled(true);
		}
		else
			addActionButton.setEnabled(false);

		if(!addResultParameter.getText().isEmpty()) {
			String[] parameter = addResultParameter.getText().split(" ");

			if(parameter.length != 2) {
				addResultButton.setEnabled(false);
				setErrorMessage("Result parameter must be valid");
				return;
			}
			addResultButton.setEnabled(true);
		}
		else
			addResultButton.setEnabled(false);
		
		if(actionType.getText().isEmpty()) {
			setMessage("Enter the action's superclass");
			return;
		}
		else {
			try {
				IType type = project.findType(actionType.getText());
				if(type == null || !type.exists()) {
					setErrorMessage(actionType.getText() + " doesn't exist");
					return;
				}
				ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
				IType[] interfaces = hierarchy.getAllInterfaces();
				boolean isAction = false;
				for(IType inter : interfaces) {
					String test = getNameWithTypeParameters(inter);
					if(test.equals("com.gwtplatform.dispatch.shared.Action<R>")) {
						isAction = true;
						break;
					}
				}
				if(!isAction) {
					setErrorMessage(actionType.getText() + " doesn't implement ActionImpl");
					return;
				}
			} 
			catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	public String getActionName() {
		return actionText.getText();
	}

	public String getActionPackage() {
		return actionPackage.getText();
	}

	public String getSuperclass() {
		return actionType.getText();
	}

	public String[] getActionParameters() {
		return actionParameterList.getItems();
	}

	public String[] getResultParameters() {
		return resultParameterList.getItems();
	}

}
