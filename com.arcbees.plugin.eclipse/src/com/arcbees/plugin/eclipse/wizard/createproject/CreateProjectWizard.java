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

package com.arcbees.plugin.eclipse.wizard.createproject;

import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.swt.widgets.Display;

import com.arcbees.plugin.eclipse.domain.Archetype;
import com.arcbees.plugin.eclipse.domain.ProjectConfigModel;

/**
 * TODO add required archetype properties to directory TODO setup the debugging for GWTP, add the default module to the
 * GPE plugin debugger
 */
public class CreateProjectWizard extends Wizard {
    private CreateProjectPage createProjectPage;
    private SelectArchetypePage selectArchetypePage;
    private ProjectConfigModel projectConfigModel;

    public CreateProjectWizard() {
        super();

        setNeedsProgressMonitor(true);
        setWindowTitle("Create GWTP Project");
    }

    @Override
    public void addPages() {
        projectConfigModel = new ProjectConfigModel(getShell());

        createProjectPage = new CreateProjectPage(projectConfigModel);
        selectArchetypePage = new SelectArchetypePage(projectConfigModel);

        addPage(createProjectPage);
        addPage(selectArchetypePage);
    }

    @Override
    public boolean performFinish() {
        boolean canBeFinished = projectConfigModel.canBeFinished();
        if (canBeFinished) {
            runGenerate();
        } else {
            warn("Looks like the form isn't completely filled out. Fill it out and hit finish.");
        }
        return canBeFinished;
    }

    public void runGenerate() {
        Job job = new Job("Generate Project") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                generate(monitor);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    private void generate(IProgressMonitor monitor) {
        createProject(monitor);
        
        // TODO have m2e update project
        
        // TODO add entrypoint to GWT plugin settings for project
    }

    private void createProject(IProgressMonitor monitor) {
        // project path is set in workspace.
        IPath location = new Path(projectConfigModel.getWorkspacePath());

        // import archetype
        org.apache.maven.archetype.catalog.Archetype archetype = getArchetype();

        // project settings
        String groupId = projectConfigModel.getGroupId();
        String artifactId = projectConfigModel.getArtifactId();
        String version = projectConfigModel.getVersion();
        String javaPackage = projectConfigModel.getPackageName();

        // config
        Properties properties = new Properties();
        properties.put("module", projectConfigModel.getModuleName());
        archetype.setProperties(properties);

        ProjectImportConfiguration configuration = new ProjectImportConfiguration();

        // create project
        IProjectConfigurationManager projectConfig = MavenPlugin.getProjectConfigurationManager();
        try {
            projectConfig.createArchetypeProjects(location, archetype, groupId, artifactId, version, javaPackage,
                    properties, configuration, monitor);
        } catch (CoreException e) {
            warn("Could not create maven project. Error: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Convert this project Archetype to maven archetype object
     */
    private org.apache.maven.archetype.catalog.Archetype getArchetype() {
        Archetype selected = projectConfigModel.getArchetypeSelected();

        org.apache.maven.archetype.catalog.Archetype archetype = new org.apache.maven.archetype.catalog.Archetype();
        archetype.setGroupId(selected.getGroupId());
        archetype.setArtifactId(selected.getArtifactId());
        archetype.setRepository(selected.getRepository());
        archetype.setVersion(selected.getVersion());
        archetype.setModelEncoding("UTF-8");
        archetype.setDescription(projectConfigModel.getDescription());

        return archetype;
    }

    private void warn(final String message) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                MessageDialog.openWarning(getShell(), "Warning", message);
            }
        });
    }
}
