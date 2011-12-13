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

package com.gwtplatform.plugin.projectfile.src.server.guice;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.gwtplatform.plugin.SourceWriter;
import com.gwtplatform.plugin.SourceWriterFactory;
import com.gwtplatform.plugin.projectfile.ProjectClass;

/**
 *
 * @author Michael Renaud
 *
 */
public class GuiceServletContextListener extends ProjectClass {

  private static final String C_GUICE_SERVLET_CONTEXT_LISTENER = "com.google.inject.servlet.GuiceServletContextListener";
  private static final String I_INJECTOR = "com.google.inject.Injector";
  private static final String C_GUICE = "com.google.inject.Guice";

  public GuiceServletContextListener(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
  }

  public GuiceServletContextListener(IPackageFragmentRoot root, String packageName,
      String elementName, SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    init();
  }

  @Override
  protected IType createType() throws JavaModelException {
	  IType result = createClass("GuiceServletContextListener", null);
	  workingCopy.createImport(C_GUICE_SERVLET_CONTEXT_LISTENER, null, new NullProgressMonitor());
	  return result;
  }

  public IMethod createInjectorGetterMethod(IType handlerModule, IType servletModule)
      throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    workingCopy.createImport(I_INJECTOR, null, new NullProgressMonitor());
    workingCopy.createImport(C_GUICE, null, new NullProgressMonitor());
    workingCopy.createImport(handlerModule.getFullyQualifiedName(), null, new NullProgressMonitor());
    workingCopy.createImport(servletModule.getFullyQualifiedName(), null, new NullProgressMonitor());

    sw.writeLines(
        "@Override",
        "protected Injector getInjector() {",
        "  return Guice.createInjector(new " + handlerModule.getElementName() + "(), new "
        + servletModule.getElementName() + "());",
        "}");

    return createMethod(sw);
  }
}
