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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

public class CreateProjectPage extends WizardPage {
    private Text projectName;
    private Text packageName;
    private Text workspacePath;
    private Text moduleName;
    private Text groupId;
    private Text artifactId;
    public CreateProjectPage() {
        super("wizardPageCreateProject");
        setMessage("Create a GWT-Platform project.");
        setPageComplete(false);
        
        setImageDescriptor(ResourceManager.getPluginImageDescriptor("com.arcbees.ide.plugin.eclipse", "icons/logo.png"));
        setTitle("GWTP Project Creation");
        setDescription("Create a GWT-Platform project.");
    }

    public void createControl(Composite parent) {
        parent.setTouchEnabled(true);
        Composite container = new Composite(parent, SWT.NULL);
        container.setTouchEnabled(true);

        setControl(container);
        container.setLayout(new GridLayout(1, false));
        
        Label lblProjectName = new Label(container, SWT.NONE);
        lblProjectName.setText("Project Name: 'My Project'");
        
        projectName = new Text(container, SWT.BORDER);
        GridData gd_projectName = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_projectName.widthHint = 516;
        projectName.setLayoutData(gd_projectName);
        projectName.setTouchEnabled(true);
        
        Label lblPackageName = new Label(container, SWT.NONE);
        lblPackageName.setText("Package Name: 'com.arcbees.project'");
        
        packageName = new Text(container, SWT.BORDER);
        packageName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        packageName.setTouchEnabled(true);
        
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
        
        Button btnPutInCustom = new Button(grpLocation, SWT.CHECK);
        btnPutInCustom.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        btnPutInCustom.setText("Put project in custom location:");
        new Label(grpLocation, SWT.NONE);
        
        workspacePath = new Text(grpLocation, SWT.BORDER);
        GridData gd_workspacePath = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_workspacePath.widthHint = 466;
        workspacePath.setLayoutData(gd_workspacePath);
        workspacePath.setEnabled(false);
        
        Button btnWorkspaceBrowse = new Button(grpLocation, SWT.NONE);
        btnWorkspaceBrowse.setEnabled(false);
        btnWorkspaceBrowse.setText("Browse");
    }
}
