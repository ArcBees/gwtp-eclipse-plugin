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

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.imagem.gwtpplugin.projectfile.ProjectClass;

public class Event extends ProjectClass {

	private static final String C_GWT_EVENT = "com.google.gwt.event.shared.GwtEvent";
	private static final String I_HAS_HANDLERS = "com.google.gwt.event.shared.HasHandlers";
	
	public Event(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		super(root, fullyQualifiedName);
	}
	
	public Event(IPackageFragmentRoot root, String packageName, String elementName, IType handler) throws JavaModelException {
		super(root, packageName, elementName);
		if(type == null) {
			cu.createPackageDeclaration(packageName, null);

			cu.createImport(C_GWT_EVENT, null, null);
			cu.createImport(handler.getFullyQualifiedName(), null, null);
			String contents = "public class " + elementName + " extends GwtEvent<" + handler.getElementName() + "> {\n\n}";
	
			type = cu.createType(contents, null, false, null);
		}
	}
	
	public IField createTypeField(IType handler) throws JavaModelException {
		String contents = "public static Type<" + handler.getElementName() + "> TYPE = new Type<" + handler.getElementName() + ">();";
		
		return type.createField(contents, null, false, null);
	}
	
	public IField createField(IType fieldType, String fieldName) throws JavaModelException {
		cu.createImport(fieldType.getFullyQualifiedName(), null, null);
		String contents = "private final " + fieldType.getElementName() + " " + fieldName + ";";
		
		return type.createField(contents, null, false, null);
	}
	
	public IField createField(String fieldType, String fieldName) throws JavaModelException {
		String contents = "private final " + fieldType + " " + fieldName + ";";
		
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
		contents += "public static void fire(HasHandlers source";
		String subContents = "";
		for(IField field : fields) {
			if(!subContents.isEmpty()) {
				subContents += ", ";
			}
			contents += ", " + Signature.toString(field.getTypeSignature()) + " " + field.getElementName();
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
		contents += "public static void fire(" + hasHandlers.getElementName() + " source";
		String subContents = "";
		for(IField field : fields) {
			if(!subContents.isEmpty()) {
				subContents += ", ";
			}
			contents += ", " + Signature.toString(field.getTypeSignature()) + " " + field.getElementName();
			subContents += field.getElementName();
		}
		contents += ") {\n";
		contents += "	source.fireEvent(new " + type.getElementName() + "(" + subContents + "));\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}

}
