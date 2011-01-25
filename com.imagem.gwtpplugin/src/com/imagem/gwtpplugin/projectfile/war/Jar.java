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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.Platform;

import com.imagem.gwtpplugin.Activator;
import com.imagem.gwtpplugin.projectfile.IProjectFile;

public class Jar implements IProjectFile {
	
	private final String EXTENSION = ".jar";
	private String jarName;
	private String path;

	public Jar(String jarName, String path) {
		this.jarName = jarName;
		this.path = path;
	}
	
	@Override
	public String getName() {
		return jarName;
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
		try {
			return Platform.getBundle(Activator.PLUGIN_ID).getEntry("/file/" + getName() + getExtension()).openStream();
		}
		catch (IOException e) {
			return null;
		}
	}
}
