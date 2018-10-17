# Color Q
Color Q is a free mobile application developed in Java for the Google Play Store. The app was developed in the Android Studio v3.2.1 integrated development environment. It works alongside the Chrome-Q hardware also developed by Lambert iGEM in order to effectively quantify the result of a biological reporter, similar to the function of a plate reader. The app is able to use circle detection in order to find the samples on the base of the Chrome-Q hardware and then detect the red, green, and blue values (RGB) of the center of each circle. The way the circles are arranged allow for a range of values to be generated. The first row contains 4 circles. The average RGB values of the first two circles are calculated as the negative control and the average RGB values of the second pair of circles are calculated as the positive control. The distance between the positive and negative control is calculated in the 3D-coordinate plane using the following formula: 



The Hough Circle Transform is used as the method of circle detection found in the app. Open Computer Vision (OpenCV) is a library that can be imported into Android Studio in order to perform image analysis-based methods. The image taken by the smartphone camera is converted into a grayscale photo. This essentially makes the image more readable in terms of edge detection and "round" estimation. There are several parameters that can be modified and calibrated in order to detect an accurate amount of circles: 

Maximum Radius: The smallest value for the radius of a detected circle
Minimum Radius: The largest value for the radius of a detected circle
Minimum Distance: The smallest distance between the centers of any two detected circles
Edge Gradient Value: The roundness of each detected circle
Threshold Value: The amount of memory the system has to store the detected circles
