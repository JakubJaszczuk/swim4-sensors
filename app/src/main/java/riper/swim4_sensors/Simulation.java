package riper.swim4_sensors;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import java.nio.IntBuffer;
import java.util.Random;

public class Simulation extends Thread{

	private static final int WIDTH = 40;
	private static final int HEIGHT = 50;
	private static final float TRESHOLD = 3;
	int[] data;
	private float[] vector = {0f, -9f, 0f};     // Czasem jest null, więc trzeba zachować pomiędzy wywołaniami poprzednią wartość
	private static final int color = 0xFF3377DD;
	ImageView image;
	MainActivity activity;
	float[] prox = {1f};    // Jak przy wektor


	public Simulation(ImageView image, MainActivity activity){
		super();
		this.image = image;
		this.activity = activity;
		data = new int[WIDTH * HEIGHT];
		initialFeed();
	}

	private void initialFeed(){
		Random rand = new Random(2435);
		for(int i = 0; i < 2; ++i){
			for(int j = 0; j < WIDTH; ++j){
				data[i * WIDTH + j] = color;
			}
		}
		updateImage();
	}

	private void updateImage(){
		final Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
		IntBuffer buffer = IntBuffer.wrap(data);
		bitmap.copyPixelsFromBuffer(buffer);
		final Bitmap bmp= Bitmap.createScaledBitmap(bitmap, 100, 100, false);
		// Obrazk trzeba aktualizować z aktywności, która go stworzyła
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				image.setImageBitmap(bmp);
				image.invalidate();
			}
		});
	}

	synchronized public void run() {
		while(true) {
			if(activity.proximityValue != null) prox = activity.proximityValue;
			//Log.e("", Float.toString(activity.proximity.getMaximumRange()));
			if(prox[0] > 0.5f) {
				//long timeStamp = System.currentTimeMillis();
				if (activity.gravityValue != null) vector = activity.gravityValue;
				move();
				updateImage();
			}
			try {
				sleep(100);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	private void move(){
		int[] newData = new int[WIDTH * HEIGHT];
		for(int i = 0; i < HEIGHT; ++i){
			for(int j = 0; j < WIDTH; ++j){
				if(data[i * WIDTH + j] == color) {
					boolean t = true;
					if(vector[1] > TRESHOLD) {
						if ((i + 1) * WIDTH + j < data.length && data[(i + 1) * WIDTH + j] != color) {
							newData[(i + 1) * WIDTH + j] = data[i * WIDTH + j];
						} else {
							newData[i * WIDTH + j] = data[i * WIDTH + j];
						}
						t = false;
					}
					if(vector[1] < -TRESHOLD){
						if ((i - 1) * WIDTH + j > 0 && data[(i - 1) * WIDTH + j] != color) {
							newData[(i - 1) * WIDTH + j] = data[i * WIDTH + j];
						} else {
							newData[i * WIDTH + j] = data[i * WIDTH + j];
						}
						t = false;
					}
					// Działa, ale nie tak jak miało, ale jest fajniej
					if(vector[0] < -TRESHOLD){
						if (i * WIDTH + (j - 1) > 0 && data[i * WIDTH + (j - 1)] != color) {
							newData[i * WIDTH + (j-1)] = data[i * WIDTH + j];
						} else {
							newData[i * WIDTH + j] = data[i * WIDTH + j];
						}
						t = false;
					}
					// Czyszczenie
					if(vector[0] > TRESHOLD){
						Random rand = new Random();
						data[rand.nextInt(data.length)] = rand.nextInt();
					}
					if(t){
						newData[i * WIDTH + j] = data[i * WIDTH + j];
					}
				}
			}
		}
		data = newData;
	}

}
