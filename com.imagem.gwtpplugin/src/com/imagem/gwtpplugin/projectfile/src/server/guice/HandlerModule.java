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

package com.imagem.gwtpplugin.projectfile.src.server.guice;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.imagem.gwtpplugin.projectfile.ProjectClass;

/**
 * 
 * @author Michael Renaud
 *
 */
public class HandlerModule extends ProjectClass {

	private static final String C_HANDLER_MODULE = "com.gwtplatform.dispatch.server.guice.HandlerModule";
	
	public HandlerModule(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		super(root, fullyQualifiedName);
	}
	
	public HandlerModule(IPackageFragmentRoot root, String packageName, String elementName) throws JavaModelException {
		super(root, packageName, elementName);
		if(type == null) {
			cu.createPackageDeclaration(packageName, null);
			
			cu.createImport(C_HANDLER_MODULE, null, null);
			String contents = "public class " + elementName + " extends HandlerModule {\n\n}";
			
			type = cu.createType(contents, null, false, null);
		}
	}
	
	public IMethod createConfigureHandlersMethod() throws JavaModelException {
		String contents = "";
		
		contents += "@Override\n";
		contents += "protected void configureHandlers() {\n";
		contents += "}";

		return type.createMethod(contents, null, false, null);
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

	public void createBinder(IType action, IType actionHandler, IType actionValidator) throws JavaModelException {
		cu.createImport(action.getFullyQualifiedName(), null, null);
		cu.createImport(actionHandler.getFullyQualifiedName(), null, null);
		cu.createImport(actionValidator.getFullyQualifiedName(), null, null);
		
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
		contents += "bindHandler(" + action.getElementName() + ".class, " + actionHandler.getElementName() + ".class, " + actionValidator.getElementName() + ".class);";
		
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
