/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.arcbees.gwtp.plugin.core.util;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class CodeFormattingUtil {
    private IJavaProject javaProject;
    private IProgressMonitor progressMonitor;
    private CodeFormatter codeFormatter;

    public CodeFormattingUtil(IJavaProject javaProject, IProgressMonitor progressMonitor) {
        this.javaProject = javaProject;
        this.progressMonitor = progressMonitor;

        codeFormatter = createCodeFormatter();
    }

    private CodeFormatter createCodeFormatter() {
        Map options = javaProject.getOptions(true);
        return ToolFactory.createCodeFormatter(options);
    }

    public String formatCodeJavaClass(IDocument document) throws JavaModelException {
        TextEdit edit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, document.get(), 0, document.get()
                .length(), 0, null);
        try {
            edit.apply(document);
        } catch (Exception e) {
            // TODO
            e.printStackTrace();
        }

        return document.get();
    }

    public String formatCodeJavaClass(String contents) throws JavaModelException {
        IDocument document = new Document(contents);
        TextEdit edit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, document.get(), 0, document.get()
                .length(), 0, null);
        try {
            edit.apply(document);
        } catch (Exception e) {
            // TODO
            e.printStackTrace();
        }

        return document.get();
    }

    public void formatCodeJavaClassAndSaveIt(ICompilationUnit unit, boolean forceWriting) throws JavaModelException {
        IDocument document = new Document(unit.getSource());
        TextEdit edit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, document.get(), 0, document.get()
                .length(), 0, null);
        try {
            edit.apply(document);
        } catch (Exception e) {
            // TODO
            e.printStackTrace();
        }

        String newSource = document.get();

        // update of the compilation unit and save it
        IBuffer buffer = unit.getBuffer();
        buffer.setContents(newSource);
        buffer.save(progressMonitor, forceWriting);
    }

    public CodeFormatter getCodeFormatter() {
        return codeFormatter;
    }

    public void organizeImports(ICompilationUnit unit) {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }

        IWorkbenchPartSite workBenchSite = window.getPartService().getActivePart().getSite();
        if (workBenchSite == null) {
            return;
        }

        OrganizeImportsAction organizeImportsAction = new OrganizeImportsAction(workBenchSite);

        organizeImportsAction.run(unit);
    }
}
