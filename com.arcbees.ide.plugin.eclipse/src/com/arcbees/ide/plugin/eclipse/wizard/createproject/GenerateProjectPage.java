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

package com.arcbees.ide.plugin.eclipse.wizard.createproject;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.maven.archetype.catalog.Archetype;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.arcbees.ide.plugin.eclipse.domain.ProjectConfigModel;
import com.arcbees.ide.plugin.eclipse.util.ProgressMonitor;

public class GenerateProjectPage extends WizardPage {
    private ProjectConfigModel projectConfigModel;
    private ProgressMonitor generateMonitor;
    private boolean loading;

    public GenerateProjectPage(ProjectConfigModel projectConfigModel) {
        super("wizardPageGenerateProject");
        this.projectConfigModel = projectConfigModel;

        setTitle("Generate Project");
        setDescription("Confirm project configuration and generate it.");
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);

        generateMonitor = new ProgressMonitor(container);
    }
    
    // TODO
    public boolean canBeFinished() {
        runGeneration();
        return false;
    }
    
    public void runGeneration() {
        //runMonitor();
        runGenerate();
    }
    
    private void runMonitor() {
        Job job = new Job("Fetching Archetypes...") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                String doing = "Fetching Archetyes...";

                monitor.beginTask(doing, 100);
                generateMonitor.beginTask(doing, 100);

                loading = true;
                do {
                    try {
                        TimeUnit.MILLISECONDS.sleep(25);

                        monitor.worked(1);
                        generateMonitor.worked(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        loading = false;
                        return Status.CANCEL_STATUS;
                    }
                } while (loading);
                
                if (!loading) {
                    generateMonitor.reset();
                    generateMonitor.setVisible(false);
                }

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    private void runGenerate() {
        Job job = new Job("Fetch Request") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                generate();
                loading = false;
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    private void generate() {
        // project path
        IPath location = projectConfigModel.getLocation();
        
        // import archetype
        Archetype archetype = getArchetype();
        
        // project settings
        String groupId = projectConfigModel.getGroupId();
        String artifactId = projectConfigModel.getArtifactId();
        String version = projectConfigModel.getVersion();
        String javaPackage = projectConfigModel.getPackageName();
        
        // config
        Properties properties = new Properties();
        ProjectImportConfiguration configuration = new ProjectImportConfiguration();

        System.out.println("started generation");
        
        IProjectConfigurationManager projectConfig = MavenPlugin.getProjectConfigurationManager();
        try {
            projectConfig.createArchetypeProjects(location, archetype, groupId, artifactId, version, javaPackage,
                    properties, configuration, generateMonitor);
        } catch (CoreException e) {
            e.printStackTrace();
        }

        System.out.println("finished generation");
    }

    private Archetype getArchetype() {
        com.arcbees.ide.plugin.eclipse.domain.Archetype selected = projectConfigModel.getArchetypeSelected();
        
        Archetype archetype = new Archetype();

        archetype.setGroupId(selected.getGroupId());
        archetype.setArtifactId(selected.getArtifactId());
        archetype.setRepository(selected.getRepository());
        archetype.setVersion(selected.getRepository());

        archetype.setModelEncoding("UTF-8");
        archetype.setDescription(projectConfigModel.getDescription());

        return archetype;
    }
}
