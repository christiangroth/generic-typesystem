Release Notes
=============

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