package com.appgoogle.Diego;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

        // Nombre de la base de datos
        private static final String DATABASE_NAME = "app_maps.db";
        // Versión de la base de datos
        private static final int DATABASE_VERSION = 1;


        // Nombre de la tabla y columnas
        private static final String TABLE_USERS = "usuarios";
        private static final String COLUMN_ID = "id";
        private static final String COLUMN_USER = "user";
        private static final String COLUMN_PASS = "pass";



        // Constructor
        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Crear tabla
            String CREATE_TABLE_USUARIOS = "CREATE TABLE usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user TEXT, " +
                    "pass TEXT)";

            String CREATE_TABLE_GUARDADOS = "CREATE TABLE guardados (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "lugar TEXT, " +
                    "direccion TEXT, " +
                    "coordenadas TEXT)";

            db.execSQL(CREATE_TABLE_USUARIOS);
            // Insertar usuario de ejemplo
            db.execSQL("INSERT INTO usuarios (user, pass) VALUES ('diego', 'levano')");

            db.execSQL(CREATE_TABLE_GUARDADOS);
            // Insertar usuario de ejemplo
            db.execSQL("INSERT INTO guardados (lugar, direccion, coordenadas) VALUES ('Lima', 'av. ejemplo mz 10 lt130' , '983828381, 882832812')");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Eliminar tabla anterior si existe y crear una nueva
            db.execSQL("DROP TABLE IF EXISTS usuarios");
            db.execSQL("DROP TABLE IF EXISTS guardados");
            onCreate(db);
        }

        // Método para autenticar usuario
        public boolean autenticarUsuario(String user, String pass) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM usuarios WHERE user = ? AND pass = ?";
            Cursor cursor = db.rawQuery(query, new String[]{user, pass});
            boolean existe = cursor.getCount() > 0;
            cursor.close();
            db.close();
            return existe;
        }

        // Método para registrar un nuevo usuario
        public boolean registrarUsuario(String user, String pass) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER, user);
            values.put(COLUMN_PASS, pass);

            // Verificar si el usuario ya existe
            Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
                    COLUMN_USER + "=?", new String[]{user}, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.close();
                db.close();
                return false; // Usuario ya existe
            }

            // Insertar nuevo usuario
            long result = db.insert(TABLE_USERS, null, values);
            cursor.close();
            db.close();
            return result != -1; // Devuelve true si la inserción fue exitosa
        }
}
