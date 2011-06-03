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

package com.imagem.gwtpplugin.projectfile.src.shared;

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
public class Model extends ProjectClass {

  private static final String I_SERIALIZABLE = "java.io.Serializable";

  public Model(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
  }

  public Model(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    init();
  }

  @Override
  protected IType createType() throws JavaModelException {
    cu.createImport(I_SERIALIZABLE, null, null);
    return createClass(null, "Serializable");
  }

  public IMethod createConstructor() throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "public " + type.getElementName() + "() {",
        "}");

    return createMethod(sw);
  }

  /*
   * public IMethod createEqualsMethod(IField[] fields) throws
   * IllegalArgumentException, JavaModelException { String contents = "";
   *
   * contents += "@Override\n"; contents +=
   * "public boolean equals(Object anObject) {\n"; contents +=
   * "\tif(this == anObject) return true;\n"; contents +=
   * "\tif(!(anObject instanceof " + type.getElementName() +
   * ")) return false;\n"; contents += "\t" + type.getElementName() + " that = ("
   * + type.getElementName() + ") anObject;\n\n";
   *
   * contents += "\treturn\n"; for(int i = 0; i < fields.length; i++) { IField
   * field = fields[i]; String fieldType =
   * Signature.toString(field.getTypeSignature());
   *
   * contents += "\t\t"; if(fieldType.equals("byte") || fieldType.equals("short")
   * || fieldType.equals("int") || fieldType.equals("long") ||
   * fieldType.equals("char") || fieldType.equals("boolean")) contents +=
   * "this.get" + field.getElementName().substring(0, 1).toUpperCase() +
   * field.getElementName().substring(1) + "() == that.get" +
   * field.getElementName().substring(0, 1).toUpperCase() +
   * field.getElementName().substring(1) + "()"; else
   * if(fieldType.equals("float")) contents += "Float.floatToIntBits(this.get" +
   * field.getElementName().substring(0, 1).toUpperCase() +
   * field.getElementName().substring(1) +
   * "()) == Float.floatToIntBits(that.get" +
   * field.getElementName().substring(0, 1).toUpperCase() +
   * field.getElementName().substring(1) + "())"; else
   * if(fieldType.equals("double")) contents +=
   * "Double.doubleToLongBits(this.get" + field.getElementName().substring(0,
   * 1).toUpperCase() + field.getElementName().substring(1) +
   * "()) == Double.doubleToLongBits(that.get" +
   * field.getElementName().substring(0, 1).toUpperCase() +
   * field.getElementName().substring(1) + "())"; else contents += "this.get" +
   * field.getElementName().substring(0, 1).toUpperCase() +
   * field.getElementName().substring(1) + "() == null ? that.get" +
   * field.getElementName().substring(0, 1).toUpperCase() +
   * field.getElementName().substring(1) + "() == null : this.get" +
   * field.getElementName().substring(0, 1).toUpperCase() +
   * field.getElementName().substring(1) + "().equals(that.get" +
   * field.getElementName().substring(0, 1).toUpperCase() +
   * field.getElementName().substring(1) + "())";
   *
   * if(i < fields.length - 1) contents += " &&\n"; else contents += ";\n"; }
   * contents += "}";
   *
   * return type.createMethod(contents, null, false, null); }
   *
   * public IMethod createHashCodeMethod(IField[] fields) throws
   * IllegalArgumentException, JavaModelException { String contents = "";
   * contents += "@Override\n"; contents += "public int hashCode() {\n";
   * contents += "\tfinal int multiplier = 23;\n"; contents +=
   * "\tint hashCode = 17;\n"; for(IField field : fields) { String fieldType =
   * Signature.toString(field.getTypeSignature());
   *
   * contents += "\thashCode = multiplier * hashCode + ";
   * if(fieldType.equals("boolean")) contents += "(" + field.getElementName() +
   * " ? 1 : 0);\n"; else if(fieldType.equals("byte") ||
   * fieldType.equals("short") || fieldType.equals("int") ||
   * fieldType.equals("char")) contents += "(int) " + field.getElementName() +
   * ";\n"; else if(fieldType.equals("long")) contents += "(int) (" +
   * field.getElementName() + "^(" + field.getElementName() + ">>>32));\n"; else
   * if(fieldType.equals("double")) contents +=
   * "(int) (Double.doubleToLongBits(" + field.getElementName() +
   * ")^(Double.doubleToLongBits(" + field.getElementName() + ")>>>32));\n";
   * else if(fieldType.equals("float")) contents += "Float.floatToIntBits(" +
   * field.getElementName() + ");\n"; else contents += "(" +
   * field.getElementName() + " == null ? 0 : " + field.getElementName() +
   * ".hashCode());\n"; } contents += "\treturn hashCode;\n"; contents += "}";
   *
   * return type.createMethod(contents, null, false, null); }
   */
}
