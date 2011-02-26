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

package com.imagem.gwtpplugin.projectfile;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * 
 * @author Michael Renaud
 *
 */
public abstract class ProjectFile {
	
	protected IFile file;
	protected IProject project;
	
	public ProjectFile(IProject project, IPath path, String name) throws CoreException {
		this.project = project;
		IContainer container = (IContainer) project.findMember(path);
		
		file = container.getFile(new Path(name));
	}
	
	public IFile getFile() {
		return file;
	}
}
