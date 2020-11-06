package service;

import com.sun.speech.freetts.VoiceManager;
import entity.Word;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Service {
    public Connection connection;

    public static Service instance;

    public Service(Connection connection) {
        this.connection = connection;
    }

    public Service() {
    }

    public static Service gI() {
        if (Service.instance == null) {
            Service.instance = new Service();
        }
        return Service.instance;
    }

    public boolean checkWordInData(String word_target) {
        try {
            Statement statement = connection.createStatement();
            String url = "SELECT * FROM `dictionary` WHERE `word`='" + word_target + "'";
            ResultSet resultSet = statement.executeQuery(url);
            if (resultSet.next()) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("" + e);
        }
        return false;
    }

    public void loadMySQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Not found!");
            return;
        }
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/btl_dictionary?characterEncoding=UTF-8&autoReconnect"
                    + "=true&connectTimeout=30000&socketTimeout=30000&serverTimezone=UTC";
            connection = DriverManager
                    .getConnection(url, "root", "thanh@2110");
            System.out.println("");
            instance = new Service(connection);

        } catch (SQLException e) {
            System.out.println("Connection Failed!");
            System.out.println(e);
            return;
        }
    }

    public List<Word> searchWord(String word_target) {
        List<Word> listWords = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            String url = "SELECT * FROM `dictionary` WHERE `word`='" + word_target + "'";
            ResultSet resultSet = statement.executeQuery(url);
            while (resultSet.next()) {
                Word word = new Word(resultSet.getString(2), resultSet.getString(3));
                listWords.add(word);
            }

        } catch (Exception e) {
            System.out.println("" + e);
        }
        return listWords;
    }

    public List<Word> findByCharacter(String keyword) {
        List<Word> listWordTarget = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM `dictionary` WHERE `word` LIKE '" + keyword + "%' LIMIT 25";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Word rs = new Word(resultSet.getString(2), resultSet.getString(3));
                if (checkItemList(listWordTarget, rs)) {
                    listWordTarget.add(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("" + e);
        }
        return listWordTarget;
    }

    private boolean checkItemList(List<Word> listWords, Word word) {
        if (listWords.size() == 0) {
            return true;
        }
        for (int i = 0; i < listWords.size(); i++) {
            if (listWords.get(i).getWord_target().equals(word.getWord_target())
                    && listWords.get(i).getWord_explain().equals(word.getWord_explain())) {
                return false;
            }
        }
        return true;
    }

    public void speech(String text) {
        VoiceManager voiceManager = VoiceManager.getInstance();
        com.sun.speech.freetts.Voice syntheticVoice = voiceManager.getVoice("kevin16");
        syntheticVoice.allocate();
        syntheticVoice.speak(text);
        syntheticVoice.deallocate();
    }

    public void startMsgBox(String info) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(info);
        ButtonType buttonTypeOK = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(buttonTypeOK);
        alert.showAndWait();
    }

    public void insertWord(String word_target, String word_explain) {
        try {
            Statement statement = connection.createStatement();
            String sql = "INSERT INTO `dictionary`(`word`,`detail`) VALUES ('"
                    + word_target + "','" + word_explain + "') ";
            statement.execute(sql);
            this.startMsgBox("Đã thêm vào từ điển!");
        } catch (Exception e) {
            System.out.println("" + e);
        }
    }

    public void removeWord(String word_target) {
        try {
            Statement statement = connection.createStatement();
            statement.executeQuery("SET SQL_SAFE_UPDATES=0 ");
            String query = "DELETE FROM `btl_dictionary`.`dictionary` WHERE `word` ='" + word_target + "'";
            statement.executeUpdate(query);
            this.startMsgBox("Đã xóa khỏi từ điển!");
        } catch (Exception e) {
            System.out.println("" + e);
        }
    }

    public void fixWord(String word_target, String word_explain) {
        try {
            Statement statement = connection.createStatement();
            String url = "UPDATE `dictionary` SET `detail` ='" + word_explain + "' WHERE `word`='" + word_target + "'";
            statement.executeUpdate(url);
            this.startMsgBox("Đã sửa vào từ điển!");
        } catch (Exception e) {
            System.out.println("" + e);
        }
    }
}
