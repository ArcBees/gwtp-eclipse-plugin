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

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.IUpdatableFile;
import com.imagem.gwtpplugin.projectfile.src.client.core.Presenter;
import com.imagem.gwtpplugin.projectfile.src.client.core.View;
import com.imagem.gwtpplugin.tool.Formatter;

public class PresenterModule implements IUpdatableFile {

	private final String EXTENSION = ".java";
	private String projectName;
	private String ginPackage;
	private String placePackage;
	private String resourcePackage;
	private String clientPackage;
	private Presenter presenter;
	private View view;

	@Deprecated
	public PresenterModule(String projectName, String ginPackage, String placePackage, String resourcePackage, String clientPackage) {
		this.projectName = projectName;
		this.ginPackage = ginPackage;
		this.placePackage = placePackage;
		this.resourcePackage = resourcePackage;
		this.clientPackage = clientPackage;
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public void setView(View view) {
		this.view = view;
	}

	@Override
	public String getName() {
		return projectName + "ClientModule";
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
		contents += "import com.google.gwt.event.shared.SimpleEventBus;\n";
		contents += "import com.google.inject.Singleton;\n";
		contents += "import com.gwtplatform.mvp.client.DefaultProxyFailureHandler;\n";
		contents += "import com.gwtplatform.mvp.client.RootPresenter;\n";
		contents += "import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;\n";
		contents += "import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;\n";
		contents += "import com.gwtplatform.mvp.client.proxy.PlaceManager;\n";
		contents += "import com.gwtplatform.mvp.client.proxy.ProxyFailureHandler;\n";
		contents += "import com.gwtplatform.mvp.client.proxy.TokenFormatter;\n\n";
		contents += "import " + clientPackage + ".ActionCallback;\n";
		contents += "import " + placePackage + "." + projectName + "PlaceManager;\n";
		contents += "import " + placePackage + "." + projectName + "Tokens;\n"; // TODO Test
		contents += "import " + placePackage + ".annotation.DefaultPlace;\n"; // TODO Test
		contents += "import " + resourcePackage + ".Resources;\n";
		contents += "import " + resourcePackage + ".Translations;\n";
		contents += "import " + clientPackage + ".core.presenter.TestPresenter;\n"; // TODO Test
		contents += "import " + clientPackage + ".core.view.TestView;\n"; // TODO Test

		contents += "public class " + getName() + " extends AbstractPresenterModule {\n\n";

		contents += "	@Override\n";
		contents += "	protected void configure() {\n";
		contents += "		// Singletons\n";
		contents += "		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);\n";
		contents += "		bind(PlaceManager.class).to(" + projectName + "PlaceManager.class).in(Singleton.class);\n";
		contents += "		bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(Singleton.class);\n";
		contents += "		bind(ProxyFailureHandler.class).to(DefaultProxyFailureHandler.class).in(Singleton.class);\n";
		contents += "		bind(RootPresenter.class).asEagerSingleton();\n";
		contents += "		bind(Resources.class).in(Singleton.class);\n";
		contents += "		bind(Translations.class).in(Singleton.class);\n\n";

		contents += "		requestStaticInjection(ActionCallback.class);\n\n";

		contents += "		// Constants\n";
		contents += "		// TODO bind the defaultPlace\n";
		contents += "		bindConstant().annotatedWith(DefaultPlace.class).to(" + projectName + "Tokens.test);\n\n"; // TODO Test

		contents += "		// Presenters\n";
		contents += "		bindPresenter(TestPresenter.class, TestPresenter.MyView.class, TestView.class, TestPresenter.MyProxy.class);\n"; // TODO Test
		contents += "	}\n\n";

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

		if(presenter != null && view != null) {
			contents = SourceEditor.insertImport(contents, presenter.getPackage() + "." + presenter.getName());
			contents = SourceEditor.insertImport(contents, view.getPackage() + "." + view.getName());
			String bindType = "";
			if(presenter.isWidget()) {
				bindType = "		bindPresenterWidget(" + presenter.getName() + ".class, " + presenter.getName() + ".MyView.class, " + view.getName() + ".class);\n";
			}
			else {
				bindType = "		bindPresenter(" + presenter.getName() + ".class, " + presenter.getName() + ".MyView.class, " + view.getName() + ".class, " + presenter.getName() + ".MyProxy.class);\n";
			}
			String method = "	protected void configure() {\n";
			contents = SourceEditor.addLine(contents, bindType, method);
		}

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

	// New Version
	private IType type;
	private ICompilationUnit cu;
	
	public PresenterModule(IJavaProject project, String fullyQualifiedName) throws JavaModelException {
		// TODO Create if doesn't exist
		type = project.findType(fullyQualifiedName);
		cu = type.getCompilationUnit();
	}
	
	public void createBinder(IType presenter, IType view) throws JavaModelException {
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
