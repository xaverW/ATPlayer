/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
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

package de.p2tools.atplayer.gui.infopane;

import de.p2tools.atplayer.ATPlayerController;
import de.p2tools.atplayer.controller.config.PListener;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneH;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DownloadInfoController extends P2ClosePaneH {

    private PaneAudioInfo paneFilmInfo;
    private PaneBandwidthChart paneBandwidthChart;
    private PaneDownloadError paneDownloadError;
    private PaneDownloadInfo paneDownloadInfo;
    private Tab tabFilmInfo;
    private Tab tabDownloadChart;
    private Tab tabDownloadError;
    private Tab tabDownloadInfo;

    private final ProgData progData;
    private final TabPane tabPane = new TabPane();

    public DownloadInfoController() {
        super(ProgConfig.DOWNLOAD_GUI_INFO_ON, false, true);
        progData = ProgData.getInstance();
        initInfoPane();
        PListener.addListener(new PListener(PListener.EVENT_TIMER, DownloadInfoController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                paneBandwidthChart.searchInfos(InfoPaneFactory.paneIsVisible(ATPlayerController.PANE_SHOWN.DOWNLOAD,
                        getVBoxAll(), tabPane, paneBandwidthChart,
                        ProgConfig.DOWNLOAD_GUI_INFO_ON, ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON));

                if (InfoPaneFactory.paneIsVisible(ATPlayerController.PANE_SHOWN.DOWNLOAD,
                        getVBoxAll(), tabPane, paneDownloadInfo,
                        ProgConfig.DOWNLOAD_GUI_INFO_ON, ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON)) {
                    paneDownloadInfo.setInfoText();
                }
            }
        });
    }

    public void setDownloadInfos(DownloadData download) {
        if (InfoPaneFactory.paneIsVisible(ATPlayerController.PANE_SHOWN.DOWNLOAD,
                getVBoxAll(), tabPane, paneFilmInfo,
                ProgConfig.DOWNLOAD_GUI_INFO_ON, ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON)) {
            paneFilmInfo.setAudioData(download);
        }
    }

    public boolean isPaneShowing() {
        return !ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON.getValue() ||
                !ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON.getValue() ||
                !ProgConfig.DOWNLOAD_PANE_DIALOG_ERROR_ON.getValue() ||
                !ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON.getValue();
    }

    private void initInfoPane() {
        paneFilmInfo = new PaneAudioInfo();
        paneBandwidthChart = new PaneBandwidthChart(progData);
        paneDownloadError = new PaneDownloadError();
        paneDownloadInfo = new PaneDownloadInfo();

        tabFilmInfo = new Tab("Beschreibung");
        tabFilmInfo.setClosable(false);
        tabDownloadChart = new Tab("Downloadchart");
        tabDownloadChart.setClosable(false);
        tabDownloadError = new Tab("Downloadfehler");
        tabDownloadError.setClosable(false);
        tabDownloadInfo = new Tab("Infos");
        tabDownloadInfo.setClosable(false);

        super.getRipProperty().addListener((u, o, n) -> {
            if (InfoPaneFactory.isSelPane(getVBoxAll(), tabPane, paneFilmInfo)) {
                dialogInfo();
            } else if (InfoPaneFactory.isSelPane(getVBoxAll(), tabPane, paneBandwidthChart)) {
                dialogChart();
            } else if (InfoPaneFactory.isSelPane(getVBoxAll(), tabPane, paneDownloadError)) {
                dialogDownloadError();
            } else if (InfoPaneFactory.isSelPane(getVBoxAll(), tabPane, paneDownloadInfo)) {
                dialogDownloadInfo();
            }
        });

        if (ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON.getValue()) {
            dialogInfo();
        }
        if (ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON.getValue()) {
            dialogChart();
        }
        if (ProgConfig.DOWNLOAD_PANE_DIALOG_ERROR_ON.getValue()) {
            dialogDownloadError();
        }
        if (ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON.getValue()) {
            dialogDownloadInfo();
        }

        ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON.addListener((u, o, n) -> setTabs());
        ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON.addListener((u, o, n) -> setTabs());
        ProgConfig.DOWNLOAD_PANE_DIALOG_ERROR_ON.addListener((u, o, n) -> setTabs());
        ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON.addListener((u, o, n) -> setTabs());
        setTabs();
    }

    private void dialogInfo() {
        InfoPaneFactory.setDialogInfo(tabFilmInfo, paneFilmInfo, "Filminfos",
                ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_SIZE, ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON,
                ProgConfig.DOWNLOAD_GUI_INFO_ON, ProgData.DOWNLOAD_TAB_ON);
    }

    private void dialogChart() {
        InfoPaneFactory.setDialogInfo(tabDownloadChart, paneBandwidthChart, "Downloadchart",
                ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_SIZE, ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON,
                ProgConfig.DOWNLOAD_GUI_INFO_ON, ProgData.DOWNLOAD_TAB_ON);
    }

    private void dialogDownloadError() {
        InfoPaneFactory.setDialogInfo(tabDownloadError, paneDownloadError, "Downloadfehler",
                ProgConfig.DOWNLOAD_PANE_DIALOG_ERROR_SIZE, ProgConfig.DOWNLOAD_PANE_DIALOG_ERROR_ON,
                ProgConfig.DOWNLOAD_GUI_INFO_ON, ProgData.DOWNLOAD_TAB_ON);
    }

    private void dialogDownloadInfo() {
        InfoPaneFactory.setDialogInfo(tabDownloadInfo, paneDownloadInfo, "Downloadinfos",
                ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_SIZE, ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON,
                ProgConfig.DOWNLOAD_GUI_INFO_ON, ProgData.DOWNLOAD_TAB_ON);
    }

    private void setTabs() {
        int i = 0;

        if (ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_ON.getValue()) {
            tabPane.getTabs().remove(tabFilmInfo);
        } else {
            tabFilmInfo.setContent(paneFilmInfo);
            if (!tabPane.getTabs().contains(tabFilmInfo)) {
                tabPane.getTabs().add(i, tabFilmInfo);
            }
            ++i;
        }

        if (ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_ON.getValue()) {
            tabPane.getTabs().remove(tabDownloadChart);
        } else {
            tabDownloadChart.setContent(paneBandwidthChart);
            if (!tabPane.getTabs().contains(tabDownloadChart)) {
                tabPane.getTabs().add(i, tabDownloadChart);
            }
            ++i;
        }

        if (ProgConfig.DOWNLOAD_PANE_DIALOG_ERROR_ON.getValue()) {
            tabPane.getTabs().remove(tabDownloadError);
        } else {
            tabDownloadError.setContent(paneDownloadError);
            if (!tabPane.getTabs().contains(tabDownloadError)) {
                tabPane.getTabs().add(i, tabDownloadError);
            }
            ++i;
        }

        if (ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_ON.getValue()) {
            tabPane.getTabs().remove(tabDownloadInfo);
        } else {
            tabDownloadInfo.setContent(paneDownloadInfo);
            if (!tabPane.getTabs().contains(tabDownloadInfo)) {
                tabPane.getTabs().add(i, tabDownloadInfo);
            }
            ++i;
        }

        if (i == 0) {
            getVBoxAll().getChildren().clear();
            ProgConfig.DOWNLOAD_GUI_INFO_ON.set(false);

        } else if (i == 1) {
            // dann gibts einen Tab
            final Node node = tabPane.getTabs().get(0).getContent();
            tabPane.getTabs().remove(0);
            getVBoxAll().getChildren().setAll(node);
            VBox.setVgrow(node, Priority.ALWAYS);
            ProgConfig.DOWNLOAD_GUI_INFO_ON.set(true);

        } else {
            // dann gibts mehre Tabs
            getVBoxAll().getChildren().setAll(tabPane);
            VBox.setVgrow(tabPane, Priority.ALWAYS);
            ProgConfig.DOWNLOAD_GUI_INFO_ON.set(true);
        }
    }
}
