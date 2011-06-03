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

import com.gwtplatform.plugin.projectfile.ProjectFile;

/**
 *
 * @author Michael Renaud
 *
 */
public class ProjectHTML extends ProjectFile {

  public ProjectHTML(IProject project, IPath path, String name) throws CoreException {
    super(project, path, name + ".html");
  }

  public IFile createFile() throws CoreException {
    String projectName = project.getName();

    String contents = "<!doctype html>\n";
    contents += "<!-- The DOCTYPE declaration above will set the    -->\n";
    contents += "<!-- browser's rendering engine into               -->\n";
    contents += "<!-- \"Standards Mode\". Replacing this declaration  -->\n";
    contents += "<!-- with a \"Quirks Mode\" doctype may lead to some -->\n";
    contents += "<!-- differences in layout.                        -->\n\n";

    contents += "<html>\n";
    contents += "\t<head>\n";
    contents += "\t\t<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n\n";

    contents += "\t\t<!--                                                               -->\n";
    contents += "\t\t<!-- Consider inlining CSS to reduce the number of requested files -->\n";
    contents += "\t\t<!--                                                               -->\n";
    contents += "\t\t<link type=\"text/css\" rel=\"stylesheet\" href=\"" + projectName
        + ".css\">\n\n";

    contents += "\t\t<!--                                           -->\n";
    contents += "\t\t<!-- Any title is fine                         -->\n";
    contents += "\t\t<!--                                           -->\n";
    contents += "\t\t<title>" + projectName + "</title>\n\n";

    contents += "\t\t<!--                                           -->\n";
    contents += "\t\t<!-- This script loads your compiled module.   -->\n";
    contents += "\t\t<!-- If you add any GWT meta tags, they must   -->\n";
    contents += "\t\t<!-- be added before this line.                -->\n";
    contents += "\t\t<!--                                           -->\n";
    contents += "\t\t<script type=\"text/javascript\" language=\"javascript\" src=\""
        + projectName.toLowerCase() + "/" + projectName.toLowerCase() + ".nocache.js\"></script>\n";
    contents += "\t</head>\n\n";

    contents += "\t<!--                                           -->\n";
    contents += "\t<!-- The body can have arbitrary html, or      -->\n";
    contents += "\t<!-- you can leave the body empty if you want  -->\n";
    contents += "\t<!-- to create a completely dynamic UI.        -->\n";
    contents += "\t<!--                                           -->\n";
    contents += "\t<body>\n\n";

    contents += "\t\t<!-- OPTIONAL: include this if you want history support -->\n";
    contents += "\t\t<iframe src=\"javascript:''\" id=\"__gwt_historyFrame\" tabIndex='-1' style=\"position: absolute; width: 0;height: 0; border: 0;\"></iframe>\n\n";

    contents += "\t\t<!-- RECOMMENDED if your web app will not function without JavaScript enabled -->\n";
    contents += "\t\t<noscript>\n";
    contents += "\t\t\t<div style=\"width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif;\">\n";
    contents += "\t\t\t\tYour web browser must have JavaScript enabled\n";
    contents += "\t\t\t\tin order for this application to display correctly.\n";
    contents += "\t\t\t</div>\n";
    contents += "\t\t</noscript>\n";
    contents += "\t</body>\n";
    contents += "</html>";

    file.create(new ByteArrayInputStream(contents.getBytes()), false, null);

    return file;
  }
}
