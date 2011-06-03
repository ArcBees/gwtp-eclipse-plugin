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

package com.imagem.gwtpplugin;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.formatter.CodeFormatter;

/**
 * A configurable factory to create {@link SourceWriter} objects.
 *
 * @author Philippe Beaudoin
 */
public class SourceWriterFactory {

  /**
   * Creates a new {@link SourceWriter} that can be used to format a new component inside the body
   * of a class, for example a field or a method.
   *
   * @return The new {@link SourceWriter}.
   */
  public SourceWriter createForNewClassBodyComponent() {
    return new SourceWriter(CodeFormatter.K_CLASS_BODY_DECLARATIONS);
  }

  /**
   * Creates a new {@link SourceWriter} that can be used to format a new class at the top-level of
   * the compilation unit.
   *
   * @return The new {@link SourceWriter}.
   */
  public SourceWriter createForNewClass() {
    return new SourceWriter(CodeFormatter.K_COMPILATION_UNIT);
  }

  /**
   * Creates a {@link SourceWriter} meant to append to the provided method. You
   * will have to call {@link SourceWriter#commit()} in order for the new
   *
   * @param method
   *          The method to append to.
   * @return The new {@link SourceWriter}.
   * @throws JavaModelException
   */
  public SourceWriter createForMethod(IMethod method) throws JavaModelException {
    return new SourceWriter(method);
  }

}
