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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
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
public class PlaceAnnotation extends ProjectClass {

  private static final String A_BINDING_ANNOTATION = "com.google.inject.BindingAnnotation";
  private static final String A_TARGET = "java.lang.annotation.Target";
  private static final String A_RETENTION = "java.lang.annotation.Retention";
  private static final String E_FIELD = "java.lang.annotation.ElementType.FIELD";
  private static final String E_PARAMETER = "java.lang.annotation.ElementType.PARAMETER";
  private static final String E_METHOD = "java.lang.annotation.ElementType.METHOD";
  private static final String E_RUNTIME = "java.lang.annotation.RetentionPolicy.RUNTIME";

  public PlaceAnnotation(IPackageFragmentRoot root, String fullyQualifiedName,
      SourceWriterFactory sourceWriterFactory)
      throws JavaModelException {
    super(root, fullyQualifiedName, sourceWriterFactory);
    init();
  }

  public PlaceAnnotation(IPackageFragmentRoot root, String packageName, String elementName,
      SourceWriterFactory sourceWriterFactory)
      throws JavaModelException {
    super(root, packageName, elementName, sourceWriterFactory);
    init();
  }

  @Override
  protected IType createType() throws JavaModelException {
	  SourceWriter sw = sourceWriterFactory.createForNewClass();

	  sw.writeLines(
			  "@BindingAnnotation",
			  "@Target({ FIELD, PARAMETER, METHOD })",
			  "@Retention(RUNTIME)",
			  "public @interface " + elementName + " {",
			  "}");

	  IType result = workingCopy.createType(sw.toString(), null, false, new NullProgressMonitor());
	  workingCopy.createImport(A_BINDING_ANNOTATION, null, new NullProgressMonitor());

	  workingCopy.createImport(A_TARGET, null, new NullProgressMonitor());
	  workingCopy.createImport(E_FIELD, null, Flags.AccStatic, new NullProgressMonitor());
	  workingCopy.createImport(E_PARAMETER, null, Flags.AccStatic, new NullProgressMonitor());
	  workingCopy.createImport(E_METHOD, null, Flags.AccStatic, new NullProgressMonitor());
	  workingCopy.createImport(A_RETENTION, null, new NullProgressMonitor());
	  workingCopy.createImport(E_RUNTIME, null, Flags.AccStatic, new NullProgressMonitor());

	  return result;
  }

}
