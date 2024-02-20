import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class ManagersTest {

    Managers managers;

    @BeforeEach
    public void beforeEach() {
        managers = new Managers();
    }

    @Test
    void getDefault() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    void getInMemoryTaskManager() {
        assertNotNull(Managers.getInMemoryTaskManager());
    }

    @Test
    void getDefaultHistory() {
        assertNotNull(Managers.getDefaultHistory());
    }
}