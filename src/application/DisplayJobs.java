package application;
	

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.cj.jdbc.MysqlDataSource;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.*;
import javafx.scene.control.*;


public class DisplayJobs extends Application {
	private TextArea ta = new TextArea();
	private Button btShowJobs = new Button("Show Records");
	private ComboBox<String> cboTableName = new ComboBox<>();
	
	private Statement stmt;
	@Override
	public void start(Stage primaryStage) {
		initializeDB(); //establish the database connection
		
		//display the job data
		btShowJobs.setOnAction(e-> showData());
		
		HBox hBox = new HBox(10);
		hBox.getChildren().addAll(new Label("Table Name"),cboTableName,btShowJobs);
		hBox.setAlignment(Pos.CENTER);
		
		BorderPane bpane = new BorderPane();
		bpane.setCenter(new ScrollPane(ta));
		bpane.setTop(hBox);
		
		Scene scene = new Scene(bpane,420,180);
		primaryStage.setTitle("Display Job Information");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void initializeDB() {
		try {
			//add code that does the following
			//1.membuat koneksi ke database oracle
			String user = "root";
			String password = "root";
			String Url = "jdbc:mysql://localhost:3306/hr?&serverTimezone=UTC";
			
			//inisiasi Connection
			MysqlDataSource ds = new MysqlDataSource();
			ds.setUrl(Url);
			Connection con = ds.getConnection(user,password);
			
			//2.Gunakan Connection untuk membuat statement
			stmt = con.createStatement();
			
			//3.Gunakan database MetaData untuk membangun ResultSet berdasarkan table 
			//yang ada di kata Job
			DatabaseMetaData dbMetaData = con.getMetaData();
			ResultSet rs = dbMetaData.getTables(null, null, "JOB%", new String[] {"TABLE"});
			
			//4.Tambahkan pengembalian nama table ke comboBox berdasarkan item pertama yang terpilih
			while(rs.next()) {
				cboTableName.getItems().add(rs.getString("TABLE_NAME"));
			}
			cboTableName.getSelectionModel().selectFirst();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	private void showData() {
		ta.clear();
		String tableName = cboTableName.getValue();
		try {
			//5.Membuat query yang akan melakukan SELECT dari table yang dipilih
			String query = "SELECT * FROM " + tableName;
			
			//6.Membuat ResultSet untuk menahan data dari query yang dieksekusi
			ResultSet rs = stmt.executeQuery(query);
			
			//7.Gunakan MetaData dari ResultSet untuk menampilkan kolom nama pada text
			ResultSetMetaData rsMetaData = rs.getMetaData();
			for(int i = 1; i <= rsMetaData.getColumnCount();i++) {
				ta.appendText(rsMetaData.getColumnName(i) + "\t");
			}
			ta.appendText("\n===============================================\n");
			
			while(rs.next()) {
			for(int i = 1;i <= rsMetaData.getColumnCount();i++) {
				ta.appendText(rs.getObject(i)+ "\t\t");
			}
			ta.appendText("\n");
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
