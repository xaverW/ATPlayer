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

package de.p2tools.atplayer.gui.dialog.downloadadd;


import de.p2tools.atplayer.controller.ProgSave;
import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.dialogs.P2DirFileChooser;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DownloadAddDialogController extends P2DialogExtra {

    private final ProgData progData;
    private final Button btnOk = new Button("_Ok");
    private final Button btnCancel = new Button("_Abbrechen");
    private boolean ok = false;

    private final List audiosToDownloadList;
    private final AddDownloadDto addDownloadDto;

    public DownloadAddDialogController(ProgData progData, List<AudioData> audiosToDownloadList,
                                       List<DownloadData> downloadDataArrayList) {
        super(progData.primaryStage,
                audiosToDownloadList != null ?
                        (audiosToDownloadList.size() > 1 ?
                                ProgConfig.DOWNLOAD_DIALOG_ADD_MORE_SIZE : ProgConfig.DOWNLOAD_DIALOG_ADD_SIZE) :
                        (downloadDataArrayList.size() > 1 ?
                                ProgConfig.DOWNLOAD_DIALOG_ADD_MORE_SIZE : ProgConfig.DOWNLOAD_DIALOG_ADD_SIZE),

                audiosToDownloadList != null ? "Download anlegen" : "Download ändern",
                true, false, DECO.BORDER_SMALL);

        // neue Downloads anlegen
        this.progData = progData;
        this.audiosToDownloadList = Objects.requireNonNullElse(audiosToDownloadList, downloadDataArrayList);
        this.addDownloadDto = new AddDownloadDto(progData, audiosToDownloadList, downloadDataArrayList);
        init(true);
    }

    @Override
    public void make() {
        initGui();
        initButton();
        addDownloadDto.updateAct();
    }

    private void initGui() {
        if (audiosToDownloadList.isEmpty()) {
            // Satz mit x, war wohl nix
            ok = false;
            quit();
            return;
        }

        DownloadAddDialogGui downloadAddDialogGui = new DownloadAddDialogGui(addDownloadDto, getVBoxCont());
        downloadAddDialogGui.addCont();
        downloadAddDialogGui.init();
        addOkCancelButtons(btnOk, btnCancel);
    }

    private void initButton() {
        addDownloadDto.btnDest.setGraphic(ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
        addDownloadDto.btnDest.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen."));
        addDownloadDto.btnDest.setOnAction(event -> P2DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, addDownloadDto.cboPath));

        addDownloadDto.btnPropose.setGraphic(ProgIcons.ICON_BUTTON_PROPOSE.getImageView());
        addDownloadDto.btnPropose.setTooltip(new Tooltip("Einen Pfad zum Speichern vorschlagen lassen."));
        addDownloadDto.btnPropose.setOnAction(event ->
                addDownloadDto.initPathName.proposeDestination());

        addDownloadDto.btnClean.setGraphic(ProgIcons.ICON_BUTTON_CLEAN.getImageView());
        addDownloadDto.btnClean.setTooltip(new Tooltip("Die Liste der Pfade löschen"));
        addDownloadDto.btnClean.setOnAction(a -> addDownloadDto.initPathName.clearPath());

        addDownloadDto.btnPrev.setOnAction(event -> {
            addDownloadDto.actAudioIsShown.setValue(addDownloadDto.actAudioIsShown.getValue() - 1);
            addDownloadDto.updateAct();
        });
        addDownloadDto.btnNext.setOnAction(event -> {
            addDownloadDto.actAudioIsShown.setValue(addDownloadDto.actAudioIsShown.getValue() + 1);
            addDownloadDto.updateAct();
        });
        btnOk.setOnAction(event -> {
            if (check()) {
                quit();
            }
        });
        btnCancel.setOnAction(event -> {
            ok = false;
            quit();
        });
    }

    private boolean check() {
        ok = false;
        for (AddDownloadData d : addDownloadDto.addDownloadData) {
            if (d.download == null) {
                P2Alert.showErrorAlert("Fehlerhafter Download!", "Fehlerhafter Download!",
                        "Download konnte nicht erstellt werden.");

            } else if (d.download.getDestPath().isEmpty() || d.download.getDestFileName().isEmpty()) {
                P2Alert.showErrorAlert("Fehlerhafter Pfad/Name!", "Fehlerhafter Pfad/Name!",
                        "Pfad oder Name ist leer.");

            } else {
                if (DownloadAddDialogFactory.checkPathWritable(d.download.getDestPath())) {
                    ok = true;
                } else {
                    P2Alert.showErrorAlert("Fehlerhafter Pfad/Name!", "Fehlerhafter Pfad/Name!",
                            "Pfad ist nicht beschreibbar.");
                }
            }
        }
        return ok;
    }

    private void quit() {
        //damit der Focus nicht aus der Tabelle verloren geht
        addDownloadDto.initPathName.setUsedPaths();
        progData.ATPlayerController.setFocus();

        if (!ok) {
            close();
            return;
        }

        if (addDownloadDto.addNewDownloads) {
            // dann neue Downloads anlegen
            addNewDownloads();
        } else {
            // oder die bestehenden ändern
            changeDownloads();
        }

        // und jetzt noch die Einstellungen speichern
        ProgSave.saveAll();
        close();
    }

    private void addNewDownloads() {
        List<DownloadData> list = new ArrayList<>();
        List<DownloadData> listStarts = new ArrayList<>();
        for (AddDownloadData addDownloadData : addDownloadDto.addDownloadData) {
            final DownloadData downloadData = addDownloadData.download;
            downloadData.setPathName(addDownloadData.download.getDestPath(),
                    addDownloadData.download.getDestFileName());
            if (addDownloadData.startNow) {
                listStarts.add(downloadData);
            }
            list.add(downloadData);
            ProgConfig.DOWNLOAD_ADD_INFO_FILE.setValue(downloadData.isInfoFile()); // als Vorgabe merken
        }

        progData.downloadList.addWithNo(list);
        progData.downloadList.startDownloads(listStarts, false);
    }

    private void changeDownloads() {
        List<DownloadData> list = new ArrayList<>();
        for (AddDownloadData addDownloadData : addDownloadDto.addDownloadData) {
            if (addDownloadData.downloadIsRunning()) {
                // schon gestartet
                continue;
            }

            final DownloadData downloadData = addDownloadData.download;
            final DownloadData downloadDataOrg = addDownloadData.downloadOrg;
            downloadDataOrg.copyToMe(downloadData);
            downloadDataOrg.setPathName(addDownloadData.download.getDestPath(),
                    addDownloadData.download.getDestFileName());
            if (addDownloadData.startNow) {
                list.add(addDownloadData.downloadOrg);
            }
            ProgConfig.DOWNLOAD_ADD_INFO_FILE.setValue(downloadData.isInfoFile()); // als Vorgabe merken
        }
        progData.downloadList.startDownloads(list, false);
    }
}
