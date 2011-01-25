/**
 * Copyright 2011 Les Systèmes Médicaux Imagem Inc.
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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.imagem.gwtpplugin.Activator;
import com.imagem.gwtpplugin.project.ProjectCreator;

public class ProjectWizard extends Wizard implements INewWizard {

	private ProjectWizardPage page;

	public ProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New GWTP Project");

		try {
			URL url = new URL(Activator.getDefault().getBundle().getEntry("/"), "icons/logo.png");
			setDefaultPageImageDescriptor(ImageDescriptor.createFromURL(url));
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void addPages() {
		page = new ProjectWizardPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		ProjectCreator.createProject(page.getProjectName(), page.getProjectPackage(), page.getProjectLocation());
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {}

}
