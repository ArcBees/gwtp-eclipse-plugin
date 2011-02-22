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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.imagem.gwtpplugin.projectfile.ProjectWarFile;

/**
 * 
 * @author Michael Renaud
 *
 */
public class Logging extends ProjectWarFile {

	public Logging(IProject project, IPath path) throws CoreException {
		super(project, path, "logging.properties");
	}
	
	public IFile createFile() throws CoreException {
		String contents = "# A default java.util.logging configuration.\n";
		contents += "# (All App Engine logging is through java.util.logging by default).\n";
		contents += "#\n";
		contents += "# To use this configuration, copy it into your application's WEB-INF\n";
		contents += "# folder and add the following to your appengine-web.xml:\n";
		contents += "# \n";
		contents += "# <system-properties>\n";
		contents += "#   <property name=\"java.util.logging.config.file\" value=\"WEB-INF/logging.properties\"/>\n";
		contents += "# </system-properties>\n";
		contents += "#\n\n";

		contents += "# Set the default logging level for all loggers to WARNING\n";
		contents += ".level = WARNING\n";
		
		file.create(new ByteArrayInputStream(contents.getBytes()), false, null);
		
		return file;
	}
}
