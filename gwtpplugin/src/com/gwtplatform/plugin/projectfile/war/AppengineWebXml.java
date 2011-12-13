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

import com.gwtplatform.plugin.projectfile.ProjectFile;

/**
 *
 * @author Michael Renaud
 *
 */
public class AppengineWebXml extends ProjectFile {

  public AppengineWebXml(IProject project, IPath path) throws CoreException {
    super(project, path, "appengine-web.xml");
  }

  public IFile createFile() throws CoreException {
    String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    contents += "<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\">\n\n";
    contents += "\t<application></application>\n";
    contents += "\t<version>1</version>\n\n";

    contents += "\t<!-- Configure serving/caching of GWT files -->\n";
    contents += "\t<static-files>\n";
    contents += "\t\t<include path=\"**\" />\n\n";

    contents += "\t\t<!-- The following line requires App Engine 1.3.2 SDK -->\n";
    contents += "\t\t<include path=\"**.nocache.*\" expiration=\"0s\" />\n\n";

    contents += "\t\t<include path=\"**.cache.*\" expiration=\"365d\" />\n";
    contents += "\t\t<exclude path=\"**.gwt.rpc\" />\n";
    contents += "\t</static-files>\n\n";

    contents += "\t<!-- Configure java.util.logging -->\n";
    contents += "\t<system-properties>\n";
    contents += "\t\t<property name=\"java.util.logging.config.file\" value=\"WEB-INF/logging.properties\"/>\n";
    contents += "\t</system-properties>\n\n";

    contents += "</appengine-web-app>";

    file.create(new ByteArrayInputStream(contents.getBytes()), false, new NullProgressMonitor());

    return file;
  }

}
