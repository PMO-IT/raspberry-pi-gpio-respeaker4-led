# raspberry-pi-gpio-respeaker4-led
A library to control the respeaker 4 leds with Java.

# Description
This is a library to control the respeaker 4 leds with the gpio controller using Java and PI4J. 
Default data pin is 12, clock pin is 14 and power pin is 21.


## Use the class
```
//Number of lights
GPIOLEDController t = new GPIOLEDController(12);

//Datapin, Clockpin
t.init(RaspiPin.GPIO_12, RaspiPin.GPIO_14);
```

## Examples

### Show first led red
```
//Lightindex, red, green, blue, brightness
t.setLight(0, 255, 0, 0, 31);

//Sent data to gpio controller and show result
t.show();
```

### Show second led blue
```
t.setLight(1, 0, 0, 255, 31);
t.show();
```

### Switch all lights off
```
 t.resetAllLights();
```

## Additional color examples
```
blue: (0, 0, 255)
green: (0, 255, 0)
orange: (255, 128, 0)
pink: (255, 51, 153)
purple (128, 0, 128)
red (255, 0, 0)
white (255, 255, 255)
yellow (255, 255, 51)
```
## Special thanks
Thanks to Ramin Sangesari for his post about the bit-shifting which helped a lot to create this code.
