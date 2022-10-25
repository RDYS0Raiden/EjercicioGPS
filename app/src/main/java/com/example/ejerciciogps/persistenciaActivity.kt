package com.example.ejerciciogps

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ejerciciogps.Constantes.FILE_NAME
import com.example.ejerciciogps.Constantes.KEY_HOBBY
import com.example.ejerciciogps.Constantes.KEY_VALORATION
import com.example.ejerciciogps.databinding.ActivityPersistenciaBinding

class persistenciaActivity : AppCompatActivity() {
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var editor:SharedPreferences.Editor

    private lateinit var binding: ActivityPersistenciaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPersistenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeSharedPreference()
        loadData()
        binding.btnGuardar.setOnClickListener{
            saveData()
            loadData()
        }
    }

    private fun initializeSharedPreference() {
        //Primero busca en el dispositivo un archivo con ese nombre
        //si no existe el archivo lo va a crear
        //pero si existe va a traer ese archivo
        sharedPreference=getSharedPreferences(FILE_NAME, MODE_PRIVATE)
        //considerar tener una variable donde gestionen el archivo en modo de escritura
        editor=sharedPreference.edit()
    }

    //Guardar datos en el archivo
    private fun saveData(){
        //En los Shared Preferences la informacion se guarda en formato de registros
        //cada registro se guarda en formato LLAVE-VALOR
        val myHobby = binding.etHobby.text.toString()
        //Cuando gurada primero barre el archivo y busca si existe ya esa llave
        //si ya existe en ese registro va a reemplazar el valor
        //si no existe recien va a crear el registro
        editor.apply{
            putString(KEY_HOBBY, myHobby)
            putInt(KEY_VALORATION, 100)
        }.apply()
        //1) usar apply(): es un guardado asincrono
        //2) usar commit(): es un guardado sincrono
    }

    private fun loadData(){
        val myHobby = sharedPreference.getString(KEY_HOBBY, "vacio")
        val myValor = sharedPreference.getInt(KEY_VALORATION, 0)
        binding.txtResultado.text = "Mis datos son: $myHobby, $myValor ...."
    }
}