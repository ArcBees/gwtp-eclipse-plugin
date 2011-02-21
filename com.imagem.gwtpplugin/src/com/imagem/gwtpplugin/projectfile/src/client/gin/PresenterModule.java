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

package com.imagem.gwtpplugin.projectfile.src.client.gin;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.imagem.gwtpplugin.projectfile.ProjectClass;

public class PresenterModule extends ProjectClass {

	private static final String C_ABSTRACT_PRESENTER_MODULE = "com.gwtplatform.mvp.client.gin.AbstractPresenterModule";
	private static final String C_SIMPLE_EVENT_BUS = "com.google.gwt.event.shared.SimpleEventBus";
	private static final String C_PARAMETER_TOKEN_FORMATTER = "com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter";
	private static final String C_DEFAULT_PROXY_FAILURE_HANDLER = "com.gwtplatform.mvp.client.DefaultProxyFailureHandler";
	private static final String C_ROOT_PRESENTER = "com.gwtplatform.mvp.client.RootPresenter";
	private static final String I_EVENT_BUS = "com.google.gwt.event.shared.EventBus";
	private static final String I_TOKEN_FORMATTER = "com.gwtplatform.mvp.client.proxy.TokenFormatter";
	private static final String I_PROXY_FAILURE_HANDLER = "com.gwtplatform.mvp.client.proxy.ProxyFailureHandler";
	private static final String A_SINGLETON = "com.google.inject.Singleton";
	private static final String I_PLACE_MANAGER = "com.gwtplatform.mvp.client.proxy.PlaceManager";
	
	public PresenterModule(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		super(root, fullyQualifiedName);
	}
	
	public PresenterModule(IPackageFragmentRoot root, String packageName, String elementName) throws JavaModelException {
		super(root, packageName, elementName);
		if(type == null) {
			cu.createPackageDeclaration(packageName, null);
			
			cu.createImport(C_ABSTRACT_PRESENTER_MODULE, null, null);
			String contents = "public class " + elementName + " extends AbstractPresenterModule {\n\n}";
			
			type = cu.createType(contents, null, false, null);
		}
	}
	
	// TODO RequestStaticInjection
	// TODO BindConstant
	
	public IMethod createConfigureMethod(IType placeManager) throws JavaModelException {
		String contents = "";

		contents += "@Override\n";
		contents += "protected void configure() {\n";
		
		cu.createImport(A_SINGLETON, null, null);
		contents += "	// Singletons\n";
		
		cu.createImport(I_EVENT_BUS, null, null);
		cu.createImport(C_SIMPLE_EVENT_BUS, null, null);
		contents += "	bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);\n";
		
		cu.createImport(I_TOKEN_FORMATTER, null, null);
		cu.createImport(C_PARAMETER_TOKEN_FORMATTER, null, null);
		contents += "	bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(Singleton.class);\n";
		
		cu.createImport(I_PROXY_FAILURE_HANDLER, null, null);
		cu.createImport(C_DEFAULT_PROXY_FAILURE_HANDLER, null, null);
		contents += "	bind(ProxyFailureHandler.class).to(DefaultProxyFailureHandler.class).in(Singleton.class);\n";

		cu.createImport(I_PLACE_MANAGER, null, null);
		cu.createImport(placeManager.getFullyQualifiedName(), null, null);
		contents += "	bind(PlaceManager.class).to(" + placeManager.getElementName() + ".class).in(Singleton.class);\n";

		cu.createImport(C_ROOT_PRESENTER, null, null);
		contents += "	bind(RootPresenter.class).asEagerSingleton();\n\n";

		contents += "	// Constants\n";
		contents += "	// TODO bind the defaultPlace\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public void createSingletonBinder(IType bind, IType to) throws JavaModelException {
		cu.createImport(bind.getFullyQualifiedName(), null, null);
		cu.createImport(to.getFullyQualifiedName(), null, null);
		
		IBuffer buffer = cu.getBuffer();
		
		IMethod configure = type.getMethod("configure", new String[0]);
		ISourceRange range = configure.getSourceRange();
		String source = configure.getSource();
		
		String[] lines = source.split("\\\n");
		
		int lastSingletonBinder = -1;
		for(int i = lines.length - 1; i >= 0; i--) {
			if(lines[i].contains("bind(") && lines[i].endsWith(".in(Singleton.class")) {
				lastSingletonBinder = i;
				break;
			}
		}
		
		int tabulations = 0;
		for(char c : lines[lastSingletonBinder].toCharArray()) {
			if(c == '\t')
				tabulations++;
			else
				break;
		}

		String contents = "";
		for(int i = 0; i < tabulations; i++) {
			contents += "\t";
		}
		contents = "bind(" + bind.getElementName() + ".class).to(" + to.getElementName() + ".class).in(Singleton.class);";
		
		String newSource = "";
		for(int i = 0; i < lines.length; i++) {
			newSource += lines[i];
			if(i != lines.length - 1)
				newSource += "\n";
			if(i == lastSingletonBinder - 2)
				newSource += contents + "\n";
		}
		
		buffer.replace(range.getOffset(), range.getLength(), newSource);
		buffer.save(null, true);
	}
	
	public void createPresenterBinder(IType presenter, IType view) throws JavaModelException {
		cu.createImport(presenter.getFullyQualifiedName(), null, null);
		cu.createImport(view.getFullyQualifiedName(), null, null);
		
		boolean isWidget = presenter.getSuperclassName().startsWith("PresenterWidget");
		
		IBuffer buffer = cu.getBuffer();
		
		IMethod configure = type.getMethod("configure", new String[0]);
		ISourceRange range = configure.getSourceRange();
		String source = configure.getSource();
		
		String[] lines = source.split("\\\n");
		int tabulations = 1;
		for(char c : lines[lines.length - 1].toCharArray()) {
			if(c == '\t')
				tabulations++;
			else
				break;
		}
		
		String contents = "";
		for(int i = 0; i < tabulations; i++) {
			contents += "\t";
		}
		if(isWidget) {
			contents += "bindPresenterWidget(" + presenter.getElementName() + ".class, " + presenter.getElementName() + ".MyView.class, " + view.getElementName() + ".class);";
		}
		else {
			contents += "bindPresenter(" + presenter.getElementName() + ".class, " + presenter.getElementName() + ".MyView.class, " + view.getElementName() + ".class, " + presenter.getElementName() + ".MyProxy.class);";
		}
		
		String newSource = "";
		for(int i = 0; i < lines.length; i++) {
			newSource += lines[i];
			if(i != lines.length - 1)
				newSource += "\n";
			if(i == lines.length - 2)
				newSource += contents + "\n";
		}
		
		buffer.replace(range.getOffset(), range.getLength(), newSource);
		buffer.save(null, true);
	}
}
