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
package com.gwtplatform.plugin.projectfile.src.server;

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
 * @author Nicolas Morel
 *
 */
public abstract class AbstractHandlerModule extends ProjectClass {

  public AbstractHandlerModule(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
  }

  public AbstractHandlerModule(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    init();
  }

  protected abstract String getHandlerModuleClass();

  @Override
  protected IType createType() throws JavaModelException {
	  IType result = createClass("HandlerModule", null);
	  workingCopy.createImport(getHandlerModuleClass(), null, new NullProgressMonitor());
	  return result;
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
	workingCopy.createImport(action.getFullyQualifiedName(), null, new NullProgressMonitor());
	workingCopy.createImport(actionHandler.getFullyQualifiedName(), null, new NullProgressMonitor());

    SourceWriter sw = createSourceWriterFor("configureHandlers");
    sw.writeLine("bindHandler(" + action.getElementName() + ".class, "
        + actionHandler.getElementName() + ".class);");
    sw.commit(workingCopy.getBuffer());
  }

  public void createBinder(IType action, IType actionHandler, IType actionValidator)
      throws JavaModelException {
	workingCopy.createImport(action.getFullyQualifiedName(), null, new NullProgressMonitor());
	workingCopy.createImport(actionHandler.getFullyQualifiedName(), null, new NullProgressMonitor());
	workingCopy.createImport(actionValidator.getFullyQualifiedName(), null, new NullProgressMonitor());

    SourceWriter sw = createSourceWriterFor("configureHandlers");
    sw.writeLine("bindHandler(" + action.getElementName() + ".class, "
        + actionHandler.getElementName() + ".class, "
        + actionValidator.getElementName() + ".class);");
    sw.commit(workingCopy.getBuffer());
  }
}