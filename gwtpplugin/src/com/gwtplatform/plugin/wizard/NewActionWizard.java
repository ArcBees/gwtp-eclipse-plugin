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
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.gwtplatform.plugin.Activator;
import com.gwtplatform.plugin.SourceWriterFactory;
import com.gwtplatform.plugin.projectfile.Field;
import com.gwtplatform.plugin.projectfile.src.server.AbstractHandlerModule;
import com.gwtplatform.plugin.projectfile.src.server.ActionHandler;
import com.gwtplatform.plugin.projectfile.src.server.ActionValidator;
import com.gwtplatform.plugin.projectfile.src.server.guice.GuiceHandlerModule;
import com.gwtplatform.plugin.projectfile.src.server.spring.SpringActionHandler;
import com.gwtplatform.plugin.projectfile.src.server.spring.SpringHandlerModule;
import com.gwtplatform.plugin.projectfile.src.shared.Action;
import com.gwtplatform.plugin.projectfile.src.shared.Result;

/**
 * 
 * @author Michael Renaud
 * @author Nicolas Morel
 * 
 */
public class NewActionWizard extends Wizard implements INewWizard {

  private final SourceWriterFactory sourceWriterFactory;

  private NewActionWizardPage page;
  private IStructuredSelection selection;
  private boolean isDone;

  public NewActionWizard() {
    super();
    setNeedsProgressMonitor(true);
    setWindowTitle("New Action");
    sourceWriterFactory = new SourceWriterFactory();
  }

  @Override
  public void addPages() {
    page = new NewActionWizardPage(selection);
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
      e.printStackTrace();
      return false;
    }
    return isDone;
  }

  protected boolean finish(IProgressMonitor desiredMonitor) {
    IProgressMonitor monitor = desiredMonitor;
    if (monitor == null) {
      monitor = new NullProgressMonitor();
    }

    Action action = null;
    Result result = null;
    ActionHandler actionHandler = null;
    AbstractHandlerModule handlerModule = null;
    try {
      monitor.beginTask("Action creation", 4);

      IPackageFragmentRoot root = page.getPackageFragmentRoot();

      // Result
      monitor.subTask("Result");
      result = new Result(root, page.getResultPackageText(), page.getResultTypeName(),
        sourceWriterFactory);
      // Issue 335: Remove serialization generated uid in Action and Result
      // result.createSerializationField();

      Field[] resultFields = page.getResultFields();
      IField[] fields = new IField[resultFields.length];

      if (resultFields.length > 0) {
        result.createSerializationConstructor();
      }
      for (int i = 0; i < resultFields.length; i++) {
        if (resultFields[i].isPrimitiveType()) {
          fields[i] = result.createField(resultFields[i].getPrimitiveType(),
              resultFields[i].getName());
        } else {
          fields[i] = result.createField(resultFields[i].getType(), resultFields[i].getName());
        }
      }
      result.createConstructor(fields);
      for (IField field : fields) {
        result.createGetterMethod(field);
      }
      monitor.worked(1);

      // Action
      monitor.subTask("Action");
      IType actionSuperclass = page.getJavaProject().findType(page.getActionSuperclass());

      action = new Action(root, page.getPackageText(), page.getTypeName(), sourceWriterFactory,
          actionSuperclass, result.getType());
      // Issue 335: Remove serialization generated uid in Action and Result
      // action.createSerializationField();

      Field[] actionFields = page.getActionFields();
      fields = new IField[actionFields.length];

      if (actionFields.length > 0) {
        action.createSerializationConstructor();
      }
      for (int i = 0; i < actionFields.length; i++) {
        if (actionFields[i].isPrimitiveType()) {
          fields[i] = action.createField(actionFields[i].getPrimitiveType(),
              actionFields[i].getName());
        } else {
          fields[i] = action.createField(actionFields[i].getType(), actionFields[i].getName());
        }
      }
      action.createConstructor(fields);
      for (IField field : fields) {
        action.createGetterMethod(field);
      }
      monitor.worked(1);

      // ActionHandler
      monitor.subTask("ActionHandler");
      actionHandler = createActionHandler(root, action, result);
      actionHandler.createConstructor();
      actionHandler.createExecuteMethod(action.getType(), result.getType());
      actionHandler.createUndoMethod(action.getType(), result.getType());
      actionHandler.createActionTypeGetterMethod(action.getType());
      monitor.worked(1);

      // HandlerModule
      monitor.subTask("Bind in HandlerModule");
      handlerModule = createHandlerModule(root);
      if (page.getActionValidator().isEmpty()) {
        handlerModule.createBinder(action.getType(), actionHandler.getType());
      } else {
        ActionValidator actionValidator = new ActionValidator(root, page.getActionValidator(),
            sourceWriterFactory);
        handlerModule.createBinder(action.getType(), actionHandler.getType(),
            actionValidator.getType());
      }
      monitor.worked(1);

      // Committing
      if (action != null) {
        action.commit();
      }
      if (result != null) {
        result.commit();
      }
      if (actionHandler != null) {
        actionHandler.commit();
      }
      if (handlerModule != null) {
        handlerModule.commit();
      }
    } catch (JavaModelException e) {
      e.printStackTrace();

      try {
        if (action != null) {
          action.discard(true);
        }
        if (result != null) {
          result.discard(true);
        }
        if (actionHandler != null) {
          actionHandler.discard(true);
        }
        if (handlerModule != null) {
          handlerModule.discard(false);
        }
      } catch (JavaModelException e1) {
      }
      
      IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "An unexpected error has happened. Close the wizard and retry.", e);
      
      ErrorDialog.openError(getShell(), null, null, status);
      
      return false;
    }

    monitor.done();
    return true;
  }
  
  private ActionHandler createActionHandler(IPackageFragmentRoot root, Action action, Result result) throws JavaModelException {
    if (isSpringHandlerModule(root)) {
      return new SpringActionHandler(root, page.getActionHandlerPackageText(), page.getActionHandlerTypeName(), sourceWriterFactory, action.getType(), result.getType());
    } else {
      return new ActionHandler(root, page.getActionHandlerPackageText(), page.getActionHandlerTypeName(), sourceWriterFactory, action.getType(), result.getType());
    }
  }

  private AbstractHandlerModule createHandlerModule(IPackageFragmentRoot root) throws JavaModelException {
    if (isSpringHandlerModule(root)) {
      return new SpringHandlerModule(root, page.getHandlerModule(), sourceWriterFactory);
    } else {
      return new GuiceHandlerModule(root, page.getHandlerModule(), sourceWriterFactory);
    }
   }

  private boolean isSpringHandlerModule(IPackageFragmentRoot root) throws JavaModelException {
    String handlerModuleString = page.getHandlerModule();
    IType handlerModuleType = root.getJavaProject().findType(handlerModuleString);
    ITypeHierarchy hierarchy = handlerModuleType.newSupertypeHierarchy(null);
    IType[] superclasses = hierarchy.getAllClasses();
    for (IType superclass : superclasses) {
      if (superclass.getFullyQualifiedName('.').equals(GuiceHandlerModule.C_HANDLER_MODULE)) {
        return false;
      } else if (superclass.getFullyQualifiedName('.').equals(
        SpringHandlerModule.C_HANDLER_MODULE)) {
        return true;
      }
    }
    return false;
  }

}
