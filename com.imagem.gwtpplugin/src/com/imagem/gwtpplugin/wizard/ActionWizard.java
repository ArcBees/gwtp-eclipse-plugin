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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.src.server.guice.ServerModule;
import com.imagem.gwtpplugin.projectfile.src.server.handler.ActionHandler;
import com.imagem.gwtpplugin.projectfile.src.shared.action.Action;
import com.imagem.gwtpplugin.projectfile.src.shared.action.Result;

public class ActionWizard extends Wizard implements INewWizard {

	private ActionWizardPage page;
	private IStructuredSelection selection;
	private IProject project;
	private IPath basePath;

	public ActionWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Action");
	}

	@Override
	public void addPages() {
		page = new ActionWizardPage(selection);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		String name = page.getActionName();
		String actionPackage = page.getActionPackage();
		String handlerPaclage = actionPackage.replaceAll("shared.action", "server.handler");
		String guicePackage = SourceEditor.toPackage(basePath.append("server").append(".guice"));
		
		final Action action = new Action(name, actionPackage);
		final Result result = new Result(name, actionPackage);
		final ActionHandler actionHandler = new ActionHandler(project.getName(), name, handlerPaclage, actionPackage);
		final ServerModule serverModule = new ServerModule(guicePackage);

		action.setSecure(page.isSecureAction());
		action.setParameters(SourceEditor.getVariables(project, basePath, page.getActionParameters()));

		result.setParameters(SourceEditor.getVariables(project, basePath, page.getResultParameters()));
		
		//actionHandler.setSecureAction(!page.useDefaultActionHandler());
		
		serverModule.setAction(action);
		serverModule.setActionHandler(actionHandler);

		try {
			SourceEditor.createProjectFile(project, action, true);
			SourceEditor.createProjectFile(project, result, true);
			SourceEditor.createProjectFile(project, actionHandler, true);
			SourceEditor.updateProjectFile(project, serverModule);
		} 
		catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		if(selection != null && !selection.isEmpty()) {
			if (selection.size() > 1)
				return;
			Object firstElement = selection.getFirstElement();
			IResource resource = null;
			if (firstElement instanceof IResource) {
				// Is it a IResource ?
				resource = (IResource) firstElement;
			}
			else if (firstElement instanceof IAdaptable) {
				// Is it a IResource adaptable ?
				IAdaptable adaptable = (IAdaptable) firstElement;
				resource = (IResource) adaptable.getAdapter(IResource.class);
			}
			project = resource.getProject();
			basePath = SourceEditor.getBasePath(resource.getFullPath());
		}
	}
}
