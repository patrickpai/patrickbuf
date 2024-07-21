# patrickbuf

Inspired by [protobuf](https://github.com/protocolbuffers/protobuf), patrickbuf is my own implementation of a
language-agnostic data representation.

## Supported user journeys

1. Define a template in a `.pbdefn`
2. Compile a `.pbdefn` into Java source code
3. In Java,
    1. create an in-memory template instance
    2. write an in-memory template instance to disk, as binary
    3. read into memory a template instance that's stored as binary on disk

## `.pbdefn`

Template files should be created with the `.pbdefn` extension.

Here's what an example `Date.pbdefn` file might look like:

```
Date
0:int:month
1:int:day
2:int:year
```

Compile using the `patrickc` compiler.

## `.pbbinary`

Binary patrickbuf files should end in the `.pbbinary` extension.

Their encoding is as follows:

```
<4 bits, the field number>
<3 bits, the field type>
<N bytes, the field value>
... repeat for all fields ...
```

| Field type | Value of N |
|------------|------------|
| int        | 4          |

## How patrickbuf was built

* [Picocli](https://picocli.info/) - to build the `patrickc` command-line tool
    * Alternatives considered: Apache Commons CLI
* [JavaPoet](https://github.com/square/javapoet) - for generating Java source code
    * Alternatives considered: Template engines (e.g. Apache FreeMarker, StringTemplate), Bytecode generators (e.g. ASM,
      Javassist)
* [Maven](https://maven.apache.org/) - for build and package management
    * Alternatives considered: Gradle
* [JUnit](https://junit.org/) - for unit testing