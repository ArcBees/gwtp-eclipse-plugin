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

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.imagem.gwtpplugin.projectfile.Field;
import com.imagem.gwtpplugin.projectfile.src.server.ActionHandler;
import com.imagem.gwtpplugin.projectfile.src.server.guice.HandlerModule;
import com.imagem.gwtpplugin.projectfile.src.shared.Action;
import com.imagem.gwtpplugin.projectfile.src.shared.Result;

public class NewActionWizard extends Wizard implements INewWizard {

	private NewActionWizardPage newActionPage;
	private IStructuredSelection selection;

	public NewActionWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Action");
	}
	
	@Override
	public void addPages() {
		newActionPage = new NewActionWizardPage(selection);
		addPage(newActionPage);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		/*String name = newActionPage.getTypeName().replace("Event", "");
		String handlerPaclage = newActionPage.getPackageText().replaceAll("shared", "server").replaceAll("action", "handler");
		String[] serverModule = newActionPage.getServerModule().split("\\.");
		IProject project = newActionPage.getPackageFragmentRoot().getJavaProject().getProject();
		
		final Action action = new Action(name, newActionPage.getPackageText());
		final Result result = new Result(name, newActionPage.getPackageText());
		final ActionHandler actionHandler = new ActionHandler(project.getName(), name, handlerPaclage, newActionPage.getPackageText());
		final HandlerModule handlerModule = new HandlerModule(getGuicePackage(serverModule));

		action.setSuperclass(newActionPage.getSuperclass());
		action.setFields(newActionPage.getActionFields());

		result.setFields(newActionPage.getResultFields());
		
		handlerModule.setName(getServerModuleName(serverModule));
		handlerModule.setAction(action);
		handlerModule.setActionHandler(actionHandler);
		
		try {
			SourceEditor.createProjectFile(project, action, true);
			SourceEditor.createProjectFile(project, result, true);
			SourceEditor.createProjectFile(project, actionHandler, true);
			SourceEditor.updateProjectFile(project, handlerModule);*/
		try {
			// Result
			Result result = new Result(newActionPage.getPackageFragmentRoot(), newActionPage.getResultPackageText(), newActionPage.getResultTypeName());
			result.createSerializationField();
			result.createSerializationConstructor();
			
			Field[] resultFields = newActionPage.getResultFields();
			IField[] fields = new IField[resultFields.length];
			for(int i = 0; i < resultFields.length; i++) {
				fields[i] = result.createField(resultFields[i].getType(), resultFields[i].getName());
			}
			result.createConstructor(fields);
			for(IField field : fields) {
				result.createGetterMethod(field);
			}
			
			// Action
			IType actionSuperclass = newActionPage.getJavaProject().findType(newActionPage.getActionSuperclass());
			
			Action action = new Action(newActionPage.getPackageFragmentRoot(), newActionPage.getPackageText(), newActionPage.getTypeName(), actionSuperclass, result.getType());
			action.createSerializationField();
			action.createSerializationConstructor();
			
			Field[] actionFields = newActionPage.getActionFields();
			fields = new IField[actionFields.length];
			for(int i = 0; i < actionFields.length; i++) {
				fields[i] = action.createField(actionFields[i].getType(), actionFields[i].getName());
			}
			action.createConstructor(fields);
			for(IField field : fields) {
				action.createGetterMethod(field);
			}
			
			// ActionHandler
			ActionHandler actionHandler = new ActionHandler(newActionPage.getPackageFragmentRoot(), newActionPage.getActionHandlerPackageText(), newActionPage.getActionHandlerTypeName(), action.getType(), result.getType());
			actionHandler.createConstructor();
			actionHandler.createExecuteMethod(action.getType(), result.getType());
			actionHandler.createUndoMethod(action.getType(), result.getType());
			actionHandler.createActionTypeGetterMethod(action.getType());
			
			HandlerModule handlerModule = new HandlerModule(newActionPage.getPackageFragmentRoot(), newActionPage.getHandlerModule());
			handlerModule.createBinder(action.getType(), actionHandler.getType());
		} 
		catch (JavaModelException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/*private String getGuicePackage(String[] serverModule) {
		String guicePackage = "";
		for(int i = 0; i < serverModule.length - 1; i++) {
			if(!guicePackage.isEmpty())
				guicePackage += ".";
			guicePackage += serverModule[i];
		}
		return guicePackage;
	}
	
	private String getServerModuleName(String[] serverModule) {
		return serverModule[serverModule.length - 1];
	}*/

}
