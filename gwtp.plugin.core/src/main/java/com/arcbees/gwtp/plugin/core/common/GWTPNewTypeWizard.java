/**
 * Copyright 2014 ArcBees Inc.
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

package com.arcbees.gwtp.plugin.core.common;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;

@SuppressWarnings("restriction")
public abstract class GWTPNewTypeWizard extends NewElementWizard {

    private final GWTPNewTypeWizardPage page;

    public GWTPNewTypeWizard(String windowTitle, GWTPNewTypeWizardPage page) {
        setWindowTitle("Create GWTP Presenter");
        setHelpAvailable(false);
        this.page = page;
    }

    @Override
    public void addPages() {
        page.init(getSelection());
        addPage(page);
    }

    @Override
    public IJavaElement getCreatedElement() {
        return page.getCreatedType();
    }

    @Override
    public boolean performFinish() {
        warnAboutTypeCommentDeprecation();
        return super.performFinish();
    }

    protected boolean autoOpen() {
        return false;
    }

    @Override
    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
        page.createType(monitor);
        if (autoOpen()) {
            selectAndReveal(getCreatedElement().getResource());
            getCreatedElement().getOpenable().open(monitor);
        }
    }
}
