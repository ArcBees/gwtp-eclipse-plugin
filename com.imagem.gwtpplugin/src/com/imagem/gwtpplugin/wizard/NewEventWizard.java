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
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.imagem.gwtpplugin.projectfile.Field;
import com.imagem.gwtpplugin.projectfile.src.client.event.Event;
import com.imagem.gwtpplugin.projectfile.src.client.event.Handler;
import com.imagem.gwtpplugin.projectfile.src.client.event.HasHandlers;

public class NewEventWizard extends Wizard implements INewWizard {

	private NewEventWizardPage page;
	private IStructuredSelection selection;

	public NewEventWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Event");
	}
	
	@Override
	public void addPages() {
		page = new NewEventWizardPage(selection);
		addPage(page);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		Event event = null;
		Handler handler = null;
		HasHandlers hasHandlers = null;
		try {
			IPackageFragmentRoot root = page.getPackageFragmentRoot();
			
			handler = new Handler(root, page.getHandlerPackageText(), page.getHandlerTypeName());
		
			event = new Event(root, page.getPackageText(), page.getTypeName(), handler.getType());
			event.createTypeField(handler.getType());
			
			Field[] eventFields = page.getFields();
			IField[] fields = new IField[eventFields.length];
			for(int i = 0; i < eventFields.length; i++) {
				fields[i] = event.createField(eventFields[i].getType(), eventFields[i].getName());
			}
			event.createConstructor(fields);
			for(IField field : fields) {
				event.createGetterMethod(field);
			}
			
			event.createDispatchMethod(handler.getType());
			event.createAssociatedTypeGetterMethod(handler.getType());
			
			
			if(page.hasHandlers()) {
				hasHandlers = new HasHandlers(root, page.getHasHandlerPackageText(), page.getHasHandlerTypeName());
				hasHandlers.createAddHandlerMethod(handler.getType());
				event.createFireMethod(fields, hasHandlers.getType());
			}
			else {
				event.createFireMethod(fields);
			}
			
			handler.createTriggerMethod(event.getType());

			// Committing
			if(event != null) event.commit();
			if(handler != null) handler.commit();
			if(hasHandlers != null) hasHandlers.commit();
		}
		catch (JavaModelException e) {
			e.printStackTrace();

			try {
				if(event != null) event.discard();
				if(handler != null) handler.discard();
				if(hasHandlers != null) hasHandlers.discard();
			}
			catch (JavaModelException e1) {	}
			
			return false;
		}
		
		return true;
	}

}
