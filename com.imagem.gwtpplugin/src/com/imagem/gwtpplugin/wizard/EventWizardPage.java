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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

@Deprecated
public class EventWizardPage extends WizardPage {

	private IStructuredSelection selection;
	private Text eventPackage;
	private Text eventName;
	private Text addParameter;
	private Button addButton;
	private List parameterList;
	private Button hasHandlers;

	protected EventWizardPage(IStructuredSelection selection) {
		super("EventWizardPage");
		setTitle("Create an Event");
		setDescription("Create an Event and it's Handler interface");
		this.selection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 5;

		// Event Package
		Label label = new Label(container, SWT.NULL);
		label.setText("Event package:");

		eventPackage = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		eventPackage.setLayoutData(gd);
		eventPackage.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);

		// Event Name
		label = new Label(container, SWT.NULL);
		label.setText("Event name:");

		eventName = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		eventName.setLayoutData(gd);
		eventName.setFocus();
		eventName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);

		// Add Parameter
		label = new Label(container, SWT.NULL);
		label.setText("Parameters:");

		addParameter = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		addParameter.setLayoutData(gd);
		addParameter.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		addButton = new Button(container, SWT.PUSH);
		addButton.setText("+");
		addButton.setEnabled(false);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean isInList = false;
				for(String item : parameterList.getItems()) {
					if(item.equals(addParameter.getText())) {
						isInList = true;
						break;
					}
				}
				if(!isInList) {
					parameterList.add(addParameter.getText());
				}
				addParameter.setText("");
				addParameter.setFocus();
			}
		});

		// Parameters List
		label = new Label(container, SWT.NULL);

		parameterList = new List(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		parameterList.setLayoutData(gd);

		Button suppButton = new Button(container, SWT.PUSH);
		suppButton.setText("-");
		suppButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(parameterList.getItemCount() > 0) {
					if(parameterList.getSelectionIndex() != -1) {
						parameterList.remove(parameterList.getSelectionIndex());
					}
				}
			}
		});
		
		// HasHandlers
		label = new Label(container, SWT.NULL);

		hasHandlers = new Button(container, SWT.CHECK);
		hasHandlers.setText("HasHandlers class");
		hasHandlers.setSelection(false);

		label = new Label(container, SWT.NULL);

		initialize();
		dialogChanged();
		setControl(container);
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

			String pack = "";
			for(int i = 1; i < container.getProjectRelativePath().segmentCount(); i++) {
				if(!pack.isEmpty())
					pack += ".";
				pack += container.getProjectRelativePath().segment(i);
			}
			eventPackage.setText(pack);
		}
	}

	private void dialogChanged() {
		if(eventPackage.getText().isEmpty()) {
			setMessage("Enter the event package");
			return;
		}

		if(eventName.getText().isEmpty()) {
			setMessage("Enter a name for the Event");
			return;
		}
		if(!eventName.getText().endsWith("Event")) {
			setErrorMessage("Event File must ends by \"Event\"");
			return;
		}
		
		if(!addParameter.getText().isEmpty()) {
			String[] parameter = addParameter.getText().split(" ");

			if(parameter.length != 2) {
				addButton.setEnabled(false);
				setErrorMessage("Parameter must be valid");
				return;
			}
			addButton.setEnabled(true);
		}
		else
			addButton.setEnabled(false);

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
	
	public String getEventPackage() {
		return eventPackage.getText();
	}
	
	public String getEventName() {
		return eventName.getText().replaceAll("Event", "");
	}

	public String[] getParameters() {
		if(!addParameter.getText().isEmpty()) {
			parameterList.add(addParameter.getText());
			addParameter.setText("");
		}
		return parameterList.getItems();
	}
	
	public boolean hasHandlers() {
		return hasHandlers.getSelection();
	}
}
