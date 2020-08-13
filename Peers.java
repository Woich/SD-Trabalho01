
public class Peers {
    String id;
    Integer fake;
    Integer total;

    public Peers (String id)
    {
        this.id = id;
        this.fake = 0;
        this.total = 0;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void addFake()
    {
        this.fake += 1;
    }

    public void addTotal()
    {
        this.total += 1;
    }

    public String getId()
    {
        return this.id;
    }

    public Integer getFake()
    {
        return this.fake;
    }

    public Integer getTotal()
    {
        return this.total;
    }

}