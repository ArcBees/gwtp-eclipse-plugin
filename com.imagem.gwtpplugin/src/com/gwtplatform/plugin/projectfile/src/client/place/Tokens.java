/**
 * Copyright 2011 IMAGEM Solutions TI sant�
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
public class Tokens extends ProjectClass {

  public Tokens(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
  }

  public Tokens(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    init();
  }

  @Override
  protected IType createType() throws JavaModelException {
    return createClass(null, null);
  }

  public IField createTokenField(String tokenName) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLine(
        "public static final String " + tokenName.replaceAll("!", "") + " = \"" + tokenName
        + "\";");

    return createField(sw);
  }

  public IMethod createTokenGetter(String tokenName) throws JavaModelException {
    String tokenNameWithoutBang = tokenName.replaceAll("!", "");
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLine(
        "public static String " + methodName("get", tokenNameWithoutBang)
        + "() { return " + tokenNameWithoutBang + "; }");

    return createMethod(sw);
  }

}
