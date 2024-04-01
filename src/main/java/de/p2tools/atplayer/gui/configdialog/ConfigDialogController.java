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
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadListener;
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
    ControllerConfig controllerConfig;
    ControllerPlay controllerPlay;
    ControllerAudio controllerAudio;
    ControllerDownload controllerDownload;
    private P2LoadListener listener;
    private TabPane tabPane = new TabPane();
    private Button btnOk = new Button("_Ok");
    private BooleanProperty diacriticChanged = new SimpleBooleanProperty(false);

    public ConfigDialogController(ProgData progData) {
        super(progData.primaryStage, ProgConfig.CONFIG_DIALOG_SIZE, "Einstellungen",
                true, false, DECO.NO_BORDER, true);

        this.progData = progData;
        init(false);
    }

    @Override
    public void make() {
        setMaskerPane();
        progData.maskerPane.visibleProperty().addListener((u, o, n) -> {
            setMaskerPane();
        });
        Button btnStop = getMaskerPane().getButton();
        getMaskerPane().setButtonText("");
        btnStop.setGraphic(ProgIcons.ICON_BUTTON_STOP.getImageView());
        btnStop.setOnAction(a -> LoadAudioFactory.getInstance().loadAudioList.setStop(true));
        listener = new P2LoadListener() {
            @Override
            public void start(P2LoadEvent event) {
                if (event.progress == P2LoadListener.PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Audioliste
                    getMaskerPane().setMaskerVisible(true, false, false);
                } else {
                    getMaskerPane().setMaskerVisible();
                }
                getMaskerPane().setMaskerProgress(event.progress, event.text);
            }

            @Override
            public void progress(P2LoadEvent event) {
                getMaskerPane().setMaskerProgress(event.progress, event.text);
            }

            @Override
            public void loaded(P2LoadEvent event) {
                getMaskerPane().setMaskerVisible(true, false, false);
                getMaskerPane().setMaskerProgress(P2LoadListener.PROGRESS_INDETERMINATE, "Audioliste verarbeiten");
            }

            @Override
            public void finished(P2LoadEvent event) {
                getMaskerPane().switchOffMasker();
            }
        };
        LoadAudioFactory.getInstance().loadAudioList.p2LoadNotifier.addListenerLoadFilmlist(listener);

        VBox.setVgrow(tabPane, Priority.ALWAYS);
        getVBoxCont().getChildren().add(tabPane);
        getVBoxCont().setPadding(new Insets(0));

        addOkButton(btnOk);
        btnOk.setOnAction(a -> close());

        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> updateCss());
        initPanel();
    }

    @Override
    public void close() {
//        if (!geo.equals(ProgConfig.SYSTEM_GEO_HOME_PLACE.get())) {
//            // dann hat sich der Geo-Standort geändert
//            progData.filmlist.markGeoBlocked();
//        }

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
        controllerDownload.close();

        LoadAudioFactory.getInstance().loadAudioList.p2LoadNotifier.removeListenerLoadFilmlist(listener);
        PListener.notify(PListener.EVEMT_SETDATA_CHANGED, ConfigDialogController.class.getSimpleName());
        super.close();
    }

    private void setMaskerPane() {
        if (progData.maskerPane.isVisible()) {
            this.setMaskerVisible(true, true);
        } else {
            this.setMaskerVisible(false);
        }
    }

    private void initPanel() {
        try {
            controllerConfig = new ControllerConfig(getStage());
            Tab tab = new Tab("Allgemein");
            tab.setClosable(false);
            tab.setContent(controllerConfig);
            tabPane.getTabs().add(tab);

            controllerPlay = new ControllerPlay(getStage());
            tab = new Tab("Audios");
            tab.setClosable(false);
            tab.setContent(controllerPlay);
            tabPane.getTabs().add(tab);

            controllerAudio = new ControllerAudio(getStage(), diacriticChanged);
            tab = new Tab("Audioliste laden");
            tab.setClosable(false);
            tab.setContent(controllerAudio);
            tabPane.getTabs().add(tab);

            controllerDownload = new ControllerDownload(getStage());
            tab = new Tab("Download");
            tab.setClosable(false);
            tab.setContent(controllerDownload);
            tabPane.getTabs().add(tab);

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
