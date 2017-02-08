Development: [![Build Status](https://secure.travis-ci.org/christiangroth/generic-typesystem.svg)](http://travis-ci.org/christiangroth/generic-typesystem) [![Coverage Status](https://coveralls.io/repos/github/christiangroth/generic-typesystem/badge.svg?branch=develop)](https://coveralls.io/github/christiangroth/generic-typesystem?branch=develop) [![Dependency Status](https://www.versioneye.com/user/projects/57d99a5d4307470032353ca5/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/57d99a5d4307470032353ca5)

Stable: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.chrgroth.generic-typesystem/generic-typesystem/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.chrgroth.generic-typesystem/generic-typesystem)

# Generic typesystem
This project allowes to programatically define, use and validate a generic typesystem consisting of types, attributes and items. Furthermore there a some services which may be customized to your own project needs. The main service to deal with is [GenericTypesystemService](src/main/java/de/chrgroth/generictypesystem/GenericTypesystemService.java). This acts as the entry point for all operations and connects the various features like validation and persistence. 

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
  - [Ownership and visibility](#ownership-and-visibility)
- [Requirements](#requirements)

## Usage
Just include the maven dependency provided via maven central:

	<dependency>
		<groupId>de.chrgroth.generic-typesystem</groupId>
		<artifactId>generic-typesystem</artifactId>
		<version>${latest version see maven central badge above}</version>
	</dependency>

back to [top](#table-of-contents).

## Datamodel
The datamodel is defined using a handful of POJOs and enums contained in [model](src/main/java/de/chrgroth/generictypesystem/model). A type can define attributes of concrete type and with specific restrictions / configuration. Each item belongs to a type and contains the concrete values for defined attributes. Due to very loose coupling an item may also contain values for undefined attributes, but in case of item validation this situation will be detected. 

back to [top](#table-of-contents).

### Types
A type is defined using [GenericType](src/main/java/de/chrgroth/generictypesystem/model/GenericType.java) and extends [GenericStructure](src/main/java/de/chrgroth/generictypesystem/model/GenericStructure.java). This allows recursive re-usage of structured attributes as kind of nested *types*. 

Beside attributes a type is defined using the following properties:
- id: a unique numeric identifier
- name, group: human readable type name and optional group for organizational purposes
- owner, visibility: see [Ownership & Visibility](#ownership-and-visibility)
- pageSize: optional default page size, see [Persistence](#persistence)
- customProperties: optional *java.util.Map* of custom properties to be used for project based customization  

back to [top](#table-of-contents).

### Attributes
A type attribute is defined by the following properties:
- id: a unique numeric identifier (per type)
- name: human readable attribute name
- type, valueType, structure: the attribute type. In case of a list type valueType defines the list values type. In case of a structure type, structure defines the nested structure (that's basically a list of attributes) to be used.
- unique: whether the type value belongs to the unique key
- mandatory: whether the type value is mandatory
- min, max, step: defines the min, max and step values for numerical types. Textual types may use min and max in case of value length validation.
- pattern: an optional pattern to be validated against
- defaultValue, defaultValueCallback: optional default values and default value callback which may be invoked somwhere outside this framework on client side.
- valueProposalDependencies: a list of attribute ids considered for value proposal. See [value proposals section](#value-proposals) under [persistence](#persistence)
- units: defines if the attribute is unit based if any unit definitions are provided. See [below](#attribute-units)
- customProperties: optional *java.util.Map* of custom properties to be used for project based customization  

Further more each attribute has a unique path in context of a type. The path is formed using the attribute name and a dot for nested structures. The path is relevant when working with [items](#items).

back to [top](#table-of-contents).

### Attribute Units
Attribute values may be unit based and the definition of units is fairly simple at the moment:
- name: a unique name (per attribute)
- factor: the units factor to allow conversion between units 

back to [top](#table-of-contents).

### Items
An item is defined using [GenericItem](src/main/java/de/chrgroth/generictypesystem/model/GenericItem.java) and is quite simple:
- id, typeId: unique item id and id of the defining type
- owner, visibility: see [Ownership & Visibility](#ownership-and-visibility)
- values: all values are stored in a *java.util.Map* using the attributes path as key. This allows all clients to work with the more manageable attribute path instead of attributes id.

The values map needs to contain a suitable java type instance for the specified attribute type. In case of nested structures a nested instance of [GenericItem](src/main/java/de/chrgroth/generictypesystem/model/GenericItem.java) is contained in the map.

Values belonging to unit based attributes are modeled using [UnitValue](src/main/java/de/chrgroth/generictypesystem/model/UnitValue.java) instances, containing the value itself and a reference to the unique units name.

back to [top](#table-of-contents).

## Services
The main service to handle generic typesystem is [GenericTypesystemService](src/main/java/de/chrgroth/generictypesystem/GenericTypesystemService.java). This service acts as main entry point and is composed using further sub-service instances for special purposes, i.e. persistence and validation. Later ones may easily be overwritten / exchanged, see also [Persistence](#persistence) and [Validation](#validation). Further sub-services may be introduces in future releases.

Thus [GenericTypesystemService](src/main/java/de/chrgroth/generictypesystem/GenericTypesystemService.java) itself contains only a minimum amount of business logic and mainly prepares and composes or just delegates calls to defined sub-services.

Every public method takes an instance of [GenericTypesystemContext](src/main/java/de/chrgroth/generictypesystem/context/GenericTypesystemContext.java). Currently the context is used to ensure [Ownership & Visibility](#ownership-and-visibility) concepts, but may be enhanced in future releases. There are two default context implementations available you may choose:
- [DefaultGenericTypesystemContext](src/main/java/de/chrgroth/generictypesystem/context/impl/DefaultGenericTypesystemContext.java)
- [NullGenericTypesystemContext](src/main/java/de/chrgroth/generictypesystem/context/impl/NullGenericTypesystemContext.java)

See [Ownership & Visibility](#ownership-and-visibility) for more details.

back to [top](#table-of-contents).

### Validation
Validation aspects are handled using a sub-service defined by [ValidationService](src/main/java/de/chrgroth/generictypesystem/validation/ValidationService.java). Each validation results in a [ValidationResult](src/main/java/de/chrgroth/generictypesystem/validation/ValidationResult.java) containing all validation error including the path the error occurred at and a message key which can be mapped to suite your own needs.

By default the [DefaultValidationService](src/main/java/de/chrgroth/generictypesystem/validation/impl/DefaultValidationService.java) is used. This service can be extended providing an implementation for [DefaultValidationServiceHooks](src/main/java/de/chrgroth/generictypesystem/validation/impl/DefaultValidationServiceHooks.java).

To completely disable validation [NoValidationService](src/main/java/de/chrgroth/generictypesystem/validation/impl/NoValidationService.java) can be used.

back to [top](#table-of-contents).

### Persistence
Persistence is also separated to an own sub-service defined by [PersistenceService](src/main/java/de/chrgroth/generictypesystem/persistence/PersistenceService.java). By default the [InMemoryPersistenceService](src/main/java/de/chrgroth/generictypesystem/persistence/impl/InMemoryPersistenceService.java) is used, meaning all data is hold in memory only and not persisted during JVM shutdowns ot even new service instantiations. This is a good starting point for prototyping but needs to be replaced for any of your projects going beyond this phase.

Apart from default CRUD operations for types and items there are some more concepts located in persistence service:

- [generating value proposals](#value-proposals)
- [a simple item query API](#querying)

Both of the above topics are implemented as further sub-services and passed to the constructor of defaults in memory persistence service instance. The default implementations may also be reused by your own implementation of persistence service, if they meet your needs.

back to [top](#table-of-contents).

#### Value Proposals
The concept of value proposals calculates all currently existing values for all value proposal capable attributes for a given type. To take it further an optional template item of given type may be provided to reduce the possible values using only items with matching attribute values regarding all defined value proposal dependencies.

The default implementation works in memory only, so all items have to be loaded for this operation. if this is somehow acceptable for you, just reuse the implementation [InMemoryValueProposalService](src/main/java/de/chrgroth/generictypesystem/persistence/values/impl/InMemoryValueProposalService.java).

back to [top](#table-of-contents).

#### Querying
[InMemoryItemsQueryService](src/main/java/de/chrgroth/generictypesystem/persistence/query/impl/InMemoryItemsQueryService.java) is the default implementation for querying purposes and also works in memory only. So again all items have to be loaded to use it. Querying contains the following features executed in declared order:
- filtering
- sorting
- paging

Unfortunately filtering is not implemented yet but will be provided (also in an in-memory only fashion) in a future release.

back to [top](#table-of-contents).

### Ownership and visibility
Ownership and visibility concepts are used to restrict access to concrete type and item instances. An owner is represented as a Long, assuming to be a unique user id or something similar. Visibility is implemented as enum and may be public or private. Default implementation rules can be found in javadocs of [GenericTypesystemContext](src/main/java/de/chrgroth/generictypesystem/context/GenericTypesystemContext.java).

If you do not want to use any ownership or visibility constraints, you may use an instance of [NullGenericTypesystemContext](src/main/java/de/chrgroth/generictypesystem/context/impl/NullGenericTypesystemContext.java) or provide your own context implementation.

back to [top](#table-of-contents).

## Requirements
- [slf4j][1]
- [Java SDK 1.8+][2]

[1]: http://www.slf4j.org/
[2]: http://www.oracle.com/technetwork/java/javase/downloads/index.html

back to [top](#table-of-contents).
