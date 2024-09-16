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
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DownloadInfoController extends VBox {

    private PaneAudioInfo paneFilmInfo;
    private PaneBandwidthChart paneBandwidthChart;
    private PaneDownloadError paneDownloadError;
    private PaneDownloadInfo paneDownloadInfoList;
    private Tab tabFilmInfo;
    private Tab tabDownloadChart;
    private Tab tabDownloadError;
    private Tab tabDownloadInfo;

    private final ProgData progData;
    private final TabPane tabPane = new TabPane();

    public DownloadInfoController() {
        progData = ProgData.getInstance();
        initInfoPane();
        PListener.addListener(new PListener(PListener.EVENT_TIMER, DownloadInfoController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                paneBandwidthChart.searchInfos(InfoPaneFactory.paneIsVisible(ATPlayerController.PANE_SHOWN.DOWNLOAD, paneBandwidthChart));

                if (InfoPaneFactory.paneIsVisible(ATPlayerController.PANE_SHOWN.DOWNLOAD, paneDownloadInfoList)) {
                    paneDownloadInfoList.setInfoText();
                }
            }
        });
    }

    public void setDownloadInfos(DownloadData download) {
        if (InfoPaneFactory.paneIsVisible(ATPlayerController.PANE_SHOWN.DOWNLOAD, paneFilmInfo)) {
            paneFilmInfo.setAudioData(download);
        }
    }

    public boolean arePanesShowing() {
        return !ProgConfig.DOWNLOAD_PANE_INFO_IS_RIP.getValue() ||
                !ProgConfig.DOWNLOAD_PANE_CHART_IS_RIP.getValue() ||
                !ProgConfig.DOWNLOAD_PANE_ERROR_IS_RIP.getValue() ||
                !ProgConfig.DOWNLOAD_PANE_INFO_LIST_IS_RIP.getValue();
    }

    private void initInfoPane() {
        paneFilmInfo = new PaneAudioInfo(ProgConfig.DOWNLOAD_PANE_INFO_DIVIDER);
        paneBandwidthChart = new PaneBandwidthChart(progData);
        paneDownloadError = new PaneDownloadError();
        paneDownloadInfoList = new PaneDownloadInfo();


        if (ProgConfig.DOWNLOAD_PANE_INFO_IS_RIP.getValue()) {
            dialogInfo();
        }
        ProgConfig.DOWNLOAD_PANE_INFO_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogInfo();
            } else {
                ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

        if (ProgConfig.DOWNLOAD_PANE_CHART_IS_RIP.getValue()) {
            dialogChart();
        }
        ProgConfig.DOWNLOAD_PANE_CHART_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogChart();
            } else {
                ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

        if (ProgConfig.DOWNLOAD_PANE_ERROR_IS_RIP.getValue()) {
            dialogDownloadError();
        }
        ProgConfig.DOWNLOAD_PANE_ERROR_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogDownloadError();
            } else {
                ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

        if (ProgConfig.DOWNLOAD_PANE_INFO_LIST_IS_RIP.getValue()) {
            dialogInfoList();
        }
        ProgConfig.DOWNLOAD_PANE_INFO_LIST_IS_RIP.addListener((u, o, n) -> {
            if (n) {
                dialogInfoList();
            } else {
                ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING.set(true);
            }
            setTabs();
        });

        setTabs();
    }

    private void dialogInfo() {
        new InfoPaneDialog(paneFilmInfo, "Filminfos",
                ProgConfig.DOWNLOAD_PANE_DIALOG_INFO_SIZE,
                ProgConfig.DOWNLOAD_PANE_INFO_IS_RIP,
                ProgData.DOWNLOAD_TAB_ON);
    }

    private void dialogChart() {
        new InfoPaneDialog(paneBandwidthChart, "Downloadchart",
                ProgConfig.DOWNLOAD_PANE_DIALOG_CHART_SIZE,
                ProgConfig.DOWNLOAD_PANE_CHART_IS_RIP,
                ProgData.DOWNLOAD_TAB_ON);
    }

    private void dialogDownloadError() {
        new InfoPaneDialog(paneDownloadError, "Downloadfehler",
                ProgConfig.DOWNLOAD_PANE_DIALOG_ERROR_SIZE,
                ProgConfig.DOWNLOAD_PANE_ERROR_IS_RIP,
                ProgData.DOWNLOAD_TAB_ON);
    }

    private void dialogInfoList() {
        new InfoPaneDialog(paneDownloadInfoList, "Downloadinfos",
                ProgConfig.DOWNLOAD_PANE_DIALOG_DOWN_INFO_SIZE,
                ProgConfig.DOWNLOAD_PANE_INFO_LIST_IS_RIP,
                ProgData.DOWNLOAD_TAB_ON);
    }

    private void setTabs() {
        tabPane.getTabs().clear();

        if (!ProgConfig.DOWNLOAD_PANE_INFO_IS_RIP.getValue()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneFilmInfo, "Beschreibung", ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING, ProgConfig.DOWNLOAD_PANE_INFO_IS_RIP));
        }

        if (!ProgConfig.DOWNLOAD_PANE_CHART_IS_RIP.getValue()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneBandwidthChart, "Downloadchart", ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING, ProgConfig.DOWNLOAD_PANE_CHART_IS_RIP));
        }

        if (!ProgConfig.DOWNLOAD_PANE_ERROR_IS_RIP.getValue()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneDownloadError, "Downloadfehler", ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING, ProgConfig.DOWNLOAD_PANE_ERROR_IS_RIP));
        }

        if (!ProgConfig.DOWNLOAD_PANE_INFO_LIST_IS_RIP.getValue()) {
            tabPane.getTabs().add(
                    InfoPaneFactory.makeTab(paneDownloadInfoList, "Infos", ProgConfig.DOWNLOAD_INFO_TAB_IS_SHOWING, ProgConfig.DOWNLOAD_PANE_INFO_LIST_IS_RIP));
        }


        if (tabPane.getTabs().isEmpty()) {

        } else if (tabPane.getTabs().size() == 1) {
            // dann gibts einen Tab
            final Node node = tabPane.getTabs().get(0).getContent();
            tabPane.getTabs().remove(0);
            getChildren().setAll(node);
            VBox.setVgrow(node, Priority.ALWAYS);

        } else {
            // dann gibts mehre Tabs
            getChildren().setAll(tabPane);
            VBox.setVgrow(tabPane, Priority.ALWAYS);
        }
    }
}
