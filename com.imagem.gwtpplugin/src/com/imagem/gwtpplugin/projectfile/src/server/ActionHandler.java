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

package com.imagem.gwtpplugin.projectfile.src.server;

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

public class ActionHandler implements IProjectFile {

	private final String EXTENSION = ".java";
	private String projectName;
	private String name;
	private String handlerPackage;
	private String actionPackage;
	private boolean isSecure = false;

	@Deprecated
	public ActionHandler(String projectName, String name, String handlerPackage, String actionPackage) {
		this.projectName = projectName;
		this.name = name;
		this.handlerPackage = handlerPackage;
		this.actionPackage = actionPackage;
	}

	public void setSecureAction(boolean isSecure) {
		this.isSecure  = isSecure;
	}

	@Override
	public String getName() {
		return name + "ActionHandler";
	}

	@Override
	public String getPackage() {
		return handlerPackage;
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
		String baseHandlerPackage = handlerPackage;
		if(isSecure) {
			while(baseHandlerPackage.length() > 15 && !baseHandlerPackage.endsWith(".server.handler")) {
				baseHandlerPackage = baseHandlerPackage.substring(0, baseHandlerPackage.length() - 1);
			}
		}

		String contents = "package " + getPackage() + ";\n\n";

		//contents += "import java.sql.SQLException;\n\n";

		contents += "import com.google.inject.Inject;\n";
		contents += "import com.gwtplatform.dispatch.server.ExecutionContext;\n";
		if(isSecure)
			contents += "import " + baseHandlerPackage +"." + projectName + "ActionHandler;\n";
		else
			contents += "import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;\n";
		contents += "import com.gwtplatform.dispatch.shared.ActionException;\n";
		contents += "import " + actionPackage + "." + name + ";\n";
		contents += "import " + actionPackage + "." + name + "Result;\n\n";

		if(isSecure)
			contents += "public class " + getName() + " extends " + projectName + "ActionHandler<" + name + ", " + name + "Result> {\n\n";
		else
			contents += "public class " + getName() + " implements ActionHandler<" + name + ", " + name + "Result> {\n\n";

		contents += "	@Inject\n";
		contents += "	public " + getName() + "() {\n";
		contents += "		\n";
		contents += "	}\n\n";

		contents += "	@Override\n";
		contents += "	public " + name + "Result execute(" + name + " action, ExecutionContext context) throws ActionException {\n";
		contents += "		" + name + "Result result = null;\n";
		contents += "		try {\n";
		if(isSecure)
			contents += "			updateTimeStamp();\n\n";
		contents += "			// TODO Put your action handling here\n";
		contents += "		}\n";
		contents += "		catch(Exception e) {\n";
		contents += "			// TODO Error handling\n";
		contents += "			throw new ActionException(e);\n";
		contents += "		}\n";
		contents += "		return result;\n";
		contents += "	}\n\n";

		contents += "	@Override\n";
		contents += "	public Class<" + name + "> getActionType() {\n";
		contents += "		return " + name + ".class;\n";
		contents += "	}\n\n";

		contents += "	@Override\n";
		contents += "	public void undo(" + name + " action, " + name + "Result result, ExecutionContext context) throws ActionException {\n";
		contents += "	}\n\n";

		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
	
	// New Version
	private static final String C_ACTION_EXCEPTION = "com.gwtplatform.dispatch.shared.ActionException";
	private static final String I_ACTION_HANDLER = "com.gwtplatform.dispatch.server.actionhandler.ActionHandler";
	private static final String I_EXECUTION_CONTEXT = "com.gwtplatform.dispatch.server.ExecutionContext";
	private static final String A_INJECT = "com.google.inject.Inject";
	
	private IType type;
	private ICompilationUnit cu;
	
	public ActionHandler(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		type = root.getJavaProject().findType(fullyQualifiedName);
		cu = type.getCompilationUnit();
	}
	
	public ActionHandler(IPackageFragmentRoot root, String packageName, String elementName, IType action, IType result) throws JavaModelException {
		type = root.getJavaProject().findType(packageName + "." + elementName);
		if(type == null) {
			String cuName = elementName + ".java";
			
			IPackageFragment pack = root.createPackageFragment(packageName, false, null);
			ICompilationUnit cu = pack.createCompilationUnit(cuName, "", false, null);
			cu.createPackageDeclaration(packageName, null);

			cu.createImport(I_ACTION_HANDLER, null, null);
			cu.createImport(action.getFullyQualifiedName(), null, null);
			cu.createImport(result.getFullyQualifiedName(), null, null);
			String contents = "public class " + elementName + " implements ActionHandler<" + action.getElementName() + ", " + result.getElementName() + "> {\n\n}";
	
			type = cu.createType(contents, null, false, null);
		}
		cu = type.getCompilationUnit();
	}
	
	public IType getType() {
		return type;
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
