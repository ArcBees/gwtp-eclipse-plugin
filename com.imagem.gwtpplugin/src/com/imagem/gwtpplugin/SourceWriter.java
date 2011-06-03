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

import java.io.StringWriter;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

/**
 * A writer that facilitates indentation of source code. Created via
 * {@link SourceWriterFactory}.
 *
 * @author Philippe Beaudoin
 */
public class SourceWriter {

  private final int formatKind; // See the first parameter of
                                // CodeFomatter.format
  private final int indentationLevel;
  private final CodeFormatter codeFormatter;
  private final StringWriter stringWriter = new StringWriter();
  private final ISourceRange methodRange;
  private final String prefix;
  private final String suffix;

  SourceWriter(int formatKind) {
    this.formatKind = formatKind;
    this.indentationLevel = 0;
    codeFormatter = ToolFactory.createCodeFormatter(null);
    methodRange = null;
    prefix = suffix = "";
  }

  SourceWriter(IMethod methodToAppendTo) throws JavaModelException {
    IScopeContext[] scopeContext = new IScopeContext[] { new InstanceScope() };
    String lineSeparator = Platform.getPreferencesService().getString(Platform.PI_RUNTIME,
        Platform.PREF_LINE_SEPARATOR, System.getProperty("line.separator"), scopeContext);
    this.formatKind = CodeFormatter.K_STATEMENTS;
    this.indentationLevel = 2;
    codeFormatter = ToolFactory.createCodeFormatter(null);
    methodRange = methodToAppendTo.getSourceRange();
    String source = methodToAppendTo.getSource();
    int closingBracketIndex = source.lastIndexOf('}');
    assert closingBracketIndex > 0 : "Cannot append to a method without a closing bracket.";
    int lastBodyLineEndingIndex = source.lastIndexOf(lineSeparator, closingBracketIndex);
    if (lastBodyLineEndingIndex > 0) {
      prefix = source.substring(0, lastBodyLineEndingIndex + lineSeparator.length())
          + lineSeparator;
      suffix = lineSeparator + source.substring(lastBodyLineEndingIndex + lineSeparator.length());
    } else {
      // Insert a CR in the prefix
      prefix = source.substring(0, closingBracketIndex) + lineSeparator;
      suffix = lineSeparator + source.substring(closingBracketIndex);
    }
  }

  /**
   * Writes out a line to the source writer using the current indentation level.
   * Empty lines are not indented.
   *
   * @param line
   *          The line to write out.
   */
  public void writeLine(String line) {
    stringWriter.write(line + '\n');
  }

  /**
   * Writes out a series of lines to the source writer using the current
   * indentation level. Empty lines are not indented.
   *
   * @param lines
   *          The lines to write out.
   */
  public void writeLines(String... lines) {
    for (String line : lines) {
      writeLine(line);
    }
  }

  /**
   * Ensures that the source is written to the specified buffer and that the
   * buffer is saved. If the {@link SourceWriter} was initialized with a
   * {@link IMethod} to append to, then you must use the buffer of that method,
   * and only the method will be replaced.
   *
   * @param buffer
   *          The {@link IBuffer} to append to.
   * @throws JavaModelException
   */
  public void commit(IBuffer buffer) throws JavaModelException {
    if (methodRange != null) {
      buffer.replace(methodRange.getOffset(), methodRange.getLength(), toString());
    } else {
      buffer.setContents(toString());
    }
    buffer.save(null, true);
  }

  @Override
  public String toString() {
    String source = stringWriter.toString();
    TextEdit textEdit = codeFormatter.format(formatKind, source, 0, source.length(),
        indentationLevel, null);
    return prefix + applyTextEdit(source, textEdit) + suffix;
  }

  private String applyTextEdit(String source, TextEdit textEdit) {
    Document document = new Document(source);
    try {
      textEdit.apply(document);
    } catch (MalformedTreeException e) {
      e.printStackTrace();
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
    return document.get();
  }
}
