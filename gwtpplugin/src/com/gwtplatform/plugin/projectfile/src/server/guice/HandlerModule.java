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
public class HandlerModule extends ProjectClass {

  private static final String C_HANDLER_MODULE = "com.gwtplatform.dispatch.server.guice.HandlerModule";

  public HandlerModule(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
  }

  public HandlerModule(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    init();
  }

  @Override
  protected IType createType() throws JavaModelException {
    workingCopy.createImport(C_HANDLER_MODULE, null, null);
    return createClass("HandlerModule", null);
  }

  public IMethod createConfigureHandlersMethod() throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "@Override",
        "protected void configureHandlers() {",
        "}");

    return createMethod(sw);
  }

  public void createBinder(IType action, IType actionHandler) throws JavaModelException {
    workingCopy.createImport(action.getFullyQualifiedName(), null, null);
    workingCopy.createImport(actionHandler.getFullyQualifiedName(), null, null);

    SourceWriter sw = createSourceWriterFor("configureHandlers");
    sw.writeLine("bindHandler(" + action.getElementName() + ".class, "
        + actionHandler.getElementName() + ".class);");
    sw.commit(workingCopy.getBuffer());
  }

  public void createBinder(IType action, IType actionHandler, IType actionValidator)
      throws JavaModelException {
    workingCopy.createImport(action.getFullyQualifiedName(), null, null);
    workingCopy.createImport(actionHandler.getFullyQualifiedName(), null, null);
    workingCopy.createImport(actionValidator.getFullyQualifiedName(), null, null);

    SourceWriter sw = createSourceWriterFor("configureHandlers");
    sw.writeLine("bindHandler(" + action.getElementName() + ".class, "
        + actionHandler.getElementName() + ".class, "
        + actionValidator.getElementName() + ".class);");
    sw.commit(workingCopy.getBuffer());
  }
}
