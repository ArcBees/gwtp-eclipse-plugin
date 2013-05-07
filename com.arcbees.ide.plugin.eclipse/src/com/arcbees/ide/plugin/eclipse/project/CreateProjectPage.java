/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.arcbees.ide.plugin.eclipse.project;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.eclipse.wb.swt.ResourceManager;

import com.arcbees.ide.plugin.eclipse.domain.ProjectConfigModel;
import com.arcbees.ide.plugin.eclipse.validators.ProjectNameValidator;
import com.arcbees.ide.plugin.eclipse.validators.PackageNameValidator;
import com.arcbees.ide.plugin.eclipse.validators.ModuleNameValidator;
import com.arcbees.ide.plugin.eclipse.validators.DirectoryExistsValidator;

public class CreateProjectPage extends WizardPage {
    private DataBindingContext m_bindingContext;
    private Text projectName;
    private Text packageName;
    private Text workspacePath;
    private Text moduleName;
    private Text groupId;
    private Text artifactId;
    private ProjectConfigModel projectConfigModel;
    private Button btnWorkspaceBrowse;

    public CreateProjectPage() {
        super("wizardPageCreateProject");
        setMessage("Create a GWT-Platform project.");
        setPageComplete(false);

        setImageDescriptor(ResourceManager.getPluginImageDescriptor("com.arcbees.ide.plugin.eclipse", "icons/logo.png"));
        setTitle("GWTP Project Creation");
        setDescription("Create a GWT-Platform project.");
    }

    public void createControl(final Composite parent) {
        projectConfigModel = new ProjectConfigModel();

        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new GridLayout(1, false));

        Label lblProjectName = new Label(container, SWT.NONE);
        lblProjectName.setText("Project Name: 'My Project'");

        projectName = new Text(container, SWT.BORDER);
        projectName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkProjectName();
            }
        });
        GridData gd_projectName = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_projectName.widthHint = 516;
        projectName.setLayoutData(gd_projectName);

        Label lblPackageName = new Label(container, SWT.NONE);
        lblPackageName.setText("Package Name: 'com.arcbees.project'");

        packageName = new Text(container, SWT.BORDER);
        packageName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        Label lblNewLabel = new Label(container, SWT.NONE);
        lblNewLabel.setText("Module Name: 'Project'");

        moduleName = new Text(container, SWT.BORDER);
        GridData gd_moduleName = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_moduleName.widthHint = 550;
        moduleName.setLayoutData(gd_moduleName);

        Group grpMaven = new Group(container, SWT.NONE);
        GridData gd_grpMaven = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_grpMaven.widthHint = 571;
        grpMaven.setLayoutData(gd_grpMaven);
        grpMaven.setLayout(new GridLayout(1, false));
        grpMaven.setText("Maven");

        Label lblGroupid = new Label(grpMaven, SWT.NONE);
        lblGroupid.setText("GroupId: 'com.arcbees.project'");

        groupId = new Text(grpMaven, SWT.BORDER);
        GridData gd_groupId = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_groupId.widthHint = 556;
        groupId.setLayoutData(gd_groupId);

        Label lblArtifactid = new Label(grpMaven, SWT.NONE);
        lblArtifactid.setText("ArtifactId: 'myproject'");

        artifactId = new Text(grpMaven, SWT.BORDER);
        artifactId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        Group grpLocation = new Group(container, SWT.NONE);
        grpLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        grpLocation.setText("Location");
        grpLocation.setLayout(new GridLayout(2, false));

        Button cbCustomLocation = new Button(grpLocation, SWT.CHECK);
        cbCustomLocation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button cb = (Button) e.getSource();
                boolean selected = cb.getSelection();
                workspacePath.setEnabled(selected);
                btnWorkspaceBrowse.setEnabled(selected);

                // Reset workspace path when disabled
                if (!selected) {
                    checkProjectName();
                }
            }
        });
        cbCustomLocation.setText("Put project in custom location:");
        new Label(grpLocation, SWT.NONE);

        workspacePath = new Text(grpLocation, SWT.BORDER);
        GridData gd_workspacePath = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_workspacePath.widthHint = 466;
        workspacePath.setLayoutData(gd_workspacePath);
        workspacePath.setEnabled(false);

        btnWorkspaceBrowse = new Button(grpLocation, SWT.NONE);
        btnWorkspaceBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dirDialog = new DirectoryDialog(parent.getShell());
                dirDialog.setText("Select a new project directory");
                String selectedDir = dirDialog.open();
                workspacePath.setText(selectedDir);
            }
        });
        btnWorkspaceBrowse.setEnabled(false);
        btnWorkspaceBrowse.setText("Browse");
        m_bindingContext = initDataBindings();

        // Observe input changes and add validator decorators
        observeBindingChanges();
    }

    private void observeBindingChanges() {
        IObservableList bindings = m_bindingContext.getValidationStatusProviders();
        for (Object o : bindings) {
            Binding binding = (Binding) o;
            
            // Validator feedback control
            ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
            
            binding.getTarget().addChangeListener(new IChangeListener() {
                @Override
                public void handleChange(ChangeEvent event) {
                    checkBindingValidationStatus();
                }
            });
        }
    }

    private void checkProjectName() {
        String name = projectName.getText().trim();

        // No zero length property names
        if (name.trim().length() == 0) {
            return;
        }

        // Pre-setup the project for checking
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);

        // Display error when project exists or remove error when it doesn't
        if (project.exists()) {
            setMessage("The '" + name + "' project name already exists.", IMessageProvider.ERROR);
        } else {
            setMessage(null);
        }

        // Convert spaces to file friendly characters
        name = name.replace(" ", "_");

        // Reflect the projects workspace path with the created project name
        String basePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
        basePath += "/" + name;
        workspacePath.setText(basePath);
    }

    /**
     * Check all the bindings validators for OK status.
     * 
     * TODO add validators to each field.
     */
    private void checkBindingValidationStatus() {
        IObservableList bindings = m_bindingContext.getValidationStatusProviders();

        boolean success = true;
        for (Object o : bindings) {
            Binding b = (Binding) o;
            IObservableValue status = b.getValidationStatus();
            IStatus istatus = (IStatus) status.getValue();
            System.out.println("isStatus=" + istatus);
            if (!istatus.isOK()) {
                success = false;
            }
        }

        // All statuses passed, enable next button.
        setPageComplete(success);
    }
    protected DataBindingContext initDataBindings() {
        DataBindingContext bindingContext = new DataBindingContext();
        //
        IObservableValue observeTextProjectNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(projectName);
        IObservableValue bytesProjectConfigModelgetProjectNameObserveValue = PojoProperties.value("bytes").observe(projectConfigModel.getProjectName());
        UpdateValueStrategy strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new ProjectNameValidator());
        bindingContext.bindValue(observeTextProjectNameObserveWidget, bytesProjectConfigModelgetProjectNameObserveValue, strategy, null);
        //
        IObservableValue observeTextPackageNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(packageName);
        IObservableValue bytesProjectConfigModelgetPackageNameObserveValue = PojoProperties.value("bytes").observe(projectConfigModel.getPackageName());
        UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
        strategy_1.setBeforeSetValidator(new PackageNameValidator());
        bindingContext.bindValue(observeTextPackageNameObserveWidget, bytesProjectConfigModelgetPackageNameObserveValue, strategy_1, null);
        //
        IObservableValue observeTextModuleNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(moduleName);
        IObservableValue bytesProjectConfigModelgetModuleNameObserveValue = PojoProperties.value("bytes").observe(projectConfigModel.getModuleName());
        UpdateValueStrategy strategy_2 = new UpdateValueStrategy();
        strategy_2.setBeforeSetValidator(new ModuleNameValidator());
        bindingContext.bindValue(observeTextModuleNameObserveWidget, bytesProjectConfigModelgetModuleNameObserveValue, strategy_2, null);
        //
        IObservableValue observeTextGroupIdObserveWidget = WidgetProperties.text(SWT.Modify).observe(groupId);
        IObservableValue bytesProjectConfigModelgetGroupIdObserveValue = PojoProperties.value("bytes").observe(projectConfigModel.getGroupId());
        UpdateValueStrategy strategy_3 = new UpdateValueStrategy();
        strategy_3.setBeforeSetValidator(new PackageNameValidator());
        bindingContext.bindValue(observeTextGroupIdObserveWidget, bytesProjectConfigModelgetGroupIdObserveValue, strategy_3, null);
        //
        IObservableValue observeTextArtifactIdObserveWidget = WidgetProperties.text(SWT.Modify).observe(artifactId);
        IObservableValue bytesProjectConfigModelgetArtifactIdObserveValue = PojoProperties.value("bytes").observe(projectConfigModel.getArtifactId());
        UpdateValueStrategy strategy_4 = new UpdateValueStrategy();
        strategy_4.setBeforeSetValidator(new ProjectNameValidator());
        bindingContext.bindValue(observeTextArtifactIdObserveWidget, bytesProjectConfigModelgetArtifactIdObserveValue, strategy_4, null);
        //
        IObservableValue observeTextWorkspacePathObserveWidget = WidgetProperties.text(SWT.Modify).observe(workspacePath);
        IObservableValue bytesProjectConfigModelgetWorkspacePathObserveValue = PojoProperties.value("bytes").observe(projectConfigModel.getWorkspacePath());
        UpdateValueStrategy strategy_5 = new UpdateValueStrategy();
        strategy_5.setBeforeSetValidator(new DirectoryExistsValidator());
        bindingContext.bindValue(observeTextWorkspacePathObserveWidget, bytesProjectConfigModelgetWorkspacePathObserveValue, strategy_5, null);
        //
        return bindingContext;
    }
}
