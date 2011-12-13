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
package com.gwtplatform.plugin.projectfile.src.server.spring;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.gwtplatform.plugin.SourceWriter;
import com.gwtplatform.plugin.SourceWriterFactory;
import com.gwtplatform.plugin.projectfile.src.server.AbstractHandlerModule;

/**
 *
 * @author Nicolas Morel
 *
 */
public class SpringHandlerModule extends AbstractHandlerModule {

  public static final String C_HANDLER_MODULE = "com.gwtplatform.dispatch.server.spring.HandlerModule";
  private static final String A_BEAN = "org.springframework.context.annotation.Bean";

  public SpringHandlerModule(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
  }

  public SpringHandlerModule(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    init();
  }

  @Override
  protected String getHandlerModuleClass() {
     return C_HANDLER_MODULE;
  }

  @Override
  public void createBinder(IType action, IType actionHandler) throws JavaModelException {
     super.createBinder(action, actionHandler);
     writeBeanConfiguration(actionHandler);
  }

  @Override
  public void createBinder(IType action, IType actionHandler, IType actionValidator) throws JavaModelException {
     super.createBinder(action, actionHandler, actionValidator);
     writeBeanConfiguration(actionHandler);
  }

  private void writeBeanConfiguration(IType bean) throws JavaModelException {
     workingCopy.createImport(A_BEAN, null, new NullProgressMonitor());
     SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
     sw.writeLine("@Bean");
     sw.writeLine("public " + bean.getElementName() + " get" + bean.getElementName() + "(){");
     sw.writeLine("return new " + bean.getElementName() + "();");
     sw.writeLine("}");
     createMethod(sw);
  }
}