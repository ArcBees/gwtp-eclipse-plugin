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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
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

	private NewPresenterWizardPage page;
	private IStructuredSelection selection;

	public NewPresenterWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Event");
	}

	@Override
	public void addPages() {
		page = new NewPresenterWizardPage(selection);
		addPage(page);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		Presenter presenter = null;
		Tokens tokens = null;
		View view = null;
		Ginjector ginjector = null;
		PresenterModule presenterModule = null;
		try {
			IPackageFragmentRoot root = page.getPackageFragmentRoot();
			
			// Presenter
			presenter = new Presenter(root, page.getPackageText(), page.getTypeName(), page.isWidget());
			presenter.createViewInterface();
			if(page.isPlace()) {
				tokens = new Tokens(root, page.getTokenClass());
				tokens.createTokenField(page.getTokenName());
				tokens.createTokenGetter(page.getTokenName());
				
				if(page.getGatekeeper().isEmpty()) {
					presenter.createProxyPlaceInterface(page.isProxyStandard(), tokens.getType(), page.getTokenName());
				}
				else {
					IType gatekeeper = page.getJavaProject().findType(page.getGatekeeper());
					
					presenter.createProxyPlaceInterface(page.isProxyStandard(), tokens.getType(), page.getTokenName(), gatekeeper);
				}
			}
			else {
				presenter.createProxyInterface(page.isProxyStandard());
			}
			
			presenter.createConstructor();
			
			IType revealEvent = page.getJavaProject().findType(page.getRevealEvent());
			if(revealEvent.getElementName().equals("RevealContentEvent")) {
				IType parent = page.getJavaProject().findType(page.getParent());
				
				presenter.createRevealInParentMethod(revealEvent, parent, page.getContentSlot());
			}
			else {
				presenter.createRevealInParentMethod(revealEvent);
			}
			
			String[] methods = page.getMethodStubs();
			for(String method : methods) {
				presenter.createMethodStub(method);
			}
			
			// View
			view = new View(root, page.getViewPackageText(), page.getViewTypeName(), presenter.getType());
			if(page.useUiBinder()) {
				view.createBinderInterface();
				view.createWidgetField();
			}
			view.createConstructor(page.useUiBinder());
			view.createAsWidgetMethod(page.useUiBinder());
			
			// Ui
			if(page.useUiBinder()) {
				Ui ui = new Ui(root, page.getViewPackageText(), page.getViewTypeName());
				ui.createFile();
			}
			
			// Ginjector
			if(!page.isWidget()) {
				ginjector = new Ginjector(root, page.getGinjector());
				ginjector.createProvider(presenter.getType());
			}
			
			// PresenterModule
			presenterModule = new PresenterModule(root, page.getPresenterModule());
			presenterModule.createPresenterBinder(presenter.getType(), view.getType());

			if(presenter != null) presenter.commit();
			if(tokens != null) tokens.commit();
			if(view != null) view.commit();
			if(ginjector != null) ginjector.commit();
			if(presenterModule != null) presenterModule.commit();
		}
		catch (CoreException e) {
			e.printStackTrace();

			try {
				if(presenter != null) presenter.discard();
				if(tokens != null) tokens.discard();
				if(view != null) view.discard();
				if(ginjector != null) ginjector.discard();
				if(presenterModule != null) presenterModule.discard();
			}
			catch (JavaModelException e1) {	}
			
			return false;
		}

		return true;
	}

}
