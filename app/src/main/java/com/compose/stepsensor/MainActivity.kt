package com.compose.stepsensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.compose.stepsensor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() , SensorEventListener{
    private var sensorManager : SensorManager ?=null
    private var running=false
    private var totalSteps=0f
    private var previousTotalSteps=0f
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sensorManager=getSystemService(Context.SENSOR_SERVICE) as SensorManager
        loadData()
        resetSteps()
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        p0?.let {
            if (running){
                totalSteps= p0.values[0]
                val currentStep=totalSteps.toInt()-previousTotalSteps.toInt()
                binding.stepsTaken.text=currentStep.toString()

                binding.circularProgressBar.apply {
                    setProgressWithAnimation(currentStep.toFloat())
                }
            }
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()
        running=true
        val stepSensor=sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if(stepSensor==null){
            Toast.makeText(this,"No sensor detected on this device",Toast.LENGTH_LONG).show()
        }else{
            sensorManager?.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun resetSteps(){
        binding.stepsTaken.setOnLongClickListener {
            previousTotalSteps=totalSteps
            binding.stepsTaken.text="0"
            saveData()
            true
        }
    }

    fun saveData(){
        val sharedPrefences=getSharedPreferences("myPrefs",Context.MODE_PRIVATE)
        val editor=sharedPrefences.edit()
        editor.putFloat("key1",previousTotalSteps)
        editor.apply()
    }

    fun loadData(){
        val sharedP=getSharedPreferences("myPrefs",Context.MODE_PRIVATE)
        val savedNumber=sharedP.getFloat("key1",0f)
        previousTotalSteps=savedNumber
    }

}