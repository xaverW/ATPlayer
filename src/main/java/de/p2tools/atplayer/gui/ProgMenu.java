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

package de.p2tools.atplayer.gui;

import de.p2tools.atplayer.controller.ProgQuit;
import de.p2tools.atplayer.controller.config.*;
import de.p2tools.atplayer.controller.update.SearchProgramUpdate;
import de.p2tools.atplayer.gui.configdialog.ConfigDialogController;
import de.p2tools.atplayer.gui.dialog.AboutDialogController;
import de.p2tools.atplayer.gui.dialog.ResetDialogController;
import de.p2tools.atplayer.gui.tools.TipOfDayFactory;
import de.p2tools.p2lib.guitools.P2Open;
import de.p2tools.p2lib.tools.shortcut.P2ShortcutWorker;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;


public class ProgMenu extends MenuButton {

    public ProgMenu() {
        makeMenue();
    }

    private void makeMenue() {
        ProgData progData = ProgData.getInstance();

        setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                ProgConfig.SYSTEM_DARK_THEME.setValue(!ProgConfig.SYSTEM_DARK_THEME.getValue());
            }
        });

        setTooltip(new Tooltip("Filmmenü anzeigen"));
        setGraphic(ProgIcons.FX_ICON_TOOLBAR_MENU.getImageView());
        getStyleClass().addAll("btnFunction", "btnFunc-1");

        //=========================
        // Info, Einstellungen
        final CheckMenuItem miDarkMode = new CheckMenuItem("Dark Mode");
        miDarkMode.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_DARK_THEME);

        final MenuItem miConfig = new MenuItem("Einstellungen");
        miConfig.setOnAction(e -> new ConfigDialogController(ProgData.getInstance()).showDialog());
        getItems().addAll(miDarkMode, miConfig, new SeparatorMenuItem());

        //=========================
        //Hilfe
        final MenuItem miUrlHelp = new MenuItem("Anleitung im Web");
        miUrlHelp.setOnAction(event -> {
            P2Open.openURL(ProgConst.URL_WEBSITE_HELP,
                    ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
        });
        final MenuItem miReset = new MenuItem("Alle Programmeinstellungen zurücksetzen");
        miReset.setOnAction(event -> new ResetDialogController(progData));
        final MenuItem miToolTip = new MenuItem("Tip des Tages");
        miToolTip.setOnAction(a -> TipOfDayFactory.showDialog(progData, true));
        final MenuItem miSearchUpdate = new MenuItem("Gibt's ein Update?");
        miSearchUpdate.setOnAction(a -> new SearchProgramUpdate(progData, progData.primaryStage).searchNewProgramVersion(true));
        final MenuItem miAbout = new MenuItem("Über dieses Programm");
        miAbout.setOnAction(event -> new AboutDialogController(ProgData.getInstance()).showDialog());

        final Menu mHelp = new Menu("Hilfe");
        mHelp.getItems().addAll(miUrlHelp, miReset, miToolTip, miSearchUpdate, new SeparatorMenuItem(), miAbout);
        getItems().addAll(mHelp);

        //=========================
        //Quitt
        final MenuItem miQuit = new MenuItem("Beenden");
        miQuit.setOnAction(e -> ProgQuit.quit(false));
        P2ShortcutWorker.addShortCut(miQuit, PShortcut.SHORTCUT_QUIT_PROGRAM);

        getItems().addAll(miQuit);
    }
}
