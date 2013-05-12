package com.arcbees.ide.plugin.eclipse.wizard.createproject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;

public class FetchArchetypesMonitor implements IProgressMonitor {
    private ProgressBar progressBar;

    public FetchArchetypesMonitor(Composite parent) {
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
                    System.out.println("reset");
                } else {
                    progressBar.setSelection(progressBar.getSelection() + work);
                    System.out.println("sel=" + progressBar.getSelection() + work);
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
}