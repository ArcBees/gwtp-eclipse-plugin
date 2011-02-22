/**
 * Copyright 2011 IMAGEM Solutions TI santé
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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.imagem.gwtpplugin.projectfile.Field;
import com.imagem.gwtpplugin.projectfile.src.shared.Model;

/**
 * 
 * @author Michael Renaud
 *
 */
public class NewModelWizard extends Wizard implements INewWizard {

	private NewModelWizardPage page;
	private IStructuredSelection selection;
	private boolean isDone = false;

	public NewModelWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Model");
	}
	
	@Override
	public void addPages() {
		page = new NewModelWizardPage(selection);
		addPage(page);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		try {
			super.getContainer().run(false, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					isDone = finish(monitor);
				}
			});
		}
		catch(Exception e) {
			return false;
		}
		return isDone;
	}
	
	protected boolean finish(IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		Model model = null;
		try {
			monitor.beginTask("Model creation", 1);
			
			IPackageFragmentRoot root = page.getPackageFragmentRoot();
			
			model = new Model(root, page.getPackageText(), page.getTypeName());
			model.createConstructor();
			model.createSerializationField();
			
			Field[] modelFields = page.getFields();
			IField[] fields = new IField[modelFields.length];
			for(int i = 0; i < modelFields.length; i++) {
				if(modelFields[i].isPrimitiveType()) {
					fields[i] = model.createField(modelFields[i].getPrimitiveType(), modelFields[i].getName());
				}
				else {
					fields[i] = model.createField(modelFields[i].getType(), modelFields[i].getName());
				}
			}
			for(IField field : fields) {
				model.createSetterMethod(field);
			}
			for(IField field : fields) {
				model.createGetterMethod(field);
			}
			
			if(page.generateEquals()) {
				model.createEqualsMethod(fields);
				model.createHashCodeMethod(fields);
			}
			monitor.worked(1);
			
			if(model != null) model.commit();
		}
		catch (JavaModelException e) {
			e.printStackTrace();

			try {
				if(model != null) model.discard();
			}
			catch (JavaModelException e1) {	}
			
			return false;
		}
		
		monitor.done();
		return true;
	}

}
