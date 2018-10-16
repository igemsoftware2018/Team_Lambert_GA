package com.lambertigem.colorq;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.graphics.Color;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.imgproc.*;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.Utils;
import org.opencv.core.Point;
import org.opencv.android.OpenCVLoader;
import java.util.Arrays;
import android.database.Cursor;
import android.app.AlertDialog;
import android.os.Build;
import android.content.DialogInterface;

public class MainActivity extends Activity {
	private static String TAG = "MainActivity";
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final int REQUEST_IMAGE_ALBUM = 2;
	private static final int REQUEST_IMAGE_CROP = 3;
	private ImageView mImageView;
	private String mCurrentPhotoPath;
	private Uri contentUri;
	private double[] hsvValues;
    static{ System.loadLibrary("opencv_java3"); }
    private Integer[] circleX;
    private Integer[] circleY;
    private Integer[] circleR;
    private Integer[] indexes;
    private ColorRGB[][] colorRGB;
    private String result;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
			if (!OpenCVLoader.initDebug()) {
				// Handle initialization error
				System.out.println("OpenCV  Init Error");
			}
		hsvValues = new double[10];
		mCurrentPhotoPath = null;
		mImageView = (ImageView)findViewById(R.id.mImageView);
		File path = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES);

/*		processbutton = (Button)findViewById(R.id.ProcessBtn);
        processbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button process = (Button)findViewById(R.id.ProcessBtn);
                process.setBackgroundResource(R.drawable.button_selector);
                buttonPressed(v);
            }
        });
*/
		if(!path.exists()) {
			path.mkdirs();
		}
	}


	public void buttonPressed(View v) {
		if(v.getId() == R.id.ProcessBtn)
        {
         int ret = ApproximateCircleCountAndgetCircleCoordinates(contentUri);
            if (ret == 0) {
                int hsvret = getHsvValuesForPixels(contentUri);
                if (hsvret == 0) {
                    Intent i = new Intent(getApplicationContext(), ResultsActivity.class);
                    i.putExtra("imagePath", contentUri.toString());
                    i.putExtra("result", this.result);
                    startActivity(i);
                }
                else {

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(this);
                    }
                    builder.setTitle("Circle Detection Failed")
                            .setMessage("Too many circles found in one row. Please take another picture.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(goToNextActivity);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

            }

            else
            {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("Circle Detection Failed")
                        .setMessage("Circle detection failed for this image. Please take another picture.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(goToNextActivity);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
		}
		else {
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, REQUEST_IMAGE_ALBUM);
		}
	}
	
	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
//            ...
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
        		contentUri = Uri.fromFile(photoFile);        	
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile));
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == RESULT_OK) {
			switch(requestCode) {
				case REQUEST_IMAGE_ALBUM:
					contentUri = data.getData();
				case REQUEST_IMAGE_CAPTURE:
					cropImage(contentUri);
					break;
				case REQUEST_IMAGE_CROP:
					Bundle extras = data.getExtras();
					if(extras != null) {
						Bitmap bitmap = (Bitmap)extras.get("data");

						mImageView.setImageBitmap(bitmap);
						
						if(mCurrentPhotoPath != null) {
							File f = new File(mCurrentPhotoPath);
							if(f.exists()) {
								f.delete();
							}
							mCurrentPhotoPath = null;
						}
					}
					break;
			}
		}	    
	}	

	private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES);
    Log.d(TAG, "storageDir : " + storageDir);
    Log.d(TAG, "fileName : " + imageFileName);
    File image = File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */
    );
    
    mCurrentPhotoPath = image.getAbsolutePath();
    
    return image;	
	}	

	private void cropImage(Uri contentUri) {

        ImageView myImage = (ImageView) findViewById(R.id.mImageView);

        File imageFile = new File(getRealPathFromURI(contentUri));

        String imagePath = imageFile.getAbsolutePath();             // photoFile is a File class.
        Bitmap myBitmap  = BitmapFactory.decodeFile(imagePath);

        Bitmap orientedBitmap = ExifUtil.rotateBitmap(imagePath, myBitmap);


        myImage.setImageBitmap(orientedBitmap);

	}

    private int  ApproximateCircleCountAndgetCircleCoordinates(Uri contentUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }

        /* convert bitmap to mat */
        Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(),
                CvType.CV_8UC1);
        Mat grayMat = new Mat(bitmap.getWidth(), bitmap.getHeight(),
                CvType.CV_8UC1);

        Utils.bitmapToMat(bitmap, mat);

        /* convert to grayscale */
        int colorChannels = (mat.channels() == 3) ? Imgproc.COLOR_BGR2GRAY
                : ((mat.channels() == 4) ? Imgproc.COLOR_BGRA2GRAY : 1);

        Imgproc.cvtColor(mat, grayMat, colorChannels);

        /* reduce the noise so we avoid false circle detection */
        Imgproc.GaussianBlur(grayMat, grayMat, new Size(9, 9), 2, 2);


        int numberOfCircles = 0;
        Mat circles;

        // min and max radii (set these values as you desire)
        int minRadius = 1, maxRadius = 20;

        int iterNumber = 1;
        int numtries = 0;

        do {

// accumulator value
            double dp = 1.0;
// minimum distance between the center coordinates of detected circles in pixels
            double minDist = 80;

// param1 = gradient value used to handle edge detection
// param2 = Accumulator threshold value for the
// cv2.CV_HOUGH_GRADIENT method.
// The smaller the threshold is, the more circles will be
// detected (including false circles).
// The larger the threshold is, the more circles will
// potentially be returned.
            double param1 = 20, param2 = 20;

            /* create a Mat object to store the circles detected */
             circles = new Mat(bitmap.getWidth(),
                    bitmap.getHeight(), CvType.CV_8UC1);

            /* find the circle in the image */
            Imgproc.HoughCircles(grayMat, circles,
                    Imgproc.CV_HOUGH_GRADIENT, dp, minDist, param1,
                    param2, minRadius, maxRadius);

            /* get the number of circles detected */
            numberOfCircles = (circles.rows() == 0) ? 0 : circles.cols();

            minRadius = minRadius + 1;
            maxRadius = maxRadius + 1;

            numtries++;

            if (numtries > 120)
            {

                numtries = 0;
                iterNumber++;
                if (iterNumber > 2 )
                {
                    return 1;
                }
                minRadius = 1;
                maxRadius = 20;
            }

        } while ( ((iterNumber == 1 && numberOfCircles < 35) || numberOfCircles > 40) ||
                ((iterNumber == 2 && numberOfCircles < 25) || numberOfCircles > 40));

        circleX = new Integer[numberOfCircles];
        circleY = new Integer[numberOfCircles];
        circleR = new Integer[numberOfCircles];

        /* draw the circles found on the image */
        for (int i=0; i<numberOfCircles; i++) {


            /* get the circle details, circleCoordinates[0, 1, 2] = (x,y,r)
             * (x,y) are the coordi
             *
             * nates of the circle's center
             */
            double[] circleCoordinates = circles.get(0, i);

            int x = (int) circleCoordinates[0], y = (int) circleCoordinates[1];

            circleX[i]=x;
            circleY[i]=y;

            Point center = new Point(x, y);

            int radius = (int) circleCoordinates[2];

            circleR[i]=radius;

            /* circle's outline */
            Imgproc.circle(mat, center, radius, new Scalar(0,
                    255, 0), 4);

            /* circle's center outline */
            Imgproc.rectangle(mat, new Point(x - 5, y - 5),
                    new Point(x + 5, y + 5),
                    new Scalar(0, 128, 255), -1);
        }

        ArrayIndexComparator comparator = new ArrayIndexComparator(circleY);
        indexes = comparator.createIndexArray();
        Arrays.sort(indexes, comparator);

        /* convert back to bitmap */
        Utils.matToBitmap(mat, bitmap);

        return 0;

    }


	private int getHsvValuesForPixels(Uri contentUri)
    {
        Bitmap bitmap = null;
        result = "Negative Control:<br>";

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),contentUri);
        }
        catch(IOException e) {
            System.out.println(e.getStackTrace());
        }

        Integer[] sortedXArray = new Integer[4];

        for (int i=0; i < 4; i++)
        {
            int index = indexes[i];
            sortedXArray[i] = circleX[index];
        }

        Arrays.sort(sortedXArray);

        colorRGB = new ColorRGB[6][6];

        for (int i=0; i < 4; i++)
        {
            int index = getYIndex(sortedXArray[i], sortedXArray.length);
            int pixel = bitmap.getPixel(sortedXArray[i],circleY[index]);
            int redValue = Color.red(pixel);
            int blueValue = Color.blue(pixel);
            int greenValue = Color.green(pixel);
            ColorRGB rgb = new ColorRGB(redValue, blueValue, greenValue,sortedXArray[i],  circleY[index], 0.0);
            colorRGB[0][i] = rgb;
        }


        //Calculate average RGB

        ColorRGB rgb1 = colorRGB[0][0];
        ColorRGB rgb2 = colorRGB[0][1];

        long avgR1 = Math.round(Math.sqrt((Math.pow(rgb1.R, 2) + Math.pow(rgb2.R, 2))/2));
        long avgG1 = Math.round(Math.sqrt((Math.pow(rgb1.G, 2) + Math.pow(rgb2.G, 2))/2));
        long avgB1 = Math.round(Math.sqrt((Math.pow(rgb1.B, 2) + Math.pow(rgb2.B, 2))/2));

        result = result +"(<font color='red'>"+avgR1+"</font>,<font color='#3d9e4d'>"+avgG1+"</font>,<font color='blue'>"+avgB1+"</font>)<br>" + "Positive Control:<br>";

        ColorRGB rgb3 = colorRGB[0][2];
        ColorRGB rgb4 = colorRGB[0][3];

        long avgR2 = Math.round(Math.sqrt((Math.pow(rgb3.R, 2) + Math.pow(rgb4.R, 2))/2));
        long avgG2 = Math.round(Math.sqrt((Math.pow(rgb3.G, 2) + Math.pow(rgb4.G, 2))/2));
        long avgB2 = Math.round(Math.sqrt((Math.pow(rgb3.B, 2) + Math.pow(rgb4.B, 2))/2));


        result = result +"(<font color='red'>"+avgR2+"</font>,<font color='#3d9e4d'>"+avgG2+"</font>,<font color='blue'>"+avgB2+"</font>)<br>";

        sortedXArray = new Integer[10];

        int start=4;
        int dim1=0;


        while(start < circleR.length-1) {
            int diff = 0;
            int firstIndex = indexes[start];
            int secondIndex = indexes[++start];
            int ctr = 0;

            result = result + "Row" + (dim1 + 1) + ": <br>";

            try {
                do {
                    int firstY = circleY[firstIndex];
                    int secondY = circleY[secondIndex];

                    diff = secondY - firstY;
                    sortedXArray[ctr++] = circleX[firstIndex];

                    firstIndex = secondIndex;

                    if (start < (circleR.length - 1)) {
                        secondIndex = indexes[++start];
                    } else {
                        sortedXArray[ctr++] = circleX[secondIndex];
                        ++start;
                    }

                } while ((diff < 100) && (start < circleR.length));


                Integer[] sortedXArray1 = Arrays.copyOf(sortedXArray, ctr);

                //           for (int i=ctr; i < 6; i++)
                //         {
                //           removeElement(, i);
                //     }

                start--;


                Arrays.sort(sortedXArray1);


                for (int i = 0; i < sortedXArray1.length; i++) {
                    int index = getYIndex(sortedXArray1[i], sortedXArray1.length);
                    int pixel = bitmap.getPixel(sortedXArray1[i], circleY[index]);
                    int redValue = Color.red(pixel);
                    int blueValue = Color.blue(pixel);
                    int greenValue = Color.green(pixel);

                    double scaleDistance1 = Math.sqrt(Math.pow(avgR1 - redValue, 2) + Math.pow(avgG1 - greenValue, 2) + Math.pow(avgB1 - blueValue, 2));
                    double scaleDistance2 = Math.sqrt(Math.pow(avgR2 - redValue, 2) + Math.pow(avgG2 - greenValue, 2) + Math.pow(avgB2 - blueValue, 2));
                    double relativeValue = Math.round((scaleDistance1 / (scaleDistance1 + scaleDistance2)) * 100);
                    ColorRGB rgb = new ColorRGB(redValue, blueValue, greenValue, sortedXArray1[i], circleY[index], relativeValue);
                    colorRGB[dim1][i] = rgb;
                    result = result + relativeValue + "%<br>";
                }
                dim1++;

            } catch (ArrayIndexOutOfBoundsException e) {
                return 1;
            }

        }
        return 0;
    }

    private int getYIndex(Integer xValue, int length)
    {
        for (int i=0; i < length; i++)
        {
            if (circleX[indexes[i]] == xValue)
                return indexes[i];
        }
        return 0;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

}



