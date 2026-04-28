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

package de.p2tools.atplayer;

import de.p2tools.atplayer.controller.audio.LoadAudioFactory;
import de.p2tools.atplayer.controller.config.PEvents;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.picon.PIconFactory;
import de.p2tools.atplayer.controller.worker.Busy;
import de.p2tools.atplayer.gui.AudioGui;
import de.p2tools.atplayer.gui.DownloadGui;
import de.p2tools.atplayer.gui.ProgMenu;
import de.p2tools.atplayer.gui.StatusBarController;
import de.p2tools.p2lib.p2event.P2Event;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

public class ATPlayerController extends StackPane {

    private final ProgData progData;
    private final BorderPane borderPane = new BorderPane();
    private final Button btnFilmlist = new Button("Audioliste");
    private final Button btnAudio = new Button("Audios");
    private final Button btnDownload = new Button("Downloads");

    public enum PANE_SHOWN {AUDIO, DOWNLOAD}

    public static PANE_SHOWN paneShown = null;
    private final AudioGui audioGui = new AudioGui();
    private final DownloadGui downloadGui = new DownloadGui();
    private HBox splitPaneAudio;
    private HBox splitPaneDownload;
    private final StackPane stackPaneCont = new StackPane();
    private StatusBarController statusBarController;

    public ATPlayerController() {
        progData = ProgData.getInstance();
        init();
    }

    private void init() {
        try {
            // Toolbar
            HBox hBoxBtn = new HBox(5);
//            hBoxBtn.setPadding(new Insets(10, 10, 0, 10));
            hBoxBtn.setAlignment(Pos.BOTTOM_CENTER);
            hBoxBtn.getChildren().addAll(btnAudio, btnDownload);

            final Button btnSize = new Button("Downloads");
            btnSize.setVisible(false);
            btnSize.getStyleClass().add("btnSize");
            Platform.runLater(() -> {
                double sizeW = btnSize.getWidth();
                btnAudio.setMinWidth(sizeW);
                btnDownload.setMinWidth(sizeW);
            });

            StackPane stackPaneTitleButton = new StackPane();
            stackPaneTitleButton.setAlignment(Pos.BOTTOM_CENTER);
            stackPaneTitleButton.setPadding(new Insets(0));
            stackPaneTitleButton.getChildren().addAll(btnSize, hBoxBtn);
            HBox.setHgrow(stackPaneTitleButton, Priority.ALWAYS);


            HBox hBoxTop = new HBox();
            hBoxTop.setPadding(new Insets(5, 10, 0, 10));
            hBoxTop.setSpacing(10);
            hBoxTop.setAlignment(Pos.CENTER);
            hBoxTop.getChildren().addAll(btnFilmlist, stackPaneTitleButton, new ProgMenu());
            HBox.setHgrow(stackPaneTitleButton, Priority.ALWAYS);


            // Center
            splitPaneAudio = audioGui.pack();
            splitPaneAudio.getStyleClass().add("splitPaneTab");
            splitPaneDownload = downloadGui.pack();
            splitPaneDownload.getStyleClass().add("splitPaneTab");
            stackPaneCont.getChildren().addAll(splitPaneAudio, splitPaneDownload);

            VBox vBox = new VBox();
            vBox.getChildren().addAll(stackPaneCont, ProgData.busy.getBusyHbox(Busy.BUSY_SRC.GUI));
            VBox.setVgrow(stackPaneCont, Priority.ALWAYS);

            // Statusbar
            statusBarController = new StatusBarController(progData);

            // Gui zusammenbauen
            borderPane.setTop(hBoxTop);
            borderPane.setCenter(vBox);
            borderPane.setBottom(statusBarController);

            this.setPadding(new Insets(0));
            this.getChildren().addAll(borderPane, progData.maskerPane);

            initMaskerPane();
            initButton();
            selPanelAudio();
        } catch (Exception ex) {
            P2Log.errorLog(597841023, ex);
        }
    }

    private void initMaskerPane() {
        StackPane.setAlignment(progData.maskerPane, Pos.CENTER);
        progData.maskerPane.setPadding(new Insets(4, 1, 1, 1));
        progData.maskerPane.toFront();
        Button btnStop = progData.maskerPane.getButton();
        progData.maskerPane.setButtonText("");
        btnStop.setGraphic(PIconFactory.PICON.BTN_CLEAR.getFontIcon());
        btnStop.setOnAction(a -> LoadAudioFactory.getInstance().loadAudioList.setStop(true));
    }

    private void initButton() {
        btnFilmlist.setMinWidth(Region.USE_PREF_SIZE);
        btnFilmlist.getStyleClass().setAll("pFuncBtn", "btnFilmlist");
        btnFilmlist.setTooltip(new Tooltip("Eine neue Audioliste laden."));
        btnFilmlist.setOnAction(e -> {
            LoadAudioFactory.getInstance().loadListButton();
        });

        btnAudio.setTooltip(new Tooltip("Filme anzeigen"));
        btnAudio.setOnAction(e -> selPanelAudio());
        btnAudio.getStyleClass().setAll("pFuncBtn", "pFuncBtnTitleBar");

        btnDownload.setTooltip(new Tooltip("Downloads anzeigen"));
        btnDownload.setOnAction(e -> selPanelDownload());
        btnDownload.getStyleClass().setAll("pFuncBtn", "pFuncBtnTitleBar");

        btnAudio.setOnMouseClicked(mouseEvent -> {
            if (progData.maskerPane.isVisible() || paneShown != PANE_SHOWN.AUDIO) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                ATPlayerFactory.setInfos();
            }
        });
        btnDownload.setOnMouseClicked(mouseEvent -> {
            if (progData.maskerPane.isVisible() || paneShown != PANE_SHOWN.DOWNLOAD) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                ATPlayerFactory.setInfos();
            }
        });
        progData.pEventHandler.addListener(new P2Listener(PEvents.LOAD_RADIO_LIST_FINISHED) {
            @Override
            public void pingGui(P2Event event) {
                if (stackPaneCont.getChildren().isEmpty()) {
                    return;
                }
                setFocus();
            }
        });
    }

    private void selPanelAudio() {
        if (paneShown == PANE_SHOWN.AUDIO) {
            // dann ist der 2. Klick
            ATPlayerFactory.setFilter();
            return;
        }

        paneShown = PANE_SHOWN.AUDIO;
        setButtonStyle();
        splitPaneAudio.toFront();
        progData.audioGuiController.isShown();
        statusBarController.setStatusbarIndex();
        ProgData.AUDIO_TAB_ON.setValue(Boolean.TRUE);
        ProgData.DOWNLOAD_TAB_ON.setValue(Boolean.FALSE);
    }

    private void selPanelDownload() {
        if (paneShown == PANE_SHOWN.DOWNLOAD) {
            // dann ist der 2. Klick
            ATPlayerFactory.setFilter();
            return;
        }

        paneShown = PANE_SHOWN.DOWNLOAD;
        setButtonStyle();
        splitPaneDownload.toFront();
        progData.downloadGuiController.isShown();
        statusBarController.setStatusbarIndex();
        ProgData.AUDIO_TAB_ON.setValue(Boolean.FALSE);
        ProgData.DOWNLOAD_TAB_ON.setValue(Boolean.TRUE);
    }

    private void setButtonStyle() {
        if (paneShown == PANE_SHOWN.AUDIO) {
            btnAudio.getStyleClass().add("pFuncBtnTitleBarSel");
        } else {
            btnAudio.getStyleClass().removeAll("pFuncBtnTitleBarSel");
        }

        if (paneShown == PANE_SHOWN.DOWNLOAD) {
            btnDownload.getStyleClass().add("pFuncBtnTitleBarSel");
        } else {
            btnDownload.getStyleClass().removeAll("pFuncBtnTitleBarSel");
        }
    }

    public void setFocus() {
        if (paneShown == PANE_SHOWN.AUDIO) {
            progData.audioGuiController.isShown();
        }
        if (paneShown == PANE_SHOWN.DOWNLOAD) {
            progData.downloadGuiController.isShown();
        }
    }
}
