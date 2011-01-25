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

package com.imagem.gwtpplugin.projectfile.src.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class EntryPoint implements IProjectFile {
	
	private final String EXTENSION = ".java";
	private String projectName;
	private String clientPackage;
	
	public EntryPoint(String projectName, String clientPackage) {
		this.projectName = projectName;
		this.clientPackage = clientPackage;
	}

	@Override
	public String getName() {
		return projectName;
	}

	@Override
	public String getPackage() {
		return clientPackage;
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
		
		contents += "import com.google.gwt.core.client.EntryPoint;\n";
		contents += "import com.google.gwt.core.client.GWT;\n";
		contents += "import " + clientPackage + ".gin." + projectName + "Ginjector;\n";
		contents += "import com.gwtplatform.mvp.client.DelayedBindRegistry;\n\n";
		
		contents += "public class " + getName() + " implements EntryPoint {\n";
		contents += "	private final " + projectName + "Ginjector ginjector = GWT.create(" + projectName + "Ginjector.class);\n\n";
		
		contents += "	public void onModuleLoad() {\n";
		contents += "		// This is required for Gwt-Platform proxy's generator\n";
		contents += "		DelayedBindRegistry.bind(ginjector);\n\n";
		
		contents += "		ginjector.getPlaceManager().revealCurrentPlace();\n";
		contents += "	}\n\n";
		
		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

}
