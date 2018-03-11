import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestApp {

    public static Logger log = LoggerFactory.getLogger(TestApp.class);

    public static void main(String[] args) {
        log.debug("debug Msg");
        log.info("Info msg");
        log.warn("warn msg");
        log.error("error msg");
    }
}
