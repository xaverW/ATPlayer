/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package de.p2tools.atplayer.gui.configdialog;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.gui.configdialog.downloadpanes.PaneDestination;
import de.p2tools.atplayer.gui.configdialog.downloadpanes.PaneDownload;
import de.p2tools.atplayer.gui.configdialog.downloadpanes.PaneReplace;
import de.p2tools.p2lib.dialogs.accordion.PAccordionPane;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class ControllerDownload extends PAccordionPane {
    private final Stage stage;
    private PaneDownload paneDownload;
    private PaneDestination paneDestination;
    private PaneReplace paneReplace;

    public ControllerDownload(Stage stage) {
        super(ProgConfig.CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_CONFIG_DIALOG_DOWNLOAD);
        this.stage = stage;
        init();
    }

    @Override
    public void close() {
        super.close();
        paneDownload.close();
        paneDestination.close();
        paneReplace.close();
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> titledPanes = new ArrayList<>();
        paneDownload = new PaneDownload(stage);
        paneDownload.makePane(titledPanes);
        paneDestination = new PaneDestination(stage);
        paneDestination.makePane(titledPanes);
        paneReplace = new PaneReplace(stage);
        paneReplace.makePane(titledPanes);
        return titledPanes;
    }
}