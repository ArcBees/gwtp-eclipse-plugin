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

package com.arcbees.plugin.eclipse.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

public class ProgressMonitor implements IProgressMonitor {
    private ProgressBar progressBar;

    public ProgressMonitor(Composite parent) {
        progressBar = new ProgressBar(parent, SWT.SMOOTH);
        progressBar.setBounds(100, 10, 200, 20);
    }

    @Override
    public void worked(final int work) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                if (progressBar.getSelection() >= 100) {
                    progressBar.setSelection(0);
                } else {
                    progressBar.setSelection(progressBar.getSelection() + work);
                }
            }
        });
    }
    
    public void reset() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                progressBar.setSelection(0);
            }
        });
    }

    @Override
    public void subTask(String name) {
    }

    @Override
    public void setTaskName(String name) {
    }

    @Override
    public void setCanceled(boolean value) {
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public void internalWorked(double work) {
    }

    @Override
    public void done() {
    }

    @Override
    public void beginTask(final String name, final int totalWork) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                progressBar.setMaximum(totalWork);
                progressBar.setToolTipText(name);
            }
        });
    }

    public void setVisible(final boolean visible) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisible(visible);
            }
        });
    }
}