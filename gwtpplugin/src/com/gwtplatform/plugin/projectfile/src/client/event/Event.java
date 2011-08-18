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

package com.gwtplatform.plugin.projectfile.src.client.event;

import org.eclipse.jdt.core.IField;
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
public class Event extends ProjectClass {

  private static final String C_GWT_EVENT = "com.google.gwt.event.shared.GwtEvent";
  private static final String I_HAS_HANDLERS = "com.google.gwt.event.shared.HasHandlers";
  private static final String I_EVENT_HANDLER = "com.google.gwt.event.shared.EventHandler";
  private static final String I_HANDLER_REGISTRATION = "com.google.gwt.event.shared.HandlerRegistration";

  public Event(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
  }

  public Event(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    init();
  }

  @Override
  protected IType createType() throws JavaModelException {
    workingCopy.createImport(C_GWT_EVENT, null, null);
    String eventName = elementName.substring(0, elementName.length() - 5);
    return createClass("GwtEvent<" + elementName + "." + eventName + "Handler>", null);
  }

  public IType createHandlerInterface() throws JavaModelException {
    workingCopy.createImport(I_EVENT_HANDLER, null, null);

    String eventName = workingCopyType.getElementName().substring(0, workingCopyType.getElementName().length() - 5);

    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines("public interface " + eventName + "Handler extends EventHandler {",
        "  void on" + eventName + "(" + workingCopyType.getElementName() + " event);", "}");

    return workingCopyType.createType(sw.toString(), null, false, null);
  }

  public IType createHasHandlersInterface(IType handler, IMethod constructor)
      throws JavaModelException {
    workingCopy.createImport(I_HAS_HANDLERS, null, null);
    workingCopy.createImport(I_HANDLER_REGISTRATION, null, null);

    String eventName = workingCopyType.getElementName().substring(0, workingCopyType.getElementName().length() - 5);

    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "public interface " + eventName + "HasHandlers extends HasHandlers {",
        "  HandlerRegistration add" + handler.getElementName() + "("
            + handler.getElementName() + " handler);",
        "}");

    return workingCopyType.createType(sw.toString(), constructor, false, null);
  }

  public IField createTypeField(IType handler) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLine("public static Type<" + handler.getElementName() + "> TYPE = new Type<"
        + handler.getElementName() + ">();");

    return createField(sw);
  }

  public IMethod createDispatchMethod(IType handler) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "@Override",
        "protected void dispatch(" + handler.getElementName() + " handler) {",
        "  handler.on" + workingCopyType.getElementName().substring(0, workingCopyType.getElementName().length() - 5)
        + "(this);",
        "}");

    return createMethod(sw);
  }

  public IMethod createAssociatedTypeGetterMethod(IType handler) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "@Override",
        "public Type<" + handler.getElementName() + "> getAssociatedType() {",
        "  return TYPE;",
        "}");

    return createMethod(sw);
  }

  public IMethod createTypeGetterMethod(IType handler) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "public static Type<" + handler.getElementName() + "> getType() {",
        "  return TYPE;",
        "}");

    return createMethod(sw);
  }

  public IMethod createFireMethod(IField[] fields) throws JavaModelException {
    workingCopy.createImport(I_HAS_HANDLERS, null, null);

    String params = getParamsString(fields, true, true);
    String paramVars = getParamsString(fields, false, false);

    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "public static void fire(HasHandlers source" + params + ") {",
        "  source.fireEvent(new " + workingCopyType.getElementName() + "(" + paramVars + "));",
        "}");

    return createMethod(sw);
  }
}
