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
import com.imagem.gwtpplugin.projectfile.src.client.ActionCallback;
import com.imagem.gwtpplugin.projectfile.src.client.EntryPoint;
import com.imagem.gwtpplugin.projectfile.src.client.core.TestPresenter;
import com.imagem.gwtpplugin.projectfile.src.client.core.TestView;
import com.imagem.gwtpplugin.projectfile.src.client.gin.ClientModule;
import com.imagem.gwtpplugin.projectfile.src.client.gin.Ginjector;
import com.imagem.gwtpplugin.projectfile.src.client.place.PlaceAnnotation;
import com.imagem.gwtpplugin.projectfile.src.client.place.PlaceManager;
import com.imagem.gwtpplugin.projectfile.src.client.place.Tokens;
import com.imagem.gwtpplugin.projectfile.src.client.resource.Resources;
import com.imagem.gwtpplugin.projectfile.src.client.resource.Translations;

public class ClientSectionCreator extends Creator {

	public static void createClientPackage(IProject project, IPath basePath) throws CoreException {
		// client Package
		IPath clientPath = basePath.append("client");
		createFolder(project.getFolder(clientPath));

		// client.core Package
		createCorePackage(project, clientPath);

		// client.event Package
		createEventPackage(project, clientPath);

		// client.gin Package
		createGinPackage(project, clientPath);

		// client.place Package
		createPlacePakage(project, clientPath);

		// client.resource Package
		createResourcePackage(project, clientPath);

		EntryPoint entryPoint = new EntryPoint(project.getName(), toPackage(clientPath));
		createProjectFile(project, entryPoint);

		ActionCallback actionCallback = new ActionCallback(toPackage(clientPath));
		createProjectFile(project, actionCallback);
	}

	private static void createCorePackage(IProject project, IPath clientPath) throws CoreException {
		IPath corePath = clientPath.append("core");
		createFolder(project.getFolder(corePath));

		IPath presenterPath = corePath.append("presenter");
		createFolder(project.getFolder(presenterPath));

		IPath viewPath = corePath.append("view");
		createFolder(project.getFolder(viewPath));
		
		createTestPresenter(project, presenterPath, viewPath, clientPath);
	}

	private static void createEventPackage(IProject project, IPath clientPath) throws CoreException {
		IPath eventPath = clientPath.append("event");
		createFolder(project.getFolder(eventPath));
	}

	private static void createGinPackage(IProject project, IPath clientPath) throws CoreException {
		IPath ginPath = clientPath.append("gin");
		createFolder(project.getFolder(ginPath));
		
		Ginjector ginjector = new Ginjector(project.getName(), toPackage(ginPath), toPackage(clientPath.append("resource")), toPackage(clientPath));
		createProjectFile(project, ginjector);
		
		ClientModule clientModule = new ClientModule(project.getName(), toPackage(ginPath), toPackage(clientPath.append("place")), toPackage(clientPath.append("resource")), toPackage(clientPath));
		createProjectFile(project, clientModule);
	}
	
	private static void createPlacePakage(IProject project, IPath clientPath) throws CoreException {
		IPath placePath = clientPath.append("place");
		createFolder(project.getFolder(placePath));
		
		PlaceManager placeManager = new PlaceManager(project.getName(), toPackage(placePath));
		createProjectFile(project, placeManager);
		
		Tokens tokens = new Tokens(project.getName(), toPackage(placePath));
		createProjectFile(project, tokens);
		
		IPath annotationPath = placePath.append("annotation");
		createFolder(project.getFolder(annotationPath));
		
		PlaceAnnotation defaultPlace = new PlaceAnnotation("DefaultPlace", toPackage(annotationPath));
		createProjectFile(project, defaultPlace);
		
		// TODO ErrorPlace, UnauthorizedPlace, SecurePlace
	}
	
	private static void createResourcePackage(IProject project, IPath clientPath) throws CoreException {
		IPath resourcePath = clientPath.append("resource");
		createFolder(project.getFolder(resourcePath));
		
		Resources resources = new Resources(toPackage(resourcePath));
		createProjectFile(project, resources);
		
		Translations translations = new Translations(toPackage(resourcePath));
		createProjectFile(project, translations);
		
		IPath imagePath = resourcePath.append("image");
		createFolder(project.getFolder(imagePath));
		
		IPath stylePath = resourcePath.append("style");
		createFolder(project.getFolder(stylePath));
	}
	
	// TODO Test
	private static void createTestPresenter(IProject project, IPath presenterPath, IPath viewPath, IPath clientPath) throws CoreException {
		TestPresenter presenter = new TestPresenter(project.getName(), "Test", toPackage(presenterPath), toPackage(clientPath.append("place")));
		presenter.setCodeSplit(true);
		presenter.setPlace(true);
		presenter.setToken("test");
		createProjectFile(project, presenter);
		
		TestView view = new TestView("Test", toPackage(viewPath), toPackage(presenterPath), toPackage(clientPath.append("resource")));
		view.setUiBinder(false);
		createProjectFile(project, view);
	}
}
