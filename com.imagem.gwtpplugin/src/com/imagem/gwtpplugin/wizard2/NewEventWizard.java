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

package com.imagem.gwtpplugin.wizard2;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.src.client.event.Event;
import com.imagem.gwtpplugin.projectfile.src.client.event.Handler;
import com.imagem.gwtpplugin.projectfile.src.client.event.HasHandlers;

public class NewEventWizard extends Wizard implements INewWizard {

	private NewEventWizardPage newEventPage;
	private IStructuredSelection selection;

	public NewEventWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Event");
	}
	
	@Override
	public void addPages() {
		newEventPage = new NewEventWizardPage(selection);
		addPage(newEventPage);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		String name = newEventPage.getTypeName().replace("Event", "");
		IProject project = newEventPage.getPackageFragmentRoot().getJavaProject().getProject();
		
		final Event event = new Event(name, newEventPage.getPackageText());
		final Handler handler = new Handler(name, newEventPage.getPackageText());
		final HasHandlers hasHandler = new HasHandlers(name, newEventPage.getPackageText());
		
		event.setFields(newEventPage.getFields());
		event.setHandlers(newEventPage.hasHandlers());
		
		try {
			SourceEditor.createProjectFile(project, event, true);
			SourceEditor.createProjectFile(project, handler, true);
			if(newEventPage.hasHandlers())
				SourceEditor.createProjectFile(project, hasHandler, true);
		} 
		catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}
