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

package com.imagem.gwtpplugin.view;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.ui.dialogs.ITypeInfoFilterExtension;
import org.eclipse.jdt.ui.dialogs.ITypeInfoRequestor;
import org.eclipse.jdt.ui.dialogs.TypeSelectionExtension;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import com.imagem.gwtpplugin.Activator;

/**
 * 
 * @author Michael Renaud
 *
 */
@SuppressWarnings("restriction")
public class GwtPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	private IJavaProject project;
	private Text nameTokens;
	private Text ginjector;
	private Text presenterModule;
	private Text handlerModule;

	public GwtPropertyPage() {
		super();
	}

	@Override
	protected Control createContents(Composite parent) {
		if(getElement() instanceof IProject) {
			project = JavaCore.create((IProject) getElement());
		}
		else if(getElement() instanceof IJavaProject) {
			project = (IJavaProject) getElement();
		}

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(layout);

		// Token
		Label label = new Label(composite, SWT.NULL);
		label.setText("NameTokens:");

		nameTokens = new Text(composite, SWT.BORDER | SWT.SINGLE);
		nameTokens.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameTokens.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				pageChanged();
			}
		});

		Button browse = new Button(composite, SWT.PUSH);
		browse.setText("Browse...");
		browse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				nameTokens.setText(chooseTokenName().getFullyQualifiedName('.'));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				nameTokens.setText(chooseTokenName().getFullyQualifiedName('.'));
			}
		});

		// Ginjector
		label = new Label(composite, SWT.NULL);
		label.setText("Ginjector:");

		ginjector = new Text(composite, SWT.BORDER | SWT.SINGLE);
		ginjector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ginjector.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				pageChanged();
			}
		});

		browse = new Button(composite, SWT.PUSH);
		browse.setText("Browse...");
		browse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ginjector.setText(chooseGinjector().getFullyQualifiedName('.'));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				ginjector.setText(chooseGinjector().getFullyQualifiedName('.'));
			}
		});

		// Presenter Module
		label = new Label(composite, SWT.NULL);
		label.setText("Presenter Module:");

		presenterModule = new Text(composite, SWT.BORDER | SWT.SINGLE);
		presenterModule.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		presenterModule.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				pageChanged();
			}
		});

		browse = new Button(composite, SWT.PUSH);
		browse.setText("Browse...");
		browse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenterModule.setText(choosePresenterModule().getFullyQualifiedName('.'));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				presenterModule.setText(choosePresenterModule().getFullyQualifiedName('.'));
			}
		});

		// HandlerModule
		label = new Label(composite, SWT.NULL);
		label.setText("HandlerModule:");

		handlerModule = new Text(composite, SWT.BORDER | SWT.SINGLE);
		handlerModule.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		handlerModule.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				pageChanged();
			}
		});

		browse = new Button(composite, SWT.PUSH);
		browse.setText("Browse...");
		browse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handlerModule.setText(chooseHandlerModule().getFullyQualifiedName('.'));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handlerModule.setText(chooseHandlerModule().getFullyQualifiedName('.'));
			}
		});

		setValues();

		return composite;
	}

	protected void pageChanged() {
		// Token
		if(!nameTokens.getText().isEmpty()) {
			try {
				IType type = project.findType(nameTokens.getText());
				if(type == null || !type.exists()) {
					setErrorMessage(nameTokens.getText() + " doesn't exist");
					return;
				}
				if(type.isBinary()) {
					setErrorMessage(nameTokens.getText() + " is a Binary class");
					return;
				}
			}
			catch(JavaModelException e) {
				setErrorMessage("An unexpected error has happened. Close the wizard and retry.");
				return;
			}
		}

		// Ginjector
		if(!ginjector.getText().isEmpty()) {
			try {
				IType type = project.findType(ginjector.getText());
				if(type == null || !type.exists()) {
					setErrorMessage(ginjector.getText() + " doesn't exist");
					return;
				}
				if(type.isBinary()) {
					setErrorMessage(ginjector.getText() + " is a Binary class");
					return;
				}
				ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
				IType[] interfaces = hierarchy.getAllInterfaces();
				boolean isGinjector = false;
				for(IType inter : interfaces) {
					if(inter.getFullyQualifiedName('.').equals("com.google.gwt.inject.client.Ginjector")) {
						isGinjector = true;
						break;
					}
				}
				if(!isGinjector) {
					setErrorMessage(ginjector.getText() + " doesn't extend Ginjector");
					return;
				}
			}
			catch(JavaModelException e) {
				setErrorMessage("An unexpected error has happened. Close the wizard and retry.");
				return;
			}
		}

		// PresenterModule
		if(!presenterModule.getText().isEmpty()) {
			try {
				IType type = project.findType(presenterModule.getText());
				if(type == null || !type.exists()) {
					setErrorMessage(presenterModule.getText() + " doesn't exist");
					return;
				}
				if(type.isBinary()) {
					setErrorMessage(ginjector.getText() + " is a Binary class");
					return;
				}
				ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
				IType[] superclasses = hierarchy.getAllClasses();
				boolean isPresenterModule = false;
				for(IType superclass : superclasses) {
					if(superclass.getFullyQualifiedName('.').equals("com.gwtplatform.mvp.client.gin.AbstractPresenterModule")) {
						isPresenterModule = true;
						break;
					}
				}
				if(!isPresenterModule) {
					setErrorMessage(ginjector.getText() + " doesn't implement AbstractPresenterModule");
					return;
				}
			}
			catch(JavaModelException e) {
				setErrorMessage("An unexpected error has happened. Close the wizard and retry.");
				return;
			}
		}

		// HandlerModule
		if(!handlerModule.getText().isEmpty()) {
			try {
				IType type = project.findType(handlerModule.getText());
				if(type == null || !type.exists()) {
					setErrorMessage(handlerModule.getText() + " doesn't exist");
					return;
				}
				ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
				IType[] superclasses = hierarchy.getAllClasses();
				boolean isHandlerModule = false;
				for(IType superclass : superclasses) {
					if(superclass.getFullyQualifiedName('.').equals("com.gwtplatform.dispatch.server.guice.HandlerModule")) {
						isHandlerModule = true;
						break;
					}
				}
				if(!isHandlerModule) {
					setErrorMessage(handlerModule.getText() + " doesn't extend HandlerModule");
					return;
				}
			}
			catch (JavaModelException e) {
				setErrorMessage("An unexpected error has happened. Close the wizard and retry.");
				return;
			}
		}

		setErrorMessage(null);
		super.setValid(true);
	}

	public void setErrorMessage(String message) {
		super.setErrorMessage(message);
		setValid(false);
	}

	private void setValues() {
		try {
			String nameTokensValue = project.getProject().getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, "nametokens"));
			String ginjectorValue = project.getProject().getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, "ginjector"));
			String presenterModuleValue = project.getProject().getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, "presentermodule"));
			String handlerModuleValue = project.getProject().getPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, "handlermodule"));

			nameTokens.setText(nameTokensValue == null ? "" : nameTokensValue);
			ginjector.setText(ginjectorValue == null ? "" : ginjectorValue);
			presenterModule.setText(presenterModuleValue == null ? "" : presenterModuleValue);
			handlerModule.setText(handlerModuleValue == null ? "" : handlerModuleValue);
		}
		catch(CoreException e1) {}

		pageChanged();
	}

	public boolean performOk() {
		try {
			project.getProject().setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, "nametokens"), nameTokens.getText());
			project.getProject().setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, "ginjector"), ginjector.getText());
			project.getProject().setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, "presentermodule"), presenterModule.getText());
			project.getProject().setPersistentProperty(new QualifiedName(Activator.PLUGIN_ID, "handlermodule"), handlerModule.getText());
		}
		catch(CoreException e) {
			e.printStackTrace();
		}
		return super.performOk();
	}

	public void performDefaults() {
		nameTokens.setText("");
		ginjector.setText("");
		presenterModule.setText("");
		handlerModule.setText("");

		super.performDefaults();
		pageChanged();
	}

	protected IType chooseTokenName() {
		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[] { project };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, null, scope, IJavaSearchConstants.CLASS, new TokenNameSelectionExtension());
		dialog.setTitle("Token name Selection");
		dialog.setMessage("Select the Tokens class");
		dialog.setInitialPattern("*Tokens");

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	protected IType chooseGinjector() {
		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[] { project };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, null, scope, IJavaSearchConstants.INTERFACE, new GinjectorSelectionExtension());
		dialog.setTitle("Ginjector Selection");
		dialog.setMessage("Select a Ginjector");
		dialog.setInitialPattern("*Ginjector");

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	protected IType choosePresenterModule() {
		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[] { project };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, null, scope, IJavaSearchConstants.CLASS, new PresenterModuleSelectionExtension());
		dialog.setTitle("PresenterModule Selection");
		dialog.setMessage("Select a PresenterModule");
		dialog.setInitialPattern("*Module");

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	private IType chooseHandlerModule() {
		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[] { project };
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(getShell(), false, null, scope, IJavaSearchConstants.CLASS, new HandlerModuleSelectionExtension());
		dialog.setTitle("HandlerModule selection");
		dialog.setMessage("Select an HandlerModule class");
		dialog.setInitialPattern("ServerModule");

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	public class TokenNameSelectionExtension extends TypeSelectionExtension {
		@Override
		public ITypeInfoFilterExtension getFilterExtension() {
			ITypeInfoFilterExtension extension = new ITypeInfoFilterExtension() {
				@Override
				public boolean select(ITypeInfoRequestor requestor) {
					try {
						IType type = project.findType(requestor.getPackageName() + "." + requestor.getTypeName());
						if(type == null || !type.exists() || type.isBinary()) {
							return false;
						}
						return true;
					}
					catch (JavaModelException e) {
						return false;
					}
				}
			};
			return extension;
		}
	}

	public class GinjectorSelectionExtension extends TypeSelectionExtension {

		@Override
		public ITypeInfoFilterExtension getFilterExtension() {
			ITypeInfoFilterExtension extension = new ITypeInfoFilterExtension() {
				@Override
				public boolean select(ITypeInfoRequestor requestor) {
					try {
						IType type = project.findType(requestor.getPackageName() + "." + requestor.getTypeName());
						if(type == null || !type.exists() || type.isBinary()) {
							return false;
						}
						ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
						IType[] interfaces = hierarchy.getAllInterfaces();
						for(IType inter : interfaces) {
							if(inter.getFullyQualifiedName('.').equals("com.google.gwt.inject.client.Ginjector")) {
								return true;
							}
						}
						return false;
					}
					catch (JavaModelException e) {
						return false;
					}
				}
			};

			return extension;
		}
	}

	public class PresenterModuleSelectionExtension extends TypeSelectionExtension {

		@Override
		public ITypeInfoFilterExtension getFilterExtension() {
			ITypeInfoFilterExtension extension = new ITypeInfoFilterExtension() {
				@Override
				public boolean select(ITypeInfoRequestor requestor) {
					try {
						IType type = project.findType(requestor.getPackageName() + "." + requestor.getTypeName());
						if(type == null || !type.exists() || type.isBinary()) {
							return false;
						}
						ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
						IType[] superclasses = hierarchy.getAllClasses();
						for(IType superclass : superclasses) {
							if(superclass.getFullyQualifiedName('.').equals("com.gwtplatform.mvp.client.gin.AbstractPresenterModule")) {
								return true;
							}
						}
						return false;
					}
					catch (JavaModelException e) {
						return false;
					}
				}
			};

			return extension;
		}
	}

	public class HandlerModuleSelectionExtension extends TypeSelectionExtension {

		@Override
		public ITypeInfoFilterExtension getFilterExtension() {
			ITypeInfoFilterExtension extension = new ITypeInfoFilterExtension() {
				@Override
				public boolean select(ITypeInfoRequestor requestor) {
					try {
						IType type = project.findType(requestor.getPackageName() + "." + requestor.getTypeName());
						if(type == null || !type.exists()) {
							return false;
						}
						ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
						IType[] superclasses = hierarchy.getAllClasses();
						for(IType superclass : superclasses) {
							if(superclass.getFullyQualifiedName('.').equals("com.gwtplatform.dispatch.server.guice.HandlerModule")) {
								return true;
							}
						}
						return false;
					}
					catch (JavaModelException e) {
						return false;
					}
				}
			};

			return extension;
		}
	}
}
