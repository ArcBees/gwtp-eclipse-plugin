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

import com.imagem.gwtpplugin.projectfile.ProjectFile;

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
		contents += "	<head>\n";
		contents += "		<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n\n";
		
		contents += "		<!--                                                               -->\n";
		contents += "		<!-- Consider inlining CSS to reduce the number of requested files -->\n";
		contents += "		<!--                                                               -->\n";
		contents += "		<link type=\"text/css\" rel=\"stylesheet\" href=\"" + projectName + ".css\">\n\n";
		
		contents += "		<!--                                           -->\n";
		contents += "		<!-- Any title is fine                         -->\n";
		contents += "		<!--                                           -->\n";
		contents += "		<title>" + projectName + "</title>\n\n";
		
		contents += "		<!--                                           -->\n";
		contents += "		<!-- This script loads your compiled module.   -->\n";
		contents += "		<!-- If you add any GWT meta tags, they must   -->\n";
		contents += "		<!-- be added before this line.                -->\n";
		contents += "		<!--                                           -->\n";
		contents += "		<script type=\"text/javascript\" language=\"javascript\" src=\"" + projectName.toLowerCase() + "/" + projectName.toLowerCase() + ".nocache.js\"></script>\n";
		contents += "	</head>\n\n";
		
		contents += "	<!--                                           -->\n";
		contents += "	<!-- The body can have arbitrary html, or      -->\n";
		contents += "	<!-- you can leave the body empty if you want  -->\n";
		contents += "	<!-- to create a completely dynamic UI.        -->\n";
		contents += "	<!--                                           -->\n";
		contents += "	<body>\n\n";
		
		contents += "		<!-- OPTIONAL: include this if you want history support -->\n";
		contents += "		<iframe src=\"javascript:''\" id=\"__gwt_historyFrame\" tabIndex='-1' style=\"position: absolute; width: 0;height: 0; border: 0;\"></iframe>\n\n";
		
		contents += "		<!-- RECOMMENDED if your web app will not function without JavaScript enabled -->\n";
		contents += "		<noscript>\n";
		contents += "			<div style=\"width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif;\">\n";
		contents += "				Your web browser must have JavaScript enabled\n";
		contents += "				in order for this application to display correctly.\n";
		contents += "			</div>\n";
		contents += "		</noscript>\n";
		contents += "	</body>\n";
		contents += "</html>";
		
		file.create(new ByteArrayInputStream(contents.getBytes()), false, null);
		
		return file;
	}
}
