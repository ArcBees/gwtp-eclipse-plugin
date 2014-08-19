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

package com.arcbees.gwtp.plugin.core.event;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.arcbees.gwtp.plugin.core.StupidVelocityShim;
import com.arcbees.gwtp.plugin.core.common.GWTPNewTypeWizardPage;

public class CreateEventPage extends GWTPNewTypeWizardPage {
    private enum EventRestriction {
        NONE,
        SINGLE_FIRE,
        SINGE_CATCH;
    }

    private class RestrictionSelector extends SelectionAdapter {
        private final EventRestriction restriction;
        private final Button button;

        RestrictionSelector(final EventRestriction restriction, final Button button) {
            this.restriction = restriction;
            this.button = button;
        }

        @Override
        public void widgetSelected(final SelectionEvent e) {
            if (button.getSelection()) {
                eventRestriction = this.restriction;
            }
        }
    }

    private EventRestriction eventRestriction = EventRestriction.SINGLE_FIRE;
    private boolean hasHandlers;

    public CreateEventPage() {
        super("wizardPageEvent", "Create Event", "Create an event for the project.");
    }

    @Override
    public void createType(final IProgressMonitor monitor) throws CoreException, InterruptedException {
        ensurePackageExists(monitor);
        StupidVelocityShim.setStripUnknownKeys(true);

        final Map<String, Object> context = new HashMap<>();
        context.put("eventName", getTypeName());
        context.put("packageName", getPackageText());
        context.put("handlerModifier", eventRestriction == EventRestriction.SINGE_CATCH ? "" : "public ");
        context.put("eventModifier", eventRestriction == EventRestriction.SINGLE_FIRE ? "" : "public ");
        if (hasHandlers) {
            context.put("hasHandlers", true);
        }

        final InputStream is = getClass().getResourceAsStream("/src/main/resources/templates/event.java.template");
        try (Scanner s = new Scanner(is)) {
            s.useDelimiter("\\A");
            final String template = s.hasNext() ? s.next() : "";

            final IProject project = getJavaProject().getProject();

            final String output = StupidVelocityShim.evaluate(template, context);

            final IFile file = project.getFile(getPackageFragment().getResource().getProjectRelativePath()
                    .append(new Path(getTypeName() + "Event.java")));
            file.create(new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8)), IResource.NONE, null);
        }
    }

    @Override
    protected void extendControl(final Composite composite) {
        createRestrictionControls(composite);
        createHasHandlersControls(composite);
    }

    @Override
    protected String getNameSuffix() {
        return "Event";
    }

    private void createHasHandlersControls(final Composite composite) {
        final Group group = createGroup(composite, "HasHandlers", 1);
        final Button hasHandlersButton = createButton(group, "Add HasHandlers Interface", SWT.CHECK);
        hasHandlersButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                hasHandlers = hasHandlersButton.getSelection();
            }
        });
    }

    private void createRestrictionControls(final Composite composite) {
        final Group group = createGroup(composite, "Event Restrictions", 1);
        final Button singleFireRadio = createButton(group, "Only presenters in the same package can fire this event.",
                SWT.RADIO);
        singleFireRadio.setSelection(true);
        singleFireRadio.addSelectionListener(new RestrictionSelector(EventRestriction.SINGLE_FIRE, singleFireRadio));

        final Button singleCatchRadio = createButton(group, "Only presenters in the same package can catch this event",
                SWT.RADIO);
        singleCatchRadio.addSelectionListener(new RestrictionSelector(EventRestriction.SINGE_CATCH, singleCatchRadio));
        final Button noneRadio = createButton(group, "All presenters can fire and catch this event", SWT.RADIO);
        noneRadio.addSelectionListener(new RestrictionSelector(EventRestriction.NONE, noneRadio));
    }
}
