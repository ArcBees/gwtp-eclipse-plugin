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

import java.io.InputStream;
import java.util.Random;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.imagem.gwtpplugin.projectfile.Field;
import com.imagem.gwtpplugin.projectfile.IProjectFile;

public class Action implements IProjectFile {

	private final String EXTENSION = ".java";
	private String name;
	private String actionPackage;
	private String superclass;
	private Field[] fields;

	public Action(String name, String actionPackage) {
		this.name = name;
		this.actionPackage = actionPackage;
	}

	public void setSuperclass(String superclass) {
		this.superclass = superclass;
	}

	public void setFields(Field... fields) {
		this.fields = fields;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPackage() {
		return actionPackage;
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
		/*Random generator = new Random();
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import " + superclass + ";\n\n";
		
		String[] classSplit = superclass.split("\\.");

		contents += "public class " + getName() + " extends " + classSplit[classSplit.length - 1] + "<" + name + "Result> {\n\n";

		contents += "	private static final long serialVersionUID = " + generator.nextLong() + "L;\n\n";

		for(int i = 0; i < fields.length; i++) {
			contents += "	private " + fields[i].getField() + ";\n";
		}
		contents += "\n";

		if(fields.length > 0) {
			contents += "	@SuppressWarnings(\"unused\")\n";
			contents += "	private " + getName() + "() {\n";
			contents += "		// For serialization only\n";
			contents += "	}\n\n";
		}

		contents += "	public " + getName() + "(";
		String separator = "";
		for(Field field : fields) {
			contents += separator + field.getField();
			separator = ", ";
		}
		contents += ") {\n";
		for(Field field : fields) {
			contents += "		this." + field.getName() + " = " + field.getName() + ";\n";
		}
		contents += "	}\n\n";

		for(Field field : fields) {
			contents += "	public " + field.getType() + " get" + field.getCapName() + "() {\n";
			contents += "		return " + field.getName() + ";\n";
			contents += "	}\n\n";
		}

		contents += "}";

		for(Field field : fields) {
			if(!field.getQualifiedType().isEmpty())
				contents = SourceEditor.insertImport(contents, field.getQualifiedType());
		}

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());*/
		return null;
	}
	
	// New Version
	private IType type;
	private ICompilationUnit cu;
	
	public Action(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		type = root.getJavaProject().findType(fullyQualifiedName);
		cu = type.getCompilationUnit();
	}
	
	public Action(IPackageFragmentRoot root, String packageName, String elementName, IType superclass, IType result) throws JavaModelException {
		type = root.getJavaProject().findType(packageName + "." + elementName);
		if(type == null) {
			String cuName = elementName + ".java";
			
			IPackageFragment pack = root.createPackageFragment(packageName, false, null);
			ICompilationUnit cu = pack.createCompilationUnit(cuName, "", false, null);
			cu.createPackageDeclaration(packageName, null);

			cu.createImport(superclass.getFullyQualifiedName(), null, null);
			cu.createImport(result.getFullyQualifiedName(), null, null);
			String contents = "public class " + elementName + " extends " + superclass.getElementName() + "<" + result.getElementName() + "> {\n\n}";
	
			type = cu.createType(contents, null, false, null);
		}
		cu = type.getCompilationUnit();
	}
	
	public IType getType() {
		return type;
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
