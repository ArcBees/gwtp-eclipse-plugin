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
import com.gwtplatform.plugin.tool.VersionTool;

/**
 *
 * @author Michael Renaud
 *
 */
public class ServletModule extends ProjectClass {

  private static final String C_SERVLET_MODULE = "com.google.inject.servlet.ServletModule";
  private static final String C_ACTION_IMPL = "com.gwtplatform.dispatch.shared.ActionImpl";
  private static final String C_DISPATCH_SERVICE_IMPL = "com.gwtplatform.dispatch.server.guice.DispatchServiceImpl";

  public ServletModule(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
  }

  public ServletModule(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    init();
  }

  @Override
  protected IType createType() throws JavaModelException {
    cu.createImport(C_SERVLET_MODULE, null, null);
    return createClass("ServletModule", null);
  }

  public IMethod createConfigureServletsMethod(String gwtVersion) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "@Override",
        "public void configureServlets() {");

    if (VersionTool.compare(gwtVersion, "2.1") == -1) {
      // GWT < 2.1
      String projectName = cu.getJavaProject().getElementName();
      cu.createImport(C_ACTION_IMPL, null, null);
      cu.createImport(C_DISPATCH_SERVICE_IMPL, null, null);
      sw.writeLine("  serve(\"/" + projectName.toLowerCase()
          + "/\" + ActionImpl.DEFAULT_SERVICE_NAME).with(DispatchServiceImpl.class);");
    } else {
      // GWT >= 2.1
      cu.createImport(C_ACTION_IMPL, null, null);
      cu.createImport(C_DISPATCH_SERVICE_IMPL, null, null);
      sw.writeLine(
          "  serve(\"/\" + ActionImpl.DEFAULT_SERVICE_NAME).with(DispatchServiceImpl.class);");
    }
    sw.writeLine("}");

    // TODO SessionID

    return createMethod(sw);
  }
}
