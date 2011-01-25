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
