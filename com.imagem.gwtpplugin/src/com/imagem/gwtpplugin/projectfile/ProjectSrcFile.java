package com.imagem.gwtpplugin.projectfile;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;

public abstract class ProjectSrcFile {

	protected IFile file;
	protected IPackageFragmentRoot root;
	
	public ProjectSrcFile(IPackageFragmentRoot root, String packageName, String elementName) throws CoreException {
		this.root = root;
		IContainer container = (IContainer) root.createPackageFragment(packageName, false, null).getResource();
		
		file = container.getFile(new Path(elementName));
	}
	
	public IFile getFile() {
		return file;
	}
}
