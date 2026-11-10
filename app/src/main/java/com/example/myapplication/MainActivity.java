package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.FeedReaderContract.FeedEntry;

public class MainActivity extends AppCompatActivity {

    private EditText txtid;
    private EditText txtnombre;
    private EditText txtapellido;
    private FeedReaderDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtid = findViewById(R.id.txtid);
        txtnombre = findViewById(R.id.txtnombre);
        txtapellido = findViewById(R.id.txtapellido);
        // El helper se crea cuando la Activity ya tiene un contexto valido
        dbHelper = new FeedReaderDbHelper(this);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    // ---------- Navegacion ----------

    public void Listar(View vista) {
        startActivity(new Intent(this, Listado.class));
    }

    // ---------- CREATE ----------

    public void Guardar(View vista) {
        String nombre = txtnombre.getText().toString().trim();
        String apellido = txtapellido.getText().toString().trim();
        if (nombre.isEmpty() || apellido.isEmpty()) {
            mostrarMensaje("Ingrese nombre y apellido");
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.column1, nombre);
        values.put(FeedReaderContract.FeedEntry.column2, apellido);
        long newRowId = db.insert(FeedReaderContract.FeedEntry.nameTable, null, values);
        if (newRowId == -1) {
            mostrarMensaje("No se pudo guardar el registro");
        } else {
            txtid.setText(String.valueOf(newRowId));
            mostrarMensaje("Se guardó el registro con clave: " + newRowId);
        }
    }

    // ---------- READ ----------

    public void Buscar(View vista) {
        String id = leerId();
        if (id == null) return;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.column1,
                FeedReaderContract.FeedEntry.column2};
        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {id};
        // try-with-resources cierra el Cursor automaticamente al terminar
        try (Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.nameTable, // tabla
                projection,                             // columnas a devolver
                selection,                              // WHERE _id = ?
                selectionArgs,                          // valor del ?
                null,                                   // GROUP BY
                null,                                   // HAVING
                null                                    // ORDER BY
        )) {
            if (cursor.moveToFirst()) {
                txtnombre.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column1)));
                txtapellido.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column2)));
            } else {
                txtnombre.setText("");
                txtapellido.setText("");
                mostrarMensaje("No existe un registro con Id "+ id);
            }
        }
    }

    // ---------- UPDATE ----------

    public void Actualizar(View vista) {
        String id = leerId();
        if (id == null) return;
        String nombre = txtnombre.getText().toString().trim();
        String apellido = txtapellido.getText().toString().trim();
        if (nombre.isEmpty() || apellido.isEmpty()) {
            mostrarMensaje("Ingrese nombre y apellido");
            return;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.column1, nombre);
        values.put(FeedReaderContract.FeedEntry.column2, apellido);
        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {id};
        int count = db.update(FeedReaderContract.FeedEntry.nameTable, values, selection, selectionArgs);
        mostrarMensaje("Se actualizó " + count + " registro(s)");
    }

    // MI VERSIÓN: Eliminar todavía no aparece en las capturas
    public void Eliminar(View vista) {
        String id = leerId();
        if (id == null) return;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filas = db.delete(FeedEntry.nameTable, FeedEntry._ID + " = ?",
                new String[]{id});
        mostrarMensaje(filas > 0 ? "Persona eliminada" : "No existe una persona con ese ID");
        limpiar();
    }

    // MI VERSIÓN: leerId() no aparece en las capturas
    private String leerId() {
        String id = txtid.getText().toString().trim();
        if (id.isEmpty()) {
            mostrarMensaje("Ingrese el id");
            return null;
        }
        return id;
    }

    private void limpiar() {
        txtid.setText("");
        txtnombre.setText("");
        txtapellido.setText("");
    }

    private void mostrarMensaje(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}
