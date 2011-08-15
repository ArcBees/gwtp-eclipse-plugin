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

package com.gwtplatform.plugin.projectfile.src.client.gin;

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
public class PresenterModule extends ProjectClass {

  private static final String C_ABSTRACT_PRESENTER_MODULE = "com.gwtplatform.mvp.client.gin.AbstractPresenterModule";
  private static final String C_DEFAULT_MODULE = "com.gwtplatform.mvp.client.gin.DefaultModule";

  public PresenterModule(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
  }

  public PresenterModule(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    init();
  }

  @Override
  protected IType createType() throws JavaModelException {
    workingCopy.createImport(C_ABSTRACT_PRESENTER_MODULE, null, null);
    return createClass("AbstractPresenterModule", null);
  }

  // TODO RequestStaticInjection
  // TODO BindConstant

  public IMethod createConfigureMethod(IType placeManager) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "@Override",
        "protected void configure() {");
    workingCopy.createImport(C_DEFAULT_MODULE, null, null);
    workingCopy.createImport(placeManager.getFullyQualifiedName(), null, null);
    sw.writeLine(
        "install(new DefaultModule(" + placeManager.getElementName() + ".class));");
    sw.writeLine("}");

    return createMethod(sw);
  }

  public void createPresenterBinder(IType presenter, IType view) throws JavaModelException {
    appendBindPresenterCode(presenter, view, "bindPresenter(" + presenter.getElementName()
        + ".class, " + presenter.getElementName() + ".MyView.class, " + view.getElementName()
        + ".class, " + presenter.getElementName() + ".MyProxy.class);");
  }

  public void createPresenterWidgetBinder(IType presenter, IType view) throws JavaModelException {
    appendBindPresenterCode(presenter, view, "bindPresenterWidget(" + presenter.getElementName()
        + ".class, " + presenter.getElementName() + ".MyView.class, " + view.getElementName()
        + ".class);");
  }

  public void createSingletonPresenterWidgetBinder(IType presenter, IType view)
      throws JavaModelException {
    appendBindPresenterCode(presenter, view, "bindSingletonPresenterWidget("
        + presenter.getElementName() + ".class, " + presenter.getElementName()
        + ".MyView.class, " + view.getElementName() + ".class);");
  }

  private void appendBindPresenterCode(IType presenter, IType view, String code)
      throws JavaModelException {
    workingCopy.createImport(presenter.getFullyQualifiedName(), null, null);
    workingCopy.createImport(view.getFullyQualifiedName(), null, null);

    SourceWriter sw = createSourceWriterFor("configure");
    sw.writeLine(code);

    sw.commit(workingCopy.getBuffer());
  }

  public void createConstantBinder(IType annotation, IType nameTokens, IField tokenField)
      throws JavaModelException {
    workingCopy.createImport(annotation.getFullyQualifiedName(), null, null);
    workingCopy.createImport(nameTokens.getFullyQualifiedName(), null, null);

    SourceWriter sw = createSourceWriterFor("configure");

    String code = "bindConstant().annotatedWith(" + annotation.getElementName() + ".class).to("
        + nameTokens.getElementName() + "." + tokenField.getElementName() + ");";
    sw.writeLine(code);

    sw.commit(workingCopy.getBuffer());
  }
}
