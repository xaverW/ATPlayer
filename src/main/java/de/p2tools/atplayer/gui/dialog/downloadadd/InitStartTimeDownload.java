/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.atplayer.gui.dialog.downloadadd;

import de.p2tools.atplayer.controller.config.ProgConfig;
import javafx.scene.control.ToggleGroup;

import java.util.Arrays;

public class InitStartTimeDownload {

    private final AddDownloadDto addDownloadDto;

    public InitStartTimeDownload(AddDownloadDto addDownloadDto) {
        this.addDownloadDto = addDownloadDto;
        init();
    }

    private void init() {
        addDownloadDto.chkStartTimeAll.setSelected(true); // soll stand. für alle gelten

        final ToggleGroup toggleGroupStart = new ToggleGroup();
        addDownloadDto.rbStartNow.setToggleGroup(toggleGroupStart);
        addDownloadDto.rbStartNotYet.setToggleGroup(toggleGroupStart);

        addDownloadDto.rbStartNow.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOW);
        addDownloadDto.rbStartNotYet.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOT);

        if (addDownloadDto.addNewDownloads) {
            setStartTime();
        } else {
            // wenn schon eine Startzeit, dann jetzt setzen
            addDownloadDto.rbStartNotYet.setSelected(true);
        }

        addDownloadDto.chkStartTimeAll.setOnAction(a -> {
            if (addDownloadDto.chkStartTimeAll.isSelected()) {
                setStartTime();
            }
        });
        addDownloadDto.rbStartNow.setOnAction(a -> {
            setStartTime();
        });
        addDownloadDto.rbStartNotYet.setOnAction(a -> {
            setStartTime();
        });
    }

    public void makeAct() {
        addDownloadDto.rbStartNow.setDisable(addDownloadDto.getAct().downloadIsRunning());
        addDownloadDto.rbStartNotYet.setDisable(addDownloadDto.getAct().downloadIsRunning());

        if (addDownloadDto.getAct().startNow) {
            // dann starten
            addDownloadDto.rbStartNow.setSelected(true);
        } else {
            // nix
            addDownloadDto.rbStartNotYet.setSelected(true);
        }
    }

    private void setStartTime() {
        if (addDownloadDto.chkStartTimeAll.isSelected()) {
            Arrays.stream(addDownloadDto.addDownloadData).forEach(this::setTime);
        } else {
            setTime(addDownloadDto.getAct());
        }
    }

    private void setTime(AddDownloadData addDownloadData) {
        if (addDownloadDto.rbStartNow.isSelected()) {
            addDownloadData.startNow = true;
        } else {
            addDownloadData.startNow = false;
        }
    }
}
