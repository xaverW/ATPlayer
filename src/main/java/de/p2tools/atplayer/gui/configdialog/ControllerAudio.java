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
import de.p2tools.atplayer.gui.configdialog.configpanes.PaneAudioFilter;
import de.p2tools.atplayer.gui.configdialog.configpanes.PaneAudioLoad;
import de.p2tools.p2lib.dialogs.accordion.P2AccordionPane;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class ControllerAudio extends P2AccordionPane {

    private final ProgData progData;
    private final Stage stage;
    private PaneAudioLoad paneAudioLoad;
    private PaneAudioFilter paneAudioFilter;
    private final BooleanProperty diacriticChanged;

    public ControllerAudio(Stage stage, BooleanProperty diacriticChanged) {
        super(ProgConfig.CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_CONFIG_DIALOG_FILM);
        this.stage = stage;
        this.diacriticChanged = diacriticChanged;
        progData = ProgData.getInstance();

        init();
    }

    @Override
    public void close() {
        super.close();
        paneAudioLoad.close();
        paneAudioFilter.close();
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        paneAudioLoad = new PaneAudioLoad(stage, diacriticChanged);
        paneAudioLoad.make(result);
        paneAudioFilter = new PaneAudioFilter(stage, progData);
        paneAudioFilter.make(result);
        return result;
    }
}