package data;

public class CommandBody {
    private String dev_name;
    private byte[] dev_props;

    public CommandBody(String dev_name, byte[] dev_props) {
        this.dev_name = dev_name;
        this.dev_props = dev_props;
    }
}
