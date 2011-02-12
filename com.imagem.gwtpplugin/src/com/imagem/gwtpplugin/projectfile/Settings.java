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

package com.imagem.gwtpplugin.projectfile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

public class Settings implements IProjectFile {

	private final String EXTENSION = ".prefs";
	private String path;
	
	public Settings(String path) {
		this.path = path;
	}
	
	@Override
	public String getName() {
		return "org.eclipse.core.resources";
	}

	@Override
	public String getPackage() {
		return "";
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getExtension() {
		return EXTENSION;
	}

	@Override
	public InputStream openContentStream() {
		String contents = "#" + new Date().toString() + "\n";
		contents += "eclipse.preferences.version=1\n";
		contents += "encoding/<project>=ISO-8859-1\n";

		return new ByteArrayInputStream(contents.getBytes());
	}
}
