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

package com.imagem.gwtpplugin.projectfile.src.server.guice;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.IUpdatableFile;
import com.imagem.gwtpplugin.projectfile.src.server.ActionHandler;
import com.imagem.gwtpplugin.projectfile.src.shared.Action;
import com.imagem.gwtpplugin.tool.Formatter;

public class HandlerModule implements IUpdatableFile {

	private final String EXTENSION = ".java";
	private String guicePackage;
	private Action action;
	private ActionHandler actionHandler;
	private String name = "ServerModule";

	@Deprecated
	public HandlerModule(String guicePackage) {
		this.guicePackage = guicePackage;
	}
	
	public void setAction(Action action) {
		this.action = action;
	}
	
	public void setActionHandler(ActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}
	
	public void setName(String name) {
		this.name  = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPackage() {
		return guicePackage;
	}

	@Override
	public String getPath() {
		return "src/" + getPackage().replace('.', '/');
	}

	@Override
	public String getExtension() {
		return EXTENSION;
	}

	public InputStream openContentStream() {
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import com.gwtplatform.dispatch.server.guice.HandlerModule;\n\n";

		contents += "/**\n";
		contents += " * Module qui lie les actions aux actionHandlers et aux actionValidators\n";
		contents += " */\n";
		contents += "public class " + getName() + " extends HandlerModule {\n\n";

		contents += "	@Override\n";
		contents += "	protected void configureHandlers() {\n";
		contents += "	}\n";

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
		
		if(action != null && actionHandler != null) {
			contents = SourceEditor.insertImport(contents, action.getPackage() + "." + action.getName());
			contents = SourceEditor.insertImport(contents, actionHandler.getPackage() + "." + actionHandler.getName());

			String line = "		bindHandler(" + action.getName() + ".class, " + actionHandler.getName() + ".class);\n";
			String method = "	protected void configureHandlers() {\n";
			contents = SourceEditor.addLine(contents, line, method);
		}
		
		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
	
	// New Version
	private IType type;
	private ICompilationUnit cu;
	
	public HandlerModule(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		type = root.getJavaProject().findType(fullyQualifiedName);
		cu = type.getCompilationUnit();
	}
	
	public IType getType() {
		return type;
	}
	
	public void createBinder(IType action, IType actionHandler) throws JavaModelException {
		cu.createImport(action.getFullyQualifiedName(), null, null);
		cu.createImport(actionHandler.getFullyQualifiedName(), null, null);
		
		IBuffer buffer = cu.getBuffer();
		
		IMethod configure = type.getMethod("configureHandlers", new String[0]);
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
		contents += "bindHandler(" + action.getElementName() + ".class, " + actionHandler.getElementName() + ".class);";
		
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
