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

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IPackageFragmentRoot;
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

	private NewActionWizardPage page;
	private IStructuredSelection selection;

	public NewActionWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Action");
	}
	
	@Override
	public void addPages() {
		page = new NewActionWizardPage(selection);
		addPage(page);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		Action action = null;
		Result result = null;
		ActionHandler actionHandler = null;
		HandlerModule handlerModule = null;
		try {
			IPackageFragmentRoot root = page.getPackageFragmentRoot();
			
			// Result
			result = new Result(root, page.getResultPackageText(), page.getResultTypeName());
			result.createSerializationField();
			result.createSerializationConstructor();
			
			Field[] resultFields = page.getResultFields();
			IField[] fields = new IField[resultFields.length];
			for(int i = 0; i < resultFields.length; i++) {
				fields[i] = result.createField(resultFields[i].getType(), resultFields[i].getName());
			}
			result.createConstructor(fields);
			for(IField field : fields) {
				result.createGetterMethod(field);
			}
			
			// Action
			IType actionSuperclass = page.getJavaProject().findType(page.getActionSuperclass());
			
			action = new Action(root, page.getPackageText(), page.getTypeName(), actionSuperclass, result.getType());
			action.createSerializationField();
			action.createSerializationConstructor();
			
			Field[] actionFields = page.getActionFields();
			fields = new IField[actionFields.length];
			for(int i = 0; i < actionFields.length; i++) {
				fields[i] = action.createField(actionFields[i].getType(), actionFields[i].getName());
			}
			action.createConstructor(fields);
			for(IField field : fields) {
				action.createGetterMethod(field);
			}
			
			// ActionHandler
			actionHandler = new ActionHandler(root, page.getActionHandlerPackageText(), page.getActionHandlerTypeName(), action.getType(), result.getType());
			actionHandler.createConstructor();
			actionHandler.createExecuteMethod(action.getType(), result.getType());
			actionHandler.createUndoMethod(action.getType(), result.getType());
			actionHandler.createActionTypeGetterMethod(action.getType());
			
			// HandlerModule
			handlerModule = new HandlerModule(root, page.getHandlerModule());
			handlerModule.createBinder(action.getType(), actionHandler.getType());
			
			// Committing
			if(action != null) action.commit();
			if(result != null) result.commit();
			if(actionHandler != null) actionHandler.commit();
			if(handlerModule != null) handlerModule.commit();
		} 
		catch (JavaModelException e) {
			e.printStackTrace();

			try {
				if(action != null) action.discard();
				if(result != null) result.discard();
				if(actionHandler != null) actionHandler.discard();
				if(handlerModule != null) handlerModule.discard();
			}
			catch (JavaModelException e1) {	}
			
			return false;
		}
		
		return true;
	}

}
