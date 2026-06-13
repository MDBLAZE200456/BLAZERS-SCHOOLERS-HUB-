package com.example.data

data class Course(
    val id: String,
    val title: String,
    val category: String,
    val difficulty: String,
    val description: String,
    val iconName: String, // e.g. "terminal", "coffee", "code", "psychology", "grid_view"
    val durationText: String,
    val rating: Float = 4.8f
)

data class Module(
    val id: String,
    val courseId: String,
    val title: String,
    val orderIndex: Int,
    val learnContent: String,
    val snippetTitle: String,
    val snippetCode: String,
    val snippetExplanation: String
)

data class QuizQuestion(
    val id: String,
    val courseId: String,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

object Curriculums {
    val courses = listOf(
        Course(
            id = "c-prog",
            title = "C Programming",
            category = "Systems Programming",
            difficulty = "Core",
            description = "Master low-level control, pointers, structures, and direct memory layout.",
            iconName = "terminal",
            durationText = "12 hours"
        ),
        Course(
            id = "java-1",
            title = "Java Programming",
            category = "Object-Oriented Design",
            difficulty = "Core",
            description = "Build solid ground in classes, inheritance, polymorphism, and basic exception handling.",
            iconName = "coffee",
            durationText = "8 hours"
        ),
        Course(
            id = "python",
            title = "Python Programming",
            category = "Scripting & Automation",
            difficulty = "Core",
            description = "Harness the power of decorators, context managers, file manipulation, and elegant data structures.",
            iconName = "code",
            durationText = "6 hours"
        ),
        Course(
            id = "discrete-ds",
            title = "Data Structures & Algorithms",
            category = "Theoretical CS",
            difficulty = "Advanced",
            description = "Deconstruct algorithms, Big-O complexity, Linked Lists, Trees, Graphs, and binary search structures.",
            iconName = "grid_view",
            durationText = "16 hours"
        ),
        Course(
            id = "ai-ml",
            title = "AI & Machine Learning",
            category = "Data Science & AI",
            difficulty = "Advanced",
            description = "Grasp data preprocessing, Linear Regression, Supervised Learning, and basic Artificial Neural Networks.",
            iconName = "psychology",
            durationText = "18 hours"
        ),
        Course(
            id = "vb",
            title = "Visual Basic",
            category = "Legacy Desktop",
            difficulty = "Legacy",
            description = "Learn vintage event-driven design, Windows Forms, controls, and basic ActiveX components.",
            iconName = "window",
            durationText = "4 hours"
        )
    )

    val modules = listOf(
        // C Programming Modules - 11 modules extracted from Kaduna Polytechnic COM 121 pdf
        Module(
            id = "c-mod-1",
            courseId = "c-prog",
            title = "Basics of Programming & Intro to C",
            orderIndex = 1,
            learnContent = "A **programming language** is a formal language comprising a set of instructions that produce various kinds of output. It is used to implement algorithms and control the behavior of a machine, especially a computer.\n\nThere are different categories of programming languages:\n- **Low-level languages** (e.g., Assembly Language & Machine Language)\n- **High-level languages** (e.g., C, Python, Java)\n\nHigh-level languages are more human-readable and easier to learn than low-level languages.\n\n**Program Structure in C:**\nThe structure of a program refers to how the code is organized and how the different components interact. In C programming, a simple program structure includes:\n- **Preprocessor directives**: `#include <stdio.h>` includes standard input-output library.\n- **Main function**: `int main()` is the entry point of any C program.\n- **Statements**: Executable instructions within `{}`.\n- **Return statement**: `return 0;` ends the program.",
            snippetTitle = "Your First C Program",
            snippetCode = """
#include <stdio.h>

// Function declaration
int main() {
    // Code body
    printf("Hello, World!\n");
    return 0;
}
            """.trimIndent(),
            snippetExplanation = "The preprocessor directive `#include <stdio.h>` imports standard I/O library functions. `int main()` is the entry point. `printf()` displays characters to the output terminal, and `return 0;` completes execution."
        ),
        Module(
            id = "c-mod-2",
            courseId = "c-prog",
            title = "Variables, Data Types & Constants",
            orderIndex = 2,
            learnContent = "In C, **data types** specify the type of data that a variable can hold. They are broadly classified into the following:\n1. **Integer Types**: Used to store whole numbers. `int` (typically 4 bytes), `short` (usually 2 bytes), `long` (at least 4 bytes), `unsigned` (no negative values).\n2. **Floating Point Types**: Used for decimal numbers. `float` (4 bytes, single precision), `double` (8 bytes, double precision), `long double` (higher precision).\n3. **Character Type**: `char` stores a single character (1 byte).\n4. **Void Type**: `void` represents no value or no return type in functions.\n\n**Variables, Constants & Literals:**\n- **Variables**: Named memory locations that store data which can be changed during program execution (e.g., `int age = 22;`).\n- **Constants**: Named values that cannot change during execution (e.g., `const float PI = 3.14;`).\n- **Literals**: Fixed values written directly in code (e.g., `100`, `'A'`, `3.14`).\n\n**Symbolic Constants:**\nCreated using the `#define` preprocessor directive, which replaces occurrences during preprocessing (e.g., `#define PI 3.14159`).",
            snippetTitle = "Declare & Use Variables & Constants",
            snippetCode = """
#include <stdio.h>
#define MAX_SCORE 100

int main() {
    int age = 20;
    float height = 5.9;
    const float PI = 3.14159;

    printf("Age: %d\n", age);
    printf("Height: %.1f\n", height);
    printf("Max Score: %d\n", MAX_SCORE);
    printf("Value of PI: %.5f\n", PI);
    return 0;
}
            """.trimIndent(),
            snippetExplanation = "This program declares and initializes integer and float variables, uses `#define` to specify `MAX_SCORE`, defines `PI` with the `const` keyword, and prints them with custom format specifiers."
        ),
        Module(
            id = "c-mod-3",
            courseId = "c-prog",
            title = "Storage Classes, Operators & Casting",
            orderIndex = 3,
            learnContent = "**Storage Classes** in C determine the scope, visibility, and lifetime of variables and/or functions:\n1. `auto` - Default storage class for local variables.\n2. `register` - Suggests storing the variable in a CPU register for faster access.\n3. `static` - Keeps the variable value intact between function calls.\n4. `extern` - Used to declare a global variable or function defined in another file.\n\n**Operators in C:**\n- Arithmetic: `+`, `-`, `*`, `/`, `%`\n- Relational: `==`, `!=`, `>`, `<`, `>=`, `<=`\n- Logical: `&&`, `||`, `!`\n- Assignment: `=`, `+=`, `-=`, `*=`, `/=`, `%=`\n- Increment/Decrement: `++`, `--`\n- Bitwise: `&`, `|`, `^`, `~`, `<<`, `>>`\n\n**Type Casting:**\nThe process of converting one data type into another, e.g. converting float values to integers. Explicit casting syntax: `(float)a`. Smaller integer types (char, short) are automatically promoted to `int` during arithmetic operations.",
            snippetTitle = "Static Storage Class Demonstration",
            snippetCode = """
#include <stdio.h>

void demo() {
    static int count = 0;
    count++;
    printf("Count = %d\n", count);
}

int main() {
    demo();
    demo();
    demo();
    return 0;
}
            """.trimIndent(),
            snippetExplanation = "The `static` local variable `count` is initialized only once and preserves its updated state across multiple invocations of the `demo()` function."
        ),
        Module(
            id = "c-mod-4",
            courseId = "c-prog",
            title = "Standard Input & Output in C",
            orderIndex = 4,
            learnContent = "Standard input refers to data entered by the user via the keyboard (handled via the `stdio.h` library). Standard output sends data from the program to an output device like a screen.\n\n**Input Functions:**\n- `scanf()`: Reads formatted input.\n- `getchar()`: Reads a single character.\n- `fgets()`: Reads a line of text safely.\n\n**Output Functions:**\n- `printf()`: Displays formatted text using specifiers.\n- `putchar()`: Outputs a single character.\n\n**Common Format Specifiers:**\n- `%d` - Integer\n- `%f` - Floating-point\n- `%c` - Character\n- `%s` - String",
            snippetTitle = "Mixed Data Input and Output",
            snippetCode = """
#include <stdio.h>

int main() {
    int age;
    float gpa;
    char grade;

    printf("Enter age, GPA and grade: ");
    scanf("%d %f %c", &age, &gpa, &grade);

    printf("Age: %d, GPA: %.2f, Grade: %c\n", age, gpa, grade);
    return 0;
}
            """.trimIndent(),
            snippetExplanation = "Reads three distinct types of values (integer, float, and character) together with one `scanf` statement, and outputs them utilizing the correct format specifiers."
        ),
        Module(
            id = "c-mod-5",
            courseId = "c-prog",
            title = "Control Structures in C",
            orderIndex = 5,
            learnContent = "**Control structures** determine the flow of execution of statements in a program. They allow conditional execution, repetition, and branching. There are three major types:\n1. **Sequential**: Default mode where statements execute one after another.\n2. **Selection (Conditional)**: Decision-making using `if`, `if...else`, nested `if`, and `switch` statements.\n3. **Iteration (Looping)**: Repeating blocks of code using `while`, `for`, and `do...while` loops.\n4. **Branching**: Alters flow using `goto`, `break`, `continue`, etc.\n\n- `while loop` is **entry-controlled** as the condition is verified before execution.\n- `do...while loop` is **exit-controlled** as the condition is verified at the end of the loop, ensuring it executes at least once.",
            snippetTitle = "If-Else and For Loop Combined",
            snippetCode = """
#include <stdio.h>

int main() {
    int age;
    printf("Enter your age: ");
    scanf("%d", &age);

    if (age >= 18) {
        printf("Adult\\n");
    } else {
        printf("Minor\\n");
    }

    printf("Counting 1 to 5:\\n");
    for (int i = 1; i <= 5; i++) {
        printf("%d ", i);
    }
    printf("\\n");
    return 0;
}
            """.trimIndent(),
            snippetExplanation = "Uses conditional branch checking (`if-else`) to determine age category, then leverages an entry-controlled `for loop` to iterate and print a sequence from 1 to 5."
        ),
        Module(
            id = "c-mod-6",
            courseId = "c-prog",
            title = "Functions in C",
            orderIndex = 6,
            learnContent = "A **function** is a block of code designed to perform a specific task. Functions improve code modularity, reusability, and readability. Every C program must have a `main()` function where execution starts.\n\n**User-defined vs. Library Functions:**\n- **User-defined**: Functions created by the programmer (e.g. `int add(int a, int b)`).\n- **Library Functions**: Predefined in standard compiler files (e.g., `printf()`, `sqrt()`).\n\n**Scope Rules:**\n- **Local Variables**: Declared inside a function or block and accessible only within it.\n- **Global Variables**: Declared outside all functions and accessible from any function in the file.\n\n**Call by Value vs. Call by Reference:**\n- **Call by Value**: Passes a copy of the variable. Original remains unchanged inside the caller.\n- **Call by Reference**: Passes the address of the variable. Allows direct modification of the original value (done in C using pointers).",
            snippetTitle = "Call by Reference Demonstration",
            snippetCode = """
#include <stdio.h>

void update(int *n) {
    *n = 20; // changes original variable at memory address
}

int main() {
    int num = 5;
    update(&num); // pass address of num
    printf("Updated value = %d\\n", num);
    return 0;
}
            """.trimIndent(),
            snippetExplanation = "The `update` function receives a pointer of `num`. Inside, modifying `*n` changes the value of original variable in `main()` directly."
        ),
        Module(
            id = "c-mod-7",
            courseId = "c-prog",
            title = "Arrays and Strings in C",
            orderIndex = 7,
            learnContent = "An **array** is a collection of variables of the same data type, stored at contiguous memory locations. Each element is accessed using an index starting from 0.\n- **One-dimensional array**: `int arr[5] = {1, 2, 3, 4, 5};`\n- **Two-dimensional array (Matrix)**: `int matrix[2][3] = {{1,2,3},{4,5,6}};`\n\n**Strings in C:**\nA string is an array of characters terminated by a null character (`\\0`). E.g. `char name[] = \"John\";`\n\n**Common String Operations (`<string.h>`):**\n- `strcat(str1, str2)`: Concatenates str2 to the end of str1.\n- `strcpy(dest, src)`: Copies contents of src to dest.\n- `strlen(str)`: Returns length of string.\n- `strcmp(str1, str2)`: Compares strings alphabetically.",
            snippetTitle = "Array & String Concatenation",
            snippetCode = """
#include <stdio.h>
#include <string.h>

int main() {
    int scores[5] = {10, 20, 30, 40, 50};
    for (int i = 0; i < 5; i++) {
        printf("Score %d: %d\\n", i + 1, scores[i]);
    }

    char a[20] = "Hello ";
    char b[] = "C!";
    strcat(a, b);
    printf("Concatenated: %s\\n", a);
    return 0;
}
            """.trimIndent(),
            snippetExplanation = "Initializes and traverses an integer array utilizing variable pointers, then demonstrates safe string appending using the standard buffer `strcat` operation."
        ),
        Module(
            id = "c-mod-8",
            courseId = "c-prog",
            title = "Pointers in C",
            orderIndex = 8,
            learnContent = "A **pointer** is a variable that stores the memory address of another variable. It allows indirect access and direct manipulation of values.\n\n**Uses of Pointers:**\n- Dynamic memory allocation.\n- Efficient array and list handling.\n- Passing large structures to functions without copying storage data.\n\n**Pointer Arithmetic:**\nPointer arithmetic allows operations such as incrementing (`ptr++`) or decrementing (`ptr--`) pointers to quickly traverse arrays. Valid operations include `ptr++`, `ptr--`, `ptr + n`, `ptr - n`.\n\n**Array of Pointers:**\nA collection of pointer variables (e.g., `char *names[] = {\"Alice\", \"Bob\", \"Charlie\"};`), which is extremely useful for managing multidimensional strings or dynamic arrays of text pointers.",
            snippetTitle = "Pointer Arithmetic over Arrays",
            snippetCode = """
#include <stdio.h>

int main() {
    int arr[] = {10, 20, 30};
    int *p = arr; // p points to first element

    for (int i = 0; i < 3; i++) {
        printf("Offset %d: %d\\n", i, *(p + i));
    }
    return 0;
}
            """.trimIndent(),
            snippetExplanation = "Initializes pointer `p` to the array base address. Adding offset `i` moves the pointer memory address to access corresponding slots before dereferencing key values."
        ),
        Module(
            id = "c-mod-9",
            courseId = "c-prog",
            title = "Structures and Unions in C",
            orderIndex = 9,
            learnContent = "Structures and unions are user-defined data types in C that allow grouping of related variables into structured clusters.\n\n**Structure (`struct`):**\nGroups multiple variables of different data types together. Each element (member) has its own memory location (e.g. `struct Student { int id; char name[50]; float gpa; };`).\n\n**Union (`union`):**\nShares the same memory chamber among all members. Only one member can store a validated value at any given time because writing to one member affects all other overlapping parameters.\n\n**Custom Types:**\n- `typedef` is used to create an alias representing a complex data structure (e.g. `typedef struct Book Book;`).\n- Preprocessor macros are created using `#define` declarations.",
            snippetTitle = "Structures Usage",
            snippetCode = """
#include <stdio.h>
#include <string.h>

struct Book {
    int id;
    char title[100];
};

int main() {
    struct Book b1;
    b1.id = 101;
    strcpy(b1.title, "C Programming");

    printf("Book ID: %d\\nTitle: %s\\n", b1.id, b1.title);
    return 0;
}
            """.trimIndent(),
            snippetExplanation = "Initializes an instance of `struct Book`, writes an ID integer directly, copies compiler characters into the title string, and displays properties safely."
        ),
        Module(
            id = "c-mod-10",
            courseId = "c-prog",
            title = "File Handling in C",
            orderIndex = 10,
            learnContent = "File I/O (Input/Output) in C is performed using functions configured in the standard library `<stdio.h>`. Operations include Reading from a file, Writing to a file, Appending data, and modifying binary streams.\n\n**Opening and Closing Files:**\n- `FILE *fp = fopen(\"example.txt\", \"r\");` opens the target file.\n- `fclose(fp);` closes the file, freeing system system resources.\n\n**File Modes:**\n- `\"r\"` - read text\n- `\"w\"` - write text\n- `\"a\"` - append text\n- `\"rb\"` - read binary\n- `\"wb\"` - write binary\n\n**Common Operations:**\nFor text files: `fprintf()`, `fscanf()`, `fgets()`, `fputs()`.\nFor binary files: `fread()`, `fwrite()`.",
            snippetTitle = "Write and Read Text Files",
            snippetCode = """
#include <stdio.h>

int main() {
    FILE *fp = fopen("sample.txt", "w");
    if (fp != NULL) {
        fprintf(fp, "C Programming");
        fclose(fp);
    }

    char buffer[50];
    fp = fopen("sample.txt", "r");
    if (fp != NULL) {
        fgets(buffer, 50, fp);
        printf("Read file: %s\\n", buffer);
        fclose(fp);
    }
    return 0;
}
            """.trimIndent(),
            snippetExplanation = "This file demo writes a clean textbook line inside a local output stream, closes the handler, reopens it in read mode, and outputs the buffer results safely."
        ),
        Module(
            id = "c-mod-11",
            courseId = "c-prog",
            title = "Preprocessors & Header Files",
            orderIndex = 11,
            learnContent = "**Preprocessor Directives** are lines included in code preceded by a `#` symbol. These are evaluated and substituted prior to executing compilation processes.\n\n**Header Files:**\nContain declarations, prototypes, and macro definitions. E.g. `#include <stdio.h>`, `#include <stdlib.h>`, or `#include <string.h>`. Custom local files are loaded using quotes (e.g. `#include \"myheader.h\"`).\n\n**Preprocessor Operators:**\n- Macro continuation (`\\`): Allows macros to stretch across lines.\n- Stringize (`#`): Transforms a variable parameters into a string quote.\n- Token-pasting (`##`): Merges two identifier tokens together.\n- Defined (`defined`): Evaluates whether a custom condition keyword is mapped.\n\n**Parameterized Macros:**\nFunction-like replacements that act as fast inline codes, e.g. `#define SQUARE(x) ((x) * (x))`.",
            snippetTitle = "Using Macros & Parameter replacements",
            snippetCode = """
#include <stdio.h>
#define MAX(a, b) ((a) > (b) ? (a) : (b))

int main() {
    int x = 10, y = 20;
    printf("Max is %d\\n", MAX(x, y));
    return 0;
}
            """.trimIndent(),
            snippetExplanation = "Constructs a ternary ternary choice macro `MAX(a,b)` which is evaluated inline, removing the function call overhead completely."
        ),

        // Java Modules - 12 modules covering Kaduna Polytechnic learning material and advanced OOP/Concurrency
        Module(
            id = "j1-mod-1",
            courseId = "java-1",
            title = "History of Java & Portability",
            orderIndex = 1,
            learnContent = "Java is an Object-Oriented design language developed by James Gosling and team at Sun Microsystems in 1991 (originally named 'Oak' and renamed to 'Java' in 1995).\n\n**Key Characteristics of Java:**\n- **Portability**: Java is highly portable. Java programs are compiled into intermediate **bytecode**, which the Java Virtual Machine (JVM) interprets into processor-native code. This yields the famous philosophy: **Write Once, Run Anywhere (WORA)**.\n- **Memory Management**: Java automatically manages memory using a **Garbage Collector** to reclaim unused objects, eliminating manual freeing risks present in C/C++.\n- **Security & Robustness**: Strongly-typed architecture with robust runtime safeguards restricting direct pointer access to host memory blocks.",
            snippetTitle = "Portable Bytecode Concept Demonstration",
            snippetCode = """
// Compiled once into standard .class bytecode
public class PortableSystem {
    public static void main(String[] args) {
        System.out.println("Java: Write Once, Run Anywhere (WORA)!");
    }
}
            """.trimIndent(),
            snippetExplanation = "The system compiles this class into bytecode instructions that run identically on any CPU equipped with a proper JVM interpreter."
        ),
        Module(
            id = "j1-mod-2",
            courseId = "java-1",
            title = "Java Programs & OOP Core",
            orderIndex = 2,
            learnContent = "Java programs are generally developed as:\n- **Java Applications**: Stand-alone programs (e.g., word processors or desktop utilities).\n- **Java Applets**: Programs executed inside browser contexts (client-side execution).\n- **Java Servlets**: Server-side applications.\n\n**Core OOP Pillars:**\n- **Encapsulation**: Methodology binding data/state variables and the methods manipulating them, keeping fields safe from external misuse by restricting direct access.\n- **Polymorphism**: The ability for a single interface or parent shape to be employed in multiple distinct forms (e.g., animals moving in different ways: hoppers, swimmers, crawlers).\n- **Inheritance**: The process of building specialized subclasses overriding features of an existing class, encouraging code reuse.",
            snippetTitle = "Binds State with Methods",
            snippetCode = """
public class SecureStudent {
    private int score; // Encapsulated private state

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        if (score >= 0 && score <= 100) {
            this.score = score; // Guarded state updating
        }
    }
}
            """.trimIndent(),
            snippetExplanation = "Encapsulation hides the physical variable 'score' using the 'private' modifier. It can only be read/updated through safe, public accessor methods."
        ),
        Module(
            id = "j1-mod-3",
            courseId = "java-1",
            title = "Program Structure & Comments",
            orderIndex = 3,
            learnContent = "All executable statements in Java reside inside class definitions. Java is strictly case-sensitive: `Hello` is distinct from `hello`.\n\n**Comment Styles in Java:**\n- **Single-line (In-line) comments**: Preceded by `//` symbols.\n- **Multi-line (Traditional) comments**: Delimited between `/*` and `*/` operators.\n- **Javadoc comments**: Enclosed inside `/**` and `*/`. Used to extract structural documentation directly into HTML formats via properties parsing.",
            snippetTitle = "Comments & Class Definition Structure",
            snippetCode = """
/**
 * HelloWorld.java
 * High-quality Javadoc documentation comment block
 */
public class HelloWorld {
    public static void main(String[] args) {
        // This is an inline single-line comment
        System.out.println("Hello, World!!!"); /* Traditional block comment */
    }
}
            """.trimIndent(),
            snippetExplanation = "Illustrates class scope definitions, matching curly braces, and the three distinct formats of Java program comments."
        ),
        Module(
            id = "j1-mod-4",
            courseId = "java-1",
            title = "Main Method & Output Stream",
            orderIndex = 4,
            learnContent = "The entrance of any executable Java system is the **main method**. It must conform exactly to: `public static void main(String[] args)`.\n\n**Keywords Explained:**\n- `public`: Means the method is globally accessible from outside class scopes.\n- `static`: Indicates the JVM can call the method directly from the class template without instantiating objects first.\n- `void`: Directs the compiler that the method does not return values.\n- `String[] args`: An array parameter receiving incoming command-line execution parameters.\n- `System.out`: Standard predefined console output stream object.",
            snippetTitle = "Display Text to Output Stream",
            snippetCode = """
public class WelcomeStream {
    public static void main(String[] args) {
        // print leaves cursor at the end of the text on the same line
        System.out.print("Welcome to ");
        // println automatically appends a newline at the end
        System.out.println("Java Programming!");
    }
}
            """.trimIndent(),
            snippetExplanation = "Using print vs println manages where the console cursor is left after output operations complete."
        ),
        Module(
            id = "j1-mod-5",
            courseId = "java-1",
            title = "Escape Sequences & Formatting",
            orderIndex = 5,
            learnContent = "Java uses backslash characters (`\\`) to escape standard characters to construct dedicated execution directives inside messages:\n- `\\n`: Carriage line break (Newline).\n- `\\t`: Horizontal positioning indentation (Tab).\n- `\\r`: Carriage return to position index at current line origin.\n- `\\\\`: Prints a literal backslash.\n- `\\\"`: Embeds a literal double quote inside text string definitions.\n\n**Formatted Outputs:**\n`System.out.printf()` allows embedding variable data using format specifiers like `%s` (representing String text) and `%d` (representing integer values).",
            snippetTitle = "Printf Formatted Output Statement",
            snippetCode = """
public class FormattedGreeting {
    public static void main(String[] args) {
        System.out.printf("Escape: \\tTabbed\\nNext Line\\n");
        System.out.printf("%s is %d years old!\\n", "Alice", 20);
    }
}
            """.trimIndent(),
            snippetExplanation = "Utilizes escape characters to jump lines, apply structural horizontal spaces, and inject arguments in standard string formats securely."
        ),
        Module(
            id = "j1-mod-6",
            courseId = "java-1",
            title = "Variable Declarations & Scope",
            orderIndex = 6,
            learnContent = "Variables are named memory locations where values are cached temporarily during execution.\n\n**Naming Integrity Rules:**\n- Names must start with an alphabet letter, underscore (`_`), or dollar sign (`\$`). Remaining positions can contain digits.\n- Names starting with digits (e.g. `9x`, `0value`) are completely invalid.\n- Embedding blank spaces inside names is forbidden (e.g. `x 10` is invalid; rewrite as `x_10` or `x10`).\n\n**Variable Scope:**\n- Instance fields defined inside classes can utilize public/private visibilities.\n- Variables declared inside methods are localized (implicitly private) and cannot carry explicit mod access modifiers.",
            snippetTitle = "Variable Creation & Declaration",
            snippetCode = """
public class VariableScopeTracker {
    private int scoreInputByUser; // Valid instance variable

    public static void main(String[] args) {
        // Local method variable initialization without modifiers
        int counter = 0; 
        System.out.println("Initial counter: " + counter);
    }
}
            """.trimIndent(),
            snippetExplanation = "Instance variables of classes use access modifiers while local variables within methods are defined strictly inside method scopes."
        ),
        Module(
            id = "j1-mod-7",
            courseId = "java-1",
            title = "Primitive Types & Suffixes",
            orderIndex = 7,
            learnContent = "Java supports 8 standard built-in primitive data types:\n- `byte`: 1 byte, -128 to 127\n- `short`: 2 bytes, -32,768 to 32,767\n- `int`: 4 bytes, ~ -2 billion to 2 billion\n- `long`: 8 bytes, scientific integers (suffix `L` or `l`)\n- `float`: 4 bytes, single-precision fractions (suffix `F` or `f`)\n- `double`: 8 bytes, double-precision default fractions (suffix `D` or `d`)\n- `char`: 2 bytes, stores single alphanumeric characters (uses Unicode/ASCII systems)\n- `boolean`: 1 byte, true or false.\n\nValues can be represented utilizing standard decimal or scientific notations (e.g. `2.25e-6`).",
            snippetTitle = "Primitives & Modifiers",
            snippetCode = """
public class MeasurementStorage {
    public static void main(String[] args) {
        int studentsCount = 583;
        long atomsCount = 1237890L;
        float ratio = 0.406F;
        double microCalculation = 2.25e-6;
        char statusMark = 'B';
        boolean isSafe = true;

        System.out.println("Micro scientific value: " + microCalculation);
    }
}
            """.trimIndent(),
            snippetExplanation = "Maps measurements utilizing the correct types. Numbers default to int / double unless explicit suffixes are appended."
        ),
        Module(
            id = "j1-mod-8",
            courseId = "java-1",
            title = "Constants & Math Class",
            orderIndex = 8,
            learnContent = "To specify read-only symbolic constants that cannot be altered, Java utilizes the keyword `final` (e.g. `final double PI = 3.14159;`). Convention suggests writing constants in uppercase separated with underscores.\n\n**Java Math Class Utilities:**\n- `Math.sqrt(x)`: Square root\n- `Math.abs(x)`: Absolute distance\n- `Math.cos(a)`, `Math.sin(a)`, `Math.tan(a)`: Angles in radians\n- `Math.exp(x)`: Exponential scaling e^x\n- `Math.log(x)`: Logarithm base e\n- `Math.max(x, y)`, `Math.min(x, y)`: Highest/lowest values\n- `Math.pow(x, y)`: x raised to the power of y\n- `Math.PI`: Approximate geometric circle Constant PI.",
            snippetTitle = "Formulating Compound Interest with Math",
            snippetCode = """
public class CompoundInterest {
    public static void main(String[] args) {
        double principal = 1000.0;
        final double RATE = 0.05; // Symbolic constant

        for (int year = 1; year <= 10; year++) {
            // formula: amount = principal * (1.0 + rate)^year
            double amount = principal * Math.pow(1.0 + RATE, year);
            System.out.printf("Year %d: %,20.2f\\n", year, amount);
        }
    }
}
            """.trimIndent(),
            snippetExplanation = "Utilizes constant variables representing interest rates and uses `Math.pow()` to solve exponential year-over-year deposits."
        ),
        Module(
            id = "j1-mod-9",
            courseId = "java-1",
            title = "User Input & Scanner Class",
            orderIndex = 9,
            learnContent = "Interaction inputs are acquired in Java programs utilizing the helper scanner resource `java.util.Scanner` coupled with standard keyboard stream `System.in`.\n\n**Reading Terminal Inputs:**\n- `input.nextInt()`: Reads next raw integer typed.\n- `input.nextDouble()`: Reads next double value.\n- `input.nextLine()`: Reads a complete raw line of textual characters.\n\nDivision arithmetic rules:\n- If both operands are integers, Java executes **integer division** throwing decimal remainders away (e.g. `1 / 2` evaluates as `0`).\n- If either operand is float or double, fractional parts are preserved (e.g. `86 / 10.0` evaluates as `8.6`).\n- The `%` remainder operator yields remaining divisions (e.g. `86 % 10` evaluates as `6`).",
            snippetTitle = "Compute Mathematical Integers",
            snippetCode = """
import java.util.Scanner;

public class DivisionSolver {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter integer seconds: ");
        int totalSeconds = input.nextInt(); // Read int

        int minutes = totalSeconds / 60; // Integer division
        int remainingSeconds = totalSeconds % 60; // Remainder division

        System.out.println(totalSeconds + " seconds is " + 
            minutes + " minutes and " + remainingSeconds + " seconds");
    }
}
            """.trimIndent(),
            snippetExplanation = "Imports the utility class, instantiates Scanner, and solves division remainders programmatically."
        ),
        Module(
            id = "j1-mod-10",
            courseId = "java-1",
            title = "Repetition Control Structures",
            orderIndex = 10,
            learnContent = "Repetitive control structures allow iterating blocks of instructions. Java provides several looping constructs:\n- **for loop**: Best for counter-controlled loops when number of iterations is known beforehand.\n- **while loop**: Entry-controlled loop. Checks condition before running code blocks.\n- **do...while**: Exit-controlled loop. Runs execution once before evaluating conditional gates: checking happens at boundaries.",
            snippetTitle = "Class Average Loop Demonstration",
            snippetCode = """
import java.util.Scanner;

public class GradeBookAverage {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int total = 0;
        int gradeCounter = 1;

        while (gradeCounter <= 5) { // Run exactly 5 loops
            System.out.print("Enter grade: ");
            int gradeVal = input.nextInt();
            total += gradeVal;
            gradeCounter++;
        }

        double average = (double) total / 5;
        System.out.println("Class average score: " + average);
    }
}
            """.trimIndent(),
            snippetExplanation = "Iterates 5 sequential increments in a while loop, summarizing grades inputs and processing averages utilizing type casting."
        ),
        Module(
            id = "j1-mod-11",
            courseId = "java-1",
            title = "Arrays & Passing Mechanics",
            orderIndex = 11,
            learnContent = "An **Array** is a group of contiguous variables (elements) sharing matching data types. Java arrays are zero-indexed and defined using square brackets (`[]`).\n- Every array tracks its length via final fields: `array.length`.\n- Accessing invalid indexes triggers a runtime exception.\n- **Enhanced for statement**: Slices through elements directly (syntax: `for (int element : array)`). It is read-only and cannot alter values.\n\n**Parameter Passing Mechanics:**\n- All arguments are **passed by value** (the method works with cloned copies of indices/variables).\n- For reference data types: references are passed by value, enabling the receiver to alter the underlying fields of the original object reference.",
            snippetTitle = "Array Creation and Summation",
            snippetCode = """
public class ArraySummation {
    public static void main(String[] args) {
        // Initializing direct array literal
        int[] scores = { 32, 27, 64, 18, 95 };
        int total = 0;

        // Enhanced for statement loops over copy of values
        for (int value : scores) {
            total += value;
        }

        System.out.println("The total element sum equals: " + total);
    }
}
            """.trimIndent(),
            snippetExplanation = "Utilizes array initializer notation, sweeps elements elegantly using an enhanced for-loop, and print sums."
        ),
        Module(
            id = "j1-mod-12",
            courseId = "java-1",
            title = "Generics & Parallel Streams",
            orderIndex = 12,
            learnContent = "Advanced configurations utilize types to scale safe platforms:\n- **Generics**: Restricts collection types to prevent runtime class cast errors (syntax: `Class<T>`).\n- **Collections Framework**: Employs structural arrays, lists, maps, sets (e.g. `ArrayList` or `HashSet`).\n- **Parallel Streams**: Splits iteration calculations dynamically across multi-core processors leveraging underlying concurrent pools securely.",
            snippetTitle = "Vibrant Concurrent Stream Processing",
            snippetCode = """
import java.util.List;
import java.util.stream.Collectors;

public class StreamsMastery {
    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8);

        // Run concurrent sorting and filtering pipeline
        List<Integer> evensSquared = list.parallelStream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * n)
            .collect(Collectors.toList());

        System.out.println("Parallelized Results: " + evensSquared);
    }
}
            """.trimIndent(),
            snippetExplanation = "Using concurrent streams distributes loop data pipeline processes over multiple system threads seamlessly."
        ),

        // Python Modules
        Module(
            id = "py-mod-1",
            courseId = "python",
            title = "Custom Decorators",
            orderIndex = 1,
            learnContent = "Decorators in Python provide an elegant mechanism for modifying or logging custom function behaviors without modifying their core definitions.\n\nThey achieve this because Python treated functions as first-class citizens: they can be stored in variables, returned from inner functions, and injected as arguments.",
            snippetTitle = "Execution Time Logger Decorator",
            snippetCode = """
import time

def timing_decorator(func):
    def wrapper(*args, **kwargs):
        start = time.time()
        result = func(*args, **kwargs)
        end = time.time()
        print(f"{func.__name__} took {(end-start)*1000:.2f} ms")
        return result
    return wrapper

@timing_decorator
def heavy_computation():
    sum(i * i for i in range(100000))

heavy_computation()
            """.trimIndent(),
            snippetExplanation = "Our timing wrapper intercepts the execution, calculates elapsed milliseconds using the time module, prints, and returns control."
        ),

        // AI & Machine Learning Modules
        Module(
            id = "ai-mod-1",
            courseId = "ai-ml",
            title = "Gradient Descent & Regression",
            orderIndex = 1,
            learnContent = "Linear Regression maps input vectors (features) to output continuums using a linear relationship defined by weight params and biases:\n\n`y = w * x + b`\n\nGradient Descent iteratively optimizes these parameters. By computing the partial derivative of the Mean Squared Error loss relative to weights and bias, we slide 'weights' down the cost slope toward local convergence.",
            snippetTitle = "Single Step Weight Update",
            snippetCode = """
def update_weights(X, Y, w, b, learning_rate):
    dw = 0
    db = 0
    N = len(X)
    
    # Calculate gradients
    for i in range(N):
        prediction = w * X[i] + b
        dw += -2 * X[i] * (Y[i] - prediction)
        db += -2 * (Y[i] - prediction)
        
    # Subtract to move opposite of gradient rise
    w_new = w - (dw / N) * learning_rate
    b_new = b - (db / N) * learning_rate
    
    return w_new, b_new
            """.trimIndent(),
            snippetExplanation = "Calculates gradients based on error rate. Shifting parameters dynamically downstream minimizes cumulative mean squared cost."
        ),

        // Discrete Math / Data Structures modules from Kaduna Polytechnic COM 121 / ADT pdf
        Module(
            id = "dm-mod-1",
            courseId = "discrete-ds",
            title = "Introduction to DS & Algorithms",
            orderIndex = 1,
            learnContent = "A **data structure** is a special format or ways of organizing, processing, retrieving and storing data in such a way that it can perform operations on these data in an effective way. Data structure is about rendering data elements in terms of some relationship, for better organization and storage.\n\n**Why the need for Data Structures?**\nApplications of data are complex and increase due to daily usage, raising these needs:\n- **Processor speed**: Large amount of data requires high-speed processing; as data increases, processors need data structures to avoid failure.\n- **Data search**: To search an item in an inventory of 101 items without structures, it must traverse all 101 items every time, slowing down search.\n- **Simultaneous search**: Multiple requests on web servers simultaneously can cause failure without instant data organization.\n- **Other needs**: Tasks scheduling, routing, problem-solving, and memory optimization.",
            snippetTitle = "Abstract Data Type (ADT) Simulation",
            snippetCode = """
class SimpleStack:
    def __init__(self):
        self.items = []
        
    def push(self, item):
        self.items.append(item)
        
    def pop(self):
        if not self.is_empty():
            return self.items.pop()
        return None
        
    def is_empty(self):
        return len(self.items) == 0
            """.trimIndent(),
            snippetExplanation = "Simulates a Stack as an Abstract Data Type (ADT), bundling the underlying array dataset container with specific LIFO execution procedures."
        ),
        Module(
            id = "dm-mod-2",
            courseId = "discrete-ds",
            title = "Data Types & Classifications",
            orderIndex = 2,
            learnContent = "A **data type** is an attribute associated with a piece of data that tells a computer system how to interpret its value. Mathematically: `data type = { (values) , (operations) }`.\n\n**Types of Data Types:**\n- **System / Primitive (built-in)**: Anything that stores basic data: `INTEGER`, `FLOAT`, `DOUBLE`, `BOOLEAN`, `CHAR`.\n  - *Integer*: whole numbers, positive/negative/zero.\n  - *Floating point*: fractional values (represented using IEEE standard).\n  - *Character*: alphabetic/printable values, stored as 8-bit unsigned integer (0-255). Java/Unicode support 16-bit wide characters.\n  - *Pointer*: variables storing memory addresses.\n- **User-Defined**: Constructed by users: `arrays`, `classes`, `structures`, `unions`, `pointers`.\n\n**Classification of Data Structures:**\n- **Primitive**: Boolean, Char, Integer, float, pointer.\n- **Non-Primitive**: Derived from primitives, cannot be manipulated directly by machine level instructions. homogeneous or heterogeneous:\n  - **Linear lists**: Stack, Queue\n  - **Non-Linear lists**: Graph, Trees",
            snippetTitle = "User-Defined Struct Emulation",
            snippetCode = """
class StudentProfile:
    def __init__(self, student_id, name, gpa):
        self.student_id = student_id
        self.name = name
        self.gpa = gpa

# Instantiate user-defined type
s1 = StudentProfile(101, "Alice", 3.9)
print(f"UID: {s1.student_id}, GPA: {s1.gpa}")
            """.trimIndent(),
            snippetExplanation = "A user-defined non-primitive structure grouping heterogeneous data types (int, str, float) under a single entity."
        ),
        Module(
            id = "dm-mod-3",
            courseId = "discrete-ds",
            title = "Algorithmic Approaches",
            orderIndex = 3,
            learnContent = "An **algorithm** is a set of step-by-step instructions to solve a given problem or achieve a specific goal. Every algorithm must satisfy these properties:\n- **Input**: 0 or more inputs supplied externally.\n- **Output**: At least 1 output obtained.\n- **Definiteness**: Every step must be clear and well defined.\n- **Finiteness**: Must have a finite number of steps.\n- **Correctness**: Every step must generate correct output.\n\n**Approaches to Design Algorithms:**\n- **Brute Force**: Exhaustive search over all possibilities. Types: Optimizing (evaluates all, selects best) or Sacrificing (stops once first acceptable is found).\n- **Divide and Conquer**: Breaks a problem into independent subproblems, solves recursively, and merges solutions.\n- **Greedy**: Makes locally optimal choice on each iteration with the hope of global optimum.\n- **Dynamic Programming**: Stores intermediate results to avoid recomputation (Memoization).\n- **Branch and Bound**: Specifically for integer programming problems; cuts off unfeasible solutions.\n- **Randomized**: Uses random bits/variables internally to improve average case complexity.\n- **Backtracking**: Solves recursive problems, backing out when a path fails constraints.",
            snippetTitle = "Divide & Conquer: Binary Search",
            snippetCode = """
def binary_search(arr, low, high, x):
    if high >= low:
        mid = (high + low) // 2
        if arr[mid] == x:
            return mid
        elif arr[mid] > x:
            return binary_search(arr, low, mid - 1, x)
        else:
            return binary_search(arr, mid + 1, high, x)
    return -1
            """.trimIndent(),
            snippetExplanation = "Implements binary search using Divide and Conquer, which recursively splits the search space in half to achieve high speeds."
        ),
        Module(
            id = "dm-mod-4",
            courseId = "discrete-ds",
            title = "Asymptotic Notation & Complexity",
            orderIndex = 4,
            learnContent = "Performance of an algorithm is measured in terms of `Time Complexity` (running time until completion) and `Space Complexity` (memory space occupied during execution comprising instruction, data, and environment space).\n\n**Asymptotic Notations:**\nUsed to describe bounding approximations of growing trends:\n- **Big O Notation (O)**: Asymptotic **upper bound** (worst-case). f(n) <= C * g(n) for all n >= K.\n- **Big Omega (Omega)**: Asymptotic **lower bound** (best-case). f(n) >= C * g(n) for all n >= K.\n- **Big Theta (Theta)**: Asymptotic **tight bound** (average-case).\n\n**Commonly used Rates of Growth:**\n1. `1` - Constant (Adding element to front of linked list)\n2. `log N` - Logarithmic (Finding element in sorted array)\n3. `N` - Linear (Finding element in unsorted array)\n4. `N log N` - Linear Logarithmic (Sorting e.g. Merge Sort)\n5. `N^2` - Quadratic (Shortest path between two nodes)\n6. `N^3` - Cubic (Matrix multiplication)\n7. `2^N` - Exponential (Towers of Hanoi puzzle)\n\n**Types of Analysis:**\n- **Best case**: Fastest time input configuration.\n- **Worst case**: Slowest time input (appropriate for safety-critical systems e.g. nuclear power plant controller).\n- **Average case**: Expected prediction across a random input distribution.",
            snippetTitle = "Rate of Growth Demonstration",
            snippetCode = """
# O(N) Linear Time Linear Scan 
def find_element(arr, target):
    for i in range(len(arr)):
        if arr[i] == target:
            return i # Worst case: target is at end or not present
    return -1
            """.trimIndent(),
            snippetExplanation = "Scanning an unsorted array takes linear time proportional to N elements since we might sweep the entire container in the worst case."
        ),
        Module(
            id = "dm-mod-5",
            courseId = "discrete-ds",
            title = "Data Structure Operations",
            orderIndex = 5,
            learnContent = "The most frequent operations performed on data structures include:\n- **Searching**: Finding the location of a particular element (e.g., in arrays, lists, trees, graphs).\n- **Sorting**: Process of ordering data elements based on a specific order, such as ascending or descending.\n- **Insertion**: Adding new data items into an existing data structure.\n- **Deletion**: Removing an existing data element from the structure.\n- **Updating**: Replacing parts or variables inside the data structure with updated components.",
            snippetTitle = "Array Operations Demonstration",
            snippetCode = """
# Simulated Array Operations
arr = [10, 20, 30]

# 1. Insertion
arr.insert(1, 15) # Shifting occurs
print("After Insertion:", arr)

# 2. Deletion
arr.pop(0)
print("After Deletion:", arr)

# 3. Updating
arr[1] = 99
print("After Update:", arr)
            """.trimIndent(),
            snippetExplanation = "Shows insertions requiring cell shifting (expensive for large arrays), pop-style deletion, and direct O(1) index updating."
        ),
        Module(
            id = "dm-mod-6",
            courseId = "discrete-ds",
            title = "The Arrays & Indexing",
            orderIndex = 6,
            learnContent = "An **array** is an Abstract Data Type (ADT) containing a sequence of same-type objects. Operations are defined as:\n- `STORE(a, i, e)`: maps to statements like a[i] := e.\n- `RETRIEVE(a, i)`: pulls out elements at indexed address.\n\n**Types of Indexing:**\n- **0 (zero-based)**: First element is subscripted with 0.\n- **1 (one-based)**: First element is subscripted with 1.\n- **n (n-based)**: Base index is chosen freely (some languages allow negative index bounds).\n\n**Advantages of Arrays:**\n- Simple and easy to use.\n- *Random Access*: Direct O(1) element retrieval by indexing position calculation.\n- *Cache Locality*: Contiguous layout maps cleanly to physical cache blocks, drastically aiding execution speeds.\n\n**Disadvantages of Arrays:**\n- Pre-allocation wastes memory space for unused cells.\n- *Fixed Size*: Static structures cannot expand dynamically.\n- *Complex Insertion*: Shifting adjacent cells is highly expensive when inserting near boundaries.",
            snippetTitle = "Array Store & Retrieve Sim",
            snippetCode = """
# Simulating index-based store & retrieve
arr = [6, -4, 3, 2, 11]

# Retrieve operation
element = arr[2] # Retrieves 3
# Store operation
arr[2] = 17 # Stores 17 at index 2

print("Modified Array:", arr)
            """.trimIndent(),
            snippetExplanation = "Using O(1) pointer-offset calculation, access and write operations retrieve and change contiguous storage cells instantly."
        ),
        Module(
            id = "dm-mod-7",
            courseId = "discrete-ds",
            title = "Address Calculations",
            orderIndex = 7,
            learnContent = "To store arrays, computers must pre-book precise physical cells ahead. \n\n**Address and Dimension Length Formulas:**\n- **One-Dimensional Array** A[1:U]:\n  - Number of elements: U - L + 1\n  - *Example*: A[1:25] has 25 - 1 + 1 = 25 elements. A[-1:26] has 26 - (-1) + 1 = 28 elements.\n- **Two-Dimensional Array** represented as A[L1:U1, L2:U2]:\n  - Sized as (U1 - L1 + 1) * (U2 - L2 + 1) elements.\n  - *Example*: A(1:10, 1:5) has (10 - 1 + 1) * (5 - 1 + 1) = 10 * 5 = 50 cells.\n  - *Example*: A(-1:2, 2:6) has (2 - (-1) + 1) * (6 - 2 + 1) = 4 * 5 = 20 cells.",
            snippetTitle = "Dimension Matrix Cell Solver",
            snippetCode = """
def calc_elements_1d(low, up):
    return up - low + 1

def calc_elements_2d(l1, u1, l2, u2):
    row_count = u1 - l1 + 1
    col_count = u2 - l2 + 1
    return row_count * col_count

print("A[-1:26] Size:", calc_elements_1d(-1, 26))
print("A(-1:2, 2:6) Size:", calc_elements_2d(-1, 2, 2, 6))
            """.trimIndent(),
            snippetExplanation = "Programmatically calculates allocated linear or multi-dimensional array boundaries using the bounds delta offsets."
        ),
        Module(
            id = "dm-mod-8",
            courseId = "discrete-ds",
            title = "Linked Lists Foundation",
            orderIndex = 8,
            learnContent = "A **linked list** is a collection of nodes traversed starting from the head node. Importantly: **the head is NOT a node**, but rather the address reference pointing to the first list node.\n- It uses dynamic memory allocation. Memory is requested only when a node is added, avoiding waste.\n- Consists of distinct objective blocks called **nodes**, where successive elements are connected by pointer links and the tail node references `NULL`.\n\n**Linked List ADT Operations:**\n- `Insert`: Appends or slices element into specified position.\n- `Delete`: Extracts or removes element from position, returning its value.\n\n**When to Use Linked Lists (Advantages):**\n- Unknown sequence length.\n- Easy dynamic expansion (constant O(1) time insertion at boundaries).\n- No copy-and-reallocation overhead when backing arrays fill up.\n\n**Disadvantages of Linked Lists:**\n- Access and sweep takes linear O(N) sequential crawl time.\n- Destroys CPU cache locality advantages because node addresses are scattered across memory pools.\n- Adds physical memory overhead to store next/prev addresses alongside data.",
            snippetTitle = "Simple Node & Core Traverser",
            snippetCode = """
class ListNode:
    def __init__(self, data):
        self.data = data
        self.next = None

# Building list: [10] -> [80] -> [26] -> NULL
head = ListNode(10)
head.next = ListNode(80)
head.next.next = ListNode(26)

# Crawling list
current = head
while current is not None:
    print(current.data, end=" -> ")
    current = current.next
print("NULL")
            """.trimIndent(),
            snippetExplanation = "Displays a sequential singly-linked list traversal, where list nodes are linked together dynamically using pointers."
        ),
        Module(
            id = "dm-mod-9",
            courseId = "discrete-ds",
            title = "Types of Linked Lists",
            orderIndex = 9,
            learnContent = "Linked list configurations can adapt to varying application patterns:\n1. **Singly-linked list**:\n   - One pointer field per node (LINK) referencing only the subsequent block.\n   - Node is split into Info and Next. Moves uni-directionally (one way) towards `NULL`.\n2. **Doubly-linked list**:\n   - Each node contains two links: a left link (LLINK) and a right link (RLINK).\n   - Enables straightforward bi-directional forward and backward traversals.\n   - Highly efficient for backtracking and recursive descent parsers.\n3. **Circularly-linked list**:\n   - First and final nodes are linked directly together.\n   - Can be singly or doubly constructed. Has no beginning or end, forming an endless ring.\n   - Extremely useful for buffering streaming inputs, asset queues, and iterative traversals.",
            snippetTitle = "Double-Link Insertion Sim",
            snippetCode = """
class DLNode:
    def __init__(self, data):
        self.data = data
        self.left = None
        self.right = None

# Manual double linking
n1 = DLNode(34)
n2 = DLNode(80)
n1.right = n2
n2.left = n1

print(f"n1 -> right -> data: {n1.right.data}")
print(f"n2 -> left -> data: {n2.left.data}")
            """.trimIndent(),
            snippetExplanation = "Shows a doubly linked structure with bi-directional pointer links, facilitating simple forward and backward traversals."
        ),
        Module(
            id = "dm-mod-10",
            courseId = "discrete-ds",
            title = "Introduction to Graphs",
            orderIndex = 10,
            learnContent = "A **Graph** G = (V, E) is a non-linear flexible structure representing entity collections. It consists of:\n- A finite set of non-empty vertices V (or nodes/points).\n- A finite set of edges E mapping unordered entity pairs.\n\n**Graph Varieties:**\n- **Multigraph**: Contains multiple parallel edges connecting identical targets, or self-loops.\n- **Undirected Graphs**: Vertex links are unordered pairs (vi, vj). The edge can be traversed both ways: (vi, vj) = (vj, vi).\n- **Directed Graphs (Digraphs)**: Edges are ordered, specifying direction: <vi, vj> is directed from tail vi to head vj.\n\n**Core Graph Terms:**\n- **Path**: Vertex sequence V1, V2, ... where sequential pairs are connected edges.\n- **Path Length**: Number of edges on the path.\n- **Simple Path**: A path where all vertices except possibly the first and last are distinct.\n- **Cycle / Circuit**: Simple path where the start and end vertices are identical.\n- **Strongly Connected**: A directed graph where a valid path exists from any vertex Vi to every other Vj.\n- **Degree**: Number of incident edges. In digraphs, split into **indegree** (number of inbound edges) and **outdegree** (outbound).\n- **Isomorphic Graphs**: Graphs containing identical counts of vertices, edges, and degree correspondences.",
            snippetTitle = "Undirected Graph Degree Solver",
            snippetCode = """
# Adjacency list representation
graph = {
    'A': ['B', 'C'],
    'B': ['A', 'C'],
    'C': ['A', 'B', 'D'],
    'D': ['C']
}

# Degree is the length of adjacency list
print("Degree('C'):", len(graph['C'])) # Returns 3
            """.trimIndent(),
            snippetExplanation = "Measures the physical degree of vertex 'C' by evaluating the length of its adjacent neighbor elements."
        ),
        Module(
            id = "dm-mod-11",
            courseId = "discrete-ds",
            title = "Graph Representations",
            orderIndex = 11,
            learnContent = "To implement graphs, we usually employ two representations:\n1. **Sequential / Matrix (Adjacency Matrix)**:\n   - An N * N symmetric binary matrix A = [aij].\n   - aij = 1 if an edge merges vertex i and j, otherwise aij = 0.\n   - Highly efficient for dense networks, but carries massive O(N^2) memory space requirements.\n   - Types: Adjacency, Incidence, Circuit, Cut set, or Path matrices.\n2. **Linked List (Adjacency List)**:\n   - Maintains an array of head nodes, where each index points to a singly-linked list containing and linking its adjacent neighbors.\n   - Extremely space-efficient for sparse graphs.",
            snippetTitle = "Adjacency Matrix Mapping",
            snippetCode = """
# Adjacency Matrix representing V1 connects to V2, V3
adj_matrix = [
    [0, 1, 1, 0], # V1
    [1, 0, 1, 0], # V2
    [1, 1, 0, 1], # V3
    [0, 0, 1, 0]  # V4
]

print("Connection V1 -> V2:", adj_matrix[0][1] == 1)
            """.trimIndent(),
            snippetExplanation = "Uses a symmetric binary matrix layout, checking connected neighbors using direct table queries."
        ),
        // Visual Basic Module
        Module(
            id = "vb-mod-1",
            courseId = "vb",
            title = "Subroutines & Event Controls",
            orderIndex = 1,
            learnContent = "Visual Basic (Classic 6.0 / .NET) is an event-driven programming language. Programs are designed visually using components and windows, and reactions are defined in Subroutines tied to interaction events like `Button_Click`.\n\nKey Concepts:\n- **Forms**: The visual container windows for your application UI.\n- **Controls**: Interactive elements such as CommandButtons, TextBoxes, and Labels.\n- **Event Handlers**: Sub procedures responding to user interaction (e.g., clicks, text changes).",
            snippetTitle = "Form Load and Click Subroutine",
            snippetCode = """
Private Sub Form_Load()
    MsgBox "Welcome to BLAZERS Scholar Hub!"
End Sub

Private Sub Command1_Click()
    Dim username As String
    username = Text1.Text
    Label1.Caption = "Hello, " & username
End Sub
            """.trimIndent(),
            snippetExplanation = "This legacy Visual Basic script initializes a message box when the Form loads. When command button 'Command1' is clicked, it extracts the input from TextBox 'Text1' and updates Label1's Caption."
        )
    )

    val quizzes = listOf(
        // C Quiz
        QuizQuestion(
            id = "c-q1",
            courseId = "c-prog",
            questionText = "Which of the following describes free(arr); correctly?",
            options = listOf(
                "It deletes the actual pointer variable 'arr' from the Stack.",
                "It marks the Heap memory block pointed to by 'arr' as available.",
                "It automatically clears and sets 'arr' pointer variable value to NULL.",
                "It frees all memory allocated in the current program execution."
            ),
            correctIndex = 1,
            explanation = "free() releases heap segments pointed to by its argument. It does not alter the actual pointer value (which remains a dangling pointer until assigned to NULL, hence setting to NULL manually is recommended)."
        ),
        QuizQuestion(
            id = "c-q2",
            courseId = "c-prog",
            questionText = "What will happen if you dereference an uninitialized pointer (wild pointer)?",
            options = listOf(
                "The C compiler will automatically initialize it to NULL.",
                "It behaves normally and prints 0.",
                "It causes undefined behavior, often triggering a Segmentation Fault.",
                "The program exits safely with exit code 0."
            ),
            correctIndex = 2,
            explanation = "Dangling or wild pointers contain residual garbage memory addresses. Modifying or reading arbitrary memory blocks leads to undefined behavior or immediate hardware segmentation faults."
        ),
        QuizQuestion(
            id = "c-q3",
            courseId = "c-prog",
            questionText = "What is the size of dynamic heap arrays allocated with malloc(10 * sizeof(char))?",
            options = listOf(
                "10 bytes",
                "20 bytes",
                "40 bytes",
                "8 Bytes"
            ),
            correctIndex = 0,
            explanation = "Since char is guaranteed to be 1 byte, 10 * sizeof(char) equals exactly 10 bytes."
        ),

        // Java Quiz
        QuizQuestion(
            id = "j1-q1",
            courseId = "java-1",
            questionText = "What keyword prevents subclass methods from overriding a superclass method implementation?",
            options = listOf(
                "static",
                "final",
                "private",
                "abstract"
            ),
            correctIndex = 1,
            explanation = "The 'final' keyword prevents subclasses from overriding methods or inheriting classes."
        ),
        QuizQuestion(
            id = "j1-q2",
            courseId = "java-1",
            questionText = "Which OOP concept is achieved by making instance variables private and providing getters and setters?",
            options = listOf(
                "Polymorphism",
                "Abstraction",
                "Inheritance",
                "Encapsulation"
            ),
            correctIndex = 3,
            explanation = "This is classic Encapsulation: wrapping data state with protection inside access functions."
        ),
        QuizQuestion(
            id = "j1-q3",
            courseId = "java-1",
            questionText = "In variable declarations, which of these is an INVALID variable name in Java?",
            options = listOf(
                "sum_counter",
                "firstName",
                "9x",
                "\$amountPaid"
            ),
            correctIndex = 2,
            explanation = "Variables cannot start with numerals in Java. Thus, '9x' is completely invalid."
        ),
        QuizQuestion(
            id = "j1-q4",
            courseId = "java-1",
            questionText = "What is the result of the division operation '1 / 2' in Java when both operands are integers?",
            options = listOf(
                "0.5",
                "0",
                "1",
                "Runtime exception"
            ),
            correctIndex = 1,
            explanation = "In Java, division between two integers executes integer division, truncating the fractional remainder, resulting in 0."
        ),
        QuizQuestion(
            id = "j1-q5",
            courseId = "java-1",
            questionText = "Which escape sequence represents a carriage line break (newline) in Java output formatting?",
            options = listOf(
                "\\t",
                "\\r",
                "\\n",
                "\\\\"
            ),
            correctIndex = 2,
            explanation = "The escape sequence '\\n' is a standard newline instruction that positions the cursor at the opening of the next line."
        ),
        QuizQuestion(
            id = "j2-q1",
            courseId = "java-1",
            questionText = "What is the advantage of using a parallelStream() versus a regular stream() in Java?",
            options = listOf(
                "The Stream uses fewer memory blocks.",
                "It leverages multi-core processors concurrently via ForkJoin pool.",
                "It compiles faster.",
                "It guarantees that elements are printed in original sequential order."
            ),
            correctIndex = 1,
            explanation = "parallelStream() splits processing tasks among multiple processor threads for high concurrency, though sequence order of processing is not guaranteed."
        ),
        QuizQuestion(
            id = "j2-q2",
            courseId = "java-1",
            questionText = "Which of the following Collections does NOT allow duplicate elements?",
            options = listOf(
                "ArrayList",
                "LinkedList",
                "HashSet",
                "HashMap"
            ),
            correctIndex = 2,
            explanation = "A Set (like HashSet) is designed to store only unique elements based on hashCode() and equals() implementations."
        ),

        // Python Quiz
        QuizQuestion(
            id = "py-q1",
            courseId = "python",
            questionText = "How do you specify a python decorator over a standard function body?",
            options = listOf(
                "Def wrapper()",
                "Using the @ symbol prefixing the decorator name.",
                "Calling decorate(func)",
                "Adding __decorator__ as an argument."
            ),
            correctIndex = 1,
            explanation = "Shorthand syntax for decorators in Python utilizes the '@decorator_name' statement on top of target definitions."
        ),

        // AI ML Quiz
        QuizQuestion(
            id = "ai-q1",
            courseId = "ai-ml",
            questionText = "In gradient descent, why do we subtract the gradients from the weights?",
            options = listOf(
                "To increase accuracy rates exponentially.",
                "To move opposite of the cost slope, minimizing total error loss.",
                "To keep the model parameters completely positive.",
                "To stop convergence from over-fitting."
            ),
            correctIndex = 1,
            explanation = "The gradient vector points in the direction of steepest curve ascent. Subtracting gradient fractions guides down to local minima."
        ),

        // DM DS Quiz
        QuizQuestion(
            id = "dm-q1",
            courseId = "discrete-ds",
            questionText = "What is the search time complexity of a balanced Binary Search Tree (BST) containing N elements?",
            options = listOf(
                "O(1)",
                "O(N)",
                "O(N log N)",
                "O(log N)"
            ),
            correctIndex = 3,
            explanation = "A balanced BST eliminates half of the remaining subtrees at each step, yielding a highly fast O(log N) runtime."
        ),
        QuizQuestion(
            id = "dm-q2",
            courseId = "discrete-ds",
            questionText = "Which structure operates on a Last-In-First-Out (LIFO) memory scheme?",
            options = listOf(
                "Queue",
                "Stack",
                "Heap",
                "Graph"
            ),
            correctIndex = 1,
            explanation = "Stacks push entries on top and pop them first, matching standard Last-In-First-Out behavior perfectly."
        ),
        QuizQuestion(
            id = "dm-q3",
            courseId = "discrete-ds",
            questionText = "What is the total number of element cells allocated for a 2-Dimensional Array A(-1:2, 2:6)?",
            options = listOf(
                "20",
                "12",
                "15",
                "25"
            ),
            correctIndex = 0,
            explanation = "Using the bounds formula: (U1 - L1 + 1) * (U2 - L2 + 1) = (2 - (-1) + 1) * (6 - 2 + 1) = 4 * 5 = 20 elements."
        ),
        QuizQuestion(
            id = "dm-q4",
            courseId = "discrete-ds",
            questionText = "In graph theory, what is a path where all vertices except possibly the first and last are distinct?",
            options = listOf(
                "Simple Path",
                "Cycle / Circuit",
                "Self-loop",
                "Isomorphism"
            ),
            correctIndex = 0,
            explanation = "A simple path is defined as a path in which all of the traversed vertices (except possibly the first and last) are completely distinct."
        ),
        // Visual Basic Quiz
        QuizQuestion(
            id = "vb-q1",
            courseId = "vb",
            questionText = "Which prefix is conventionally used for a classic Visual Basic CommandButton control?",
            options = listOf(
                "btn",
                "cmd",
                "txt",
                "lbl"
            ),
            correctIndex = 1,
            explanation = "In legacy Visual Basic 6.0/classic conventions, 'cmd' (e.g. cmdSubmit) was standard for CommandButtons, while 'btn' is more common in modern frameworks."
        )
    )
}
