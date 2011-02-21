package com.imagem.gwtpplugin.projectfile;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public abstract class ProjectWarFile {
	
	protected IFile file;
	protected IProject project;
	
	public ProjectWarFile(IProject project, IPath path, String name) throws CoreException {
		this.project = project;
		IContainer container = (IContainer) project.findMember(path);
		
		file = container.getFile(new Path(name));
	}
	
	public IFile getFile() {
		return file;
	}
}
