package in.securelearning.lil.android.syncadapter.rest;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Created by Prabodh Dhabaria on 19-08-2016.
 */
public class ObjectIdExclusionStrategy implements ExclusionStrategy {
    private Class<?> mClass;
    private String mFieldName;

    public ObjectIdExclusionStrategy(String fullyQualifiedFieldName) throws ClassNotFoundException {
        this.mClass = Class.forName(fullyQualifiedFieldName.substring(0, fullyQualifiedFieldName.lastIndexOf(".")));
        this.mFieldName = fullyQualifiedFieldName.substring(fullyQualifiedFieldName.lastIndexOf(".")+1);
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return (f.getDeclaringClass() == mClass && f.getName().equals(mFieldName));
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
