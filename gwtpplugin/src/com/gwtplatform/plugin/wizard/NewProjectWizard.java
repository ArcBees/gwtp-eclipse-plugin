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

package com.gwtplatform.plugin.wizard;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.google.appengine.eclipse.core.nature.GaeNature;
import com.google.appengine.eclipse.core.sdk.GaeSdkContainer;
import com.google.appengine.eclipse.core.validators.GaeProjectValidator;
import com.google.gdt.eclipse.core.validators.WebAppProjectValidator;
import com.google.gdt.eclipse.suite.launch.WebAppLaunchUtil;
import com.google.gwt.eclipse.core.nature.GWTNature;
import com.google.gwt.eclipse.core.preferences.GWTPreferences;
import com.google.gwt.eclipse.core.runtime.GWTRuntimeContainer;
import com.gwtplatform.plugin.Activator;
import com.gwtplatform.plugin.SourceWriterFactory;
import com.gwtplatform.plugin.projectfile.src.GwtXmlModule;
import com.gwtplatform.plugin.projectfile.src.Jdoconfig;
import com.gwtplatform.plugin.projectfile.src.Log4j;
import com.gwtplatform.plugin.projectfile.src.client.EntryPoint;
import com.gwtplatform.plugin.projectfile.src.client.gin.Ginjector;
import com.gwtplatform.plugin.projectfile.src.client.gin.PresenterModule;
import com.gwtplatform.plugin.projectfile.src.client.place.PlaceAnnotation;
import com.gwtplatform.plugin.projectfile.src.client.place.PlaceManager;
import com.gwtplatform.plugin.projectfile.src.client.place.Tokens;
import com.gwtplatform.plugin.projectfile.src.server.guice.GuiceServletContextListener;
import com.gwtplatform.plugin.projectfile.src.server.guice.HandlerModule;
import com.gwtplatform.plugin.projectfile.src.server.guice.ServletModule;
import com.gwtplatform.plugin.projectfile.war.AppengineWebXml;
import com.gwtplatform.plugin.projectfile.war.Jar;
import com.gwtplatform.plugin.projectfile.war.Logging;
import com.gwtplatform.plugin.projectfile.war.ProjectCSS;
import com.gwtplatform.plugin.projectfile.war.ProjectHTML;
import com.gwtplatform.plugin.projectfile.war.WebXml;
import com.gwtplatform.plugin.tool.VersionTool;

/**
 *
 * @author Michael Renaud
 *
 */
@SuppressWarnings("restriction")
public class NewProjectWizard extends Wizard implements INewWizard {

  private final SourceWriterFactory sourceWriterFactory;

  private NewProjectWizardPage page;
  private boolean isDone;

  private String formattedName;

  public NewProjectWizard() {
    super();
    setNeedsProgressMonitor(true);
    setWindowTitle("New GWTP Project");
    sourceWriterFactory = new SourceWriterFactory();

    try {
      URL url = new URL(Activator.getDefault().getBundle().getEntry("/"), "icons/gwtp-logo.png");
      setDefaultPageImageDescriptor(ImageDescriptor.createFromURL(url));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void addPages() {
    page = new NewProjectWizardPage();
    addPage(page);
  }

  @Override
  public boolean performFinish() {
    try {
      super.getContainer().run(false, false, new IRunnableWithProgress() {
        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {
          isDone = finish(monitor);
        }
      });
    } catch (Exception e) {
      return false;
    }
    return isDone;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected boolean finish(IProgressMonitor desiredMonitor) {
    IProgressMonitor monitor = desiredMonitor;
    if (monitor == null) {
      monitor = new NullProgressMonitor();
    }

    try {
      monitor.beginTask("GWT-Platform project creation", 4);

      // Project base creation
      monitor.subTask("Base project creation");
      formattedName = projectNameToClassName(page.getProjectName(), page.isRemoveEnabled());
      IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(page.getProjectName());

      // Project location
      URI location = null;
      String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocationURI().toString() + "/";
      if (page.getProjectLocation() != null
          && !workspace.equals(page.getProjectLocation().toString())) {
        location = page.getProjectLocation();
      }
      IProjectDescription description = project.getWorkspace().newProjectDescription(
          project.getName());
      description.setLocationURI(location);

      // Project natures and builders
      ICommand javaBuilder = description.newCommand();
      javaBuilder.setBuilderName(JavaCore.BUILDER_ID);

      ICommand webAppBuilder = description.newCommand();
      webAppBuilder.setBuilderName(WebAppProjectValidator.BUILDER_ID);

      ICommand gwtBuilder = description.newCommand();
      // TODO use the BUILDER_UI field
      gwtBuilder.setBuilderName("com.google.gwt.eclipse.core.gwtProjectValidator");

      if (page.useGAE()) {
        ICommand gaeBuilder = description.newCommand();
        gaeBuilder.setBuilderName(GaeProjectValidator.BUILDER_ID);

        // TODO use the BUILDER_UI field
        ICommand enhancer = description.newCommand();
        // TODO use the BUILDER_UI field
        enhancer.setBuilderName("com.google.appengine.eclipse.core.enhancerbuilder");

        description.setBuildSpec(new ICommand[] { javaBuilder, webAppBuilder, gwtBuilder,
            gaeBuilder, enhancer });
        description.setNatureIds(new String[] { JavaCore.NATURE_ID, GWTNature.NATURE_ID,
            GaeNature.NATURE_ID });
      } else {
        description.setBuildSpec(new ICommand[] { javaBuilder, webAppBuilder, gwtBuilder });
        description.setNatureIds(new String[] { JavaCore.NATURE_ID, GWTNature.NATURE_ID });
      }

      project.create(description, null); // TODO Progress Monitor
      if (!project.isOpen()) {
        project.open(null); // TODO Progress Monitor
      }
      monitor.worked(1);

      // Java Project creation
      monitor.subTask("Classpath entries creation");
      IJavaProject javaProject = JavaCore.create(project);

      // war/WEB-INF/lib folder creation
      IPath warPath = new Path("war");
      project.getFolder(warPath).create(false, true, null); // TODO Progress Monitor

      IPath webInfPath = warPath.append("WEB-INF");
      project.getFolder(webInfPath).create(false, true, null); // TODO Progress Monitor

      IPath libPath = webInfPath.append("lib");
      project.getFolder(libPath).create(false, true, null); // TODO Progress Monitor

      Thread.sleep(1000);

      Jar[] libs = VersionTool.getLibs(project, libPath);

      // Classpath Entries creation
      List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();

      // Default output location
      IPath outputPath = new Path("/" + page.getProjectName()).append(webInfPath).append("classes");
      javaProject.setOutputLocation(outputPath, null);

      // Source folder
      IPath srcPath = new Path("src");
      project.getFolder(srcPath).create(false, true, null); // TODO Progress Monitor

      entries.add(JavaCore.newSourceEntry(javaProject.getPath().append("src")));

      // GWT SDK container
      IPath gwtContainer = GWTRuntimeContainer.CONTAINER_PATH;
      ClasspathContainerInitializer gwtInitializer = JavaCore
          .getClasspathContainerInitializer(gwtContainer.segment(0));
      gwtInitializer.initialize(gwtContainer, javaProject);
      entries.add(JavaCore.newContainerEntry(gwtContainer));

      // GAE SDK container
      if (page.useGAE()) {
        IPath gaeContainer = GaeSdkContainer.CONTAINER_PATH;
        ClasspathContainerInitializer gaeInitializer = JavaCore
            .getClasspathContainerInitializer(gaeContainer.segment(0));
        gaeInitializer.initialize(gaeContainer, javaProject);
        entries.add(JavaCore.newContainerEntry(gaeContainer));
      }

      // JRE container
      entries.addAll(Arrays.asList(PreferenceConstants.getDefaultJRELibrary()));

      // GWTP libs
      for (Jar lib : libs) {
        entries.add(JavaCore.newLibraryEntry(lib.getFile().getFullPath(), null, null));
      }

      // TODO Progress Monitor
      javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
      monitor.worked(1);

      // TODO Create settings

      monitor.subTask("Default classes creation");
      IPackageFragmentRoot root = javaProject.findPackageFragmentRoot(javaProject.getPath().append(
          "src"));

      // Create sources
      if (page.useGAE()) {
        Log4j log4j = new Log4j(project, srcPath);
        log4j.createFile();

        IPath metaInfPath = srcPath.append("META-INF");
        // TODO Progress Monitor
        project.getFolder(metaInfPath).create(false, true, null);

        Jdoconfig jdoconfig = new Jdoconfig(project, metaInfPath);
        jdoconfig.createFile();
      }

      IPackageFragment projectPackage = root.createPackageFragment(page.getProjectPackage(), false,
          null); // TODO Progress Monitor

      // Client package
      IPackageFragment clientPackage = root.createPackageFragment(projectPackage.getElementName()
          + ".client", false, null); // TODO Progress Monitor

      // Place package
      IPackageFragment placePackage = root.createPackageFragment(clientPackage.getElementName()
          + ".place", false, null); // TODO Progress Monitor

      PlaceAnnotation defaultPlace = new PlaceAnnotation(root, placePackage.getElementName(),
          "DefaultPlace", sourceWriterFactory);

      PlaceManager placeManager = new PlaceManager(root, placePackage.getElementName(),
          "ClientPlaceManager", sourceWriterFactory);
      IField defaultPlaceField = placeManager.createPlaceRequestField(defaultPlace.getType());
      placeManager.createConstructor(new IType[] { defaultPlace.getType() },
          new IField[] { defaultPlaceField });
      placeManager.createRevealDefaultPlaceMethod(defaultPlaceField);

      Tokens tokens = new Tokens(root, placePackage.getElementName(), "NameTokens",
          sourceWriterFactory);

      // Gin package
      IPackageFragment ginPackage = root.createPackageFragment(clientPackage.getElementName()
          + ".gin", false, null); // TODO Progress Monitor

      PresenterModule presenterModule = new PresenterModule(root, ginPackage.getElementName(),
          "ClientModule", sourceWriterFactory);
      presenterModule.createConfigureMethod(placeManager.getType());

      Ginjector ginjector = new Ginjector(root, ginPackage.getElementName(), "ClientGinjector",
          presenterModule.getType(), sourceWriterFactory);
      ginjector.createDefaultGetterMethods();

      // Client package contents
      EntryPoint entryPoint = new EntryPoint(root, clientPackage.getElementName(),
    		  formattedName, sourceWriterFactory);
      entryPoint.createGinjectorField(ginjector.getType());
      entryPoint.createOnModuleLoadMethod();

      // Project package contents
      GwtXmlModule gwtXmlModule = new GwtXmlModule(root, projectPackage.getElementName(),
    		  formattedName);
      gwtXmlModule.createFile(entryPoint.getType(), ginjector.getType());

      // Server package
      IPackageFragment serverPackage = root.createPackageFragment(projectPackage.getElementName()
          + ".server", false, null); // TODO Progress Monitor

      // Guice package
      IPackageFragment guicePackage = root.createPackageFragment(serverPackage.getElementName()
          + ".guice", false, null); // TODO Progress Monitor

      String gwtVersion = GWTPreferences.getDefaultRuntime().getVersion();

      ServletModule servletModule = new ServletModule(root, guicePackage.getElementName(),
          "DispatchServletModule", sourceWriterFactory);
      servletModule.createConfigureServletsMethod(gwtVersion);

      HandlerModule handlerModule = new HandlerModule(root, guicePackage.getElementName(),
          "ServerModule", sourceWriterFactory);
      handlerModule.createConfigureHandlersMethod();

      GuiceServletContextListener guiceServletContextListener = new GuiceServletContextListener(
          root, guicePackage.getElementName(), "GuiceServletConfig", sourceWriterFactory);
      guiceServletContextListener.createInjectorGetterMethod(handlerModule.getType(),
          servletModule.getType());

      // Shared package
      // TODO Progress Monitor
      root.createPackageFragment(projectPackage.getElementName() + ".shared", false, null);

      // Commit
      presenterModule.commit();
      ginjector.commit();
      defaultPlace.commit();
      placeManager.commit();
      tokens.commit();
      entryPoint.commit();

      servletModule.commit();
      handlerModule.commit();
      guiceServletContextListener.commit();

      // war contents
      ProjectHTML projectHTML = new ProjectHTML(project, warPath, project.getName());
      projectHTML.createFile();

      ProjectCSS projectCSS = new ProjectCSS(project, warPath, project.getName());
      projectCSS.createFile();

      // war/WEB-INF contents
      WebXml webXml = new WebXml(project, webInfPath);
      webXml.createFile(projectHTML.getFile(), guiceServletContextListener.getType());

      if (page.useGAE()) {
        AppengineWebXml appengineWebXml = new AppengineWebXml(project, webInfPath);
        appengineWebXml.createFile();

        Logging logging = new Logging(project, webInfPath);
        logging.createFile();
      }
      monitor.worked(1);

      // Launch Config
      monitor.subTask("Launch config creation");

      ILaunchConfigurationWorkingCopy launchConfig = WebAppLaunchUtil
          .createLaunchConfigWorkingCopy(project.getName(), project,
              WebAppLaunchUtil.determineStartupURL(project, false), false);
      ILaunchGroup[] groups = DebugUITools.getLaunchGroups();

      ArrayList groupsNames = new ArrayList();
      for (ILaunchGroup group : groups) {
        if ((!("org.eclipse.debug.ui.launchGroup.debug".equals(group.getIdentifier())))
            && (!("org.eclipse.debug.ui.launchGroup.run".equals(group.getIdentifier())))) {
          continue;
        }
        groupsNames.add(group.getIdentifier());
      }

      launchConfig.setAttribute("org.eclipse.debug.ui.favoriteGroups", groupsNames);
      launchConfig.doSave();

      project.getProject().setPersistentProperty(
          new QualifiedName(Activator.PLUGIN_ID, "nametokens"),
          tokens.getType().getFullyQualifiedName());
      project.getProject().setPersistentProperty(
          new QualifiedName(Activator.PLUGIN_ID, "ginjector"),
          ginjector.getType().getFullyQualifiedName());
      project.getProject().setPersistentProperty(
          new QualifiedName(Activator.PLUGIN_ID, "presentermodule"),
          presenterModule.getType().getFullyQualifiedName());
      project.getProject().setPersistentProperty(
          new QualifiedName(Activator.PLUGIN_ID, "handlermodule"),
          handlerModule.getType().getFullyQualifiedName());
      project.getProject().setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, "action"),
          "com.gwtplatform.dispatch.shared.ActionImpl");

      // Remove bin folder
      IFolder binFolder = project.getFolder(new Path("/bin"));
      if (binFolder.exists()) {
        binFolder.delete(true, null);
      }

      monitor.worked(1);
    } catch (Exception e) {
      IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "An unexpected error has happened. Close the wizard and retry.", e);
        
      ErrorDialog.openError(getShell(), null, null, status);
        
      return false;
    }

    monitor.done();
    return true;
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection) {
  }
  
  /**
   * Convert an Eclipse project name into a valid Java class name.  This method does not work with Unicode;
   * the fix for this involves using <code>codePointAt</code> instead of <code>charAt</code> but I am going
   * to leave this for somebody else to tackle.
   *
   * @param projectName the Eclipse project name to convert
   * @param remove <code>true</code> remove invalid characters, <code>false</code> replace them by _
   * @return a valid Java class name based on the given project name
   */
  private String projectNameToClassName(String projectName, boolean remove) {
    StringBuffer buffer = new StringBuffer(projectName);
  
    while (!Character.isJavaIdentifierStart(buffer.charAt(0))) {
      // Java identifiers can't start with a non-letter
      if (remove) {
        buffer.deleteCharAt(0);
      } else {
        buffer.setCharAt(0, '_');
      }
    } 
    
    if (Character.isLowerCase(buffer.charAt(0))) {
      // By convention, Java class names start with a capital letter.
      buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
    }
    
    for (int i = 1; i < buffer.length(); ++i) {
      if (!Character.isJavaIdentifierPart(buffer.charAt(i))) {
        // Delete or replace any invalid characters.
        if (remove) {
          buffer.deleteCharAt(i);
        } else {
          buffer.setCharAt(i, '_');
        }
      }
    }
  
    return buffer.toString();
  }
  
}
