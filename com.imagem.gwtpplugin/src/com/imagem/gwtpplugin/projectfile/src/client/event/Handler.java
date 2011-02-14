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

package com.imagem.gwtpplugin.projectfile.src.client.event;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class Handler implements IProjectFile {

	private final String EXTENSION = ".java";
	private String name;
	private String eventPackage;

	@Deprecated
	public Handler(String name, String eventPackage) {
		this.name = name;
		this.eventPackage = eventPackage;
	}

	@Override
	public String getName() {
		return name + "Handler";
	}

	@Override
	public String getPackage() {
		return eventPackage;
	}

	@Override
	public String getPath() {
		return "src/" + getPackage().replace('.', '/');
	}

	@Override
	public String getExtension() {
		return EXTENSION;
	}

	@Override
	public InputStream openContentStream() {
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import com.google.gwt.event.shared.EventHandler;\n\n";
		
		contents += "public interface " + getName() + " extends EventHandler {\n\n";
		
		contents += "	public void on" + name + "(" + name + "Event event);\n";
		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
	
	// New Version
	private static final String I_EVENT_HANDLER = "com.google.gwt.event.shared.EventHandler";
	
	private IType type;
	private ICompilationUnit cu;
	
	public Handler(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		type = root.getJavaProject().findType(fullyQualifiedName);
		cu = type.getCompilationUnit();
	}
	
	public Handler(IPackageFragmentRoot root, String packageName, String elementName) throws JavaModelException {
		type = root.getJavaProject().findType(packageName + "." + elementName);
		if(type == null) {
			String cuName = elementName + ".java";
			
			IPackageFragment pack = root.createPackageFragment(packageName, false, null);
			ICompilationUnit cu = pack.createCompilationUnit(cuName, "", false, null);
			cu.createPackageDeclaration(packageName, null);

			cu.createImport(I_EVENT_HANDLER, null, null);
			String contents = "public interface " + elementName + " extends EventHandler {\n\n}";
	
			type = cu.createType(contents, null, false, null);
		}
		cu = type.getCompilationUnit();
	}
	
	public IType getType() {
		return type;
	}
	
	public IMethod createTriggerMethod(IType event) throws JavaModelException {
		cu.createImport(event.getFullyQualifiedName(), null, null);
		String contents = "public void on" + event.getElementName().substring(0, event.getElementName().length() - 5) + "(" + event.getElementName() + " event);";
		
		return type.createMethod(contents, null, false, null);
	}

}
