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
import de.p2tools.atplayer.controller.ProgSave;
import de.p2tools.atplayer.controller.config.PShortcut;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgConst;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.picon.PIconFactory;
import de.p2tools.atplayer.controller.update.SearchProgramUpdate;
import de.p2tools.atplayer.gui.configdialog.ConfigDialogController;
import de.p2tools.atplayer.gui.dialog.AboutDialogController;
import de.p2tools.atplayer.gui.dialog.ResetDialogController;
import de.p2tools.atplayer.tips.TipsDialog;
import de.p2tools.p2lib.guitools.P2Open;
import de.p2tools.p2lib.tools.shortcut.P2ShortcutWorker;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import org.kordamp.ikonli.javafx.FontIcon;


public class ProgMenu extends MenuButton {

    public ProgMenu() {
        makeMenue();
    }

    private void makeMenue() {
        ProgData progData = ProgData.getInstance();

        setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() > 1) {
                    ProgConfig.SYSTEM_GUI_THEME_1.set(!ProgConfig.SYSTEM_GUI_THEME_1.get());
                }
            }

            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                ProgConfig.SYSTEM_DARK_THEME.set(!ProgConfig.SYSTEM_DARK_THEME.get());
            }
        });

        setTooltip(new Tooltip("Programmmenü anzeigen"));
        setText("");
        getStyleClass().addAll("pFuncBtn", "btnProgMenu");
        FontIcon node = PIconFactory.PICON.PROG_MENU.getFontIcon();
        node.setScaleX(1.5);
        setGraphic(node);

        //=========================
        // Info, Einstellungen
        final MenuItem miConfig = new MenuItem("Einstellungen");
        miConfig.setOnAction(e -> new ConfigDialogController(ProgData.getInstance()));
        miConfig.disableProperty().bind(ConfigDialogController.dialogIsRunning);

        final CheckMenuItem miDarkMode = new CheckMenuItem("Dunkle Oberfläche");
        miDarkMode.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_DARK_THEME);

        final CheckMenuItem miColorMode = new CheckMenuItem("Farb-Modus-1");
        miColorMode.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_GUI_THEME_1);

        getItems().addAll(miConfig, miDarkMode, miColorMode, new SeparatorMenuItem());
        addMenuButton();

        //=========================
        final MenuItem miTipps = new MenuItem("Hilfedialog");
        miTipps.setOnAction(a -> new TipsDialog(progData));

        //Hilfe
        final MenuItem miUrlHelp = new MenuItem("Anleitung im Web");
        miUrlHelp.setOnAction(event -> {
            P2Open.openURL(ProgConst.URL_WEBSITE_HELP,
                    ProgConfig.SYSTEM_PROG_OPEN_URL, PIconFactory.PICON.BTN_DIR_OPEN.getFontIcon());
        });
        final MenuItem miReset = new MenuItem("Alle Programmeinstellungen zurücksetzen");
        miReset.setOnAction(event -> new ResetDialogController(progData));
        final MenuItem miSearchUpdate = new MenuItem("Gibt's ein Update?");
        miSearchUpdate.setOnAction(a -> new SearchProgramUpdate().searchNewProgramVersion(true));
        final MenuItem miAbout = new MenuItem("Über dieses Programm");
        miAbout.setOnAction(event -> new AboutDialogController(ProgData.getInstance()).showDialog());

        final Menu mHelp = new Menu("Hilfe");
        mHelp.getItems().addAll(miTipps, new SeparatorMenuItem(), miUrlHelp,
                miReset, miSearchUpdate, new SeparatorMenuItem(), miAbout);
        getItems().addAll(mHelp);

        if (ProgData.debug) {
            final MenuItem miSearchAllUpdate = new MenuItem("Alle Programm-Downloads anzeigen");
            miSearchAllUpdate.setOnAction(a -> new SearchProgramUpdate()
                    .searchNewProgramVersion());

            final MenuItem miResetTodayDone = new MenuItem("<SYSTEM_SEARCH_UPDATE_TODAY_DONE> zurücksetzen");
            miResetTodayDone.setOnAction(a -> {
                ProgConfig.SYSTEM_SEARCH_UPDATE_TODAY_DONE.set("2020.01.01"); // heute noch nicht gemacht
            });
            final MenuItem miResetLastSearch = new MenuItem("<SYSTEM_SEARCH_UPDATE_LAST_DATE> zurücksetzen");
            miResetLastSearch.setOnAction(a -> {
                ProgConfig.SYSTEM_SEARCH_UPDATE_LAST_DATE.set("2020.01.01"); // letztes Datum, bis zu dem geprüft wurde, wenn leer wird das buildDate genommen
            });
            final MenuItem miResetUpdate = new MenuItem("<SYSTEM_SEARCH_UPDATE_TODAY_DONE und \n" +
                    "SYSTEM_SEARCH_UPDATE_LAST_DATE> zurücksetzen");

            miResetUpdate.setOnAction(a -> {
                ProgConfig.SYSTEM_SEARCH_UPDATE_TODAY_DONE.set("2020.01.01"); // heute noch nicht gemacht
                ProgConfig.SYSTEM_SEARCH_UPDATE_LAST_DATE.set("2020.01.01"); // letztes Datum, bis zu dem geprüft wurde, wenn leer wird das buildDate genommen
            });

            final MenuItem miSave = new MenuItem("Alles Speichern");
            miSave.setOnAction(a -> ProgSave.saveAll());

            mHelp.getItems().addAll(new SeparatorMenuItem(), miSearchAllUpdate,
                    miResetTodayDone, miResetLastSearch, miResetUpdate, miSave);
        }


        //=========================
        //Quitt
        final MenuItem miQuit = new MenuItem("Beenden");
        miQuit.setOnAction(e -> ProgQuit.quit(false));
        P2ShortcutWorker.addShortCut(miQuit, PShortcut.SHORTCUT_QUIT_PROGRAM);

        getItems().addAll(miQuit);
    }

    private void addMenuButton() {
        final CheckMenuItem miAudio = new CheckMenuItem("Rechte Menüleiste");
        miAudio.visibleProperty().bind(ProgData.AUDIO_TAB_ON);
        miAudio.selectedProperty().bindBidirectional(ProgConfig.AUDIO_GUI_SHOW_MENU);

        final CheckMenuItem miDownload = new CheckMenuItem("Rechte Menüleiste");
        miDownload.visibleProperty().bind(ProgData.DOWNLOAD_TAB_ON);
        miDownload.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_GUI_SHOW_MENU);

        getItems().addAll(miAudio, miDownload);
    }
}
