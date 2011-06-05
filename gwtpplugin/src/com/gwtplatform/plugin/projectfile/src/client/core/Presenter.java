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
public class Presenter extends ProjectClass {

  private static final String I_VIEW = "com.gwtplatform.mvp.client.View";
  private static final String I_POPUP_VIEW = "com.gwtplatform.mvp.client.PopupView";
  private static final String I_PROXY = "com.gwtplatform.mvp.client.proxy.Proxy";
  private static final String I_PROXY_PLACE = "com.gwtplatform.mvp.client.proxy.ProxyPlace";
  private static final String A_PROXY_STANDARD = "com.gwtplatform.mvp.client.annotations.ProxyStandard";
  private static final String A_PROXY_CODESPLIT = "com.gwtplatform.mvp.client.annotations.ProxyCodeSplit";
  private static final String A_NAME_TOKEN = "com.gwtplatform.mvp.client.annotations.NameToken";
  private static final String A_USE_GATEKEEPER = "com.gwtplatform.mvp.client.annotations.UseGatekeeper";
  private static final String C_PRESENTER = "com.gwtplatform.mvp.client.Presenter";
  private static final String C_PRESENTER_WIDGET = "com.gwtplatform.mvp.client.PresenterWidget";
  private static final String C_EVENT_BUS = "com.google.gwt.event.shared.EventBus";
  private static final String A_INJECT = "com.google.inject.Inject";

  private final boolean isPresenterWidget;

  public Presenter(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
    isPresenterWidget = type.getSuperclassName().startsWith("PresenterWidget");
  }

  public Presenter(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory, boolean isPresenterWidget)
      throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    this.isPresenterWidget = isPresenterWidget;
    init();
  }

  @Override
  protected IType createType() throws JavaModelException {
    if (isPresenterWidget) {
      cu.createImport(C_PRESENTER_WIDGET, null, null);
      return createClass("PresenterWidget<" + elementName + ".MyView>", null);
    } else {
      cu.createImport(C_PRESENTER, null, null);
      return createClass("Presenter<" + elementName + ".MyView, " + elementName + ".MyProxy>",
          null);
    }
  }

  public IType createPopupViewInterface() throws JavaModelException {
    cu.createImport(I_POPUP_VIEW, null, null);

    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "public interface MyView extends PopupView {",
        "  // TODO Put your view methods here",
        "}");

    return type.createType(sw.toString(), null, false, null);
  }

  public IType createViewInterface() throws JavaModelException {
    cu.createImport(I_VIEW, null, null);

    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "public interface MyView extends View {",
        "  // TODO Put your view methods here",
        "}");

    return type.createType(sw.toString(), null, false, null);
  }

  public IType createProxyInterface(boolean isStandard) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    outputProxyAnnotation(isStandard, sw);

    cu.createImport(I_PROXY, null, null);
    sw.writeLine("public interface MyProxy extends Proxy<" + type.getElementName() + "> {}");

    return type.createType(sw.toString(), null, false, null);
  }

  public IType createProxyPlaceInterface(boolean isStandard, IType tokens, String tokenName)
      throws JavaModelException {
    return createProxyPlaceInterface(isStandard, tokens, tokenName, null);
  }

  public IType createProxyPlaceInterface(boolean isStandard, IType tokens, String tokenName,
      IType gatekeeper) throws JavaModelException {
    String tokenNameWithoutBang = tokenName.replaceAll("!", "");

    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    outputProxyAnnotation(isStandard, sw);

    cu.createImport(A_NAME_TOKEN, null, null);
    cu.createImport(tokens.getFullyQualifiedName(), null, null);
    sw.writeLine("@NameToken(" + tokens.getElementName() + "." + tokenNameWithoutBang + ")");

    if (gatekeeper != null) {
      cu.createImport(A_USE_GATEKEEPER, null, null);
      cu.createImport(gatekeeper.getFullyQualifiedName(), null, null);
      sw.writeLine("@UseGatekeeper(" + gatekeeper.getElementName() + ".class)");
    }

    cu.createImport(I_PROXY_PLACE, null, null);
    sw.writeLine("public interface MyProxy extends ProxyPlace<" + type.getElementName() + "> {}");

    return type.createType(sw.toString(), null, false, null);
  }

  public IMethod createConstructor() throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();

    cu.createImport(A_INJECT, null, null);
    cu.createImport(C_EVENT_BUS, null, null);
    sw.writeLines(
        "@Inject",
        "public " + type.getElementName() + "(");

    if (isPresenterWidget) {
      sw.writeLines(
          "    final EventBus eventBus,",
          "    final MyView view) {",
          "  super(eventBus, view);");
    } else {
      sw.writeLines(
          "    final EventBus eventBus,",
          "    final MyView view,",
          "    final MyProxy proxy) {",
          "  super(eventBus, view, proxy);");
    }
    sw.writeLines("}");

    return createMethod(sw);
  }

  public IMethod createRevealInParentMethod(IType revealEvent) throws JavaModelException {
    return createRevealInParentMethod(revealEvent, null, null);
  }

  public IMethod createRevealInParentMethod(IType revealEvent, IType parent, String contentSlot)
      throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();

    sw.writeLines(
        "@Override",
        "protected void revealInParent() {");

    cu.createImport(revealEvent.getFullyQualifiedName(), null, null);
    String slotConstant = "";
    if (revealEvent.getElementName().equals("RevealContentEvent")) {
      cu.createImport(parent.getFullyQualifiedName(), null, null);
      slotConstant = parent.getElementName() + "." + contentSlot + ", ";
    }
    sw.writeLines(
        revealEvent.getElementName() + ".fire(this, " + slotConstant + "this);",
        "}");

    return createMethod(sw);
  }

  public IMethod createMethodStub(String name) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();

    sw.writeLines(
        "@Override",
        "protected void " + name + "() {" ,
        "  super." + name + "();",
        "}");

    return createMethod(sw);
  }

  private void outputProxyAnnotation(boolean isStandard, SourceWriter sw) throws JavaModelException {
    if (isStandard) {
      cu.createImport(A_PROXY_STANDARD, null, null);
      sw.writeLine("@ProxyStandard");
    } else {
      cu.createImport(A_PROXY_CODESPLIT, null, null);
      sw.writeLine("@ProxyCodeSplit");
    }
  }
}
