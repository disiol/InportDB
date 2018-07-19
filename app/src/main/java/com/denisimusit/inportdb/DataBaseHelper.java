package com.denisimusit.inportdb;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.res.AssetManager.ACCESS_BUFFER;

class DataBaseHelper extends SQLiteOpenHelper {

	// путь к базе данных вашего приложения
	private static String DB_PATH ;
	private static String DB_NAME = "company";
	private static String InputDb = "company";
	private SQLiteDatabase myDataBase;
	private final Context mContext;

	public static final String LOG_TAG = "My log";


	/**
	 * Конструктор
	 * Принимает и сохраняет ссылку на переданный контекст для доступа к ресурсам приложения
	 * @param context
	 */
	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		if(android.os.Build.VERSION.SDK_INT >= 4.2){
			DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
		} else {
			DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
		}
		this.mContext = context;
	}

	/**
	 * Создает пустую базу данных и перезаписывает ее нашей собственной базой
	 * */
	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();

		if(dbExist){
			//ничего не делать - база уже есть
		}else{
			//вызывая этот метод создаем пустую базу, позже она будет перезаписана
			this.getReadableDatabase();

			try {
				copyDataBase();
			} catch (IOException e) {
			    e.getStackTrace();
				throw new Error(String.format("Error copying database: %s",e.getCause().toString()));
			}
		}
	}

	/**
	 * Проверяет, существует ли уже эта база, чтобы не копировать каждый раз при запуске приложения
	 * @return true если существует, false если не существует
	 */
	private boolean checkDataBase(){
		SQLiteDatabase checkDB = null;

		try{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		}catch(SQLiteException e){
			//база еще не существует
		}
		if(checkDB != null){
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	/**
	 * Копирует базу из папки assets заместо созданной локальной БД
	 * Выполняется путем копирования потока байтов.
	 * */
	private void copyDataBase() throws IOException{
		//Открываем локальную БД как входящий поток
		
		InputStream myInput = mContext.getAssets().open(InputDb + ".db");
		Log.d(LOG_TAG,"myInput: " + myInput.toString());

		//Путь ко вновь созданной БД
		String outFileName = DB_PATH + DB_NAME;

		//Открываем пустую базу данных как исходящий поток
		OutputStream myOutput = new FileOutputStream(outFileName);

		//перемещаем байты из входящего файла в исходящий
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}

		//закрываем потоки
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException {
		//открываем БД
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	}

	@Override
	public synchronized void close() {
		if(myDataBase != null)
			myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return myDataBase.query(table, null, null, null, null, null, null);
	}
	// Здесь можно добавить вспомогательные методы для доступа и получения данных из БД
	// вы можете возвращать курсоры через "return myDataBase.query(....)", это облегчит их использование
	// в создании адаптеров для ваших view
}