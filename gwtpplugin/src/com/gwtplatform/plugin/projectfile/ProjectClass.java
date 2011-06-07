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

package com.gwtplatform.plugin.projectfile;

import java.util.Random;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.gwtplatform.plugin.SourceWriter;
import com.gwtplatform.plugin.SourceWriterFactory;

/**
 * A class that makes it easy to synthesise new java classes.
 *
 * @author Michael Renaud
 * @author Philippe Beaudoin
 */
public abstract class ProjectClass {
  protected final IPackageFragmentRoot root;
  protected final String packageName;
  protected final String elementName;
  protected final ICompilationUnit cu;
  protected final SourceWriterFactory sourceWriterFactory;
  protected IType type;

  public ProjectClass(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    this.root = root;
    int lastDotIndex = fullyQualifiedName.lastIndexOf('.');
    if (lastDotIndex <= 1) {
      packageName = "";
    } else {
      packageName = fullyQualifiedName.substring(0, lastDotIndex);
    }
    elementName = fullyQualifiedName.substring(lastDotIndex + 1);
    this.sourceWriterFactory = sourceWriterFactory;
    type = root.getJavaProject().findType(fullyQualifiedName);
    cu = createTypeIfNeeded();
  }

  public ProjectClass(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    this.root = root;
    this.packageName = packageName;
    this.elementName = elementName;
    this.sourceWriterFactory = sourceWriterFactory;
    type = root.getJavaProject().findType(packageName + "." + elementName);
    cu = createTypeIfNeeded();
  }

  private ICompilationUnit createTypeIfNeeded() throws JavaModelException {
    ICompilationUnit compilationUnit;
    if (type == null) {
      String cuName = elementName + ".java";
      IPackageFragment pack = root.createPackageFragment(packageName, false, null);
      compilationUnit = pack.createCompilationUnit(cuName, "", false, null);
      compilationUnit.becomeWorkingCopy(null);
      compilationUnit.createPackageDeclaration(packageName, null);
    } else {
      compilationUnit = type.getCompilationUnit();
      compilationUnit.becomeWorkingCopy(null);
    }
    return compilationUnit;
  }

  protected void init() throws JavaModelException {
    if (type == null) {
      type = createType();
    }
  }

  protected abstract IType createType() throws JavaModelException;

  public void commit() throws JavaModelException {
    cu.commitWorkingCopy(true, null);
    cu.discardWorkingCopy();
  }

  public void discard() throws JavaModelException {
    cu.discardWorkingCopy();
    if (cu.getSource().isEmpty()) {
      cu.delete(true, null);
    }
  }

  public IType getType() {
    return type;
  }

  public IField createSerializationField() throws JavaModelException {
    Random generator = new Random();
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLine("private static final long serialVersionUID = " + generator.nextLong() + "L;");
    return createField(sw);
  }

  public IField createField(IType fieldType, String fieldName) throws JavaModelException {
    cu.createImport(fieldType.getFullyQualifiedName(), null, null);
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLine("private " + fieldType.getElementName() + " " + fieldName + ";");
    return createField(sw);
  }

  public IField createField(String fieldType, String fieldName) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLine("private " + fieldType + " " + fieldName + ";");
    return createField(sw);
  }

  public IMethod createSerializationConstructor() throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "@SuppressWarnings(\"unused\")",
        "private " + type.getElementName() + "() {",
        "  // For serialization only\n",
        "}");

    return createMethod(sw);
  }

  public IMethod createConstructor(IField[] fields) throws JavaModelException {
    /*
     * if(fields.length == 0) { IMethod serializationConstructor =
     * type.getMethod(type.getElementName(), new String[0]);
     *
     * if(serializationConstructor.exists()) { ISourceRange range =
     * serializationConstructor.getSourceRange();
     *
     * IBuffer buffer = cu.getBuffer(); buffer.replace(range.getOffset(),
     * range.getLength(), ""); buffer.save(null, true); } }
     */
    String params = getParamsString(fields, false, true);

    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLine("public " + type.getElementName() + "(" + params + ") {");
    writeAssignations(fields, sw);
    sw.writeLine("}");

    return createMethod(sw);
  }

  public IMethod createGetterMethod(IField field) throws JavaModelException {
     SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
     // TODO Use NamingConventions.suggestGetterName
     sw.writeLines(
         "public " + signature(field) + " " + methodName("get", field) + "() {",
         "  return " + field.getElementName() + ";",
         "}");

    return createMethod(sw);
  }

  public IMethod createSetterMethod(IField field) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    // TODO Use NamingConventions.suggestSetterName
    sw.writeLines(
        "public void " + methodName("set", field) + "(" + signature(field) + " "
        + field.getElementName() + ") {",
        "  this." + field.getElementName() + " = " + field.getElementName() + ";" +
        "}");

    return createMethod(sw);
  }

  protected SourceWriter createSourceWriterFor(String methodName, String... params)
      throws JavaModelException {
    return sourceWriterFactory.createForMethod(type.getMethod(methodName, params));
  }

  protected IType createClass(String extendedClass, String implementedInterface)
      throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    String extendsString = "";
    String implementsString = "";
    if (extendedClass != null) {
      extendsString = " extends " + extendedClass;
    }
    if (implementedInterface != null) {
      implementsString = " implements " + implementedInterface;
    }

    sw.writeLines("public class " + elementName + extendsString + implementsString + " {", "}");

    return cu.createType(sw.toString(), null, false, null);
  }

  protected IMethod createMethod(SourceWriter sw) throws JavaModelException {
    return type.createMethod(sw.toString(), null, false, null);
  }

  protected IField createField(SourceWriter sw) throws JavaModelException {
    return type.createField(sw.toString(), null, false, null);
  }

  protected String getParamsString(IField[] fields, boolean leadingComma, boolean includeType)
  throws IllegalArgumentException, JavaModelException {
    String params = "";
    for (IField field : fields) {
      if (leadingComma || params.length() > 0) {
        params += ", ";
      }
      if (includeType) {
        params += signature(field) + " ";
      }
      params += field.getElementName();
    }
    return params;
  }

  protected void writeAssignations(IField[] fields, SourceWriter sw) {
    for (IField field : fields) {
      sw.writeLine("  this." + field.getElementName() + " = " + field.getElementName() + ";");
    }
  }

  protected String methodName(String prefix, IField field) {
    return methodName(prefix, field.getElementName());
  }

  protected String methodName(String prefix, String fieldName) {
    return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
  }

  protected String fieldName(IType type) {
    return type.getElementName().substring(0, 1).toLowerCase() + type.getElementName().substring(1);
  }

  protected String signature(IField field) throws JavaModelException {
    return Signature.toString(field.getTypeSignature());
  }
}
