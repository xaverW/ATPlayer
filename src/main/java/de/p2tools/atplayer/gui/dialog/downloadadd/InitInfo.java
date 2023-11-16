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

import java.util.Arrays;

public class InitInfo {

    private final AddDownloadDto addDownloadDto;

    public InitInfo(AddDownloadDto addDownloadDto) {
        this.addDownloadDto = addDownloadDto;
        init();
    }

    private void init() {
        addDownloadDto.chkInfo.setOnAction(a -> setInfoSubTitle());
        addDownloadDto.chkInfoAll.setOnAction(a -> {
            if (addDownloadDto.chkInfoAll.isSelected()) {
                setInfoSubTitle();
            }
        });
    }

    public void makeAct() {
        addDownloadDto.chkInfo.setDisable(addDownloadDto.getAct().downloadIsRunning());
        addDownloadDto.chkInfo.setSelected(addDownloadDto.getAct().download.isInfoFile());
    }

    private void setInfoSubTitle() {
        // Info
        if (addDownloadDto.chkInfoAll.isSelected()) {
            Arrays.stream(addDownloadDto.addDownloadData).forEach(downloadAddData ->
                    downloadAddData.download.setInfoFile(addDownloadDto.chkInfo.isSelected()));
        } else {
            addDownloadDto.getAct().download.setInfoFile(addDownloadDto.chkInfo.isSelected());
        }
    }
}
