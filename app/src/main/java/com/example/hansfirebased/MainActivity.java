package com.example.hansfirebased;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etNombre, etTipo, etEdad;
    private Button btnEnviar, btnCargar;
    private ListView lvMascotas;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Enlazar variables con el XML
        etNombre = findViewById(R.id.etNombre);
        etTipo = findViewById(R.id.etTipo);
        etEdad = findViewById(R.id.etEdad);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnCargar = findViewById(R.id.btnCargar);
        lvMascotas = findViewById(R.id.lvMascotas);

        db = FirebaseFirestore.getInstance();

        btnEnviar.setOnClickListener(v -> enviarDatos());
        btnCargar.setOnClickListener(v -> cargarListaFirestore());
    }

    // Envía los datos del formulario a la colección "mascotas"
    private void enviarDatos() {
        String nombre = etNombre.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();

        if (nombre.isEmpty() || tipo.isEmpty() || edad.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> mascota = new HashMap<>();
        mascota.put("nombre", nombre);
        mascota.put("tipo", tipo);
        mascota.put("edad", Integer.parseInt(edad));

        db.collection("mascotas")
                .add(mascota)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show();
                    etNombre.setText("");
                    etTipo.setText("");
                    etEdad.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al enviar: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // Lee los documentos de Firestore y los muestra en el ListView
    private void cargarListaFirestore() {
        db.collection("mascotas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> lista = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            lista.add(doc.getString("nombre") + " - "
                                    + doc.getString("tipo") + " - "
                                    + doc.getLong("edad") + " años");
                        }
                        lvMascotas.setAdapter(new ArrayAdapter<>(this,
                                android.R.layout.simple_list_item_1, lista));
                    } else {
                        Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
