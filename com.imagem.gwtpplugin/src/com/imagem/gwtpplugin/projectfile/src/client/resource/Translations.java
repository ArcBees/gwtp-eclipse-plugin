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

package com.imagem.gwtpplugin.projectfile.src.client.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class Translations implements IProjectFile {
	
	private final String EXTENSION = ".java";
	private String resourcePackage;
	
	public Translations(String resourcePackage) {
		this.resourcePackage = resourcePackage;
	}

	@Override
	public String getName() {
		return "Translations";
	}

	@Override
	public String getPackage() {
		return resourcePackage;
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

		contents += "import com.google.gwt.i18n.client.Constants;\n\n";

		contents += "public interface " + getName() + " extends Constants {\n\n";
		// TODO create subsections
		
		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
}
