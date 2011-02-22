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

package com.imagem.gwtpplugin.projectfile.src.server;

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
public class ActionHandler extends ProjectClass {

	private static final String C_ACTION_EXCEPTION = "com.gwtplatform.dispatch.shared.ActionException";
	private static final String I_ACTION_HANDLER = "com.gwtplatform.dispatch.server.actionhandler.ActionHandler";
	private static final String I_EXECUTION_CONTEXT = "com.gwtplatform.dispatch.server.ExecutionContext";
	private static final String A_INJECT = "com.google.inject.Inject";
	
	public ActionHandler(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		super(root, fullyQualifiedName);
	}
	
	public ActionHandler(IPackageFragmentRoot root, String packageName, String elementName, IType action, IType result) throws JavaModelException {
		super(root, packageName, elementName);
		if(type == null) {
			cu.createPackageDeclaration(packageName, null);
			
			cu.createImport(I_ACTION_HANDLER, null, null);
			cu.createImport(action.getFullyQualifiedName(), null, null);
			cu.createImport(result.getFullyQualifiedName(), null, null);
			String contents = "public class " + elementName + " implements ActionHandler<" + action.getElementName() + ", " + result.getElementName() + "> {\n\n}";
			
			type = cu.createType(contents, null, false, null);
		}
	}
	
	public IMethod createConstructor() throws JavaModelException {
		String contents = "";
		
		cu.createImport(A_INJECT, null, null);
		contents += "@Inject\n";
		contents += "public " + type.getElementName() + "() {\n\n}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createExecuteMethod(IType action, IType result) throws JavaModelException {
		String contents = "";
		
		contents += "@Override\n";

		cu.createImport(I_EXECUTION_CONTEXT, null, null);
		cu.createImport(C_ACTION_EXCEPTION, null, null);
		contents += "public " + result.getElementName() + " execute(" + action.getElementName() + " action, ExecutionContext context) throws ActionException {\n";
		contents += "	return null;\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createUndoMethod(IType action, IType result) throws JavaModelException {
		String contents = "";

		contents += "@Override\n";
		contents += "public void undo(" + action.getElementName() + " action, " + result.getElementName() + " result, ExecutionContext context) throws ActionException {\n}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createActionTypeGetterMethod(IType action) throws JavaModelException {
		String contents = "";

		contents += "@Override\n";
		contents += "public Class<" + action.getElementName() + "> getActionType() {\n";
		contents += "	return " + action.getElementName() + ".class;\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}

}
