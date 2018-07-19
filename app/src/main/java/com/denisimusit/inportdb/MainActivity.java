package com.denisimusit.inportdb;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String LOG_TAG = "My log";
    Button btnAdd, btnRead, btnClear;
    EditText etName, etEmail, etSurname;
    TextView textViewContentsTable;
    DataBaseHelper myDbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRead = (Button) findViewById(R.id.btnRead);
        btnRead.setOnClickListener(this);

        textViewContentsTable = (TextView) findViewById(R.id.textViewContentsTable);

        myDbHelper = new DataBaseHelper(this);


    }




    @Override
    public void onClick(View v) {

        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }


        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }





        Log.d(LOG_TAG, "--- Rows in mytable: ---");
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor cursor = myDbHelper.query("users", null, null,
                null, null, null, null);

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (cursor.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idIndex = cursor.getColumnIndex("_id");
            int photoIndex = cursor.getColumnIndex("photo");
            int nameIndex = cursor.getColumnIndex("name");
            int surnameIndex = cursor.getColumnIndex("surname");

            do {

                // получаем значения по номерам столбцов
                String msg = "ID = " + cursor.getInt(idIndex) +
                        ", photo = " + cursor.getString(photoIndex) +
                        ", name = " + cursor.getString(nameIndex) +
                        ", email = " + cursor.getString(surnameIndex);
                //пишем все в лог
                Log.d(LOG_TAG, msg);

                //выодим все на екран
                // textViewContentsTable.setText("");
                textViewContentsTable.append(msg + "\n");


            } while (cursor.moveToNext());
        } else {
            Log.d("mLog", "0 rows");
            textViewContentsTable.setText("0 rows");

        }
        // закрываем подключение к Curcor
        cursor.close();
    }
}
