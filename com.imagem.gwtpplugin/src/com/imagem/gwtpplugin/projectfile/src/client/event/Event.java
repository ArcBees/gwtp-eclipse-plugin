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

import java.io.InputStream;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.imagem.gwtpplugin.projectfile.Field;
import com.imagem.gwtpplugin.projectfile.IProjectFile;

public class Event implements IProjectFile {

	private final String EXTENSION = ".java";
	private String name;
	private String eventPackage;
	private Field[] fields;
	private boolean hasHandlers = false;

	@Deprecated
	public Event(String name, String eventPackage) {
		this.name = name;
		this.eventPackage = eventPackage;
	}
	
	public void setFields(Field... fields) {
		this.fields = fields;
	}
	
	public void setHandlers(boolean hasHandlers) {
		this.hasHandlers  = hasHandlers;
	}

	@Override
	public String getName() {
		return name + "Event";
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
		/*if(fields == null)
			fields = new Field[0];
		
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import com.google.gwt.event.shared.GwtEvent;\n";
		if(!hasHandlers)
			contents += "import com.google.gwt.event.shared.HasHandlers;\n";
		contents += "\n";
		
		contents += "public class " + getName() + " extends GwtEvent<" + name + "Handler> {\n";
		contents += "	public static Type<" + name + "Handler> TYPE = new Type<" + name + "Handler>();\n\n";
		

		for(Field field : fields) {
			contents += "	private final " + field.getField() + ";\n";
		}
		contents += "\n";
		
		contents += "	public " + getName() + "(";
		String separator = "";
		for(Field param : fields) {
			contents += separator + "final " + param.getField();
			separator = ", ";
		}
		contents += ") {\n";
		for(Field param : fields) {
			contents += "		this." + param.getName() + " = " + param.getName() + ";\n";
		}
		contents += "	}\n\n";
		
		for(Field field : fields) {
			if((field.getType().equals("boolean") || field.getType().equals("Boolean")) && (field.getName().startsWith("is") || field.getName().startsWith("has")))
				contents += "	public " + field.getField() + "() {\n";
			else
				contents += "	public " + field.getType() + " get" + field.getCapName() + "() {\n";
			contents += "		return " + field.getName() + ";\n";
			contents += "	}\n\n";
		}
		
		contents += "	@Override\n";
		contents += "	protected void dispatch(" + name + "Handler handler) {\n";
		contents += "		handler.on" + name + "(this);\n";
		contents += "	}\n\n";
		
		contents += "	@Override\n";
		contents += "	public Type<" + name + "Handler> getAssociatedType() {\n";
		contents += "		return TYPE;\n";
		contents += "	}\n\n";
		
		contents += "	public static Type<" + name + "Handler> getType() {\n";
		contents += "		return TYPE;\n";
		contents += "	}\n\n";
		
		if(hasHandlers)
			contents += "	public static void fire(Has" + name + "Handlers source";
		else
			contents += "	public static void fire(HasHandlers source";
		for(Field field : fields) {
			contents += ", " + field.getField();
		}
		contents += ") {\n";
		contents += "		source.fireEvent(new " + getName() + "(";
		separator = "";
		for(Field field : fields) {
			contents += separator + field.getName();
			separator = ", ";
		}
		contents += "));\n";
		contents += "	}\n\n";
		
		contents += "}";
		
		for(Field field : fields) {
			if(!field.getQualifiedType().isEmpty())
				contents = SourceEditor.insertImport(contents, field.getQualifiedType());
		}

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());*/
		return null;
	}
	
	// New Version
	private static final String C_GWT_EVENT = "com.google.gwt.event.shared.GwtEvent";
	private static final String C_TYPE = "com.google.gwt.event.shared.Type";
	private static final String I_HAS_HANDLERS = "com.google.gwt.event.shared.HasHandlers";
	
	private IType type;
	private ICompilationUnit cu;
	
	public Event(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		type = root.getJavaProject().findType(fullyQualifiedName);
		cu = type.getCompilationUnit();
	}
	
	public Event(IPackageFragmentRoot root, String packageName, String elementName, IType handler) throws JavaModelException {
		type = root.getJavaProject().findType(packageName + "." + elementName);
		if(type == null) {
			String cuName = elementName + ".java";
			
			IPackageFragment pack = root.createPackageFragment(packageName, false, null);
			ICompilationUnit cu = pack.createCompilationUnit(cuName, "", false, null);
			cu.createPackageDeclaration(packageName, null);

			cu.createImport(C_GWT_EVENT, null, null);
			cu.createImport(handler.getFullyQualifiedName(), null, null);
			String contents = "public class " + elementName + " extends GwtEvent<" + handler.getElementName() + "> {\n\n}";
	
			type = cu.createType(contents, null, false, null);
		}
		cu = type.getCompilationUnit();
	}
	
	public IType getType() {
		return type;
	}
	
	public IField createTypeField(IType handler) throws JavaModelException {
		cu.createImport(C_TYPE, null, null);
		String contents = "public static Type<" + handler.getElementName() + "> TYPE = new Type<" + name + "Handler>();";
		
		return type.createField(contents, null, false, null);
	}
	
	public IField createField(IType fieldType, String fieldName) throws JavaModelException {
		cu.createImport(fieldType.getFullyQualifiedName(), null, null);
		String contents = "private final " + fieldType.getElementName() + " " + fieldName + ";";
		
		return type.createField(contents, null, false, null);
	}
	
	public IMethod createConstructor(IField[] fields) throws JavaModelException {
		String contents = "public " + type.getElementName() + "(";
		String fieldContents = "";
		for(IField field : fields) {
			if(!contents.endsWith("("))
				contents += ", ";
			contents += Signature.toString(field.getTypeSignature()) + " " + field.getElementName();
			fieldContents += "	this." + field.getElementName() + " = " + field.getElementName() + ";\n";
		}
		contents += ") {\n";
		contents += fieldContents;
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createGetterMethod(IField field) throws JavaModelException {
		String contents = "public " + Signature.toString(field.getTypeSignature());
		contents += " get" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1) + " () {\n";
		contents += "	return " + field.getElementName() + ";\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createDispatchMethod(IType handler) throws JavaModelException {
		String contents = "";
		
		contents += "@Override\n";
		contents += "protected void dispatch(" + handler.getElementName() + " handler) {\n";
		contents += "	handler.on" + type.getElementName().substring(0, type.getElementName().length() - 5) + "(this);\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createAssociatedTypeGetterMethod(IType handler) throws JavaModelException {
		String contents = "";
		
		contents += "@Override\n";
		contents += "public Type<" + handler.getElementName() + "> getAssociatedType() {\n";
		contents += "	return TYPE;\n";
		contents += "}";

		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createFireMethod(IField[] fields) throws JavaModelException {
		String contents = "";
		
		cu.createImport(I_HAS_HANDLERS, null, null);
		contents += "public static void fire(HasHandlers source, ";
		String subContents = "";
		for(IField field : fields) {
			if(!contents.endsWith("(")) {
				contents += ", ";
				subContents += ", ";
			}
			contents += Signature.toString(field.getTypeSignature()) + " " + field.getElementName();
			subContents += field.getElementName();
		}
		contents += ") {\n";
		contents += "	source.fireEvent(new " + type.getElementName() + "(" + subContents + "));\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createFireMethod(IField[] fields, IType hasHandlers) throws JavaModelException {
		String contents = "";
		
		cu.createImport(hasHandlers.getFullyQualifiedName(), null, null);
		contents += "public static void fire(" + hasHandlers.getElementName() + " source, ";
		String subContents = "";
		for(IField field : fields) {
			if(!contents.endsWith("(")) {
				contents += ", ";
				subContents += ", ";
			}
			contents += Signature.toString(field.getTypeSignature()) + " " + field.getElementName();
			subContents += field.getElementName();
		}
		contents += ") {\n";
		contents += "	source.fireEvent(new " + type.getElementName() + "(" + subContents + "));\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}

}
