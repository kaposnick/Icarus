package input;

import java.util.Set;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NodeConfiguration {
    @SerializedName("id") @Expose private long id;
    @SerializedName("active") @Expose private boolean active;
    @SerializedName("cheater") @Expose private boolean cheater;
    
    public long getId() {
        return id;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public boolean isCheater() {
        return cheater;
    }
}
