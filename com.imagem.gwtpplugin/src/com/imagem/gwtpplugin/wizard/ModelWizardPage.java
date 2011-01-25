package com.imagem.gwtpplugin.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public class ModelWizardPage extends WizardPage {

	private IStructuredSelection selection;
	private Text modelPackage;
	private Text modelName;
	private Text addVariable;
	private Button addButton;
	private List variableList;

	protected ModelWizardPage(IStructuredSelection selection) {
		super("ModelWizardPage");
		setTitle("Create a Model");
		setDescription("Create a Model that hold data");
		this.selection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 5;

		// Model Package
		Label label = new Label(container, SWT.NULL);
		label.setText("Model package:");

		modelPackage = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		modelPackage.setLayoutData(gd);
		modelPackage.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);

		// Model Name
		label = new Label(container, SWT.NULL);
		label.setText("Model name:");

		modelName = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		modelName.setLayoutData(gd);
		modelName.setFocus();
		modelName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);

		// Add Variable
		label = new Label(container, SWT.NULL);
		label.setText("Variables:");

		addVariable = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		addVariable.setLayoutData(gd);
		addVariable.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		addButton = new Button(container, SWT.PUSH);
		addButton.setText("+");
		addButton.setEnabled(false);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean isInList = false;
				for(String item : variableList.getItems()) {
					if(item.equals(addVariable.getText())) {
						isInList = true;
						break;
					}
				}
				if(!isInList) {
					variableList.add(addVariable.getText());
				}
				addVariable.setText("");
				addVariable.setFocus();
			}
		});

		// Variables List
		label = new Label(container, SWT.NULL);

		variableList = new List(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		variableList.setLayoutData(gd);

		Button suppButton = new Button(container, SWT.PUSH);
		suppButton.setText("-");
		suppButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(variableList.getItemCount() > 0) {
					if(variableList.getSelectionIndex() != -1) {
						variableList.remove(variableList.getSelectionIndex());
					}
				}
			}
		});


		initialize();
		dialogChanged();
		setControl(container);
	}

	private void dialogChanged() {
		if(modelPackage.getText().isEmpty()) {
			setMessage("Enter the model package");
			return;
		}

		if(modelName.getText().isEmpty()) {
			setMessage("Enter a name for the Model");
			return;
		}
		
		if(!addVariable.getText().isEmpty()) {
			String[] parameter = addVariable.getText().split(" ");

			if(parameter.length != 2) {
				addButton.setEnabled(false);
				setErrorMessage("Variable must be valid");
				return;
			}
			addButton.setEnabled(true);
		}
		else
			addButton.setEnabled(false);

		setPageComplete(true);
	}

	private void initialize() {
		if(selection != null && !selection.isEmpty()) {
			if (selection.size() > 1)
				return;
			Object firstElement = selection.getFirstElement();
			IResource resource = null;
			if (firstElement instanceof IResource) {
				// Is it a IResource ?
				resource = (IResource) firstElement;
			}
			else if (firstElement instanceof IAdaptable) {
				// Is it a IResource adaptable ?
				IAdaptable adaptable = (IAdaptable) firstElement;
				resource = (IResource) adaptable.getAdapter(IResource.class);
			}

			IContainer container = null;
			if(resource != null) {
				if(resource instanceof IContainer)
					container = (IContainer) resource;
				else
					container = resource.getParent();
			}

			String pack = "";
			for(int i = 1; i < container.getProjectRelativePath().segmentCount(); i++) {
				if(!pack.isEmpty())
					pack += ".";
				pack += container.getProjectRelativePath().segment(i);
			}
			modelPackage.setText(pack);
		}
	}
	
	public void setMessage(String newMessage) {
		super.setMessage(newMessage);
		super.setErrorMessage(null);
		super.setPageComplete(false);
	}

	public void setErrorMessage(String newMessage) {
		super.setMessage(null);
		super.setErrorMessage(newMessage);
		super.setPageComplete(false);
	}

	public void setPageComplete(boolean complete) {
		if(complete) {
			super.setMessage(null);
			super.setErrorMessage(null);
		}
		super.setPageComplete(complete);
	}
	
	public String getModelPackage() {
		return modelPackage.getText();
	}
	
	public String getModelName() {
		return modelName.getText();
	}

	public String[] getVariables() {
		if(!addVariable.getText().isEmpty()) {
			variableList.add(addVariable.getText());
			addVariable.setText("");
		}
		return variableList.getItems();
	}
}
