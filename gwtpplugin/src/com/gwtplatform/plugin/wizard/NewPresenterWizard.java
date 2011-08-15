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

package com.gwtplatform.plugin.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.gwtplatform.plugin.Activator;
import com.gwtplatform.plugin.SourceWriterFactory;
import com.gwtplatform.plugin.projectfile.src.client.core.Presenter;
import com.gwtplatform.plugin.projectfile.src.client.core.Ui;
import com.gwtplatform.plugin.projectfile.src.client.core.View;
import com.gwtplatform.plugin.projectfile.src.client.gin.Ginjector;
import com.gwtplatform.plugin.projectfile.src.client.gin.PresenterModule;
import com.gwtplatform.plugin.projectfile.src.client.place.PlaceAnnotation;
import com.gwtplatform.plugin.projectfile.src.client.place.Tokens;

/**
 *
 * @author Michael Renaud
 *
 */
public class NewPresenterWizard extends Wizard implements INewWizard {

  private final SourceWriterFactory sourceWriterFactory;

  private NewPresenterWizardPage page;
  private IStructuredSelection selection;
  private boolean isDone;

  public NewPresenterWizard() {
    super();
    setNeedsProgressMonitor(true);
    setWindowTitle("New Event");
    sourceWriterFactory = new SourceWriterFactory();
  }

  @Override
  public void addPages() {
    page = new NewPresenterWizardPage(selection);
    addPage(page);
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    this.selection = selection;
  }

  @Override
  public boolean performFinish() {
    try {
      super.getContainer().run(false, false, new IRunnableWithProgress() {
        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {
          isDone = finish(monitor);
        }
      });
    } catch (Exception e) {
      return false;
    }
    return isDone;
  }

  protected boolean finish(IProgressMonitor desiredMonitor) {
    IProgressMonitor monitor = desiredMonitor;
    if (desiredMonitor == null) {
      monitor = new NullProgressMonitor();
    }

    Presenter presenter = null;
    Tokens tokens = null;
    View view = null;
    Ui ui = null;
    Ginjector ginjector = null;
    PresenterModule presenterModule = null;
    PlaceAnnotation newPlaceAnnotation = null;

    IField tokenField = null;
    try {
      monitor.beginTask("Presenter creation", 3);

      IPackageFragmentRoot root = page.getPackageFragmentRoot();

      // Presenter
      monitor.subTask("Presenter");
      presenter = new Presenter(root, page.getPackageText(), page.getTypeName(),
          sourceWriterFactory, page.isWidget());
      if (page.isPopup()) {
        presenter.createPopupViewInterface();
      } else {
        presenter.createViewInterface();
      }
      if (page.isPlace()) {
        tokens = new Tokens(root, page.getTokenClass(), sourceWriterFactory);
        tokenField = tokens.createTokenField(page.getTokenName());
        tokens.createTokenGetter(page.getTokenName());

        if (page.getGatekeeper().isEmpty()) {
          presenter.createProxyPlaceInterface(page.isProxyStandard(), tokens.getType(),
              page.getTokenName());
        } else {
          IType gatekeeper = page.getJavaProject().findType(page.getGatekeeper());

          presenter.createProxyPlaceInterface(page.isProxyStandard(), tokens.getType(),
              page.getTokenName(), gatekeeper);
        }
      } else if (!page.isWidget()) {
        presenter.createProxyInterface(page.isProxyStandard());
      }

      presenter.createConstructor();

      if (!page.isWidget()) {
        IType revealEvent = page.getJavaProject().findType(page.getRevealEvent());
        if (revealEvent.getElementName().equals("RevealContentEvent")) {
          IType parent = page.getJavaProject().findType(page.getParent());

          presenter.createRevealInParentMethod(revealEvent, parent, page.getContentSlot());
        } else {
          presenter.createRevealInParentMethod(revealEvent);
        }
      }

      String[] methods = page.getMethodStubs();
      for (String method : methods) {
        presenter.createMethodStub(method);
      }
      monitor.worked(1);

      // View
      monitor.subTask("View");
      view = new View(root, page.getViewPackageText(), page.getViewTypeName(), sourceWriterFactory,
          presenter.getType(), page.isPopup());
      if (page.useUiBinder()) {
        view.createBinderInterface();
        view.createWidgetField();
      }
      view.createConstructor(page.useUiBinder());
      view.createAsWidgetMethod(page.useUiBinder());

      // Ui
      if (page.useUiBinder()) {
        monitor.subTask("UiBinder");
        ui = new Ui(root, page.getViewPackageText(), page.getViewTypeName());
        ui.createFile(page.isPopup());
      }

      // Ginjector
      if (!page.isWidget()) {
        monitor.subTask("Provider in Ginjector");
        ginjector = new Ginjector(root, page.getGinjector(), sourceWriterFactory);
        ginjector.createProvider(presenter.getType());
      }
      monitor.worked(1);

      // PresenterModule
      monitor.subTask("Bind in PresenterModule");
      presenterModule = new PresenterModule(root, page.getPresenterModule(), sourceWriterFactory);
      if (page.isWidget()) {
        if (page.isSingleton()) {
          presenterModule.createSingletonPresenterWidgetBinder(presenter.getType(), view.getType());
        } else {
          presenterModule.createPresenterWidgetBinder(presenter.getType(), view.getType());
        }
      } else {
        presenterModule.createPresenterBinder(presenter.getType(), view.getType());
      }
      if (!page.getAnnotation().isEmpty()) {
        IType annotation = root.getJavaProject().findType(page.getAnnotation());
        if (annotation == null) {
          // Annotation type doesn't exist, create it!
          newPlaceAnnotation = new PlaceAnnotation(root, page.getAnnotation(), sourceWriterFactory);
          annotation = newPlaceAnnotation.getType();
        }
        presenterModule.createConstantBinder(annotation, tokens.getType(), tokenField);
      }
      monitor.worked(1);

      if (presenter != null) {
        presenter.commit();
      }
      if (tokens != null) {
        tokens.commit();
      }
      if (view != null) {
        view.commit();
      }
      if (ginjector != null) {
        ginjector.commit();
      }
      if (presenterModule != null) {
        presenterModule.commit();
      }
      if (newPlaceAnnotation != null) {
        newPlaceAnnotation.commit();
      }
    } catch (Exception e) {
      try {
        if (presenter != null) {
          presenter.discard(true);
        }
        if (tokens != null) {
          tokens.discard(false);
        }
        if (view != null) {
          view.discard(true);
        }
        if (ui != null) {
          ui.getFile().delete(true, null);
        }
        if (ginjector != null) {
          ginjector.discard(false);
        }
        if (presenterModule != null) {
          presenterModule.discard(false);
        }
      } catch (Exception e1) {
          e1.printStackTrace();
      	}
      
      IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "An unexpected error has happened. Close the wizard and retry.", e);
      
      ErrorDialog.openError(getShell(), null, null, status);

      return false;
    }
    monitor.done();
    return true;
  }

}
