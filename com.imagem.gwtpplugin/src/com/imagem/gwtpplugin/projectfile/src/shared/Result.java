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

package com.imagem.gwtpplugin.projectfile.src.shared;

import java.util.Random;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.imagem.gwtpplugin.projectfile.ProjectClass;

public class Result extends ProjectClass {

	private static final String I_RESULT = "com.gwtplatform.dispatch.shared.Result";
	
	public Result(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		super(root, fullyQualifiedName);
	}
	
	public Result(IPackageFragmentRoot root, String packageName, String elementName) throws JavaModelException {
		super(root, packageName, elementName);
		if(type == null) {
			cu.createPackageDeclaration(packageName, null);

			cu.createImport(I_RESULT, null, null);
			String contents = "public class " + elementName + " implements Result {\n\n}";
			
			type = cu.createType(contents, null, false, null);
		}
	}
	
	public IField createSerializationField() throws JavaModelException {
		Random generator = new Random();
		
		String contents = "private static final long serialVersionUID = " + generator.nextLong() + "L;\n";
		
		return type.createField(contents, null, false, null);
	}
	
	public IField createField(IType fieldType, String fieldName) throws JavaModelException {
		cu.createImport(fieldType.getFullyQualifiedName(), null, null);
		String contents = "private " + fieldType.getElementName() + " " + fieldName + ";";
		
		return type.createField(contents, null, false, null);
	}
	
	public IField createField(String fieldType, String fieldName) throws JavaModelException {
		String contents = "private " + fieldType + " " + fieldName + ";";
		
		return type.createField(contents, null, false, null);
	}
	
	public IMethod createSerializationConstructor() throws JavaModelException {
		String contents = "";
		
		contents += "@SuppressWarnings(\"unused\")\n";
		contents += "private " + type.getElementName() + "() {\n";
		contents += "	// For serialization only\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createConstructor(IField[] fields) throws JavaModelException {
		if(fields.length == 0) {
			IMethod serializationConstructor = type.getMethod(type.getElementName(), new String[0]);
			
			if(serializationConstructor.exists()) {
				ISourceRange range = serializationConstructor.getSourceRange();
				
				IBuffer buffer = cu.getBuffer();
				buffer.replace(range.getOffset(), range.getLength(), "");
			}
		}
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
	
}
