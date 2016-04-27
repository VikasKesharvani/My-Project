package foi.restaurant.com.mapactivity;

/**
 * Created by vikas on 4/26/2016.
 */
public class ModelClass {
    public String id;

    public ModelClass(String id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public  String name;

}
