package fi.oamk.mapbox

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions




class MainActivity : AppCompatActivity(),OnMapReadyCallback {

    /*
    companion object {

        const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
        const val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5

        const val  SOURCE_ID = "SOURCE_ID"
        const val  ICON_ID = "ICON_ID"
        const val  LAYER_ID = "LAYER_ID"
    }*/

    var mapView: MapView? = null
    private lateinit var symbolManager: SymbolManager
    private lateinit var _mapboxMap: MapboxMap
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        setContentView(R.layout.activity_main)

        database = Firebase.database.reference

        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        //addMarkers(mapboxMap)
        _mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            var style: Style = mapboxMap.style!!
            symbolManager = SymbolManager(mapView!!,mapboxMap,style)

            symbolManager.iconAllowOverlap = true
            symbolManager.iconIgnorePlacement = true

            val bm: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.mapbox_marker_icon_default)
            mapboxMap.style?.addImage("place-marker", bm)


            database.child("coordinates").get().addOnSuccessListener {
                val itemsFromDb: ArrayList<Any> = it.value as ArrayList<Any>
                for (i in 0..itemsFromDb.size-1) {
                    val itemFromDb = itemsFromDb[i] as HashMap<String, Double>
                    val lat = itemFromDb.get("lat").toString().toDouble()
                    val lng = itemFromDb.get("lng").toString().toDouble()
                    symbolManager.create(
                        SymbolOptions()
                            .withLatLng(LatLng(lat, lng))
                            .withIconImage("place-marker")
                            .withIconSize(1.3f))
                }

            }


/*
            symbolManager.create(
                SymbolOptions()
                .withLatLng(LatLng(65.00, 24.939876))
                .withIconImage("place-marker")
                .withIconSize(1.3f))

            symbolManager.create(
                SymbolOptions()
                    .withLatLng(LatLng(65.1010, 25.20))
                    .withIconImage("place-marker")
                    .withIconSize(1.3f))
*/
            /*
            val symbolOptions: SymbolOptions = SymbolOptions()
                .withLatLng(LatLng(65.00,25.00))
                .withIconImage("place-marker")
                .withIconSize(1.3f)

            symbolManager.create(symbolOptions)

             */
        }



        setCamera(mapboxMap)
    }

    private fun setCamera(map: MapboxMap) {
        val latLng = LatLng(65.00,25.00)
        val position = CameraPosition.Builder().target(latLng).zoom(8.0).tilt(10.0).build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(position))

    }

    /*
    private fun addMarkers(map: MapboxMap) {
        val symbolLayers = ArrayList<Feature>()

        symbolLayers.add(Feature.fromGeometry(Point.fromLngLat(25.30, 65.00)))
        symbolLayers.add(Feature.fromGeometry(Point.fromLngLat(25.50, 65.00)))


        map.setStyle(
            Style.Builder().fromUri(Style.MAPBOX_STREETS)
                .withImage(ICON_ID, BitmapUtils
                    .getBitmapFromDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.mapbox_marker_icon_default))!!)
                .withSource(GeoJsonSource(SOURCE_ID, FeatureCollection.fromFeatures(symbolLayers)))
                .withLayer(
                    SymbolLayer(LAYER_ID, SOURCE_ID)
                    .withProperties(iconImage(ICON_ID), iconSize(1.0f), iconAllowOverlap(true), iconIgnorePlacement(true)))

        )
        {
            //Here is style loaded
        }
    }*/



    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState);
    }


}
