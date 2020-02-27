package in.securelearning.lil.android.syncadapter.events;

import in.securelearning.lil.android.base.constants.SyncStatus;
import in.securelearning.lil.android.base.dataobjects.BaseDataObject;

/**
 * Created by Prabodh Dhabaria on 09-02-2017.
 */

public class ObjectDownloadComplete {
    private final String mId;
    private final String mAlias;
    private final SyncStatus mSyncStatus;
    private final Class mObjectClass;

    public ObjectDownloadComplete(String id, String alias, SyncStatus syncStatus, Class objectClass) {
        mId = id;
        mAlias = alias;
        mSyncStatus = syncStatus;
        mObjectClass = objectClass;
    }

    public ObjectDownloadComplete(String id, SyncStatus syncStatus) {
        mId = id;
        mAlias = "";
        mSyncStatus = syncStatus;
        mObjectClass = BaseDataObject.class;
    }

    public ObjectDownloadComplete(String groupId, Class objectClass) {
        mId = groupId;
        mAlias = "";
        mSyncStatus = SyncStatus.COMPLETE_SYNC;
        mObjectClass = objectClass;
    }

    public String getAlias() {
        return mAlias;
    }

    public String getId() {
        return mId;
    }

    public Class getObjectClass() {
        return mObjectClass;
    }

    public SyncStatus getSyncStatus() {
        return mSyncStatus;
    }

}
