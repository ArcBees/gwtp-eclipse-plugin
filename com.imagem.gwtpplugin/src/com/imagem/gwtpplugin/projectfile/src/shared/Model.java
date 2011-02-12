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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.projectfile.Field;
import com.imagem.gwtpplugin.tool.Formatter;

public class Model implements IProjectFile {

	private final String EXTENSION = ".java";
	private String name;
	private String modelPackage;
	private Field[] fields;
	private boolean generateEquals = true;

	public Model(String name, String modelPackage) {
		this.name = name;
		this.modelPackage = modelPackage;
	}

	public void setFields(Field[] fields) {
		this.fields = fields;
	}

	public void setGenerateEquals(boolean generateEquals) {
		this.generateEquals  = generateEquals;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPackage() {
		return modelPackage;
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

		contents += "import java.io.Serializable;\n\n";

		contents += "public class " + getName() + " implements Serializable {\n\n";

		contents += "	private static final long serialVersionUID = " + generator.nextLong() + "L;\n\n";

		for(Field field : fields) {
			contents += "	private " + field.getField() + ";\n";
		}
		contents += "\n";

		/*contents += "	public " + getName() + "() {\n";
		contents += "		init();\n";
		contents += "	}\n\n";

		contents += "	public void clear() {\n";
		contents += "		init();\n";
		contents += "	}\n\n";

		contents += "	private void init() {\n";
		for(Variable var : variables) {
			String defaultValue = "null";
			if(var.getType().equals("byte") || var.getType().equals("short") || var.getType().equals("int") ||
					var.getType().equals("Byte") || var.getType().equals("Short") || var.getType().equals("Integer"))
				defaultValue = "0";
			else if(var.getType().equals("long") || var.getType().equals("Long"))
				defaultValue = "0L";
			else if(var.getType().equals("float") || var.getType().equals("Float"))
				defaultValue = "0.0f";
			else if(var.getType().equals("double") || var.getType().equals("Double"))
				defaultValue = "0.0d";
			else if(var.getType().equals("boolean") || var.getType().equals("Boolean"))
				defaultValue = "false";
			else if(var.getType().equals("char"))
				defaultValue = "'\\u0000'";
			else if(var.getType().equals("String"))
				defaultValue = "\"\"";
			else
				defaultValue = "new " + var.getType() + "()";

			contents += "		set" + var.getCapName() + "(" + defaultValue + ");\n";
		}
		contents += "	}\n\n";*/

		/*for(Field field : fields) {
			contents += "	public void set" + field.getCapName() + "(" + field.getType() + " " + field.getName() + ") {this." + field.getName() + " = " + field.getName() + ";}\n";
		}
		contents += "\n";

		for(Field field : fields) {
			contents += "	public " + field.getType() + " get" + field.getCapName() + "() {return " + field.getName() + ";}\n";
		}
		contents += "\n";

		if(generateEquals) {
			contents += "	@Override\n";
			contents += "	public boolean equals(Object anObject) {\n";
			contents += "		if(this == anObject) return true;\n";
			contents += "		if(!(anObject instanceof " + getName() + ")) return false;\n";
			contents += "		" + getName() + " that = (" + getName() + ") anObject;\n\n";

			contents += "		return\n";
			for(int i = 0; i < fields.length; i++) {
				Field field = fields[i];

				contents += "			";
				if(field.getType().equals("byte") || field.getType().equals("short") || field.getType().equals("int") || field.getType().equals("long") || 
						field.getType().equals("char") || field.getType().equals("boolean"))
					contents += "this.get" + field.getCapName() + "() == that.get" + field.getCapName() + "()";
				else if(field.getType().equals("float"))
					contents += "Float.floatToIntBits(this.get" + field.getCapName() + "()) == Float.floatToIntBits(that.get" + field.getCapName() + "())";
				else if(field.getType().equals("double"))
					contents += "Double.doubleToLongBits(this.get" + field.getCapName() + "()) == Double.doubleToLongBits(that.get" + field.getCapName() + "())";
				else
					contents += "this.get" + field.getCapName() + "() == null ? that.get" + field.getCapName() + "() == null : this.get" + field.getCapName() + "().equals(that.get" + field.getCapName() + "())";

				if(i < fields.length - 1)
					contents += " &&\n";
				else
					contents += ";\n";
			}
			contents += "	}\n\n";

			contents += "	@Override\n";
			contents += "	public int hashCode() {\n";
			contents += "		final int multiplier = 23;\n";
			contents += "		int hashCode = 17;\n";
			for(Field field : fields) {
				contents += "		hashCode = multiplier * hashCode + ";
				if(field.getType().equals("boolean"))
					contents += "(" + field.getName() + " ? 1 : 0);\n";
				else if(field.getType().equals("byte") || field.getType().equals("short") || field.getType().equals("int") || field.getType().equals("char"))
					contents += "(int) " + field.getName() + ";\n";
				else if(field.getType().equals("long"))
					contents += "(int) (" + field.getName() + "^(" + field.getName() + ">>>32));\n";
				else if(field.getType().equals("double"))
					contents += "(int) (Double.doubleToLongBits(" + field.getName() + ")^(Double.doubleToLongBits(" + field.getName() + ")>>>32));\n";
				else if(field.getType().equals("float"))
					contents += "Float.floatToIntBits(" + field.getName() + ");\n";
				else
					contents += "(" + field.getName() + " == null ? 0 : " + field.getName() + ".hashCode());\n";
			}
			contents += "		return hashCode;\n";
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
}
