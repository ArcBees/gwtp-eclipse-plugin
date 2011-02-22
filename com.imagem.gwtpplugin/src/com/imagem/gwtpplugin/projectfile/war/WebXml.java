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
import org.eclipse.jdt.core.IType;

import com.imagem.gwtpplugin.projectfile.ProjectWarFile;

/**
 * 
 * @author Michael Renaud
 *
 */
public class WebXml extends ProjectWarFile {

	public WebXml(IProject project, IPath path) throws CoreException {
		super(project, path, "web.xml");
	}
	
	public IFile createFile(IFile projectHTML, IType guiceServletContextListener) throws CoreException {
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		contents += "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n";
		contents += "	xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\"\n";
		contents += "	version=\"2.5\">\n\n";
		
		contents += "	<display-name>" + project.getName() + "</display-name>\n\n";
		
		contents += "	<!-- Default page to serve -->\n";
		contents += "	<welcome-file-list>\n";
		contents += "		<welcome-file>" + projectHTML.getName() + "</welcome-file>\n";
		contents += "	</welcome-file-list>\n\n";
		
		contents += "	<!--\n";
		contents += "		This Guice listener hijacks all further filters and servlets. Extra\n";
		contents += "		filters and servlets have to be configured in your\n";
		contents += "		ServletModule#configureServlets() by calling\n";
		contents += "		serve(String).with(Class<? extends HttpServlet>) and\n";
		contents += "		filter(String).through(Class<? extends Filter)\n";
		contents += "	-->\n";
		contents += "	<listener>\n";
		contents += "		<listener-class>" + guiceServletContextListener.getFullyQualifiedName() + "</listener-class>\n";
		contents += "	</listener>\n\n";
		
		contents += "	<filter>\n";
		contents += "		<filter-name>guiceFilter</filter-name>\n";
		contents += "		<filter-class>com.google.inject.servlet.GuiceFilter</filter-class>\n";
		contents += "	</filter>\n\n";
		
		contents += "	<filter-mapping>\n";
		contents += "		<filter-name>guiceFilter</filter-name>\n";
		contents += "		<url-pattern>/*</url-pattern>\n";
		contents += "	</filter-mapping>\n\n";
		
		contents += "</web-app>";
		
		file.create(new ByteArrayInputStream(contents.getBytes()), false, null);
		
		return file;
	}

}
