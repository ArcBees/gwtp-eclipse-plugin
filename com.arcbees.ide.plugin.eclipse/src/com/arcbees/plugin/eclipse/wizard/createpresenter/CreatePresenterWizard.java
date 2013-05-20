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

        IProject project = getProjectFocusedOn();
        presenterConfigModel.setProject(project);
        
        createPresenterPage = new CreatePresenterPage(presenterConfigModel);
        addPage(createPresenterPage);
    }

    @Override
    public boolean performFinish() {
        return false;
    }

    // TODO when deeper in the project, for some reason firstElement type is different
    private IProject getProjectFocusedOn() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
            Object firstElement = selection.getFirstElement();
            if (firstElement instanceof IAdaptable) {
                IProject project = (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);
                if (project != null) {
                    IPath path = project.getFullPath();
                    System.out.println(path);
                    return project;
                }
            }
        }
        return null;
    }
}
