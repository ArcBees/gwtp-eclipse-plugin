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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.NamingConventions;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;

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
  protected final SourceWriterFactory sourceWriterFactory;
  protected final ICompilationUnit originalUnit;
  protected final ICompilationUnit workingCopy;
  protected IType workingCopyType;
  private IType type;

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
    originalUnit = createCompilationUnitIfNeeded();
    workingCopy = originalUnit.getWorkingCopy(new NullProgressMonitor());
    workingCopyType = workingCopy.getType(type.getElementName());
  }

  public ProjectClass(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory) throws JavaModelException {
    this.root = root;
    this.packageName = packageName;
    this.elementName = elementName;
    this.sourceWriterFactory = sourceWriterFactory;
    type = root.getJavaProject().findType(packageName + "." + elementName);
    originalUnit = createCompilationUnitIfNeeded();
    workingCopy = originalUnit.getWorkingCopy(new NullProgressMonitor());
  }

  private ICompilationUnit createCompilationUnitIfNeeded() throws JavaModelException {
    ICompilationUnit compilationUnit;
    if (type == null) {
      String cuName = elementName + ".java";
      IPackageFragment pack = root.createPackageFragment(packageName, false, new NullProgressMonitor());
      compilationUnit = pack.createCompilationUnit(cuName, "", false, new NullProgressMonitor());
      compilationUnit.createPackageDeclaration(packageName, new NullProgressMonitor());
    } else {
      compilationUnit = type.getCompilationUnit();
    }
    return compilationUnit;
  }

  protected void init() throws JavaModelException {
    if (type == null) {
      type = createType();
      workingCopyType = workingCopy.getType(type.getElementName());
    }
  }

  protected abstract IType createType() throws JavaModelException;

  public void commit() throws JavaModelException {
	workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, new NullProgressMonitor());
    workingCopy.commitWorkingCopy(true, new NullProgressMonitor());
    workingCopy.discardWorkingCopy();
  }

  public void discard(boolean andDelete) throws JavaModelException {
    workingCopy.discardWorkingCopy();
    if (andDelete) {
    	originalUnit.delete(true, new NullProgressMonitor());
    }
  }
  
  public void becomeWorkingCopy() throws JavaModelException {
	  workingCopy.becomeWorkingCopy(new NullProgressMonitor());
  }

  public IType getType() {
    return workingCopyType;
  }

  public IField createSerializationField() throws JavaModelException {
    Random generator = new Random();
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLine("private static final long serialVersionUID = " + generator.nextLong() + "L;");
    return createField(sw);
  }

  public IField createField(IType fieldType, String fieldName) throws JavaModelException {
    workingCopy.createImport(fieldType.getFullyQualifiedName(), null, new NullProgressMonitor());
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
        "private " + workingCopyType.getElementName() + "() {",
        "  // For serialization only",
        "}");

    return createMethod(sw);
  }

  public IMethod createConstructor(IField... fields) throws JavaModelException {
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
    sw.writeLine("public " + workingCopyType.getElementName() + "(" + params + ") {");
    writeAssignations(fields, sw);
    sw.writeLine("}");

    return createMethod(sw);
  }

  public IMethod createGetterMethod(IField field) throws JavaModelException {
     SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
     sw.writeLines(
         "public " + signature(field) + " " + getterName(field) + "() {",
         "  return " + field.getElementName() + ";",
         "}");

    return createMethod(sw);
  }

  public IMethod createSetterMethod(IField field) throws JavaModelException {
    SourceWriter sw = sourceWriterFactory.createForNewClassBodyComponent();
    sw.writeLines(
        "public void " + setterName(field) + "(" + signature(field) + " "
        + field.getElementName() + ") {",
        "  this." + field.getElementName() + " = " + field.getElementName() + ";" +
        "}");

    return createMethod(sw);
  }

  protected SourceWriter createSourceWriterFor(String methodName, String... params)
      throws JavaModelException {
    return sourceWriterFactory.createForMethod(workingCopyType.getMethod(methodName, params));
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

    return workingCopy.createType(sw.toString(), null, false, new NullProgressMonitor());
  }

  public IMethod createMethod(SourceWriter sw) throws JavaModelException {
    return workingCopyType.createMethod(sw.toString(), null, false, new NullProgressMonitor());
  }

  public IField createField(SourceWriter sw) throws JavaModelException {
    return workingCopyType.createField(sw.toString(), null, false, new NullProgressMonitor());
  }
  
  public IImportDeclaration createImport(String name) throws JavaModelException {
	  return workingCopy.createImport(name, null, new NullProgressMonitor());
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

  protected String getterName(IField field) {
	try {
		return NamingConventions.suggestGetterName(root.getJavaProject(), field.getElementName(), field.getFlags(), JavaModelUtil.isBoolean(field), new String[0]);
	} catch (JavaModelException e) {
	    return "get" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1);
	}
  }

  protected String setterName(IField field) {
	try {
		return NamingConventions.suggestSetterName(root.getJavaProject(), field.getElementName(), field.getFlags(), JavaModelUtil.isBoolean(field), new String[0]);
	} catch (JavaModelException e) {
	    return "set" + field.getElementName().substring(0, 1).toUpperCase() + field.getElementName().substring(1);
	}
  }

  protected String fieldName(IType type) {
    return type.getElementName().substring(0, 1).toLowerCase() + type.getElementName().substring(1);
  }

  protected String signature(IField field) throws JavaModelException {
    return Signature.toString(field.getTypeSignature());
  }
  
  public IBuffer getBuffer() {
	try {
	  return workingCopy.getBuffer();
	} catch (JavaModelException e) {
	  return null;
	}
  }
}
