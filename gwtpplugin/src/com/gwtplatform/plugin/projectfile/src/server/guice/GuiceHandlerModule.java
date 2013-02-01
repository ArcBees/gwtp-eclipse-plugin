/**
 * Copyright 2011 IMAGEM Solutions TI sant
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

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import com.gwtplatform.plugin.SourceWriterFactory;
import com.gwtplatform.plugin.projectfile.src.server.AbstractHandlerModule;

/**
 *
 * @author Michael Renaud
 * @author Nicolas Morel
 *
 */
public class GuiceHandlerModule extends AbstractHandlerModule {

  public static final String C_HANDLER_MODULE = "com.gwtplatform.dispatch.server.guice.HandlerModule";

  public GuiceHandlerModule(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
  }

  public GuiceHandlerModule(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
  }

  @Override
  protected String getHandlerModuleClass() {
	  return C_HANDLER_MODULE;
  }
}
