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
  - [Units](#attribute-units)
  - [Items](#items)
- [Services](#services)
  - [Validation](#validation)
  - [Persistence](#persistence)
  - [Ownership & Visibility](#ownership-visibility)
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

Beside attributes a type is defined using the following properties:
- id: a unique numeric identifier
- name, group: human readable type name and optional group for organizational purposes
- owner, visibility: see [Ownership & Visibility](#ownership-visibility)
- pageSize: optional default page size, see [Persistence](#persistence)
- customProperties: optional *java.util.Map* of custom properties to be used for project based customization  

back to [top](#table-of-contents).

### Attributes
A type attribute is defined by the following properties:
- id: a unique numeric identifier (per type)
- name: human readable attribute name
- type, valueType, structure: the attribute type. In case of a list type valueType defines the list values type. In case of a structure type, structure defines the nested structure (thatÄs basically a list of attributes) to be used.
- unique: whether the type value belongs to the unique key
- mandatory: whether the type value is mandatory
- min, max, step: defines the min, max and step values for numerical types. Textual types may use min and max in case of value length validation.
- pattern: an optional pattern to be validated against
- defaultValue, defaultValueCallback: optional default values and default value callback which may be invoked somwhere outside this framework on client side.
- valueProposalDependencies: a list of attribute ids considered for value proposal. See [Persistence](#persistence)
- units: defines if the attribute is unit based if any unit definitions are provided. See [below](#attribute-units)

Further more each attribute has a unique path in context of a type. The path is formed using the attribute name and a dot for nested structures. The path is relevant when working with [items](#items).

back to [top](#table-of-contents).

### Attribute Units
Attribute values may be unit based and the definition of units is fairly simple at the moment:
- name: a unique name (per attribute)
- factor: the units factor to allow conversion between units 

back to [top](#table-of-contents).

### Items
An item is defined using *de.chrgroth.generictypesystem.model.GenericItem* and is uite simple:
- id, typeId: unique item id and id of the defining type
- owner, visibility: see [Ownership & Visibility](#ownership-visibility)
- values: all values are stored in a *java.util.Map* using the attributes path as key. This allows all clients to work with the more manageable attribute path instead of attributes id.

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

#### Value Proposals
TODO ...

back to [top](#table-of-contents).

#### Querying
TODO ...

back to [top](#table-of-contents).

### Ownership & Visibility
TODO ...

back to [top](#table-of-contents).

## Requirements
- [slf4j][1]
- [Java SDK 1.8+][2]

[1]: http://www.slf4j.org/
[2]: http://www.oracle.com/technetwork/java/javase/downloads/index.html

back to [top](#table-of-contents).
