package com.imagem.gwtpplugin.projectfile;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public abstract class ProjectClass {
	protected IType type;
	protected ICompilationUnit cu;
	
	public ProjectClass(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		type = root.getJavaProject().findType(fullyQualifiedName);
		cu = type.getCompilationUnit();
		cu.becomeWorkingCopy(null);
	}
	
	public ProjectClass(IPackageFragmentRoot root, String packageName, String elementName) throws JavaModelException {
		type = root.getJavaProject().findType(packageName + "." + elementName);
		if(type == null) {
			String cuName = elementName + ".java";
			
			IPackageFragment pack = root.createPackageFragment(packageName, false, null);
			cu = pack.createCompilationUnit(cuName, "", false, null);
			cu.becomeWorkingCopy(null);
		}
	}
	
	public void commit() throws JavaModelException {
		cu.commitWorkingCopy(true, null);
		cu.discardWorkingCopy();
	}
	
	public void discard() throws JavaModelException {
		cu.discardWorkingCopy();
		if(cu.getSource().isEmpty()) {
			cu.delete(true, null);
		}
	}
	
	public IType getType() {
		return type;
	}
}
