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

public class HasHandlers implements IProjectFile {

	private final String EXTENSION = ".java";
	private String name;
	private String eventPackage;

	@Deprecated
	public HasHandlers(String name, String eventPackage) {
		this.name = name;
		this.eventPackage = eventPackage;
	}

	@Override
	public String getName() {
		return "Has" + name + "Handlers";
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

		contents += "import com.google.gwt.event.shared.HandlerRegistration;\n";
		contents += "import com.google.gwt.event.shared.HasHandlers;\n\n";
		
		contents += "public interface " + getName() + " extends HasHandlers {\n\n";
		
		contents += "	public HandlerRegistration add" + name + "Handler(" + name + "Handler handler);\n";
		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
	
	// New Version
	private static final String I_HAS_HANDLERS = "com.google.gwt.event.shared.HasHandlers";
	
	private IType type;
	private ICompilationUnit cu;
	
	public HasHandlers(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		type = root.getJavaProject().findType(fullyQualifiedName);
		cu = type.getCompilationUnit();
	}
	
	public HasHandlers(IPackageFragmentRoot root, String packageName, String elementName) throws JavaModelException {
		type = root.getJavaProject().findType(packageName + "." + elementName);
		if(type == null) {
			String cuName = elementName + ".java";
			
			IPackageFragment pack = root.createPackageFragment(packageName, false, null);
			ICompilationUnit cu = pack.createCompilationUnit(cuName, "", false, null);
			cu.createPackageDeclaration(packageName, null);

			cu.createImport(I_HAS_HANDLERS, null, null);
			String contents = "public interface " + elementName + " extends HasHandlers {\n\n}";
	
			type = cu.createType(contents, null, false, null);
		}
		cu = type.getCompilationUnit();
	}
	
	public IType getType() {
		return type;
	}
	
	public IMethod createAddHandlerMethod(IType eventHandler) throws JavaModelException {
		cu.createImport(eventHandler.getFullyQualifiedName(), null, null);
		String contents = "public HandlerRegistration add" + eventHandler.getElementName() + "(" + eventHandler.getElementName() + " handler);";
		
		return type.createMethod(contents, null, false, null);
	}
}
