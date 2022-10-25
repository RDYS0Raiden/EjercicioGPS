package com.example.ejerciciogps

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ejerciciogps.databinding.ActivityMainBinding
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {
    //OJITO: esta forma no es necesariamente
    //obligatoria de usar para este dato, solo es referencia
    //companion object se usa para definir constantes que seran globales en tu clase que sus valores son accedidos por cualquier instancia de esta
    companion object{
        val PERMISSION_GRANTED = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        )
    }

    private lateinit var binding: ActivityMainBinding

    //Nosotros usaremos un aheramienta de los servicios de Google
    private lateinit var fusedLocation: FusedLocationProviderClient
    private val PERMISSION_ID = 42
    private var isGpsEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.fabGps.setOnClickListener {
            enabledGPSService()
        }
        binding.fabCoordenadas.setOnClickListener {

        }
    }

    /**
    *Situacion: Configurar la habilitacion
     *          de GPS en el celular
     */
    private fun enabledGPSService() {
        if (!hasGPSEnabled()){
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_text_title)
                .setMessage(R.string.dialog_text_description)
                .setPositiveButton(R.string.dialog_button_accept,
                    DialogInterface.OnClickListener {
                            dialog, wich ->  goToEnabledGPS()
                    })
                .setNegativeButton(R.string.dialog_button_deny){
                        dialog, wich -> isGpsEnabled = false
                }

                .setCancelable(true)
                .show()
        }else{
            Toast.makeText(this,"El GPS ya esta activado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToEnabledGPS() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }
    private fun hasGPSEnabled(): Boolean {
        //Manager: Orquestador o director de la orquesta
        //      es el qeu lleva la batuta
        //      organiza y gestiona lo referido al manejo
        //      de cierto servicio o recurso
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    /**
     * Situacion: Configuracion u solicitud de permisos
     * en la APP para poder usar GPS
     */
    //evalua el valor que tienen
    //en tu app cierto permiso, no verifica si tienes o no permiso
    //solo ve que el valor numerico tiene asignado ese permiso en tu app
    //PERMISSION_GRANTED: es un valor numerico general en Android
    //que representan el valor que significa un permiso otorgado
    //Revisan si Android tiene como permiso otorgado, los permisos
    //que estan revisando en este metodo
    private fun allPermissionsGranted(): Boolean =
        PERMISSION_GRANTED.all {
            ActivityCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun checkPermission():Boolean{
        return ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
        android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionUser(){
        //Lanzar la ventana al usuario
        //para solicitarle que habilite permiso o los deniegue
        ActivityCompat.requestPermissions(
            this,
            PERMISSION_GRANTED,
            PERMISSION_ID
        )
    }

    /**
     * Situacion: obtencion de coordenadas
     * configuracion de objeto que trabaja con el sensor
     * y obtiene localizaciones llamado FusedLocation
     */

    @SuppressLint("MissingPermission")
    private fun manageLocation(){
        if(hasGPSEnabled()){
            if(allPermissionsGranted()){
                fusedLocation = LocationServices.getFusedLocationProviderClient(this)
                fusedLocation.lastLocation.addOnSuccessListener {
                    location -> getCoordinates()
                }
            }
        }else
            goToEnabledGPS()
    }

    @SuppressLint("MissingPermission")
    private fun getCoordinates() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }
        fusedLocation.requestLocationUpdates(
            locationRequest,
            myLocationCallback,
            Looper.myLooper()
        )
    }

    private val myLocationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (locationResult != null){
                var myLastLocation = locationResult.lastLocation
                binding.txtLatitud.text=locationResult.lastLocation!!.latitude.toString()
                binding.txtLongitud.text=locationResult.lastLocation!!.longitude.toString()
            }
        }

    }
}