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

public class ActionWizardPage extends WizardPage {

	private IStructuredSelection selection;
	private Text actionPackage;
	private Text actionText;
	private Text addActionParameter;
	private Text addResultParameter;
	private List actionParameterList;
	private List resultParameterList;
	private Button isSecureCheckBox;
	private Button addActionButton;
	private Button addResultButton;

	public ActionWizardPage(IStructuredSelection selection) {
		super("ActionWizardPage");
		setTitle("Create an Action");
		setDescription("Create an Action and related classes");
		this.selection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 5;

		// Action Package
		Label label = new Label(container, SWT.NULL);
		label.setText("Action package:");

		actionPackage = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		actionPackage.setLayoutData(gd);
		actionPackage.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);

		// Action Name
		label = new Label(container, SWT.NULL);
		label.setText("Action name:");

		actionText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		actionText.setLayoutData(gd);
		actionText.setFocus();
		actionText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);

		// Use Secure Action
		label = new Label(container, SWT.NULL);

		isSecureCheckBox = new Button(container, SWT.CHECK);
		isSecureCheckBox.setText("Secure Action");
		isSecureCheckBox.setSelection(false);

		label = new Label(container, SWT.NULL);

		// Add Parameter
		label = new Label(container, SWT.NULL);
		label.setText("Action Parameters:");

		addActionParameter = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		addActionParameter.setLayoutData(gd);
		addActionParameter.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		addActionButton = new Button(container, SWT.PUSH);
		addActionButton.setText("+");
		addActionButton.setEnabled(false);
		addActionButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean isInList = false;
				for(String item : actionParameterList.getItems()) {
					if(item.equals(addActionParameter.getText())) {
						isInList = true;
						break;
					}
				}
				if(!isInList) {
					actionParameterList.add(addActionParameter.getText());
				}
				addActionParameter.setText("");
				addActionParameter.setFocus();
			}
		});

		// Parameters List
		label = new Label(container, SWT.NULL);

		actionParameterList = new List(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		actionParameterList.setLayoutData(gd);

		Button suppButton = new Button(container, SWT.PUSH);
		suppButton.setText("-");
		suppButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(actionParameterList.getItemCount() > 0) {
					if(actionParameterList.getSelectionIndex() != -1) {
						actionParameterList.remove(actionParameterList.getSelectionIndex());
					}
				}
			}
		});

		// Add Parameter
		label = new Label(container, SWT.NULL);
		label.setText("Result Parameters:");

		addResultParameter = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		addResultParameter.setLayoutData(gd);
		addResultParameter.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		addResultButton = new Button(container, SWT.PUSH);
		addResultButton.setText("+");
		addResultButton.setEnabled(false);
		addResultButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean isInList = false;
				for(String item : resultParameterList.getItems()) {
					if(item.equals(addResultParameter.getText())) {
						isInList = true;
						break;
					}
				}
				if(!isInList) {
					resultParameterList.add(addResultParameter.getText());
				}
				addResultParameter.setText("");
				addResultParameter.setFocus();
			}
		});

		// Parameters List
		label = new Label(container, SWT.NULL);

		resultParameterList = new List(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		resultParameterList.setLayoutData(gd);

		suppButton = new Button(container, SWT.PUSH);
		suppButton.setText("-");
		suppButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(resultParameterList.getItemCount() > 0) {
					if(resultParameterList.getSelectionIndex() != -1) {
						resultParameterList.remove(resultParameterList.getSelectionIndex());
					}
				}
			}
		});

		initialize();
		dialogChanged();
		setControl(container);
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
			actionPackage.setText(pack);
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */
	private void dialogChanged() {
		if(actionPackage.getText().isEmpty()) {
			setMessage("Enter the action package");
			return;
		}
		if(actionText.getText().isEmpty()) {
			setMessage("Enter a name for the Action");
			return;
		}
		if(!actionText.getText().endsWith("Action")) {
			setErrorMessage("Action File must ends by \"Action\"");
			return;
		}

		if(!addActionParameter.getText().isEmpty()) {
			String[] parameter = addActionParameter.getText().split(" ");

			if(parameter.length != 2) {
				addActionButton.setEnabled(false);
				setErrorMessage("Action parameter must be valid");
				return;
			}
			addActionButton.setEnabled(true);
		}
		else
			addActionButton.setEnabled(false);

		if(!addResultParameter.getText().isEmpty()) {
			String[] parameter = addResultParameter.getText().split(" ");

			if(parameter.length != 2) {
				addResultButton.setEnabled(false);
				setErrorMessage("Result parameter must be valid");
				return;
			}
			addResultButton.setEnabled(true);
		}
		else
			addResultButton.setEnabled(false);
		
		setPageComplete(true);
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

	public String getActionName() {
		return actionText.getText().replace("Action", "");
	}

	public String getActionPackage() {
		return actionPackage.getText();
	}

	public boolean isSecureAction() {
		return isSecureCheckBox.getSelection();
	}

	public String[] getActionParameters() {
		return actionParameterList.getItems();
	}

	public String[] getResultParameters() {
		return resultParameterList.getItems();
	}

}
