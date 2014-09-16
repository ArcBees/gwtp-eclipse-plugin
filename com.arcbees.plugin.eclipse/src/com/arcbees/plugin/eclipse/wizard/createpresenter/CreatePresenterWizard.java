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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.arcbees.plugin.eclipse.domain.PresenterConfigModel;

public class CreatePresenterWizard extends Wizard {
    private CreatePresenterPage createPresenterPage;
    private PresenterConfigModel presenterConfigModel;

    public CreatePresenterWizard() {
        setWindowTitle("Create Presenter");
    }

    @Override
    public void addPages() {
        presenterConfigModel = new PresenterConfigModel();

        initProjectFocusedOn();

        createPresenterPage = new CreatePresenterPage(presenterConfigModel);
        addPage(createPresenterPage);
    }

    @Override
    public boolean performFinish() {
        boolean canBeFinished = createPresenterPage.isPageComplete();
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
    private void initProjectFocusedOn() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        try {
            initProjectFromSelectedFocus(window);
        } catch (Exception e) {
            // TODO display failed init
            e.printStackTrace();
        }
    }

    private void initProjectFromSelectedFocus(IWorkbenchWindow window) {
        if (window == null) {
            // TODO init error
            return;
        }

        IStructuredSelection selection;
        try {
            selection = (IStructuredSelection) window.getSelectionService().getSelection();
        } catch (Exception e1) {
            // TODO nothing is selected?
            e1.printStackTrace();
            return;
        }
        Object firstElement = selection.getFirstElement();
        IJavaProject project = null;
        IPackageFragment selectedPackage = null;
        if (firstElement instanceof IPackageFragment) {
            // when focused on a package in the project
            selectedPackage = (IPackageFragment) firstElement;
            project = selectedPackage.getJavaProject();
        } else if (firstElement instanceof ICompilationUnit) {
            // when focused on class, project comes back this way
            ICompilationUnit compilationUnit = (ICompilationUnit) firstElement;
            project = compilationUnit.getJavaProject();

            IPackageDeclaration declaration = null;
            try {
                declaration = compilationUnit.getPackageDeclarations()[0];
            } catch (JavaModelException e) {
                e.printStackTrace();
            }

            if (declaration != null) {
                try {
                    IPath path = declaration.getPath();
                    path = path.removeLastSegments(1);
                    selectedPackage = project.findPackageFragment(path);
                } catch (JavaModelException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("test");
        } else if (firstElement instanceof IAdaptable) {
            // when focused on the root, project comes back
            IProject iproject = (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);
            if (iproject != null) {
                project = JavaCore.create(iproject);
            }
        }

        if (selectedPackage == null) {
            // TODO warn about focus on package before presenter creation
        }

        presenterConfigModel.setJavaProject(project);
        presenterConfigModel.setSelectedPackage(selectedPackage);
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
        CreatePresenterTask.run(presenterConfigModel, monitor);
    }
}
