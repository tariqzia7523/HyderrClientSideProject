package com.map.hyderrclientsideproject

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.util.*

class CartFragment : Fragment() {
    var totalText: TextView? = null
    var recyclerView: RecyclerView? = null
    var myAdapter: MyAdapterForCart? = null
    var list: ArrayList<DishModel?>? = null
    var myRefCart: DatabaseReference? = null
    var myRefOrders: DatabaseReference? = null
    var myRefUser: DatabaseReference? = null
    var progressDialog: ProgressDialog? = null
    var firebaseUser: FirebaseUser? = null
    var TAG: String? = null
    var sum = 0
    var userModel: UserModel? = null
    var address: EditText? = null
    var addressstring: String? = null
    var locationModel: LocationModel? = null
    var addLocaiton: Button? = null
    var locationdialog: AlertDialog?=null


    private lateinit var mFusedLocationClient: FusedLocationProviderClient // A fused location client variable which is further user to get the user's current location


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.cart_fragment, container, false)
        recyclerView = v.findViewById(R.id.cart_list)
        instance = this
        list = ArrayList()
        TAG = "***CartFrag"
        userModel = arguments!!.getSerializable("data") as UserModel?
        totalText = v.findViewById(R.id.total_amount)
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("Please wait")
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val linearLayoutManager = LinearLayoutManager(activity)
        //        linearLayoutManager.setReverseLayout(true);
        recyclerView!!.setLayoutManager(linearLayoutManager)
        myAdapter = MyAdapterForCart(context, activity, list)
        recyclerView!!.setAdapter(myAdapter)
        myRefUser = FirebaseDatabase.getInstance().getReference("Users").child("Restaurants")
        myRefOrders = FirebaseDatabase.getInstance().getReference("Orders")
        myRefCart = FirebaseDatabase.getInstance().getReference("Carts")
        myRefCart!!.child(firebaseUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    for (dataSnapshot in snapshot.children) {
                        val dishModel = dataSnapshot.getValue(DishModel::class.java)
                        dishModel!!.setId(dataSnapshot.key)
                        list!!.add(dishModel)
                        myAdapter!!.notifyDataSetChanged()
                    }
                    totalPrice()
                } catch (c: Exception) {
                    c.printStackTrace()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        v.findViewById<View>(R.id.check_out).setOnClickListener {
            addLocation()
//            MainActivity.activity.paymentiflatecall(sum.toDouble())
        }
        return v
    }

    fun addLocation(): Int {
        val al = AlertDialog.Builder(activity!!)
        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View
        //        view = inflater.inflate(R.layout.payment_layout, null);
        view = inflater.inflate(R.layout.drop_off_location_popup, null)
        al.setView(view)
        val value = al.create()
        value.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        value.setCancelable(false)
        //final ListView lv=new ListView(this);
        address = view.findViewById(R.id.address)
        addLocaiton = view.findViewById(R.id.current_location)
        address!!.setFocusable(false)
        address!!.setOnClickListener(View.OnClickListener {
//            value.dismiss()
            val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(activity!!)
            activity!!.startActivityForResult(intent, 1)
        })
        addLocaiton!!.setOnClickListener(View.OnClickListener {

            if (locationModel != null) {
                value.dismiss()
                MainActivity.activity.paymentiflatecall(sum.toDouble())
            } else {
                if (!isLocationEnabled()) {
                    Toast.makeText(
                            activity,
                            "Your location provider is turned off. Please turn it on.",
                            Toast.LENGTH_SHORT
                    ).show()

                    // This will redirect you to settings from where you need to turn on the location provider.
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                } else {
                    // For Getting current location of user please have a look at below link for better understanding
                    // https://www.androdocs.com/kotlin/getting-current-location-latitude-longitude-in-android-using-kotlin.html
                    Dexter.withActivity(activity)
                            .withPermissions(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                            .withListener(object : MultiplePermissionsListener {
                                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                                    if (report!!.areAllPermissionsGranted()) {

                                        requestNewLocationData()
                                    }
                                }

                                override fun onPermissionRationaleShouldBeShown(
                                        permissions: MutableList<PermissionRequest>?,
                                        token: PermissionToken?
                                ) {
                                    showRationalDialogForPermissions()
                                }
                            }).onSameThread()
                            .check()
                }
            }


        })
        value.show()
        locationdialog=value
        value.setCancelable(true)
        return 0
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(activity!!)
                .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
                .setPositiveButton(
                        "GO TO SETTINGS"
                ) { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", activity!!.packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }
                .setNegativeButton("Cancel") { dialog,
                                               _ ->
                    dialog.dismiss()
                }.show()
    }
    fun updateAddress(place: Place) {
        locationModel = LocationModel()
        locationModel!!.setLat(place.latLng!!.latitude)
        locationModel!!.setLng(place.latLng!!.longitude)
        addressstring = getAddress(place.latLng!!.latitude, place.latLng!!.longitude)
        address!!.setText(addressstring)
        addLocaiton!!.text = "Add location"

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
                activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        )
    }

    fun updateTable(transectionid: String?, paymentId: String?, phoneNumber: String?) {
        progressDialog!!.show()
        val orderModel = OrderModel()
        orderModel.setAmount(sum)
        orderModel.setDishes(list)
        orderModel.setPaymentId(paymentId)
        orderModel.setStatus("Ordered")
        orderModel.setTransactionId(transectionid)
        orderModel.setUserAddress(addressstring)
        orderModel.setUserLat(locationModel!!.getLat())
        orderModel.setUserLng(locationModel!!.getLng())
        orderModel.setUserName(userModel!!.getName())
        orderModel.setPhoneNumber(phoneNumber)
        myRefOrders!!.child(userModel!!.getId()).push().setValue(orderModel).addOnSuccessListener {
            progressDialog!!.dismiss()
            Toast.makeText(activity, "Order Placed", Toast.LENGTH_LONG).show()
            myRefCart!!.child(firebaseUser!!.uid).removeValue()
            myRefUser!!.child(list!!.get(0)!!.getResturentID()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val userModel = snapshot.getValue(UserModel::class.java)
                        sendNotification(userModel!!.getFcmToken(),getString(R.string.new_order),getString(R.string.you_have_a_new_order),"Android")
                    } catch (c: Exception) {
                        c.printStackTrace()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
            list!!.clear()
            myAdapter!!.notifyDataSetChanged()



            try{

            }catch (e: Exception){
                e.printStackTrace()
            }

        }
    }

    fun totalPrice() {
        for (i in list!!.indices) {
            sum += list!![i]!!.getQuantity() * list!![i]!!.getPrice().replace("USD".toRegex(), "").toInt()
        }
        totalText!!.text = "Total Price : $sum USD"
    }

    fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(activity, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            val obj = addresses[0]
            obj.getAddressLine(0)

            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
            ""
        }
    }

    companion object {
        @JvmField
        var instance: CartFragment? = null
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        )
    }

    /**
     * A location callback object of fused location provider client where we will get the current location details.
     */
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            locationModel = LocationModel()
            locationModel!!.lat = mLastLocation.latitude
            Log.e("Current Latitude", locationModel!!.lat.toString() + "")
            locationModel!!.lng = mLastLocation.longitude
            Log.e("Current Longitude", locationModel!!.lng.toString() + "")

            // TODO(Step 2: Call the AsyncTask class fot getting an address from the latitude and longitude.)
            // START
            addressstring = getAddress(locationModel!!.getLat(), locationModel!!.getLng())
            address!!.setText(addressstring)
            try{
                locationdialog!!.dismiss()
            }catch (e: Exception){
                e.printStackTrace()
            }
            MainActivity.activity.paymentiflatecall(sum.toDouble())
            // END
        }

    }

    fun sendNotification(fcmToken: String, title: String?, body: String?, osType: String) {
        object : AsyncTask<Void?, Void?, Void?>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                try {
                    val JSON: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
                    val client = OkHttpClient()
                    val json = JSONObject()
                    val dataJson = JSONObject()
                    dataJson.put("body", body)
                    dataJson.put("title", title)
                    json.put("data", dataJson)
                    json.put("to", fcmToken)
                    Log.e("finalResponse", "token is $fcmToken")
                    val body: RequestBody = RequestBody.create(JSON, json.toString())
                    val request: Request = Request.Builder() //                        .header("Authorization", "key=AIzaSyANhp-yl7w4fKmgD-cuV_7U72CKCb3UA78") //Legacy Server Key
                            .header("Authorization", "key=AAAA4xZbSQY:APA91bGt1Hna8M1bKvymRWjsmCjTI86IvjGcJHi4TlkM_Pv83wxZSnXgvxcSMh6nvxqMsK4pYOnvHrK542WL2BQJ0CDhrkmesQJwPQOqTJ8IQfSDImN0e8kS2Mwlpltcb9nYuT88hcvQ") //Legacy Server Key
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build()
                    val response: Response = client.newCall(request).execute()
                    val finalResponse: String = (response.body?.string() ?: Log.e("finalResponse", response.toString())) as String
                    Log.e("finalResponse", finalResponse.toString())

                } catch (e: java.lang.Exception) {
                    //Log.d(TAG,e+"");
                    e.printStackTrace()
                }
                return null
            }


        }.execute()
    }


}