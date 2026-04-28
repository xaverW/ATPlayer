package de.p2tools.atplayer.gui.tools.table;

import de.p2tools.atplayer.controller.audio.AudioPlayFactory;
import de.p2tools.atplayer.controller.audio.AudioSaveFactory;
import de.p2tools.atplayer.controller.audio.AudioToolsFactory;
import de.p2tools.atplayer.controller.config.ProgColorList;
import de.p2tools.atplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.mediathek.audio.AudioSize;
import de.p2tools.p2lib.mediathek.audiodata.AudioData;
import de.p2tools.p2lib.tools.date.P2Date;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;

public class TableAudioFactory {
    private TableAudioFactory() {

    }

    public static void columnFactoryString(TableColumn<AudioData, String> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(item);
                AudioData film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryInteger(TableColumn<AudioData, Integer> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                if (item == 0) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(item + "");
                }

                AudioData film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryBoolean(TableColumn<AudioData, Boolean> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                setAlignment(Pos.CENTER);
                CheckBox box = new CheckBox();
                box.setMaxHeight(6);
                box.setMinHeight(6);
                box.setPrefSize(6, 6);
                box.setDisable(true);
                box.getStyleClass().add("checkbox-table");
                box.setSelected(item);
                setGraphic(box);

                AudioData film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryP2Date(TableColumn<AudioData, P2Date> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(P2Date item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(item.toString());
                AudioData film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryFilmSize(TableColumn<AudioData, AudioSize> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(AudioSize item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(item.toString());
                AudioData film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryButton(TableColumn<AudioData, String> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                AudioData audioData = getTableView().getItems().get(getIndex());
                final HBox hbox = new HBox();
                hbox.setSpacing(4);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final Button btnPlay;
                final Button btnSave;
                final Button btnBookmark;

                btnPlay = new Button("");
                btnPlay.getStyleClass().addAll("pFuncBtn", "btnTable");
                btnPlay.setGraphic(PIconFactory.PICON.TABLE_FILM_PLAY.getFontIcon());

                btnSave = new Button("");
                btnSave.getStyleClass().addAll("pFuncBtn", "btnTable");
                btnSave.setGraphic(PIconFactory.PICON.TABLE_FILM_SAVE.getFontIcon());

                btnBookmark = new Button("");
                btnBookmark.getStyleClass().addAll("pFuncBtn", "btnTable");
                if (audioData.isBookmark()) {
                    btnBookmark.setGraphic(PIconFactory.PICON.TABLE_BOOKMARK_DEL.getFontIcon());
                } else {
                    btnBookmark.setGraphic(PIconFactory.PICON.TABLE_BOOKMARK_ADD.getFontIcon());
                }

                btnPlay.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    AudioPlayFactory.playAudio(audioData);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });
                btnSave.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    AudioSaveFactory.saveAudio(audioData);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });
                btnBookmark.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    AudioToolsFactory.changeBookmarkFilm(audioData);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });
                btnPlay.setMaxHeight(Table.ROW_HEIGHT_MIN);
                btnPlay.setMinHeight(Table.ROW_HEIGHT_MIN);
                btnSave.setMaxHeight(Table.ROW_HEIGHT_MIN);
                btnSave.setMinHeight(Table.ROW_HEIGHT_MIN);
                btnBookmark.setMaxHeight(Table.ROW_HEIGHT_MIN);
                btnBookmark.setMinHeight(Table.ROW_HEIGHT_MIN);
                hbox.getChildren().addAll(btnPlay, btnSave, btnBookmark);
                setGraphic(hbox);

                set(audioData, this);
            }
        });
    }

    public static void set(AudioData audioData, TableCell tableCell) {
        if (audioData.isNewAudio()) {
            // neuer Film
            tableCell.setStyle(ProgColorList.AUDIO_NEW.getCssFont());
        } else {
            tableCell.setStyle("");
        }
    }
}
