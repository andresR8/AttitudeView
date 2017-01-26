# Android - AttitudeView
Android attitude view for drones, airplanes, rovs and mobile robots.

### Demo
<p align="center"><img src="https://media.giphy.com/media/l3q2w5LBCWopLNk5O/giphy.gif"></p>

***

### How install it

- Add [AttitudeView.java](https://github.com/andresR8/AttitudeView/blob/master/AttitudeView.java) to `src\main\java\package\`
- Replace the [AttitudeView.java Line #1 ](https://github.com/andresR8/AttitudeView/blob/master/AttitudeView.java#L1) with corresponding package. `com.your.package` 
- Add the content of [attrs_attitude_view.xml] (https://github.com/andresR8/AttitudeView/blob/master/attrs_attitude_view.xml) to `app\src\main\res\values\attrs.xml` file between the resources tag `<resources>HERE</resources>`.

### How to Use it

1. In XML File:
  1. In the root ViewGroup property of your layout, specify: `xmlns:custom="http://schemas.android.com/apk/res-auto"`;
  2. Within the ViewGroup, add the following code:
    ```
    <package.AttitudeView
        android:id="@+id/attitudeView"
        android:layout_width="300dp"
        android:layout_height="300dp" />
    ```
  3. To change the color of the numbers: `custom:textC="@android:color/black"`
  4. To change the color of the ticks in the pitch indicator use:`custom:lineC="@android:color/black""`
  4. To change the background color of the roll indicator use: `custom:markerC="#fff741"`

2. In Code:
  ```
  AttitudeView  av= (AttitudeView) findViewById(R.id.attitudeView);
  // Specify the property like layout params
  av.setPitch(20); // Update the Pitch value in degrees.
  float pitch=av.getPitch(); // Update get the actual pitch value degrees.
  av.setRoll(10); // Update the Roll value in degrees.
  float roll=av.getRoll(); // Update get the actual roll value degrees.
  ```
  For further details, checkout the [AttitudeView.java](https://github.com/andresR8/AttitudeView/blob/master/AttitudeView.java) class.

