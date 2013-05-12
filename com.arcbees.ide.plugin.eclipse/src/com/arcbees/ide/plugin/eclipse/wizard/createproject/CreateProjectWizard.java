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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;

import com.arcbees.ide.plugin.eclipse.domain.Archetype;
import com.arcbees.ide.plugin.eclipse.domain.ProjectConfigModel;

/**
 * TODO add required archetype properties to directory
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
        projectConfigModel = new ProjectConfigModel();
        
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
            // TODO status or display why
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
        // project path
        IPath location = projectConfigModel.getLocation();
        
        // import archetype
        org.apache.maven.archetype.catalog.Archetype archetype = getArchetype();
        
        // project settings
        String groupId = projectConfigModel.getGroupId();
        String artifactId = projectConfigModel.getArtifactId();
        String version = projectConfigModel.getVersion();
        String javaPackage = projectConfigModel.getPackageName();
        
        // config
        Properties properties = new Properties();

        
        // TODO need to get from directory and add to directory the required properties.
        properties.put("module", projectConfigModel.getModuleName());
        archetype.setProperties(properties);
        // TODO 
        
        ProjectImportConfiguration configuration = new ProjectImportConfiguration();

        IProjectConfigurationManager projectConfig = MavenPlugin.getProjectConfigurationManager();
        try {
            projectConfig.createArchetypeProjects(location, archetype, groupId, artifactId, version, javaPackage,
                    properties, configuration, monitor);
        } catch (CoreException e) {
            // TODO display error
            e.printStackTrace();
        }
    }

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
}
