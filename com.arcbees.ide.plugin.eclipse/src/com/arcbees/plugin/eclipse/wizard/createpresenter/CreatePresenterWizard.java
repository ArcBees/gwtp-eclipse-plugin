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

package com.arcbees.plugin.eclipse.wizard.createpresenter;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.arcbees.plugin.eclipse.domain.PresenterConfigModel;
import com.arcbees.plugin.template.create.presenter.CreateNestedPresenter;
import com.arcbees.plugin.template.domain.presenter.CreatedNestedPresenter;
import com.arcbees.plugin.template.domain.presenter.NestedPresenterOptions;
import com.arcbees.plugin.template.domain.presenter.PresenterOptions;
import com.arcbees.plugin.template.utils.FetchTemplate;
import com.arcbees.plugin.template.utils.FetchTemplates;

public class CreatePresenterWizard extends Wizard {
    private static final String URL_NESTED = "https://raw.github.com/ArcBees/IDE-Templates/1.0.0/src/main/resources/com/arcbees/plugin/template/presenter/nested";

    private CreatePresenterPage createPresenterPage;
    private PresenterConfigModel presenterConfigModel;

    public CreatePresenterWizard() {
        setWindowTitle("Create Presenter");
    }

    @Override
    public void addPages() {
        presenterConfigModel = new PresenterConfigModel();

        IJavaProject project = getProjectFocusedOn();
        presenterConfigModel.setProject(project);

        createPresenterPage = new CreatePresenterPage(presenterConfigModel);
        addPage(createPresenterPage);
    }

    @Override
    public boolean performFinish() {
        // TODO add finish logic
        boolean canBeFinished = true;
        if (canBeFinished) {
            runGenerate();
        } else {
            // TODO status or display why
        }
        return canBeFinished;
    }

    /**
     * When focused on the project, save that for use when creating units.
     */
    private IJavaProject getProjectFocusedOn() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        IJavaProject project = null;
        try {
            project = getProject(window);
        } catch (Exception e) {
        }

        return project;
    }

    private IJavaProject getProject(IWorkbenchWindow window) {
        if (window == null) {
            return null;
        }
        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
        Object firstElement = selection.getFirstElement();
        IJavaProject project = null;

        if (firstElement instanceof IPackageFragment) {
            // when focused on a package in the project
            IPackageFragment packageFrag = (IPackageFragment) firstElement;
            project = packageFrag.getJavaProject();
        } else if (firstElement instanceof ICompilationUnit) {
            ICompilationUnit compilationUnit = (ICompilationUnit) firstElement;
            // TODO verify
            project = compilationUnit.getJavaProject();
        } else if (firstElement instanceof IAdaptable) {
            // when focused on the root, project comes back
            IProject iproject = (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);
            if (iproject != null) {
                project = JavaCore.create(iproject);
            }
        }
        return project;
    }

    public void runGenerate() {
        Job job = new Job("Generate Presenter") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                generate(monitor);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    private void generate(IProgressMonitor monitor) {
        try {
            createDir();
        } catch (IOException e) {
            // TODO display error
            e.printStackTrace();
            return;
        }

        fetchTemplates();
    }

    private void createDir() throws IOException {
        // TODO dir property is missing, fix
        String dir = presenterConfigModel.getPath();
        FileUtils.forceMkdir(new File(dir));
    }

    private void fetchTemplates() {
        // TODO fetch logic
        FetchTemplates fetchedTemplates = fetchNestedPresenter();
        Map<String, FetchTemplate> fetchedPaths = fetchedTemplates.getPathsToFetch();

        createFiles(fetchedPaths);
    }

    private FetchTemplates fetchNestedPresenter() {
        FetchTemplates fetchTemplates = new FetchTemplates();
        fetchTemplates.addPath(URL_NESTED + "/__name__Module.java.vm");
        fetchTemplates.addPath(URL_NESTED + "/__name__Presenter.java.vm");
        fetchTemplates.addPath(URL_NESTED + "/__name__UiHandlers.java.vm");
        fetchTemplates.addPath(URL_NESTED + "/__name__View.java.vm");
        fetchTemplates.addPath(URL_NESTED + "/__name__View.ui.xml.vm");
        fetchTemplates.run();

        return fetchTemplates;
    }

    private void createFiles(Map<String, FetchTemplate> fetchedPaths) {
        Set<String> paths = fetchedPaths.keySet();
        for (String path : paths) {
            createFile(path, fetchedPaths.get(path));
        }
    }

    private void createFile(String path, FetchTemplate fetchedTemplate) {
        String filename = path.replaceAll("__.*?__", "");
        System.out.println("filename=" + filename);
        // TODO confirm things look correct
    }

    private void process() {
        PresenterOptions presenterOptions = new PresenterOptions();
        presenterOptions.setPackageName("com.arcbees.project.client.app");
        presenterOptions.setName("MyAppHome");

        NestedPresenterOptions nestedPresenterOptions = new NestedPresenterOptions();
        nestedPresenterOptions.setCodeSplit(true);

        CreatedNestedPresenter created = CreateNestedPresenter.run(presenterOptions, nestedPresenterOptions);

        System.out.println("test");
    }

    private void createFile() {

    }
}
