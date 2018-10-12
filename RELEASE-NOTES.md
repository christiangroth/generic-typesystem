Release Notes
=============

# 0.6.0 (in progress)
- ...

# 0.5.0
- refactored units to be a standalone top-level type
- added conversion function for unit based values
- refactored persistence service interface and abstract persistence service base implementation (breaking API changes)
- added attribute based enum definitions

# 0.4.0
- fixed item sorting in case of integer/long or float/double clash

# 0.3.0
- removed deprecated methods without context parameter
- updated dependencies to latest version
- added AbstractPersistenceService to allow easier persistence customization
- added parse method to GenericTypeAttribute
- fixed NPE when validating null default values

# 0.2.0
- default value, unit value and custom properties value are of type GenericValue now. This allows to be type safe and better supports JSON marshaling and unmarshalling.
- Introduced GenericTypesystemContext to allow type and item accessibility checks based on owner and visibility attributes.
- added method to create a new item with set default values for a given type

# 0.1.0
- extracted from personal project
- completed datamodel for type, attribute and item
- implemented central typesystem service
- moved persistence to own configurable sub-service
- moved validation to own configurable sub-service
- provided default validation service
- made validation and attribute types extensible
- provided naive in-memory persistence
- provided in-memory sub-services for querying and value proposals
- prepared ownership and visibility in datamodel
- prepared unit based attributes in datamodel
- added much more unit tests