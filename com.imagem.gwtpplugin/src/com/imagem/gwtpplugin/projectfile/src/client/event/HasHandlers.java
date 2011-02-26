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

package com.imagem.gwtpplugin.projectfile.src.client.event;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.imagem.gwtpplugin.projectfile.ProjectClass;

/**
 * 
 * @author Michael Renaud
 *
 */
public class HasHandlers extends ProjectClass {

	private static final String I_HAS_HANDLERS = "com.google.gwt.event.shared.HasHandlers";
	private static final String I_HANDLER_REGISTRATION = "com.google.gwt.event.shared.HandlerRegistration";
	
	public HasHandlers(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		super(root, fullyQualifiedName);
	}
	
	public HasHandlers(IPackageFragmentRoot root, String packageName, String elementName) throws JavaModelException {
		super(root, packageName, elementName);
		if(type == null) {
			cu.createPackageDeclaration(packageName, null);

			cu.createImport(I_HAS_HANDLERS, null, null);
			String contents = "public interface " + elementName + " extends HasHandlers {\n\n}";
	
			type = cu.createType(contents, null, false, null);
		}
	}
	
	public IMethod createAddHandlerMethod(IType eventHandler) throws JavaModelException {
		cu.createImport(I_HANDLER_REGISTRATION, null, null);
		cu.createImport(eventHandler.getFullyQualifiedName(), null, null);
		String contents = "public HandlerRegistration add" + eventHandler.getElementName() + "(" + eventHandler.getElementName() + " handler);";
		
		return type.createMethod(contents, null, false, null);
	}
}
