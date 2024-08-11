package de.p2tools.atplayer.gui.tools.table;

import de.p2tools.atplayer.controller.audio.AudioTools;
import de.p2tools.atplayer.controller.config.ProgColorList;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.controller.data.audiodata.AudioData;
import de.p2tools.atplayer.controller.data.audiodata.AudioSize;
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

                final HBox hbox = new HBox();
                hbox.setSpacing(4);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final Button btnPlay;
                final Button btnSave;
                final Button btnBookmark;

                btnPlay = new Button("");
                btnPlay.getStyleClass().addAll("btnFunction", "btnFuncTable");
                btnPlay.setGraphic(ProgIcons.ICON_TABLE_FILM_PLAY.getImageView());

                btnSave = new Button("");
                btnSave.getStyleClass().addAll("btnFunction", "btnFuncTable");
                btnSave.setGraphic(ProgIcons.ICON_TABLE_FILM_SAVE.getImageView());

                btnBookmark = new Button("");
                btnBookmark.getStyleClass().addAll("btnFunction", "btnFuncTable");
                btnBookmark.setGraphic(ProgIcons.ICON_TABLE_FILM_BOOKMARK.getImageView());

                btnPlay.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    AudioData audioData = getTableView().getItems().get(getIndex());
                    AudioTools.playAudio(audioData);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });
                btnSave.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    AudioData audioData = getTableView().getItems().get(getIndex());
                    AudioTools.saveAudio(audioData);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });
                btnBookmark.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    AudioData audioData = getTableView().getItems().get(getIndex());
                    AudioTools.changeBookmarkFilm(audioData);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });
                hbox.getChildren().addAll(btnPlay, btnSave, btnBookmark);
                setGraphic(hbox);

                AudioData audioData = getTableView().getItems().get(getIndex());
                set(audioData, this);
            }
        });
    }

    public static void set(AudioData audioData, TableCell tableCell) {
//            if (film.isNewAudio()) {
//                for (int i = 0; i < getChildren().size(); i++) {
//                    getChildren().get(i).setStyle(ProgColorList.AUDIO_NEW.getCssFont());
//                }
//
//            } else {
//                for (int i = 0; i < getChildren().size(); i++) {
//                    getChildren().get(i).setStyle("");
//                }
//            }
//            if (film.isBookmark()) {
//                setStyle(ProgColorList.AUDIO_BOOKMARK.getCssBackground());
//
//            } else if (film.isShown()) {
//                setStyle(ProgColorList.AUDIO_HISTORY.getCssBackground());
//
//            } else {
//                setStyle("");
//            }
//        }

        if (audioData.isNewAudio()) {
            // neuer Film
            tableCell.setStyle(ProgColorList.AUDIO_NEW.getCssFont());
        } else {
            tableCell.setStyle("");
        }
    }
}
