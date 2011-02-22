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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;

/**
 * 
 * @author Michael Renaud
 *
 */
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
