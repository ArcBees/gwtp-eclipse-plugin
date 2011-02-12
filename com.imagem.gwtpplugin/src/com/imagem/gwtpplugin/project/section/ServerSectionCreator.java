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

package com.imagem.gwtpplugin.project.section;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.imagem.gwtpplugin.project.Creator;
import com.imagem.gwtpplugin.projectfile.src.server.guice.DispatchServletModule;
import com.imagem.gwtpplugin.projectfile.src.server.guice.GuiceServletConfig;
import com.imagem.gwtpplugin.projectfile.src.server.guice.HandlerModule;

public class ServerSectionCreator extends Creator {

	public static void createServerPackage(IProject project, IPath basePath) throws CoreException {
		// server Package
		IPath serverPath = basePath.append("server");
		createFolder(project.getFolder(serverPath));
		
		createGuicePackage(project, serverPath);

		createHandlerPackage(project, serverPath);
		
		//createServerInterfacePackage(project, serverPath);
		
		//createSessionPackage(project, serverPath, basePath.append("shared").append("model"));
		
		//Logger logger = new Logger(toPackage(serverPath));
		//createProjectFile(project, logger);
		
		//ServerConfig serverConfig = new ServerConfig(project.getName(), toPackage(serverPath));
		//createProjectFile(project, serverConfig);
	}

	private static void createGuicePackage(IProject project, IPath serverPath) throws CoreException {
		IPath guicePath = serverPath.append("guice");
		createFolder(project.getFolder(guicePath));
		
		DispatchServletModule dispatchServletModule = new DispatchServletModule(project.getName(), toPackage(guicePath)/*, toPackage(serverPath), toPackage(serverPath.append("serverinterface")), toPackage(serverPath.append("session")), toPackage(serverPath.append("handler"))*/);
		createProjectFile(project, dispatchServletModule);
		
		GuiceServletConfig guiceServletConfig = new GuiceServletConfig(project.getName(), toPackage(guicePath));
		createProjectFile(project, guiceServletConfig);
		
		HandlerModule serverModule = new HandlerModule(toPackage(guicePath));
		createProjectFile(project, serverModule);
	}
	
	private static void createHandlerPackage(IProject project, IPath serverPath) throws CoreException {
		IPath handlerPath = serverPath.append("handler");
		createFolder(project.getFolder(handlerPath));
		
		//ProjectActionHandler projectActionHandler = new ProjectActionHandler(project.getName(), toPackage(handlerPath), toPackage(serverPath.append("session")), toPackage(serverPath.removeLastSegments(1).append("shared")));
		//createProjectFile(project, projectActionHandler);
	}

}
