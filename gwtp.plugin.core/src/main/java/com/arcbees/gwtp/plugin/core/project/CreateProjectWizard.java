/**
 * Copyright 2014 ArcBees Inc.
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

package com.arcbees.gwtp.plugin.core.project;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.arcbees.gwtp.plugin.core.StupidVelocityShim;
import com.arcbees.gwtp.plugin.core.util.zip.TemplateZipItem;
import com.arcbees.gwtp.plugin.core.util.zip.ZipTemplateIterator;

public class CreateProjectWizard extends Wizard implements INewWizard {
    private IWorkbench fWorkbench;

    public CreateProjectWizard() {
        setWindowTitle("Create GWTP Project");
        setHelpAvailable(false);
    }

    @Override
    public void addPages() {
        addPage(NameProjectPage.get());
        addPage(AddFeaturesPage.get());
        addPage(FeatureConfigPage.get());
    }

    public IWorkbench getWorkbench() {
        return fWorkbench;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        fWorkbench = workbench;
    }

    @Override
    public boolean performFinish() {
        runGenerate();
        return true;
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

    protected void selectAndReveal(IResource newResource) {
        BasicNewResourceWizard.selectAndReveal(newResource, fWorkbench.getActiveWorkbenchWindow());
    }

    private void createProject(IProgressMonitor monitor) throws Exception {
        StupidVelocityShim.setStripUnknownKeys(false);
        IProject project = ResourcesPlugin.getWorkspace().getRoot()
                .getProject(NameProjectPage.get().getProjectName());
        project.create(monitor);
        project.open(monitor);

        if (!project.getLocation().equals(NameProjectPage.get().getLocation())) {
            IProjectDescription description = project.getDescription();
            description.setLocation(NameProjectPage.get().getLocation());
            project.setDescription(description, monitor);
        }

        Map<String, Object> context = new HashMap<>();
        context.put("mavenGroup", NameProjectPage.get().getMavenGroup());
        context.put("mavenArtifactId", NameProjectPage.get().getMavenArtifactId());
        context.put("projectName", NameProjectPage.get().getProjectName());
        context.put("packageName", NameProjectPage.get().getPackageName());
        context.put("packageFolder", NameProjectPage.get().getPackageName().replace(".", "/"));

        AddFeaturesPage.get().fillContext(context);
        FeatureConfigPage.get().fillContext(context);

        String lastFolderPath = "";
        ZipTemplateIterator zipTemplateIterator = new ZipTemplateIterator(
                "/src/main/resources/templates/project/project.zip");
        for (TemplateZipItem item : zipTemplateIterator) {
            String path = StupidVelocityShim.evaluate(item.getName(), context);
            if (item.isFolder()) {
                String[] last = lastFolderPath.split("/");
                String[] current = path.split("/");
                if (current.length > last.length) {
                    StringBuilder pathBuilder = new StringBuilder();
                    for (int i = 0; i < current.length; i++) {
                        pathBuilder.append(current[i]).append("/");
                        if (last.length <= i) {
                            project.getFolder(new Path(pathBuilder.toString())).create(true, true, monitor);
                        }
                    }
                } else {
                    project.getFolder(new Path(path)).create(true, true, monitor);
                }
                lastFolderPath = path;
            } else {
                String fileContent = StupidVelocityShim.evaluate(item.getText(), context);
                if (!fileContent.trim().isEmpty()) {
                    IFile file = project.getFile(new Path(path));
                    file.create(new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)),
                            IResource.NONE, null);
                }
            }
        }
        zipTemplateIterator.closeCurrentStream();

        IProjectConfigurationManager mavenConfig = MavenPlugin.getProjectConfigurationManager();
        ResolverConfiguration resolverConfiguration = mavenConfig.getResolverConfiguration(project);
        mavenConfig.enableMavenNature(project, resolverConfiguration, monitor);
    }

    private void generate(IProgressMonitor monitor) {
        try {
            createProject(monitor);
        } catch (Exception e) {
            warn("Could not create gwtp project. Error: " + e.toString());
            e.printStackTrace();
        }
    }

    private void warn(final String message) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openWarning(getShell(), "Warning", message);
            }
        });
    }
}
