package com.imagem.gwtpplugin.project.section;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.imagem.gwtpplugin.project.Creator;

public class SharedSectionCreator extends Creator {

	/**
	 * Create Shared package structure and base classes
	 * 
	 * @param project
	 * @param basePath
	 * @param options 
	 * @throws CoreException
	 */
	public static void createSharedPackage(IProject project, IPath basePath) throws CoreException {
		// shared Package
		IPath sharedPath = basePath.append("shared");
		createFolder(project.getFolder(sharedPath));

		// shared.action Package
		IPath actionPath = sharedPath.append("action");
		createFolder(project.getFolder(actionPath));

		// shared.model Package
		IPath modelPath = sharedPath.append("model");
		createFolder(project.getFolder(modelPath));
	}

}
