module com.example.mediaplayer1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires  javafx.media;


    opens com.example.mediaplayer1 to javafx.fxml;
    exports com.example.mediaplayer1;
}