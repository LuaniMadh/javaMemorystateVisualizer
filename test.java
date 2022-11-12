public class test {
    public static void main(String[] args) {
        testIntegerWrapper tWrapper1 = new testIntegerWrapper();
        testIntegerWrapper tWrapper2 = new testIntegerWrapper();

        tWrapper1.i = 0;
        tWrapper2.i = 2;
        int[] arr0 = { 3, 1 };
        memorystateVisualizer.captureMemoryblock("main", new v("tWrapper1", tWrapper1), new v("tWrapper2", tWrapper2), new v("arr0", arr0));
        f(arr0, tWrapper1, tWrapper2);
        memorystateVisualizer.saveLatexGraphics("");
    }

    public static void f(int[] a, testIntegerWrapper... wrapperArray) {
        if (wrapperArray.length < 1) {
            a = new int[2];
            a[1] = 10;
            a[0] = 11;
        } else {
            wrapperArray[wrapperArray.length - 1].i = a[0];
            wrapperArray[0].i += wrapperArray[wrapperArray.length - 1].i;
            a[1] += a[0];
        }
        memorystateVisualizer.captureMemorystate("test", new Memoryblock("f", new v("a", a), new v("wrapperArray", wrapperArray)));
    }
}

class testIntegerWrapper {
    int i;
}

