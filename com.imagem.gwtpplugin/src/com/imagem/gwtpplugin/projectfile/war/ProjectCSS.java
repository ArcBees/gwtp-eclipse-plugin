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

package com.imagem.gwtpplugin.projectfile.war;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;

public class ProjectCSS implements IProjectFile {

	private final String EXTENSION = ".css";
	private String projectName;
	private String path;
	
	public ProjectCSS(String projectName, String path) {
		this.projectName = projectName;
		this.path = path;
	}
	
	@Override
	public String getName() {
		return projectName;
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
		String contents = "/** Add css rules here for your application. **/\n\n";

		return new ByteArrayInputStream(contents.getBytes());
	}
}
