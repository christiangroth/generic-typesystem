package de.chrgroth.generictypesystem.persistence;

import java.util.List;
import java.util.Set;

import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;

// TODO add exception to interface to indicate persistence problems??
public interface PersistenceService {

    List<String> typeGroups();

    List<GenericType> types();

    GenericType type(long typeId);

    void type(GenericType type);

    Set<GenericItem> items(long typeId);

    GenericItem item(long typeId, long id);

    void item(GenericType type, GenericItem item);

    boolean removeItem(long typeId, long id);

    boolean removeType(long typeId);

}
