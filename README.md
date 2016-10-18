Development: [![Build Status](https://secure.travis-ci.org/christiangroth/generic-typesystem.svg)](http://travis-ci.org/christiangroth/generic-typesystem) [![Coverage Status](https://coveralls.io/repos/github/christiangroth/generic-typesystem/badge.svg?branch=develop)](https://coveralls.io/github/christiangroth/generic-typesystem?branch=develop) [![Dependency Status](https://www.versioneye.com/user/projects/57d99a5d4307470032353ca5/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/57d99a5d4307470032353ca5)

Stable: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.chrgroth.generic-typesystem/generic-typesystem/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.chrgroth.generic-typesystem/generic-typesystem)

https://maven-badges.herokuapp.com/maven-central/de.chrgroth.smartcron/smartcron

# Generic typesystem
This project allowes to programatically define, use and validate a generic typesystem consisting of types, attributes and items. Furthermore there a some services which may be customized to your own project needs. The main service to deal with is *de.chrgroth.generictypesystem.GenericTypesystemService*. This acts as the entry point for all operations and connects the various features like validation and persistence. 

Although this project was extracted from a single personal project the datamodel and services are designed to be extended. But to be honest datamodel customization is not really used by myself at the moment.

## Table of Contents
- [Usage](#usage)
- [Datamodel](#datamodel)
  - [Types](#types)
  - [Attributes](#attributes)
   - [Items](#items)
- [Services](#services)
  - [Validation](#validation)
  - [Persistence](#persistence)
- [Ownership & Visibility](#ownership)
- [Requirements](#requirements)

## Usage
Just include the maven dependency provided via maven central:

	<dependency>
		<groupId>de.chrgroth.generic-typesystem</groupId>
		<artifactId>generic-typesystem</artifactId>
		<version>${latest version see maven central badge above}</version>
	</dependency>

## Datamodel
The datamodel is defined using a handful of POJOs and enums contained in *de.chrgroth.generictypesystem.model*. A type can define attributes of concrete type and with specific restrictions / configuration. Each item belongs to a type and contains the concrete values for defined attributes. Due to very loose coupling an item may also contain values for undefined attributes, but in case of item validation this situation will be detected. 

back to [top](#table-of-contents).

### Types
A type is defined using *de.chrgroth.generictypesystem.model.GenericType* and extends *de.chrgroth.generictypesystem.model.GenericStructure*. This allows recursive re-usage of structured attributes as kind of nested *types*. 

back to [top](#table-of-contents).

#### Attributes
TODO ...

back to [top](#table-of-contents).

### Items
TODO ...

back to [top](#table-of-contents).

## Services
TODO ...

back to [top](#table-of-contents).

### Validation
TODO ...

back to [top](#table-of-contents).

#### Type Validation
TODO ...

back to [top](#table-of-contents).

#### Item Validation
TODO ...

back to [top](#table-of-contents).

### Persistence
TODO ...

back to [top](#table-of-contents).

#### Querying
TODO ...

back to [top](#table-of-contents).

## Requirements
- [slf4j][1]
- [Java SDK 1.8+][2]

[1]: http://www.slf4j.org/
[2]: http://www.oracle.com/technetwork/java/javase/downloads/index.html

back to [top](#table-of-contents).
