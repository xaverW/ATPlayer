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

package de.p2tools.atplayer.gui.configdialog.configpanes;

import de.p2tools.atplayer.controller.audio.LoadAudioFactory;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneAudioLoad {

    private final P2ToggleSwitch tglLoad = new P2ToggleSwitch("Audioliste beim Programmstart laden");
    private final P2ToggleSwitch tglRemoveDiacritic = new P2ToggleSwitch("Diakritische Zeichen ändern");
    private final BooleanProperty diacriticChanged;
    private final Stage stage;

    public PaneAudioLoad(Stage stage, BooleanProperty diacriticChanged) {
        this.stage = stage;
        this.diacriticChanged = diacriticChanged;
    }

    public void close() {
        tglLoad.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_LOAD_FILMS_ON_START);
        tglRemoveDiacritic.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_REMOVE_DIACRITICS);
    }

    public TitledPane make(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.DIST_EDGE));

        tglLoad.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_LOAD_FILMS_ON_START);
        final Button btnHelpLoad = P2Button.helpButton(stage, "Audioliste laden",
                HelpText.LOAD_FILMLIST_PROGRAMSTART);

        //Diacritic
        tglRemoveDiacritic.setMaxWidth(Double.MAX_VALUE);
        tglRemoveDiacritic.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_REMOVE_DIACRITICS);
        tglRemoveDiacritic.selectedProperty().addListener((u, o, n) -> diacriticChanged.setValue(true));
        final Button btnHelpDia = P2Button.helpButton(stage, "Diakritische Zeichen",
                HelpText.DIAKRITISCHE_ZEICHEN);

        Button btnLoad = new Button("_Audioliste mit dieser Einstellung neu laden");
        btnLoad.setTooltip(new Tooltip("Eine komplette neue Audioliste laden.\n" +
                "Geänderte Einstellungen für das Laden der Audioliste werden so sofort übernommen"));
        btnLoad.setOnAction(event -> LoadAudioFactory.getInstance().loadListButton());

        int row = 0;
        gridPane.add(tglLoad, 0, row);
        gridPane.add(btnHelpLoad, 1, row);

        ++row;
        gridPane.add(tglRemoveDiacritic, 0, ++row);
        gridPane.add(btnHelpDia, 1, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(btnLoad, 0, ++row, 2, 1);
        GridPane.setHalignment(btnLoad, HPos.RIGHT);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow(),
                P2ColumnConstraints.getCcPrefSize()
        );

        TitledPane tpConfig = new TitledPane("Audioliste laden", gridPane);
        result.add(tpConfig);
        return tpConfig;
    }
}