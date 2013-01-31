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

package com.gwtplatform.plugin.projectfile.src.client.core;

import org.eclipse.core.runtime.NullProgressMonitor;
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
  private static final String C_EVENT_BUS = "com.google.web.bindery.event.shared.EventBus";
  private static final String A_INJECT = "com.google.inject.Inject";

  private final boolean isPresenterWidget;

  public Presenter(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
    isPresenterWidget = workingCopyType.getSuperclassName().startsWith("PresenterWidget");
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
	  IType result = null;
	  if (isPresenterWidget) {
		  result = createClass("PresenterWidget<" + elementName + ".MyView>", null);
		  workingCopy.createImport(C_PRESENTER_WIDGET, null, new NullProgressMonitor());
	  } else {
		  result = createClass("Presenter<" + elementName + ".MyView, " + elementName + ".MyProxy>",
				  null);
		  workingCopy.createImport(C_PRESENTER, null, new NullProgressMonitor()); 
	  }
	  return result;
  }

  public IType createPopupViewInterface() throws JavaModelException {
    workingCopy.createImport(I_POPUP_VIEW, null, new NullProgressMonitor());

    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "public interface MyView extends PopupView {",
        "  // TODO Put your view methods here",
        "}");

    return workingCopyType.createType(sw.toString(), null, false, new NullProgressMonitor());
  }

  public IType createViewInterface() throws JavaModelException {
    workingCopy.createImport(I_VIEW, null, new NullProgressMonitor());

    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "public interface MyView extends View {",
        "}");

    return workingCopyType.createType(sw.toString(), null, false, new NullProgressMonitor());
  }

  public IType createProxyInterface(boolean isStandard) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    outputProxyAnnotation(isStandard, sw);

    workingCopy.createImport(I_PROXY, null, new NullProgressMonitor());
    sw.writeLine("public interface MyProxy extends Proxy<" + workingCopyType.getElementName() + "> {}");

    return workingCopyType.createType(sw.toString(), null, false, new NullProgressMonitor());
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

    workingCopy.createImport(A_NAME_TOKEN, null, new NullProgressMonitor());
    workingCopy.createImport(tokens.getFullyQualifiedName(), null, new NullProgressMonitor());
    sw.writeLine("@NameToken(" + tokens.getElementName() + "." + tokenNameWithoutBang + ")");

    if (gatekeeper != null) {
      workingCopy.createImport(A_USE_GATEKEEPER, null, new NullProgressMonitor());
      workingCopy.createImport(gatekeeper.getFullyQualifiedName(), null, new NullProgressMonitor());
      sw.writeLine("@UseGatekeeper(" + gatekeeper.getElementName() + ".class)");
    }

    workingCopy.createImport(I_PROXY_PLACE, null, new NullProgressMonitor());
    sw.writeLine("public interface MyProxy extends ProxyPlace<" + workingCopyType.getElementName() + "> {}");

    return workingCopyType.createType(sw.toString(), null, false, new NullProgressMonitor());
  }

  public IMethod createConstructor() throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();

    workingCopy.createImport(A_INJECT, null, new NullProgressMonitor());
    workingCopy.createImport(C_EVENT_BUS, null, new NullProgressMonitor());
    sw.writeLines(
        "@Inject",
        "public " + workingCopyType.getElementName() + "(");

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

    workingCopy.createImport(revealEvent.getFullyQualifiedName(), null, new NullProgressMonitor());
    String slotConstant = "";
    if (revealEvent.getElementName().equals("RevealContentEvent")) {
      workingCopy.createImport(parent.getFullyQualifiedName(), null, new NullProgressMonitor());
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
      workingCopy.createImport(A_PROXY_STANDARD, null, new NullProgressMonitor());
      sw.writeLine("@ProxyStandard");
    } else {
      workingCopy.createImport(A_PROXY_CODESPLIT, null, new NullProgressMonitor());
      sw.writeLine("@ProxyCodeSplit");
    }
  }
}
