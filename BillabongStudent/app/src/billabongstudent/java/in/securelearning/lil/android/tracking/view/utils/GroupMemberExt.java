package in.securelearning.lil.android.tracking.view.utils;

import in.securelearning.lil.android.base.dataobjects.GroupMember;

/**
 * Created by Secure on 22-05-2017.
 */

public class GroupMemberExt extends GroupMember {
    private boolean isChecked = false;
    private boolean isDone = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public GroupMemberExt(String id, String name) {
        super(id, name);
    }

    public GroupMemberExt(GroupMember groupMember) {
        super(groupMember.getObjectId(),groupMember.getName());
        this.setPic(groupMember.getPic());
    }

    public GroupMemberExt() {
    }
}
