package com.example.sqlite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
	@SuppressLint("SdCardPath") 
	private static String DB_PATH = "/data/data/com.example.lyfaultdiagnosissystem/databases/";
	private static String DB_NAME = "LYFDSData.db";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	public MyDatabaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	/**
	 * 创建数据库
	 * 
	 * @throws IOException
	 */
	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			// 数据库已经存在
		} else {
			// 将assets中的数据库复制进来
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}

	}

	/**
	 * 检查数据库是否已经存在
	 * 
	 * @return
	 */
	private boolean checkDataBase() {
		File dbFile=myContext.getDatabasePath(DB_NAME);
		return dbFile.exists();
	}

	/**
	 * 将assets中的数据库文件复制过来
	 * 
	 * @throws IOException
	 */
	private void copyDataBase() throws IOException {
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException {
		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);
	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
