# javaMemorystateVisualizer
A simple tool for visualizing a memorystate in latex with tikz.

![image](https://user-images.githubusercontent.com/58110032/201468978-ab44a7db-6951-4944-a86d-7b711e369962.png)

## When to use

Homeworks, visualizations, ... You tell me!</br>

Only tested for small programs, it can get very cluttered very quickly.</br>
As references are drawn with arrows, you might want to give them a few corners manually.

## How to use

At the end of a block of memory you want to capture add

```java
memorystateVisualizer.captureMemoryblock(name, vars);
```

``name`` is the name of the memoryblock.
```vars`` is a vararg-argument of v-Objects, which represent the variables.

```java
new v(name, value);
```
``name`` is the name of the variable. Best use the one used in code.
  E.g.: ``new v("str",str);``
``value`` is the value. You can pass any object in here.

At the actual position in code you want to make an image of the memory add

```java
memorystateVisualizer.captureMemorystate(statename, new Memoryblock(name, vars));
```

``statename`` will be the name of the file when it is eventually saved. If you capture a state with the same name multiple times the files will get indicies.
The memoryblock has the same syntax as seen above.

To save your captures memorystates put

```java
memorystateVisualizer.saveLatexGraphics(path);
```

at the end of your program.
``path`` should be the folder where the files will be saved.
