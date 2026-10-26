package com.example.midcase1;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText etName, etId;
    Spinner spinnerMajor;
    Button btnAdd, btnEdit, btnDelete, btnView;
    ListView listView;

    String selectedMajor = "";
    String selectedIdInDb = null;
    boolean isListVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);

        etName = findViewById(R.id.etStudentName);
        etId = findViewById(R.id.etStudentId);
        spinnerMajor = findViewById(R.id.spinnerMajor);
        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnView = findViewById(R.id.btnView);
        listView = findViewById(R.id.listViewStudents);

        String[] majors = {"Informatics", "Information Systems"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, majors);
        spinnerMajor.setAdapter(adapter);

        spinnerMajor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMajor = majors[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMajor = "Informatics";
            }
        });

        addData();
        viewData();
        updateData();
        deleteData();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedText = (String) parent.getItemAtPosition(position);
            try {
                String[] parts = selectedText.split(" \\| ");
                selectedIdInDb = parts[0].replace("ID: ", "").trim();

                String[] subParts = parts[1].split(" - ");
                String studentId = subParts[0];
                String nameAndMajor = subParts[1];

                String studentName = nameAndMajor.substring(0, nameAndMajor.lastIndexOf(" ("));
                String major = nameAndMajor.substring(nameAndMajor.lastIndexOf("(") + 1, nameAndMajor.lastIndexOf(")"));

                etId.setText(studentId);
                etName.setText(studentName);

                if (major.equals("Informatics")) {
                    spinnerMajor.setSelection(0);
                } else {
                    spinnerMajor.setSelection(1);
                }

                Toast.makeText(MainActivity.this, "Dipilih untuk diedit/dihapus", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void addData() {
        btnAdd.setOnClickListener(v -> {
            String studentId = etId.getText().toString().trim();
            String studentName = etName.getText().toString().trim();

            if(studentId.isEmpty() || studentName.isEmpty()) {
                Toast.makeText(MainActivity.this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = myDb.insertData(studentId, studentName, selectedMajor);
            if (isInserted) {
                Toast.makeText(MainActivity.this, "Data Berhasil Ditambahkan", Toast.LENGTH_SHORT).show();
                clearForm();
                // Jika list sedang terbuka, update otomatis tampilannya
                if (isListVisible) {
                    refreshListOnly();
                }
            } else {
                Toast.makeText(MainActivity.this, "Gagal! Student ID harus Unik (sudah terdaftar).", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void viewData() {
        btnView.setOnClickListener(v -> {
            if (isListVisible) {
                listView.setAdapter(null);
                isListVisible = false;
                Toast.makeText(MainActivity.this, "List ditutup", Toast.LENGTH_SHORT).show();
            } else {
                Cursor res = myDb.getAllData();
                if (res.getCount() == 0) {
                    Toast.makeText(MainActivity.this, "Tidak ada data mahasiswa", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<String> listData = new ArrayList<>();
                while (res.moveToNext()) {
                    String item = "ID: " + res.getString(0) + " | " +
                            res.getString(1) + " - " +
                            res.getString(2) + " (" +
                            res.getString(3) + ")";
                    listData.add(item);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
                listView.setAdapter(adapter);
                isListVisible = true;
                Toast.makeText(MainActivity.this, "Data berhasil ditampilkan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshListOnly() {
        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            listView.setAdapter(null);
            return;
        }

        ArrayList<String> listData = new ArrayList<>();
        while (res.moveToNext()) {
            String item = "ID: " + res.getString(0) + " | " +
                    res.getString(1) + " - " +
                    res.getString(2) + " (" +
                    res.getString(3) + ")";
            listData.add(item);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(adapter);
    }

    public void updateData() {
        btnEdit.setOnClickListener(v -> {
            if (selectedIdInDb == null) {
                Toast.makeText(MainActivity.this, "Pilih data dari list terlebih dahulu (klik View lalu pilih item)!", Toast.LENGTH_LONG).show();
                return;
            }

            boolean isUpdate = myDb.updateData(
                    selectedIdInDb,
                    etId.getText().toString().trim(),
                    etName.getText().toString().trim(),
                    selectedMajor
            );

            if (isUpdate) {
                Toast.makeText(MainActivity.this, "Data Berhasil Diperbarui", Toast.LENGTH_SHORT).show();
                clearForm();
                if (isListVisible) {
                    refreshListOnly();
                }
            } else {
                Toast.makeText(MainActivity.this, "Gagal Memperbarui Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteData() {
        btnDelete.setOnClickListener(v -> {
            if (selectedIdInDb == null) {
                Toast.makeText(MainActivity.this, "Pilih data dari list terlebih dahulu (klik View lalu pilih item)!", Toast.LENGTH_LONG).show();
                return;
            }

            Integer deletedRows = myDb.deleteData(selectedIdInDb);
            if (deletedRows > 0) {
                Toast.makeText(MainActivity.this, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                clearForm();
                if (isListVisible) {
                    refreshListOnly();
                }
            } else {
                Toast.makeText(MainActivity.this, "Gagal Menghapus Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearForm() {
        etId.setText("");
        etName.setText("");
        selectedIdInDb = null;
        spinnerMajor.setSelection(0);
    }
}