
public class test {
    public static void main(String[] args) {
        HWrapper w1 = new HWrapper();
        HWrapper w2 = new HWrapper();

        w1.i = 0;
        w2.i = 2;
        int[] arr0 = { 3, 1 };
        speicherzustand.captureSpeicherbereich("main", new v("w1", w1), new v("w2", w2), new v("arr0", arr0));
        f(arr0, w1, w2);
        int[] arr1 = { 2 * arr0[0], 3 * arr0[1] };
        speicherzustand.captureSpeicherbereich("main", new v("w1", w1), new v("w2", w2), new v("arr0", arr0),
                new v("arr1", arr1));
        f(arr1, w2);
        speicherzustand.captureSpeicherbereich("main", new v("w1", w1), new v("w2", w2), new v("arr0", arr0),
                new v("arr1", arr1));
        f(arr0);
        speicherzustand.saveLatexGraphics("");
    }

    public static void f(int[] a, HWrapper... ws) {
        if (ws.length < 1) {
            a = new int[2];
            a[1] = 8;
            a[0] = 9;
        } else {
            ws[ws.length - 1].i = a[0];
            ws[0].i += ws[ws.length - 1].i;
            a[1] += a[0];
        }
        // Speicherzustand jeweils hier zeichnen
        speicherzustand.captureMemorystate("test", new Memoryblock("f", new v("a", a), new v("ws", ws)));
    }
}

class HWrapper {
    int i;
}

