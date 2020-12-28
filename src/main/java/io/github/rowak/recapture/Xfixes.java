package io.github.rowak.recapture;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.ptr.NativeLongByReference;

/*
 * Source: https://stackoverflow.com/a/59267235/6767660
 */

public interface Xfixes extends Library {

    Xfixes INSTANCE = Native.load("Xfixes", Xfixes.class);

    @Structure.FieldOrder({ "x", "y", "width", "height", "xhot", "yhot", "cursor_serial", "pixels", "atom", "name"})
    class XFixesCursorImage extends Structure {
        public short x;
        public short y;
        public short width;
        public short height;
        public short xhot;
        public short yhot;
        public NativeLong cursor_serial;

        public NativeLongByReference pixels;

        public NativeLong atom;
        public Pointer name;

        public XFixesCursorImage() {
            super();
        }
    }
    
    XFixesCursorImage XFixesGetCursorImage(X11.Display dpy);
}

