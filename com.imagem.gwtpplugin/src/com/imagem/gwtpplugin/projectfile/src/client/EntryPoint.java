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

package com.imagem.gwtpplugin.projectfile.src.client;

import org.eclipse.jdt.core.IField;
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
public class EntryPoint extends ProjectClass {

  private static final String I_ENTRY_POINT = "com.google.gwt.core.client.EntryPoint";
  private static final String C_GWT = "com.google.gwt.core.client.GWT";
  private static final String C_DELAYED_BIND_REGISTRY = "com.gwtplatform.mvp.client.DelayedBindRegistry";

  public EntryPoint(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory)
      throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
  }

  public EntryPoint(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    init();
  }

  @Override
  protected IType createType() throws JavaModelException {
    cu.createImport(I_ENTRY_POINT, null, null);
    return createClass(null, "EntryPoint");
  }

  public IField createGinjectorField(IType ginjector) throws JavaModelException {
    cu.createImport(ginjector.getFullyQualifiedName(), null, null);
    cu.createImport(C_GWT, null, null);
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLine("private final " + ginjector.getElementName() + " ginjector = GWT.create("
        + ginjector.getElementName() + ".class);");
    return createField(sw);
  }

  public IMethod createOnModuleLoadMethod() throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "@Override",
        "public void onModuleLoad() {");
    cu.createImport(C_DELAYED_BIND_REGISTRY, null, null);
    sw.writeLines(
        "  // This is required for Gwt-Platform proxy's generator",
        "  DelayedBindRegistry.bind(ginjector);",
        "",
        "ginjector.getPlaceManager().revealCurrentPlace();");
    sw.writeLine("}");

    return createMethod(sw);
  }
}
