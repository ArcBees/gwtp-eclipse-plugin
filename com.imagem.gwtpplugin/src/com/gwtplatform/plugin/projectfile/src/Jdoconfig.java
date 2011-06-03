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

package com.gwtplatform.plugin.projectfile.src;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.gwtplatform.plugin.projectfile.ProjectFile;

/**
 *
 * @author Michael Renaud
 *
 */
public class Jdoconfig extends ProjectFile {

  public Jdoconfig(IProject project, IPath path) throws CoreException {
    super(project, path, "jdoconfig.xml");
  }

  public IFile createFile() throws CoreException {
    String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    contents += "<jdoconfig xmlns=\"http://java.sun.com/xml/ns/jdo/jdoconfig\"\n";
    contents += "\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n";
    contents += "\txsi:noNamespaceSchemaLocation=\"http://java.sun.com/xml/ns/jdo/jdoconfig\">\n\n";

    contents += "\t<persistence-manager-factory name=\"transactions-optional\">\n";
    contents += "\t\t<property name=\"javax.jdo.PersistenceManagerFactoryClass\"\n";
    contents += "\t\t\tvalue=\"org.datanucleus.store.appengine.jdo.DatastoreJDOPersistenceManagerFactory\"/>\n";
    contents += "\t\t<property name=\"javax.jdo.option.ConnectionURL\" value=\"appengine\"/>\n";
    contents += "\t\t<property name=\"javax.jdo.option.NontransactionalRead\" value=\"true\"/>\n";
    contents += "\t\t<property name=\"javax.jdo.option.NontransactionalWrite\" value=\"true\"/>\n";
    contents += "\t\t<property name=\"javax.jdo.option.RetainValues\" value=\"true\"/>\n";
    contents += "\t\t<property name=\"datanucleus.appengine.autoCreateDatastoreTxns\" value=\"true\"/>\n";
    contents += "\t</persistence-manager-factory>\n";
    contents += "</jdoconfig>";

    file.create(new ByteArrayInputStream(contents.getBytes()), false, null);

    return file;
  }

}
