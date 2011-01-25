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

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class HasHandlers implements IProjectFile {

	private final String EXTENSION = ".java";
	private String name;
	private String eventPackage;

	public HasHandlers(String name, String eventPackage) {
		this.name = name;
		this.eventPackage = eventPackage;
	}

	@Override
	public String getName() {
		return "Has" + name + "Handlers";
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
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import com.google.gwt.event.shared.HandlerRegistration;\n";
		contents += "import com.google.gwt.event.shared.HasHandlers;\n\n";
		
		contents += "public interface " + getName() + " extends HasHandlers {\n\n";
		
		contents += "	public HandlerRegistration add" + name + "Handler(" + name + "Handler handler);\n";
		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

}
