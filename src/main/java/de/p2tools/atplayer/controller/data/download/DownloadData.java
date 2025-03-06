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

package de.p2tools.atplayer.controller.data.download;

import de.p2tools.atplayer.controller.config.ProgConst;
import de.p2tools.atplayer.controller.starter.StartDownloadDto;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.atdata.AudioDataXml;
import de.p2tools.p2lib.tools.P2InfoFactory;
import de.p2tools.p2lib.tools.date.P2LDateFactory;
import de.p2tools.p2lib.tools.file.P2FileUtils;
import de.p2tools.p2lib.tools.net.PUrlTools;
import javafx.application.Platform;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public final class DownloadData extends DownloadDataProps {

    private StartDownloadDto startDownloadDto = new StartDownloadDto(this);
    private AudioData audioData = null;
    private String errorMessage = "";

    public DownloadData() {
    }

    public DownloadData(AudioData audioData) {
        setAudioData(audioData);
        // und endlich Aufruf bauen :)
        DownloadFactoryMakeParameter.makeProgParameter(this);
    }

    public DownloadData(List<AudioData> filmList) {
        // das ist ein Download der über den Button/Menü "Abspielen" gestartet wurde
        // und der wird nicht in die DownloadListe einsortiert, muss also sofort gestartet werden

        setAudioData(filmList.get(0));
        setInfoFile(false);
        setSource(DownloadConstants.SRC_BUTTON);
        if (filmList.size() > 1) {
            // dass müssen die URLs aller Filme gesetzt werden, dass alle drin sind
            getUrlList().clear();
            for (AudioData audioData : filmList) {
                getUrlList().add(audioData.getUrl());
            }
        }

        DownloadFactoryMakeParameter.makeProgParameter(this);
    }

    //==============================================
    // Downloadstatus
    //==============================================
    public boolean isStateInit() {
        return getState() == DownloadConstants.STATE_INIT;
    }

    public boolean isStateStopped() {
        return getState() == DownloadConstants.STATE_STOPPED;
    }

    public boolean isStateStartedWaiting() {
        return getState() == DownloadConstants.STATE_STARTED_WAITING;
    }

    public boolean isStateStartedRun() {
        return getState() == DownloadConstants.STATE_STARTED_RUN;
    }

    public boolean isStateFinished() {
        return getState() == DownloadConstants.STATE_FINISHED;
    }

    public boolean isStateError() {
        return getState() == DownloadConstants.STATE_ERROR;
    }

    public void setStateStartedWaiting() {
        setState(DownloadConstants.STATE_STARTED_WAITING);
    }

    public void setStateStartedRun() {
        setState(DownloadConstants.STATE_STARTED_RUN);
    }

    public void setStateFinished() {
        setState(DownloadConstants.STATE_FINISHED);
    }

    public void setStateError(String error) {
        if (!error.isEmpty()) {
            getDownloadStartDto().addErrMsg(error);
        }
        setState(DownloadConstants.STATE_ERROR);
    }

    //=======================================
    public boolean isStarted() {
        return getState() > DownloadConstants.STATE_STOPPED && !isStateFinished();
    }

    public boolean isNotStartedOrFinished() {
        return isStateInit() || isStateStopped();
    }

    public boolean isFinishedOrError() {
        return getState() >= DownloadConstants.STATE_FINISHED;
    }

    //==============================================
    //==============================================
    public void initStartDownload() {
        // Download zum Start vorbereiten
        getDownloadStartDto().setDeleteAfterStop(false);
        getDownloadStartDto().setStartCounter(0);
        setBandwidth(0);
        setStateStartedWaiting();
        setErrorMessage("");
    }

    public void putBack() {
        // download resetten, und als "zurückgestellt" markieren
        setPlacedBack(true);
        resetDownload();
    }

    // todo: reset, restart, stop????
    public void resetDownload() {
        // stoppen und alles zurücksetzen
        stopDownload(false);
        setProgress(DownloadConstants.PROGRESS_NOT_STARTED); // damit auch fehlerhafte zurückgesetzt werden
        setState(DownloadConstants.STATE_INIT);
    }

    public void stopDownload(boolean deleteAfterStop) {
        getDownloadStartDto().setDeleteAfterStop(deleteAfterStop);
        stopDownload();
    }

    public void stopDownload() {
        if (!isStateError()) {
            setProgress(DownloadConstants.PROGRESS_NOT_STARTED);
            setState(DownloadConstants.STATE_STOPPED);
        }

        getDownloadSize().resetActFileSize();
        setRemaining(DownloadConstants.REMAINING_NOT_STARTET);
        setBandwidth(0);
        setNo(P2LibConst.NUMBER_NOT_STARTED);
    }

    //==============================================
    // Get/Set
    //==============================================
    public StartDownloadDto getDownloadStartDto() {
        return startDownloadDto;
    }

    public void setDownloadStartDto(StartDownloadDto startDownloadDto) {
        this.startDownloadDto = startDownloadDto;
    }

    public AudioData getAudioData() {
        return audioData;
    }

    public void setAudioData(AudioData audioData) {
        if (audioData == null) {
            // bei gespeicherten Downloads kann es den Film nicht mehr geben
            setFilmNo(ProgConst.NUMBER_NOT_EXISTS);
            return;
        }

        this.audioData = audioData;
        setFilmNo(audioData.getNo());
        setUrl(audioData.getUrl()); // im MTPlayer wird das in setResolution gemacht

        setChannel(audioData.getChannel());
        setGenre(audioData.getGenre());
        setTheme(audioData.getTheme());
        setTitle(audioData.getTitle());
        setFilmUrl(audioData.getUrl());
        setUrlWebsite(audioData.getWebsite());
        setDescription(audioData.getDescription());
        getDownloadSize().setTargetSize(audioData.arr[AudioData.AUDIO_SIZE_MB]);

        setFilmDate(audioData.arr[AudioDataXml.AUDIO_DATE]);
        setFilmTime(audioData.getTime());
        setDurationMinute(audioData.getDurationMinute());
    }

    public String getFileNameWithoutSuffix() {
        return PUrlTools.getFileNameWithoutSuffix(getDestFileName());
    }

    public String getPathFileNameWithoutSuffix() {
        return PUrlTools.getFileNameWithoutSuffix(getDestPathFile());
    }

    public String getFileNameSuffix() {
        return P2FileUtils.getFileNameSuffix(getDestPathFile());
    }

    public File getFile() {
        return startDownloadDto.getFile();
    }

    public void setFile(File file) {
        this.startDownloadDto.setFile(file);
        destFileNameProperty().setValue(file.getName());
        destPathProperty().setValue(file.getParent());
        destPathFileProperty().setValue(file.getAbsolutePath());
    }

    public void setFile(String path, String name) {
        setFile(P2FileUtils.addsPath(path, name));
    }

    public void setFile(String file) {
        startDownloadDto.setFile(Path.of(file).toFile());
        String name = startDownloadDto.getFile().getName();
        String path = startDownloadDto.getFile().getParent();
        String pathFile = startDownloadDto.getFile().getAbsolutePath();
        destFileNameProperty().setValue(name == null ? "" : name);
        destPathProperty().setValue(path == null ? "" : path);
        destPathFileProperty().setValue(pathFile == null ? "" : pathFile);
    }

    public void setPathName(String path, String name) {
        // setzt den neuen Namen/Pfad und kontrolliert nochmal
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }

        //=====================================================
        // zur Sicherheit
        if (path.isEmpty()) {
            path = P2InfoFactory.getStandardDownloadPath();
        }
        if (name.isEmpty()) {
            name = P2LDateFactory.toStringR(LocalDate.now()) + '_' + getTheme() + '-' + getTitle() + ".mp3";
        }
        final String[] pathName = {path, name};
        P2FileUtils.checkLengthPath(pathName);
        if (!pathName[0].equals(path) || !pathName[1].equals(name)) {
            Platform.runLater(() ->
                    new P2Alert().showInfoAlert("Pfad zu lang!", "Pfad zu lang!",
                            "Dateiname war zu lang und wurde gekürzt!")
            );
            path = pathName[0];
            name = pathName[1];
        }

        //=====================================================
        setFile(P2FileUtils.addsPath(path, name));
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        final String s = "Der Download hatte einen Fehler:\n\n";
        this.errorMessage = s + errorMessage;
    }

    public DownloadData getCopy() {
        final DownloadData downloadData = new DownloadData();
        for (int i = 0; i < properties.length; ++i) {
            downloadData.properties[i].setValue(this.properties[i].getValue());
        }

        downloadData.audioData = audioData;
        downloadData.setDownloadStartDto(getDownloadStartDto());
        downloadData.getUrlList().setAll(getUrl());
        return downloadData;
    }

    public void copyToMe(DownloadData downloadData) {
        for (int i = 0; i < properties.length; ++i) {
            properties[i].setValue(downloadData.properties[i].getValue());
        }

        audioData = downloadData.audioData;
        setDownloadStartDto(downloadData.getDownloadStartDto());
        getUrlList().setAll(downloadData.getUrlList());
    }
}
