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

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.gwtplatform.plugin.SourceWriter;
import com.gwtplatform.plugin.SourceWriterFactory;
import com.gwtplatform.plugin.projectfile.src.server.ActionHandler;

/**
 *
 * @author Nicolas Morel
 *
 */
public class SpringActionHandler extends ActionHandler {

  public SpringActionHandler(IPackageFragmentRoot root, String fullyQualifiedName,
       SourceWriterFactory sourceWriterFactory) throws JavaModelException {
     super(root, fullyQualifiedName, sourceWriterFactory);
  }

  public SpringActionHandler(IPackageFragmentRoot root, String packageName, String elementName,
       SourceWriterFactory sourceWriterFactory, IType action, IType result)
             throws JavaModelException {
     super(root, packageName, elementName, sourceWriterFactory, action, result);
  }

  @Override
  public IMethod createConstructor() throws JavaModelException {
     SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
     sw.writeLines("public " + workingCopyType.getElementName() + "() {","}");
     return createMethod(sw);
  }
}
