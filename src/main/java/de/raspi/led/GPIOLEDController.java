package de.raspi.led;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPin;


/**
 * Raspberry Pi GPIO control library for respeaker 4 leds.
 */
public class GPIOLEDController {
    private GpioPinDigitalOutput dataPin;
    private GpioPinDigitalOutput clockPin;
    private int[] data;
    public int numberOfLights;

    public GPIOLEDController(int numberOfLights) {
        this.numberOfLights = numberOfLights;
    }

    /**
     * Initialise the required pins and prepare the dataarray. Shut of all.
     * ReSpeaker 4 power pin default is 12, default clock is 14. lights.
     */
    public void init(Pin data_pin, Pin clock_pin) {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider());
        GpioController gpioController = GpioFactory.getInstance();
        setPowerToHigh(gpioController);

        dataPin = gpioController.provisionDigitalOutputPin(data_pin);
        clockPin = gpioController.provisionDigitalOutputPin(clock_pin);
        data = new int[numberOfLights];
        resetAllLights();
    }

    /**
     * Shut of all lights.
     */
    public void resetAllLights() {
        for (int i = 0; i < numberOfLights; ++ i) {
            data[i] = 0;
        }
        show();
    }

    /**
     * Sent content of data array to the gpio controller.
     */
    public final void show() {
        sendPreamble();
        sendData();
        sendLatch();
    }

    /**
     * Sets the light color.
     * 
     * @param lightIndex
     * @param redRatio
     * @param greenRatio
     * @param blueRatio
     * @param brightness
     */
    public void setLight(int lightIndex, int redRatio, int greenRatio, int blueRatio, int brightness) {
        checkParameterValues(lightIndex, redRatio, greenRatio, blueRatio, brightness);
        data[lightIndex] = ( brightness << 24 ) | ( redRatio << 16 ) | ( greenRatio << 8 ) | blueRatio;
    }

    /**
     * Set the power pin to high. Default pin for reSpeaker 4 is 21.
     * 
     * @param gpioController
     */
    private void setPowerToHigh(GpioController gpioController) {
        gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_21).high();
    }

    private void checkParameterValues(int lightIndex, int redRatio, int greenRatio, int blueRatio, int brightness) {
        if ( ! ( isInRange(lightIndex, 0, numberOfLights) & isInRange(redRatio, 0, 255) & isInRange(greenRatio, 0, 255)
            & isInRange(blueRatio, 0, 255) & isInRange(brightness, 0, 31) )) {
            throw new IllegalArgumentException("Invalid parameter at light number " + lightIndex);
        }
    }

    private boolean isInRange(int value, int min, int max) {
        if (value >= min && value <= max) {
            return true;
        }
        return false;
    }

    private void sendPreamble() {
        for (int i = 0; i < numberOfLights; ++ i)
            writeByte(( byte ) 0);
    }

    private void sendData() {
        for (int i = 0; i < numberOfLights; ++ i)
            writeLedData(data[i]);
    }

    private void writeByte(byte out) {
        for (int i = 7; i >= 0; -- i) {
            dataPin.setState( ( out & ( 1 << i ) ) != 0);
            clockPin.setState(true);
            clockPin.setState(false);
        }
    }

    private void writeLedData(int data) {
        writeByte(( byte ) ( 0xe0 | ( ( data >> 24 ) & 0x1f ) ));
        writeByte(( byte ) ( data ));
        writeByte(( byte ) ( data >> 8 ));
        writeByte(( byte ) ( data >> 16 ));
    }

    private void sendLatch() {
        dataPin.setState(false);
        for (int i = 0; i < 36; ++ i) {
            clockPin.setState(true);
            clockPin.setState(false);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int numberOfLights = 12;

        System.out.println("Test1 - Start");
        GPIOLEDController t = new GPIOLEDController(numberOfLights);
        t.init(RaspiPin.GPIO_12, RaspiPin.GPIO_14);

        t.setLight(0, 255, 0, 0, 31);
        t.show();
        t.setLight(1, 0, 255, 0, 31);
        t.show();
        t.setLight(2, 0, 0, 255, 31);
        t.show();
        t.setLight(3, 255, 51, 153, 31);
        t.show();
        t.setLight(4, 15, 240, 90, 5);
        t.show();
        t.setLight(5, 15, 240, 90, 25);
        t.show();
        t.setLight(6, 30, 240, 90, 5);
        t.show();
        Thread.sleep(5000);

        t.resetAllLights();
        Thread.sleep(2000);
        for (int i = 0; i < 12; i ++ ) {
            for (int x = 0; x < numberOfLights; ++ x) {
                if (x % 2 == 0) {
                    t.setLight(x, 15, 250, 60, 31);
                } else {
                    t.setLight(x, 15, 240, 20, 5);
                }
            }
            t.show();
            Thread.sleep(500);

            for (int x = 0; x < numberOfLights; ++ x) {
                if (x % 2 == 0) {
                    t.setLight(x, 15, 240, 60, 5);
                } else {
                    t.setLight(x, 15, 250, 20, 31);
                }
            }
            t.show();
            Thread.sleep(1000);
        }
        System.out.println("Test1 - End");

        // blue=(0, 0, 255),
        // green=(0, 255, 0),
        // orange=(255, 128, 0),
        // pink=(255, 51, 153),
        // purple=(128, 0, 128),
        // red=(255, 0, 0),
        // white=(255, 255, 255),
        // yellow=(255, 255, 51),
    }
}
