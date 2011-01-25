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

package com.imagem.gwtpplugin.project;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.projectfile.IUpdatableFile;

public abstract class Creator {

	/**
	 * Create a folder
	 * 
	 * @param folder
	 * @throws CoreException
	 */
	public static void createFolder(IFolder folder) throws CoreException {
		IContainer parent = folder.getParent();
		if (parent instanceof IFolder) {
			createFolder((IFolder) parent);
		}
		if (!folder.exists()) {
			folder.create(false, true, null);
		}
	}

	/**
	 * Create a file
	 * 
	 * @param project
	 * @param projectFile
	 * @throws CoreException
	 */
	public static void createProjectFile(IProject project, IProjectFile projectFile) throws CoreException {
		createProjectFile(project, projectFile, false);
	}

	/**
	 * Create a file
	 * 
	 * @param project
	 * @param projectFile
	 * @param open
	 * @throws CoreException
	 */
	public static void createProjectFile(IProject project, IProjectFile projectFile, boolean open) throws CoreException {
		IContainer container = (IContainer) project.findMember(projectFile.getPath());
		if(container == null) {
			createFolder(project.getFolder(projectFile.getPath()));
			container = (IContainer) project.findMember(projectFile.getPath());
		}

		IFile file = container.getFile(new Path(projectFile.getName() + projectFile.getExtension()));
		try {
			InputStream inputStream = projectFile.openContentStream();
			file.create(inputStream, true, null);
			inputStream.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		if(open) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditor(page, file, false);
			} 
			catch (PartInitException e) {
			}
		}
	}

	/**
	 * Update a file
	 * 
	 * @param project
	 * @param updatableFile
	 * @throws CoreException
	 */
	public static void updateProjectFile(IProject project, IUpdatableFile updatableFile) throws CoreException {
		IContainer container = (IContainer) project.findMember(updatableFile.getPath());
		if(container != null) {
			final IFile file = container.getFile(new Path(updatableFile.getName() + updatableFile.getExtension()));
			if(file.exists()) {
				try {
					InputStream inputStream = updatableFile.updateFile(file.getContents());
					file.setContents(inputStream, true, true, null);
					inputStream.close();
				}
				catch(IOException e) {
				}
			}
		}
	}

	/**
	 * Check if file exist in project
	 * 
	 * @param project
	 * @param projectFile
	 * @return
	 * @throws CoreException
	 */
	public static boolean exist(IProject project, IProjectFile projectFile) throws CoreException {
		IContainer container = (IContainer) project.findMember(projectFile.getPath());
		if(container != null) {
			final IFile file = container.getFile(new Path(projectFile.getName() + projectFile.getExtension()));
			if(file.exists()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Convert path to package
	 * 
	 * @param path
	 * @return
	 */
	public static String toPackage(IPath path) {
		return path.toString().substring(4).replace('/', '.');
	}
}
