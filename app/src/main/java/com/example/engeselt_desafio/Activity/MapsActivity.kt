package com.example.engeselt_desafio.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.engeselt_desafio.R
import com.example.engeselt_desafio.db.DataBase
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*


private const val PERMISSION_REQUEST = 10
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private lateinit var mMap: GoogleMap
    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        disableView()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission(permissions)){
                enableView()
            }else{
                requestPermissions(permissions,
                    PERMISSION_REQUEST
                )
            }
        }else{
            enableView()
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        val context = this
        var db = DataBase(context)

        //Pega todos dados do DataBase
        var data = db.getAll()

        //Realiza o loop para adicionar marker gravados no Banco de Dados
        for(i in 0 until data.size){

            val locationDB = LatLng(data[i].latitudePoint, data[i].longitudePoint)
            mMap.addMarker(MarkerOptions().position(locationDB).title(data[i].id.toString()).snippet(data[i].namePoint))
        }

        //pega localidade atual
        getLocation()

        //adiciona o marker da localidade atual
        val location = LatLng(locationGps!!.latitude, locationGps!!.longitude)
        mMap.addMarker(MarkerOptions().position(location).title("Você está aqui!"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,15.0f))


        //Ação ao apertar na marcação no mapa
        mMap.setOnMarkerClickListener { marker: Marker ->

            //Se apertar no marker que o usuário está vai retornar o aviso
            //pois para adicionar o local é necessário acessar pelo botão flutuante "+"
            //caso não seja o marker atual, envia valores para proxima tela para edição ou delete
            if(marker.title != "Você está aqui!"){
            val intent = Intent(this, UpdateDeleteActivity::class.java)

            //Enviando valores para a proxima activity
            intent.putExtra("id", marker.title)
            intent.putExtra("namePoint", marker.snippet)
            intent.putExtra("latitude",marker.position.latitude)
            intent.putExtra("longitude",marker.position.longitude)
            startActivity(intent)
            }else{
                Toast.makeText(this@MapsActivity,"Você está aqui!", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    //Realizar o check de Permissão para buscar posição atual
    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    //só mostra o botão de flutuante caso a permissão for aceita
    private fun disableView() {
        floatingActionButton.isEnabled = false
    }

    private fun enableView() {
        floatingActionButton.isEnabled = true
        floatingActionButton.alpha = 1F
        floatingActionButton.setOnClickListener { openNextActivityForm()}
    }

    @SuppressLint("MissingPermission")
    //Realiza a busca pela localização atual do usuário, tanto por serviço de internet
    //quanto por serviço do GPS do celular. Onde fica atualizando caso seja alterada a localização
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {

            if (hasGps) {
                //Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location
                            //Log.d("Localização", " GPS Latitude : " + locationGps!!.latitude)
                            //Log.d("Localização", " GPS Longitude : " + locationGps!!.longitude)
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if (hasNetwork) {
                //Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location
                            //Log.d("Localização", " Network Latitude : " + locationNetwork!!.latitude)
                            //Log.d("Localização", " Network Longitude : " + locationNetwork!!.longitude)
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if(locationGps!= null && locationNetwork!= null){
                if(locationGps!!.accuracy > locationNetwork!!.accuracy){
                    //Log.d("Localização", " Network Latitude : " + locationNetwork!!.latitude)
                    //Log.d("Localização", " Network Longitude : " + locationNetwork!!.longitude)
                }else{
                    //Log.d("Localização", " GPS Latitude : " + locationGps!!.latitude)
                    //Log.d("Localização", " GPS Longitude : " + locationGps!!.longitude)
                }
            }

        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    //Realização o encaminhamento dos valores para a próxima activity para
    //poder realizar a adição de uma nova localização
    private fun openNextActivityForm(){

        val intent = Intent(this, CollectActivity::class.java)

        //Enviando valores para a proxima activity
        intent.putExtra("latitude",(locationGps!!.latitude).toString())
        intent.putExtra("longitude",(locationGps!!.longitude).toString())
        startActivity(intent)
    }

}