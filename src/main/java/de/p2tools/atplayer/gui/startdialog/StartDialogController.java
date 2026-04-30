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

package de.p2tools.atplayer.gui.startdialog;

import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;


public class StartDialogController extends P2DialogExtra {

    private static final String STR_START_1 = "Infos";
    private static final String STR_START_2 = "Infos";
    private static final String STR_UPDATE = "Update";
    private static final String STR_COLOR = "Farbe";
    private static final String STR_FILM = "Audios";
    private static final String STR_PATH = "Pfade";
    private final ProgData progData;
    private boolean ok = false;
    private Button btnOk, btnCancel;
    private Button btnPrev, btnNext;
    private Button btnStart1 = new Button(STR_START_1), btnStart2 = new Button(STR_START_2),
            btnUpdate = new Button(STR_UPDATE),
            btnColor = new Button(STR_COLOR),
            btnFilm = new Button(STR_FILM),
            btnPath = new Button(STR_PATH);
    private State aktState = State.START_1;
    private VBox vBoxCont = new VBox();

    private StartPane startPane1;
    private StartPane startPane2;
    private StartPaneUpdate startPaneUpdate;
    private StartPaneColor startPaneColor;
    private StartPaneAudio startPaneAudio;
    private StartPaneFilm startPaneFilm;

    public StartDialogController() {
        super(null, null, "Starteinstellungen");

        this.progData = ProgData.getInstance();
        init(true);
    }

    @Override
    public void make() {
        init();
        initStack();
        initButton();
        initTooltip();
        selectActPane();
    }

    private void closeDialog(boolean ok) {
        this.ok = ok;
        startPane1.close();
        startPane2.close();
        startPaneUpdate.close();
        startPaneColor.close();
        startPaneAudio.close();
        startPaneFilm.close();
        super.close();
    }

    public boolean isOk() {
        return ok;
    }

    private void init() {
        final TilePane tilePane = new TilePane();
        tilePane.setAlignment(Pos.CENTER);
        tilePane.setHgap(10);
        tilePane.setVgap(10);

        tilePane.getChildren().addAll(btnStart1, btnStart2, btnUpdate, btnColor, btnFilm, btnPath);
        tilePane.setAlignment(Pos.CENTER);
        tilePane.setPadding(new Insets(10, 10, 20, 10));
        tilePane.setHgap(10);
        tilePane.setVgap(10);

        initTopButton(btnStart1, State.START_1);
        initTopButton(btnStart2, State.START_2);
        initTopButton(btnUpdate, State.UPDATE);
        initTopButton(btnColor, State.COLOR);
        initTopButton(btnFilm, State.FILM);
        initTopButton(btnPath, State.PATH);

        VBox.setVgrow(vBoxCont, Priority.ALWAYS);
        getVBoxCont().setPadding(new Insets(5));
        getVBoxCont().getChildren().addAll(tilePane, P2GuiTools.getHDistance(5), vBoxCont);
    }

    private void initTopButton(Button btn, State state) {
        btn.getStyleClass().addAll("btnStartDialog");
        btn.setAlignment(Pos.CENTER);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(a -> {
            aktState = state;
            selectActPane();
        });
    }

    private void initStack() {
        //startPane 1
        startPane1 = new StartPane(getStage());
        startPane1.makeStart1();

        //startPane 2
        startPane2 = new StartPane(getStage());
        startPane2.makeStart2();

        //updatePane
        startPaneUpdate = new StartPaneUpdate(getStage());
        startPaneUpdate.makeStart();

        //colorPane
        startPaneColor = new StartPaneColor(getStage());
        startPaneColor.make();

        //filmPane
        startPaneAudio = new StartPaneAudio(getStage());
        startPaneAudio.make();

        //pathPane
        startPaneFilm = new StartPaneFilm(getStage());
        startPaneFilm.makePath();
    }

    private void initButton() {
        btnOk = new Button("_Ok");
        btnOk.setDisable(true);
        btnOk.setOnAction(a -> {
            closeDialog(true);
        });

        btnCancel = new Button("_Abbrechen");
        btnCancel.setOnAction(a -> closeDialog(false));

        btnNext = P2Button.getButton(PIconFactory.PICON.BTN_NEXT.getFontIcon(), "nächste Seite");
        btnNext.setOnAction(event -> {
            switch (aktState) {
                case START_1:
                    aktState = State.START_2;
                    break;
                case START_2:
                    aktState = State.UPDATE;
                    break;
                case UPDATE:
                    aktState = State.COLOR;
                    break;
                case COLOR:
                    aktState = State.FILM;
                    break;
                case FILM:
                    aktState = State.PATH;
                    break;
                case PATH:
                    break;
            }
            selectActPane();
        });
        btnPrev = P2Button.getButton(PIconFactory.PICON.BTN_PREV.getFontIcon(), "vorherige Seite");
        btnPrev.setOnAction(event -> {
            switch (aktState) {
                case START_1:
                    break;
                case START_2:
                    aktState = State.START_1;
                    break;
                case UPDATE:
                    aktState = State.START_2;
                    break;
                case COLOR:
                    aktState = State.UPDATE;
                    break;
                case FILM:
                    aktState = State.COLOR;
                    break;
                case PATH:
                    aktState = State.FILM;
                    break;
            }
            selectActPane();
        });

        addOkCancelButtons(btnOk, btnCancel);
        ButtonBar.setButtonData(btnPrev, ButtonBar.ButtonData.BACK_PREVIOUS);
        ButtonBar.setButtonData(btnNext, ButtonBar.ButtonData.NEXT_FORWARD);
        addAnyButton(btnNext);
        addAnyButton(btnPrev);
        getButtonBar().setButtonOrder("BX+CO");
    }

    private void selectActPane() {
        switch (aktState) {
            case START_1:
                btnPrev.setDisable(true);
                btnNext.setDisable(false);
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(startPane1);
                setButtonStyle(btnStart1);
                break;
            case START_2:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(startPane2);
                setButtonStyle(btnStart2);
                break;
            case UPDATE:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(startPaneUpdate);
                setButtonStyle(btnUpdate);
                break;
            case COLOR:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(startPaneColor);
                setButtonStyle(btnColor);
                break;
            case FILM:
                btnPrev.setDisable(false);
                btnNext.setDisable(false);
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(startPaneFilm);
                setButtonStyle(btnFilm);
                break;
            case PATH:
                btnPrev.setDisable(false);
                btnNext.setDisable(true);
                btnOk.setDisable(false);
                vBoxCont.getChildren().clear();
                vBoxCont.getChildren().add(startPaneAudio);
                setButtonStyle(btnPath);
                break;
            default:
                btnOk.setDisable(false);
        }
    }

    private void setButtonStyle(Button btnSel) {
        btnStart1.getStyleClass().setAll("btnStartDialog");
        btnStart2.getStyleClass().setAll("btnStartDialog");
        btnUpdate.getStyleClass().setAll("btnStartDialog");
        btnColor.getStyleClass().setAll("btnStartDialog");
        btnFilm.getStyleClass().setAll("btnStartDialog");
        btnPath.getStyleClass().setAll("btnStartDialog");
        btnSel.getStyleClass().setAll("btnStartDialog", "btnStartDialogSel");
    }

    private void initTooltip() {
        btnStart1.setTooltip(new Tooltip("Infos über das Programm"));
        btnStart2.setTooltip(new Tooltip("Infos über das Programm"));
        btnUpdate.setTooltip(new Tooltip("Soll das Programm nach Updates suchen?"));
        btnColor.setTooltip(new Tooltip("Wie soll die Programmoberfläche aussehen?"));
        btnFilm.setTooltip(new Tooltip("Damit kann man die Größe der\n" +
                "Audioliste reduzieren und damit die Geschwindigkeit\n" +
                "des Programms auf langsamen Rechnern verbessern"));
        btnPath.setTooltip(new Tooltip("Angabe von Programmen zum Anhören\n" +
                "und Speichern der Audios"));

        btnOk.setTooltip(new Tooltip("Programm mit den gewählten Einstellungen starten"));
        btnCancel.setTooltip(new Tooltip("Das Programm nicht einrichten\n" +
                "und starten sondern Dialog wieder beenden"));
        btnNext.setTooltip(new Tooltip("Nächste Einstellmöglichkeit"));
        btnPrev.setTooltip(new Tooltip("Vorherige Einstellmöglichkeit"));
    }

    private enum State {START_1, START_2, UPDATE, COLOR, FILM, PATH}
}
