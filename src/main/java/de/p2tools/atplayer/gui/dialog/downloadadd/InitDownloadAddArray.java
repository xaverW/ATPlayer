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
import de.p2tools.atplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.atdata.AudioData;

import java.util.List;

public class InitDownloadAddArray {

    private InitDownloadAddArray() {
    }

    public static AddDownloadData[] initDownloadInfoArrayAudio(List<AudioData> audiosToDownloadList, AddDownloadDto addDownloadDto) {
        // DownloadArr anlegen
        AddDownloadData[] addDownloadData = new AddDownloadData[audiosToDownloadList.size()];
        for (int i = 0; i < audiosToDownloadList.size(); ++i) {
            addDownloadData[i] = new AddDownloadData();
            addDownloadData[i].download = new DownloadData(audiosToDownloadList.get(i));
            addDownloadData[i].download.setInfoFile(ProgConfig.DOWNLOAD_ADD_INFO_FILE.getValue());
        }
        return addDownloadData;
    }

    public static AddDownloadData[] initDownloadInfoArrayDownload(List<DownloadData> downloadDataArrayList, AddDownloadDto addDownloadDto) {
        // DownloadArr anlegen
        AddDownloadData[] addDownloadData = new AddDownloadData[downloadDataArrayList.size()];
        for (int i = 0; i < downloadDataArrayList.size(); ++i) {
            addDownloadData[i] = new AddDownloadData();
            addDownloadData[i].download = downloadDataArrayList.get(i).getCopy();
            addDownloadData[i].downloadOrg = downloadDataArrayList.get(i);
        }
        return addDownloadData;
    }
}
