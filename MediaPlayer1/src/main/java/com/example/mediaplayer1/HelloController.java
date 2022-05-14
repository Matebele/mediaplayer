package com.example.mediaplayer1;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class HelloController implements Initializable{

    @FXML
    private VBox vBoxPane;

    @FXML
    private MediaView mvVideo;
    private MediaPlayer mpVideo;
    private Media mediaVideo;

    @FXML
    private HBox hboxControls;

    @FXML
    private HBox hBoxV;

    @FXML
    private Button buttonPP;
    @FXML
    private Button buttonS;

    @FXML
    private Label labelcurrentTime;
    @FXML
    private Label labeltotalTime;
    @FXML
    private Label labelV;

    @FXML
    private Slider sliderV;
    @FXML
    private Slider sliderTime;

    private Boolean atEndOfVideo = false;
    private Boolean isPlaying = true;
    private Boolean isMuted = true;

    private ImageView iplay;
    private ImageView ipause;
    private ImageView istop;
    private ImageView ivolume;
    private ImageView imute;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final int IV_SIZE = 25;
        String vid = getClass().getResource("/table.mp4").toExternalForm();
        mediaVideo = new Media(vid);
        mpVideo = new MediaPlayer(mediaVideo);
        mvVideo.setMediaPlayer(mpVideo);
        mpVideo.play();
        mvVideo.setFitWidth(700);
        mvVideo.setFitHeight(600);

        Image imagePlay = new Image(new File("play.png").toURI().toString());
        iplay = new ImageView(imagePlay);
        iplay.setFitWidth(IV_SIZE);
        iplay.setFitWidth(IV_SIZE);

        Image imagePause = new Image(new File("pause.png").toURI().toString());
        ipause = new ImageView(imagePause);
        ipause.setFitWidth(IV_SIZE);
        ipause.setFitWidth(IV_SIZE);

        Image imageStop = new Image(new File("stop.png").toURI().toString());
        istop = new ImageView(imageStop);
        istop.setFitWidth(IV_SIZE);
        istop.setFitWidth(IV_SIZE);

        Image imageVolume = new Image(new File("volume.png").toURI().toString());
        ivolume = new ImageView(imageVolume);
        ivolume.setFitWidth(IV_SIZE);
        ivolume.setFitWidth(IV_SIZE);

        Image imageMute = new Image(new File("mute.png").toURI().toString());
        imute = new ImageView(imageMute);
        imute.setFitWidth(IV_SIZE);
        imute.setFitWidth(IV_SIZE);

        buttonPP.setGraphic(ipause);
        labelV.setGraphic(imute);

        hBoxV.getChildren().remove(sliderV);

        mpVideo.volumeProperty().bindBidirectional(sliderV.valueProperty());

        bindCurrentTimeLabel();

        buttonPP.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button buttonPlay = (Button) actionEvent.getSource();
                if (atEndOfVideo){
                    sliderTime.setValue(0);
                    atEndOfVideo = false;
                    isPlaying = false;
                }
                if (isPlaying){
                    buttonPlay.setGraphic(iplay);
                    mpVideo.pause();
                    isPlaying = false;
                }else {
                    buttonPlay.setGraphic(ipause);
                    mpVideo.play();
                    isPlaying = true;
                }
            }
        });
        sliderV.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mpVideo.setVolume(sliderV.getValue());
                if (mpVideo.getVolume() != 0.0){
                    labelV.setGraphic(ivolume);
                    isMuted = false;
                }else {
                    labelV.setGraphic(imute);
                    isMuted = true;
                }
            }
        });
        labelV.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (isMuted){
                    labelV.setGraphic(ivolume);
                    sliderV.setValue(0.2);
                    isMuted = false;
                }else {
                    labelV.setGraphic(imute);
                    sliderV.setValue(0);
                    isMuted = true;
                }
            }
        });
        labelV.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (hBoxV.lookup("sliderVolume") == null ){
                    hBoxV.getChildren().add(sliderV);
                    sliderV.setValue(mpVideo.getVolume());
                }
            }
        });
        hBoxV.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                hBoxV.getChildren().remove(sliderV);
            }
        });
        vBoxPane.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observableValue, Scene oldScene, Scene newScene) {

                if (oldScene == null && newScene != null){
                    mvVideo.fitHeightProperty().bind(newScene.heightProperty().subtract(hboxControls.heightProperty().add(20)));
                }
            }
        });
        mpVideo.totalDurationProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
                sliderTime.setMax(newDuration.toSeconds());
                labeltotalTime.setText(getTime(newDuration));
                sliderTime.setValue(40);
            }
        });
        sliderTime.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasChanging, Boolean isChanging) {
                if (isChanging){
                    mpVideo.seek(Duration.seconds(sliderTime.getValue()));
                }
            }
        });
        sliderTime.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                double currentTime = mpVideo.getCurrentTime().toSeconds();
                if (Math.abs(currentTime - newValue.doubleValue()) > 0.5){
                    mpVideo.seek(Duration.seconds(newValue.doubleValue()));

                }
                labelMatchEndVideo(labelcurrentTime.getText(),labeltotalTime.getText());
            }
        });
        mpVideo.totalDurationProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldTime, Duration newTime) {
                if (!sliderTime.isValueChanging()){
                    sliderTime.setValue(newTime.toSeconds());
                }
                labelMatchEndVideo(labelcurrentTime.getText(),labeltotalTime.getText());
            }
        });
        mpVideo.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                atEndOfVideo = true;
                if (!labelcurrentTime.textProperty().equals(labeltotalTime.textProperty())){
                    labelcurrentTime.textProperty().unbind();
                    labelcurrentTime.setText(getTime(mpVideo.getTotalDuration()) + " / ");
                }
            }
        });
    }
    public void bindCurrentTimeLabel(){
        labelcurrentTime.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getTime(mpVideo.getCurrentTime()) + " / ";
            }
        },mpVideo.currentCountProperty()));
    }
    public String getTime(Duration time){
        int hours = (int) time.toHours();
        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        if (seconds>59) seconds = seconds % 60;
        if (minutes>59) minutes = minutes % 60;
        if (hours>59) hours = hours % 60;

        if (hours>0) return String.format("%d:%02d:%02d",
                hours,
                minutes,
                seconds);
        else return String.format("%02d:%02d",
                minutes,
                seconds);

    }

    public void labelMatchEndVideo(String labelTime, String labelTotalTime){

        for (int i =0;i< labelTotalTime.length();i++){
            if (labelTime.charAt(i) != labelTotalTime.charAt(i)){
                atEndOfVideo = false;
                if (isPlaying) buttonPP.setGraphic(ipause);
                else  buttonPP.setGraphic(iplay);
                break;
            }
        }
    }
}
