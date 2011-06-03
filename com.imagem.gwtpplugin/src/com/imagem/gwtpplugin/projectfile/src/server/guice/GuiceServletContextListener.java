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

package com.imagem.gwtpplugin.projectfile.src.server.guice;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.imagem.gwtpplugin.SourceWriter;
import com.imagem.gwtpplugin.SourceWriterFactory;
import com.imagem.gwtpplugin.projectfile.ProjectClass;

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
    cu.createImport(C_GUICE_SERVLET_CONTEXT_LISTENER, null, null);
    return createClass("GuiceServletContextListener", null);
  }

  public IMethod createInjectorGetterMethod(IType handlerModule, IType servletModule)
      throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    cu.createImport(I_INJECTOR, null, null);
    cu.createImport(C_GUICE, null, null);
    cu.createImport(handlerModule.getFullyQualifiedName(), null, null);
    cu.createImport(servletModule.getFullyQualifiedName(), null, null);

    sw.writeLines(
        "@Override",
        "protected Injector getInjector() {",
        "  return Guice.createInjector(new " + handlerModule.getElementName() + "(), new "
        + servletModule.getElementName() + "());",
        "}");

    return createMethod(sw);
  }
}
