package cn.edu.sdu.litong.whoere;

import java.io.Serializable;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Account implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	boolean isFind = false;
	Connection c = null;
	Statement stmt = null;
	private String ip;
	private int id=0;
	boolean isRight=false;

	public Account(String username, String password) {
		this.username = username;
		this.password = password;

	}

	public void setSocket(String ip) {
		this.ip = ip;
	}

	public void connect() {

		System.out.println(info() + "正在连接数据库......");
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:C:/sqlite/Whoere.db");
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println(info() + "数据库打开成功");
	}

	public String info() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "：" + ip;
	}

	public void match() {
		try {
			Class.forName("org.sqlite.JDBC");
		    c = DriverManager.getConnection("jdbc:sqlite:C:/sqlite/Whoere.db");

			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM ACCOUNT;");
			while (rs.next()) {
				int id = rs.getInt("id");
				String username = rs.getString("username");
				String password = rs.getString("password");
				if (username.equals(this.username)) {
					if (password.equals(this.password)) {
						System.out.println(info() + "用户名：" + username + "  密码正确");
						this.id=id;
						isRight=true;
					} else {
						System.out.println(info() + "用户名：" + username + "  密码错误");
					}
					isFind = true;
					break;
				}

			}
			if (isFind == false) {
				System.out.println(info() + "未找到" + username);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			
			e.printStackTrace();
			System.exit(0);
		}

	}

	public boolean existCheck() {
		try {
			Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:C:/sqlite/Whoere.db");

			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM ACCOUNT;");
			while (rs.next()) {
				
				String username = rs.getString("username");

				if (username.equals(this.username)) {
					return true;
				}

			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return false;
	}

	public void insert() {
		synchronized (this) {
			try {
				Class.forName("org.sqlite.JDBC");
			      c = DriverManager.getConnection("jdbc:sqlite:C:/sqlite/Whoere.db");

				c.setAutoCommit(false);
				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM ACCOUNT;");
				int i=0;
				while(rs.next()){
					i=rs.getInt(1);
				}
				
				int id = i + 1;
				this.id=id;
				String sql = "INSERT INTO ACCOUNT (ID,USERNAME,PASSWORD) " + "VALUES (" + id + ", '" + username + "', '"
						+ password + "' );";
				stmt.executeUpdate(sql);
				rs.close();
				stmt.close();
			    c.commit();
			    c.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		
	}
	public void upadteLocation(String data){
		synchronized (this) {
			try {
				Class.forName("org.sqlite.JDBC");
			    c = DriverManager.getConnection("jdbc:sqlite:C:/sqlite/Whoere.db");

				c.setAutoCommit(false);
				stmt = c.createStatement();
				String sql = "UPDATE ACCOUNT set LOCATION = '"+data+"' where ID="+id+";";
			    stmt.executeUpdate(sql);
			    c.commit();
			    stmt.close();
			    c.close();
			    System.out.println(this.username+"："+data);

			
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		
	}
	public Object getLocation(){
		ArrayList<HashMap<String, String>> list=new ArrayList<HashMap<String,String>>();
		try {
			Class.forName("org.sqlite.JDBC");
		    c = DriverManager.getConnection("jdbc:sqlite:C:/sqlite/Whoere.db");

			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM ACCOUNT;");
			while (rs.next()) {
				HashMap<String, String> map=new HashMap<>();
				map.put("1",rs.getString(2));
				map.put("2",rs.getString(4));
				list.add(map);
			}
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);}
		
		return list;
		
		
		
	}
	public String getUsername(){
		return username;
	}
}
