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

        RestrictionSelector(final EventRestriction restriction) {
            this.restriction = restriction;
        }

        @Override
        public void widgetSelected(final SelectionEvent e) {
            eventRestriction = this.restriction;
        }
    }

    private EventRestriction eventRestriction = EventRestriction.SINGLE_FIRE;

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
    }

    @Override
    protected String getNameSuffix() {
        return "Event";
    }

    private void createRestrictionControls(final Composite composite) {
        final Group group = createGroup(composite, "Event Restrictions", 1);
        final Button singleFireRadio = createButton(group, "Only presenters in the same package can fire this event.",
                SWT.RADIO);
        singleFireRadio.setSelection(true);
        singleFireRadio.addSelectionListener(new RestrictionSelector(EventRestriction.SINGLE_FIRE));
        createButton(group, "Only presenters in the same package can catch this event", SWT.RADIO)
        .addSelectionListener(new RestrictionSelector(EventRestriction.SINGE_CATCH));
        createButton(group, "All presenters can fire and catch this event", SWT.RADIO).addSelectionListener(
                new RestrictionSelector(EventRestriction.NONE));
    }
}
