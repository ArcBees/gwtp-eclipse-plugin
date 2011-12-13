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

package com.gwtplatform.plugin.projectfile.war;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IType;

import com.gwtplatform.plugin.projectfile.ProjectFile;

/**
 *
 * @author Michael Renaud
 *
 */
public class WebXml extends ProjectFile {

  public WebXml(IProject project, IPath path) throws CoreException {
    super(project, path, "web.xml");
  }

  public IFile createFile(IFile projectHTML, IType guiceServletContextListener)
      throws CoreException {
    String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    contents += "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n";
    contents += "\txsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\"\n";
    contents += "\tversion=\"2.5\">\n\n";

    contents += "\t<display-name>" + project.getName() + "</display-name>\n\n";

    contents += "\t<!-- Default page to serve -->\n";
    contents += "\t<welcome-file-list>\n";
    contents += "\t\t<welcome-file>" + projectHTML.getName() + "</welcome-file>\n";
    contents += "\t</welcome-file-list>\n\n";

    contents += "\t<!--\n";
    contents += "\t\tThis Guice listener hijacks all further filters and servlets. Extra\n";
    contents += "\t\tfilters and servlets have to be configured in your\n";
    contents += "\t\tServletModule#configureServlets() by calling\n";
    contents += "\t\tserve(String).with(Class<? extends HttpServlet>) and\n";
    contents += "\t\tfilter(String).through(Class<? extends Filter)\n";
    contents += "\t-->\n";
    contents += "\t<listener>\n";
    contents += "\t\t<listener-class>" + guiceServletContextListener.getFullyQualifiedName()
        + "</listener-class>\n";
    contents += "\t</listener>\n\n";

    contents += "\t<filter>\n";
    contents += "\t\t<filter-name>guiceFilter</filter-name>\n";
    contents += "\t\t<filter-class>com.google.inject.servlet.GuiceFilter</filter-class>\n";
    contents += "\t</filter>\n\n";

    contents += "\t<filter-mapping>\n";
    contents += "\t\t<filter-name>guiceFilter</filter-name>\n";
    contents += "\t\t<url-pattern>/*</url-pattern>\n";
    contents += "\t</filter-mapping>\n\n";

    contents += "</web-app>";

    file.create(new ByteArrayInputStream(contents.getBytes()), false, new NullProgressMonitor());

    return file;
  }

}
