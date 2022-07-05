package objectbrowser;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import groovy.inspect.Inspector;
import groovy.lang.PropertyValue;
import objectbrowser.treetable.AbstractTreeTableModel;
import objectbrowser.treetable.TreeTableModel;

public class InspectorModel extends AbstractTreeTableModel {

    // Names of the columns.
    static protected String[] cNames = { "Name", "Value", "Type", "Modifier", "Declarer", "Orgin" };

    // Types of the columns.
    static protected Class<?>[] cTypes = { TreeTableModel.class, String.class, String.class, String.class, String.class,
            String.class };

    public InspectorModel(Object objectToInspect) {
        super(InspectorModel.getRootNode(objectToInspect));
    }

    public static Object getRootNode(Object objectToInspect) {
        return new PropertyNode(objectToInspect);
    }

    //
    // Some convenience methods.
    //

    protected Object[] getChildren(Object node) {
        PropertyNode propertyNode = ((PropertyNode) node);
        return propertyNode.getChildren();
    }

    //
    // The TreeModel interface
    //

    public int getChildCount(Object node) {
        Object[] children = getChildren(node);
        return (children == null) ? 0 : children.length;
    }

    public Object getChild(Object node, int i) {
        return getChildren(node)[i];
    }

    // The superclass's implementation would work, but this is more efficient.
    public boolean isLeaf(Object node) {
        return false;
    }

    //
    // The TreeTableNode interface.
    //

    public int getColumnCount() {
        return cNames.length;
    }

    public String getColumnName(int column) {
        return cNames[column];
    }

    public Class<?> getColumnClass(int column) {
        return cTypes[column];
    }

    public Object getValueAt(Object node, int column) {
        PropertyNode propertyNode = (PropertyNode) node;
        try {
            switch (column) {
                case 0:
                    return propertyNode.getName();
                case 1:
                    return propertyNode.getValue();
                case 2:
                    return propertyNode.getType();
                case 3:
                    return propertyNode.getModifier();
                case 4:
                    return propertyNode.getDeclarer();
                case 5:
                    return propertyNode.getOrigin();
            }
        } catch (SecurityException se) {
        }

        return null;
    }
}

class ArrayNode {

}

class CollectionNode {

}

class MapNode {

}

class PropertyNode {
    private String path;
    private final Inspector inspector;
    Object[] children;
    private final String[] propertyInfo;


    public PropertyNode(Object objectToInspect) {
        this(objectToInspect, "");
    }

    public PropertyNode(Object objectToInspect, String path) {
        this(objectToInspect,
        path,
        new String[] {
            "",
            Modifier.toString(objectToInspect.getClass().getModifiers()),
            "",
            objectToInspect.getClass().getName(),
            "[Object under inspection]",
                        "Instance of " + objectToInspect.getClass().getName() });
    }

    public PropertyNode(Object objectToInspect, String path, String[] propertyInfo) {
        this.path = (path == null ? "": path);
        this.inspector = new Inspector(objectToInspect);
        this.propertyInfo = propertyInfo;
        if (objectToInspect instanceof String) {
            children = new Object[0];
        }
    }

    public String toString() {
        return getName();
    }

    public String getName() {
        return propertyInfo[Inspector.MEMBER_NAME_IDX];
    }

    public String getValue() {
        return propertyInfo[Inspector.MEMBER_VALUE_IDX];
    }

    public String getType() {
        return propertyInfo[Inspector.MEMBER_TYPE_IDX];
    }

    public String getModifier() {
        return propertyInfo[Inspector.MEMBER_MODIFIER_IDX];
    }

    public String getDeclarer() {
        return propertyInfo[Inspector.MEMBER_DECLARER_IDX];
    }

    public String getOrigin() {
        return propertyInfo[Inspector.MEMBER_ORIGIN_IDX];
    }

    public String getPath() {
        return path;
    }

    /**
     * Loads the children, caching the results in the children ivar.
     */
    protected Object[] getChildren() {
        if (children != null) {
            return children;
        }
        List<PropertyNode> childrenList = new LinkedList<>();

        Object object = inspector.getObject();

        if (object.getClass().isArray()) {
            Object[] objectArray = (Object[]) object;
            for (int i = 0; i < objectArray.length; i++) {
                Object o = objectArray[i];
                if (o == null) {
                    continue;
                }
                childrenList.add(new PropertyNode(o,
                    path + "[" + i +"]",
                        new String[] {
                            "",
                            "",
                            "",
                            (o == null ? "" : o.getClass().getName()),
                            "[" + i + "]",
                            String.valueOf(o),
                        }
                    ));

            }
            children = childrenList.toArray(new Object[childrenList.size()]);
            return children;
        } else if (object instanceof Collection) {
            Collection collection = (Collection) object;
            int i = 0;
            for (Object o : collection) {
                if (o == null) {
                    continue;
                }
                childrenList.add(new PropertyNode(o,
                        path + "[" + i + "]",
                        new String[] {
                            "",
                            "",
                            "",
                            (o == null ? "" : o.getClass().getName()),
                            "[" + i + "]",
                            String.valueOf(o)
                        }));
                i++;
            }
            children = childrenList.toArray(new Object[childrenList.size()]);
            return children;
        } else if (object instanceof Map) {
            Map map = (Map) object;
            for (Object key : map.keySet()) {
                Object value = map.get(key);
                if (value == null) {
                    continue;
                }
                childrenList.add(new PropertyNode(value,
                path + "[" + String.valueOf(key) +"]",
                new String[] {
                        "",
                        "",
                        "",
                        (value == null ? "" : value.getClass().getName()),
                        "['" + String.valueOf(key) + "']",
                        String.valueOf(value),
                    }
                ));
            }
            children = childrenList.toArray(new Object[childrenList.size()]);
            return children;
        }

        try {
            Object[] propertyInfos = inspector.getPropertyInfo();
            if (propertyInfos != null) {
                List<PropertyValue> propertyValues = DefaultGroovyMethods.getMetaPropertyValues(object);

                for (int i = 0; i < propertyInfos.length; i++) {
                    String[] propertyInfo = ((String[]) propertyInfos[i]);
                    PropertyValue propertyValue = propertyValues.get(i);
                    Object value = null;
                    try {
                        value = propertyValue.getValue();
                    } catch (Exception e) {
                    }
                    if (value != null) {
                        childrenList.add(new PropertyNode(value, "", propertyInfo));
                    }
                }
                children = childrenList.toArray(new Object[childrenList.size()]);
                return children;
            }
        } catch (SecurityException se) {
        }
        return children;
    }
}
