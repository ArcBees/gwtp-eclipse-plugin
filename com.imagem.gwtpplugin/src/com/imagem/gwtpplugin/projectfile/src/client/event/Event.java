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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.project.SourceEditor;
import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.projectfile.Field;
import com.imagem.gwtpplugin.tool.Formatter;

public class Event implements IProjectFile {

	private final String EXTENSION = ".java";
	private String name;
	private String eventPackage;
	private Field[] fields;
	private boolean hasHandlers = false;

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

}
