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
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.guitools.P2Hyperlink;
import de.p2tools.p2lib.tools.date.P2LDateFactory;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.*;

import java.util.List;

public class AddDownloadDto {

    public boolean addNewDownloads = true;

    public InitPathName initPathName;
    public InitInfo initInfo;
    public InitUrl initUrl;
    public InitStartTimeDownload initStartTimeDownload;

    public ProgData progData;
    public AddDownloadData[] addDownloadData;

    public IntegerProperty actAudioIsShown = new SimpleIntegerProperty(0);

    public final Label lblFree = new Label("4M noch frei");
    public final Label lblAudio = new Label("Audio:");
    public final Label lblAudioTitle = new Label("ARD: Tatort, ..");
    public final Label lblAudioDateTime = new Label("2023, ...");
    public final Label lblAudioSize = new Label("15MB");
    public final Button btnPrev = new Button("<");
    public final Button btnNext = new Button(">");
    public final Label lblSum = new Label("");
    public Label lblAll = new Label("Für alle\nändern");

    public CheckBox chkSetAll = new CheckBox();
    public CheckBox chkResolutionAll = new CheckBox();
    public CheckBox chkPathAll = new CheckBox();
    public CheckBox chkInfoAll = new CheckBox();
    public CheckBox chkStartTimeAll = new CheckBox();

    // URL
    public final P2Hyperlink p2HyperlinkUrlDownload = new P2Hyperlink("",
            ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());

    // Path / Name
    public final ComboBox<String> cboPath = new ComboBox<>();
    public final Button btnDest = new Button(); // Pfad auswählen
    public final Button btnPropose = new Button(); // Pfad vorschlagen
    public final Button btnClean = new Button(); // Liste der Pfade löschen
    public final TextField txtName = new TextField();

    // info
    public final CheckBox chkInfo = new CheckBox("Infodatei anlegen: \"Name.txt\"");

    // Startzeit
    public final RadioButton rbStartNotYet = new RadioButton("noch nicht");
    public final RadioButton rbStartNow = new RadioButton("sofort");

    public AddDownloadDto(ProgData progData, List<AudioData> toDownloadList,
                          List<DownloadData> downloadDataArrayList) {
        this.progData = progData;

        if (toDownloadList != null) {
            addDownloadData = InitDownloadAddArray.initDownloadInfoArrayAudio(toDownloadList, this);
            initPathName = new InitPathName(this);
            initInfo = new InitInfo(this);
            initUrl = new InitUrl(this);
            initStartTimeDownload = new InitStartTimeDownload(this);
        } else {
            addNewDownloads = false;
            addDownloadData = InitDownloadAddArray.initDownloadInfoArrayDownload(downloadDataArrayList, this);
            initPathName = new InitPathName(this);
            initInfo = new InitInfo(this);
            initUrl = new InitUrl(this);
            initStartTimeDownload = new InitStartTimeDownload(this);
        }
    }

    public AddDownloadData getAct() {
        return addDownloadData[actAudioIsShown.getValue()];
    }

    public void updateAct() {
        final int nr = actAudioIsShown.getValue() + 1;
        lblSum.setText("Audio " + nr + " von " + addDownloadData.length + " Audios");

        if (actAudioIsShown.getValue() == 0) {
            btnPrev.setDisable(true);
            btnNext.setDisable(false);
        } else if (actAudioIsShown.getValue() == addDownloadData.length - 1) {
            btnPrev.setDisable(false);
            btnNext.setDisable(true);
        } else {
            btnPrev.setDisable(false);
            btnNext.setDisable(false);
        }

        txtName.setEditable(!getAct().downloadIsRunning());
        cboPath.setEditable(!getAct().downloadIsRunning());
        chkInfo.setDisable(getAct().downloadIsRunning());
        btnDest.setDisable(getAct().downloadIsRunning());
        btnPropose.setDisable(getAct().downloadIsRunning());
        btnClean.setDisable(getAct().downloadIsRunning());
        rbStartNow.setDisable(getAct().downloadIsRunning());
        rbStartNotYet.setDisable(getAct().downloadIsRunning());

        lblAudioTitle.setText(getAct().download.getChannel()
                + "  -  " + getAct().download.getTitle());
        lblAudioDateTime.setText("Datum: " + P2LDateFactory.toString(getAct().download.getFilmDate())
                + "       Zeit: " + getAct().download.getFilmTime());
        lblAudioSize.setText("Dauer [min]: " + getAct().download.getDurationMinute()
                + (getAct().download.getDownloadSize().getTargetSizeMBStr().isEmpty() ?
                "" : "       Größe [MB]: " + getAct().download.getDownloadSize().getTargetSizeMBStr()));

        initPathName.makeAct();
        initInfo.makeAct();
        initUrl.makeAct();
        initStartTimeDownload.makeAct();
    }
}
