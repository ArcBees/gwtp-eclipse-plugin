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

import java.net.URI;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Michael Renaud
 *
 */
public class NewProjectWizardPage extends WizardPage {

	private Text projectName;
	private Text projectPackage;
	private Button locationWorkspace;
	private Composite locationContainer;
	private Text location;
	private Button browse;
	private Label locationLabel;
	private Button useGAE;

	protected NewProjectWizardPage() {
		super("ProjectWizardPage");
		setTitle("Create a GWTP Project");
		setDescription("Create a GWTP project in the workspace or in an external location");
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		layout.verticalSpacing = 5;
		
		// Project Name
		Label label = new Label(container, SWT.NULL);
		label.setText("Project name:");
		
		projectName = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectName.setLayoutData(gd);
		projectName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		// Project Package
		label = new Label(container, SWT.NULL);
		label.setText("Package: (e.g. com.example.myproject)");
		
		projectPackage = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		projectPackage.setLayoutData(gd);
		projectPackage.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		// Location
		Group group = new Group(container, SWT.NULL);
		group.setText("Location");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		layout = new GridLayout();
		group.setLayout(layout);
		layout.numColumns = 1;
		layout.verticalSpacing = 5;
		
		locationWorkspace = new Button(group, SWT.RADIO);
		locationWorkspace.setText("Create new project in workspace");
		locationWorkspace.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(locationWorkspace.getSelection()) {
					locationLabel.setEnabled(false);
					location.setEnabled(false);
					browse.setEnabled(false);
				}
				else {
					locationLabel.setEnabled(true);
					location.setEnabled(true);
					browse.setEnabled(true);
				}
				dialogChanged();
			}
		});
		
		Button locationOther = new Button(group, SWT.RADIO);
		locationOther.setText("Create new project in:");
		
		locationContainer = new Composite(group, SWT.NULL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		locationContainer.setLayoutData(gd);
		layout = new GridLayout();
		locationContainer.setLayout(layout);
		layout.numColumns = 3;
		
		locationLabel = new Label(locationContainer, SWT.NULL);
		locationLabel.setText("Directory:");
		
		location = new Text(locationContainer, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		location.setLayoutData(gd);
		
		browse = new Button(locationContainer, SWT.PUSH);
		browse.setText("Browse...");
		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setMessage("Choose a directory for the project contents:");
				location.setText(dialog.open());
				
				dialogChanged();
			}
		});
		
		// GAE
		useGAE = new Button(container, SWT.CHECK);
		useGAE.setText("Use App Engine");
		
		// TODO Google SDKs

		setDefaultValues();
		dialogChanged();
		setControl(container);
	}
	
	private void setDefaultValues() {
		String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
		
		locationWorkspace.setSelection(true);
		locationLabel.setEnabled(false);
		location.setEnabled(false);
		location.setText(workspace);
		browse.setEnabled(false);
	}

	private void dialogChanged() {
		if(projectName.getText().isEmpty()) {
			setMessage("Enter a name for the project");
			setErrorMessage(null);
			setPageComplete(false);
			return;
		}
		IStatus status = ResourcesPlugin.getWorkspace().validateName(getProjectName(), IResource.PROJECT);
		if(!status.isOK()) {
			setErrorMessage(status.getMessage());
			setPageComplete(false);
			return;
		}
		if(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName.getText()).exists()) {
			setErrorMessage("A project with this name already exists.");
			setPageComplete(false);
			return;
		}
		
		if(projectPackage.getText().isEmpty()) {
			setMessage("Enter a package name");
			setErrorMessage(null);
			setPageComplete(false);
			return;
		}
		if(projectPackage.getText().startsWith(".") || projectPackage.getText().endsWith(".")) {
			setErrorMessage("A package name cannot start or end with a dot");
			setPageComplete(false);
			return;
		}
		setMessage(null);
		setErrorMessage(null);
		setPageComplete(true);
	}
	
	public String getProjectName() {
		return projectName.getText();
	}
	
	public String getProjectPackage() {
		return projectPackage.getText();
	}
	
	public URI getProjectLocation() {
		if(!locationWorkspace.getSelection()) {
			return new Path(location.getText()).toFile().toURI();
		}
		return null;
	}

	public boolean useGAE() {
		return useGAE.getSelection();
	}

}
