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

package com.gwtplatform.plugin.projectfile.src.shared;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.gwtplatform.plugin.SourceWriterFactory;
import com.gwtplatform.plugin.projectfile.ProjectClass;

/**
 *
 * @author Michael Renaud
 *
 */
public class Action extends ProjectClass {

  private final IType superclass;
  private final IType result;

  public Action(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
    superclass = result = null;
  }

  public Action(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory, IType superclass, IType result)
      throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    this.superclass = superclass;
    this.result = result;
    init();
  }

  @Override
  protected IType createType() throws JavaModelException {
	  IType resultType = createClass(superclass.getElementName() + "<" + result.getElementName() + ">", null);
	  workingCopy.createImport(superclass.getFullyQualifiedName(), null, new NullProgressMonitor());
	  workingCopy.createImport(result.getFullyQualifiedName(), null, new NullProgressMonitor());
	  return resultType;
  }
}
