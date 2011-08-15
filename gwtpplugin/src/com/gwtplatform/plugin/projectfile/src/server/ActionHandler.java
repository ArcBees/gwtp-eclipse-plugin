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
public class ActionHandler extends ProjectClass {

  private static final String C_ACTION_EXCEPTION = "com.gwtplatform.dispatch.shared.ActionException";
  private static final String I_ACTION_HANDLER = "com.gwtplatform.dispatch.server.actionhandler.ActionHandler";
  private static final String I_EXECUTION_CONTEXT = "com.gwtplatform.dispatch.server.ExecutionContext";
  private static final String A_INJECT = "com.google.inject.Inject";
  private final IType action;
  private final IType result;

  public ActionHandler(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
    action = result = null;
  }

  public ActionHandler(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory, IType action, IType result)
      throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    this.action = action;
    this.result = result;
    init();
  }

  @Override
  protected IType createType() throws JavaModelException {
    workingCopy.createImport(I_ACTION_HANDLER, null, null);
    workingCopy.createImport(action.getFullyQualifiedName(), null, null);
    workingCopy.createImport(result.getFullyQualifiedName(), null, null);
    return createClass(null,
        "ActionHandler<" + action.getElementName() + ", " + result.getElementName() + ">");
  }

  public IMethod createConstructor() throws JavaModelException {
    workingCopy.createImport(A_INJECT, null, null);

    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "@Inject",
        "public " + workingCopyType.getElementName() + "() {",
        "}");

    return createMethod(sw);
  }

  public IMethod createExecuteMethod(IType action, IType result) throws JavaModelException {
    workingCopy.createImport(I_EXECUTION_CONTEXT, null, null);
    workingCopy.createImport(C_ACTION_EXCEPTION, null, null);

    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "@Override",
        "public " + result.getElementName() + " execute(" + action.getElementName()
        + " action, ExecutionContext context) throws ActionException {",
        "  return null;",
        "}");

    return createMethod(sw);
  }

  public IMethod createUndoMethod(IType action, IType result) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "@Override",
        "public void undo(" + action.getElementName() + " action, "
        + result.getElementName() + " result, ExecutionContext context) throws ActionException {",
        "}");

    return createMethod(sw);
  }

  public IMethod createActionTypeGetterMethod(IType action) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "@Override",
        "public Class<" + action.getElementName() + "> getActionType() {",
        "  return " + action.getElementName() + ".class;",
        "}");

    return createMethod(sw);
  }

}
