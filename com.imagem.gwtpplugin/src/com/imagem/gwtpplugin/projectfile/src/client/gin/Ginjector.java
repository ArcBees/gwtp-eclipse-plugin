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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.IUpdatableFile;
import com.imagem.gwtpplugin.projectfile.src.client.core.Presenter;
import com.imagem.gwtpplugin.tool.Formatter;

public class Ginjector implements IUpdatableFile {

	private final String EXTENSION = ".java";
	private String projectName;
	private String ginPackage;
	private String resourcePackage;
	private String clientPackage;
	private Presenter presenter;

	@Deprecated
	public Ginjector(String projectName, String ginPackage, String resourcePackage, String clientPackage) {
		this.projectName = projectName;
		this.ginPackage = ginPackage;
		this.resourcePackage = resourcePackage;
		this.clientPackage = clientPackage;
	}
	
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public String getName() {
		return projectName + "Ginjector";
	}

	@Override
	public String getPackage() {
		return ginPackage;
	}

	@Override
	public String getPath() {
		return "src/" + ginPackage.replace('.', '/');
	}
	
	@Override
	public String getExtension() {
		return EXTENSION;
	}

	@Override
	public InputStream openContentStream() {
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import com.google.gwt.event.shared.EventBus;\n";
		contents += "import com.google.gwt.inject.client.AsyncProvider;\n";
		contents += "import com.google.gwt.inject.client.GinModules;\n";
		contents += "import com.google.gwt.inject.client.Ginjector;\n";
		contents += "import com.gwtplatform.dispatch.client.DispatchAsync;\n";
		contents += "import com.gwtplatform.dispatch.client.gin.DispatchAsyncModule;\n";
		contents += "import com.gwtplatform.mvp.client.proxy.PlaceManager;\n";
		contents += "import com.gwtplatform.mvp.client.proxy.ProxyFailureHandler;\n";
		contents += "import " + clientPackage + ".core.presenter.TestPresenter;\n"; // TODO Test
		contents += "import " + resourcePackage + ".Resources;\n";
		contents += "import " + resourcePackage + ".Translations;\n\n";
		
		contents += "@GinModules({ DispatchAsyncModule.class, " + projectName + "ClientModule.class })\n";
		contents += "public interface " + getName() + " extends Ginjector {\n";
		contents += "	PlaceManager getPlaceManager();\n";
		contents += "	EventBus getEventBus();\n";
		contents += "	DispatchAsync getDispatcher();\n";
		contents += "	ProxyFailureHandler getProxyFailureHandler();\n";
		contents += "	Resources getResources();\n";
		contents += "	Translations getTranslations();\n\n";
		
		contents += "	AsyncProvider<TestPresenter> getTestPresenter();\n"; // TODO Test
		
		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

	@Override
	public InputStream updateFile(InputStream is) {
		String contents = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while((line = br.readLine()) != null) {
				contents += line + "\n";
			}
		}
		catch (IOException e) {
			return is;
		}
		
		if(presenter != null && !presenter.isWidget()) {
			contents = SourceEditor.insertImport(contents, presenter.getPackage() + "." + presenter.getName());
			
			String provider = "";
			if(presenter.isCodeSplit()) {
				provider = "AsyncProvider";
				contents = SourceEditor.insertImport(contents, "com.google.gwt.inject.client.AsyncProvider");
			}
			else {
				provider = "Provider";
				contents = SourceEditor.insertImport(contents, "com.google.inject.Provider");
			}
			String inject = "	" + provider + "<" + presenter.getName() + "> get" + presenter.getName() + "();\n";
			String method = "public interface " + getName() + " extends Ginjector {\n";
			contents = SourceEditor.addLine(contents, inject, method);
		}
		
		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
	
	// New Version
	private final String PROVIDER = "com.google.inject.Provider";
	private final String ASYNC_PROVIDER = "com.google.gwt.inject.client.AsyncProvider";
	
	private IType type;
	private ICompilationUnit cu;
	
	public Ginjector(IJavaProject project, String fullyQualifiedName) throws JavaModelException {
		// TODO Create if doesn't exist
		type = project.findType(fullyQualifiedName);
		cu = type.getCompilationUnit();
	}
	
	public IMethod createProvider(IType presenter) throws JavaModelException {
		IAnnotation annotation = null;
		IJavaElement[] children = presenter.getChildren();
		for(IJavaElement child : children) {
			if(child instanceof IType && ((IType) child).getElementName().equals("MyProxy")) {
				annotation = ((IType) child).getAnnotation("ProxyStandard");
				break;
			}
		}
		
		cu.createImport(annotation.exists() ? PROVIDER : ASYNC_PROVIDER, null, null);
		cu.createImport(presenter.getFullyQualifiedName(), null, null);
		
		String contents = (annotation.exists() ? "Provider<" : "AsyncProvider<") + presenter.getElementName() + "> get" + presenter.getElementName() + "();";
		return type.createMethod(contents, null, false, null);
	}

}
