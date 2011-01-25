package com.imagem.gwtpplugin.project.section;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.imagem.gwtpplugin.project.Creator;
import com.imagem.gwtpplugin.projectfile.src.server.guice.DispatchServletModule;
import com.imagem.gwtpplugin.projectfile.src.server.guice.GuiceServletConfig;
import com.imagem.gwtpplugin.projectfile.src.server.guice.ServerModule;

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
		
		ServerModule serverModule = new ServerModule(toPackage(guicePath));
		createProjectFile(project, serverModule);
	}
	
	private static void createHandlerPackage(IProject project, IPath serverPath) throws CoreException {
		IPath handlerPath = serverPath.append("handler");
		createFolder(project.getFolder(handlerPath));
		
		//ProjectActionHandler projectActionHandler = new ProjectActionHandler(project.getName(), toPackage(handlerPath), toPackage(serverPath.append("session")), toPackage(serverPath.removeLastSegments(1).append("shared")));
		//createProjectFile(project, projectActionHandler);
	}

}
