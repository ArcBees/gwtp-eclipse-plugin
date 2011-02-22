/**
 * Copyright 2011 IMAGEM Solutions TI santé
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
public class AppengineWebXml extends ProjectWarFile {

	public AppengineWebXml(IProject project, IPath path) throws CoreException {
		super(project, path, "appengine-web.xml");
	}
	
	public IFile createFile() throws CoreException {
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		contents += "<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\">\n\n";
		contents += "	<application></application>\n";
		contents += "	<version>1</version>\n\n";
		
		contents += "	<!-- Configure serving/caching of GWT files -->\n";
		contents += "	<static-files>\n";
		contents += "		<include path=\"**\" />\n\n";
		
		contents += "		<!-- The following line requires App Engine 1.3.2 SDK -->\n";
		contents += "		<include path=\"**.nocache.*\" expiration=\"0s\" />\n\n";
		
		contents += "		<include path=\"**.cache.*\" expiration=\"365d\" />\n";
		contents += "		<exclude path=\"**.gwt.rpc\" />\n";
		contents += "	</static-files>\n\n";
		
		contents += "	<!-- Configure java.util.logging -->\n";
		contents += "	<system-properties>\n";
		contents += "		<property name=\"java.util.logging.config.file\" value=\"WEB-INF/logging.properties\"/>\n";
		contents += "	</system-properties>\n\n";
		
		contents += "</appengine-web-app>";
		
		file.create(new ByteArrayInputStream(contents.getBytes()), false, null);
		
		return file;
	}

}
