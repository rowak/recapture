# recapture
Recapture is a Linux tool for capturing a section of a display and upscaling it on a different display, filling the entire screen. The mouse can also optionally be emulated.

## Performance
This tool is is very CPU intensive. Increasing the capture area and upscale quality will decrease frame rate very quickly.

Also, I noticed that performance decreased significantly when compiling with Java 8 compared to Java 7, so I recommend using Java 7.

## Configuration
The settings from the each session are saved in <code>~/.recapture/prefs.txt</code>. The <code>source</code> and <code>destination</code> options represent the bounds of the source and destination displays respectively, and are stored in the format <code>x,y,width,height</code>.
