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

package com.arcbees.gwtp.plugin.core.util.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipTemplateIterator implements Iterable<TemplateZipItem>, Iterator<TemplateZipItem> {

    private ZipInputStream zipInputStream;

    private boolean hasNextCalled;

    private ZipEntry nextEntry;

    private String zipFilePath;

    public ZipTemplateIterator(final String zipFilePath) {
        this.zipFilePath = zipFilePath;
    }

    public void closeCurrentStream() {
        if (zipInputStream != null) {
            try {
                zipInputStream.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean hasNext() {
        if (!hasNextCalled) {
            try {
                if (nextEntry != null) {
                    zipInputStream.closeEntry();
                }
                nextEntry = zipInputStream.getNextEntry();
                hasNextCalled = true;
                return nextEntry != null;
            } catch (final IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public Iterator<TemplateZipItem> iterator() {
        closeCurrentStream();
        final InputStream inputStream = getClass().getResourceAsStream(zipFilePath);
        this.zipInputStream = new ZipInputStream(inputStream);
        return this;
    }

    @Override
    public TemplateZipItem next() {
        if (hasNext()) {
            hasNextCalled = false;
            String name = nextEntry.getName();
            if (name.isEmpty()) {
                return next();
            }

            if (name.endsWith(".template")) {
                name = name.substring(0, name.length() - ".template".length());
                return new TemplateZipItem(name, readZipEntryContents());
            } else {
                return new TemplateZipItem(name, null);
            }
        } else {
            return null;
        }
    }

    @Override
    public void remove() {
        // noop
    }

    @Override
    protected void finalize() throws Throwable {
        closeCurrentStream();
        super.finalize();
    }

    private String readZipEntryContents() {
        @SuppressWarnings("resource")
        final Scanner scanner = new Scanner(zipInputStream);
        final StringBuilder sb = new StringBuilder();
        while (scanner.hasNext()) {
            sb.append(scanner.nextLine()).append("\n");
        }
        return sb.toString();
    }
}
