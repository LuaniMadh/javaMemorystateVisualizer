import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.*;

class Memoryblock {
    boolean includeInGrahics = true;
    String name = "";
    int startOnstack = -1;
    int length;
    ArrayList<obj> objs = new ArrayList<>();

    v[] unbakedVars = null;

    public Memoryblock(String name, v... vars) {
        this(name, -1, vars);
    }

    public Memoryblock(String name, int startOnstack, v... vars) {
        this.name = name;
        this.startOnstack = startOnstack;
        unbakedVars = vars;
        length = vars.length;
    }

    public void bake() {
        for (v o : unbakedVars) {
            if (obj.isPrimitive(o.value())) {
                this.objs.add(new primitive(o.name(), o.value()));
            } else {
                this.objs.add(new reference(o.name(), o.value()));
            }
        }

    }

    public void setInclusion(boolean includeInGrahics) {
        this.includeInGrahics = includeInGrahics;
    }

    String draw(coordinate origin) {
        String s = "";
        if (includeInGrahics) {
            double y = origin.y();
            ArrayList<obj> alreadyDrawnObjs = new ArrayList<>();
            double highestNameLength = 0;
            for (obj obj : objs) {
                if (obj.getName().length() > highestNameLength) {
                    highestNameLength = obj.getName().length();
                }
            }
            for (obj o : objs) {
                s += o.drawMe(new coordinate(origin.x(), y), alreadyDrawnObjs,
                        highestNameLength * drawnObject.textWidth);
                alreadyDrawnObjs.add(o);
                s += "\\draw "
                        + new coordinate(memorystateVisualizer.stackX - memorystateVisualizer.stackWidth / 2,
                                y - (memorystateVisualizer.stackElementHeight / 2))
                        + " -- " + new coordinate(memorystateVisualizer.stackX + memorystateVisualizer.stackWidth / 2,
                                y - (memorystateVisualizer.stackElementHeight / 2))
                        + ";\n";
                y += memorystateVisualizer.stackElementHeight;
            }
            s += "\\draw " // last line
                    + new coordinate(memorystateVisualizer.stackX - memorystateVisualizer.stackWidth / 2,
                            y - (memorystateVisualizer.stackElementHeight / 2))
                    + " -- " + new coordinate(memorystateVisualizer.stackX + memorystateVisualizer.stackWidth / 2,
                            y - (memorystateVisualizer.stackElementHeight / 2))
                    + ";\n";

            double bracketX = origin.x() - memorystateVisualizer.stackWidth / 2
                    - (highestNameLength + 2) * drawnObject.textWidth;
            s += "\\draw " + // curly bracket
                    "[decorate,decoration = {calligraphic brace},thick] "
                    + new coordinate(bracketX, y - (memorystateVisualizer.stackElementHeight / 2)) + " -- "
                    + new coordinate(bracketX, origin.y() - memorystateVisualizer.stackElementHeight / 2) + ";\n";
            s += "\\draw " + // name
                    new coordinate(bracketX - 1 * drawnObject.textWidth,
                            (y - memorystateVisualizer.stackElementHeight + origin.y()) / 2)
                    + " node[anchor = east] {\\textbf{" + name + "}};\n";
        }
        return s;
    }
}

@SuppressWarnings("rawtypes")
abstract class obj {
    public static boolean isPrimitive(Object o) {
        if (o instanceof Class c)
            return isPrimitive(c.getSimpleName());
        else
            return isPrimitive(o.getClass().getSimpleName());
    }

    public static boolean isPrimitive(String className) {
        switch (className) {
            case "int":
            case "Integer":
            case "double":
            case "Double":
            case "float":
            case "Float":
            case "long":
            case "Long":
            case "short":
            case "Short":
            case "byte":
            case "Byte":
            case "boolean":
            case "Boolean":
            case "char":
            case "Character":
                return true;
            default:
                return false;
        }
    }

    boolean isPrimitive() {
        return isPrimitive(o);
    }

    String name;
    Object o = null;

    protected obj(String name, Object o) {
        this.name = name;
        this.o = o;
    }

    public Object getObj() {
        return o;
    }

    public String getName() {
        return name;
    }

    abstract String drawMe(coordinate p, ArrayList<obj> paintedObjs, double nameWidth);

    abstract String drawNameInHeap(coordinate p);

    abstract String drawValueInHeap(coordinate p);

    abstract int drawnCharWidthOfValue();
}

class reference extends obj {
    public reference(Object o) {
        super("", o);
        drawnRep = drawnObject.getDrawnObject(o);
    }

    public reference(String name, Object o) {
        super(name, o);
        drawnRep = drawnObject.getDrawnObject(o);
    }

    drawnObject drawnRep = null;

    public drawnObject getDrawnRep() {
        return drawnRep;
    }

    @Override
    String drawMe(coordinate p, ArrayList<obj> paintedObjs, double nameWidth) {
        // arrow to drawRep
        String s = "";
        s += "% " + name + " -> " + getObj().toString() + "\n";
        s += "\\node[align=right] at "
                + p.move(-memorystateVisualizer.stackWidth / 2 - nameWidth / 2 - 1 * drawnObject.textWidth, 0) + " {"
                + getName() + "};\n";
        s += "\\draw [{Circle}-Stealth] " + p + " -- " + drawnRep.pos + ";\n";
        memorystateVisualizer.stackHeight++;
        return s;
    }

    @Override
    String drawNameInHeap(coordinate p) {
        String res = "";
        res += "\\node[anchor=west,align=left] at " + p + " {" + getObj().getClass().getSimpleName() + " " + getName()
                + "};\n";
        return res;
    }

    @Override
    String drawValueInHeap(coordinate p) {
        String res = "";
        // arrow to value
        res += "\\draw [{Circle}-Stealth] " + p + " -- " + drawnRep.pos + ";\n";
        return res;
    }

    @Override
    int drawnCharWidthOfValue() {
        return 1;
    }
}

class primitive extends obj {
    public primitive(Object o) {
        super("", o);
    }

    public primitive(String name, Object o) {
        super(name, o);
    }

    public static String primitiveName(String name) {
        return switch (name) {
            case "Integer", "integer" -> "int";
            case "Double", "double" -> "double";
            case "Float", "float" -> "float";
            case "Long", "long" -> "long";
            case "Short", "short" -> "short";
            case "Byte", "byte" -> "byte";
            case "Boolean", "boolean" -> "boolean";
            case "Character", "char" -> "char";
            default -> name;
        };
    }

    @Override
    String drawMe(coordinate p, ArrayList<obj> paintedObjs, double nameWidth) {
        String s = "";
        s += "% primitive " + getName() + "\n";
        s += "\\node[] at "
                + p.move(-memorystateVisualizer.stackWidth / 2 - nameWidth / 2 - 1 * drawnObject.textWidth, 0)
                + " {" + getName() + "};\n";
        s += "\\node[] at " + p + " {" + getObj() + "};\n";
        memorystateVisualizer.stackHeight++;
        return s;
    }

    @Override
    String drawNameInHeap(coordinate p) {
        String res = "";
        res += "\\node[anchor=west,align=left] at " + p + " {" + primitiveName(getObj().getClass().getSimpleName())
                + " " + getName() + "};\n";
        return res;
    }

    @Override
    String drawValueInHeap(coordinate p) {
        String res = "";
        res += "\\node[] at " + p + " {" + getObj() + "};\n";
        return res;
    }

    @Override
    int drawnCharWidthOfValue() {
        return getObj().toString().length();
    }
}

class drawnObject {
    static ArrayList<drawnObject> drawnObjects = new ArrayList<>();
    coordinate pos;
    double w, h;
    Object o;

    record attribute(String name, Object value) {
    }

    ArrayList<obj> attributes = new ArrayList<>();

    public static double textHeight = 0.5;
    public static double minWidth = 1;
    public static double textWidth = 0.2; // die Stringlänge wird mit diesem Wert multipliziert, um die Breite des
                                          // Objekts zu bestimmen

    private drawnObject(coordinate pos, Object o) {
        this(o);
        this.pos = pos;
        // drawMe();
    }

    private drawnObject(Object o) {
        // needs coordinates set afterwards
        this.o = o;
        Field[] fields = o.getClass().getDeclaredFields();
        double highestNameLength = 0;
        double highestValueLength = 0;
        int attributeAmount = 0;
        for (Field f : fields) {
            try {
                if (obj.isPrimitive(f.getType()))
                    attributes.add(new primitive(f.getName(), f.get(o)));
                else
                    attributes.add(new reference(f.getName(), f.get(o)));
                if (f.getName().length() > highestNameLength) {
                    highestNameLength = f.getName().length();
                }
                if (attributes.get(attributes.size() - 1).drawnCharWidthOfValue() > highestValueLength) {
                    highestValueLength = f.get(o).toString().length() + f.getType().getSimpleName().length();
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        attributeAmount = attributes.size();
        if (o.getClass().isArray()) {
            Class<?> componentType = o.getClass().getComponentType();
            if (obj.isPrimitive(componentType)) {
                highestNameLength = 2 + (Array.getLength(o) + "").length();
                // attributes.add(new primitive("length", Array.getLength(o)));
            }
            attributeAmount = Array.getLength(o);
            for (int i = 0; i < attributeAmount; i++) {
                try {
                    if (obj.isPrimitive(componentType))
                        attributes.add(new primitive("[" + i + "]", Array.get(o, i)));
                    else
                        attributes.add(new reference("[" + i + "]", Array.get(o, i)));
                    if (attributes.get(attributes.size() - 1).drawnCharWidthOfValue() > highestValueLength) {
                        highestValueLength = attributes.get(attributes.size() - 1).drawnCharWidthOfValue();
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        w = Math.max(Math.max(minWidth, o.getClass().getSimpleName().length() * textWidth),
                (highestNameLength + (highestValueLength + 1.5) + 2) * textWidth);
        h = (attributeAmount + 1) * textHeight;// +1 due to the type beeing displayed too
    }

    public static drawnObject getDrawnObject(Object o) {
        System.out.print("drawnObject angefragt für " + o.toString());
        for (drawnObject d : drawnObjects) {
            if (d.o == o) {
                return d;
            }
        }
        drawnObject newObj = new drawnObject(o);
        if (drawnObjects.isEmpty()) {
            newObj.pos = new coordinate(memorystateVisualizer.heapMinX, 0);
            drawnObjects.add(newObj);
            return newObj;
        }

        // search for free space

        drawnObject firstObj = drawnObjects.get(0);
        // get next free position to the right of lowest
        double nextFreeX = firstObj.pos.x() + firstObj.w + textHeight;
        double nextFreeY = firstObj.pos.y();
        ArrayList<drawnObject> possibleObjectsInTheWay = new ArrayList<>();
        for (drawnObject d : drawnObjects)
            possibleObjectsInTheWay.add(d);
        while (nextFreeY < memorystateVisualizer.maxErrorHeight) {
            boolean found = false;
            while (nextFreeX + newObj.w < memorystateVisualizer.maxWidth) {
                boolean free = true;
                for (int i = 0; i < possibleObjectsInTheWay.size(); i++) {
                    drawnObject d = possibleObjectsInTheWay.get(i);
                    if (d.pos.y() + d.h < nextFreeY) {
                        possibleObjectsInTheWay.remove(i);
                        i--;
                        continue;
                    }

                    if (d.pos.x() > nextFreeX + newObj.w || d.pos.x() + d.w < nextFreeX
                            || d.pos.y() > nextFreeY + newObj.h) {// d is to the left or right
                        // of the new object
                        continue;
                    } else {
                        free = false;
                        nextFreeX = d.pos.x() + d.w + textHeight;
                        break;
                    }
                }
                if (free) {
                    if (nextFreeX + newObj.w < memorystateVisualizer.maxWidth)
                        found = true;
                    break;
                }
            }
            if (found)
                break;

            nextFreeY += textHeight;
            nextFreeX = memorystateVisualizer.heapMinX;
        }
        newObj.setPos(new coordinate(nextFreeX, nextFreeY));
        drawnObjects.add(newObj);
        return newObj;
    }

    private void setPos(coordinate pos) {
        this.pos = pos;
    }

    static String drawObjects() {
        String res = "";
        for (drawnObject d : drawnObjects) {
            res += d.drawMe();
        }
        return res;
    }

    private String drawMe() {
        String res = "";
        res += "% drawnObject " + o.getClass().getSimpleName() + ": " + o + "\n";
        res += "\\filldraw[draw=black, fill=white] " + new coordinate(pos.x(), pos.y()) + " rectangle "
                + new coordinate(pos.x() + w, pos.y() + h) + ";\n";
        res += "\\node[] at " + pos.move(w / 2, textHeight / 2) + " {" + o.getClass().getSimpleName() + "};\n";
        if (attributes.size() > 0)
            res += "\\draw[thick] " + new coordinate(pos.x(), pos.y() + textHeight) + " -- "
                    + new coordinate(pos.x() + w, pos.y() + textHeight) + ";\n";
        double y = pos.y() + 1.5 * textHeight;
        double maxValWidth = 0;
        for (obj o : attributes) {
            if (o.drawnCharWidthOfValue() > maxValWidth)
                maxValWidth = o.drawnCharWidthOfValue();
        }
        maxValWidth += 1.5;
        int elementIndex = 0;
        for (obj dro : attributes) {
            if (o.getClass().isArray())
                res += "\\node[anchor=west,align=left] at " + new coordinate(pos.x(), y) + " {[" + elementIndex
                        + "]};\n";
            else
                res += dro.drawNameInHeap(new coordinate(pos.x(), y));
            res += dro.drawValueInHeap(new coordinate(pos.x() + w - (textWidth * maxValWidth) / 2, y));

            if (elementIndex < attributes.size() - 1)
                res += "\\draw[] " + new coordinate(pos.x(), y + textHeight / 2)
                        + " -- "
                        + new coordinate(pos.x() + w, y + textHeight / 2) + ";\n";

            y += textHeight;
            elementIndex++;
        }
        res += "\\draw[] " + new coordinate(pos.x() + w - (textWidth * maxValWidth), pos.y() + textHeight) + " -- "
                + new coordinate(pos.x() + w - (textWidth * maxValWidth), pos.y() + h) + ";\n";
        return res;
    }

    public static void clear() {
        drawnObjects.clear();
    }
}

record coordinate(double x, double y) {
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public coordinate move(double x, double y) {
        return new coordinate(this.x + x, this.y + y);
    }
}

record latexElement(String name, String value) {
}

public class memorystateVisualizer {

    static ArrayList<Memoryblock> memoryblocks = new ArrayList<>();
    static ArrayList<latexElement> latexGraphics = new ArrayList<>();

    public static void captureMemoryblock(String name, v... vars) {
        for (int i = 0; i < memoryblocks.size(); i++) {
            Memoryblock s = memoryblocks.get(i);
            if (s.name.equals(name)) {
                memoryblocks.set(i, new Memoryblock(name, vars));
                return;
            }
        }
        Memoryblock sb = new Memoryblock(name, vars);
        memoryblocks.add(sb);
    }

    public static void include(String... names) {
        for (String name : names) {
            for (Memoryblock sb : memoryblocks) {
                if (sb.name.equals(name)) {
                    sb.setInclusion(true);
                }
            }
        }
    }

    public static void captureMemorystate(String captureName, Memoryblock... speicher) {
        ArrayList<Memoryblock> memoryblocksToDraw = new ArrayList<>();
        for (Memoryblock s : memoryblocks)
            memoryblocksToDraw.add(s);
        for (Memoryblock s : speicher)
            memoryblocksToDraw.add(s);
        latexGraphics.add(new latexElement(captureName, generateLatexCode(memoryblocksToDraw)));
    }

    public static final String preamble = """
            % Generated with memorystateVisualizer.java
            % https://github.com/LuaniMadh/javaMemorystateVisualizer
            \\documentclass{article}
            \\usepackage{tikz}
            \\usetikzlibrary{arrows.meta,decorations.pathreplacing,calligraphy}
            \\begin{document}
            \\texttt{
            \\begin{tikzpicture}[y=-1cm]
            """;

    public static final double stackX = 4;
    public static double stackHeight = 0;
    public static double stackWidth = 1.5;
    public static final double maxWidth = 11;
    public static final double maxErrorHeight = 100;
    public static final double stackElementHeight = 1;
    public static final double heapElementHeight = 0.7;
    public static final double distanceBetweenSpeicherbereichen = 1.7;// in elements
    public static final double heapMinX = stackX + stackWidth + 1;

    static String generateLatexCode(ArrayList<Memoryblock> memoryblocks) {
        double initStackHeight = stackHeight;
        double initStackWidth = stackWidth;

        for (Memoryblock s : memoryblocks)
            s.bake();

        String latexCode = "" + preamble;
        int i = 0;
        for (Memoryblock sb : memoryblocks) {
            if (sb.includeInGrahics) {
                latexCode += sb.draw(new coordinate(stackX, stackHeight * stackElementHeight));
                if (i != memoryblocks.size() - 1) {
                    latexCode += "\\node at " + new coordinate(stackX,
                            (stackHeight + distanceBetweenSpeicherbereichen / 2 - 0.5) * stackElementHeight
                                    - drawnObject.textHeight / 4)
                            + " {\\vdots};\n";
                    stackHeight += distanceBetweenSpeicherbereichen;
                }

            }
            i++;
        }
        latexCode += "%\n% draw heap\n";
        latexCode += drawnObject.drawObjects();
        latexCode += "%\n% stacklines\n";
        latexCode += "\\draw[very thick] " + new coordinate(stackX + stackWidth / 2, -stackElementHeight) + " -- "
                + new coordinate(stackX + stackWidth / 2, stackHeight * stackElementHeight) + ";\n";
        latexCode += "\\draw[very thick] " + new coordinate(stackX - +stackWidth / 2, -stackElementHeight) + " -- "
                + new coordinate(stackX - stackWidth / 2, stackHeight * stackElementHeight) + ";\n";
        latexCode += """
                \\end{tikzpicture}
                }
                \\end{document}
                """;

        stackHeight = initStackHeight;
        stackWidth = initStackWidth;
        drawnObject.clear();
        return latexCode;
    }

    public static void saveLatexGraphics(String path) {

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> occurances = new ArrayList<>();

        for (latexElement s : latexGraphics) {
            if (names.contains(s.name())) {
                int index = names.indexOf(s.name());
                occurances.set(index, occurances.get(index) + 1);
                saveFile(path + "/" + s.name() + "#" + occurances.get(index) + ".tex", s.value());
            } else {
                names.add(s.name());
                occurances.add(1);
                saveFile(path + "/" + s.name() + ".tex", s.value());
            }

        }
    }

    private static void saveFile(String Name, String FileContent) {
        FileOutputStream fop = null;
        File file;
        String content = FileContent;

        try {

            file = new File(Name);
            System.out.println("Saving file: " + file.getAbsolutePath());
            fop = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = content.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

record v(String name, Object value) {
}
