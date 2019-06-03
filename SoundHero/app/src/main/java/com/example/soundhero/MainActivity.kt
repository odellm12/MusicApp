package com.example.soundhero

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), BLE.Callback, SensorEventListener {

    //Create Media Player
    private lateinit var mp: MediaPlayer
    private lateinit var sensorManager: SensorManager
    var position = 0


    // Bluetooth
    private var ble: BLE? = null
    private var messages: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mp = MediaPlayer.create(this, R.raw.dota)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        val adapter: BluetoothAdapter?
        adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter != null) {
            if (!adapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)

            }
        }

        // Get Bluetooth
        messages = findViewById(R.id.bluetoothText)
        messages!!.movementMethod = ScrollingMovementMethod()
        ble = BLE(applicationContext, DEVICE_NAME)

        // Check permissions
        ActivityCompat.requestPermissions(this,
            arrayOf( Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)

    }

    override fun onResume() {
        super.onResume()
        ble!!.registerCallback(this)
    }

    override fun onStop() {
        super.onStop()
        ble!!.unregisterCallback(this)
        ble!!.disconnect()
    }

    fun connect(v: View) {
        startScan()
    }
    fun playButton(v: View)
    {
        mp.start()
    }
    fun pauseButton(v: View)
    {
        if (mp.isPlaying ()) {
            position = mp.getCurrentPosition()
            mp.pause ()
        }
    }
    fun continueButton(v: View)
    {
        if (mp.isPlaying () == false)
        {
            mp.seekTo(position)
            mp.start()
        }
    }
    fun stopButton(v: View)
    {
        mp.pause ()
        position = 0
        mp.seekTo (0)
    }

    private fun startScan() {
        writeLine("Scanning for devices ...")
        ble!!.connectFirstAvailable()
    }

    /**
     * Writes a line to the messages textbox
     * @param text: the text that you want to write
     */
    private fun writeLine(text: CharSequence) {
        runOnUiThread {
            messages!!.append(text)
            messages!!.append("\n")
        }
    }

    /**
     * Called when a UART device is discovered (after calling startScan)
     * @param device: the BLE device
     */
    override fun onDeviceFound(device: BluetoothDevice) {
        writeLine("Found device : " + device.name)
        writeLine("Waiting for a connection ...")
    }

    /**
     * Prints the devices information
     */
    override fun onDeviceInfoAvailable() {
        writeLine(ble!!.deviceInfo)
    }

    /**
     * Called when UART device is connected and ready to send/receive data
     * @param ble: the BLE UART object
     */
    override fun onConnected(ble: BLE) {
        writeLine("Ready to Start Music!")
    }

    /**
     * Called when some error occurred which prevented UART connection from completing
     * @param ble: the BLE UART object
     */
    override fun onConnectFailed(ble: BLE) {
        writeLine("Error connecting to device!")
    }

    /**
     * Called when the UART device disconnected
     * @param ble: the BLE UART object
     */
    override fun onDisconnected(ble: BLE) {
        writeLine("Disconnected!")
    }

    /**
     * Called when data is received by the UART
     * @param ble: the BLE UART object
     * @param rx: the received characteristic
     */
    override fun onReceive(ble: BLE, rx: BluetoothGattCharacteristic) {
        writeLine("Received value: " + rx.getStringValue(0))

    }

    override fun onDestroy () {
        super.onDestroy ()
        mp.release ()
    }

    companion object {
        private val DEVICE_NAME = "SoundHero"
        private val REQUEST_ENABLE_BT = 0
    }
    override fun onSensorChanged(event: SensorEvent?) {
        var x = event!!.values[0]
        var y  = event!!.values[1]
        var z  = event!!.values[2]

        var accR = sqrt(x*x + y*y + z*z)

        var accThresh = 18
        Log.i("ACCEL", "Accelerometer Data: " + accR)
        if(accR > accThresh){
            Log.i("CHECK", "SENDING DATA TO CHECK")
            checkUserBeat()
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    //var accR // This is the accelerometer reading
    //var accR = 0
    //var accThresh = 18// This is the threshold that, if crossed, triggers a fist pump
    var beats = arrayOf(5059,
        18772,
        32490,
        35907,
        36335,
        36764,
        37193,
        37632,
        38050,
        38478,
        38907,
        39335,
        39764,
        40193,
        40621,
        41050,
        41478,
        41907,
        42336,
        42764,
        43193,
        43621,
        44050,
        44489,
        44907,
        45335,
        45764,
        46193,
        46621,
        47050,
        47479,
        47907,
        48325,
        48764,
        49193,
        49621,
        50050,
        50478,
        50907,
        51336,
        51764,
        52192,
        52621,
        53049,
        53479,
        53907,
        54336,
        54764,
        55192,
        55621,
        56050,
        56478,
        56907,
        57336,
        57764,
        58203,
        58621,
        59049,
        59478,
        59907,
        60335,
        60764,
        61193,
        61621,
        62049,
        62478,
        63024,
        63142,
        63239,
        63331,
        71477,
        71907,
        72346,
        72764,
        73203,
        73632,
        74050,
        74478,
        74917,
        75335,
        75764,
        76203,
        76621,
        77049,
        77478,
        77907,
        78335,
        78764,
        79192,
        79621,
        80060,
        80478,
        80906,
        81335,
        81764,
        82192,
        82621,
        83050,
        83478,
        83907,
        84335,
        84763,
        85192,
        85621,
        86060,
        86478,
        86918,
        87335,
        87764,
        88203,
        88620,
        89049,
        89489,
        89907,
        90335,
        90764,
        91192,
        91620,
        92049,
        92478,
        92907,
        93345,
        93774,
        94203,
        94643,
        99343,
        114771,
        128488,
        131906,
        132335,
        132763,
        133192,
        133620,
        134049,
        134477,
        134906,
        135334,
        135763,
        136191,
        136620,
        137049,
        137477,
        137916,
        138335,
        138773,
        139192,
        139620,
        140049,
        140478,
        140906,
        141334,
        141763,
        142192,
        142620,
        143048,
        143477,
        143906,
        144335,
        144773,
        145191,
        145502,
        145620,
        146049,
        146477,
        146906,
        147334,
        147763,
        148191,
        148620,
        149048,
        149477,
        149905,
        150334,
        150763,
        151202,
        151620,
        152049,
        152477,
        152906,
        153334,
        153774,
        154192,
        154620,
        155049,
        155477,
        155905,
        156334,
        156762,
        157191,
        157620,
        158049,
        158477,
        159063,
        159197,
        159341,
        169483,
        169631,
        170048,
        170487,
        170906,
        171345,
        171762,
        172191,
        172620,
        173049,
        173476,
        173905,
        174333,
        174762,
        175191,
        175620,
        176048,
        176477,
        176906,
        177334,
        177763,
        178201,
        178620,
        179048,
        179487,
        179905,
        180334,
        180763,
        181191,
        181620,
        182048,
        182477,
        182905,
        183334,
        183763,
        184201,
        184620,
        185059,
        185476,
        185905,
        186324,
        186762,
        187191,
        187619,
        188048,
        188477,
        188915,
        189344,
        189763,
        190201,
        190620,
        191049,
        191477,
        191916,
        193627,
        197059)
    // (18 is a good value for ythe arduino but idk about android)
    var grace_window = 250 // This is the amount of time the accelerometer has to wait
    // before counting an "above threshold" signal as a fist pump (200-250ms seems resonable)
    var greenThresh = 30 // This is the distance from the actual beat to the user's attempt that gets a score of "green"
    // (20 is a resonable number)
    var yellowThresh = 60 // This is the distance from the actual beat to the user's attempt that gets a score of "yellow"
    // (40 is a resonable number)
    var prevAttempt = 0 // This variable keeps track of the time stamp of the previous succesful fist pump

    fun checkUserBeat(){
        // I'm not sure what you called your media player or how to get milliseconds from it
        var attempt = mp.getCurrentPosition() // This is the time stamp where the user might have "fist pumped"

        Log.i("Attempt", "Current: "+ attempt+ "\n" + "Previous: " + prevAttempt)
        // This checks if enough time has passed before the next fist pump can be detected
        if(attempt - prevAttempt > grace_window){
            // Loops through all the values in "beats"
            var distances = arrayOfNulls<Number>(beats.size)
            for (i in beats.indices) {
                distances[i] = abs(attempt-beats[i])// This is an array of all the distances from each beat to the user's attempt
                // We may need to subtract every distance by 20-40ms here.
                // Lets do that if the beat detector seems off
            }
            distances.sort()
            var zeroIndex = distances[0]
            Log.i("Distance", "Distance Away: "+ zeroIndex)
            if(zeroIndex!!.toInt()<greenThresh)
            {
                ble!!.send("green")
            }
            else if(zeroIndex!!.toInt()<yellowThresh)
            {
                ble!!.send("blue")
            }
            else
            {
                ble!!.send("red")
            }
            prevAttempt = attempt
        }
    }

}
