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

package com.gwtplatform.plugin.projectfile.src.client.place;

import java.util.ArrayList;
import java.util.List;

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
public class PlaceManager extends ProjectClass {

  private static final String C_PLACE_MANAGER_IMPL = "com.gwtplatform.mvp.client.proxy.PlaceManagerImpl";
  private static final String C_PLACE_REQUEST = "com.gwtplatform.mvp.client.proxy.PlaceRequest";
  private static final String I_EVENT_BUS = "com.google.gwt.event.shared.EventBus";
  private static final String I_TOKEN_FORMATTER = "com.gwtplatform.mvp.client.proxy.TokenFormatter";
  private static final String A_INJECT = "com.google.inject.Inject";

  public PlaceManager(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
  }

  public PlaceManager(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    init();
  }

  @Override
  protected IType createType() throws JavaModelException {
    cu.createImport(C_PLACE_MANAGER_IMPL, null, null);
    return createClass("PlaceManagerImpl", null);
  }

  public IField createPlaceRequestField(IType annotation) throws JavaModelException {
    cu.createImport(C_PLACE_REQUEST, null, null);
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLine("private final PlaceRequest " + fieldName(annotation) + "Request;");
    return createField(sw);
  }

  public IMethod createConstructor(IType[] annotations, IField[] placeRequests)
      throws JavaModelException {
    assert annotations.length == placeRequests.length :
      "Number of annotations doesnt match number of place requests.";
    String params = "";
    List<String> assignations = new ArrayList<String>(annotations.length);
    for (int i = 0; i < annotations.length; i++) {
      cu.createImport(annotations[i].getFullyQualifiedName(), null, null);
      params += ", @" + annotations[i].getElementName() + " final String "
          + fieldName(annotations[i]) + "NameToken";
      assignations.add("this." + placeRequests[i].getElementName() + " = new PlaceRequest("
          + fieldName(annotations[i]) + "NameToken);");
    }

    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();

    cu.createImport(A_INJECT, null, null);
    cu.createImport(I_EVENT_BUS, null, null);
    cu.createImport(I_TOKEN_FORMATTER, null, null);
    sw.writeLines(
        "@Inject",
        "public " + type.getElementName() + "(final EventBus eventBus, "
        + "final TokenFormatter tokenFormatter" + params + ") {",
        "  super(eventBus, tokenFormatter);");

    String[] assignationsArray = new String[assignations.size()];
    assignations.toArray(assignationsArray);
    sw.writeLines(assignationsArray);
    sw.writeLine("}");

    return createMethod(sw);
  }

  public IMethod createRevealDefaultPlaceMethod(IField defaultPlace) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "@Override",
        "public void revealDefaultPlace() {",
        "  revealPlace(" + defaultPlace.getElementName() + ", false);",
        "}");

    return createMethod(sw);
  }
}
