/**
 * Copyright 2011 Les Syst�mes M�dicaux Imagem Inc.
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

package com.imagem.gwtpplugin.wizard2;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.src.shared.Model;

public class NewModelWizard extends Wizard implements INewWizard {

	private NewModelWizardPage newModelPage;
	private IStructuredSelection selection;

	public NewModelWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Model");
	}
	
	@Override
	public void addPages() {
		newModelPage = new NewModelWizardPage(selection);
		addPage(newModelPage);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		IProject project = newModelPage.getPackageFragmentRoot().getJavaProject().getProject();

		final Model model = new Model(newModelPage.getTypeName(), newModelPage.getPackageText());
		model.setFields(newModelPage.getFields());
		model.setGenerateEquals(newModelPage.generateEquals());
		
		try {
			SourceEditor.createProjectFile(project, model, true);
		} 
		catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
