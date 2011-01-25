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

package com.imagem.gwtpplugin.projectfile.src.server.guice;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class GuiceServletConfig implements IProjectFile {
	
	private final String EXTENSION = ".java";
	private String projectName;
	private String guicePackage;

	public GuiceServletConfig(String projectName, String guicePackage) {
		this.projectName = projectName;
		this.guicePackage = guicePackage;
	}

	@Override
	public String getName() {
		return projectName + "GuiceServletConfig";
	}

	@Override
	public String getPackage() {
		return guicePackage;
	}

	@Override
	public String getPath() {
		return "src/" + getPackage().replace('.', '/');
	}
	
	@Override
	public String getExtension() {
		return EXTENSION;
	}

	public InputStream openContentStream() {
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import com.google.inject.Guice;\n";
		contents += "import com.google.inject.Injector;\n";
		contents += "import com.google.inject.servlet.GuiceServletContextListener;\n\n";

		contents += "public class " + getName() + " extends GuiceServletContextListener {\n\n";

		contents += "	@Override\n";
		contents += "	protected Injector getInjector() {\n";
		contents += "		return Guice.createInjector(new ServerModule(), new DispatchServletModule());\n";
		contents += "	}\n";

		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}
}
