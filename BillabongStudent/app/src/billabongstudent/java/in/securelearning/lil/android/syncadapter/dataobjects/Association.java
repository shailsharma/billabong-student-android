package in.securelearning.lil.android.syncadapter.dataobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Association implements Serializable {

@SerializedName("id")
@Expose
private String id;
@SerializedName("name")
@Expose
private String name;
@SerializedName("isApproved")
@Expose
private Boolean isApproved;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public Boolean getIsApproved() {
return isApproved;
}

public void setIsApproved(Boolean isApproved) {
this.isApproved = isApproved;
}

}