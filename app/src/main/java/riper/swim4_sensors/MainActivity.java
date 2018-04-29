package riper.swim4_sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

	SensorManager sensorManager;
	Sensor gravity;
	Sensor proximity;
	SensorEventListener listener;
	SensorEventListener listenerProximity;
	float[] gravityValue;
	float[] proximityValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ImageView image = (ImageView) findViewById(R.id.image_view);
		initSensors();
		Simulation sim = new Simulation(image, this);
		sim.start();
	}

	void initSensors(){
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		listener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				gravityValue = event.values;
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		listenerProximity = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				proximityValue = event.values;
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(listener, gravity, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(listenerProximity, proximity, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(listener);
		sensorManager.unregisterListener(listenerProximity);
	}

}
