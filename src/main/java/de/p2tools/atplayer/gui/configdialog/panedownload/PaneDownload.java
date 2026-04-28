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

package de.p2tools.atplayer.gui.configdialog.panedownload;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collection;

public class PaneDownload {

    final GridPane gridPane = new GridPane();
    private final P2ToggleSwitch tglFinished = new P2ToggleSwitch("Benachrichtigung wenn abgeschlossen");
    private final P2ToggleSwitch tglError = new P2ToggleSwitch("Bei Downloadfehler Fehlermeldung anzeigen");
    private final P2ToggleSwitch tglSSL = new P2ToggleSwitch("SSL-Download-URLs: Bei Problemen SSL abschalten");
    private final Stage stage;

    public PaneDownload(Stage stage) {
        this.stage = stage;

        make();
    }

    public void makePane(Collection<TitledPane> titledPanes) {
        TitledPane tpConfig = new TitledPane("Download", gridPane);
        titledPanes.add(tpConfig);
    }

    public void close() {
        tglFinished.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_SHOW_NOTIFICATION);
        tglError.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ERROR_SHOW);
        tglSSL.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SSL_ALWAYS_TRUE);
    }

    private void make() {
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));

        tglFinished.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_SHOW_NOTIFICATION);
        final Button btnHelpFinished = P2Button.helpButton(stage, "Download",
                HelpText.DOWNLOAD_FINISHED);

        tglError.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ERROR_SHOW);
        final Button btnHelpError = P2Button.helpButton(stage, "Download",
                HelpText.DOWNLOAD_ERROR);

        tglSSL.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SSL_ALWAYS_TRUE);
        final Button btnHelpSSL = P2Button.helpButton(stage, "Download",
                HelpText.DOWNLOAD_SSL_ALWAYS_TRUE);

        GridPane.setHalignment(btnHelpFinished, HPos.RIGHT);
        GridPane.setHalignment(btnHelpSSL, HPos.RIGHT);

        int row = 0;
        gridPane.add(tglFinished, 0, row);
        gridPane.add(btnHelpFinished, 1, row);

        gridPane.add(tglError, 0, ++row);
        gridPane.add(btnHelpError, 1, row);

        ++row;
        gridPane.add(tglSSL, 0, ++row);
        gridPane.add(btnHelpSSL, 1, row);

        gridPane.getColumnConstraints().addAll(de.p2tools.p2lib.guitools.grid.P2GridConstraints.getCcComputedSizeAndHgrow(),
                de.p2tools.p2lib.guitools.grid.P2GridConstraints.getCcPrefSize());
    }

}