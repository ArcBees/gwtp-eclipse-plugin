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

package com.imagem.gwtpplugin.projectfile.src.client.core;

import org.eclipse.jdt.core.IField;
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
public class View extends ProjectClass {

	private static final String C_VIEW_IMPL = "com.gwtplatform.mvp.client.ViewImpl";
	private static final String C_WIDGET = "com.google.gwt.user.client.ui.Widget";
	private static final String A_INJECT = "com.google.inject.Inject";
	private static final String I_UI_BINDER = "com.google.gwt.uibinder.client.UiBinder";

	public View(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		super(root, fullyQualifiedName);
	}

	public View(IPackageFragmentRoot root, String packageName, String elementName, IType presenter) throws JavaModelException {
		super(root, packageName, elementName);
		if(type == null) {
			cu.createPackageDeclaration(packageName, null);
			
			cu.createImport(C_VIEW_IMPL, null, null);
			String contents = "public class " + elementName + " extends ViewImpl implements " + presenter.getElementName() + ".MyView {\n\n}";

			type = cu.createType(contents, null, false, null);
		}
	}
	
	public IType createBinderInterface() throws JavaModelException {
		cu.createImport(I_UI_BINDER, null, null);
		return type.createType("public interface Binder extends UiBinder<Widget, " + type.getElementName() + "> { }", null, false, null);
	}
	
	public IField createWidgetField() throws JavaModelException {
		cu.createImport(C_WIDGET, null, null);
		return type.createField("private final Widget widget;", null, false, null);
	}
	
	public IMethod createConstructor(boolean useUiBinder) throws JavaModelException {
		String contents = "";
		
		cu.createImport(A_INJECT, null, null);
		contents += "@Inject\n";
		contents += "public " + type.getElementName() + "(" + (useUiBinder ? "final Binder binder" : "") + ") {\n";
		if(useUiBinder)
			contents += "	widget = binder.createAndBindUi(this);\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createAsWidgetMethod(boolean useUiBinder) throws JavaModelException {
		String contents = "";
		
		contents += "@Override\n";
		
		cu.createImport(C_WIDGET, null, null);
		contents += "public Widget asWidget() {\n";
		contents += "	return " + (useUiBinder ? "widget" : "null") + ";\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
}
