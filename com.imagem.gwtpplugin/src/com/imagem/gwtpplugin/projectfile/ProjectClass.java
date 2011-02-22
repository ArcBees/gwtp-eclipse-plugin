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

package com.imagem.gwtpplugin.projectfile;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * 
 * @author Michael Renaud
 *
 */
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
