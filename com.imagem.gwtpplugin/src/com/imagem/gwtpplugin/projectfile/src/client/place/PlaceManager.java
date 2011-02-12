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

package com.imagem.gwtpplugin.projectfile.src.client.place;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class PlaceManager implements IProjectFile {
	
	private final String EXTENSION = ".java";
	private String projectName;
	private String placePackage;

	public PlaceManager(String projectName, String placePackage) {
		this.projectName = projectName;
		this.placePackage = placePackage;
	}
	
	@Override
	public String getName() {
		return projectName + "PlaceManager";
	}

	@Override
	public String getPackage() {
		return placePackage;
	}

	@Override
	public String getPath() {
		return "src/" + placePackage.replace('.', '/');
	}
	
	@Override
	public String getExtension() {
		return EXTENSION;
	}

	@Override
	public InputStream openContentStream() {
		String contents = "package " + getPackage() + ";\n\n";

		contents += "import com.google.gwt.event.shared.EventBus;\n";
		contents += "import com.google.inject.Inject;\n";
		contents += "import " + placePackage + ".annotation.DefaultPlace;\n";
		contents += "import com.gwtplatform.mvp.client.proxy.PlaceManagerImpl;\n";
		contents += "import com.gwtplatform.mvp.client.proxy.PlaceRequest;\n";
		contents += "import com.gwtplatform.mvp.client.proxy.TokenFormatter;\n";
		
		
		contents += "public class " + getName() + " extends PlaceManagerImpl {\n\n";
		
		//contents += "	private final EventBus eventBus;\n\n";
		// TODO Translations, Session
		contents += "	private final PlaceRequest defaultPlaceRequest;\n\n";
		
		contents += "	@Inject\n";
		contents += "	public " + getName() + "(final EventBus eventBus, \n";
		contents += "							 final TokenFormatter tokenFormatter, \n";
		contents += "							 @DefaultPlace final String defaultPlaceNameToken) {\n";
		contents += "		super(eventBus, tokenFormatter);\n\n";

		//contents += "		this.eventBus = eventBus;\n\n";
		
		contents += "		this.defaultPlaceRequest = new PlaceRequest(defaultPlaceNameToken);\n";
		contents += "	}\n\n";

		contents += "	@Override\n";
		contents += "	public void revealDefaultPlace() {\n";
		contents += "		revealPlace(defaultPlaceRequest);\n";
		contents += "	}\n\n";
		
		contents += "	@Override\n";
		contents += "	public void revealErrorPlace(String invalidHistoryToken) {\n";
		contents += "		super.revealErrorPlace(invalidHistoryToken);\n";
		contents += "		// TODO replace by implemented ErrorPlace\n";
		contents += "	}\n\n";
		
		contents += "	@Override\n";
		contents += "	public void revealUnauthorizedPlace(String unauthorizedHistoryToken) {\n";
		contents += "		super.revealUnauthorizedPlace(unauthorizedHistoryToken);\n";
		contents += "		// TODO replace by implemented UnauthorizedPlace\n";
		contents += "	}\n\n";
		
		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

}
