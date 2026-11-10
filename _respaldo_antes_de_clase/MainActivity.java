package com.example.myapplication;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText txtNombre, txtCorreo, txtEdad;
    DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNombre = findViewById(R.id.txtNombre);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtEdad = findViewById(R.id.textEdad);
        Button btnCrear = findViewById(R.id.create);
        Button btnConsultar = findViewById(R.id.select);
        Button btnModificar = findViewById(R.id.update);
        Button btnEliminar = findViewById(R.id.delete);

        helper = new DBHelper(this, "escuela.db", null, 1);

        btnCrear.setOnClickListener(v -> crear());
        btnConsultar.setOnClickListener(v -> consultar());
        btnModificar.setOnClickListener(v -> modificar());
        btnEliminar.setOnClickListener(v -> eliminar());
    }

    private void crear() {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", txtNombre.getText().toString());
        valores.put("correo", txtCorreo.getText().toString());
        valores.put("edad", Integer.parseInt(txtEdad.getText().toString()));
        long id = db.insert("estudiante", null, valores);
        db.close();
        mensaje(id != -1 ? "Estudiante guardado" : "Error al guardar");
        limpiar();
    }

    private void consultar() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT nombre, edad FROM estudiante WHERE correo = ?",
                new String[]{txtCorreo.getText().toString()});
        if (c.moveToFirst()) {
            txtNombre.setText(c.getString(0));
            txtEdad.setText(String.valueOf(c.getInt(1)));
        } else {
            mensaje("No existe ese estudiante");
        }
        c.close();
        db.close();
    }

    private void modificar() {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", txtNombre.getText().toString());
        valores.put("edad", Integer.parseInt(txtEdad.getText().toString()));
        int filas = db.update("estudiante", valores, "correo = ?",
                new String[]{txtCorreo.getText().toString()});
        db.close();
        mensaje(filas > 0 ? "Estudiante modificado" : "No existe ese estudiante");
    }

    private void eliminar() {
        SQLiteDatabase db = helper.getWritableDatabase();
        int filas = db.delete("estudiante", "correo = ?",
                new String[]{txtCorreo.getText().toString()});
        db.close();
        mensaje(filas > 0 ? "Estudiante eliminado" : "No existe ese estudiante");
        limpiar();
    }

    private void limpiar() {
        txtNombre.setText("");
        txtCorreo.setText("");
        txtEdad.setText("");
    }

    private void mensaje(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}