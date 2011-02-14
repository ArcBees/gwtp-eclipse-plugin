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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.PreferenceConstants;

import com.google.appengine.eclipse.core.nature.GaeNature;
import com.google.appengine.eclipse.core.sdk.GaeSdkContainer;
import com.google.gdt.eclipse.suite.launch.WebAppLaunchUtil;
import com.google.gwt.eclipse.core.nature.GWTNature;
import com.google.gwt.eclipse.core.runtime.GWTRuntimeContainer;
import com.imagem.gwtpplugin.project.section.ClientSectionCreator;
import com.imagem.gwtpplugin.project.section.ServerSectionCreator;
import com.imagem.gwtpplugin.project.section.SharedSectionCreator;
import com.imagem.gwtpplugin.projectfile.Settings;
import com.imagem.gwtpplugin.projectfile.src.GwtXmlModule;
import com.imagem.gwtpplugin.projectfile.src.Jdoconfig;
import com.imagem.gwtpplugin.projectfile.src.Log4j;
import com.imagem.gwtpplugin.projectfile.war.AppengineWebXml;
import com.imagem.gwtpplugin.projectfile.war.Jar;
import com.imagem.gwtpplugin.projectfile.war.Logging;
import com.imagem.gwtpplugin.projectfile.war.ProjectCSS;
import com.imagem.gwtpplugin.projectfile.war.ProjectHTML;
import com.imagem.gwtpplugin.projectfile.war.WebXml;

@SuppressWarnings("restriction")
public class ProjectCreator extends Creator {

	private static final String AOPALLIANCE = "aopalliance";
	private static final String GIN = "gin-r137";
	private static final String GUICE = "guice-2.0";
	private static final String GUICE_SERVLET = "guice-servlet-2.0";
	//private static final String GWTP = "gwtp-0.4";
	private static final String GWTP = "gwtp-all-0.5";

	/**
	 * For this marvelous project we need to:
	 * - create the default Eclipse project
	 * - add the Java project nature
	 * - add the GWT project nature
	 * - add required libraries
	 * - create the folder structure
	 *
	 * @param projectName
	 * @param projectPackage
	 * @param projectLocation 
	 * @param options
	 * @return
	 */
	public static IProject createProject(String projectName, String projectPackage, URI projectLocation, boolean useGAE) {
		IProject project = null;

		try {
			project = createBaseProject(projectName, projectLocation);
			addNature(project, useGAE);

			createWarFolder(project, projectPackage, useGAE);
			createSrcFolder(project, projectPackage, useGAE);
			createSettings(project);

			IJavaProject javaProject = JavaCore.create(project);
			createEntries(javaProject, useGAE);

			createLaunchConfig(project);
		}
		catch(CoreException e) {
			e.printStackTrace();
			project = null;
		}

		return project;
	}

	/**
	 * Just do the basics: create a basic project.
	 *
	 * @param projectName
	 * @param projectLocation
	 * @throws CoreException 
	 */
	private static IProject createBaseProject(String projectName, URI projectLocation) throws CoreException {
		// it is acceptable to use the ResourcesPlugin class
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		if(!newProject.exists()) {
			String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocationURI().toString() + "/";
			if(projectLocation != null && workspace.equals(projectLocation.toString())) {
				projectLocation = null;
			}
			IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());
			desc.setLocationURI(projectLocation);

			newProject.create(desc, null);
			if(!newProject.isOpen()) {
				newProject.open(null);
			}
		}

		return newProject;
	}

	/**
	 * Add Java and GWT nature to the project
	 * 
	 * @param project
	 * @throws CoreException
	 */
	private static void addNature(IProject project, boolean useGAE) throws CoreException {
		if (!project.hasNature(JavaCore.NATURE_ID)) {
			IProjectDescription description = project.getDescription();
			if(useGAE)
				description.setNatureIds(new String[]{JavaCore.NATURE_ID, GWTNature.NATURE_ID, GaeNature.NATURE_ID});
			else
				description.setNatureIds(new String[]{JavaCore.NATURE_ID, GWTNature.NATURE_ID});
			project.setDescription(description, null);
		}
	}

	/**
	 * Create the war folder structure and base files
	 * 
	 * @param project
	 * @param projectPackage
	 * @param options 
	 * @throws CoreException
	 */
	private static void createWarFolder(IProject project, String projectPackage, boolean useGAE) throws CoreException {
		IPath warPath = new Path("war");
		createFolder(project.getFolder(warPath));

		ProjectHTML projectHTML = new ProjectHTML(project.getName(), warPath.toString());
		createProjectFile(project, projectHTML);

		ProjectCSS projectCSS = new ProjectCSS(project.getName(), warPath.toString());
		createProjectFile(project, projectCSS);

		IPath webInfPath = warPath.append("WEB-INF");
		createFolder(project.getFolder(webInfPath));

		WebXml webXml = new WebXml(project.getName(), projectPackage, webInfPath.toString());
		createProjectFile(project, webXml);

		if(useGAE) {
			AppengineWebXml appengineWebXml = new AppengineWebXml(webInfPath.toString());
			createProjectFile(project, appengineWebXml);

			Logging logging = new Logging(webInfPath.toString());
			createProjectFile(project, logging);
		}

		createLibFolder(project, webInfPath);
	}

	/**
	 * Create the lib folder with all needed dependencies
	 * 
	 * @param project
	 * @param webInfPath
	 * @throws CoreException
	 */
	private static void createLibFolder(IProject project, IPath webInfPath) throws CoreException {
		IPath libPath = webInfPath.append("lib");
		createFolder(project.getFolder(libPath));

		Jar aopallianceJar = new Jar(AOPALLIANCE, libPath.toString());
		createProjectFile(project, aopallianceJar);

		Jar ginJar = new Jar(GIN, libPath.toString());
		createProjectFile(project, ginJar);

		Jar guiceJar = new Jar(GUICE, libPath.toString());
		createProjectFile(project, guiceJar);

		Jar guiceServletJar = new Jar(GUICE_SERVLET, libPath.toString());
		createProjectFile(project, guiceServletJar);

		Jar gwtpJar = new Jar(GWTP, libPath.toString());
		createProjectFile(project, gwtpJar);
	}

	/**
	 * Create src folder structure and base classes
	 * 
	 * @param project
	 * @param projectPackage
	 * @param options 
	 * @throws CoreException
	 */
	private static void createSrcFolder(IProject project, String projectPackage, boolean useGAE) throws CoreException {
		IPath srcPath = new Path("src");
		createFolder(project.getFolder(srcPath));

		if(useGAE) {
			Log4j log4j = new Log4j(srcPath.toString());
			createProjectFile(project, log4j);

			IPath metaInfPath = srcPath.append("META-INF");
			createFolder(project.getFolder(metaInfPath));

			Jdoconfig jdoconfig = new Jdoconfig(metaInfPath.toString());
			createProjectFile(project, jdoconfig);
		}

		IPath basePath = srcPath.append(projectPackage.replace('.', '/'));
		createFolder(project.getFolder(basePath));

		SharedSectionCreator.createSharedPackage(project, basePath);
		ClientSectionCreator.createClientPackage(project, basePath);
		ServerSectionCreator.createServerPackage(project, basePath);

		GwtXmlModule gwtXmlModule = new GwtXmlModule(project.getName(), toPackage(basePath), basePath.toString());
		createProjectFile(project, gwtXmlModule);
	}

	/**
	 * Create entries in the classpath
	 * 
	 * @param javaProject
	 * @param useGAE 
	 * @throws CoreException
	 */
	private static void createEntries(IJavaProject javaProject, boolean useGAE) throws CoreException {
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();

		// Default output location
		IPath outputPath = new Path("/" + javaProject.getElementName() + "/war/WEB-INF/classes");
		javaProject.setOutputLocation(outputPath, null);

		// Source folder
		entries.add(JavaCore.newSourceEntry(javaProject.getPath().append("src")));

		// GWT SDK container
		IPath gwtContainer = GWTRuntimeContainer.CONTAINER_PATH;
		ClasspathContainerInitializer gwtInitializer = JavaCore.getClasspathContainerInitializer(gwtContainer.segment(0));
		gwtInitializer.initialize(gwtContainer, javaProject);
		entries.add(JavaCore.newContainerEntry(gwtContainer));

		// GAE SDK container
		if(useGAE) {
			IPath gaeContainer = GaeSdkContainer.CONTAINER_PATH;
			ClasspathContainerInitializer gaeInitializer = JavaCore.getClasspathContainerInitializer(gaeContainer.segment(0));
			gaeInitializer.initialize(gaeContainer, javaProject);
			entries.add(JavaCore.newContainerEntry(gaeContainer));
		}

		// JRE container
		entries.addAll(Arrays.asList(PreferenceConstants.getDefaultJRELibrary()));

		// GWTP libs
		entries.add(JavaCore.newLibraryEntry(new Path("/" + javaProject.getElementName() + "/war/WEB-INF/lib/" + AOPALLIANCE + ".jar"), null, null));
		entries.add(JavaCore.newLibraryEntry(new Path("/" + javaProject.getElementName() + "/war/WEB-INF/lib/" + GIN + ".jar"), null, null));
		entries.add(JavaCore.newLibraryEntry(new Path("/" + javaProject.getElementName() + "/war/WEB-INF/lib/" + GUICE + ".jar"), null, null));
		entries.add(JavaCore.newLibraryEntry(new Path("/" + javaProject.getElementName() + "/war/WEB-INF/lib/" + GUICE_SERVLET + ".jar"), null, null));
		entries.add(JavaCore.newLibraryEntry(new Path("/" + javaProject.getElementName() + "/war/WEB-INF/lib/" + GWTP + ".jar"), null, null));

		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
	}

	private static void createSettings(IProject project) throws CoreException {
		IPath settingsPath = new Path(".settings");

		Settings settings = new Settings(settingsPath.toString());
		createProjectFile(project, settings);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void createLaunchConfig(IProject project) throws CoreException {
		ILaunchConfigurationWorkingCopy wc = WebAppLaunchUtil.createLaunchConfigWorkingCopy(project.getName(), project, WebAppLaunchUtil.determineStartupURL(project, false), false);
		ILaunchGroup[] groups = DebugUITools.getLaunchGroups();

		ArrayList groupsNames = new ArrayList();
		for(ILaunchGroup group : groups) {
			if((!("org.eclipse.debug.ui.launchGroup.debug".equals(group.getIdentifier()))) && (!("org.eclipse.debug.ui.launchGroup.run".equals(group.getIdentifier()))))
				continue;
			groupsNames.add(group.getIdentifier());
		}

		wc.setAttribute("org.eclipse.debug.ui.favoriteGroups", groupsNames);
		wc.doSave();
	}
}
