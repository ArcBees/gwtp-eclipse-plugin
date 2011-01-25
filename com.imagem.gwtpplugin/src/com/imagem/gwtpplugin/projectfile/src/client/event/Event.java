/**
 * Copyright 2011 Les Syst�mes M�dicaux Imagem Inc.
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
import com.imagem.gwtpplugin.projectfile.Variable;
import com.imagem.gwtpplugin.tool.Formatter;

public class Event implements IProjectFile {

	private final String EXTENSION = ".java";
	private String name;
	private String eventPackage;
	private Variable[] parameters;
	private boolean hasHandlers = false;

	public Event(String name, String eventPackage) {
		this.name = name;
		this.eventPackage = eventPackage;
	}
	
	public void setParameters(Variable... parameters) {
		this.parameters = parameters;
	}
	
	public void hasHandlers(boolean hasHandlers) {
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
		if(parameters == null)
			parameters = new Variable[0];
		
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import com.google.gwt.event.shared.GwtEvent;\n";
		if(!hasHandlers)
			contents += "import com.gwtplatform.mvp.client.HasEventBus;\n";
		contents += "\n";
		
		contents += "public class " + getName() + " extends GwtEvent<" + name + "Handler> {\n";
		contents += "	public static Type<" + name + "Handler> TYPE = new Type<" + name + "Handler>();\n\n";
		

		for(Variable param : parameters) {
			contents += "	private final " + param.toString() + ";\n";
		}
		contents += "\n";
		
		contents += "	public " + getName() + "(";
		String separator = "";
		for(Variable param : parameters) {
			contents += separator + "final " + param.toString();
			separator = ", ";
		}
		contents += ") {\n";
		for(Variable param : parameters) {
			contents += "		this." + param.getName() + " = " + param.getName() + ";\n";
		}
		contents += "	}\n\n";
		
		for(Variable param : parameters) {
			if((param.getType().equals("boolean") || param.getType().equals("Boolean")) && (param.getName().startsWith("is") || param.getName().startsWith("has")))
				contents += "	public " + param.toString() + "() {\n";
			else
				contents += "	public " + param.getType() + " get" + param.getCapName() + "() {\n";
			contents += "		return " + param.getName() + ";\n";
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
			contents += "	public static void fire(HasEventBus source";
		for(Variable param : parameters) {
			contents += ", " + param.toString();
		}
		contents += ") {\n";
		contents += "		source.fireEvent(new " + getName() + "(";
		separator = "";
		for(Variable param : parameters) {
			contents += separator + param.getName();
			separator = ", ";
		}
		contents += "));\n";
		contents += "	}\n\n";
		
		contents += "}";
		
		for(Variable param : parameters) {
			if(!param.getImport().isEmpty())
				contents = SourceEditor.insertImport(contents, param.getImport());
		}

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

}
