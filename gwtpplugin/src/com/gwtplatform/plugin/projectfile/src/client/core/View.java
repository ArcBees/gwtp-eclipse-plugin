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

package com.gwtplatform.plugin.projectfile.src.client.core;

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
public class View extends ProjectClass {

  private static final String C_EVENT_BUS = "com.google.gwt.event.shared.EventBus";
  private static final String C_VIEW_IMPL = "com.gwtplatform.mvp.client.ViewImpl";
  private static final String C_POPUP_VIEW_IMPL = "com.gwtplatform.mvp.client.PopupViewImpl";
  private static final String C_WIDGET = "com.google.gwt.user.client.ui.Widget";
  private static final String A_INJECT = "com.google.inject.Inject";
  private static final String I_UI_BINDER = "com.google.gwt.uibinder.client.UiBinder";

  private final IType presenter;
  private final boolean isPopupView;

  public View(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
    presenter = null;
    isPopupView = workingCopyType.getSuperclassName().startsWith("PopupView");
  }

  public View(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory, IType presenter, boolean isPopupView)
      throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    this.presenter = presenter;
    this.isPopupView = isPopupView;
    init();
  }

  @Override
  protected IType createType() throws JavaModelException {
    if (isPopupView) {
      workingCopy.createImport(C_POPUP_VIEW_IMPL, null, null);
      return createClass("PopupViewImpl", presenter.getElementName() + ".MyView");
    } else {
      workingCopy.createImport(C_VIEW_IMPL, null, null);
      return createClass("ViewImpl", presenter.getElementName() + ".MyView");
    }
  }

  public IType createBinderInterface() throws JavaModelException {
    workingCopy.createImport(I_UI_BINDER, null, null);

    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLine(
        "public interface Binder extends UiBinder<Widget, " + workingCopyType.getElementName() + "> { }");

    return workingCopyType.createType(sw.toString(), null, false, null);
  }

  public IField createWidgetField() throws JavaModelException {
    workingCopy.createImport(C_WIDGET, null, null);
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLine("private final Widget widget;");
    return createField(sw);
  }

  public IMethod createConstructor(boolean useUiBinder) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();

    String parameters = "";
    if (isPopupView) {
      workingCopy.createImport(C_EVENT_BUS, null, null);
      parameters += "final EventBus eventBus";
      if (useUiBinder) {
        parameters += ", ";
      }
    }
    if (useUiBinder) {
      parameters += "final Binder binder";
    }

    workingCopy.createImport(A_INJECT, null, null);
    sw.writeLines(
        "@Inject",
        "public " + workingCopyType.getElementName() + "(" + parameters + ") {");

    if (isPopupView) {
      sw.writeLine("  super(eventBus);");
    }
    if (useUiBinder) {
      sw.writeLine("  widget = binder.createAndBindUi(this);");
    }
    sw.writeLine("}");

    return createMethod(sw);
  }

  public IMethod createAsWidgetMethod(boolean useUiBinder) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();

    workingCopy.createImport(C_WIDGET, null, null);
    sw.writeLines(
        "@Override",
        "public Widget asWidget() {",
        "  return " + (useUiBinder ? "widget" : "null") + ";",
        "}");

    return createMethod(sw);
  }
}
