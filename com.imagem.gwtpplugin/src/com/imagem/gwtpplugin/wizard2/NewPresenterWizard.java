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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.imagem.gwtpplugin.projectfile.src.client.core.Presenter;
import com.imagem.gwtpplugin.projectfile.src.client.core.Ui;
import com.imagem.gwtpplugin.projectfile.src.client.core.View;
import com.imagem.gwtpplugin.projectfile.src.client.gin.Ginjector;
import com.imagem.gwtpplugin.projectfile.src.client.gin.PresenterModule;
import com.imagem.gwtpplugin.projectfile.src.client.place.Tokens;

public class NewPresenterWizard extends Wizard implements INewWizard {

	private NewPresenterWizardPage newPresenterPage;
	private IStructuredSelection selection;

	public NewPresenterWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Event");
	}

	@Override
	public void addPages() {
		newPresenterPage = new NewPresenterWizardPage(selection);
		addPage(newPresenterPage);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		try {
			// Presenter
			Presenter presenter = new Presenter(newPresenterPage.getPackageFragmentRoot(), newPresenterPage.getPackageText(), newPresenterPage.getTypeName(), newPresenterPage.isWidget());
			presenter.createViewInterface();
			if(newPresenterPage.isPlace()) {
				Tokens tokens = new Tokens(newPresenterPage.getJavaProject(), newPresenterPage.getTokenClass());
				tokens.createTokenField(newPresenterPage.getTokenName());
				tokens.createTokenGetter(newPresenterPage.getTokenName());
				
				if(newPresenterPage.getGatekeeper().isEmpty()) {
					presenter.createProxyPlaceInterface(newPresenterPage.isProxyStandard(), tokens.getType(), newPresenterPage.getTokenName());
				}
				else {
					IType gatekeeper = newPresenterPage.getJavaProject().findType(newPresenterPage.getGatekeeper());
					
					presenter.createProxyPlaceInterface(newPresenterPage.isProxyStandard(), tokens.getType(), newPresenterPage.getTokenName(), gatekeeper);
				}
			}
			else {
				presenter.createProxyInterface(newPresenterPage.isProxyStandard());
			}
			
			presenter.createConstructor();
			
			IType revealEvent = newPresenterPage.getJavaProject().findType(newPresenterPage.getRevealEvent());
			if(revealEvent.getElementName().equals("RevealContentEvent")) {
				IType parent = newPresenterPage.getJavaProject().findType(newPresenterPage.getParent());
				
				presenter.createRevealInParentMethod(revealEvent, parent, newPresenterPage.getContentSlot());
			}
			else {
				presenter.createRevealInParentMethod(revealEvent);
			}
			
			String[] methods = newPresenterPage.getMethodStubs();
			for(String method : methods) {
				presenter.createMethodStub(method);
			}
			
			// View
			View view = new View(newPresenterPage.getPackageFragmentRoot(), newPresenterPage.getViewPackageText(), newPresenterPage.getViewTypeName(), presenter.getType());
			if(newPresenterPage.useUiBinder()) {
				view.createBinderInterface();
				view.createWidgetField();
			}
			view.createConstructor(newPresenterPage.useUiBinder());
			view.createAsWidgetMethod(newPresenterPage.useUiBinder());
			
			// Ui
			if(newPresenterPage.useUiBinder()) {
				Ui ui = new Ui(newPresenterPage.getPackageFragmentRoot(), newPresenterPage.getViewPackageText(), newPresenterPage.getViewTypeName(), view.getType());
				ui.createFile();
			}
			
			// Ginjector
			if(!newPresenterPage.isWidget()) {
				Ginjector ginjector = new Ginjector(newPresenterPage.getJavaProject(), newPresenterPage.getGinjector());
				ginjector.createProvider(presenter.getType());
			}
			
			// PresenterModule
			PresenterModule presenterModule = new PresenterModule(newPresenterPage.getJavaProject(), newPresenterPage.getPresenterModule());
			presenterModule.createBinder(presenter.getType(), view.getType());
		}
		catch (CoreException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
