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

import de.p2tools.atplayer.controller.audio.LoadAudioFactory;
import de.p2tools.atplayer.controller.config.PListener;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class ConfigDialogController extends P2DialogExtra {

    private final ProgData progData;
    IntegerProperty propSelectedTab = ProgConfig.SYSTEM_CONFIG_DIALOG_TAB;
    private ControllerConfig controllerConfig;
    private ControllerPlay controllerPlay;
    private ControllerAudio controllerAudio;
    private ControllerBlackList controllerBlackList;
    private ControllerDownload controllerDownload;
    private final BooleanProperty blackChanged = new SimpleBooleanProperty(false);

    private final TabPane tabPane = new TabPane();
    private final Button btnOk = new Button("_Ok");
    private final BooleanProperty diacriticChanged = new SimpleBooleanProperty(false);
    private boolean blackListDialog = false;
    private final Button btnApply = new Button("_Anwenden");
    public static BooleanProperty dialogIsRunning = new SimpleBooleanProperty(false);

    public ConfigDialogController(ProgData progData) {
        super(progData.primaryStage, ProgConfig.CONFIG_DIALOG_SIZE, "Einstellungen",
                true, false, DECO.NO_BORDER, true);

        this.progData = progData;
        dialogIsRunning.setValue(true);
        btnApply.setVisible(false);
        init(true);
    }

    public ConfigDialogController(ProgData progData, boolean blackListDialog) {
        super(progData.primaryStage, ProgConfig.CONFIG_DIALOG_SIZE, "Einstellungen",
                true, false, DECO.NO_BORDER, true);

        this.progData = progData;
        this.blackListDialog = blackListDialog;
        dialogIsRunning.setValue(true);
        if (blackListDialog) {
            propSelectedTab = ProgConfig.SYSTEM_CONFIG_DIALOG_BLACKLIST_TAB;
        } else {
            btnApply.setVisible(false);
        }
        init(true);
    }

    @Override
    public void make() {
        getMaskerPane().visibleProperty().bind(progData.maskerPane.visibleProperty());

        VBox.setVgrow(tabPane, Priority.ALWAYS);
        getVBoxCont().getChildren().add(tabPane);
        getVBoxCont().setPadding(new Insets(0));

        if (btnApply.isVisible()) {
            // nur dann einfügen
            addOkCancelApplyButtons(btnOk, null, btnApply);
            btnApply.setOnAction(a -> onlyApply());

        } else {
            addOkButton(btnOk);
        }
        btnOk.setOnAction(a -> close());

//        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> updateCss());
        initPanel();
    }

    private void onlyApply() {
        if (!LoadAudioFactory.getInstance().loadAudioList.getPropLoadAudiolist()) {
            //dann wird die Blacklist immer neu gemacht, sonst wirds dann eh gemacht
            BlacklistFilterFactory.markBlack(true);
            blackChanged.setValue(false);
        }
    }

    @Override
    public void close() {
        if (blackChanged.get()) {
            // sonst hat sich nichts geändert oder wird dann eh gemacht
            BlacklistFilterFactory.markBlack(true);
        }

        if (diacriticChanged.getValue() && ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue()) {
            //hat sich geändert UND ist eingeschaltet
            //Diakritika entfernen, macht nur dann Sinn
            //zum Einfügen der Diakritika muss eine neue Audioliste geladen werden
            new Thread(() -> {
                ProgData.getInstance().maskerPane.setMaskerText("Diakritika entfernen");
                ProgData.getInstance().maskerPane.setMaskerVisible();
//                FilmFactory.flattenDiacritic(progData.filmlist);
                PListener.notify(PListener.EVENT_DIACRITIC_CHANGED, ConfigDialogController.class.getSimpleName());
                ProgData.getInstance().maskerPane.switchOffMasker();
            }).start();
        }

        controllerConfig.close();
        controllerPlay.close();
        controllerAudio.close();
        controllerBlackList.close();
        controllerDownload.close();

        PListener.notify(PListener.EVEMT_SETDATA_CHANGED, ConfigDialogController.class.getSimpleName());

        dialogIsRunning.setValue(false);
        super.close();
    }

    private void initPanel() {
        try {
            controllerConfig = new ControllerConfig(getStage());
            Tab tab = new Tab("Allgemein");
            tab.setClosable(false);
            tab.setContent(controllerConfig);
            if (!blackListDialog) {
                tabPane.getTabs().add(tab);
            }
            controllerPlay = new ControllerPlay(getStage());
            tab = new Tab("Audios");
            tab.setClosable(false);
            tab.setContent(controllerPlay);
            if (!blackListDialog) {
                tabPane.getTabs().add(tab);
            }

            controllerAudio = new ControllerAudio(getStage(), diacriticChanged);
            tab = new Tab("Audioliste laden");
            tab.setClosable(false);
            tab.setContent(controllerAudio);
            if (!blackListDialog) {
                tabPane.getTabs().add(tab);
            }

            controllerBlackList = new ControllerBlackList(this.getStage(), blackChanged);
            tab = new Tab("Blacklist");
            tab.setClosable(false);
            tab.setContent(controllerBlackList);
            tabPane.getTabs().add(tab);

            controllerDownload = new ControllerDownload(getStage());
            tab = new Tab("Download");
            tab.setClosable(false);
            tab.setContent(controllerDownload);
            if (!blackListDialog) {
                tabPane.getTabs().add(tab);
            }

            tabPane.getSelectionModel().select(propSelectedTab.get());
            tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                // readOnlyBinding!!
                propSelectedTab.setValue(newValue);
            });

        } catch (final Exception ex) {
            P2Log.errorLog(784459510, ex);
        }
    }
}
