# patrickbuf

Inspired by [protobuf](https://github.com/protocolbuffers/protobuf), patrickbuf is my own implementation of a
language-agnostic data representation.

https://github.com/user-attachments/assets/18c2b744-1783-43e7-bb7a-f77e395a81ab

## Supported user journeys

1. Define a template in a `.pbdefn`
2. Compile a `.pbdefn` into Java source code
3. In Java,
    1. create an in-memory template instance
    2. write an in-memory template instance to disk, as binary
    3. read into memory a template instance that's stored as binary on disk

## `.pbdefn` template files

A template file contains one *template*, which is a description of some related data. For example, here's what
a `Date.pbdefn` file might look like:

```
Date
0:int:month
1:int:day
2:int:year
```

The top-most line contains the *template name*, in this case "Date". Every subsequent line contains a *template field*,
and must follow the `<field number>:<field type>:<field name>` format.

Currently supported are:

* Field numbers in the range [0, 15]
* Field type "int"
* Field names matching regex `[a-zA-Z]+`

To compile a template file into source code, use the `patrickc` compiler. Example invocation:

```
java -jar target/patrickbuf-1.0-SNAPSHOT-jar-with-dependencies.jar src/test/java/com/patrickbuf/valid.pbdefn --java_out=src/main/java/`
```

## Generated code

For a template with name "Date",

```java
public final class Date {
    ...

    public static Date create(/* template fields here */) { ... }

    public static Date readFromDisk(Path in) throws Exception { ... }

    public static void writeToDisk(Path out, Date instance) throws Exception { ... }

    @Override
    public String toString() { ... }
}
```

For each template field of type "int",

```java
public final class Date {
    public int yourTemplateFieldName;

    ...
}
```

## Binary encoding and `.pbbinary`

Template instances can be encoded to binary, for compact storage. Binary patrickbuf files conventionally end in
the `.pbbinary` extension.

Their encoding is as follows:

```
# Header
[4 bytes] Number of subsequent bits

# Body
For each template field:
  [4 bits] Field number
  [3 bits] Field type, represented as an integer
  [?] Field value
```

| Field type | Field type integer representation | Value of ? |
|------------|-----------------------------------|------------|
| int        | 0                                 | 4 bytes    |

Multi-byte values are stored in big-endian order.

## How patrickbuf was built

* [Picocli](https://picocli.info/) - to build the `patrickc` command-line tool
    * Alternatives considered: Apache Commons CLI
* [JavaPoet](https://github.com/square/javapoet) - for generating Java source code
    * Alternatives considered: Template engines (e.g. Apache FreeMarker, StringTemplate), Bytecode generators (e.g. ASM,
      Javassist)
* [Maven](https://maven.apache.org/) - for build and package management
    * Alternatives considered: Gradle
* [JUnit](https://junit.org/) - for unit testing
