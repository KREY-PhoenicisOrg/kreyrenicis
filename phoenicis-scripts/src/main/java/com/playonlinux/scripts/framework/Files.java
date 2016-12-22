/*
 * Copyright (C) 2015 PÂRIS Quentin
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.playonlinux.scripts.framework;

import com.playonlinux.scripts.wizard.SetupWizard;
import com.playonlinux.scripts.interpreter.CancelException;
import com.playonlinux.scripts.interpreter.ScriptException;
import com.playonlinux.scripts.ui.ProgressControl;

import java.io.*;

import static com.playonlinux.configuration.localisation.Localisation.translate;


/* A builder pattern could be used here but we chose not to use it to facilitate com.playonlinux.core.scripts.sh syntax
 */

public class Files {
    private SetupWizard setupWizard;
    private ProgressControl progressControl;

    private static final int BLOCK_SIZE = 1024;

    /**
     * Create a downloader object that is not hooked to any progress bar
     */
    public Files() {

    }

    public Files(ProgressControl progressControl) {
        this.progressControl = progressControl;
    }

    private Files(SetupWizard setupWizard) {
        this.setupWizard = setupWizard;
    }

    public static Files wizard(SetupWizard setupWizard) {
        return new Files(setupWizard);
    }

    private void defineProgressStep(File sourceFile) {
        if (this.progressControl == null) {
            this.progressControl = this.setupWizard.progressBar(
                    translate("Please wait while ${application.name} is copying:") + "\n" + sourceFile.getName());
        }
    }

    private void copyFile(File sourceFile, File destinationFile) throws IOException {
        int fileSize = (int) sourceFile.length();
        float totalDataRead = 0.0F;

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(sourceFile));
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destinationFile),
                        BLOCK_SIZE)) {
            byte[] data = new byte[BLOCK_SIZE];
            int i;
            while ((i = inputStream.read(data, 0, BLOCK_SIZE)) >= 0) {
                totalDataRead += i;
                outputStream.write(data, 0, i);
                if (progressControl != null) {
                    int percentCopied = (int) (totalDataRead * 100 / fileSize);
                    progressControl.setProgressPercentage(percentCopied);
                }

                if (Thread.interrupted()) {
                    throw new CancelException("The copy process was interrupted");
                }
            }
            inputStream.close();
            outputStream.close();
        }
    }

    public Files copy(String sourceFilePath, String destinationFilePath) throws CancelException {
        File sourceFile = new File(sourceFilePath);
        File destinationFile = new File(destinationFilePath);

        this.defineProgressStep(sourceFile);

        try {
            this.copyFile(sourceFile, destinationFile);
        } catch (IOException e) {
            throw new ScriptException(e);
        }
        return this;
    }

    public Files mkdir(String directoryToCreate) {
        try {
            java.nio.file.Files.createDirectories(new File(directoryToCreate).toPath());
        } catch (IOException e) {
            throw new ScriptException(
                    String.format("Unable to createPrefix the directory %s", directoryToCreate), e);
        }

        return this;
    }

    public Files remove(String pathToDelete) {
        /* try {
            com.playonlinux.core.utils.Files.remove(new File(pathToDelete));
        } catch (IOException e) {
            throw new ScriptException("Unable to deletePrefix the file", e);
        }*/

        return this;
    }

}