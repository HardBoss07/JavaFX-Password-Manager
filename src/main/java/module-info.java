module ch.bosshard.matteo.javapasswordmanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens ch.bosshard.matteo.javapasswordmanager to javafx.fxml;
    exports ch.bosshard.matteo.javapasswordmanager;
}