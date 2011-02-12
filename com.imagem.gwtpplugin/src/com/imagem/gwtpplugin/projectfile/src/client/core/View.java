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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class View implements IProjectFile {

	private final String EXTENSION = ".java";
	private String name;
	private String viewPackage;
	private String presenterPackage;
	private boolean useUiBinder = false;

	@Deprecated
	public View(String name, String viewPackage, String presenterPackage) {
		this.name = name;
		this.viewPackage = viewPackage;
		this.presenterPackage = presenterPackage;
	}

	public void setUiBinder(boolean useUiBinder) {
		this.useUiBinder  = useUiBinder;
	}

	@Override
	public String getName() {
		return name + "View";
	}

	@Override
	public String getPackage() {
		return viewPackage;
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

		contents += "import com.google.gwt.user.client.ui.Widget;\n";
		contents += "import com.google.inject.Inject;\n";
		contents += "import com.gwtplatform.mvp.client.ViewImpl;\n";
		contents += "import " + presenterPackage + "." + name + "Presenter;\n";
		if(useUiBinder) {
			contents += "import com.google.gwt.uibinder.client.UiBinder;\n";
		}

		contents += "public class " + getName() + " extends ViewImpl implements " + name + "Presenter.MyView {\n\n";

		if(useUiBinder) {
			contents += "	public interface Binder extends UiBinder<Widget, " + getName() + "> { }\n\n";

			contents += "	private final Widget widget;\n\n";

			contents += "	@Inject\n";
			contents += "	public " + getName() + "(final Binder binder) {\n";
			contents += "		widget = binder.createAndBindUi(this);\n";
		}
		else {
			contents += "	@Inject\n";
			contents += "	public " + getName() + "() {\n";
			contents += "		// TODO Create your controls here\n";
		}
		contents += "	}\n\n";

		contents += "	@Override\n";
		contents += "	public Widget asWidget() {\n";
		if(useUiBinder) {
			contents += "		return widget;\n";
		}
		else {
			contents += "		// TODO Return the main panel of the view\n";
			contents += "		return null;\n";
		}
		contents += "	}\n\n";

		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

	// New Version
	private static final String C_VIEW_IMPL = "com.gwtplatform.mvp.client.ViewImpl";
	private static final String C_WIDGET = "com.google.gwt.user.client.ui.Widget";
	private static final String A_INJECT = "com.google.inject.Inject";
	private static final String I_UI_BINDER = "com.google.gwt.uibinder.client.UiBinder";
	
	private IType type;
	private ICompilationUnit cu;

	public View(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		type = root.getJavaProject().findType(fullyQualifiedName);
		cu = type.getCompilationUnit();
	}

	public View(IPackageFragmentRoot root, String packageName, String elementName, IType presenter) throws JavaModelException {
		type = root.getJavaProject().findType(packageName + "." + elementName);
		if(type == null) {
			String cuName = elementName + ".java";

			IPackageFragment pack = root.createPackageFragment(packageName, false, null);
			ICompilationUnit cu = pack.createCompilationUnit(cuName, "", false, null);
			cu.createPackageDeclaration(packageName, null);

			String contents = "";


			cu.createImport(C_VIEW_IMPL, null, null);
			contents += "public class " + elementName + " extends ViewImpl implements " + presenter.getElementName() + ".MyView {\n\n}";

			type = cu.createType(contents, null, false, null);
		}
		cu = type.getCompilationUnit();
	}

	public IType getType() {
		return type;
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
