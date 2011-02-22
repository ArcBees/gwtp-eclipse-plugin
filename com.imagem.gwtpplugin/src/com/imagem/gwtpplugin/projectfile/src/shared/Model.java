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

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.imagem.gwtpplugin.projectfile.ProjectClass;

/**
 * 
 * @author Michael Renaud
 *
 */
public class Model extends ProjectClass {

	private static final String I_SERIALIZABLE = "java.io.Serializable";
	
	public Model(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		super(root, fullyQualifiedName);
	}
	
	public Model(IPackageFragmentRoot root, String packageName, String elementName) throws JavaModelException {
		super(root, packageName, elementName);
		if(type == null) {
			cu.createPackageDeclaration(packageName, null);

			cu.createImport(I_SERIALIZABLE, null, null);
			String contents = "public class " + elementName + " implements Serializable {\n\n}";
			
			type = cu.createType(contents, null, false, null);
		}
	}
	
	public IMethod createConstructor() throws JavaModelException {
		String contents = "public " + type.getElementName() + "() {\n\n}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IField createSerializationField() throws JavaModelException {
		Random generator = new Random();
		
		String contents = "private static final long serialVersionUID = " + generator.nextLong() + "L;";
		
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
	
	public IMethod createSetterMethod(IField field) throws JavaModelException {
		String contents = "public void set" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1);
		contents += "(" + Signature.toString(field.getTypeSignature()) + " " + field.getElementName() + ") {\n";
		contents += "	this." + field.getElementName() + " = " + field.getElementName() + ";\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createGetterMethod(IField field) throws JavaModelException {
		String contents = "public " + Signature.toString(field.getTypeSignature());
		contents += " get" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1) + "() {\n";
		contents += "	return " + field.getElementName() + ";\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createEqualsMethod(IField[] fields) throws IllegalArgumentException, JavaModelException {
		String contents = "";
		
		contents += "@Override\n";
		contents += "public boolean equals(Object anObject) {\n";
		contents += "	if(this == anObject) return true;\n";
		contents += "	if(!(anObject instanceof " + type.getElementName() + ")) return false;\n";
		contents += "	" + type.getElementName() + " that = (" + type.getElementName() + ") anObject;\n\n";

		contents += "	return\n";
		for(int i = 0; i < fields.length; i++) {
			IField field = fields[i];
			String fieldType = Signature.toString(field.getTypeSignature());

			contents += "		";
			if(fieldType.equals("byte") || fieldType.equals("short") || fieldType.equals("int") || fieldType.equals("long") || 
					fieldType.equals("char") || fieldType.equals("boolean"))
				contents += "this.get" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1) + "() == that.get" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1) + "()";
			else if(fieldType.equals("float"))
				contents += "Float.floatToIntBits(this.get" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1) + "()) == Float.floatToIntBits(that.get" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1) + "())";
			else if(fieldType.equals("double"))
				contents += "Double.doubleToLongBits(this.get" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1) + "()) == Double.doubleToLongBits(that.get" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1) + "())";
			else
				contents += "this.get" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1) + "() == null ? that.get" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1) + "() == null : this.get" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1) + "().equals(that.get" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1) + "())";

			if(i < fields.length - 1)
				contents += " &&\n";
			else
				contents += ";\n";
		}
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
	
	public IMethod createHashCodeMethod(IField[] fields) throws IllegalArgumentException, JavaModelException {
		String contents = "";
		contents += "@Override\n";
		contents += "public int hashCode() {\n";
		contents += "	final int multiplier = 23;\n";
		contents += "	int hashCode = 17;\n";
		for(IField field : fields) {
			String fieldType = Signature.toString(field.getTypeSignature());
			
			contents += "	hashCode = multiplier * hashCode + ";
			if(fieldType.equals("boolean"))
				contents += "(" + field.getElementName() + " ? 1 : 0);\n";
			else if(fieldType.equals("byte") || fieldType.equals("short") || fieldType.equals("int") || fieldType.equals("char"))
				contents += "(int) " + field.getElementName() + ";\n";
			else if(fieldType.equals("long"))
				contents += "(int) (" + field.getElementName() + "^(" + field.getElementName() + ">>>32));\n";
			else if(fieldType.equals("double"))
				contents += "(int) (Double.doubleToLongBits(" + field.getElementName() + ")^(Double.doubleToLongBits(" + field.getElementName() + ")>>>32));\n";
			else if(fieldType.equals("float"))
				contents += "Float.floatToIntBits(" + field.getElementName() + ");\n";
			else
				contents += "(" + field.getElementName() + " == null ? 0 : " + field.getElementName() + ".hashCode());\n";
		}
		contents += "	return hashCode;\n";
		contents += "}";
		
		return type.createMethod(contents, null, false, null);
	}
}
