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

import de.p2tools.atplayer.controller.config.ProgColorList;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.mtdownload.GetProgramStandardPath;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.Collection;

public class PanePlay {

    private final Stage stage;
    StringProperty propProgram = ProgConfig.SYSTEM_PROG_PLAY;
    StringProperty propParameter = ProgConfig.SYSTEM_PROG_PLAY_PARAMETER;

    public PanePlay(Stage stage) {
        this.stage = stage;
    }

    public void close() {
    }

    public TitledPane make(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(P2LibConst.PADDING));
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow(), P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcPrefSize());

        int row = addVideoPlayer(gridPane, 0);
        TitledPane tpConfig = new TitledPane("Programme", gridPane);
        result.add(tpConfig);
        return tpConfig;
    }

    private int addVideoPlayer(GridPane gridPane, int row) {
        TextField txtPlay = new TextField();
        txtPlay.textProperty().bindBidirectional(propProgram);
        txtPlay.textProperty().addListener((l, o, n) -> {
            File file = new File(txtPlay.getText());
            if (!file.exists() || !file.isFile()) {
                txtPlay.setStyle(ProgColorList.ERROR.getCssBackground());
            } else {
                txtPlay.setStyle("");
            }
        });

        TextField txtParameter = new TextField();
        txtParameter.textProperty().bindBidirectional(propParameter);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.FileChooserOpenFile(ProgData.getInstance().primaryStage, txtPlay);
        });
        btnFile.setGraphic(ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Einen Videoplayer zum Abspielen der Beiträge auswählen"));

        final Button btnProgramReset = new Button();
        btnProgramReset.setGraphic(ProgIcons.ICON_BUTTON_RESET.getImageView());
        btnProgramReset.setTooltip(new Tooltip("Die Init-Parameter wieder herstellen"));
        btnProgramReset.setOnAction(event -> txtPlay.setText(GetProgramStandardPath.getTemplatePathVlc()));

        final Button btnParameterReset = new Button();
        btnParameterReset.setGraphic(ProgIcons.ICON_BUTTON_RESET.getImageView());
        btnParameterReset.setTooltip(new Tooltip("Die Init-Parameter wieder herstellen"));
        btnParameterReset.setOnAction(event -> txtParameter.setText(ProgConfig.SYSTEM_PROG_PLAY_PARAMETER_INIT));

        final Button btnHelpProgram = P2Button.helpButton(stage, "Videoplayer", HelpText.VIDEOPLAYER);
        final Button btnHelpParameter = P2Button.helpButton(stage, "Videoplayer", HelpText.PLAY_FILE_HELP_PARAMETER);

        gridPane.add(new Label("Videoplayer zum Abspielen der Audios"), 0, row, 2, 1);

        gridPane.add(new Label("Programm:"), 0, ++row);
        gridPane.add(txtPlay, 1, row);
        gridPane.add(btnProgramReset, 2, row);
        gridPane.add(btnFile, 3, row);
        gridPane.add(btnHelpProgram, 4, row);

        gridPane.add(new Label("Parameter:"), 0, ++row);
        gridPane.add(txtParameter, 1, row);
        gridPane.add(btnParameterReset, 2, row);
        gridPane.add(btnHelpParameter, 4, row);

        return row;
    }
}
